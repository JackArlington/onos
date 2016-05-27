package org.onosproject.net;

import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onosproject.net.sensor.SensorNodeAddress;

/**
 * Created by aca on 3/14/15.
 */
public interface SensorNode extends Element {
    @Override
    SensorNodeId id();

    DeviceId deviceId();

    MacAddress mac();

    MacAddress sinkMac();

    IpAddress sinkAddress();

    PortNumber sinkPort();

    IpAddress sinkConnectionAddress();

    PortNumber sinkConnectionPort();

    SensorNode associatedSink();

    DeviceId sinkLocation();

    int netId();

    byte[] addr();

    SensorNodeAddress nodeAddress();

    double[] xyzCoordinates(SensorNodeLocalization localizationAlgorithm);
}
