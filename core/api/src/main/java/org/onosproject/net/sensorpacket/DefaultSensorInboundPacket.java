package org.onosproject.net.sensorpacket;

import org.onlab.packet.Ethernet;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.packet.DefaultInboundPacket;
import org.onosproject.net.sensorpacket.SensorPacketTypeRegistry.SensorPacketType;

import java.nio.ByteBuffer;

/**
 * Created by aca on 9/16/15.
 */
public final class DefaultSensorInboundPacket implements SensorInboundPacket {
    private DefaultInboundPacket defaultInboundPacket;
    private SensorPacketType sensorPacketType;

    public DefaultSensorInboundPacket(SensorPacketType sensorPacketType,
                                      ConnectPoint receivedFrom, Ethernet parsed, ByteBuffer unparsed) {
        this.sensorPacketType = sensorPacketType;
        this.defaultInboundPacket = new DefaultInboundPacket(receivedFrom, parsed, unparsed);
    }

    @Override
    public SensorPacketType sensorPacketType() {
        return sensorPacketType;
    }

    @Override
    public ConnectPoint receivedFrom() {
        return defaultInboundPacket.receivedFrom();
    }

    @Override
    public Ethernet parsed() {
        return defaultInboundPacket.parsed();
    }

    @Override
    public ByteBuffer unparsed() {
        return defaultInboundPacket.unparsed();
    }
}
