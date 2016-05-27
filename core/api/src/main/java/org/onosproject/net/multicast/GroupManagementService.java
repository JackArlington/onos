package org.onosproject.net.multicast;

import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onosproject.net.Host;
import org.onosproject.net.SensorNode;

import java.net.URI;
import java.util.List;

/**
 * Created by aca on 4/29/15.
 */
public interface GroupManagementService {
    List<Group> groups();

    List<Host> hosts(URI groupId);

    List<SensorNode> sensorNodes(URI groupId);

    List<Host> hosts(IpAddress groupIp);

    List<SensorNode> sensorNodes(IpAddress groupIp);

    List<Host> hosts(MacAddress groupMac);

    List<SensorNode> sensorNodes(MacAddress groupMac);
}
