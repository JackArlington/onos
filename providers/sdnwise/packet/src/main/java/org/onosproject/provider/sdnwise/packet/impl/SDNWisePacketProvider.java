package org.onosproject.provider.sdnwise.packet.impl;

import com.google.common.collect.Lists;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onlab.packet.Data;
import org.onlab.packet.Ethernet;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.DefaultAnnotations;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Link;
import org.onosproject.net.SensorNode;
import org.onosproject.net.SensorNodeId;
import org.onosproject.net.SparseAnnotations;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.link.DefaultLinkDescription;
import org.onosproject.net.link.LinkDescription;
import org.onosproject.net.link.LinkProvider;
import org.onosproject.net.link.LinkProviderRegistry;
import org.onosproject.net.link.LinkProviderService;
import org.onosproject.net.link.LinkService;
import org.onosproject.net.multicast.GroupManagementProvider;
import org.onosproject.net.multicast.GroupManagementProviderRegistry;
import org.onosproject.net.multicast.GroupManagementProviderService;
import org.onosproject.net.packet.InboundPacket;
import org.onosproject.net.packet.OutboundPacket;
import org.onosproject.net.packet.PacketProvider;
import org.onosproject.net.packet.PacketProviderRegistry;
import org.onosproject.net.packet.PacketProviderService;
import org.onosproject.net.provider.AbstractProvider;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.net.sensor.DefaultSensorNodeDescription;
import org.onosproject.net.sensor.SensorNodeAddress;
import org.onosproject.net.sensor.SensorNodeDesciption;
import org.onosproject.net.sensor.SensorNodeProvider;
import org.onosproject.net.sensor.SensorNodeProviderRegistry;
import org.onosproject.net.sensor.SensorNodeProviderService;
import org.onosproject.net.sensor.SensorNodeService;
import org.onosproject.net.sensorflow.SensorFlowInstruction;
import org.onosproject.net.sensorflow.SensorFlowPacketTypeInstruction;
import org.onosproject.net.sensorflow.SensorFlowSetDstAddrInstruction;
import org.onosproject.net.sensorflow.SensorFlowSetGeoNxHopCoordinatesInstruction;
import org.onosproject.net.sensorflow.SensorFlowSetGeoNxHopInstruction;
import org.onosproject.net.sensorflow.SensorFlowSetGeoPrvHopInstruction;
import org.onosproject.net.sensorflow.SensorFlowSetSrcAddrInstruction;
import org.onosproject.net.sensorflow.SensorTrafficTreatment;
import org.onosproject.net.sensorpacket.DefaultSensorInboundPacket;
import org.onosproject.net.sensorpacket.SensorPacketTypeRegistry.SensorPacketType;
import org.onosproject.sdnwise.controller.SDNWiseController;
import org.onosproject.sdnwise.controller.SDNWisePacketListener;
import org.onosproject.sdnwise.protocol.SDNWiseBuiltinMessageType;
import org.onosproject.sdnwise.protocol.SDNWiseDPConnectionMessage;
import org.onosproject.sdnwise.protocol.SDNWiseMessage;
import org.onosproject.sdnwise.protocol.SDNWiseMessageType;
import org.onosproject.sdnwise.protocol.SDNWiseMulticastDataMessage;
import org.onosproject.sdnwise.protocol.SDNWiseNode;
import org.onosproject.sdnwise.protocol.SDNWiseNodeId;
import org.onosproject.sdnwise.protocol.SDNWiseReportMessage;
import org.onosproject.sdnwise.protocol.SDNWiseResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by aca on 2/26/15.
 */
