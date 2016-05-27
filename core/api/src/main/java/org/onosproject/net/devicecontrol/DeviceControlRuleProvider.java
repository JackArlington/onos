package org.onosproject.net.devicecontrol;

import org.onosproject.net.provider.Provider;

/**
 * Created by aca on 6/19/15.
 */
public interface DeviceControlRuleProvider extends Provider {
    void applyDeviceControlRule(DeviceControlRule deviceControlRule);
}
