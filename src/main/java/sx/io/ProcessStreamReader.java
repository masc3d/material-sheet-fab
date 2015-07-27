package sx.io;

import com.google.common.base.StandardSystemProperty;
import sx.Disposable;

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
    private Process mProcess;
    private StreamCollector mOutputCollector;
    private StreamCollector mErrorCollector;

    private class StreamCollector extends Thread {
        InputStream mStream;
        BufferedReader mReader;
        StringBuffer mBuffer = new StringBuffer();

        public StreamCollector(InputStream is) {
            mStream = is;
            mReader = new BufferedReader(new InputStreamReader(mStream));
        }

        @Override
        public void run() {
            try {
                String line;
                String crlf = StandardSystemProperty.LINE_SEPARATOR.value();
                while( (line = mReader.readLine()) != null ) {
                    mBuffer.append(line + crlf);
                }
            } catch (Exception ex) {
            }
        }

        public String getContent() {
            return mBuffer.toString();
        }
    }

    /**
     * c'tor
     * @param process
     */
    public ProcessStreamReader(Process process) {
        mProcess = process;
        mOutputCollector = new StreamCollector(process.getInputStream());
        mErrorCollector = new StreamCollector(process.getErrorStream());
        mOutputCollector.start();
        mErrorCollector.start();
    }

    /**
     * Get output
     * @return
     */
    public String getOutput() {
        return mOutputCollector.getContent();
    }

    /**
     * Get error output
     * @return
     */
    public String getError() {
        return mErrorCollector.getContent();
    }

    @Override
    public void dispose() {
        if (mOutputCollector != null) {
            mOutputCollector.interrupt();
            try {
                mOutputCollector.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mOutputCollector = null;
        }
        if (mErrorCollector != null) {
            mErrorCollector.interrupt();
            try {
                mErrorCollector.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mErrorCollector = null;
        }
    }
}