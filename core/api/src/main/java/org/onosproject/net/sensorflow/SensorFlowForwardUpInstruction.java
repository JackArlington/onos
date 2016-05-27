package org.onosproject.net.sensorflow;

import com.google.common.base.MoreObjects;
import org.onosproject.net.flow.instructions.Instruction;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Created by aca on 6/18/15.
 */
public class SensorFlowForwardUpInstruction implements SensorFlowInstruction {
    private byte functionId;
    private boolean isMultimatch = false;
    private int[] args = null;

    public SensorFlowForwardUpInstruction(byte functionId) {
        this.functionId = functionId;
    }

    public SensorFlowForwardUpInstruction(byte functionId, boolean isMultimatch) {
        this.functionId = functionId;
        this.isMultimatch = isMultimatch;
    }

    public SensorFlowForwardUpInstruction(byte functionId, boolean isMultimatch, int[] args) {
        this.functionId = functionId;
        this.isMultimatch = isMultimatch;
        if (args != null) {
            this.args = new int[args.length];
            System.arraycopy(args, 0, this.args, 0, args.length);
        }
    }

    public byte getFunctionId() {
        return functionId;
    }

    public int[] getArgs() {
        return args;
    }

    @Override
    public Type getSensorFlowInstructionType() {
        return Type.FORWARD_UP;
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

        stringHelper.add("functionId", functionId);
        return stringHelper.toString();
    }
}
