package org.onosproject.net.sensorflow;

import org.onosproject.net.flow.instructions.Instruction;

import static org.onosproject.net.sensorflow.SensorFlowInstruction.Type.DROP;

/**
 * Created by aca on 7/23/15.
 */
public class SensorFlowDropPacketInstruction implements SensorFlowInstruction {
    private boolean isMultimatch = false;

    public SensorFlowDropPacketInstruction() {

    }

    public SensorFlowDropPacketInstruction(boolean isMultimatch) {
        this.isMultimatch = isMultimatch;
    }

    @Override
    public Type getSensorFlowInstructionType() {
        return DROP;
    }

    @Override
    public boolean multimatch() {
        return isMultimatch;
    }

    @Override
    public Instruction.Type type() {
        return null;
    }
}
