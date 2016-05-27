package org.onosproject.sdnwise.protocol;

import org.onosproject.net.sensorpacket.SensorPacketTypeRegistry.SensorPacketType;

/**
 * Created by aca on 9/15/15.
 */
public interface SDNWiseMessageType {
    int getNetworkPacketType();

    SensorPacketType getSensorPacketType();
}
