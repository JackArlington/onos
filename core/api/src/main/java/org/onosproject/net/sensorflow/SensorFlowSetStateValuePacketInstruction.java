package org.onosproject.net.sensorflow;

import com.google.common.base.MoreObjects;
import org.onosproject.net.flow.instructions.Instruction;

import static com.google.common.base.MoreObjects.toStringHelper;
import static org.onosproject.net.sensorflow.SensorFlowInstruction.Type.SET_STATE_VALUE_PACKET;

/**
 * Created by aca on 9/15/15.
 */
public class SensorFlowSetStateValuePacketInstruction implements SensorFlowInstruction {
    private boolean isMultimatch = false;
    private int beginPos;
    private int offset;
    private int packetPos;

    public SensorFlowSetStateValuePacketInstruction(int beginPos, int offset, int packetPos, boolean isMultimatch) {
        this.isMultimatch = isMultimatch;
        this.beginPos = beginPos;
        this.offset = offset;
        this.packetPos = packetPos;
    }

    public int beginPosition() {
        return beginPos;
    }

    public int offset() {
        return offset;
    }

    public int packetPosition() {
        return packetPos;
    }

    @Override
    public Type getSensorFlowInstructionType() {
        return SET_STATE_VALUE_PACKET;
    }

    @Override
    public boolean multimatch() {
        return isMultimatch;
    }

    @Override
    public Instruction.Type type() {
        return null;
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper stringHelper =
                toStringHelper(getSensorFlowInstructionType().toString());

        stringHelper.add("beginPosition", beginPos)
                .add("offset", offset)
                .add("packetPosition", packetPos);
        return stringHelper.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SensorFlowSetStateValuePacketInstruction)) {
            return false;
        }

        SensorFlowSetStateValuePacketInstruction that = (SensorFlowSetStateValuePacketInstruction) o;

        if (isMultimatch != that.isMultimatch) {
            return false;
        }
        if (beginPos != that.beginPos) {
            return false;
        }
        if (offset != that.offset) {
            return false;
        }
        return packetPos == that.packetPos;

    }

    @Override
    public int hashCode() {
        int result = (isMultimatch ? 1 : 0);
        result = 31 * result + beginPos;
        result = 31 * result + offset;
        result = 31 * result + packetPos;
        return result;
    }
}
