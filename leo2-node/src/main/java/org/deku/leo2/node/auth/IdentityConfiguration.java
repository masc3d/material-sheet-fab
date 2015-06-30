package org.deku.leo2.node.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.node.LocalStorage;
import sx.LazyInstance;

import java.io.File;

/**
 * Created by masc on 30.06.15.
 */
public class IdentityConfiguration {
    private Log mLog = LogFactory.getLog(this.getClass());

    //region Singleton
    private static LazyInstance<IdentityConfiguration> mInstance = new LazyInstance<>(IdentityConfiguration::new);

    public static IdentityConfiguration instance() {
        return mInstance.get();
    }
    //endregion

    Identity mIdentity;

    /** c'tor */
    private IdentityConfiguration() { }

    public Identity getIdentity() {
        return mIdentity;
    }

    /**
     * Initialize identity
     */
    public void initialize() {
        if (mIdentity != null)
            throw new IllegalStateException("Identity already initialized");

        Identity identity = null;
        File identityFile = LocalStorage.instance().getIdentityConfigurationFile();
        if (identityFile.exists()) {
            try {
                identity = Identity.read(identityFile);
            } catch (Exception e) {
                mLog.error(e.getMessage(), e);
            }
        }
        // Create identity if it doesn't exist or could not be read/parsed
        if (identity == null) {
            identity = Identity.create();
        } else {
            // Update identity if there was one
            try {
                identity.update();
            } catch (Exception e) {
                mLog.error(e.getMessage(), e);
            }
        }
        // Store updates/created identity
        try {
            identity.store(identityFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mLog.info(identity);
        mIdentity = identity;
    }
}
