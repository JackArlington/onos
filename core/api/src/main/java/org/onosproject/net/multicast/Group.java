package org.onosproject.net.multicast;

import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onosproject.net.sensor.SensorNodeAddress;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by aca on 4/30/15.
 */
public class Group {
    public static final String MULTICAST_SCHEME = "multicast";
    private URI id;
    private MacAddress macAddress;
    private IpAddress ipAddress;
    private SensorNodeAddress sensorNodeAddress;

    public Group(URI id, MacAddress macAddress, IpAddress ipAddress) {
        this.id = id;
        this.macAddress = macAddress;
        this.ipAddress = ipAddress;
    }

    public Group(MacAddress macAddress, IpAddress ipAddress) {
        this.macAddress = macAddress;
        this.ipAddress = ipAddress;

        try {
            this.id = new URI(MULTICAST_SCHEME, ipAddress.toString(), macAddressToString(macAddress));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Group(MacAddress macAddress) {
        byte[] mac = macAddress.toBytes();
        byte[] ipOctet = {(byte) 224, mac[3], mac[4], mac[5]};

        this.macAddress = macAddress;
        this.ipAddress = IpAddress.valueOf(IpAddress.Version.INET, ipOctet);
        this.sensorNodeAddress = new SensorNodeAddress(mac[3], (new byte[] {mac[4], mac[5]}));

        try {
            this.id = new URI(MULTICAST_SCHEME, ipAddress.toString(), macAddressToString(macAddress));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Group(IpAddress ipAddress) {
        this.ipAddress = ipAddress;
        byte[] addr = ipAddress.toOctets();
        byte[] mac = {0x01, 0x00, 0x5e, addr[1], addr[2], addr[3]};
        macAddress = MacAddress.valueOf(mac);
        this.sensorNodeAddress = new SensorNodeAddress(mac[3], (new byte[] {mac[4], mac[5]}));

        try {
            this.id = new URI(MULTICAST_SCHEME, ipAddress.toString(), macAddressToString(macAddress));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Group(SensorNodeAddress sensorNodeAddress) {
        this.sensorNodeAddress = sensorNodeAddress;
        byte[] sensAddr = sensorNodeAddress.getAddr();
        byte[] mac = {0x01, 0x00, 0x5e, sensorNodeAddress.getNetId(), sensAddr[1], sensAddr[0]};
        macAddress = MacAddress.valueOf(mac);
        byte[] ipOctet = {(byte) 224, mac[3], mac[4], mac[5]};
        this.ipAddress = IpAddress.valueOf(IpAddress.Version.INET, ipOctet);

        try {
            this.id = new URI(MULTICAST_SCHEME, ipAddress.toString(), macAddressToString(macAddress));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private String macAddressToString(MacAddress macAddress) {
        String mac = macAddress.toString();
        String macString = mac.replaceAll(":", "-");
        return macString;
    }

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public MacAddress getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(MacAddress macAddress) {
        this.macAddress = macAddress;
    }

    public IpAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(IpAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public SensorNodeAddress getSensorNodeAddress() {
        return sensorNodeAddress;
    }

    public void setSensorNodeAddress(SensorNodeAddress sensorNodeAddress) {
        this.sensorNodeAddress = sensorNodeAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Group)) {
            return false;
        }

        Group group = (Group) o;

        if (id != null ? !id.equals(group.id) : group.id != null) {
            return false;
        }
        if (macAddress != null ? !macAddress.equals(group.macAddress) : group.macAddress != null) {
            return false;
        }
        return !(ipAddress != null ? !ipAddress.equals(group.ipAddress) : group.ipAddress != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (macAddress != null ? macAddress.hashCode() : 0);
        result = 31 * result + (ipAddress != null ? ipAddress.hashCode() : 0);
        return result;
    }
}
