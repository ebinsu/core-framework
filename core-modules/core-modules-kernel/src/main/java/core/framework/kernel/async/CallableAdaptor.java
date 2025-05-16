package core.framework.kernel.async;

import java.util.concurrent.Callable;

/**
 * @author ebin
 */
record CallableAdaptor(Runnable runnable) implements Callable<Void> {
    @Override
    public Void call() throws Exception {
        runnable.run();
        return null;
    }
}
