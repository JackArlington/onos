package org.onosproject.net.sensorflow;

import com.google.common.base.MoreObjects;
import org.onosproject.net.flow.instructions.Instruction;
import org.onosproject.net.sensor.SensorNodeAddress;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Created by aca on 5/24/15.
 */
public class SensorFlowSetDstAddrInstruction implements SensorFlowInstruction {
    private SensorNodeAddress dstAddr;
    private boolean multimatch = false;

    public SensorFlowSetDstAddrInstruction(SensorNodeAddress dstAddr) {
        this.dstAddr = dstAddr;
    }

    public SensorFlowSetDstAddrInstruction(SensorNodeAddress dstAddr, boolean multimatch) {
        this.dstAddr = dstAddr;
        this.multimatch = multimatch;
    }

    public SensorNodeAddress getDstAddr() {
        return dstAddr;
    }

    @Override
    public Type getSensorFlowInstructionType() {
        return Type.SET_DST_ADDR;
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

        stringHelper.add("dst", dstAddr.toString());
        return stringHelper.toString();
    }
}
