package org.onosproject.net.sensorflow;

import org.onosproject.net.flow.instructions.Instruction;

import java.util.Arrays;

/**
 * Created by aca on 7/16/15.
 */
public class SensorFlowSetGeoNxHopCoordinatesInstruction implements SensorFlowInstruction {
    private byte[] coords;

    public SensorFlowSetGeoNxHopCoordinatesInstruction(byte[] coordinates) {
        setCoords(coordinates);
    }

    public byte[] getCoords() {
        return coords;
    }

    public void setCoords(byte[] coordinates) {
        this.coords = new byte[coordinates.length];
        System.arraycopy(coordinates, 0, this.coords, 0, coordinates.length);
    }

    @Override
    public Type getSensorFlowInstructionType() {
        return Type.SET_GEO_NX_HOP_COORD;
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
        if (!(o instanceof SensorFlowSetGeoNxHopCoordinatesInstruction)) {
            return false;
        }

        SensorFlowSetGeoNxHopCoordinatesInstruction that = (SensorFlowSetGeoNxHopCoordinatesInstruction) o;

        return Arrays.equals(coords, that.coords);

    }

    @Override
    public int hashCode() {
        return coords != null ? Arrays.hashCode(coords) : 0;
    }
}
