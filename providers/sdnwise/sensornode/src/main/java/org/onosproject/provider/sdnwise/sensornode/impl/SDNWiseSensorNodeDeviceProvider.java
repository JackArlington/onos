package org.onosproject.provider.sdnwise.sensornode.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onlab.packet.ChassisId;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.MastershipRole;
import org.onosproject.net.device.DefaultDeviceDescription;
import org.onosproject.net.device.DeviceDescription;
import org.onosproject.net.device.DeviceProvider;
import org.onosproject.net.device.DeviceProviderRegistry;
import org.onosproject.net.device.DeviceProviderService;
import org.onosproject.net.provider.AbstractProvider;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.sdnwise.controller.SDNWiseController;
import org.onosproject.sdnwise.controller.SDNWiseSensorNodeListener;
import org.onosproject.sdnwise.protocol.SDNWiseNodeId;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by aca on 3/15/15.
 */
@Component(immediate = true)
public class SDNWiseSensorNodeDeviceProvider extends AbstractProvider
        implements DeviceProvider {
    private final Logger log = getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceProviderRegistry deviceProviderRegistry;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected SDNWiseController controller;

    private DeviceProviderService deviceProviderService;

    private SDNWiseSensorNodeListener sensorNodeListener =
            new InternalSDNWiseSensorNodeLister();

    /**
     * Creates a provider with the supplier identifier.
     */
    public SDNWiseSensorNodeDeviceProvider() {
        super(new ProviderId("sdnwise", "org.onosproject.provider.sdnwise"));
    }

    @Activate
    public void activate() {
        deviceProviderService = deviceProviderRegistry.register(this);
        controller.addSensorNodeListener(sensorNodeListener);
        log.info("SDN-WISE Sensor Node Provider is now active");
    }

    @Deactivate
    public void deactivate() {
        deviceProviderRegistry.unregister(this);
        controller.removeSensorNodeListener(sensorNodeListener);
        deviceProviderService = null;
        log.info("SDN-WISE Sensor Node Provider is no longer active");
    }

    @Override
    public void triggerProbe(DeviceId deviceId) {

    }

    @Override
    public void roleChanged(DeviceId deviceId, MastershipRole newRole) {

    }

    @Override
    public boolean isReachable(DeviceId deviceId) {
        return true;
    }

    private class InternalSDNWiseSensorNodeLister implements SDNWiseSensorNodeListener {

        @Override
        public void sensorNodeAdded(SDNWiseNodeId nodeId) {
            // At this point register as a device
            DeviceId deviceId = DeviceId.deviceId(nodeId.uri());
            DeviceDescription deviceDescription =
                    new DefaultDeviceDescription(deviceId.uri(), Device.Type.OTHER,
                            "CNIT", "1.0", "1.0", null, new ChassisId(nodeId.value()));
            deviceProviderService.deviceConnected(deviceId, deviceDescription);
        }

        @Override
        public void sensorNodeRemoved(SDNWiseNodeId nodeId) {
            DeviceId deviceId = DeviceId.deviceId(nodeId.uri());
            deviceProviderService.deviceDisconnected(deviceId);
        }
    }
}
