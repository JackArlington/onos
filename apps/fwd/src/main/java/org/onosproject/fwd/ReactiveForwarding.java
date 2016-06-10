/*
 * Copyright 2014 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onosproject.fwd;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Dictionary;
import java.util.Set;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onlab.packet.Ethernet;
import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Host;
import org.onosproject.net.HostId;
import org.onosproject.net.Link;
import org.onosproject.net.Path;
import org.onosproject.net.PortNumber;
import org.onosproject.net.SensorNode;
import org.onosproject.net.flow.DefaultFlowRule;
import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onosproject.net.flow.FlowRule;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.host.HostService;
import org.onosproject.net.packet.DefaultOutboundPacket;
import org.onosproject.net.packet.InboundPacket;
import org.onosproject.net.packet.OutboundPacket;
import org.onosproject.net.packet.PacketContext;
import org.onosproject.net.packet.PacketProcessor;
import org.onosproject.net.packet.PacketService;
import org.onosproject.net.sensor.SensorNodeAddress;
import org.onosproject.net.sensor.SensorNodeService;
import org.onosproject.net.topology.PathService;
import org.onosproject.net.topology.TopologyService;
import org.onosproject.net.sensorflow.SensorEnabledTrafficSelector;
import org.onosproject.net.sensorflow.SensorEnabledTrafficTreatment;
import org.onosproject.net.sensorflow.SensorTrafficSelector;
import org.onosproject.net.sensorflow.SensorTrafficTreatment;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;

/**
 * Sample reactive forwarding application.
 */
@Component(immediate = true)
public class ReactiveForwarding {

    private static final int TIMEOUT = 10;
    private static final int PRIORITY = 10;

    private final Logger log = getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected TopologyService topologyService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected PacketService packetService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected HostService hostService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowRuleService flowRuleService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected PathService pathService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected SensorNodeService sensorNodeService;

    private ReactivePacketProcessor processor = new ReactivePacketProcessor();

    private ApplicationId appId;

    @Property(name = "packetOutOnly", boolValue = false,
            label = "Enable packet-out only forwarding; default is false")
    private boolean packetOutOnly = false;

    @Activate
    public void activate() {
        appId = coreService.registerApplication("org.onosproject.fwd");
        packetService.addProcessor(processor, PacketProcessor.ADVISOR_MAX + 2);
        log.info("Started with Application ID {}", appId.id());
    }

    @Deactivate
    public void deactivate() {
        flowRuleService.removeFlowRulesById(appId);
        packetService.removeProcessor(processor);
        processor = null;
        log.info("Stopped");
    }

    @Modified
    public void modified(ComponentContext context) {
        Dictionary properties = context.getProperties();
        String flag = (String) properties.get("packetOutOnly");
        if (flag != null) {
            boolean enabled = flag.equals("true");
            if (packetOutOnly != enabled) {
                packetOutOnly = enabled;
                log.info("Reconfigured. Packet-out only forwarding is {}",
                         packetOutOnly ? "enabled" : "disabled");
            }
        }
    }

    /**
     * Packet processor responsible for forwarding packets along their paths.
     */
    private class ReactivePacketProcessor implements PacketProcessor {

