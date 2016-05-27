package org.onosproject.net.sensorflow;

import com.google.common.base.MoreObjects;
import org.onosproject.net.flow.instructions.Instruction;

/**
 * Created by aca on 9/28/15.
 */
public class SensorFlowSetPacketLengthInstruction implements SensorFlowInstruction {
    private int packetLength;

    public SensorFlowSetPacketLengthInstruction(int packetLength) {
        this.packetLength = packetLength;
    }

    public int length() {
        return packetLength;
    }

    @Override
    public Type getSensorFlowInstructionType() {
        return Type.SET_PKT_LEN;
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
    public String toString() {
        return MoreObjects.toStringHelper(SensorFlowSetPacketLengthInstruction.class)
                .add("length", packetLength)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SensorFlowSetPacketLengthInstruction)) {
            return false;
        }

        SensorFlowSetPacketLengthInstruction that = (SensorFlowSetPacketLengthInstruction) o;

        return packetLength == that.packetLength;

    }

    @Override
    public int hashCode() {
        return packetLength;
    }
}
