package org.onosproject.net.sensorflow;

import com.google.common.base.MoreObjects;
import org.onosproject.net.flow.instructions.Instruction;
import org.onosproject.net.sensor.SensorNodeAddress;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Created by aca on 5/24/15.
 */
public class SensorFlowSetSrcAddrInstruction implements SensorFlowInstruction {
    private SensorNodeAddress srcAddr;
    private boolean multimatch = false;

    public SensorFlowSetSrcAddrInstruction(SensorNodeAddress srcAddr) {
        this.srcAddr = srcAddr;
    }

    public SensorFlowSetSrcAddrInstruction(SensorNodeAddress srcAddr, boolean multimatch) {
        this.srcAddr = srcAddr;
        this.multimatch = multimatch;
    }

    public SensorNodeAddress getSrcAddr() {
        return this.srcAddr;
    }

    @Override
    public Type getSensorFlowInstructionType() {
        return Type.SET_SRC_ADDR;
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

        stringHelper.add("src", srcAddr.toString());
        return stringHelper.toString();
    }
}
