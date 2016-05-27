package org.onosproject.net.sensor;

import org.onlab.packet.ChassisId;
import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onosproject.net.Device;
import org.onosproject.net.PortNumber;
import org.onosproject.net.SensorDevice;
import org.onosproject.net.SparseAnnotations;
import org.onosproject.net.device.DefaultDeviceDescription;

import java.net.URI;

/**
 * Created by aca on 2/18/15.
 */
@Deprecated
public abstract class DefaultSensorNodeDeviceDescription extends DefaultDeviceDescription
        implements SensorNodeDesciption {
    private final SensorDevice.Type type;

    public DefaultSensorNodeDeviceDescription(URI uri, SensorDevice.Type type, String manufacturer,
                                              String hwVersion, String swVersion,
                                              String serialNumber, ChassisId chassis,
                                              SparseAnnotations... annotations) {
        super(uri, Device.Type.OTHER, manufacturer, hwVersion, swVersion, serialNumber, chassis, annotations);
        this.type = type;
    }

    public SensorDevice.Type sensorDeviceType() {
        return this.type;
    }

    @Override
    public MacAddress hwAddress() {
        return null;
    }

    @Override
    public MacAddress sinkHwAddress() {
        return null;
    }

    @Override
    public IpAddress sinkIpAddress() {
        return null;
    }

    @Override
    public PortNumber sinkPort() {
        return null;
    }

    @Override
    public IpAddress sinkConnectionIpAddress() {
        return null;
    }

    @Override
    public PortNumber sinkConnectionPort() {
        return null;
    }

    @Override
    public int netId() {
        return 0;
    }

    @Override
    public byte[] address() {
        return new byte[0];
    }

    @Override
    public SensorNodeNeighborhood neighbors() {
        return null;
    }

    @Override
    public Float batteryLevel() {
        return (float) 0;
    }
}
