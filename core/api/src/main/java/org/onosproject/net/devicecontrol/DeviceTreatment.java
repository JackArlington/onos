package org.onosproject.net.devicecontrol;

import org.onosproject.net.SensorNode;
import org.onosproject.net.SensorNodeLocalization;
import org.onosproject.net.devicecontrol.instructions.DeviceControlInstruction;
import org.onosproject.net.sensor.SensorNodeAddress;

import java.net.URI;
import java.util.List;

/**
 * Created by aca on 6/19/15.
 */
public interface DeviceTreatment {
    List<DeviceControlInstruction> instructions();

    interface Builder {
        Builder add(DeviceControlInstruction instruction);

        Builder setPower(SensorNodeAddress sinkNodeAddress, Double power);

        Builder installFunction(SensorNodeAddress sinkNodeAddress, URI functionLocation, URI functionCallback);

        Builder turnOn(SensorNodeAddress sinkNodeAddress);

        Builder turnOff(SensorNodeAddress sinkNodeAddress, Long sleepTime);

        Builder setCoordinates(SensorNodeAddress sinkNodeAddress, Double x, Double y, Double z);

        Builder setNeighborsCoordinates(SensorNodeAddress sinkNodeAddress,
                                        List<SensorNode> neighbors, SensorNodeLocalization localizationAlgo);

        DeviceTreatment build();
    }
}
