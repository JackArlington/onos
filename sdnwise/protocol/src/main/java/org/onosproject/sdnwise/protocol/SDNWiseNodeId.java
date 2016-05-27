package org.onosproject.sdnwise.protocol;

import org.onlab.packet.MacAddress;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by aca on 3/15/15.
 */
public class SDNWiseNodeId {
    public static final String SCHEME = "sdnwise";

    private byte[] addr;
    private int netId;

    public SDNWiseNodeId(int netId, byte[] addr) {
        this.netId = netId;
        this.addr = new byte[addr.length];
        System.arraycopy(addr, 0, this.addr, 0, addr.length);
    }

    public SDNWiseNodeId(int netId, Byte[] addr) {
        this.netId = netId;
        this.addr = new byte[addr.length];
        for (int i = 0; i < addr.length; i++) {
            this.addr[i] = addr[i].byteValue();
        }
    }

    public static SDNWiseNodeId fromMacAddress(MacAddress macAddress) {
        byte[] mac = macAddress.toBytes();
        byte[] networkId = new byte[4];
        byte[] addr = new byte[2];
        System.arraycopy(mac, 0, networkId, 0, 4);
        System.arraycopy(mac, 4, addr, 0, 2);
        int netId = new BigInteger(networkId).intValue();

        return new SDNWiseNodeId(netId, addr);
    }

    public static SDNWiseNodeId fromUri(URI uri) {
        String schemeSpecificPart = uri.getSchemeSpecificPart();
        MacAddress macAddress = MacAddress.valueOf(schemeSpecificPart);

        return fromMacAddress(macAddress);
    }

    public byte[] address() {
        return this.addr;
    }

    public int netId() {
        return this.netId;
    }

    public MacAddress generateMacAddress() {
        byte[] suffix = {addr[0], addr[1]};

        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byte[] prefix = byteBuffer.putInt(netId).array();

        byte[] mac = new byte[6];
        System.arraycopy(prefix, 0, mac, 0, 4);
        System.arraycopy(suffix, 0, mac, 4, 2);
        MacAddress macAddress = new MacAddress(mac);

        return macAddress;
    }

    public URI uri() {
        String schemeSpecificPart = generateMacAddress().toString();

        try {
            return new URI(SCHEME, schemeSpecificPart, null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }

    public long value() {
        MacAddress macAddress = generateMacAddress();
        byte[] mac = macAddress.toBytes();
        return new BigInteger(mac).longValue();
    }

    @Override
    public String toString() {
        return uri().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SDNWiseNodeId)) {
            return false;
        }

        SDNWiseNodeId that = (SDNWiseNodeId) o;

        if (netId != that.netId) {
            return false;
        }
        if (!Arrays.equals(addr, that.addr)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(addr);
        result = 31 * result + netId;
        return result;
    }
}
