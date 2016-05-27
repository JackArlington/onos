package org.onosproject.net.devicecontrol.instructions;

import org.onosproject.net.sensor.SensorNodeAddress;

/**
 * Created by aca on 6/19/15.
 */
public interface DeviceControlInstruction {
    enum Type {
        SET_POWER,
        INSTALL_FUNCTION,
        TURN_ON,
        TURN_OFF,
        SET_COORDINATES,
        SET_NEIGHBOR_COORDINATES
    }

    Type type();

    SensorNodeAddress sinkNodeAddress();
}
