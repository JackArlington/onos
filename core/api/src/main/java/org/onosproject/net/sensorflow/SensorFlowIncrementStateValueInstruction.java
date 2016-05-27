package org.onosproject.net.sensorflow;

import com.google.common.base.MoreObjects;
import org.onosproject.net.flow.instructions.Instruction;

import static com.google.common.base.MoreObjects.toStringHelper;
import static org.onosproject.net.sensorflow.SensorFlowInstruction.Type.INCREMENT_STATE_VALUE;

/**
 * Created by aca on 9/15/15.
 */
public class SensorFlowIncrementStateValueInstruction implements SensorFlowInstruction {
    private boolean isMultimatch = false;
    private int pos;
    private int incValue;

    public SensorFlowIncrementStateValueInstruction(int pos, int incValue, boolean isMultimatch) {
        this.pos = pos;
        this.isMultimatch = isMultimatch;
        this.incValue = incValue;
    }

    public int position() {
        return pos;
    }

    public int value() {
        return incValue;
    }

    @Override
    public Type getSensorFlowInstructionType() {
        return INCREMENT_STATE_VALUE;
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

        stringHelper.add("position", pos)
                .add("value", incValue);
        return stringHelper.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SensorFlowIncrementStateValueInstruction)) {
            return false;
        }

        SensorFlowIncrementStateValueInstruction that = (SensorFlowIncrementStateValueInstruction) o;

        if (isMultimatch != that.isMultimatch) {
            return false;
        }
        if (pos != that.pos) {
            return false;
        }
        return incValue == that.incValue;

    }

    @Override
    public int hashCode() {
        int result = (isMultimatch ? 1 : 0);
        result = 31 * result + pos;
        result = 31 * result + incValue;
        return result;
    }
}
