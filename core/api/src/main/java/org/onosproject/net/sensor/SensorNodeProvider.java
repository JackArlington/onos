package org.onosproject.net.sensor;

import org.onosproject.net.SensorNode;
import org.onosproject.net.provider.Provider;

/**
 * Created by aca on 3/15/15.
 */
public interface SensorNodeProvider extends Provider {
    void triggerProbe(SensorNode sensorNode);
}
