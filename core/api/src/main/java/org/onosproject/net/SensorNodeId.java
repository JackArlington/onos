package org.onosproject.net;

import org.onlab.packet.MacAddress;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by aca on 3/14/15.
 */
public final class SensorNodeId extends ElementId {
    public static final int DEFAULT_NETWORK_ID = 1;

    private final MacAddress macAddress;
    private final int networkId;

    private SensorNodeId(MacAddress macAddress, int networkId) {
        this.macAddress = macAddress;
        this.networkId = networkId;
    }

    private SensorNodeId(MacAddress macAddress) {
        this.macAddress = macAddress;
        this.networkId = DEFAULT_NETWORK_ID;
    }

    public MacAddress mac() {
        return this.macAddress;
    }

    public int netId() {
        return this.networkId;
    }

    public static SensorNodeId sensorNodeId(MacAddress macAddress, int networkId) {
        return new SensorNodeId(macAddress, networkId);
    }

    public static SensorNodeId sensorNodeId(MacAddress macAddress) {
        return new SensorNodeId(macAddress);
    }

    public static SensorNodeId fromMacUri(URI uri) {
        if (uri.getScheme().equals("sdnwise")) {
            String mac = uri.getSchemeSpecificPart();
            MacAddress address = MacAddress.valueOf(mac);
            byte[] networkId = new byte[4];
            System.arraycopy(address.toBytes(), 0, networkId, 0, 4);
            int netId = new BigInteger(networkId).intValue();

            return new SensorNodeId(address, netId);
        }
        return null;
    }

    public URI uri(String scheme) {
        String schemeSpecificPart = macAddress.toString();

        try {
            return new URI(scheme, schemeSpecificPart, null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String toString() {
        return this.macAddress.toString() + "/" + this.networkId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SensorNodeId)) {
            return false;
        }

        SensorNodeId that = (SensorNodeId) o;

        if (networkId != that.networkId) {
            return false;
        }
        if (!macAddress.equals(that.macAddress)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = macAddress.hashCode();
        result = 31 * result + networkId;
        return result;
    }
}
