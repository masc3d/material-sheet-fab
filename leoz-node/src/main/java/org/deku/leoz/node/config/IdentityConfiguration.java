package org.deku.leoz.node.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leoz.messaging.activemq.ActiveMQContext;
import org.deku.leoz.node.App;
import org.deku.leoz.node.LocalStorage;
import org.deku.leoz.node.SystemInformation;
import org.deku.leoz.node.auth.Authorizer;
import org.deku.leoz.node.auth.Identity;
import sx.LazyInstance;

import java.io.File;

/**
 * Identity related configuration
 * TODO: migrate to spring configuration
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
    private Authorizer mAuthorizer;

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
        SystemInformation systemInformation = SystemInformation.create();
        mLog.info(systemInformation);

        // Verify and read existing identity file
        File identityFile = LocalStorage.getInstance().getIdentityConfigurationFile();
        if (identityFile.exists()) {
            try {
                identity = Identity.createFromFile(systemInformation, identityFile);
            } catch (Exception e) {
                mLog.error(e.getMessage(), e);
            }
        }
        // Create identity if it doesn't exist or could not be read/parsed
        if (identity == null) {
            identity = Identity.create(systemInformation);

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

        mSystemInformation = systemInformation;
        mIdentity = identity;
    }
}
