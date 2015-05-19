package org.deku.leo2.central.data;

import com.google.common.base.Stopwatch;
import org.junit.Test;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Created by masc on 16.05.15.
 */
public class DatabaseSyncTest extends DataTest {
    private Logger mLog = Logger.getLogger(DatabaseSyncTest.class.getName());

    @Inject
    DatabaseSync mDatabaseSync;

    @Test
    public void test() {
        Stopwatch sw = Stopwatch.createStarted();
        mDatabaseSync.sync();
        mLog.info(String.format("Took %s", sw.toString()));
    }
}
