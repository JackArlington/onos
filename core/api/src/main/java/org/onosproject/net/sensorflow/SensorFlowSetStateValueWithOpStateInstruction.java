package org.onosproject.net.sensorflow;

import com.google.common.base.MoreObjects;
import org.onosproject.net.flow.instructions.Instruction;

/**
 * Created by aca on 9/18/15.
 */
public class SensorFlowSetStateValueWithOpStateInstruction implements SensorFlowInstruction {
    private int stateOperand1Pos;
    private int stateOperand2Pos;
    private int stateResultPos;
    private int offset;
    private Operator operator;

    public SensorFlowSetStateValueWithOpStateInstruction(int stateOperand1Pos, int stateOperand2Pos, int stateResultPos,
                                                         int offset, Operator operator) {
        this.stateOperand1Pos = stateOperand1Pos;
        this.stateOperand2Pos = stateOperand2Pos;
        this.stateResultPos = stateResultPos;
        this.offset = offset;
        this.operator = operator;
    }

    public int stateOperand1Pos() {
        return stateOperand1Pos;
    }

    public int stateOperand2Pos() {
        return stateOperand2Pos;
    }

    public int stateResultPos() {
        return stateResultPos;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public Type getSensorFlowInstructionType() {
        return Type.STATE_STATE_OP;
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
        return MoreObjects.toStringHelper(SensorFlowSetStateValueWithOpStateInstruction.class)
                .add("stateOperand1Pos", stateOperand1Pos)
                .add("stateOperand2Pos", stateOperand2Pos)
                .add("stateResultPos", stateResultPos)
                .add("offset", offset)
                .add("operator", operator.name())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SensorFlowSetStateValueWithOpStateInstruction)) {
            return false;
        }

        SensorFlowSetStateValueWithOpStateInstruction that = (SensorFlowSetStateValueWithOpStateInstruction) o;

        if (stateOperand1Pos != that.stateOperand1Pos) {
            return false;
        }
        if (stateOperand2Pos != that.stateOperand2Pos) {
            return false;
        }
        if (stateResultPos != that.stateResultPos) {
            return false;
        }
        if (offset != that.offset) {
            return false;
        }
        return operator == that.operator;

    }

    @Override
    public int hashCode() {
        int result = stateOperand1Pos;
        result = 31 * result + stateOperand2Pos;
        result = 31 * result + stateResultPos;
        result = 31 * result + offset;
        result = 31 * result + operator.hashCode();
        return result;
    }
}
