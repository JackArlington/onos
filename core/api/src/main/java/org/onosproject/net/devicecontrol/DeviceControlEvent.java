package org.onosproject.net.devicecontrol;

import org.onosproject.event.AbstractEvent;

/**
 * Created by aca on 6/19/15.
 */
public class DeviceControlEvent extends AbstractEvent<DeviceControlEvent.Type, DeviceControlRule> {
    public enum Type {
        DEVICE_CONTROL_RULE_ADDED,
        DEVICE_CONTROL_RULE_REMOVED
    }

    public DeviceControlEvent(Type type, DeviceControlRule subject) {
        super(type, subject);
    }

    public DeviceControlEvent(Type type, DeviceControlRule subject, long time) {
        super(type, subject, time);
    }
}
