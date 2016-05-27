package org.onosproject.net.sensorflow;

import com.google.common.base.MoreObjects;
import org.onosproject.net.flow.instructions.Instruction;

/**
 * Created by aca on 9/24/15.
 */
public class SensorFlowSetPacketValueConstInstruction implements SensorFlowInstruction {
    private int packetPos;
    private int val;

    public SensorFlowSetPacketValueConstInstruction(int packetPos, int val) {
        this.packetPos = packetPos;
        this.val = val;
    }

    public int packetPosition() {
        return packetPos;
    }

    public int value() {
        return val;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(SensorFlowSetPacketValueConstInstruction.class)
                .add("position", packetPos)
                .add("value", val)
                .toString();
    }

    @Override
    public Type getSensorFlowInstructionType() {
        return Type.SET_PKT_VAL_CONST;
    }

    @Override
    public boolean multimatch() {
        return false;
    }

    @Override
    public Instruction.Type type() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SensorFlowSetPacketValueConstInstruction)) {
            return false;
        }

        SensorFlowSetPacketValueConstInstruction that = (SensorFlowSetPacketValueConstInstruction) o;

        if (packetPos != that.packetPos) {
            return false;
        }
        return val == that.val;

    }

    @Override
    public int hashCode() {
        int result = packetPos;
        result = 31 * result + val;
        return result;
    }
}