        @Override
        public void process(PacketContext context) {
            // Stop processing if the packet has been handled, since we
            // can't do any more to it.

//            log.info("Destination MAC {}", context.inPacket().parsed().getDestinationMAC());

            if (context.isHandled()) {
//                log.info("Returning due to handled packet");
                return;
            }

            InboundPacket pkt = context.inPacket();
            Ethernet ethPkt = pkt.parsed();

            if (isControlPacket(ethPkt) || isIpv6Multicast(ethPkt) || ethPkt.isMulticast()) {
//                log.info("Returning due to control/IPv6 packet");
                return;
            }

            DeviceId checkSensor = DeviceId.deviceId(
                    "sdnwise:" + ethPkt.getDestinationMAC().toString());
            SensorNode sensorNode = sensorNodeService.getSensorNode(checkSensor);
            if (sensorNode != null) {
                integratedProcess(context);
            } else {

//            integratedProcess(context);
                if (pkt.receivedFrom().port().equals(PortNumber.ALL)) {
                    log.info("Got the SDN-WISE Message!!!");
                    sdnWiseProcess(pkt);
                    return;
                }

                // Bail if this is deemed to be a control or IPv6 multicast packet.
                if (isControlPacket(ethPkt) || isIpv6Multicast(ethPkt)) {
//                log.info("Returning due to control/IPv6 packet");
                    return;
                }

                HostId id = HostId.hostId(ethPkt.getDestinationMAC());

                // Do not process link-local addresses in any way.
                if (id.mac().isLinkLocal()) {
//                log.info("Returning due to link-local packet");
                    return;
                }

                // Do we know who this is for? If not, flood and bail.
                Host dst = hostService.getHost(id);
                if (dst == null) {
//                log.info("Returning due to no destination packet");
                    flood(context);
                    return;
                }

                // Are we on an edge switch that our destination is on? If so,
                // simply forward out to the destination and bail.
                if (pkt.receivedFrom().deviceId().equals(dst.location().deviceId())) {
                    if (!context.inPacket().receivedFrom().port().equals(dst.location().port())) {
                        installRule(context, dst.location().port());
                    }
//                log.info("Returning due to edge host");
                    return;
                }

                // Otherwise, get a set of paths that lead from here to the
                // destination edge switch.
                Set<Path> paths = topologyService.getPaths(topologyService.currentTopology(),
                        pkt.receivedFrom().deviceId(),
                        dst.location().deviceId());
                if (paths.isEmpty()) {
                    // If there are no paths, flood and bail.
                    log.info("Returning due to no paths");
                    flood(context);
                    return;
                }

//            Set<Path> pathServicePaths = pathService.getPaths(pkt.receivedFrom().deviceId(),
//                    dst.location().deviceId());
//

//            for (Path path : paths) {
//                log.info("Link {} in path from Node {} to Node {}",
//                        path.toString(), path.src().toString(), path.dst().toString());
//            }

//            if ((pathServicePaths != null) && (!pathServicePaths.isEmpty())) {
//                for (Path path : pathServicePaths) {
//                    log.info("Link {} in path from Node {} to Node {}",
//                            path.toString(), path.src().toString(), path.dst().toString());
//                    List<Link> pathLinks = path.links();
//                    for (Link link : pathLinks) {
//                        log.info("Got link from Node {} to Node {}",
//                                link.src().deviceId(), link.dst().deviceId());
//                    }
//                }
//            } else {
//                log.info("Empty path set from PathService");
//            }

                // Otherwise, pick a path that does not lead back to where we
                // came from; if no such path, flood and bail.
                Path path = pickForwardPath(paths, pkt.receivedFrom().port());
                if (path == null) {
                    log.warn("Doh... don't know where to go... {} -> {} received on {}",
                            ethPkt.getSourceMAC(), ethPkt.getDestinationMAC(),
                            pkt.receivedFrom());
                    flood(context);
                    return;
                }

//            log.info("Installing rule");
                // Otherwise forward and be done with it.
                installRule(context, path.src().port());
            }

        }

    }

