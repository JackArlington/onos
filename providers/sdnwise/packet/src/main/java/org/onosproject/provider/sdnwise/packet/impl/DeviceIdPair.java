package org.onosproject.provider.sdnwise.packet.impl;

import org.onosproject.net.ConnectPoint;
import org.onosproject.net.DeviceId;
import org.onosproject.net.PortNumber;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by aca on 9/30/15.
 */
public class DeviceIdPair {
    private static Map<DeviceId, Integer> portsUsedPerNode = new ConcurrentHashMap<>();
    public static final PortNumber CONTROLLER_PORT = PortNumber.portNumber(1);
    private DeviceId dev1;
    private DeviceId dev2;

    private int port1;
    private int port2;

    public DeviceIdPair(DeviceId dev1, DeviceId dev2) {
        this.dev1 = dev1;
        this.dev2 = dev2;

        Integer dev1Ports = portsUsedPerNode.get(dev1);
        if (dev1Ports == null) {
            port1 = 2;
        } else {
            port1 = dev1Ports + 1;
        }

        Integer dev2Ports = portsUsedPerNode.get(dev2);
        if (dev2Ports == null) {
            port2 = 2;
        } else {
            port2 = dev2Ports + 1;
        }
    }

    public ConnectPoint getConnectPoint1() {
        ConnectPoint connectPoint = new ConnectPoint(dev1, PortNumber.portNumber(port1));
        return connectPoint;
    }

    public ConnectPoint getConnectPoint2() {
        ConnectPoint connectPoint = new ConnectPoint(dev2, PortNumber.portNumber(port2));
        return connectPoint;
    }

    public void confirmPorts() {
        portsUsedPerNode.put(dev1, port1);
        portsUsedPerNode.put(dev2, port1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeviceIdPair)) {
            return false;
        }

        DeviceIdPair that = (DeviceIdPair) o;

        if (!dev1.equals(that.dev1)) {
            return false;
        }
        return dev2.equals(that.dev2);

    }

    @Override
    public int hashCode() {
        int result = dev1.hashCode();
        result = 31 * result + dev2.hashCode();
        return result;
    }
}
