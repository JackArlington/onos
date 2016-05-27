package org.onosproject.net;

import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.net.sensor.SensorNodeAddress;

import java.util.Arrays;

/**
 * Created by aca on 3/15/15.
 */
public class DefaultSensorNode extends AbstractElement
        implements SensorNode {

    private MacAddress macAddress;
    private MacAddress sinkMacAddress;
    private int netId;
    private byte[] addr;
    private DeviceId deviceId;
    private int[] coordinates;
    private SensorNodeId sensorNodeId;
    private IpAddress sinkIpAddress;
    private SensorNode associatedSinkNode;
    private PortNumber sinkPort;
    private IpAddress sinkConnectionIpAddress;
    private PortNumber sinkConnectionPort;
    private DeviceId sinkLocation;

    public DefaultSensorNode(ProviderId providerId, SensorNodeId sensorNodeId,
                             DeviceId deviceId, MacAddress mac,
                             int netId, byte[] addr, Annotations ...annotations) {
        super(providerId, sensorNodeId, annotations);
        this.macAddress = mac;
        this.netId = netId;
        this.addr = new byte[addr.length];
        this.addr = Arrays.copyOf(addr, addr.length);
        this.deviceId = deviceId;
        this.coordinates = null;
        this.sensorNodeId = sensorNodeId;
    }

    public DefaultSensorNode(ProviderId providerId, SensorNodeId sensorNodeId,
                             DeviceId deviceId, MacAddress mac, MacAddress sinkMac,
                             IpAddress sinkIpAddress, PortNumber sinkPort, IpAddress sinkConnectionIpAddress,
                             PortNumber sinkConnectionPort, SensorNode associatedSinkNode, DeviceId sinkLocation,
                             int netId, byte[] addr, Annotations ...annotations) {
        super(providerId, sensorNodeId, annotations);
        this.macAddress = mac;
        this.netId = netId;
        this.addr = new byte[addr.length];
        this.addr = Arrays.copyOf(addr, addr.length);
        this.deviceId = deviceId;
        this.sinkMacAddress = sinkMac;
        this.coordinates = null;
        this.sensorNodeId = sensorNodeId;
        this.sinkIpAddress = sinkIpAddress;
        this.sinkPort = sinkPort;
        this.sinkConnectionIpAddress = sinkConnectionIpAddress;
        this.sinkConnectionPort = sinkConnectionPort;
        this.sinkLocation = sinkLocation;

        if (associatedSinkNode != null) {
            this.associatedSinkNode = associatedSinkNode;
        } else {
            this.associatedSinkNode = this;
        }
    }

    public DefaultSensorNode(ProviderId providerId, SensorNodeId sensorNodeId,
                             DeviceId deviceId, MacAddress mac, MacAddress sinkMac,
                             int netId, byte[] addr, int[] xyzCoordinates,
                             Annotations ...annotations) {
        super(providerId, sensorNodeId, annotations);
        this.macAddress = mac;
        this.netId = netId;
        this.addr = new byte[addr.length];
        this.addr = Arrays.copyOf(addr, addr.length);
        this.deviceId = deviceId;
        this.sinkMacAddress = sinkMac;
        this.coordinates = new int[xyzCoordinates.length];
        this.sensorNodeId = sensorNodeId;
        System.arraycopy(xyzCoordinates, 0, this.coordinates, 0, xyzCoordinates.length);
    }

    @Override
    public SensorNodeId id() {
        return (SensorNodeId) id;
    }

    @Override
    public DeviceId deviceId() {
        return deviceId;
    }

    @Override
    public MacAddress mac() {
        return macAddress;
    }

    @Override
    public MacAddress sinkMac() {
        return sinkMacAddress;
    }

    @Override
    public IpAddress sinkAddress() {
        return sinkIpAddress;
    }

    @Override
    public PortNumber sinkPort() {
        return sinkPort;
    }

    @Override
    public IpAddress sinkConnectionAddress() {
        return sinkConnectionIpAddress;
    }

    @Override
    public PortNumber sinkConnectionPort() {
        return sinkConnectionPort;
    }

    @Override
    public SensorNode associatedSink() {
        return associatedSinkNode;
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
    public byte[] addr() {
        return addr;
    }

    @Override
    public SensorNodeAddress nodeAddress() {
        return new SensorNodeAddress((byte) netId, addr);
    }

    @Override
    public double[] xyzCoordinates(SensorNodeLocalization localizationAlgorithm) {
        double[] coordinates = localizationAlgorithm.xyzCoordinates(sensorNodeId);
        return coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultSensorNode)) {
            return false;
        }

        DefaultSensorNode that = (DefaultSensorNode) o;

        return deviceId.equals(that.deviceId);

    }

    @Override
    public int hashCode() {
        return deviceId.hashCode();
    }
}