    private void integratedProcess(PacketContext context) {
        InboundPacket pkt = context.inPacket();
        Ethernet ethPkt = pkt.parsed();

        log.info("Received packet from device {}", pkt.receivedFrom().deviceId());

        HostId id = HostId.hostId(ethPkt.getDestinationMAC());

        // Do not process link-local addresses in any way.
        if (id.mac().isLinkLocal()) {
            return;
        }

        // Do we know who this is for? If not, flood and bail.
        Host dst = hostService.getHost(id);
        if (dst != null) {
            if (pkt.receivedFrom().deviceId().equals(dst.location().deviceId())) {
                if (!context.inPacket().receivedFrom().port().equals(dst.location().port())) {
                    installRule(context, dst.location().port());
                }
//                log.info("Returning due to edge host");
                return;
            }

            // Otherwise, get a set of paths that lead from here to the
            // destination edge switch.
            Set<Path> paths = topologyService.getPaths(topologyService.currentTopology(),
                    pkt.receivedFrom().deviceId(),
                    dst.location().deviceId());
            if (paths.isEmpty()) {
                // If there are no paths, flood and bail.
//                log.info("Returning due to no paths");
                flood(context);
                return;
            }

            // Otherwise, pick a path that does not lead back to where we
            // came from; if no such path, flood and bail.
            Path path = pickForwardPath(paths, pkt.receivedFrom().port());
            if (path == null) {
                log.warn("Doh... don't know where to go... {} -> {} received on {}",
                        ethPkt.getSourceMAC(), ethPkt.getDestinationMAC(),
                        pkt.receivedFrom());
                flood(context);
                return;
            }

//            log.info("Installing rule");
            // Otherwise forward and be done with it.
            installRule(context, path.src().port());
        } else {
            // just forward towards node sdnwise:00:00:00:01:00:04
//            String dest = "sdnwise:00:00:00:01:00:04";
            DeviceId dstDeviceId = DeviceId.deviceId("sdnwise:" +
                    context.inPacket().parsed().getDestinationMAC().toString());

            SensorNode sensorNode = sensorNodeService.getSensorNode(dstDeviceId);
            if (sensorNode == null) {
                log.info("Something is wrong with destination id {}", dstDeviceId.toString());
            } else {
                log.info("Destination id {} appears to be OK", dstDeviceId.toString());
            }

            log.info("Target device MAC: {}", ethPkt.getDestinationMAC());

            DeviceId srcDeviceId = pkt.receivedFrom().deviceId();

//            TopologyGraph topologyGraph = topologyService.getGraph(topologyService.currentTopology());
//            Set<TopologyEdge> topologyEdges = topologyGraph.getEdges();
//            for (TopologyEdge topologyEdge : topologyEdges) {
//                Link link = topologyEdge.link();
//                log.info("Got link: {} - {}", link.src().deviceId().toString(),
//                        link.dst().deviceId().toString());
//            }

            Set<Path> pathSet = topologyService.getPaths(topologyService.currentTopology(),
                    srcDeviceId, dstDeviceId);
            if ((pathSet == null) || (pathSet.isEmpty())) {
                log.info("Empty path set from {} to {} ...",
                        pkt.receivedFrom().deviceId().toString(),
                        dstDeviceId.toString());
                return;
            }

            Path path = pathSet.iterator().next();
            if (context.inPacket().receivedFrom().deviceId().toString().startsWith("of")) {
                // find the sink
                for (Link link : path.links()) {
                    if (link.dst().deviceId().toString().startsWith("sdnwise")) {
                        DeviceId sinkDeviceId = DeviceId.deviceId(link.dst().deviceId().toString());
                        SensorNode sinkSensorNode1 = sensorNodeService.getSensorNode(sinkDeviceId);
                        MacAddress gatewayMac = sinkSensorNode1.sinkMac();
                        reProcess(context, gatewayMac);
                        break;
                    }
                }
                return;
            }


            if (path.links().get(0).src().deviceId().toString().startsWith("of")) {
                if (path.links().get(0).dst().deviceId().toString().startsWith("sdnwise")) {
                    log.info("Should be sending to SDN-WISE");
//                    DeviceId sensorNodeDeviceId = path.links().get(0).dst().deviceId();
//                    SensorNode curSensorNode = sensorNodeService.getSensorNode(sensorNodeDeviceId);
//
//
//                    List<Link> newLinks = new ArrayList<>();
//                    List<Link> existingLinks = path.links();
//                    Link replacedLink = existingLinks.get(0);
//                    Link replacementLink = new DefaultLink()
//                    for (int i = 1; i < existingLinks.size(); i ++) {
//                        newLinks.add(existingLinks.get(i));
//                    }
//
                }
                installRule(context, path.src().port());

            } else {
                log.info("Sending rules to SDN-WISE");
                SensorNode srcNode = sensorNodeService.getSensorNode(srcDeviceId);
                SensorNode dstNode = sensorNodeService.getSensorNode(dstDeviceId);
                SensorTrafficSelector sensorTrafficSelector =
                        (SensorTrafficSelector) SensorEnabledTrafficSelector.builder()
                                .matchNodeSrcAddr(new SensorNodeAddress((byte) srcNode.netId(), srcNode.addr()))
                                .matchNodeDstAddr(new SensorNodeAddress((byte) dstNode.netId(), dstNode.addr()))
                                .build();
                SensorTrafficTreatment sensorTrafficTreatment = SensorEnabledTrafficTreatment.builder()
                        .setOpenPath(path)
                        .buildSensorFlow();

                FlowRule flowRule = new DefaultFlowRule(srcDeviceId, sensorTrafficSelector, sensorTrafficTreatment,
                        PRIORITY, appId, TIMEOUT, false);
                flowRuleService.applyFlowRules(flowRule);

                OutboundPacket outboundPacket = new DefaultOutboundPacket(srcDeviceId, null, pkt.unparsed());
                packetService.emit(outboundPacket);
            }


        }
    }

