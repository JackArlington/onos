package org.onosproject.net.sensor;

import org.onosproject.net.DeviceId;
import org.onosproject.net.SensorNodeId;
import org.onosproject.net.provider.ProviderService;

/**
 * Created by aca on 3/15/15.
 */
public interface SensorNodeProviderService extends ProviderService<SensorNodeProvider> {
    void sensorNodeDetected(SensorNodeId sensorNodeId, DeviceId deviceId,
                                   SensorNodeDesciption sensorNodeDesciption);

    void sensorNodeVanished(SensorNodeId sensorNodeId);
}
