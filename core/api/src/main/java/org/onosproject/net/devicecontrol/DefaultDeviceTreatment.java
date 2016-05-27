package org.onosproject.net.devicecontrol;

import com.google.common.collect.ImmutableList;
import org.onosproject.net.SensorNode;
import org.onosproject.net.SensorNodeLocalization;
import org.onosproject.net.devicecontrol.instructions.DeviceControlInstruction;
import org.onosproject.net.devicecontrol.instructions.DeviceControlInstructions;
import org.onosproject.net.sensor.SensorNodeAddress;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Created by aca on 6/19/15.
 */
public final class DefaultDeviceTreatment implements DeviceTreatment {
    private List<DeviceControlInstruction> instructions;

    private DefaultDeviceTreatment(List<DeviceControlInstruction> instructions) {
        this.instructions = ImmutableList.copyOf(instructions);
    }

    @Override
    public List<DeviceControlInstruction> instructions() {
        return instructions;
    }

    public static DeviceTreatment.Builder builder() {
        return new Builder();
    }

    @Override
    public int hashCode() {
        return Objects.hash(instructions);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DefaultDeviceTreatment) {
            DefaultDeviceTreatment that = (DefaultDeviceTreatment) obj;
            return Objects.equals(instructions, that.instructions);

        }
        return false;
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("instructions", instructions)
                .toString();
    }

    public static final class Builder implements DeviceTreatment.Builder {
        private List<DeviceControlInstruction> instructions = new ArrayList<>();

        private Builder() {

        }

        @Override
        public DeviceTreatment.Builder add(DeviceControlInstruction instruction) {
            instructions.add(instruction);
            return this;
        }

        @Override
        public DeviceTreatment.Builder setPower(SensorNodeAddress sinkNodeAddress, Double power) {
            return add(DeviceControlInstructions.createSetPower(sinkNodeAddress, power));
        }

        @Override
        public DeviceTreatment.Builder installFunction(SensorNodeAddress sinkNodeAddress,
                                                       URI functionLocation, URI functionCallback) {
            return add(DeviceControlInstructions.createInstallFunction(sinkNodeAddress,
                    functionLocation, functionCallback));
        }

        @Override
        public DeviceTreatment.Builder turnOn(SensorNodeAddress sinkNodeAddress) {
            return add(DeviceControlInstructions.createTurnOn(sinkNodeAddress));
        }

        @Override
        public DeviceTreatment.Builder turnOff(SensorNodeAddress sinkNodeAddress, Long sleepTime) {
            return add(DeviceControlInstructions.createTurnOff(sinkNodeAddress, sleepTime));
        }

        @Override
        public DeviceTreatment.Builder setCoordinates(SensorNodeAddress sinkNodeAddress,
                                                      Double x, Double y, Double z) {
            return add(DeviceControlInstructions.createSetCoordinates(sinkNodeAddress, x, y, z));
        }

        @Override
        public DeviceTreatment.Builder setNeighborsCoordinates(SensorNodeAddress sinkNodeAddress,
                                                               final List<SensorNode> neighbors,
                                                               SensorNodeLocalization localizationAlgo) {
            return add(DeviceControlInstructions.createSetNeighborsCoordinates(
                    sinkNodeAddress, neighbors, localizationAlgo));
        }

        @Override
        public DeviceTreatment build() {
            return new DefaultDeviceTreatment(this.instructions);
        }
    }
}
