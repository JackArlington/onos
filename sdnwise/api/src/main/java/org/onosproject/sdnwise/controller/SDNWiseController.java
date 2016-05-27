package org.onosproject.sdnwise.controller;

import org.onosproject.net.DeviceId;
import org.onosproject.sdnwise.protocol.SDNWiseMessage;
import org.onosproject.sdnwise.protocol.SDNWiseNode;
import org.onosproject.sdnwise.protocol.SDNWiseNodeId;

/**
 * Created by aca on 2/17/15.
 */
public interface SDNWiseController {
    public Iterable<SDNWiseNode> getNodes();

    public SDNWiseNode getNode(DeviceId id);

    public void addListener(SDNWiseNodeListener listener);

    public void removeListener(SDNWiseNodeListener listener);

    // TODO: Check the priority in OpenFlow implmenetation
    public void addPacketListener(SDNWisePacketListener packetListener);

    public void addEventListener(SDNWiseEventListener eventListener);

    public void removePacketListener(SDNWisePacketListener packetListener);

    public void addSensorNodeListener(SDNWiseSensorNodeListener sensorNodeListener);

    public void removeSensorNodeListener(SDNWiseSensorNodeListener sensorNodeListener);

    public void write(SDNWiseNodeId nodeId, SDNWiseMessage message);

    public void processPacket(SDNWiseNodeId nodeId, SDNWiseMessage message);
}
