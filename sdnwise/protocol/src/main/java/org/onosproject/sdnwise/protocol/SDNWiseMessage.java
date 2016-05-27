package org.onosproject.sdnwise.protocol;

import com.github.sdnwiselab.sdnwise.packet.BeaconPacket;
import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import com.github.sdnwiselab.sdnwise.packet.ReportPacket;
import com.github.sdnwiselab.sdnwise.util.NodeAddress;
import com.google.common.base.MoreObjects;
import org.onlab.packet.IpAddress;
import org.onosproject.net.PortNumber;
import org.slf4j.Logger;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.sdnwiselab.sdnwise.packet.NetworkPacket.*;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by aca on 2/21/15.
 */
public class SDNWiseMessage {
    private static final Logger log = getLogger(SDNWiseMessage.class);

    private int length;
    private int id;
    private SDNWiseMessageType messageType;
    private SDNWiseNodeId source;
    private SDNWiseNodeId destination;
    private int ttl;
    private SDNWiseVersion version;
    private byte[] rawDataPayload;
    private SDNWiseNodeId nxHop;

    public static NetworkPacket setPayload(NetworkPacket np, byte[] payload) {
        byte[] packet = np.toByteArray();
        System.arraycopy(payload, 0, packet, DFLT_HDR_LEN, payload.length);
        np.setArray(packet);
        return np;
    }

    public static byte[] getPayload(NetworkPacket np) {
        byte[] packet = np.toByteArray();
        return Arrays.copyOfRange(packet, DFLT_HDR_LEN, np.getLen());
    }

    public static SDNWiseMessage fromPayload(ByteBuffer byteBuffer) {
        byte[] data = byteBuffer.array();
        SDNWiseMessage sdnWiseMessage = new SDNWiseMessage();
        sdnWiseMessage.setRawDataPayload(data);

        sdnWiseMessage.setLength(10 + data.length);

        return sdnWiseMessage;
    }

    public static SDNWiseMessage fromByteBuffer(ByteBuffer byteBuffer) {
        byte[] data = byteBuffer.array();
        NetworkPacket networkPacket = new NetworkPacket(data);

        return getMessageFromPacket(networkPacket);
    }

    public static SDNWiseMessage getMessageFromPacket(NetworkPacket networkPacket,
                                                      IpAddress ipAddress, PortNumber port) {

        SDNWiseMessage sdnWiseMessage = null;
        int sensorMessageType = networkPacket.getTyp();
        if (sensorMessageType < 0) {
            sensorMessageType = ((byte) networkPacket.getTyp()) & 0xFF;
        }
//        int networkPacketType = networkPacket.getType();
        if (sensorMessageType > 127) {
            sensorMessageType = sensorMessageType - 128;
        }

        SDNWiseMessageType messageType = SDNWiseBuiltinMessageType.getType(networkPacket.getTyp());

        if (sensorMessageType == REG_PROXY) {
            log.info("Received DP CONNECTION PACKET {}", Arrays.toString(networkPacket.toByteArray()));
            sdnWiseMessage = new SDNWiseDPConnectionMessage(networkPacket, ipAddress, port);
        } else if (sensorMessageType == REPORT) {
            ReportPacket reportPacket = new ReportPacket(networkPacket);
            sdnWiseMessage = new SDNWiseReportMessage(reportPacket.getDistance(), reportPacket.getBattery(),
                    reportPacket.getNeigborsSize(), ipAddress, port);
            HashMap<NodeAddress, Byte> neighborsMap = reportPacket.getNeighbors();
            if ((neighborsMap != null) && (neighborsMap.size() > 0)) {
                for (Map.Entry<NodeAddress, Byte> entry : neighborsMap.entrySet()) {
                    NodeAddress nodeAddress = entry.getKey();
                    byte rssi = entry.getValue();
                    ((SDNWiseReportMessage) sdnWiseMessage).addNeighborRSSI(
                            new SDNWiseNodeId(networkPacket.getNet(), nodeAddress.getArray()),
                            (rssi & 0xFF));
                }
            }
        } else {
            return getMessageFromPacket(networkPacket);
        }

        if (sdnWiseMessage != null) {
            sdnWiseMessage.setMessageType(messageType);
            sdnWiseMessage.setId(networkPacket.getNet());
            sdnWiseMessage.setLength(networkPacket.getLen());
            sdnWiseMessage.setSource(new SDNWiseNodeId(
                    networkPacket.getNet(), networkPacket.getSrc().getArray()));
            sdnWiseMessage.setDestination(new SDNWiseNodeId(
                    networkPacket.getNet(), networkPacket.getDst().getArray()));
            sdnWiseMessage.setTtl(networkPacket.getTtl());
            sdnWiseMessage.setVersion(SDNWiseVersion.SDN_WISE);
            sdnWiseMessage.setRawDataPayload(getPayload(networkPacket));
            sdnWiseMessage.setNxHop(new SDNWiseNodeId(networkPacket.getNet(), networkPacket.getNxh().getArray()));
        }

        return sdnWiseMessage;
    }

