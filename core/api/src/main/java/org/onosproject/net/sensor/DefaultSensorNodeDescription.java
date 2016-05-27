package org.onosproject.net.sensor;

import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onosproject.net.AbstractDescription;
import org.onosproject.net.DeviceId;
import org.onosproject.net.PortNumber;
import org.onosproject.net.SensorNodeId;
import org.onosproject.net.SparseAnnotations;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by aca on 3/15/15.
 */
public class DefaultSensorNodeDescription extends AbstractDescription
        implements SensorNodeDesciption {
    private MacAddress macAddress;
    private MacAddress sinkMacAddress;
    private int netId;
    private byte[] address;
    private SensorNodeNeighborhood neighbors;
    private Float batteryLevel;
    private IpAddress sinkIpAddress;
    private PortNumber sinkPort;
    private IpAddress sinkConnectionIpAddress;
    private PortNumber sinkConnectionPort;
    private DeviceId sinkLocation;

    public DefaultSensorNodeDescription(MacAddress hwAddress, MacAddress sinkMacAddress,
                                        IpAddress sinkIpAddress, PortNumber sinkPort,
                                        IpAddress sinkConnectionIpAddress, PortNumber sinkConnectionPort,
                                        DeviceId sinkLocation, int netId, byte[] address,
                                        Map<SensorNodeId, Integer> neighbors, Float batteryLevel) {
        super();
        this.macAddress = hwAddress;
        this.netId = netId;
        this.address = new byte[address.length];
        this.address = Arrays.copyOf(address, address.length);
        this.neighbors = new SensorNodeNeighborhood(neighbors);
        this.batteryLevel = batteryLevel;
        this.sinkMacAddress = sinkMacAddress;
        this.sinkIpAddress = sinkIpAddress;
        this.sinkPort = sinkPort;
        this.sinkConnectionIpAddress = sinkConnectionIpAddress;
        this.sinkConnectionPort = sinkConnectionPort;
        this.sinkLocation = sinkLocation;
    }

    public DefaultSensorNodeDescription(MacAddress hwAddress, MacAddress sinkMacAddress,
                                        IpAddress sinkIpAddress, PortNumber sinkPort,
                                        IpAddress sinkConnectionIpAddress, PortNumber sinkConnectionPort,
                                        DeviceId sinkLocation, int netId, byte[] address,
                                        Map<SensorNodeId, Integer> neighbors,
                                        Float batteryLevel, SparseAnnotations... annotations) {
        super(annotations);
        this.macAddress = hwAddress;
        this.netId = netId;
        this.address = new byte[address.length];
        this.address = Arrays.copyOf(address, address.length);
        this.neighbors = new SensorNodeNeighborhood(neighbors);
        this.batteryLevel = batteryLevel;
        this.sinkMacAddress = sinkMacAddress;
        this.sinkIpAddress = sinkIpAddress;
        this.sinkPort = sinkPort;
        this.sinkConnectionIpAddress = sinkConnectionIpAddress;
        this.sinkConnectionPort = sinkConnectionPort;
        this.sinkLocation = sinkLocation;
    }

    public DefaultSensorNodeDescription(MacAddress hwAddress, int netId,
                                        byte[] address, Map<SensorNodeId, Integer> neighbors,
                                        Float batteryLevel, SparseAnnotations... annotations) {
        super(annotations);
        this.macAddress = hwAddress;
        this.netId = netId;
        this.address = new byte[address.length];
        this.address = Arrays.copyOf(address, address.length);
        this.neighbors = new SensorNodeNeighborhood(neighbors);
        this.batteryLevel = batteryLevel;
    }


    @Override
    public MacAddress hwAddress() {
        return macAddress;
    }

    @Override
    public MacAddress sinkHwAddress() {
        return sinkMacAddress;
    }

    @Override
    public IpAddress sinkIpAddress() {
        return sinkIpAddress;
    }

    @Override
    public PortNumber sinkPort() {
        return sinkPort;
    }

    @Override
    public IpAddress sinkConnectionIpAddress() {
        return sinkConnectionIpAddress;
    }

    @Override
    public PortNumber sinkConnectionPort() {
        return sinkConnectionPort;
    }

    @Override
    public DeviceId sinkLocation() {
        return sinkLocation;
    }

    @Override
    public int netId() {
        return netId;
    }

    @Override
    public byte[] address() {
        return address;
    }

    @Override
    public SensorNodeNeighborhood neighbors() {
        return neighbors;
    }

    @Override
    public Float batteryLevel() {
        return batteryLevel;
    }
}
