package org.onosproject.net.sensorflow;

import org.onosproject.net.flow.instructions.Instruction;
import org.onosproject.net.sensor.SensorNodeAddress;

/**
 * Created by aca on 7/17/15.
 */
public class SensorFlowSetGeoNxHopInstruction implements SensorFlowInstruction {
    private SensorNodeAddress geoNxHopAddress;

    public SensorFlowSetGeoNxHopInstruction(SensorNodeAddress geoNxHopAddress) {
        this.geoNxHopAddress = geoNxHopAddress;
    }

    public SensorNodeAddress geoNxHopAddress() {
        return geoNxHopAddress;
    }

    @Override
    public Type getSensorFlowInstructionType() {
        return Type.SET_GEO_NX_HOP;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SensorFlowSetGeoNxHopInstruction)) {
            return false;
        }

        SensorFlowSetGeoNxHopInstruction that = (SensorFlowSetGeoNxHopInstruction) o;

        return !(geoNxHopAddress != null ? !geoNxHopAddress.equals(that.geoNxHopAddress) : that.geoNxHopAddress !=
                null);

    }

    @Override
    public int hashCode() {
        return geoNxHopAddress != null ? geoNxHopAddress.hashCode() : 0;
    }
}
