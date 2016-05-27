package org.onosproject.net.sensorflow;

import org.onosproject.net.flow.instructions.Instruction;
import org.onosproject.net.sensor.SensorNodeAddress;

/**
 * Created by aca on 7/17/15.
 */
public class SensorFlowSetGeoPrvHopInstruction implements SensorFlowInstruction {
    private SensorNodeAddress prvNodeAddress;
    private boolean multimatch = false;

    public SensorFlowSetGeoPrvHopInstruction(SensorNodeAddress prvNodeAddress) {
        this.prvNodeAddress = prvNodeAddress;
    }

    public SensorFlowSetGeoPrvHopInstruction(SensorNodeAddress prvNodeAddress, boolean multimatch) {
        this.prvNodeAddress = prvNodeAddress;
        this.multimatch = multimatch;
    }

    public SensorNodeAddress geoPrvHopAddress() {
        return prvNodeAddress;
    }

    @Override
    public Type getSensorFlowInstructionType() {
        return Type.SET_GEO_PRV_HOP;
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
        if (!(o instanceof SensorFlowSetGeoPrvHopInstruction)) {
            return false;
        }

        SensorFlowSetGeoPrvHopInstruction that = (SensorFlowSetGeoPrvHopInstruction) o;

        if (multimatch != that.multimatch) {
            return false;
        }
        return !(prvNodeAddress != null ? !prvNodeAddress.equals(that.prvNodeAddress) : that.prvNodeAddress != null);

    }

    @Override
    public int hashCode() {
        int result = prvNodeAddress != null ? prvNodeAddress.hashCode() : 0;
        result = 31 * result + (multimatch ? 1 : 0);
        return result;
    }
}
