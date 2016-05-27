package org.onosproject.net.sensor;

import com.google.common.base.MoreObjects;
import org.onlab.packet.MacAddress;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by aca on 4/30/15.
 */
public class SensorNodeAddress {
    private byte netId;
    private byte[] addr;

    public SensorNodeAddress(byte netId, byte[] addr) {
        this.netId = netId;
        this.addr = new byte[addr.length];
        System.arraycopy(addr, 0, this.addr, 0, addr.length);
    }

    public byte getNetId() {
        return netId;
    }

    public void setNetId(byte netId) {
        this.netId = netId;
    }

    public byte[] getAddr() {
        return addr;
    }

    public void setAddr(byte[] addr) {
        this.addr = addr;
    }

    public byte[] toByteArray() {
        byte[] arr = new byte[addr.length + 1];
        for (int i = 0; i < addr.length; i++) {
            arr[i] = addr[i];
        }
        arr[addr.length] = netId;

        return arr;
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(SensorNodeAddress.class)
                .add("netId", netId)
                .add("address", Arrays.toString(addr))
                .toString();
    }
}
