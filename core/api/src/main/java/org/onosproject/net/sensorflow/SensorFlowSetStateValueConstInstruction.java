package org.onosproject.net.sensorflow;

import com.google.common.base.MoreObjects;
import org.onosproject.net.flow.instructions.Instruction;

import static com.google.common.base.MoreObjects.toStringHelper;
import static org.onosproject.net.sensorflow.SensorFlowInstruction.Type.SET_STATE_VALUE_CONST;

/**
 * Created by aca on 9/14/15.
 */
public class SensorFlowSetStateValueConstInstruction implements SensorFlowInstruction {
    private int beginPos;
    private int offset;
    private int value;
    private boolean multimatch = false;

    public SensorFlowSetStateValueConstInstruction(int beginPos, int offset, int value, boolean multimatch) {
        this.beginPos = beginPos;
        this.offset = offset;
        this.value = value;
        this.multimatch = multimatch;
    }

    public int beginPosition() {
        return beginPos;
    }

    public int offset() {
        return offset;
    }

    public int value() {
        return value;
    }

    @Override
    public Type getSensorFlowInstructionType() {
        return SET_STATE_VALUE_CONST;
    }

    @Override
    public boolean multimatch() {
        return multimatch;
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
                .add("value", value);
        return stringHelper.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SensorFlowSetStateValueConstInstruction)) {
            return false;
        }

        SensorFlowSetStateValueConstInstruction that = (SensorFlowSetStateValueConstInstruction) o;

        if (beginPos != that.beginPos) {
            return false;
        }
        if (offset != that.offset) {
            return false;
        }
        if (value != that.value) {
            return false;
        }
        return multimatch == that.multimatch;

    }

    @Override
    public int hashCode() {
        int result = beginPos;
        result = 31 * result + offset;
        result = 31 * result + value;
        result = 31 * result + (multimatch ? 1 : 0);
        return result;
    }
}
