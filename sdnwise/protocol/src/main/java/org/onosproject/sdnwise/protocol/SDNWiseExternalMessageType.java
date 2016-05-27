package org.onosproject.sdnwise.protocol;

import org.onosproject.net.sensorpacket.SensorPacketTypeRegistry;
import org.onosproject.net.sensorpacket.SensorPacketTypeRegistry.SensorPacketType;

/**
 * Created by aca on 9/16/15.
 */
public class SDNWiseExternalMessageType implements SDNWiseMessageType {
    private SensorPacketType sensorPacketType;

    public SDNWiseExternalMessageType(int packetType) {
        this.sensorPacketType = SensorPacketTypeRegistry.getPacketType(packetType, "EXTERNAL");
    }

    public SDNWiseExternalMessageType(int packetType, String packetTypeName) {
        this.sensorPacketType = SensorPacketTypeRegistry.getPacketType(packetType, packetTypeName);
    }

    @Override
    public int getNetworkPacketType() {
        return sensorPacketType.originalId();
    }

    @Override
    public SensorPacketType getSensorPacketType() {
        return sensorPacketType;
    }
}
