package org.deku.leo2.node.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.auth.AUTH;
import org.deku.leo2.messaging.activemq.ActiveMQContext;
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

    Identity mIdentity;
    SystemInformation mSystemInformation;

    Authorizer mAuthorizer;

    /** c'tor */
    private IdentityConfiguration() { }

    /**
     * Node identity
     * @retur
     */
    public Identity getIdentity() {
        return mIdentity;
    }

    /**
     * System information
     * @return
     */
    public SystemInformation getSystemInformation() {
        return mSystemInformation;
    }

    /**
     * Initialize identity
     */
    public void initialize() {
        Identity identity = null;

        mSystemInformation = SystemInformation.create();
        mLog.info(mSystemInformation);

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

        // Start authorizing process if there's no id yet
        if (identity.getId() == null) {
            mAuthorizer = new Authorizer(ActiveMQContext.instance());
            mAuthorizer.start(identity);
        }

        mIdentity = identity;
    }
}