    public static SDNWiseMessage getMessageFromPacket(NetworkPacket networkPacket) {
        SDNWiseMessage sdnWiseMessage = null;
        int sensorMessageType = networkPacket.getTyp();
//        int networkPacketType = networkPacket.getType();
        if (sensorMessageType < 0) {
            sensorMessageType = ((byte) networkPacket.getTyp()) & 0xFF;
        }
        if (sensorMessageType > 127) {
            sensorMessageType = sensorMessageType - 128;
        }

        SDNWiseMessageType messageType = SDNWiseBuiltinMessageType.getType(sensorMessageType);

        switch (sensorMessageType) {
            case DATA:
                sdnWiseMessage = new SDNWiseDataMessage(getPayload(networkPacket));
                break;
            case BEACON:
                BeaconPacket beaconPacket = new BeaconPacket(networkPacket);
                sdnWiseMessage = new SDNWiseBeaconMessage(beaconPacket.getDistance(), beaconPacket.getBattery());
                break;
//            case MULTICAST_GROUP_JOIN:
//                byte[] multicastPayload = networkPacket.getPayload();
//                sdnWiseMessage = new SDNWiseMulticastReportMessage(
//                        multicastPayload[0], new byte[] {multicastPayload[1], multicastPayload[2]});
//                break;
//            case MULTICAST_DATA:
//                log.info("Getting multicast data message from packet {}",
//                        Arrays.toString(networkPacket.toByteArray()));
//                sdnWiseMessage = new SDNWiseMulticastDataMessage((byte) networkPacket.getNetId(),
//                        networkPacket.getPayload());
//                if (sdnWiseMessage == null) {
//                    log.info("Received null message");
//                }
//                break;
//            case MULTICAST_DATA_REQUEST:
//                log.info("Getting multicast data request message from packet {}",
//                        Arrays.toString(networkPacket.toByteArray()));
//                sdnWiseMessage = new SDNWiseMulticastDataMessage((byte) networkPacket.getNetId(),
//                        networkPacket.getPayload());
//                break;
//            case MULTICAST_CTRL_DATA_REQUEST:
//                log.info("Getting multicast data controller request message from packet {}",
//                        Arrays.toString(networkPacket.toByteArray()));
//                sdnWiseMessage = new SDNWiseMulticastDataMessage((byte) networkPacket.getNetId(),
//                        networkPacket.getPayload());
//                break;
            case REQUEST:
                sdnWiseMessage = new SDNWiseRequestMessage();
                break;
            case RESPONSE:
                SDNWiseNodeId src = new SDNWiseNodeId(networkPacket.getNet(), networkPacket.getSrc().getArray());
                SDNWiseNodeId dst = new SDNWiseNodeId(networkPacket.getNet(), networkPacket.getDst().getArray());
                sdnWiseMessage = new SDNWiseResponseMessage(src, dst);
                break;
//            case OPEN_PATH:
//                break;
//            case CONFIG:
//                break;
//            case DATA_REQUEST:
//                sdnWiseMessage = new SDNWiseDataMessage(networkPacket.getPayload());
//                break;
            default:
                sdnWiseMessage = new SDNWiseAppMessage(getPayload(networkPacket));
//                if (networkPacket.getType() >= 128) {
//                    sensorMessageType = DATA_REQUEST;
//                    sdnWiseMessage = new SDNWiseDataMessage(networkPacket.getPayload());
//                }
                break;
        }

        if (sdnWiseMessage != null) {
            sdnWiseMessage.setMessageType(messageType);
            sdnWiseMessage.setId(networkPacket.getNet());
            sdnWiseMessage.setLength(networkPacket.getLen());
            sdnWiseMessage.setSource(new SDNWiseNodeId(
                    networkPacket.getNet(), networkPacket.getSrc().getArray()));
            sdnWiseMessage.setDestination(new SDNWiseNodeId(
                    networkPacket.getNet(), networkPacket.getDst().getArray()));
            sdnWiseMessage.setTtl(networkPacket.getTtl());
            sdnWiseMessage.setVersion(SDNWiseVersion.SDN_WISE);
            sdnWiseMessage.setRawDataPayload(getPayload(networkPacket));
            sdnWiseMessage.setNxHop(new SDNWiseNodeId(networkPacket.getNet(),
                    networkPacket.getNxh().getArray()));
        }


        return sdnWiseMessage;
    }

