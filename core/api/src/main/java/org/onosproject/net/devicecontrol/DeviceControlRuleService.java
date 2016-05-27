package org.onosproject.net.devicecontrol;

/**
 * Created by aca on 6/19/15.
 */
public interface DeviceControlRuleService {
    void applyDeviceControlRules(DeviceControlRule... deviceControlRules);

    void addListener(DeviceControlRuleListener listener);

    void removeListener(DeviceControlRuleListener listener);
}
