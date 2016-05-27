/*
 * Copyright 2015 CNIT
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
package org.onosproject.provider.sdnwise.device.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onlab.packet.ChassisId;
import org.onosproject.net.DefaultAnnotations;
import org.onosproject.net.DeviceId;
import org.onosproject.net.MastershipRole;
import org.onosproject.net.SensorDevice;
import org.onosproject.net.SparseAnnotations;
import org.onosproject.net.device.DeviceProvider;
import org.onosproject.net.device.DeviceProviderRegistry;
import org.onosproject.net.device.DeviceProviderService;
import org.onosproject.net.provider.AbstractProvider;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.sdnwise.controller.SDNWiseController;
import org.onosproject.sdnwise.controller.SDNWiseNodeListener;
import org.onosproject.sdnwise.protocol.SDNWiseNode;
import org.onosproject.sdnwise.protocol.SDNWiseNodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.onosproject.net.DeviceId.deviceId;

/**
 * Created by aca on 2/13/15.
 */
@Deprecated
@Component(immediate = true)
public class SDNWiseDeviceProvider extends AbstractProvider implements DeviceProvider {
    private static final Logger LOG = LoggerFactory.getLogger(SDNWiseDeviceProvider.class);

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceProviderRegistry providerRegistry;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected SDNWiseController controller;

    private DeviceProviderService providerService;

    private final SDNWiseNodeListener listener = new InternalDeviceProvider();

    public SDNWiseDeviceProvider() {
        super(new ProviderId("sdnwise", "org.onosproject.provider.sdnwise"));
        LOG.info("Initializing SDN WISE Device Provider");
    }

    @Activate
    public void activate() {
        providerService = providerRegistry.register(this);
        controller.addListener(listener);
        for (SDNWiseNode node : controller.getNodes()) {
            listener.sensorNodeAdded(node.getId());
        }
        LOG.info("Started");
    }

    @Deactivate
    public void deactivate() {
        providerRegistry.unregister(this);
        controller.removeListener(listener);
        providerService = null;

        LOG.info("Stopped");
    }

    @Override
    public void triggerProbe(DeviceId deviceId) {

    }

    @Override
    public void roleChanged(DeviceId deviceId, MastershipRole newRole) {

    }

    @Override
    public boolean isReachable(DeviceId deviceId) {
        // TODO: Check whether the node is really reachable
        return true;
    }

    private class InternalDeviceProvider implements SDNWiseNodeListener {

        @Override
        public void sensorNodeAdded(SDNWiseNodeId nodeId) {
            if (providerService == null) {
                return;
            }
            DeviceId did = deviceId(nodeId.uri());
            SDNWiseNode node = controller.getNode(did);

            SensorDevice.Type deviceType = SensorDevice.Type.IEEE802_15_4;
            ChassisId cId = new ChassisId(nodeId.toString());
            SparseAnnotations annotations = DefaultAnnotations.builder()
                    .set("protocol", node.getVersion().toString()).build();
//            DeviceDescription description =
//                    new DefaultSensorNodeDeviceDescription(nodeId.uri(), deviceType,
//                            node.manufacturerDescription(),
//                            node.hardwareDescription(),
//                            node.softwareDescription(),
//                            node.serialNumber(),
//                            cId, annotations);
//            providerService.deviceConnected(deviceId(nodeId.uri()), description);
//            providerService.updatePorts(did, buildPortDescriptions(sw.getPorts()));
        }

        @Override
        public void sensorNodeRemoved(SDNWiseNodeId nodeId) {
            if (providerService == null) {
                return;
            }
            providerService.deviceDisconnected(deviceId(nodeId.uri()));
        }
    }
}
