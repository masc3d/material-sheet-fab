package org.deku.leo2.node.auth;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

/**
 * Identity of a leo2 node
 * Created by masc on 26.06.15.
 */
public class Identity {
    private static Log mLog = LogFactory.getLog(Identity.class);

    private static final String PROP_ID = "id";
    private static final String PROP_KEY = "key";
    private static final String PROP_HOSTNAME = "hostname";
    private static final String PROP_HWADDRESS = "hw";
    private static final String PROP_NETADDRESSES = "net";

    /** The numeric/short id of a node */
    private Integer mId;
    /** Authorization key */
    private String mKey;
    /** Host name */
    private String mHostname;
    private String mHardwareAddress;
    private String mNetworkAddresses;

    /**
     * c'tor
     * @param id
     * @param key
     * @param hostname
     * @param networkAddresses
     */
    private Identity(Integer id, String key, String hostname, String hardwareAddress, String networkAddresses) {
        mId = id;
        mKey = key;
        mHostname = hostname;
        mHardwareAddress = hardwareAddress;
        mNetworkAddresses = networkAddresses;
    }
    private Identity() { }

    public Integer getId() {
        return mId;
    }

    public String getKey() {
        return mKey;
    }

    public String getHostname() {
        return mHostname;
    }

    public String getHardwareAddress() {
        return mHardwareAddress;
    }

    public String getNetworkAddresses() {
        return mNetworkAddresses;
    }

    /**
     * Creates an identity with a random key and current system information
     * @return
     */
    public static Identity create() {
        try {
            Identity id = new Identity();

            // Update basic identity attributes
            id.update();

            // Generate key
            SecureRandom sr = new SecureRandom();
            MessageDigest m = MessageDigest.getInstance("SHA-1");

            String hashBase = String.join(";", id.getHostname(), id.getHardwareAddress(), id.getNetworkAddresses());
            m.update(hashBase.getBytes(Charsets.US_ASCII));
            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            m.update(salt);

            // Calculate digest and format to hex
            id.mKey = BaseEncoding.base16().encode(m.digest()).toLowerCase();

            return id;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find appropriate ipv4 address
     * @param networkInterface
     * @return
     */
    private Inet4Address findIpv4Address(NetworkInterface networkInterface) {
        for (InetAddress li : Collections.list(networkInterface.getInetAddresses())) {
            if (li instanceof Inet4Address)
                return (Inet4Address) li;
        }
        return null;
    }

    /**
     * Find apporopriate ipv6 address
     * @param networkInterface
     * @return
     */
    private Inet6Address findIpv6Address(NetworkInterface networkInterface) {
        for (InetAddress li : Collections.list(networkInterface.getInetAddresses())) {
            if (li instanceof  Inet6Address && !li.isLinkLocalAddress())
                return (Inet6Address) li;
        }
        return null;
    }

    /**
     * Update non-sensitive information
     */
    public void update() throws UnknownHostException, SocketException {
        Integer id = null;
        String key = null;
        String hostname = null;
        String hardwareAddress = null;
        Inet4Address ipv4 = null;
        Inet6Address ipv6 = null;

        List<InetAddress> addresses = new ArrayList<InetAddress>();

        InetAddress localhost = InetAddress.getLocalHost();
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localhost);

        if (networkInterface == null) {
            mLog.warn("No network interface referring to host name");

            for (NetworkInterface nii : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (nii.isUp() && !nii.isLoopback()) {
                    networkInterface = nii;
                    localhost = findIpv4Address(nii);
                    break;
                }
            }
        }

        // Hardware address
        List<Byte> hwAddressParts = Lists.newArrayList(ArrayUtils.toObject(
                networkInterface.getHardwareAddress()));

        // Format hardware address
        hardwareAddress = String.join(":", (Iterable) hwAddressParts.stream().map(c -> String.format("%02x", c))::iterator);

        // Hostname
        hostname = localhost.getCanonicalHostName();

        // Find network interface addresses
        ipv4 = this.findIpv4Address(networkInterface);
        if (ipv4 != null)
            addresses.add(ipv4);

        ipv6 = this.findIpv6Address(networkInterface);
        if (ipv6 != null)
            addresses.add(ipv6);

        mHostname = hostname;
        mHardwareAddress = hardwareAddress;
        mNetworkAddresses = String.join(", ", (Iterable) addresses.stream().map(a ->
                CharMatcher.is('/').trimLeadingFrom(a.toString()))
                ::iterator);
    }

    /**
     * Store identity locally
     * @param destination Destination file
     * @throws IOException
     */
    public void store(File destination) throws IOException {
        Properties p = new Properties();

        if (mId != null)
            p.put(PROP_ID, mId);
        if (mKey != null)
            p.put(PROP_KEY, mKey);
        if (mHostname != null)
            p.put(PROP_HOSTNAME, mHostname);
        if (mHardwareAddress != null)
            p.put(PROP_HWADDRESS, mHardwareAddress);
        if (mNetworkAddresses != null)
            p.put(PROP_NETADDRESSES, mNetworkAddresses);

        p.store(new FileOutputStream(destination), "Identity");
    }

    /**
     * Read identity file
     * @param source
     * @return Identity instance
     */
    public static Identity read(File source) throws IOException {
        Properties p = new Properties();
        p.load(new FileInputStream(source));
        String id = p.getProperty(PROP_ID);
        return new Identity(
                (id != null) ? Integer.valueOf(id) : null,
                p.getProperty(PROP_KEY),
                p.getProperty(PROP_HOSTNAME),
                p.getProperty(PROP_HWADDRESS),
                p.getProperty(PROP_NETADDRESSES)
        );
    }

    @Override
    public String toString() {
        return String.format("Identity id [%d] key [%s] hostname [%s] hw [%s] network [%s]",
                mId,
                mKey,
                mHostname,
                mHardwareAddress,
                mNetworkAddresses);
    }
}
