package core.framework.namequery;

import core.framework.namequery.impl.NameQueryParamImpl;

import java.util.Map;

/**
 * @author ebin
 */
public interface NameQueryParam<T> {
    String getQueryName();

    Class<T> getResultType();

    Map<String, Object> getQueryParam();

    NameQueryParamImpl<T> addQueryParam(String key, Object value);
}
