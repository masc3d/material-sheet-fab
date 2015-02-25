package sx.concurrent;

import java.util.concurrent.TimeUnit;

public interface Event {
    public void set();
    public void reset();
    public void waitOne() throws InterruptedException;
    public boolean waitOne(int timeout, TimeUnit unit) throws InterruptedException;
    public boolean isSignalled();
}