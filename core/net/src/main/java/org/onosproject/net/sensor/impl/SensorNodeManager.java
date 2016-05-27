package org.onosproject.net.sensor.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onlab.packet.MacAddress;
import org.onosproject.event.AbstractListenerRegistry;
import org.onosproject.event.EventDeliveryService;
import org.onosproject.net.DeviceId;
import org.onosproject.net.SensorNode;
import org.onosproject.net.SensorNodeId;
import org.onosproject.net.provider.AbstractProviderRegistry;
import org.onosproject.net.provider.AbstractProviderService;
import org.onosproject.net.sensor.SensorNodeDesciption;
import org.onosproject.net.sensor.SensorNodeEvent;
import org.onosproject.net.sensor.SensorNodeListener;
import org.onosproject.net.sensor.SensorNodeProvider;
import org.onosproject.net.sensor.SensorNodeProviderRegistry;
import org.onosproject.net.sensor.SensorNodeProviderService;
import org.onosproject.net.sensor.SensorNodeService;
import org.onosproject.net.sensor.SensorNodeStore;
import org.onosproject.net.sensor.SensorNodeStoreDelegate;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by aca on 3/15/15.
 */
@Component(immediate = true)
@Service
public class SensorNodeManager
        extends AbstractProviderRegistry<SensorNodeProvider, SensorNodeProviderService>
        implements SensorNodeService, SensorNodeProviderRegistry {

    private final Logger log = getLogger(getClass());

    private final AbstractListenerRegistry<SensorNodeEvent, SensorNodeListener>
            listenerRegistry = new AbstractListenerRegistry<>();

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected SensorNodeStore sensorNodeStore;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected EventDeliveryService eventDispatcher;

    @Activate
    public void activate() {
        eventDispatcher.addSink(SensorNodeEvent.class, listenerRegistry);
        log.info("Sensor Node Manager activated");
    }

    @Deactivate
    public void deactivate() {
        eventDispatcher.removeSink(SensorNodeEvent.class);
        log.info("Sensor Node Manager deactivated");
    }


    @Override
    protected SensorNodeProviderService createProviderService(SensorNodeProvider provider) {
        return new InternalSensorNodeProviderService(provider);
    }

    @Override
    public int getSensorNodeCount() {
        return sensorNodeStore.getSensorNodeCount();
    }

    @Override
    public Iterable<SensorNode> getSensorNodes() {
        return sensorNodeStore.getSensorNodes();
    }

    @Override
    public SensorNode getSensorNode(SensorNodeId sensorNodeId) {
        return sensorNodeStore.getSensorNode(sensorNodeId);
    }

    @Override
    public SensorNode getSensorNode(DeviceId deviceId) {
        Iterable<SensorNode> sensorNodes = getSensorNodes();
        for (SensorNode sensorNode : sensorNodes) {
            if (sensorNode.deviceId().equals(deviceId)) {
                return sensorNode;
            }
        }
        return null;
    }

    @Override
    public Set<SensorNode> getSensorNodesInNetwork(int netId) {
        return sensorNodeStore.getSensorNodesInNetwork(netId);
    }

    @Override
    public Set<SensorNode> getSensorNodesByMac(MacAddress macAddress) {
        return sensorNodeStore.getSensorNodes(macAddress);
    }

    @Override
    public SensorNode getSensorNodeByAddress(int netId, byte[] addr) {
        return sensorNodeStore.getSensorNodeByAddress(netId, addr);
    }

    @Override
    public List<SensorNode> getSinks() {
        return sensorNodeStore.sinks();
    }

    @Override
    public Float getSensorNodeBatteryLevel(SensorNodeId sensorNodeId) {
        return sensorNodeStore.getSensorNodeBatteryLevel(sensorNodeId);
    }

    @Override
    public void addListener(SensorNodeListener listener) {
        listenerRegistry.addListener(listener);
    }

    @Override
    public void removeListner(SensorNodeListener listener) {
        listenerRegistry.removeListener(listener);
    }

    @Override
    public Map<SensorNodeId, Integer> getSensorNodeNeighbors(SensorNodeId sensorNodeId) {
        return sensorNodeStore.getSensorNodeNeighbors(sensorNodeId);
    }

    private class InternalSensorNodeProviderService
            extends AbstractProviderService<SensorNodeProvider>
            implements SensorNodeProviderService {

        /**
         * Creates a provider service on behalf of the specified provider.
         *
         * @param provider provider to which this service is being issued
         */
        protected InternalSensorNodeProviderService(SensorNodeProvider provider) {
            super(provider);
        }

        @Override
        public void sensorNodeDetected(SensorNodeId sensorNodeId, DeviceId deviceId,
                                       SensorNodeDesciption sensorNodeDesciption) {
            SensorNodeEvent event = sensorNodeStore.createOrUpdateSensorNode(provider().id(),
                    sensorNodeId, deviceId, sensorNodeDesciption);
            if (event != null) {
                eventDispatcher.post(event);
            }
        }

        @Override
        public void sensorNodeVanished(SensorNodeId sensorNodeId) {
            SensorNodeEvent event = sensorNodeStore.removeSensorNode(sensorNodeId);
            if (event != null) {
                eventDispatcher.post(event);
            }
        }
    }

    private class InternalSensorNodeStoreDelegate implements SensorNodeStoreDelegate {

        @Override
        public void notify(SensorNodeEvent event) {
            if (event != null) {
                eventDispatcher.post(event);
            }
        }
    }
}
