package org.onosproject.sdnwise.controller.impl;

import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.onlab.packet.IpAddress;
import org.onosproject.net.PortNumber;
import org.onosproject.sdnwise.controller.driver.SDNWiseNodeDriver;
import org.onosproject.sdnwise.protocol.SDNWiseMessage;
import org.onosproject.sdnwise.protocol.SDNWiseNodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by aca on 2/19/15.
 */
public class SDNWiseChannelHandler extends IdleStateAwareChannelHandler {
    private static final Logger log = LoggerFactory.getLogger(SDNWiseChannelHandler.class);
    private final Controller controller;
    private SDNWiseNodeDriver driver;
    private Channel channel;
    byte[] prevRecvBuf = null;

    public SDNWiseChannelHandler(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx,
                                 ChannelStateEvent e) throws Exception {
        channel = e.getChannel();
        log.info("New sensor node connection from {}", channel.getRemoteAddress());
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx,
                                    ChannelStateEvent e) throws Exception {
//        log.info("Sensor node disconnected callback for node:{}. Cleaning up..." + getSensorNodeInfoString());
//        driver.removeConnectedNode();
    }

    private String getSensorNodeInfoString() {
        if (driver != null) {
            return driver.toString();
        }
        String channelString;
        if (channel == null || channel.getRemoteAddress() == null) {
            channelString = "?";
        } else {
            channelString = channel.getRemoteAddress().toString();
        }

        return String.format("[%s]", channelString);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        InetAddress address = ((InetSocketAddress) ctx.getChannel().getRemoteAddress()).getAddress();
        int port = ((InetSocketAddress) ctx.getChannel().getRemoteAddress()).getPort();
        IpAddress ipAddress = IpAddress.valueOf(address);
        PortNumber portNumber = PortNumber.portNumber(port);

//        log.info("Received the packet from node with IP {} and Port {}", ipAddress.toString(), port);

        if (e.getMessage() instanceof List) {
            List<NetworkPacket> networkPackets = (List<NetworkPacket>) e.getMessage();
            for (NetworkPacket networkPacket : networkPackets) {
                this.processNetworkPacket(networkPacket, ipAddress, portNumber);
            }
        } else if (e.getMessage() instanceof NetworkPacket) {
            NetworkPacket networkPacket = (NetworkPacket) e.getMessage();
        } else if (e.getMessage() instanceof byte[]) {
            byte[] bytes = (byte[]) e.getMessage();
            log.info(new String(Arrays.toString(bytes)));
        } else {
            ChannelBuffer buf = (ChannelBuffer) e.getMessage();
            ByteBuffer byteBuffer = buf.toByteBuffer();
            List<NetworkPacket> networkPackets = buildNetworkPackets(byteBuffer);
            try {
//                NetworkPacket networkPacket = new NetworkPacket(byteBuffer.array());
//                processNetworkPacket(networkPacket);
                if (networkPackets != null) {
                    networkPackets.forEach(networkPacket -> processNetworkPacket(networkPacket, ipAddress, portNumber));
                }
            } catch (Exception ex) {
                log.info(Arrays.toString(byteBuffer.array()));
                ex.printStackTrace();
            }

//            log.error("Unknown message type: ");
//            while (buf.readable()) {
//
//            }
        }
    }

