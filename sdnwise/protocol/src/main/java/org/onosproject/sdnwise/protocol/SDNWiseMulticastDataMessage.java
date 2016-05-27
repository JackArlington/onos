package org.onosproject.sdnwise.protocol;

import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import org.onosproject.net.sensor.SensorNodeAddress;

import java.util.Arrays;

/**
 * Created by aca on 5/28/15.
 */
@SuppressWarnings("MOVE TO APPLICATION")
@Deprecated
public class SDNWiseMulticastDataMessage extends SDNWiseMessage {
    private SensorNodeAddress prev;
    private SensorNodeAddress cur;
    private SensorNodeAddress group;
    private SensorNodeAddress init;

    private byte[] curCoordinates = null;
    private byte[] payload;

    @SuppressWarnings("Message type")
    public SDNWiseMulticastDataMessage(byte netId, byte[] payload) {
//        this.setMessageType(SensorMessageType.MULTICAST_DATA);
        this.payload = new byte[payload.length];

        System.arraycopy(payload, 0, this.payload, 0, payload.length);
        byte[] grpAddr = new byte[2];
        byte[] prevAddr = new byte[2];
        byte[] curAddr = new byte[2];
        byte[] initAddr = new byte[2];

        grpAddr[0] = payload[0];
        grpAddr[1] = payload[1];
        group = new SensorNodeAddress(netId, grpAddr);

        initAddr[0] = payload[2];
        initAddr[1] = payload[3];
        init = new SensorNodeAddress(netId, initAddr);

        curAddr[0] = payload[6];
        curAddr[1] = payload[7];
        cur = new SensorNodeAddress(netId, curAddr);

        prevAddr[0] = payload[4];
        prevAddr[1] = payload[5];
        if (((prevAddr[0] == Byte.MAX_VALUE) && (prevAddr[1] == Byte.MAX_VALUE)) ||
                ((prevAddr[0] == curAddr[0]) && (prevAddr[1] == curAddr[1]))) {
            prev = null;
        } else {
            prev = new SensorNodeAddress(netId, prevAddr);
        }


        if (payload.length > 8) {
            this.curCoordinates = new byte[6];
            System.arraycopy(payload, 8, this.curCoordinates, 0, 6);
        }
    }

    public SensorNodeAddress prev() {
        return prev;
    }

    public SensorNodeAddress cur() {
        return cur;
    }

    public SensorNodeAddress group() {
        return group;
    }

    public SensorNodeAddress init() {
        return init;
    }

    public byte[] getCurCoordinates() {
        return this.curCoordinates;
    }

    public void setCurCoordinates(byte[] curCoordinates) {
        this.curCoordinates = new byte[6];
        System.arraycopy(curCoordinates, 0, this.curCoordinates, 0, curCoordinates.length);
        for (int i = 0; i < 6; i++) {
            this.payload[8 + i] = curCoordinates[i];
        }
    }

    public void setCur(SensorNodeAddress curAddress) {
        this.cur = curAddress;
        payload[6] = curAddress.getAddr()[0];
        payload[7] = curAddress.getAddr()[1];
    }

    public void setPrev(SensorNodeAddress prvAddress) {
        this.prev = prvAddress;
        payload[4] = prvAddress.getAddr()[0];
        payload[5] = prvAddress.getAddr()[1];
    }

    @Override
    public NetworkPacket getNetworkPacket() {
        NetworkPacket networkPacket = super.getNetworkPacket();
//        networkPacket.setType((byte) SDNWiseMessageType.MULTICAST_DATA.ordinal());
        networkPacket = setPayload(networkPacket, payload);
        networkPacket.setLen((byte) (10 + payload.length));
        return networkPacket;
    }

    @Override
    public String toString() {
        return Arrays.toString(getNetworkPacket().toByteArray());
    }
}
