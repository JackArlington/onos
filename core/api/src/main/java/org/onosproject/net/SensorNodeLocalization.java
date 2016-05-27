package org.onosproject.net;

/**
 * Created by aca on 5/27/15.
 */
public interface SensorNodeLocalization {
    double[] xyzCoordinates(SensorNodeId sensorNodeId);
}
