package org.onosproject.net.devicecontrol;

import org.onosproject.net.DeviceId;

/**
 * Created by aca on 6/19/15.
 */
public interface DeviceControlRule {
    DeviceId deviceId();

    DeviceTreatment deviceTreatment();
}
