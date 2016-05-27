package org.onosproject.sdnwise.controller.impl;

import com.google.common.collect.Sets;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.net.DeviceId;
import org.onosproject.sdnwise.controller.SDNWiseController;
import org.onosproject.sdnwise.controller.SDNWiseEventListener;
import org.onosproject.sdnwise.controller.SDNWiseNodeListener;
import org.onosproject.sdnwise.controller.SDNWisePacketListener;
import org.onosproject.sdnwise.controller.SDNWiseSensorNodeListener;
import org.onosproject.sdnwise.controller.driver.SDNWiseAgent;
import org.onosproject.sdnwise.protocol.SDNWiseMessage;
import org.onosproject.sdnwise.protocol.SDNWiseNode;
import org.onosproject.sdnwise.protocol.SDNWiseNodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.onlab.util.Tools.namedThreads;

/**
 * Created by aca on 2/18/15.
 */
@Component(immediate = true)
@Service
public class SDNWiseControllerImpl implements SDNWiseController {
    private static final Logger log = LoggerFactory.getLogger(SDNWiseControllerImpl.class);

    private final ExecutorService executorMsgs =
            Executors.newFixedThreadPool(32,
                    namedThreads("sdnwise-event-stats-%d"));

    private final ExecutorService executorBarrier =
            Executors.newFixedThreadPool(4,
                    namedThreads("sdnwise-event-barrier-%d"));

    protected ConcurrentHashMap<DeviceId, SDNWiseNode> connectedNodes =
            new ConcurrentHashMap<>();

    protected SDNWiseAgent agent = new SDNWiseNodeAgent();
    protected Set<SDNWiseNodeListener> sdnWiseNodeListeners =
            new HashSet<>();

    protected Set<SDNWisePacketListener> sdnWisePacketListeners = Sets.newHashSet();

    protected Set<SDNWiseEventListener> sdnWiseEventListeners = Sets.newHashSet();

    protected Set<SDNWiseSensorNodeListener> sdnWiseSensorNodeListeners = Sets.newHashSet();

    private final Controller ctrl = new Controller();

    @Override
    public Iterable<SDNWiseNode> getNodes() {
        return connectedNodes.values();
    }

    @Override
    public SDNWiseNode getNode(DeviceId id) {
        return connectedNodes.get(id);
    }

    @Override
    public void addListener(SDNWiseNodeListener listener) {
        if (!this.sdnWiseNodeListeners.contains(listener)) {
            this.sdnWiseNodeListeners.add(listener);
        }
    }

    @Override
    public void removeListener(SDNWiseNodeListener listener) {
        this.sdnWiseNodeListeners.remove(listener);
    }

    @Override
    public void addPacketListener(SDNWisePacketListener packetListener) {
        this.sdnWisePacketListeners.add(packetListener);
    }

    @Override
    public void addEventListener(SDNWiseEventListener eventListener) {
        if (!this.sdnWiseEventListeners.contains(eventListener)) {
            this.sdnWiseEventListeners.add(eventListener);
        }
    }

    @Override
    public void removePacketListener(SDNWisePacketListener packetListener) {
        this.sdnWisePacketListeners.remove(packetListener);
    }

    @Override
    public void addSensorNodeListener(SDNWiseSensorNodeListener sensorNodeListener) {
        if (!this.sdnWiseSensorNodeListeners.contains(sensorNodeListener)) {
            this.sdnWiseSensorNodeListeners.add(sensorNodeListener);
        }
    }

    @Override
    public void removeSensorNodeListener(SDNWiseSensorNodeListener sensorNodeListener) {
        this.sdnWiseSensorNodeListeners.remove(sensorNodeListener);
    }

    @Override
    public void write(SDNWiseNodeId nodeId, SDNWiseMessage message) {
        this.getNode(DeviceId.deviceId(nodeId.uri())).sendMsg(message);
    }

    @Override
    public void processPacket(SDNWiseNodeId nodeId, SDNWiseMessage message) {
        for (SDNWisePacketListener sdnWisePacketListener : sdnWisePacketListeners) {
            sdnWisePacketListener.handlePacket(message);
        }
    }

    @Activate
    public void activate() {
        ctrl.start(agent);
        log.info("Started SDNWiseController");
    }

    @Deactivate
    public void deactivate() {
        ctrl.stop();
    }

    public class SDNWiseNodeAgent implements SDNWiseAgent {
        private final Logger log = LoggerFactory.getLogger(SDNWiseNodeAgent.class);

        private SDNWiseNodeId id;

        @Override
        public boolean addConnectedNode(SDNWiseNodeId id, SDNWiseNode node) {
            if (connectedNodes.get(DeviceId.deviceId(id.uri())) != null) {
//                log.error("Tried to connect node with id " + id.toString() + " but found already existing one.");
                return false;
            }
            this.id = id;
//            log.info("Added node " + id.toString());
            connectedNodes.put(DeviceId.deviceId(id.uri()), node);
//            for (SDNWiseNodeListener sdnWiseNodeListener : sdnWiseNodeListeners) {
//                sdnWiseNodeListener.sensorNodeAdded(id);
//            }

            for (SDNWiseSensorNodeListener sdnWiseSensorNodeListener : sdnWiseSensorNodeListeners) {
                sdnWiseSensorNodeListener.sensorNodeAdded(id);
            }
            return true;
        }

        @Override
        public void removeConnectedNode(SDNWiseNodeId id) {
            if (connectedNodes.get(DeviceId.deviceId(id.uri())) == null) {
                log.error("No node found with id " + id.toString());
            } else {
                SDNWiseNode node = connectedNodes.remove(DeviceId.deviceId(id.uri()));
                if (node == null) {
                    log.warn("The node object for id " + id.toString() + " was null");
                }
//                for (SDNWiseNodeListener sdnWiseNodeListener : sdnWiseNodeListeners) {
//                    sdnWiseNodeListener.sensorNodeRemoved(id);
//                }
                for (SDNWiseSensorNodeListener sdnWiseSensorNodeListener : sdnWiseSensorNodeListeners) {
                    sdnWiseSensorNodeListener.sensorNodeRemoved(id);
                }
            }
        }

        @Override
        public void processMessage(SDNWiseMessage message) {
            processPacket(id, message);
        }

//        @Override
//        public void processMessage(SDNWiseMessage message) {
//
//        }
    }

    private final class SDNWiseMessageHandler implements Runnable {

        private final SDNWiseMessage message;
        private final SDNWiseNodeId id;

        public SDNWiseMessageHandler(SDNWiseNodeId id, SDNWiseMessage message) {
            this.message = message;
            this.id = id;
        }

        @Override
        public void run() {
            for (SDNWiseEventListener sdnWiseEventListener : sdnWiseEventListeners) {
                sdnWiseEventListener.handleMessage(id, message);
            }
        }
    }
}
