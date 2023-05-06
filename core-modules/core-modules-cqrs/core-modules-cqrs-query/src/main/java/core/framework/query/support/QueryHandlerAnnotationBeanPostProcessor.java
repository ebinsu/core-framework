package core.framework.query.support;

import core.framework.query.QueryBus;
import core.framework.query.annotation.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ebin
 */
public class QueryHandlerAnnotationBeanPostProcessor implements BeanPostProcessor, Ordered, ApplicationContextAware, SmartInitializingSingleton {
    public static final String BEAN_NAME = "queryHandlerAnnotationBeanPostProcessor";

    private final Logger logger = LoggerFactory.getLogger(QueryHandlerAnnotationBeanPostProcessor.class);
    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));
    private final List<InvocableQueryHandlerMethod> invocableQueryHandlerMethods = new ArrayList<>();
    private QueryBus queryBus;
    private BeanFactory beanFactory;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!this.nonAnnotatedClasses.contains(bean.getClass())) {
            Class<?> targetClass = AopUtils.getTargetClass(bean);

            Map<Method, Set<QueryHandler>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                    (MethodIntrospector.MetadataLookup<Set<QueryHandler>>) method -> {
                        Set<QueryHandler> listenerMethods = findQueryHandlerAnnotations(method);
                        return listenerMethods.isEmpty() ? null : listenerMethods;
                    });

            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(bean.getClass());
                this.logger.trace("No @QueryHandler annotations found on bean type: " + bean.getClass());
            } else {
                // Non-empty set of methods
                for (Map.Entry<Method, Set<QueryHandler>> entry : annotatedMethods.entrySet()) {
                    Method method = entry.getKey();
                    processQueryHandler(method, bean);
                }
                this.logger.debug(" @QueryHandler methods processed on bean '" + beanName + "': " + annotatedMethods);
            }
        }
        return bean;
    }

    @Override
    public void afterSingletonsInstantiated() {
        if (this.queryBus == null) {
            this.queryBus = this.beanFactory.getBean(QueryBus.class);
        }
        invocableQueryHandlerMethods.forEach(methods -> {
            queryBus.subscribe(methods);
        });
    }

    protected void processQueryHandler(Method method, Object bean) {
        Method methodToUse = checkProxy(method, bean);
        if (methodToUse.getParameterCount() > 1) {
            throw new IllegalStateException(String.format(
                    "@QueryHandler method '%s' found on bean target class '%s', "
                            + "but parameter count not equal 1.'", method.getName(),
                    method.getDeclaringClass().getSimpleName()));
        }
        InvocableQueryHandlerMethod invocableCommandHandlerMethod = new InvocableQueryHandlerMethod(bean, methodToUse);
        invocableQueryHandlerMethods.add(invocableCommandHandlerMethod);
    }

    private Method checkProxy(Method methodArg, Object bean) {
        Method method = methodArg;
        if (AopUtils.isJdkDynamicProxy(bean)) {
            try {
                method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
                Class<?>[] proxiedInterfaces = ((Advised) bean).getProxiedInterfaces();
                for (Class<?> iface : proxiedInterfaces) {
                    try {
                        method = iface.getMethod(method.getName(), method.getParameterTypes());
                        break;
                    } catch (@SuppressWarnings("unused") NoSuchMethodException noMethod) {
                        // NOSONAR
                    }
                }
            } catch (SecurityException ex) {
                ReflectionUtils.handleReflectionException(ex);
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException(String.format(
                        "@QueryHandler method '%s' found on bean target class '%s', "
                                + "but not found in any interface(s) for bean JDK proxy. Either "
                                + "pull the method up to an interface or switch to subclass (CGLIB) "
                                + "proxies by setting proxy-target-class/proxyTargetClass "
                                + "attribute to 'true'", method.getName(),
                        method.getDeclaringClass().getSimpleName()), ex);
            }
        }
        return method;
    }

    private Set<QueryHandler> findQueryHandlerAnnotations(Method method) {
        Set<QueryHandler> listeners = new HashSet<>();
        QueryHandler ann = AnnotatedElementUtils.findMergedAnnotation(method, QueryHandler.class);
        if (ann != null) {
            listeners.add(ann);
        }
        QueryHandler anns = AnnotationUtils.findAnnotation(method, QueryHandler.class);
        if (anns != null) {
            listeners.add(anns);
        }
        return listeners;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            setBeanFactory(((ConfigurableApplicationContext) applicationContext).getBeanFactory());
        } else {
            setBeanFactory(applicationContext);
        }
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
