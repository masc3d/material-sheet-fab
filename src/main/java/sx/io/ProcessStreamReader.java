package sx.io;

import com.google.common.base.StandardSystemProperty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sx.Action;
import sx.Disposable;
import sx.event.EventDelegate;
import sx.event.EventDispatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.ArrayList;

/**
 * Threaded stream reader for processes
 */
public class ProcessStreamReader implements Disposable {
    private Log mLog = LogFactory.getLog(this.getClass());

    private Process mProcess;
    private Handler mHandler;
    private StreamReaderThread mOutputReaderThread;
    private StreamReaderThread mErrorReaderThread;

    public interface Handler {
        void onOutput(String output);
        void onError(String output);
    }

    /**
     * Stream reader thread
     */
    private class StreamReaderThread extends Thread {
        InputStream mStream;
        BufferedReader mReader;
        Action<String> mAction;

        /**
         * c'tor
         * @param is Stream to read from
         * @param action Action to perform on each line
         */
        public StreamReaderThread(InputStream is, Action<String> action) {
            mStream = is;
            mReader = new BufferedReader(new InputStreamReader(mStream));
            mAction = action;
        }

        @Override
        public void run() {
            try {
                String line;
                while( (line = mReader.readLine()) != null ) {
                    mAction.perform(line);
                }
            } catch (Exception ex) {
                if (!(ex instanceof  InterruptedException))
                    mLog.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * c'tor
     * @param process
     */
    public ProcessStreamReader(Process process, Handler handler) {
        mProcess = process;
        mHandler = handler;
        mOutputReaderThread = new StreamReaderThread(process.getInputStream(), (x) -> mHandler.onOutput(x));
        mErrorReaderThread = new StreamReaderThread(process.getErrorStream(), (x) -> mHandler.onError(x));
        mOutputReaderThread.start();
        mErrorReaderThread.start();
    }

    @Override
    public void dispose() {
        if (mOutputReaderThread != null) {
            mOutputReaderThread.interrupt();
            try {
                mOutputReaderThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mOutputReaderThread= null;
        }

        if (mErrorReaderThread != null) {
            mErrorReaderThread.interrupt();
            try {
                mErrorReaderThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mErrorReaderThread = null;
        }
    }
}