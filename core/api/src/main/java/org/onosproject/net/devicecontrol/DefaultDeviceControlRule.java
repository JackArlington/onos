package org.onosproject.net.devicecontrol;

import org.onosproject.net.DeviceId;

/**
 * Created by aca on 6/21/15.
 */
public class DefaultDeviceControlRule implements DeviceControlRule {
    private DeviceId deviceId;
    private DeviceTreatment deviceTreatment;

    public DefaultDeviceControlRule(DeviceId deviceId, DeviceTreatment deviceTreatment) {
        this.deviceId = deviceId;
        this.deviceTreatment = deviceTreatment;
    }

    @Override
    public DeviceId deviceId() {
        return deviceId;
    }

    @Override
    public DeviceTreatment deviceTreatment() {
        return deviceTreatment;
    }
}
