package org.onosproject.net.sensorflow;

import org.onosproject.net.flow.instructions.Instruction;
import org.onosproject.net.sensorpacket.SensorPacketTypeRegistry.SensorPacketType;

/**
 * Created by aca on 7/21/15.
 */
public class SensorFlowPacketTypeInstruction implements SensorFlowInstruction {
    private boolean multimatch = false;
    private SensorPacketType packetType;

    public SensorFlowPacketTypeInstruction(SensorPacketType packetType) {
        this.packetType = packetType;
    }

    public SensorFlowPacketTypeInstruction(SensorPacketType packetType, boolean multimatch) {
        this.packetType = packetType;
        this.multimatch = multimatch;
    }

    public SensorPacketType packetType() {
        return packetType;
    }

    @Override
    public Type getSensorFlowInstructionType() {
        return Type.SET_PACKET_TYPE;
    }

    @Override
    public boolean multimatch() {
        return multimatch;
    }

    @Override
    public Instruction.Type type() {
        return null;
    }
}
