package org.onosproject.net.sensor;

import org.onlab.packet.MacAddress;
import org.onosproject.net.DeviceId;
import org.onosproject.net.SensorNode;
import org.onosproject.net.SensorNodeId;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.store.Store;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by aca on 3/14/15.
 */
public interface SensorNodeStore extends Store<SensorNodeEvent, SensorNodeStoreDelegate> {
    int getSensorNodeCount();

    Iterable<SensorNode> getSensorNodes();

    SensorNode getSensorNode(SensorNodeId sensorNodeId);

    SensorNodeEvent createOrUpdateSensorNode(ProviderId providerId, SensorNodeId sensorNodeId,
                                             DeviceId deviceId, SensorNodeDesciption sensorNodeDesciption);

    SensorNodeEvent removeSensorNode(SensorNodeId sensorNodeId);

    Set<SensorNode> getSensorNodes(MacAddress macAddress);

    Set<SensorNode> getSensorNodesInNetwork(int netId);

    SensorNode getSensorNodeByAddress(int netId, byte[] address);

    Map<SensorNodeId, Integer> getSensorNodeNeighbors(SensorNodeId sensorNodeId);

    float getSensorNodeBatteryLevel(SensorNodeId sensorNodeId);

    List<SensorNode> sinks();
}
