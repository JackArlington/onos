package org.onosproject.sdnwise.protocol;

/**
 * Created by aca on 2/25/15.
 */
public class SDNWiseBeaconMessage extends SDNWiseMessage {
    private int distance;
    private int batteryLevel;

    public SDNWiseBeaconMessage() {
        super();
    }

    public SDNWiseBeaconMessage(int distance, int batteryLevel) {
        super();
        this.distance = distance;
        this.batteryLevel = batteryLevel;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
}
