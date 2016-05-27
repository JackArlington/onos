package org.onosproject.net.sensor;

import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onosproject.net.Description;
import org.onosproject.net.DeviceId;
import org.onosproject.net.PortNumber;

/**
 * Created by aca on 3/14/15.
 */
public interface SensorNodeDesciption extends Description {
    MacAddress hwAddress();

    MacAddress sinkHwAddress();

    IpAddress sinkIpAddress();

    PortNumber sinkPort();

    IpAddress sinkConnectionIpAddress();

    PortNumber sinkConnectionPort();

    DeviceId sinkLocation();

    int netId();

    byte[] address();

    SensorNodeNeighborhood neighbors();

    Float batteryLevel();
}