    private List<NetworkPacket> buildNetworkPackets(ByteBuffer buf) {
        byte[] packets = buf.array();
        List<NetworkPacket> networkPackets = new ArrayList<>();

        int i = 0;
        while (i < packets.length) {
            int length;
            if (prevRecvBuf != null) {
                length = prevRecvBuf[1];
//                log.info("Processing with previously received packets overall length is {}", length);
                byte[] intermediatePacket = new byte[length];

                for (int j = 0; j < prevRecvBuf.length; j++) {
                    intermediatePacket[j] = prevRecvBuf[j];
                }
                for (int j = prevRecvBuf.length; j < length; j++) {
                    intermediatePacket[j] = packets[i];
                    i++;
                }
                prevRecvBuf = null;
                NetworkPacket networkPacket = new NetworkPacket(intermediatePacket);
                networkPackets.add(networkPacket);
            } else {
                length = packets[i + 1];
                byte[] intermediatePacket = new byte[length];
                try {
                    if ((i + length) > packets.length) {
                        int availableLength = packets.length - i;
                        prevRecvBuf = new byte[availableLength];
                        for (int j = 0; j < availableLength; j++) {
                            prevRecvBuf[j] = packets[i];
                            i++;
                        }
                    } else {
                        for (int j = 0; j < length; j++) {
                            intermediatePacket[j] = packets[i];
                            i++;
                        }
//                        log.info("No previous packets; attempting to create packet with length {}",
//                                intermediatePacket[0]);
                        NetworkPacket networkPacket = new NetworkPacket(intermediatePacket);
                        networkPackets.add(networkPacket);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    log.info("Exception on Packet {}", Arrays.toString(packets));
                }

            }
        }

        return networkPackets;
    }

    private void processNetworkPacket(NetworkPacket networkPacket, IpAddress ipAddress, PortNumber portNumber) {
        log.info("Received message {}", networkPacket.toString());
        SDNWiseMessage sdnWiseMessage = SDNWiseMessage.getMessageFromPacket(networkPacket, ipAddress, portNumber);
        if (sdnWiseMessage != null) {
            SDNWiseNodeId nodeId = sdnWiseMessage.getSource();
//        DeviceId nodeId = sdnWiseMessage.getSource();
            SDNWiseNodeDriver nodeDriver = controller.getDriver(nodeId, channel);
            this.driver = controller.getDriver(nodeId, channel);
            boolean nodeConnected = nodeDriver.connectNode();
//            log.info("Connecting node {} : {}", nodeId.uri(), nodeConnected);
//            if (sdnWiseMessage.getMessageType().equals(SDNWiseBuiltinMessageType.REPORT)) {
//                log.info("Have a report message here");
//            } else {
//                log.info("Found a message of type {}",
//                        sdnWiseMessage.getMessageType().getSensorPacketType().originalId());
//            }
            nodeDriver.handleMessage(sdnWiseMessage);
//            switch (networkPacket.getType()) {
//                case SDN_WISE_REPORT:
//                    sdnWiseMessage = (SDNWiseReportMessage) sdnWiseMessage;
//                    nodeDriver.connectNode();
////                nodeDriver.setChannel(channel);
//                    nodeDriver.handleMessage(sdnWiseMessage);
//                    break;
//                case MULTICAST_DATA:
//                    nodeDriver.handleMessage(sdnWiseMessage);
//                    break;
//                case MULTICAST_GROUP_JOIN:
//                    nodeDriver.handleMessage(sdnWiseMessage);
//                    break;
//                case DATA_REQUEST:
//                    nodeDriver.handleMessage(sdnWiseMessage);
//                    break;
//                case REQUEST:
//                    nodeDriver.handleMessage(sdnWiseMessage);
//                    break;
//                case DATA:
//                    nodeDriver.handleMessage(sdnWiseMessage);
//                    break;
//                case DPID_CONNECTION:
//                    nodeDriver.connectNode();
//                    nodeDriver.handleMessage(sdnWiseMessage);
//                    break;
//                case MULTICAST_DATA_REQUEST:
//                    nodeDriver.handleMessage(sdnWiseMessage);
//                    break;
//                case MULTICAST_CTRL_DATA_REQUEST:
//                    nodeDriver.handleMessage(sdnWiseMessage);
//                    break;
//                default:
//
//                    break;
//            }
        } else {
            log.error("Have a NULL message from packet {}", Arrays.toString(networkPacket.toByteArray()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        if (e.getCause() instanceof IOException) {
            log.error("Disconnecting node {} due to IOError: {}",
                    getSensorNodeInfoString(), e.getCause().getMessage());
            StackTraceElement[] stackTraceElements = e.getCause().getStackTrace();
            for (int i = 0; i < stackTraceElements.length; i++) {
                log.error(stackTraceElements[i].toString());
            }
        } else {
            log.error("Error while processing message from sensor node "
                    + getSensorNodeInfoString(), e.getCause());
//            ctx.getChannel().close();
        }
    }
}
