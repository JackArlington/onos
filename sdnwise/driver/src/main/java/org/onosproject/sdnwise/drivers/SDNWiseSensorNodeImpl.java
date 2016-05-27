package org.onosproject.sdnwise.drivers;

import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.onosproject.sdnwise.controller.driver.SDNWiseAgent;
import org.onosproject.sdnwise.controller.driver.SDNWiseNodeDriver;
import org.onosproject.sdnwise.protocol.SDNWiseMessage;
import org.onosproject.sdnwise.protocol.SDNWiseNodeId;
import org.onosproject.sdnwise.protocol.SDNWiseNodeState;
import org.onosproject.sdnwise.protocol.SDNWiseVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by aca on 2/20/15.
 */
public class SDNWiseSensorNodeImpl implements SDNWiseNodeDriver {
    private final Logger log = LoggerFactory.getLogger(SDNWiseSensorNodeImpl.class);

    private Channel channel;
    private boolean connected;
    private final SDNWiseNodeId id;
    private SDNWiseAgent agent;

    private Map<SDNWiseNodeId, Integer> rssi;

    private final AtomicInteger xidCounter = new AtomicInteger(0);

    public SDNWiseSensorNodeImpl(SDNWiseNodeId id, Channel channel) {
        this.id = id;
        this.channel = channel;
        this.rssi = new HashMap<>();
    }

    @Override
    public void setAgent(SDNWiseAgent agent) {
        this.agent = agent;
    }

    @Override
    public void startDriverHandshake() {

    }

    @Override
    public boolean isDriverHandshakeComplete() {
        return false;
    }

    @Override
    public boolean connectNode() {
        return this.agent.addConnectedNode(id, this);
    }

    @Override
    public void removeConnectedNode() {
        this.agent.removeConnectedNode(id);
    }

    @Override
    public int getNextTransactionId() {
        return this.xidCounter.getAndIncrement();
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public void write(SDNWiseMessage msg) {
        if (msg != null) {
            if (msg.getNetworkPackets() != null) {
                List<NetworkPacket> networkPackets = msg.getNetworkPackets();
                networkPackets.forEach(this::sendNetworkPacket);
            } else {
                byte[] buf = msg.serialize();
                ChannelBuffer channelBuffer = ChannelBuffers.wrappedBuffer(buf);
//                if (channel.isConnected()) {
//                    log.info("Channel connected");
//                }
//                if (channel.isOpen()) {
//                    log.info("Channel open");
//                }
//                if (channel.isWritable()) {
//                    log.info("Channel writeable");
//                }
//                if (channel.isReadable()) {
//                    log.info("Channel readable");
//                } else {
//                    log.info("The buffer to be written is {}", Arrays.toString(channelBuffer.array()));
//                }
                channel.write(channelBuffer);
            }

//            log.info("Sending message {}", msg.toString());
//            log.info("Channel status is Connected {} Open {}",
//                    channel.isConnected(),
//                    channel.isOpen());
        } else {
            byte[] buf = {5, 0, 0, 0, 0};
            ChannelBuffer channelBuffer = ChannelBuffers.wrappedBuffer(buf);
//            ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes());
            channel.write(channelBuffer);
        }

    }

    private void sendNetworkPacket(NetworkPacket networkPacket) {
        ChannelBuffer channelBuffer = ChannelBuffers.wrappedBuffer(networkPacket.toByteArray());
//        log.info("Writing to channel {}", Arrays.toString(networkPacket.toByteArray()));
        channel.write(channelBuffer);
    }

    @Override
    public void write(List<SDNWiseMessage> msgs) {
        channel.write(msgs);
    }

    @Override
    public void sendMsg(SDNWiseMessage msg) {
        this.write(msg);
    }

    @Override
    public void sendMsg(List<SDNWiseMessage> msgs) {
        this.write(msgs);
    }

    @Override
    public void handleMessage(SDNWiseMessage fromNode) {
        this.agent.processMessage(fromNode);
    }

    @Override
    public String getID() {
        return this.id.toString();
    }

    @Override
    public SDNWiseNodeId getId() {
        return this.id;
    }

//    @Override
//    public long getId() {
//        return this.id.value();
//    }

    @Override
    public String manufacturerDescription() {
        return null;
    }

    @Override
    public String hardwareDescription() {
        return null;
    }

    @Override
    public String softwareDescription() {
        return null;
    }

    @Override
    public String serialNumber() {
        return null;
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public void disconnectNode() {
        this.channel.disconnect();
    }

    @Override
    public double batteryLevel() {
        return 0;
    }

    @Override
    public void setTransmissionPowerLevel(double level) {

    }

    @Override
    public double getTransmissionPowerLevel() {
        return 0;
    }

    @Override
    public double getRSSI(SDNWiseNodeId neighbor) {
        return this.rssi.get(neighbor);
    }

    @Override
    public void setRSSI(SDNWiseNodeId neighbor, int rssi) {
        this.rssi.put(neighbor, rssi);
    }

    @Override
    public SDNWiseNodeState getNodeState() {
        return null;
    }

    @Override
    public SDNWiseVersion getVersion() {
        return SDNWiseVersion.SDN_WISE;
    }

    @Override
    public String toString() {
        return "Node " + id.uri() + " Driver";
    }
}