    private void sdnWiseProcess(InboundPacket pkt) {
        log.info("Received packet from device {}", pkt.receivedFrom().deviceId());

//        MacAddress srcMacAddress = pkt.parsed().getSourceMAC();
        MacAddress dstMacAddress = pkt.parsed().getDestinationMAC();

        log.info("Looking for destination MAC {}", dstMacAddress.toString());

//        SensorNode srcSensorNode = sensorNodeService
//                .getSensorNodesByMac(srcMacAddress).iterator().next();
        SensorNode dstSensorNode = sensorNodeService
                .getSensorNodesByMac(dstMacAddress).iterator().next();

//        DeviceId srcDeviceId = srcSensorNode.deviceId();
        DeviceId srcDeviceId = pkt.receivedFrom().deviceId();
        DeviceId dstDeviceId = dstSensorNode.deviceId();

//        byte[] srcAddr = new byte[3];
//        byte[] dstAddr = new byte[3];
//
//        System.arraycopy(srcMacAddress.toBytes(), 3, srcAddr, 0, 3);
//        System.arraycopy(dstMacAddress.toBytes(), 3, dstAddr, 0, 3);
//
//        String srcDeviceIdAddr = "sdnwise:000000000462f490"; // + srcMacAddress.toBytes()[5];
//        String dstDeviceIdAddr = "sdnwise:000000000462f494"; // + dstMacAddress.toBytes()[5];

//        for (int i = 0; i < 3; i++) {
//            srcDeviceIdAddr = srcDeviceIdAddr + Byte.valueOf(srcAddr[i]).toString();
//            dstDeviceIdAddr = dstDeviceIdAddr + Byte.valueOf(dstAddr[i]).toString();
//        }

//        DeviceId srcDeviceId = DeviceId.deviceId(srcDeviceIdAddr);
//        DeviceId dstDeviceId = DeviceId.deviceId(dstDeviceIdAddr);

        log.info("Looking for paths from Node {} to Node {}", srcDeviceId.toString(), dstDeviceId.toString());

        Set<Path> pathSet = topologyService.getPaths(topologyService.currentTopology(),
                srcDeviceId, dstDeviceId);

        if ((pathSet == null) || (pathSet.isEmpty())) {
            log.info("Empty path set...");
        }

        Path path = pathSet.iterator().next();
        SensorNode srcNode = sensorNodeService.getSensorNode(srcDeviceId);
        SensorNode dstNode = sensorNodeService.getSensorNode(dstDeviceId);
        SensorTrafficSelector sensorTrafficSelector =
                (SensorTrafficSelector) SensorEnabledTrafficSelector.builder()
                    .matchNodeSrcAddr(srcNode.nodeAddress())
                    .matchNodeDstAddr(dstNode.nodeAddress())
                    .build();
        SensorTrafficTreatment sensorTrafficTreatment = SensorEnabledTrafficTreatment.builder()
                .setOpenPath(path)
                .buildSensorFlow();

        FlowRule flowRule = new DefaultFlowRule(srcDeviceId, sensorTrafficSelector, sensorTrafficTreatment,
                PRIORITY, appId, TIMEOUT, false);
        flowRuleService.applyFlowRules(flowRule);

//        OutboundPacket outboundPacket = new DefaultOutboundPacket(srcDeviceId, null, pkt.unparsed());
//        packetService.emit(outboundPacket);
//        pkt.parsed().setDestinationMACAddress(new byte[]{0, 0, 0, 0, 0, 4});
//        DeviceId deviceId = DeviceId.deviceId("sdnwise:0000000000000004");
//        Set<Path> pathServicePaths = topologyService.getPaths(topologyService.currentTopology(),
//                pkt.receivedFrom().deviceId(),
//                deviceId);
//        if ((pathServicePaths != null) && (!pathServicePaths.isEmpty())) {
//            for (Path path : pathServicePaths) {
//
//                List<Link> pathLinks = path.links();
//                log.info("Links {} in path from Node {} to Node {}",
//                        path.toString(), path.src().toString(), path.dst().toString());
//                for (Link link : pathLinks) {
//                    log.info("Got link from Node {} to Node {}",
//                            link.src().deviceId(), link.dst().deviceId());
//                }
//            }
//        } else {
//            log.info("Empty path set from PathService {} -> {}", pkt.receivedFrom().deviceId(),
//                    deviceId);
//        }
//        Path path = pathServicePaths.iterator().next();
    }

