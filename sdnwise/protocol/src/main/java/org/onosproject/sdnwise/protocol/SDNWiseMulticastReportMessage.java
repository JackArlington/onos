package org.onosproject.sdnwise.protocol;

import org.onosproject.net.sensor.SensorNodeAddress;

/**
 * Created by aca on 4/30/15.
 */
public class SDNWiseMulticastReportMessage extends SDNWiseMessage {
    private SensorNodeAddress sensorNodeAddress;

    public SDNWiseMulticastReportMessage(byte netId, byte[] addr) {
        this.sensorNodeAddress = new SensorNodeAddress(netId, addr);
    }

    public SensorNodeAddress getSensorNodeAddress() {
        return sensorNodeAddress;
    }

    public void setSensorNodeAddress(SensorNodeAddress sensorNodeAddress) {
        this.sensorNodeAddress = sensorNodeAddress;
    }
}
