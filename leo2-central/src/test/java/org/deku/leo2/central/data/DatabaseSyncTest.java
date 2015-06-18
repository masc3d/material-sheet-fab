package org.deku.leo2.central.data;

import com.google.common.base.Stopwatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.central.data.sync.DatabaseSync;
import org.deku.leo2.central.DataTest;
import org.junit.Test;

import javax.inject.Inject;

/**
 * Created by masc on 16.05.15.
 */
public class DatabaseSyncTest extends DataTest {
    private Log mLog = LogFactory.getLog(DatabaseSyncTest.class);

    @Inject
    DatabaseSync mDatabaseSync;

    @Test
    public void test() {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            mDatabaseSync.sync(true);
        } finally {
            mLog.info(String.format("Took %s", sw.toString()));
        }
    }
}
