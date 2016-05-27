package org.onosproject.net.sensorflow;

import org.onosproject.net.flow.instructions.Instruction;

import static org.onosproject.net.sensorflow.SensorFlowInstruction.Type.ASK_CONTROLLER;

/**
 * Created by aca on 9/16/15.
 */
public class SensorFlowAskControllerInstruction implements SensorFlowInstruction {
    private boolean multimatch = false;

    public SensorFlowAskControllerInstruction(boolean multimatch) {
        this.multimatch = multimatch;
    }

    @Override
    public Type getSensorFlowInstructionType() {
        return ASK_CONTROLLER;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SensorFlowAskControllerInstruction)) {
            return false;
        }

        SensorFlowAskControllerInstruction that = (SensorFlowAskControllerInstruction) o;

        return multimatch == that.multimatch;

    }

    @Override
    public int hashCode() {
        return (multimatch ? 1 : 0);
    }
}