@Component(immediate = true)
public class SDNWisePacketProvider extends AbstractProvider
        implements PacketProvider, LinkProvider, SensorNodeProvider, GroupManagementProvider {
    private static final Logger LOG = LoggerFactory.getLogger(SDNWisePacketProvider.class);

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected PacketProviderRegistry packetProviderRegistry;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected SDNWiseController controller;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LinkProviderRegistry linkProviderRegistry;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LinkService linkService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected SensorNodeProviderRegistry sensorNodeProviderRegistry;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected GroupManagementProviderRegistry groupManagementProviderRegistry;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected SensorNodeService sensorNodeService;

    private PacketProviderService providerService;
    private LinkProviderService linkProviderService;
    private SensorNodeProviderService sensorNodeProviderService;
    private GroupManagementProviderService groupManagementProviderService;

    private InternalDeviceListener deviceListener = new InternalDeviceListener();

    private SDNWisePacketListener packetListener = new InternalPacketProvider();

    private Map<String, Long> sensorPortsUsed = new ConcurrentHashMap<>();
    private List<DeviceIdPair> sensorPairs = Lists.newCopyOnWriteArrayList();

    private Map<DeviceId, LinkDescription> linkDescriptions = new ConcurrentHashMap<>();

    private Object lock = new Object();
    private Object linkLock = new Object();

    /**
     * Creates a provider with the supplier identifier.
     */
    public SDNWisePacketProvider() {
        super(new ProviderId("sdnwise", "org.onosproject.provider.sdnwise"));
        LOG.info("Initializing SDN WISE Packet Provider");
    }

    @Activate
    public void activate() {
        sensorNodeProviderService = sensorNodeProviderRegistry.register(this);
        providerService = packetProviderRegistry.register(this);
        linkProviderService = linkProviderRegistry.register(this);
        groupManagementProviderService = groupManagementProviderRegistry.register(this);
        deviceService.addListener(deviceListener);
        controller.addPacketListener(packetListener);
        LOG.info("Started SDNWise Packet Provider");
    }

    @Deactivate
    public void deactivate() {
        sensorNodeProviderRegistry.unregister(this);
        packetProviderRegistry.unregister(this);
        groupManagementProviderRegistry.unregister(this);
        controller.removePacketListener(packetListener);
        providerService = null;
        linkProviderService = null;
        linkService = null;
        sensorNodeProviderService = null;
        LOG.info("Stopped SDNWise Packet Provider");
    }

    @Override
    public void emit(OutboundPacket packet) {
        DeviceId deviceId = packet.sendThrough();
//        SDNWiseNodeId sdnWiseNodeId = SDNWiseNodeId.dpid(deviceId.uri());
//        LOG.info("Emitting to device with id {}", deviceId.uri());
        SDNWiseNode node = controller.getNode(deviceId);

//        SDNWiseMessage message = SDNWiseMessage.fromByteBuffer(packet.data());
        SDNWiseMessage message = SDNWiseMessage.fromPayload(packet.data());
        SensorNode dstNode = sensorNodeService.getSensorNode(
                SensorNodeId.sensorNodeId(node.getId().generateMacAddress(), node.getId().netId()));
        SensorNode associatedSink = dstNode.associatedSink();
        message.setNxHop(SDNWiseNodeId.fromMacAddress(associatedSink.mac()));
        message.setTtl(100);

//        NetworkPacket networkPacket = new NetworkPacket(packet.data().array());

        SensorTrafficTreatment localTrafficTreatment = (SensorTrafficTreatment) packet.treatment();

        if (localTrafficTreatment != null) {
            List<SensorFlowInstruction> instructions = localTrafficTreatment.sensorFlowInstructions();
            if ((instructions != null) && (instructions.size() > 0)) {
                for (SensorFlowInstruction instruction : instructions) {
                    switch (instruction.getSensorFlowInstructionType()) {
                        case SET_SRC_ADDR:
                            SensorFlowSetSrcAddrInstruction srcAddrInstruction =
                                    (SensorFlowSetSrcAddrInstruction) instruction;
                            int netId = srcAddrInstruction.getSrcAddr().getNetId();
                            SDNWiseNodeId srcNodeId = new SDNWiseNodeId(netId,
                                    srcAddrInstruction.getSrcAddr().getAddr());
//                            LOG.info("Setting source to node {}", srcNodeId.uri());
                            message.setSource(srcNodeId);
//                            message.setMessageType(SensorMessageType.MULTICAST_DATA);
//                            networkPacket.setType((byte) 10);
                            break;
                        case SET_DST_ADDR:
                            SensorFlowSetDstAddrInstruction dstAddrInstruction =
                                    (SensorFlowSetDstAddrInstruction) instruction;
                            netId = dstAddrInstruction.getDstAddr().getNetId();
                            SDNWiseNodeId dstNodeId = new SDNWiseNodeId(netId,
                                    dstAddrInstruction.getDstAddr().getAddr());
//                            LOG.info("Setting destination to node {}", dstNodeId.uri());
                            message.setDestination(dstNodeId);
//                            message.setMessageType(SensorMessageType.MULTICAST_DATA);
                            break;
                        case SET_GEO_NX_HOP_COORD:
                            SensorFlowSetGeoNxHopCoordinatesInstruction coordinatesInstruction =
                                    (SensorFlowSetGeoNxHopCoordinatesInstruction) instruction;
                            byte[] coords = coordinatesInstruction.getCoords();
                            ((SDNWiseMulticastDataMessage) message).setCurCoordinates(coords);
                            LOG.info("Setting coordinates to {}", Arrays.toString(coords));
                            break;
                        case SET_GEO_NX_HOP:
                            SensorFlowSetGeoNxHopInstruction geoNxHopInstruction =
                                    (SensorFlowSetGeoNxHopInstruction) instruction;
                            SensorNodeAddress geoNxHopAddress = geoNxHopInstruction.geoNxHopAddress();
                            ((SDNWiseMulticastDataMessage) message).setCur(geoNxHopAddress);
                            break;
                        case SET_GEO_PRV_HOP:
                            SensorFlowSetGeoPrvHopInstruction geoPrvHopInstruction =
                                    (SensorFlowSetGeoPrvHopInstruction) instruction;
                            SensorNodeAddress geoPrvHopAddress = geoPrvHopInstruction.geoPrvHopAddress();
                            ((SDNWiseMulticastDataMessage) message).setPrev(geoPrvHopAddress);
                            break;
                        case SET_PACKET_TYPE:
                            SensorFlowPacketTypeInstruction packetTypeInstruction =
                                    (SensorFlowPacketTypeInstruction) instruction;
                            SensorPacketType messageType = packetTypeInstruction.packetType();
                            message.setMessageType(SDNWiseBuiltinMessageType.getType(messageType.originalId()));
//                            LOG.info("Set message type to emit to {} and packet type to {}", message.getMessageType()
//                                    .getSensorPacketType().originalId(),
//                                    message.getMessageType().getNetworkPacketType());
                            break;
                        default:
                            LOG.warn("Cannot handle instruction {}", instruction.getSensorFlowInstructionType());
                    }
                }
            }
//            LOG.info("Getting multicast data message");
//            message = SDNWiseMulticastDataMessage.getMessageFromPacket(networkPacket);
            if (message == null) {
                LOG.info("The message is null");
            }
        } else {
            LOG.info("Getting plain data message");
//            message = SDNWiseDataMessage.getMessageFromPacket(networkPacket);
        }

//        LOG.info("Emitting message {}", Arrays.toString(message.getNetworkPacket().toByteArray()));

//        sdnWiseDataMessage.setDestination(node.getId());
//        LOG.info("Data message payload size = " + message.getAppData().length);
//        SDNWiseMessage sdnWiseMessage = SDNWiseMessage.getMessageFromPacket(networkPacket);

        if (node == null) {
            LOG.info("Do not have the node {}", deviceId);
        }
        //TODO: FIXME
//        node = controller.getNode(DeviceId.deviceId("sdnwise:00:00:00:01:00:02"));
        node.sendMsg(message);
    }

    @Override
    public void triggerProbe(SensorNode sensorNode) {

    }


    private class InternalDeviceListener implements DeviceListener {

        @Override
        public void event(DeviceEvent event) {
            if (event.type().equals(DeviceEvent.Type.DEVICE_ADDED)) {
                Device device = event.subject();
                LOG.info("Device {} is now alive; looking for links", device.id());
                LinkDescription linkDescription = linkDescriptions.get(device.id());
                if (linkDescription != null) {
                    LOG.info("Found link {}", linkDescription);
                    linkProviderService.linkDetected(linkDescription);
                    linkDescriptions.remove(device.id());
                }
            }
        }
    }

    private class InternalPacketProvider implements SDNWisePacketListener {
        @Override
        public void handlePacket(SDNWiseMessage message) {
            InboundPacket inboundPacket = null;
            SDNWiseNodeId incomingNodeId = message.getSource();
            SDNWiseNodeId destinationNodeId = message.getDestination();
//            LOG.info("Received message with Type {}, SRC {} and DST {}",
//                    message.getMessageType().getSensorPacketType().originalId(),
//                    incomingNodeId.uri(), destinationNodeId.uri());
            DeviceId incomingDeviceId = DeviceId.deviceId(incomingNodeId.uri());
            SDNWiseNode incomingNode = controller.getNode(incomingDeviceId);
            Long curPortNumber = sensorPortsUsed.get(incomingDeviceId.uri().toString());
            long portConnection = 0;
            if (curPortNumber != null) {
                portConnection = curPortNumber.longValue();
            }
            portConnection++;
            ConnectPoint connectPoint = new ConnectPoint(incomingDeviceId, DeviceIdPair.CONTROLLER_PORT);
            sensorPortsUsed.put(incomingDeviceId.uri().toString(), portConnection);

            Ethernet ethernet = new Ethernet();

            if (destinationNodeId != null) {
                ethernet.setDestinationMACAddress(destinationNodeId.generateMacAddress());
            }
            ethernet.setVlanID((short) incomingNodeId.netId());
            SDNWiseCorePacketContext sdnWiseCorePacketContext = null;

            SDNWiseMessageType messageType = message.getMessageType();

            if (messageType.equals(SDNWiseBuiltinMessageType.BEACON)) {
                LOG.warn("Cannot handle BEACON packets");
            } else if (messageType.equals(SDNWiseBuiltinMessageType.CONFIG)) {
                LOG.warn("Cannot handle CONFIG packets");
            } else if (messageType.equals(SDNWiseBuiltinMessageType.DATA)) {
                ethernet.setSourceMACAddress(incomingNodeId.generateMacAddress());
                inboundPacket = new DefaultSensorInboundPacket(messageType.getSensorPacketType(), connectPoint,
                        ethernet, ByteBuffer.wrap(message.serialize()));
                sdnWiseCorePacketContext = new SDNWiseCorePacketContext(System.currentTimeMillis(),
                        inboundPacket, null, false, incomingNode);
                providerService.processPacket(sdnWiseCorePacketContext);
            } else if (messageType.equals(SDNWiseBuiltinMessageType.OPEN_PATH)) {
                ethernet.setSourceMACAddress(incomingNodeId.generateMacAddress())
                        .setDestinationMACAddress(message.getDestination().generateMacAddress())
                        .setPayload(new Data(message.getRawDataPayload()));
                inboundPacket = new DefaultSensorInboundPacket(messageType.getSensorPacketType(), connectPoint,
                        ethernet, ByteBuffer.wrap(message.serialize()));
                sdnWiseCorePacketContext = new SDNWiseCorePacketContext(System.currentTimeMillis(),
                        inboundPacket, null, false, incomingNode);
                providerService.processPacket(sdnWiseCorePacketContext);
            } else if (messageType.equals(SDNWiseBuiltinMessageType.REG_PROXY)) {
                SDNWiseDPConnectionMessage sdnWiseDPConnectionMessage =
                        (SDNWiseDPConnectionMessage) message;
                handleDPConnectionMessage(sdnWiseDPConnectionMessage);
                DeviceId connectionSwitchId = sdnWiseDPConnectionMessage.dpid();
                ethernet.setSourceMACAddress(incomingNodeId.generateMacAddress());
                ConnectPoint switchConnectPoint = new ConnectPoint(
                        connectionSwitchId, sdnWiseDPConnectionMessage.portNumber());
                LinkDescription sensorSwitchLinkDescription = new DefaultLinkDescription(
                        connectPoint, switchConnectPoint, Link.Type.DIRECT);
                if (deviceService.getDevice(incomingDeviceId) != null) {
                    linkProviderService.linkDetected(sensorSwitchLinkDescription);
                } else {
                    linkDescriptions.put(incomingDeviceId, sensorSwitchLinkDescription);
                }
                sensorSwitchLinkDescription = new DefaultLinkDescription(
                        switchConnectPoint, connectPoint, Link.Type.DIRECT);
                linkProviderService.linkDetected(sensorSwitchLinkDescription);
                if (deviceService.getDevice(incomingDeviceId) != null) {
                    linkProviderService.linkDetected(sensorSwitchLinkDescription);
                } else {
                    linkDescriptions.put(incomingDeviceId, sensorSwitchLinkDescription);
                }
//                linkProviderService.linkDetected(sensorSwitchLinkDescription);

                LOG.info("Connecting SINK {} with OFSwitch {}",
                        connectPoint.deviceId(), switchConnectPoint.deviceId());
            } else if (messageType.equals(SDNWiseBuiltinMessageType.REPORT)) {
                ethernet.setSourceMACAddress(incomingNodeId.generateMacAddress());
                SDNWiseReportMessage reportMessage = (SDNWiseReportMessage) message;

//                LOG.info("Received REPORT message from node {}", Arrays.toString(message.getSource().address()));
                Map<SDNWiseNodeId, Integer> rssis = reportMessage.getNeighborRSSI();
                if ((rssis != null) && (rssis.size() > 0)) {
                    for (Map.Entry<SDNWiseNodeId, Integer> rssiEntry : rssis.entrySet()) {
                        SDNWiseNodeId neighborNodeId = rssiEntry.getKey();

//                        if (!(sensorNeighborExists(SensorNodeId.sensorNodeId(
//                                        incomingNodeId.generateMacAddress(), incomingNodeId.netId()),
//                                SensorNodeId.sensorNodeId(
//                                        neighborNodeId.generateMacAddress(), neighborNodeId.netId())))) {
//
                            incomingNode.setRSSI(rssiEntry.getKey(), rssiEntry.getValue());
                            DeviceId neighborDeviceId = DeviceId.deviceId(neighborNodeId.uri());
//
//                            Long neighborCurPortNumber = sensorPortsUsed.get(neighborDeviceId.uri().toString());
//                            long neighborPortNumber = 0;
//                            if (neighborCurPortNumber != null) {
//                                neighborPortNumber = neighborCurPortNumber.longValue();
//                            }
//                            neighborPortNumber++;
//                            ConnectPoint neighborConnectPoint = new ConnectPoint(neighborDeviceId,
//                                    PortNumber.portNumber(neighborPortNumber));
//
//                            sensorPortsUsed.put(neighborDeviceId.uri().toString(), neighborPortNumber);

//                            LOG.info("About to get into the critical section");
                            synchronized (lock) {
                                DeviceIdPair deviceIdPairToCheck =
                                        new DeviceIdPair(connectPoint.deviceId(), neighborDeviceId);
                                DeviceIdPair deviceIdPair = null;
                                for (DeviceIdPair pair : sensorPairs) {
                                    if (pair.equals(deviceIdPairToCheck)) {
                                        deviceIdPair = pair;
                                        break;
                                    }
                                }
                                if (deviceIdPair == null) {
                                    deviceIdPairToCheck.confirmPorts();
                                    deviceIdPair = deviceIdPairToCheck;
                                    sensorPairs.add(deviceIdPairToCheck);
                                }

                                SparseAnnotations linkAnnotations = DefaultAnnotations.builder()
                                        .set(neighborNodeId.toString(), rssiEntry.getValue().toString())
                                        .build();
                                LinkDescription linkDescription = new DefaultLinkDescription(
                                        deviceIdPair.getConnectPoint1(), deviceIdPair.getConnectPoint2(),
                                        Link.Type.DIRECT, linkAnnotations);

                                if (deviceService.getDevice(incomingDeviceId) != null) {
//                                    LOG.info("Device {} is there; creating link now...");
                                    linkProviderService.linkDetected(linkDescription);
                                } else {
//                                    LOG.info("Device {} is not there; storing and waiting...");
                                    linkDescriptions.put(deviceIdPair.getConnectPoint1().deviceId(), linkDescription);
                                }

//                                linkAnnotations = DefaultAnnotations.builder()
//                                        .set(incomingNodeId.toString(), rssiEntry.getValue().toString())
//                                        .build();
//                                linkDescription = new DefaultLinkDescription(
//                                        deviceIdPair.getConnectPoint2(), deviceIdPair.getConnectPoint1(),
//                                        Link.Type.DIRECT, linkAnnotations);
//                                linkProviderService.linkDetected(linkDescription);
                            }
//                        } else {
//                            LOG.info("Node {} is neighbor of {} already", neighborNodeId, incomingNodeId);
//                        }
                    }
                } else {
                    LOG.info("Node {} appears to have no neighbors", incomingNodeId);
                }

                handleReportMessage(reportMessage);
            } else if (messageType.equals(SDNWiseBuiltinMessageType.REQUEST)) {
                LOG.info("Received REQUEST message {}", Arrays.toString(message.serialize()));
                ethernet.setSourceMACAddress(incomingNodeId.generateMacAddress());
                ethernet.setDestinationMACAddress(message.getDestination().generateMacAddress());
                ethernet.setPayload(new Data(message.getRawDataPayload()));
                inboundPacket = new DefaultSensorInboundPacket(messageType.getSensorPacketType(), connectPoint,
                        ethernet, ByteBuffer.wrap(message.getNetworkPacket().toByteArray()));
                sdnWiseCorePacketContext = new SDNWiseCorePacketContext(System.currentTimeMillis(),
                        inboundPacket, null, false, incomingNode);
                providerService.processPacket(sdnWiseCorePacketContext);
            } else if (messageType.equals(SDNWiseBuiltinMessageType.RESPONSE)) {
                SDNWiseResponseMessage responseMessage = (SDNWiseResponseMessage) message;
                ethernet.setSourceMACAddress(incomingNodeId.generateMacAddress());
                ethernet.setDestinationMACAddress(message.getDestination().generateMacAddress());
                ethernet.setPayload(new Data(responseMessage.getRawDataPayload()));
                inboundPacket = new DefaultSensorInboundPacket(messageType.getSensorPacketType(), connectPoint,
                        ethernet, ByteBuffer.wrap(responseMessage.serialize()));
                sdnWiseCorePacketContext = new SDNWiseCorePacketContext(System.currentTimeMillis(),
                        inboundPacket, null, false, incomingNode);
                providerService.processPacket(sdnWiseCorePacketContext);
            } else {
//                LOG.info("Received MISC message {}", Arrays.toString(message.serialize()));
                ethernet.setSourceMACAddress(incomingNodeId.generateMacAddress())
                        .setDestinationMACAddress(message.getDestination().generateMacAddress())
                        .setPayload(new Data(message.getRawDataPayload()));
                inboundPacket = new DefaultSensorInboundPacket(messageType.getSensorPacketType(), connectPoint,
                        ethernet, ByteBuffer.wrap(message.serialize()));
                sdnWiseCorePacketContext = new SDNWiseCorePacketContext(System.currentTimeMillis(),
                        inboundPacket, null, false, incomingNode);
                providerService.processPacket(sdnWiseCorePacketContext);
            }
        }

        private boolean sensorNeighborExists(SensorNodeId sensor, SensorNodeId neighbor) {
            boolean isNeighborAlready = false;
            Map<SensorNodeId, Integer> neighborhood = sensorNodeService.getSensorNodeNeighbors(sensor);
            if (neighborhood != null) {
                Set<SensorNodeId> nodes = neighborhood.keySet();
                if (nodes.contains(neighbor)) {
                    isNeighborAlready = true;
                } else {
                    neighborhood = sensorNodeService.getSensorNodeNeighbors(neighbor);
                    if (neighborhood != null) {
                        nodes = neighborhood.keySet();
                        if (nodes.contains(sensor)) {
                            isNeighborAlready = true;
                        }
                    }
                }

            }
            return isNeighborAlready;
        }

        private void handleDPConnectionMessage(SDNWiseDPConnectionMessage sdnWiseDPConnectionMessage) {
            SensorNodeId sensorNodeId = SensorNodeId.sensorNodeId(
                    sdnWiseDPConnectionMessage.getSource().generateMacAddress(),
                    sdnWiseDPConnectionMessage.getSource().netId());

            SensorNodeDesciption sensorNodeDesciption =
                    new DefaultSensorNodeDescription(sensorNodeId.mac(), sdnWiseDPConnectionMessage.sinkMac(),
                            sdnWiseDPConnectionMessage.sinkIp(), sdnWiseDPConnectionMessage.sinkPort(),
                            sdnWiseDPConnectionMessage.sinkConnectionIp(),
                            sdnWiseDPConnectionMessage.sinkConnectionPort(), sdnWiseDPConnectionMessage.dpid(),
                            sensorNodeId.netId(), sdnWiseDPConnectionMessage.getSource().address(),
                            new HashMap<>(), Float.MAX_VALUE);

            DeviceId deviceId = DeviceId.deviceId(sdnWiseDPConnectionMessage.getSource().uri());
            sensorNodeProviderService.sensorNodeDetected(sensorNodeId, deviceId, sensorNodeDesciption);
            LOG.info("Detected node " + sensorNodeId.toString());
        }

        private void handleReportMessage(SDNWiseReportMessage sdnWiseReportMessage) {
            SensorNodeId sensorNodeId = SensorNodeId.sensorNodeId(
                    sdnWiseReportMessage.getSource().generateMacAddress(),
                    sdnWiseReportMessage.getSource().netId());
            Map<SDNWiseNodeId, Integer> neighbors = sdnWiseReportMessage.getNeighborRSSI();
            Map<SensorNodeId, Integer> sensorNeighbors = new HashMap<>();
            if ((neighbors != null) && (neighbors.size() > 0)) {
                for (Map.Entry<SDNWiseNodeId, Integer> neighbor : neighbors.entrySet()) {
                    SDNWiseNodeId id = neighbor.getKey();
                    SensorNodeId neighborSensorNodeId = SensorNodeId.sensorNodeId(
                            id.generateMacAddress(), id.netId());
                    sensorNeighbors.put(neighborSensorNodeId, neighbor.getValue());
                }
            }
            SensorNodeDesciption sensorNodeDesciption =
                    new DefaultSensorNodeDescription(sensorNodeId.mac(), null, null, null,
                            sdnWiseReportMessage.sinkIp(), sdnWiseReportMessage.sinkPort(), null,
                            sensorNodeId.netId(), sdnWiseReportMessage.getSource().address(),
                            sensorNeighbors, (float) sdnWiseReportMessage.getBatteryLevel());

            DeviceId deviceId = DeviceId.deviceId(sdnWiseReportMessage.getSource().uri());
            sensorNodeProviderService.sensorNodeDetected(sensorNodeId, deviceId, sensorNodeDesciption);
//            LOG.info("Detected node " + sensorNodeId.toString());
        }

    }

    class SDNWiseLinkDiscovery implements DeviceListener {

        @Override
        public void event(DeviceEvent event) {

        }
    }
}

