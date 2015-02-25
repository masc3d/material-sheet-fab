package sx.concurrent;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Automatic reset mSemaphore
 * @author masc
 */
public class AutoResetEvent implements Event {
    private final Semaphore mSemaphore;

    public AutoResetEvent(boolean signalled) {
        mSemaphore = new Semaphore(signalled ? 1 : 0);
    }

    public void set() {
        synchronized (mSemaphore) {
            if (mSemaphore.availablePermits() == 0)
                mSemaphore.release();
        }
    }

    public void reset() {
        mSemaphore.drainPermits();
    }

    public void waitOne() throws InterruptedException {
        mSemaphore.acquire();
    }

    public boolean waitOne(int timeout, TimeUnit unit) throws InterruptedException {
        return mSemaphore.tryAcquire(timeout, unit);
    }

    public boolean isSignalled() {
        return mSemaphore.availablePermits() > 0;
    }
}