package org.onosproject.store.trivial.impl;

import com.google.common.collect.ImmutableSet;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.onlab.packet.MacAddress;
import org.onosproject.net.DefaultSensorNode;
import org.onosproject.net.DeviceId;
import org.onosproject.net.SensorNode;
import org.onosproject.net.SensorNodeId;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.net.sensor.SensorNodeDesciption;
import org.onosproject.net.sensor.SensorNodeEvent;
import org.onosproject.net.sensor.SensorNodeNeighborhood;
import org.onosproject.net.sensor.SensorNodeStore;
import org.onosproject.net.sensor.SensorNodeStoreDelegate;
import org.onosproject.store.AbstractStore;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.currentTimeMillis;
import static org.onosproject.net.sensor.SensorNodeEvent.Type.SENSOR_ADDED;
import static org.onosproject.net.sensor.SensorNodeEvent.Type.SENSOR_REMOVED;
import static org.onosproject.net.sensor.SensorNodeEvent.Type.SENSOR_UPDATED;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by aca on 3/15/15.
 */
@Component(immediate = true)
@Service
public class SimpleSensorNodeStore
        extends AbstractStore<SensorNodeEvent, SensorNodeStoreDelegate>
        implements SensorNodeStore {

    private final Logger log = getLogger(getClass());

    private final Map<SensorNodeId, SensorNode> sensorNodes = new ConcurrentHashMap<>(200000, 0.75f, 16);

    private final Map<SensorNodeId, SensorNodeNeighborhood> sensorNodeNeighborhood =
            new ConcurrentHashMap<>(200000, 0.75f, 16);

    private final Map<SensorNodeId, Float> sensorNodeBatteryLevel =
            new ConcurrentHashMap<>(200000, 0.75f, 16);

    private final List<SensorNode> sinks = new ArrayList<>();

    @Activate
    public void activate() {
        log.info("Activating Sensor Node Store");
    }

    @Deactivate
    public void deactivate() {
        log.info("Deactivating Sensor Node Store");
    }

    @Override
    public int getSensorNodeCount() {
        return this.sensorNodes.size();
    }

    @Override
    public Iterable<SensorNode> getSensorNodes() {
        return ImmutableSet.copyOf(this.sensorNodes.values());
    }

    @Override
    public SensorNode getSensorNode(SensorNodeId sensorNodeId) {
        return this.sensorNodes.get(sensorNodeId);
    }

    @Override
    public SensorNodeEvent createOrUpdateSensorNode(ProviderId providerId, SensorNodeId sensorNodeId,
                                                    DeviceId deviceId, SensorNodeDesciption sensorNodeDesciption) {
        synchronized (this) {

            SensorNode sensorNode = this.sensorNodes.get(sensorNodeId);
            SensorNodeEvent event = null;
            MacAddress sinkMac = sensorNodeDesciption.sinkHwAddress();

            SensorNodeEvent.Type eventType = SENSOR_ADDED;
            if (sensorNode != null) {
                eventType = SENSOR_UPDATED;
            }

//            if (sensorNode == null) {
//                sinkMac = sensorNodeDesciption.sinkHwAddress();
//            } else {
//                if (sensorNodeDesciption.hwAddress().equals(sensorNodeDesciption.sinkHwAddress())) {
//                    sinkMac = sensorNode.sinkMac();
//                } else {
//                    sinkMac = sensorNodeDesciption.sinkHwAddress();
//                }
//
//            }

            boolean failure = true;
            if (sinkMac != null) {
                DeviceId sensorNodeLocation = sensorNodeDesciption.sinkLocation();
                if (sensorNodeLocation == null) {
                    sensorNodeLocation = sensorNode.sinkLocation();
                }
                // The node is sink
                sensorNode = new DefaultSensorNode(providerId, sensorNodeId, deviceId,
                        sensorNodeDesciption.hwAddress(), sinkMac, sensorNodeDesciption.sinkIpAddress(),
                        sensorNodeDesciption.sinkPort(), sensorNodeDesciption.sinkConnectionIpAddress(),
                        sensorNodeDesciption.sinkConnectionPort(), null, sensorNodeLocation,
                        sensorNodeDesciption.netId(),
                        sensorNodeDesciption.address(), sensorNodeDesciption.annotations());
//                log.info("Node {} is sink", sensorNode.deviceId());
                failure = false;
            } else {
                // Find the sink and associate it with the node
                for (SensorNode sinkNode : sinks) {
                    if ((sinkNode.sinkConnectionAddress().equals(sensorNodeDesciption.sinkConnectionIpAddress())) &&
                            (sinkNode.sinkConnectionPort().equals(sensorNodeDesciption.sinkConnectionPort()))) {

                        if (sinkNode.deviceId().equals(deviceId)) {
                            sinkMac = sinkNode.sinkMac();
                        }

                        sensorNode = new DefaultSensorNode(providerId, sensorNodeId, deviceId,
                                sensorNodeDesciption.hwAddress(), sinkMac, sinkNode.sinkAddress(),
                                sinkNode.sinkPort(), sinkNode.sinkConnectionAddress(),
                                sinkNode.sinkConnectionPort(), sinkNode, sinkNode.sinkLocation(),
                                sensorNodeDesciption.netId(),
                                sensorNodeDesciption.address(), sensorNodeDesciption.annotations());

//                        log.info("Associating node {} with sink {}", sensorNode.deviceId(), sinkNode.deviceId());
                        failure = false;
                        break;
                    }
                }
            }



            if (!failure) {
                event = new SensorNodeEvent(eventType, sensorNode, currentTimeMillis());
                this.sensorNodes.put(sensorNodeId, sensorNode);
                this.sensorNodeNeighborhood.put(sensorNodeId, sensorNodeDesciption.neighbors());
                this.sensorNodeBatteryLevel.put(sensorNodeId, sensorNodeDesciption.batteryLevel());
                if (sinkMac != null) {
                    this.sinks.add(sensorNode);
                }
            }

            return event;
        }

    }

    @Override
    public SensorNodeEvent removeSensorNode(SensorNodeId sensorNodeId) {
        synchronized (this) {
            SensorNode sensorNode = this.sensorNodes.remove(sensorNodeId);
            if (sensorNode != null) {
                this.sensorNodeNeighborhood.remove(sensorNodeId);
                this.sensorNodeBatteryLevel.remove(sensorNodeId);
                return new SensorNodeEvent(SENSOR_REMOVED, sensorNode, currentTimeMillis());
            }
        }
        return null;
    }

    @Override
    public Set<SensorNode> getSensorNodes(MacAddress macAddress) {
        Set<SensorNode> sensorNodeSet = new HashSet<>();
        for (SensorNode sensorNode : sensorNodes.values()) {
            if (sensorNode.mac().equals(macAddress)) {
                sensorNodeSet.add(sensorNode);
            }
        }
        return sensorNodeSet;
    }

    @Override
    public Set<SensorNode> getSensorNodesInNetwork(int netId) {
        Set<SensorNode> sensorNodeSet = new HashSet<>();
        for (SensorNode sensorNode : sensorNodes.values()) {
            if (sensorNode.netId() == netId) {
                sensorNodeSet.add(sensorNode);
            }
        }
        return sensorNodeSet;
    }

    @Override
    public SensorNode getSensorNodeByAddress(int netId, byte[] address) {
        for (SensorNode sensorNode : sensorNodes.values()) {
            if ((sensorNode.netId() == netId) &&
                    (Arrays.equals(sensorNode.addr(), address))) {
                return sensorNode;
            }
        }
        return null;
    }

    @Override
    public Map<SensorNodeId, Integer> getSensorNodeNeighbors(SensorNodeId sensorNodeId) {
        SensorNodeNeighborhood neighborhood = this.sensorNodeNeighborhood.get(sensorNodeId);
        if (neighborhood != null) {
            return neighborhood.getNeighborhood();
        }
        return null;
    }

    @Override
    public float getSensorNodeBatteryLevel(SensorNodeId sensorNodeId) {
        return this.sensorNodeBatteryLevel.get(sensorNodeId);
    }

    @Override
    public List<SensorNode> sinks() {
        return sinks;
    }
}