//            switch (message.getMessageType()) {
//                case SensorMessageType.MULTICAST_DATA_REQUEST:
//                    SDNWiseMulticastDataMessage multicastDataReqMessage = (SDNWiseMulticastDataMessage) message;
//                    multicastDataReqMessage.setMessageType(SensorMessageType.MULTICAST_DATA_REQUEST);
//                    LOG.info("Processing multicast data request message {}", multicastDataReqMessage.toString());
//                    SensorNodeAddress reqMutlicastAddress = multicastDataReqMessage.group();
//                    Group reqGroup = new Group(reqMutlicastAddress);
//                    MacAddress dstMacAddress = reqGroup.getMacAddress();
//                    MacAddress srcMacAddress = null;
//                    if (multicastDataReqMessage.prev() != null) {
//                        srcMacAddress = multicastDataReqMessage.prev().generateMacAddress();
//                        ethernet.setSourceMACAddress(srcMacAddress);
//                    }
//                    ethernet.setDestinationMACAddress(dstMacAddress);
//                    ConnectPoint receivedPoint = new ConnectPoint(
//                            sensorNodeService.getSensorNodeByAddress(
//                                multicastDataReqMessage.cur().getNetId(), multicastDataReqMessage.cur().getAddr())
//                                    .deviceId(), null);
//                    inboundPacket = new DefaultInboundPacket(receivedPoint, ethernet,
//                            ByteBuffer.wrap(multicastDataReqMessage.getNetworkPacket().toByteArray()));
//                    sdnWiseCorePacketContext = new SDNWiseCorePacketContext(System.currentTimeMillis(),
//                            inboundPacket, null, false, incomingNode);
//                    providerService.processPacket(sdnWiseCorePacketContext);
//                    break;
//                case SensorMessageType.MULTICAST_CTRL_DATA_REQUEST:
//                    multicastDataReqMessage = (SDNWiseMulticastDataMessage) message;
//                    multicastDataReqMessage.setMessageType(SensorMessageType.MULTICAST_DATA_REQUEST);
//                    LOG.info("Processing multicast data request message {}", multicastDataReqMessage.toString());
//                    reqMutlicastAddress = multicastDataReqMessage.group();
//                    reqGroup = new Group(reqMutlicastAddress);
//                    dstMacAddress = reqGroup.getMacAddress();
//                    srcMacAddress = null;
//                    if (multicastDataReqMessage.prev() != null) {
//                        srcMacAddress = multicastDataReqMessage.prev().generateMacAddress();
//                        ethernet.setSourceMACAddress(srcMacAddress);
//                    }
//                    ethernet.setDestinationMACAddress(dstMacAddress);
//                    receivedPoint = new ConnectPoint(
//                            sensorNodeService.getSensorNodeByAddress(
//                                multicastDataReqMessage.cur().getNetId(), multicastDataReqMessage.cur().getAddr())
//                                    .deviceId(), null);
//                    inboundPacket = new DefaultInboundPacket(receivedPoint, ethernet,
//                            ByteBuffer.wrap(multicastDataReqMessage.getNetworkPacket().toByteArray()));
//                    sdnWiseCorePacketContext = new SDNWiseCorePacketContext(System.currentTimeMillis(),
//                            inboundPacket, null, false, incomingNode);
//                    providerService.processPacket(sdnWiseCorePacketContext);
//                    break;
//                case SensorMessageType.MULTICAST_DATA:
//                    multicastDataReqMessage = (SDNWiseMulticastDataMessage) message;
//                    LOG.info("Processing multicast data message {}", multicastDataReqMessage.toString());
//                    reqMutlicastAddress = multicastDataReqMessage.group();
//                    reqGroup = new Group(reqMutlicastAddress);
//                    dstMacAddress = reqGroup.getMacAddress();
//                    srcMacAddress = null;
//                    if (multicastDataReqMessage.prev() != null) {
//                        srcMacAddress = multicastDataReqMessage.prev().generateMacAddress();
//                        ethernet.setSourceMACAddress(srcMacAddress);
//                    }
//                    ethernet.setDestinationMACAddress(dstMacAddress);
//                    receivedPoint = new ConnectPoint(
//                            sensorNodeService.getSensorNodeByAddress(
//                                    multicastDataReqMessage.cur().getNetId(),
//                                    multicastDataReqMessage.cur().getAddr()).deviceId(), null);
//                    inboundPacket = new DefaultInboundPacket(receivedPoint, ethernet,
//                            ByteBuffer.wrap(multicastDataReqMessage.getNetworkPacket().toByteArray()));
//                    sdnWiseCorePacketContext = new SDNWiseCorePacketContext(System.currentTimeMillis(),
//                            inboundPacket, null, false, incomingNode);
//                    providerService.processPacket(sdnWiseCorePacketContext);
//                    break;
//                case SensorMessageType.MULTICAST_GROUP_JOIN:
//                    ethernet.setSourceMACAddress(incomingNodeId.generateMacAddress());
//                    SDNWiseMulticastReportMessage sdnWiseMulticastReportMessage =
//                            (SDNWiseMulticastReportMessage) message;
//                    SensorNodeId sensorNodeId = SensorNodeId.sensorNodeId(
//                            sdnWiseMulticastReportMessage.getSource().generateMacAddress(),
//                            sdnWiseMulticastReportMessage.getSource().netId());
//                    groupManagementProviderService.sensorNodeAdded(sensorNodeId,
//                            sdnWiseMulticastReportMessage.getSensorNodeAddress());
//                    break;
//            }
