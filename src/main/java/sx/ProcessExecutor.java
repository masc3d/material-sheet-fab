package sx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Process executor with threaded stream reading support
 */
public class ProcessExecutor implements Disposable {
    /**
     * Process exception
     */
    public class ProcessException extends java.lang.Exception {
        private int mErrorCode;

        public ProcessException(int errorCode) {
            super(String.format("Process failed with error code [%d]", errorCode));
            mErrorCode = errorCode;
        }

        public int getErrorCode() {
            return mErrorCode;
        }
    }

    private Log mLog = LogFactory.getLog(this.getClass());

    private ProcessBuilder mProcessBuilder;
    private Process mProcess;
    private StreamHandler mStreamHandler;
    private StreamReaderThread mOutputReaderThread;
    private StreamReaderThread mErrorReaderThread;
    private MonitorThread mMonitorThread;

    public interface StreamHandler {
        void onOutput(String output);
        void onError(String output);
    }

    private class MonitorThread extends Thread {
        @Override
        public void run() {
            Thread shutdownHook = new Thread("ProcessExecutor shutdown hook") {
                @Override
                public void run() {
                    if (mProcess.isAlive()) {
                        mLog.warn(String.format("Terminating process [%s]", mProcessBuilder.command().get(0)));
                        mProcess.destroy();
                    }
                }
            };

            Runtime.getRuntime().addShutdownHook(shutdownHook);

            try {
                mProcess.waitFor();
            } catch (InterruptedException e) {
                mLog.error(e.getMessage(), e);
            } finally {
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
                shutdownHook.run();
            }
        }
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
     * @param processBuilder Process builder
     * @param streamHandler Stream handler implementation
     */
    public ProcessExecutor(ProcessBuilder processBuilder, StreamHandler streamHandler) {
        mProcessBuilder = processBuilder;
        mStreamHandler = streamHandler;
    }

    /**
     * c'tor
     * @param processBuilder Process builder
     */
    public ProcessExecutor(ProcessBuilder processBuilder) {
        this(processBuilder, null);
    }

    /**
     * Start process
     * @throws IOException
     */
    public void start() throws IOException {
        if (mProcess != null)
            throw new IllegalStateException("Process already started");

        // Start process
        mProcess = mProcessBuilder.start();
        mMonitorThread = new MonitorThread();
        mMonitorThread.start();

        // Add stream handlers
        if (mStreamHandler != null) {
            mOutputReaderThread = new StreamReaderThread(mProcess.getInputStream(), new Action<String>() {
                @Override
                public void perform(String it) {
                    mStreamHandler.onOutput(it);
                }
            });
            mErrorReaderThread = new StreamReaderThread(mProcess.getErrorStream(), new Action<String>() {
                @Override
                public void perform(String it) {
                    mStreamHandler.onError(it);
                }
            });
            mOutputReaderThread.start();
            mErrorReaderThread.start();
        }
    }

    /**
     * Process
     * @return
     */
    public Process getProcess() {
        return mProcess;
    }

    /**
     * Wait for process and stream reader threads to terminate
     * @return
     * @throws InterruptedException
     */
    public void waitFor() throws InterruptedException, ProcessException {
        if (mProcess == null)
            throw new IllegalStateException("Process not started");

        int returnCode = -1;
        try {
            returnCode = mProcess.waitFor();
        } finally {
            if (mProcess.isAlive()) {
                mProcess.destroy();
            }

            // Wait for stream reader threads to terminate
            if (mOutputReaderThread != null) {
                mOutputReaderThread.join();
                mOutputReaderThread = null;
            }
            if (mErrorReaderThread != null) {
                mErrorReaderThread.join();
                mErrorReaderThread = null;
            }
        }

        if (returnCode != 0)
            throw new ProcessException(returnCode);
    }

    @Override
    public void dispose() {
        if (mProcess != null && mProcess.isAlive()) {
            mProcess.destroy();
        }
        mProcess = null;

        if (mOutputReaderThread != null) {
            mOutputReaderThread.interrupt();
            try {
                mOutputReaderThread.join();
            } catch (InterruptedException e) {
                mLog.error(e.getMessage(), e);
            }
            mOutputReaderThread= null;
        }

        if (mErrorReaderThread != null) {
            mErrorReaderThread.interrupt();
            try {
                mErrorReaderThread.join();
            } catch (InterruptedException e) {
                mLog.error(e.getMessage(), e);
            }
            mErrorReaderThread = null;
        }
    }
}