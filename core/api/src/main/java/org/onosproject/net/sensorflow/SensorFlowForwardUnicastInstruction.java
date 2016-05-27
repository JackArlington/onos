package org.onosproject.net.sensorflow;

import org.onosproject.net.flow.instructions.Instruction;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Created by aca on 3/2/15.
 */
public class SensorFlowForwardUnicastInstruction implements SensorFlowInstruction {
    @Override
    public Type getSensorFlowInstructionType() {
        return Type.FORWARD_UNICAST;
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
        return toStringHelper(getSensorFlowInstructionType()).toString();
    }
}