    public SDNWiseNodeId getNxHop() {
        return nxHop;
    }

    public void setNxHop(SDNWiseNodeId nxHop) {
        this.nxHop = nxHop;
    }

    public byte[] getRawDataPayload() {
        return rawDataPayload;
    }

    public void setRawDataPayload(byte[] rawDataPayload) {
        this.rawDataPayload = rawDataPayload;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SDNWiseMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(SDNWiseMessageType messageType) {
        this.messageType = messageType;
    }

    public SDNWiseNodeId getSource() {
        return source;
    }

    public void setSource(SDNWiseNodeId source) {
        this.source = source;
        this.setId(source.netId());
    }

    public SDNWiseNodeId getDestination() {
        return destination;
    }

    public void setDestination(SDNWiseNodeId destination) {
        this.setId(destination.netId());
        this.destination = destination;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public SDNWiseVersion getVersion() {
        return version;
    }

    public void setVersion(SDNWiseVersion version) {
        this.version = version;
    }

    public void writeTo(org.onosproject.sdnwise.protocol.SDNWiseNode sdnWiseNode) {
        sdnWiseNode.sendMsg(this);
    }

    public byte[] serialize() {
        byte[] buf;

        if (getNetworkPackets() == null) {
            buf = getNetworkPacket().toByteArray();
        } else {
            buf = new byte[this.getLength()];
            List<NetworkPacket> networkPackets = getNetworkPackets();
            int offset = 0;
            for (NetworkPacket networkPacket : networkPackets) {
                byte[] arr = networkPacket.toByteArray();
                System.arraycopy(arr, 0, buf, offset, arr.length);
                offset = offset + arr.length;
            }
        }

        return buf;
    }

    public NetworkPacket getNetworkPacket() {
        NetworkPacket networkPacket = new NetworkPacket(this.getId(), new NodeAddress(0), new NodeAddress(0));
        networkPacket.setTyp((byte) this.getMessageType().getNetworkPacketType());
        if (destination != null) {
            networkPacket.setDst(new NodeAddress(destination.address()));
        }
        if (source != null) {
            networkPacket.setSrc(new NodeAddress(source.address()));
            networkPacket.setNxh(new NodeAddress(source.address()));
        }
        networkPacket.setNet((byte) this.getId());
//        networkPacket.setLen((byte) 10);
        networkPacket.setTtl((byte) 100);
        networkPacket = setPayload(networkPacket, this.getRawDataPayload());
        networkPacket.setNxh(new NodeAddress(getNxHop().address()));

        return networkPacket;
    }

    public List<NetworkPacket> getNetworkPackets() {
        return null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(SDNWiseMessage.class)
                .add("type", getMessageType())
                .add("src", source.toString())
                .add("dst", destination.toString())
                .toString();
    }
}
