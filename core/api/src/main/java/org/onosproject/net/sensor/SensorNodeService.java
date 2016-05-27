package org.onosproject.net.sensor;

import org.onlab.packet.MacAddress;
import org.onosproject.net.DeviceId;
import org.onosproject.net.SensorNode;
import org.onosproject.net.SensorNodeId;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by aca on 3/15/15.
 */
public interface SensorNodeService {
    int getSensorNodeCount();

    Iterable<SensorNode> getSensorNodes();

    SensorNode getSensorNode(SensorNodeId sensorNodeId);

    SensorNode getSensorNode(DeviceId deviceId);

    Set<SensorNode> getSensorNodesInNetwork(int netId);

    Set<SensorNode> getSensorNodesByMac(MacAddress macAddress);

    SensorNode getSensorNodeByAddress(int netId, byte[] addr);

    List<SensorNode> getSinks();

    Float getSensorNodeBatteryLevel(SensorNodeId sensorNodeId);

    void addListener(SensorNodeListener listener);

    void removeListner(SensorNodeListener listener);

    Map<SensorNodeId, Integer> getSensorNodeNeighbors(SensorNodeId sensorNodeId);
}
