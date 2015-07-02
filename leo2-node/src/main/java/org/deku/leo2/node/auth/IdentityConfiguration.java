package org.deku.leo2.node.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.activemq.ActiveMQContext;
import org.deku.leo2.node.App;
import org.deku.leo2.node.LocalStorage;
import sx.LazyInstance;

import java.io.File;

/**
 * Identity related configuration
 * Created by masc on 30.06.15.
 */
public class IdentityConfiguration {
    //region Singleton
    private static LazyInstance<IdentityConfiguration> mInstance = new LazyInstance<>(IdentityConfiguration::new);
    public static IdentityConfiguration instance() {
        return mInstance.get();
    }
    //endregion

    private Log mLog = LogFactory.getLog(this.getClass());

    /** Authorizer */
    Authorizer mAuthorizer;

    /** c'tor */
    private IdentityConfiguration() { }

    /**
     * Application wide Node identity
     * @retur
     */
    public Identity getIdentity() {
        return mIdentity;
    }
    private Identity mIdentity;

    /**
     * Application wide system information
     * @return
     */
    public SystemInformation getSystemInformation() {
        return mSystemInformation;
    }
    private SystemInformation mSystemInformation;

    /**
     * Initialize identity
     */
    public void initialize() {
        Identity identity = null;

        // Collect system information
        mSystemInformation = SystemInformation.create();
        mLog.info(mSystemInformation);

        // Verify and read existing identity file
        File identityFile = LocalStorage.instance().getIdentityConfigurationFile();
        if (identityFile.exists()) {
            try {
                identity = Identity.createFromFile(mSystemInformation, identityFile);
            } catch (Exception e) {
                mLog.error(e.getMessage(), e);
            }
        }
        // Create identity if it doesn't exist or could not be read/parsed
        if (identity == null) {
            identity = Identity.create(mSystemInformation);

            // Store updates/created identity
            try {
                identity.store(identityFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        mLog.info(identity);

        // Start authorizer (on client nodes only)
        if (App.instance().getProfile() == App.PROFILE_CLIENT_NODE) {
            mAuthorizer = new Authorizer(ActiveMQContext.instance());
            mAuthorizer.start(identity);
        }

        mIdentity = identity;
    }
}
