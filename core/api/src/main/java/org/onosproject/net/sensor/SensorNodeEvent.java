package org.onosproject.net.sensor;

import org.onosproject.event.AbstractEvent;
import org.onosproject.net.SensorNode;

/**
 * Created by aca on 3/14/15.
 */
public class SensorNodeEvent extends AbstractEvent<SensorNodeEvent.Type, SensorNode> {

    public enum Type {
        SENSOR_ADDED,
        SENSOR_UPDATED,
        SENSOR_REMOVED
    }

    public SensorNodeEvent(Type type, SensorNode subject) {
        super(type, subject);
    }

    public SensorNodeEvent(Type type, SensorNode subject, long time) {
        super(type, subject, time);
    }
}
