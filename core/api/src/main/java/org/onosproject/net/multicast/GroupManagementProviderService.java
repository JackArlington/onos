package org.onosproject.net.multicast;

import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onosproject.net.DeviceId;
import org.onosproject.net.HostId;
import org.onosproject.net.SensorNodeId;
import org.onosproject.net.provider.ProviderService;
import org.onosproject.net.sensor.SensorNodeAddress;

/**
 * Created by aca on 4/29/15.
 */
public interface GroupManagementProviderService
        extends ProviderService<GroupManagementProvider> {
    void hostAdded(HostId hostId, IpAddress multicastAddress);

    void hostRemoved(HostId hostId, IpAddress multicastAddress);

    void sensorNodeAdded(SensorNodeId sensorNodeId, SensorNodeAddress multicastAddress);

    void sensorNodeRemoved(SensorNodeId sensorNodeId, SensorNodeAddress multicastAddress);

    void deviceAdded(DeviceId deviceId, MacAddress multicastAddress);

    void deviceRemoved(DeviceId deviceId, MacAddress multicastAddress);
}
