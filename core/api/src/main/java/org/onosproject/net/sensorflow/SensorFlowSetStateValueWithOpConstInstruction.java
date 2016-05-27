package org.onosproject.net.sensorflow;

import com.google.common.base.MoreObjects;
import org.onosproject.net.flow.instructions.Instruction;

/**
 * Created by aca on 9/17/15.
 */
public class SensorFlowSetStateValueWithOpConstInstruction implements SensorFlowInstruction {
    private int stateOperandPos;
    private int stateResultPos;
    private int constValue;
    private int offset;
    private Operator operator;

    public SensorFlowSetStateValueWithOpConstInstruction(int stateOperandPos, int stateResultPos, int constValue,
                                                         int offset, Operator operator) {
        this.stateOperandPos = stateOperandPos;
        this.stateResultPos = stateResultPos;
        this.constValue = constValue;
        this.offset = offset;
        this.operator = operator;
    }

    public int stateOperandPos() {
        return stateOperandPos;
    }

    public int stateResultPos() {
        return stateResultPos;
    }

    public int constValue() {
        return constValue;
    }

    public int offset() {
        return offset;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(SensorFlowSetStateValueWithOpConstInstruction.class)
                .add("stateOperandPos", stateOperandPos)
                .add("stateResultsPos", stateResultPos)
                .add("constValue", constValue)
                .add("operator", operator.name())
                .toString();
    }

    @Override
    public Type getSensorFlowInstructionType() {
        return Type.STATE_CONST_OP;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SensorFlowSetStateValueWithOpConstInstruction)) {
            return false;
        }

        SensorFlowSetStateValueWithOpConstInstruction that = (SensorFlowSetStateValueWithOpConstInstruction) o;

        if (stateOperandPos != that.stateOperandPos) {
            return false;
        }
        if (stateResultPos != that.stateResultPos) {
            return false;
        }
        if (offset != that.offset) {
            return false;
        }
        if (constValue != that.constValue) {
            return false;
        }
        return operator == that.operator;

    }

    @Override
    public int hashCode() {
        int result = stateOperandPos;
        result = 31 * result + stateResultPos;
        result = 31 * result + constValue;
        result = 31 * result + offset;
        result = 31 * result + operator.hashCode();
        return result;
    }
}
