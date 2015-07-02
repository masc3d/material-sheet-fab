package org.deku.leo2.node.auth;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sx.event.*;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

/**
 * Holds all identity information for a leo2 node including system information
 * Created by masc on 26.06.15.
 */
public class Identity {
    // Property keys for file storage
    private static final String PROP_ID = "id";
    private static final String PROP_KEY = "key";

    //region Events
    public interface Listener extends sx.event.EventListener {
        void onIdUpdated(Identity identity);
    }

    private EventDispatcher<Listener> mEventDispatcher = EventDispatcher.createThreadSafe();
    public EventDelegate<Listener> getDelegate() { return mEventDispatcher; }
    //endregion

    /**
     * c'tor
     * @param id
     * @param key
     */
    private Identity(Integer id, String key, SystemInformation systemInformation) {
        if (Strings.isNullOrEmpty(key))
            throw new IllegalArgumentException("Key cannot be null");

        mId = id;
        mKey = key;
        mSystemInformation = systemInformation;
    }

    /**
     * @return The numeric/short id of a node
     */
    public synchronized Integer getId() { return mId; }
    public synchronized void setId(Integer id) {
        mId = id;
        mEventDispatcher.emit(listener -> listener.onIdUpdated(this));
    }
    private Integer mId;

    /**
     * INdicates if identity has an id
     * @return
     */
    public boolean hasId() {
        return this.getId() != null;
    }

    /**
     * @return  Authorization key
     */
    public String getKey() {
        return mKey;
    }
    private String mKey;

    /** System information */
    public SystemInformation getSystemInformation() {
        return mSystemInformation;
    }
    private SystemInformation mSystemInformation;

    /**
     * Creates an identity with a random key and current system information
     * @return
     */
    public static Identity create(SystemInformation systemInformation) {
        try {
            // Generate key
            SecureRandom sr = new SecureRandom();
            MessageDigest m = MessageDigest.getInstance("SHA-1");

            String hashBase = String.join(";",
                    systemInformation.getHostname(),
                    systemInformation.getHardwareAddress(),
                    String.join(", ", systemInformation.getNetworkAddresses()));

            m.update(hashBase.getBytes(Charsets.US_ASCII));
            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            m.update(salt);

            // Calculate digest and format to hex
            String key = BaseEncoding.base16().encode(m.digest()).toLowerCase();

            return new Identity(null, key, systemInformation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Read identity file
     * @param source
     * @return Identity instance
     */
    public static Identity createFromFile(SystemInformation systemInfo, File source) throws IOException {
        Properties p = new Properties();
        p.load(new FileInputStream(source));
        String id = p.getProperty(PROP_ID);
        return new Identity(
                (id != null) ? Integer.valueOf(id) : null,
                p.getProperty(PROP_KEY),
                systemInfo
        );
    }

    /**
     * Store identity locally
     * @param destination Destination file
     * @throws IOException
     */
    public synchronized void store(File destination) throws IOException {
        Properties p = new Properties();

        if (mId != null)
            p.put(PROP_ID, mId.toString());
        if (mKey != null)
            p.put(PROP_KEY, mKey);

        OutputStream os = new FileOutputStream(destination);
        p.store(os, "Identity");
        os.close();
    }

    @Override
    public String toString() {
        return String.format("Identity id [%d] key [%s]",
                mId,
                mKey);
    }
}
