/**
 *
 */
package sx.concurrent;

import java.util.concurrent.*;

/**
 * Extensible task for asynchronous operations
 *
 * @author masc
 */
public abstract class Task<V> implements RunnableFuture<V> {

    FutureTask<V> _futureTask;
    Callable<V> _callable;
    TaskCallback<V> _callback;
    ManualResetEvent _completion = new ManualResetEvent(true);

    public Task(TaskCallback<V> callback) {
        _callback = callback;
        _futureTask = new FutureTask<V>(new Callable<V>() {
            @Override
            public V call() throws Exception {
                _completion.reset();

                try {
                    Exception error = null;
                    V result = null;

                    if (_callback != null)
                        _callback.onStart();

                    try {
                        /** Perform actual task */
                        result = perform();
                    } catch (Exception e) {
                        error = e;
                    }

                    onCompletion(result, _futureTask.isCancelled(), error);
                    if (_callback != null)
                        _callback.onCompletion(result, _futureTask.isCancelled(), error);

                    if (error != null)
                        throw error;

                    return result;
                } finally {
                    _completion.set();
                }
            }
        });
    }

    public Task() {
        this(null);
    }

    protected abstract V perform() throws Exception;

    protected abstract void onCompletion(V result, boolean cancelled, Exception error);

    public final boolean cancel(boolean mayInterruptIfRunning) {
        return _futureTask.cancel(mayInterruptIfRunning);
    }

    public final boolean isCancelled() {
        return _futureTask.isCancelled();
    }

    public final boolean isDone() {
        return _futureTask.isDone();
    }

    public final V get() throws InterruptedException, ExecutionException {
        return _futureTask.get();
    }

    public final V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return _futureTask.get(timeout, unit);
    }

    @Override
    public final void run() {
        _futureTask.run();
    }

    public void waitForCompletion() throws InterruptedException {
        _completion.waitOne();
    }
}
