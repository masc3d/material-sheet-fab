package org.deku.leoz.central.data;

import com.google.common.base.Stopwatch;
import org.apache.commons.logging.Log;
import org.deku.leoz.central.DataTest;
import org.deku.leoz.central.services.DatabaseSyncService;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Created by masc on 16.05.15.
 */
public class DatabaseSyncTest extends DataTest {
    private Log mLog = LoggerFactory.getLogger(DatabaseSyncTest.class);

    @Inject
    DatabaseSyncService mDatabaseSync;

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
