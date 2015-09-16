package sx.concurrent;

import java.util.concurrent.TimeUnit;

public interface Event {
    void set();
    void reset();
    void waitOne() throws InterruptedException;
    boolean waitOne(int timeout, TimeUnit unit) throws InterruptedException;
    boolean isSignalled();
}