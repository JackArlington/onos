package org.onosproject.sdnwise.protocol;

import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import org.onosproject.net.sensor.SensorNodeAddress;

/**
 * Created by aca on 6/17/15.
 */
@SuppressWarnings("MOVE TO APPLICATION")
@Deprecated
public class SDNWiseNodeCoordinatesMessage extends SDNWiseMessage {
    private SensorNodeAddress node;
    private Integer xCoord;
    private Integer yCoord;
    private Integer zCoord;
    private int nofCoordinates;

    public SDNWiseNodeCoordinatesMessage(byte[] payload) {
        nofCoordinates = 0;
        if (payload.length >= 2) {
            xCoord = ((payload[0] & 0xFF) * 256) + (payload[1] & 0xFF);
            nofCoordinates++;
        }

        if (payload.length >= 4) {
            yCoord = ((payload[2] & 0xFF) * 256) + (payload[3] & 0xFF);
            nofCoordinates++;
        }

        if (payload.length == 6) {
            zCoord = ((payload[4] & 0xFF) * 256) + (payload[5] & 0xFF);
            nofCoordinates++;
        }
    }

    @SuppressWarnings("Message type")
    public SDNWiseNodeCoordinatesMessage(SensorNodeAddress sink, SensorNodeAddress nodeAddress,
                                         Integer xCoord, Integer yCoord, Integer zCoord) {
//        super.setMessageType(SensorMessageType.MULTICAST_GEO_COORDINATES);
        super.setSource(new SDNWiseNodeId(sink.getNetId(), sink.getAddr()));
        super.setDestination(new SDNWiseNodeId(nodeAddress.getNetId(), nodeAddress.getAddr()));
        this.node = nodeAddress;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        if (xCoord >= 0) {
            nofCoordinates++;
        }
        if (yCoord >= 0) {
            nofCoordinates++;
        }
        if (zCoord >= 0) {
            nofCoordinates++;
        }
    }

    @SuppressWarnings("Message type")
    public SDNWiseNodeCoordinatesMessage(SensorNodeAddress sink, SensorNodeAddress nodeAddress,
                                         Integer xCoord, Integer yCoord) {
//        super.setMessageType(SensorMessageType.MULTICAST_GEO_COORDINATES);
        super.setSource(new SDNWiseNodeId(sink.getNetId(), sink.getAddr()));
        super.setDestination(new SDNWiseNodeId(nodeAddress.getNetId(), nodeAddress.getAddr()));
        this.node = nodeAddress;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = 0;
        if (xCoord >= 0) {
            nofCoordinates++;
        }
        if (yCoord >= 0) {
            nofCoordinates++;
        }
    }

    @Override
    public NetworkPacket getNetworkPacket() {
        NetworkPacket networkPacket = super.getNetworkPacket();

//        int size = 2 * nofCoordinates;
        int size = 4;
        byte[] payload = new byte[size];
        if (xCoord != null) {
            payload[0] = (byte) (xCoord.intValue() >> 8);
            payload[1] = (byte) (xCoord.intValue() & 0xFF);
        } else {
            payload[0] = 0;
            payload[1] = 0;
        }

        if (yCoord != null) {
            payload[2] = (byte) (yCoord.intValue() >> 8);
            payload[3] = (byte) (yCoord.intValue() & 0xFF);
        } else {
            payload[2] = 0;
            payload[3] = 0;
        }

//        if (zCoord != null) {
//            payload[4] = (byte) (zCoord.intValue() >> 8);
//            payload[5] = (byte) (zCoord.intValue() & 0xFF);
//        } else {
//            payload[4] = 0;
//            payload[5] = 0;
//        }

//        networkPacket.setDst(new NodeAddress(node.getAddr()));
        networkPacket.setNet(node.getNetId());
        networkPacket = setPayload(networkPacket, payload);
        networkPacket.setLen((byte) (10 + payload.length));

        return networkPacket;
    }
}
