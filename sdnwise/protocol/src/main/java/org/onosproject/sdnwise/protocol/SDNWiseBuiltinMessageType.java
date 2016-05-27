package org.onosproject.sdnwise.protocol;

import org.onosproject.net.sensorpacket.SensorPacketTypeRegistry.SensorPacketType;

import static org.onosproject.net.sensorpacket.SensorPacketTypeRegistry.getPacketType;

/**
 * Created by aca on 9/15/15.
 */
public enum SDNWiseBuiltinMessageType implements SDNWiseMessageType {
    DATA(getPacketType(com.github.sdnwiselab.sdnwise.packet.NetworkPacket.DATA, "DATA")),
    BEACON(getPacketType(com.github.sdnwiselab.sdnwise.packet.NetworkPacket.BEACON, "BEACON")),
    REPORT(getPacketType(com.github.sdnwiselab.sdnwise.packet.NetworkPacket.REPORT, "REPORT")),
    REQUEST(getPacketType(com.github.sdnwiselab.sdnwise.packet.NetworkPacket.REQUEST, "REQUEST")),
    RESPONSE(getPacketType(com.github.sdnwiselab.sdnwise.packet.NetworkPacket.RESPONSE, "RESPONSE")),
    OPEN_PATH(getPacketType(com.github.sdnwiselab.sdnwise.packet.NetworkPacket.OPEN_PATH, "OPEN_PATH")),
    CONFIG(getPacketType(com.github.sdnwiselab.sdnwise.packet.NetworkPacket.CONFIG, "CONFIG")),
    REG_PROXY(getPacketType(com.github.sdnwiselab.sdnwise.packet.NetworkPacket.REG_PROXY, "REG_PROXY"));

    SensorPacketType sensorPacketType;

    SDNWiseBuiltinMessageType(SensorPacketType data) {
        this.sensorPacketType = data;
    }

    public static SDNWiseMessageType getType(int networkPacketType) {
        for (SDNWiseBuiltinMessageType messageType : values()) {
            if (messageType.sensorPacketType.originalId() == networkPacketType) {
                return messageType;
            }
        }

        return new SDNWiseExternalMessageType(networkPacketType);
    }

    public int getNetworkPacketType() {
        return this.sensorPacketType.originalId();
    }

    public SensorPacketType getSensorPacketType() {
        return this.sensorPacketType;
    }
}
