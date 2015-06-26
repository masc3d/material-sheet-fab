package org.deku.leo2.node;

import sx.LazyInstance;

import java.io.File;

/**
 * Created by masc on 26.06.15.
 */
public class LocalStorage {
    private static LazyInstance<LocalStorage> mInstance = new LazyInstance(LocalStorage::new);

    // Directories
    private File mHomeDirectory;
    private File mDataDirectory;
    private File mConfigurationDirectory;

    // Files
    private File mApplicationConfigurationFile;
    private File mIdentityConfigurationFile;

    /**
     * c'tor
     */
    private LocalStorage() {
        mHomeDirectory = new File(System.getProperty("user.home"), ".leo2");
        mDataDirectory = new File(this.getHomeDirectory(), "data");

        mApplicationConfigurationFile = new File(this.getHomeDirectory(), "leo2.properties");
        mIdentityConfigurationFile = new File(this.getDataDirectory(), "identity.properties");
    }

    /**
     * Initializes local storage, takes care directories exist etc.
     */
    public void initialize() {
        mHomeDirectory.mkdirs();
        mDataDirectory.mkdirs();
    }

    /** Singleton accessor */
    public static LocalStorage instance() {
        return mInstance.get();
    }

    /**
     * Local home directory
     * @return
     */
    public File getHomeDirectory() {
        return mHomeDirectory;
    }

    /**
     * Local data directory
     * @return
     */
    public File getDataDirectory() {
        return mDataDirectory;
    }

    /**
     * Local application configuration file
     * @return
     */
    public File getApplicationConfigurationFile() {
        return mApplicationConfigurationFile;
    }

    /**
     * Local identity configuration file
     * @return
     */
    public File getIdentityConfigurationFile() {
        return mIdentityConfigurationFile;
    }
}
