package org.deku.leo2.node.auth;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generic system information
 * Created by masc on 01.07.15.
 */
@JsonPropertyOrder( { "hardwareAddress", "hostname", "networkAddresses" } )
public class SystemInformation implements Serializable {
    private static final long serialVersionUID = 1558995436722991648L;

    private static Log mLog = LogFactory.getLog(SystemInformation.class);

    private String mHostname = null;
    private String mHardwareAddress;
    private List<String> mNetworkAddresses;

    /**
     * Find appropriate ipv4 address
     * @param networkInterface
     * @return
     */
    private static Inet4Address findIpv4Address(NetworkInterface networkInterface) {
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
    private static Inet6Address findIpv6Address(NetworkInterface networkInterface) {
        for (InetAddress li : Collections.list(networkInterface.getInetAddresses())) {
            if (li instanceof Inet6Address && !li.isLinkLocalAddress())
                return (Inet6Address) li;
        }
        return null;
    }

    public static SystemInformation create() {
        String hostname = null;
        String hardwareAddress = null;
        Inet4Address ipv4 = null;
        Inet6Address ipv6 = null;

        List<InetAddress> addresses = new ArrayList<InetAddress>();

        NetworkInterface networkInterface = null;
        InetAddress localhost = null;
        try {
            localhost = InetAddress.getLocalHost();
            networkInterface = NetworkInterface.getByInetAddress(localhost);
        } catch (Exception e) {
            mLog.warn(e.getMessage(), e);
        }

        if (networkInterface == null) {
            mLog.warn("No network interface referring to host name");

            try {
                for (NetworkInterface nii : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                    if (nii.isUp() && !nii.isLoopback()) {
                        networkInterface = nii;
                        localhost = findIpv4Address(nii);
                        break;
                    }
                }
            } catch (Exception e) {
                mLog.warn(e.getMessage(), e);
            }
        }

        if (networkInterface != null) {
            try {
                // Hardware address
                List<Byte> hwAddressParts = Lists.newArrayList(ArrayUtils.toObject(
                        networkInterface.getHardwareAddress()));

                // Format hardware address
                hardwareAddress = String.join(":", (Iterable) hwAddressParts.stream().map(c -> String.format("%02x", c))::iterator);

                // Hostname. getHostName()/getCanonicalHostName() as it does connection check/DNS resolve
                // and may be slow in some scenarios (eg. windows). toString() is good enough and
                // returns hostname without connection check/lookup
                hostname = localhost.toString();

                // Find network interface addresses
                ipv4 = findIpv4Address(networkInterface);
                if (ipv4 != null)
                    addresses.add(ipv4);

                ipv6 = findIpv6Address(networkInterface);
                if (ipv6 != null)
                    addresses.add(ipv6);
            } catch (Exception e) {
                mLog.warn(e.getMessage(), e);
            }
        }

        SystemInformation si = new SystemInformation();
        si.mHardwareAddress = hardwareAddress;
        si.mHostname = hostname;
        si.mNetworkAddresses = addresses.stream()
                .map(a -> CharMatcher.is('/').trimLeadingFrom(a.toString()))
                .collect(Collectors.toList());

        return si;
    }

    public String getHostname() {
        return mHostname;
    }

    public String getHardwareAddress() {
        return mHardwareAddress;
    }

    public List<String> getNetworkAddresses() {
        return mNetworkAddresses;
    }

    @Override
    public String toString() {
        return String.format("System information: hardware address [%s] hostname [%s] network addresses [%s]",
                mHardwareAddress,
                mHostname,
                String.join(", ", mNetworkAddresses));
    }
}
