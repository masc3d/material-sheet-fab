package sx.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Manual reset mLatch
 * @author masc
 */
public class ManualResetEvent implements Event {
    private volatile CountDownLatch mLatch;
    private Object mSync = new Object();

    public ManualResetEvent(boolean signalled) {
        if (signalled) {
            mLatch = new CountDownLatch(0);
        } else {
            mLatch = new CountDownLatch(1);
        }
    }

    public void set() {
        mLatch.countDown();
    }

    public void reset() {
        synchronized (mSync) {
            if (mLatch.getCount() == 0) {
                mLatch = new CountDownLatch(1);
            }
        }
    }

    public void waitOne() throws InterruptedException {
        mLatch.await();
    }

    public boolean waitOne(int timeout, TimeUnit unit) throws InterruptedException {
        return mLatch.await(timeout, unit);
    }

    public boolean isSignalled() {
        return mLatch.getCount() == 0;
    }
}