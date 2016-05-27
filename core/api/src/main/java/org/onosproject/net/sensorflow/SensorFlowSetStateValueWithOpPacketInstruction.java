package org.onosproject.net.sensorflow;

import com.google.common.base.MoreObjects;
import org.onosproject.net.flow.instructions.Instruction;

/**
 * Created by aca on 9/17/15.
 */
public class SensorFlowSetStateValueWithOpPacketInstruction implements SensorFlowInstruction {
    private Operator operator;
    private int stateOperandPos;
    private int stateResultPos;
    private int packetPos;
    private int offset;

    public SensorFlowSetStateValueWithOpPacketInstruction(int packetPos, int stateOperandPos, int stateResultPos,
                                                          int offset, Operator operator) {
        this.operator = operator;
        this.packetPos = packetPos;
        this.stateOperandPos = stateOperandPos;
        this.stateResultPos = stateResultPos;
        this.offset = offset;
    }

    public int stateOperandPos() {
        return stateOperandPos;
    }

    public int stateResultPos() {
        return stateResultPos;
    }

    public int packetPos() {
        return packetPos;
    }

    public int offset() {
        return offset;
    }

    @Override
    public Type getSensorFlowInstructionType() {
        return Type.STATE_PACKET_OP;
    }

    @Override
    public boolean multimatch() {
        return false;
    }

    @Override
    public Operator getOperator() {
        return operator;
    }

    @Override
    public Instruction.Type type() {
        return null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(SensorFlowSetStateValueWithOpPacketInstruction.class)
                .add("operator", operator.name())
                .add("stateOperandPos", stateOperandPos)
                .add("stateResultPos", stateResultPos)
                .add("packetPos", packetPos)
                .add("offset", offset)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SensorFlowSetStateValueWithOpPacketInstruction)) {
            return false;
        }

        SensorFlowSetStateValueWithOpPacketInstruction that = (SensorFlowSetStateValueWithOpPacketInstruction) o;

        if (stateOperandPos != that.stateOperandPos) {
            return false;
        }
        if (stateResultPos != that.stateResultPos) {
            return false;
        }
        if (packetPos != that.packetPos) {
            return false;
        }
        if (offset != that.offset) {
            return false;
        }
        return operator == that.operator;

    }

    @Override
    public int hashCode() {
        int result = operator.hashCode();
        result = 31 * result + stateOperandPos;
        result = 31 * result + stateResultPos;
        result = 31 * result + packetPos;
        result = 31 * result + offset;
        return result;
    }
}