    // Indicates whether this is a control packet, e.g. LLDP, BDDP
    private boolean isControlPacket(Ethernet eth) {
        short type = eth.getEtherType();
        return type == Ethernet.TYPE_LLDP || type == Ethernet.TYPE_BSN;
    }

    // Indicated whether this is an IPv6 multicast packet.
    private boolean isIpv6Multicast(Ethernet eth) {
        return eth.getEtherType() == Ethernet.TYPE_IPV6 && eth.isMulticast();
    }

    // Selects a path from the given set that does not lead back to the
    // specified port.
    private Path pickForwardPath(Set<Path> paths, PortNumber notToPort) {
        for (Path path : paths) {
            if (!path.src().port().equals(notToPort)) {
                return path;
            }
        }
        return null;
    }

    // Floods the specified packet if permissible.
    private void flood(PacketContext context) {
        if (topologyService.isBroadcastPoint(topologyService.currentTopology(),
                                             context.inPacket().receivedFrom())) {
            packetOut(context, PortNumber.FLOOD);
        } else {
            context.block();
        }
    }

    // Sends a packet out the specified port.
    private void packetOut(PacketContext context, PortNumber portNumber) {
        context.treatmentBuilder().setOutput(portNumber);
        context.send();
    }

    // Install a rule forwarding the packet to the specified port.
    private void installRule(PacketContext context, PortNumber portNumber) {
        // We don't yet support bufferids in the flowservice so packet out first.
        packetOut(context, portNumber);
        if (!packetOutOnly) {
            // Install the flow rule to handle this type of message from now on.
            Ethernet inPkt = context.inPacket().parsed();
            TrafficSelector.Builder builder = DefaultTrafficSelector.builder();
//            builder.matchEthType(inPkt.getEtherType())
//                    .matchEthSrc(inPkt.getSourceMAC())
//                    .matchEthDst(MacAddress.valueOf("00:00:00:01:00:04"))
//                    .matchInport(context.inPacket().receivedFrom().port());
            builder.matchEthType(inPkt.getEtherType())
                    .matchEthSrc(inPkt.getSourceMAC())
                    .matchEthDst(inPkt.getDestinationMAC())
                    .matchInport(context.inPacket().receivedFrom().port());

            TrafficTreatment.Builder treat = DefaultTrafficTreatment.builder();
            treat.setOutput(portNumber);

            FlowRule f = new DefaultFlowRule(context.inPacket().receivedFrom().deviceId(),
                                             builder.build(), treat.build(), PRIORITY, appId, TIMEOUT, false);

//            log.info("Applying flow rule " + f.toString());
            flowRuleService.applyFlowRules(f);
        }
    }

