package org.onosproject.sdnwise.protocol;

import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import org.onosproject.net.sensor.SensorNodeAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aca on 6/17/15.
 */
@SuppressWarnings("MOVE TO APPLICATION")
@Deprecated
public class SDNWiseNeighborsCoordinatesMessage extends SDNWiseMessage {
    private List<NodeCoordinates> neighbors;
    private SensorNodeAddress nodeAddress;

    @SuppressWarnings("Message type")
    public SDNWiseNeighborsCoordinatesMessage(SensorNodeAddress sink, SensorNodeAddress nodeAddress) {
        this.neighbors = new ArrayList<>();
        this.nodeAddress = nodeAddress;
//        super.setMessageType(SensorMessageType.REPORT_GEO_COORDINATES);
        super.setSource(new SDNWiseNodeId(sink.getNetId(), sink.getAddr()));
        super.setDestination(new SDNWiseNodeId(nodeAddress.getNetId(), nodeAddress.getAddr()));
        super.setId(nodeAddress.getNetId());
    }

    public void addNeighborCoordinates(SensorNodeAddress nodeAddress, int xCoord, int yCoord, int zCoord) {
        NodeCoordinates nodeCoordinates = new NodeCoordinates(nodeAddress, xCoord, yCoord, zCoord);
        neighbors.add(nodeCoordinates);
    }

    public void addNeighborCoordinates(SensorNodeAddress nodeAddress, int xCoord, int yCoord) {
        addNeighborCoordinates(nodeAddress, xCoord, yCoord, 0);
    }

    @Override
    public NetworkPacket getNetworkPacket() {
        NetworkPacket networkPacket = super.getNetworkPacket();

//        networkPacket.setDst(new NodeAddress(nodeAddress.getAddr()));
        if ((neighbors != null) && (neighbors.size() > 0)) {
            int nofNeighbors = neighbors.size();
            byte[] payload = new byte[6 * nofNeighbors];
            int offset = 0;
            for (NodeCoordinates neighbor : neighbors) {
                System.arraycopy(neighbor.toByteArray(), 0, payload, offset, 6);
                offset = offset + 6;
            }

            networkPacket = setPayload(networkPacket, payload);
        }

        return networkPacket;
    }

    private class NodeCoordinates {
        private SensorNodeAddress nodeAddress;
        private int xCoord;
        private int yCoord;
        private int zCoord;

        public NodeCoordinates(SensorNodeAddress nodeAddress, int xCoord, int yCoord, int zCoord) {
            this.nodeAddress = nodeAddress;
            this.xCoord = xCoord;
            this.yCoord = yCoord;
            this.zCoord = zCoord;
        }

        public SensorNodeAddress getNodeAddress() {
            return nodeAddress;
        }

        public void setNodeAddress(SensorNodeAddress nodeAddress) {
            this.nodeAddress = nodeAddress;
        }

        public int getxCoord() {
            return xCoord;
        }

        public void setxCoord(int xCoord) {
            this.xCoord = xCoord;
        }

        public int getyCoord() {
            return yCoord;
        }

        public void setyCoord(int yCoord) {
            this.yCoord = yCoord;
        }

        public int getzCoord() {
            return zCoord;
        }

        public void setzCoord(int zCoord) {
            this.zCoord = zCoord;
        }

        public byte[] toByteArray() {
            byte[] arr = new byte[6];

            arr[0] = nodeAddress.getAddr()[0];
            arr[1] = nodeAddress.getAddr()[1];
            arr[2] = (byte) (xCoord >> 8);
            arr[3] = (byte) (xCoord & 0xFF);
            arr[4] = (byte) (yCoord >> 8);
            arr[5] = (byte) (yCoord & 0xFF);
//            arr[6] = (byte) (zCoord >> 8);
//            arr[7] = (byte) (zCoord & 0xFF);

            return arr;
        }
    }
}
