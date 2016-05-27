package org.onosproject.net.sensorflow;

import org.onosproject.net.flow.instructions.Instruction;

import java.util.Arrays;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Created by aca on 3/3/15.
 */
public class SensorFlowForwardPacketUnicastInstruction implements SensorFlowInstruction {
    private byte[] dst;

    public SensorFlowForwardPacketUnicastInstruction(byte[] dst) {
        this.dst = dst;
    }

    public byte[] getDst() {
        return dst;
    }

    public void setDst(byte[] dst) {
        this.dst = dst;
    }

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
        return toStringHelper(getSensorFlowInstructionType().toString())
                .add("dst", Arrays.toString(dst)).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SensorFlowForwardPacketUnicastInstruction)) {
            return false;
        }

        SensorFlowForwardPacketUnicastInstruction that = (SensorFlowForwardPacketUnicastInstruction) o;

        if (!Arrays.equals(dst, that.dst)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return dst != null ? Arrays.hashCode(dst) : 0;
    }
}
