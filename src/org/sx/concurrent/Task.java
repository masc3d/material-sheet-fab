/**
 *
 */
package org.sx.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Extensible task for asynchronous operations
 *
 * @author masc
 */
public abstract class Task<V> implements RunnableFuture<V> {

    FutureTask<V> _futureTask;
    Callable<V> _callable;
    TaskCallback<V> _callback;

    public Task(TaskCallback<V> callback) {
        _callback = callback;
        _futureTask = new FutureTask<V>(new Callable<V>() {
            @Override
            public V call() throws Exception {
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

                if (_callback != null)
                    _callback.onCompletion(result, error);

                if (error != null)
                    throw error;

                return result;
            }
        });
    }

    public Task() {
        this(null);
    }

    protected abstract V perform() throws Exception;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return _futureTask.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return _futureTask.isCancelled();
    }

    @Override
    public boolean isDone() {
        return _futureTask.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return _futureTask.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return _futureTask.get(timeout, unit);
    }

    @Override
    public void run() {
        _futureTask.run();
    }
}
