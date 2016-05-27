package org.onosproject.net.sensorflow;

import com.google.common.base.MoreObjects;
import org.onosproject.net.flow.instructions.Instruction;

import static org.onosproject.net.sensorflow.SensorFlowInstruction.Type.REMATCH_PACKET;

/**
 * Created by aca on 9/24/15.
 */
public class SensorFlowReMatchPacketInstruction implements SensorFlowInstruction {
    public SensorFlowReMatchPacketInstruction() {

    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(SensorFlowReMatchPacketInstruction.class).toString();
    }

    @Override
    public Type getSensorFlowInstructionType() {
        return REMATCH_PACKET;
    }

    @Override
    public boolean multimatch() {
        return false;
    }

    @Override
    public Instruction.Type type() {
        return null;
    }
}