    private void installRule(PacketContext context, PortNumber portNumber,
                             MacAddress forcedMac, IpAddress hostIP) {
        // We don't yet support bufferids in the flowservice so packet out first.
        packetOut(context, portNumber);
        if (!packetOutOnly) {
            // Install the flow rule to handle this type of message from now on.
            Ethernet inPkt = context.inPacket().parsed();
            TrafficSelector.Builder builder = DefaultTrafficSelector.builder();
//            builder.matchEthType(inPkt.getEtherType())
//                    .matchEthSrc(inPkt.getSourceMAC())
//                    .matchEthDst(MacAddress.valueOf("00:00:00:01:00:04"))
//                    .matchInport(context.inPacket().receivedFrom().port());
            builder.matchEthType(inPkt.getEtherType())
                    .matchEthSrc(inPkt.getSourceMAC())
                    .matchEthDst(inPkt.getDestinationMAC())
                    .matchInport(context.inPacket().receivedFrom().port());

            TrafficTreatment.Builder treat = DefaultTrafficTreatment.builder();
            treat.setOutput(portNumber);
            treat.setIpDst(hostIP);
            treat.setEthDst(forcedMac);

            FlowRule f = new DefaultFlowRule(context.inPacket().receivedFrom().deviceId(),
                    builder.build(), treat.build(), 0, appId, 1000, false);

            log.info("Applying flow rule " + f.toString());
            flowRuleService.applyFlowRules(f);
        }
    }

    private void reProcess(PacketContext context, MacAddress hostMac) {
        log.info("Reprocessing for sending to {}", hostMac);
        InboundPacket pkt = context.inPacket();
        Ethernet ethPkt = pkt.parsed();

        // Bail if this is deemed to be a control or IPv6 multicast packet.
        if (isControlPacket(ethPkt) || isIpv6Multicast(ethPkt)) {
//                log.info("Returning due to control/IPv6 packet");
            return;
        }

        HostId id = HostId.hostId(hostMac);

        // Do not process link-local addresses in any way.
        if (id.mac().isLinkLocal()) {
                log.info("Returning due to link-local packet");
            return;
        }

        // Do we know who this is for? If not, flood and bail.
        Host dst = hostService.getHost(id);
        if (dst == null) {
                log.info("Returning due to no destination packet");
            flood(context);
            return;
        }

        // Are we on an edge switch that our destination is on? If so,
        // simply forward out to the destination and bail.
        if (pkt.receivedFrom().deviceId().equals(dst.location().deviceId())) {
            if (!context.inPacket().receivedFrom().port().equals(dst.location().port())) {
                installRule(context, dst.location().port(),
                        hostMac, dst.ipAddresses().iterator().next());
            }
                log.info("Returning due to edge host port {}", dst.location().port().toString());
            return;
        }

        // Otherwise, get a set of paths that lead from here to the
        // destination edge switch.
        Set<Path> paths = topologyService.getPaths(topologyService.currentTopology(),
                pkt.receivedFrom().deviceId(),
                dst.location().deviceId());
        if (paths.isEmpty()) {
            // If there are no paths, flood and bail.
            log.info("Returning due to no paths");
            flood(context);
            return;
        }

//            Set<Path> pathServicePaths = pathService.getPaths(pkt.receivedFrom().deviceId(),
//                    dst.location().deviceId());
//

//            for (Path path : paths) {
//                log.info("Link {} in path from Node {} to Node {}",
//                        path.toString(), path.src().toString(), path.dst().toString());
//            }

//            if ((pathServicePaths != null) && (!pathServicePaths.isEmpty())) {
//                for (Path path : pathServicePaths) {
//                    log.info("Link {} in path from Node {} to Node {}",
//                            path.toString(), path.src().toString(), path.dst().toString());
//                    List<Link> pathLinks = path.links();
//                    for (Link link : pathLinks) {
//                        log.info("Got link from Node {} to Node {}",
//                                link.src().deviceId(), link.dst().deviceId());
//                    }
//                }
//            } else {
//                log.info("Empty path set from PathService");
//            }

        // Otherwise, pick a path that does not lead back to where we
        // came from; if no such path, flood and bail.
        Path path = pickForwardPath(paths, pkt.receivedFrom().port());
        if (path == null) {
            log.warn("Doh... don't know where to go... {} -> {} received on {}",
                    ethPkt.getSourceMAC(), hostMac,
                    pkt.receivedFrom());
            flood(context);
            return;
        }

            log.info("Installing rule");
        // Otherwise forward and be done with it.
        installRule(context, path.src().port(), hostMac, dst.ipAddresses().iterator().next());
    }

}


