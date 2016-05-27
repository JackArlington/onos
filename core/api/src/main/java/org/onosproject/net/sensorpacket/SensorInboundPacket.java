package org.onosproject.net.sensorpacket;

import org.onosproject.net.packet.InboundPacket;
import org.onosproject.net.sensorpacket.SensorPacketTypeRegistry.SensorPacketType;

/**
 * Created by aca on 9/16/15.
 */
public interface SensorInboundPacket extends InboundPacket {
    SensorPacketType sensorPacketType();
}
