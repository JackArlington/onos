package org.onosproject.sdnwise.protocol;

import org.onlab.packet.IpAddress;
import org.onosproject.net.PortNumber;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aca on 2/25/15.
 */
public class SDNWiseReportMessage extends SDNWiseMessage {
    private int distance;
    private int batteryLevel;
    private int nofNeighbors;
    private Map<SDNWiseNodeId, Integer> neighborRSSI;
    private IpAddress sinkIpAddress;
    private PortNumber sinkPortNumber;

    public SDNWiseReportMessage() {
        super();
        this.neighborRSSI = new HashMap<>();
    }

    public SDNWiseReportMessage(int distance, int batteryLevel, int nofNeighbors,
                                IpAddress sinkIpAddress, PortNumber sinkPortNumber) {
        super();
        this.distance = distance;
        this.batteryLevel = batteryLevel;
        this.nofNeighbors = nofNeighbors;
        this.neighborRSSI = new HashMap<>();
        this.sinkIpAddress = sinkIpAddress;
        this.sinkPortNumber = sinkPortNumber;
    }

    public SDNWiseReportMessage(int distance, int batteryLevel, int nofNeighbors,
                                Map<SDNWiseNodeId, Integer> neighborRSSI) {
        super();
        this.distance = distance;
        this.batteryLevel = batteryLevel;
        this.nofNeighbors = nofNeighbors;
        this.neighborRSSI = neighborRSSI;
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

    public int getNofNeighbors() {
        return nofNeighbors;
    }

    public void setNofNeighbors(int nofNeighbors) {
        this.nofNeighbors = nofNeighbors;
    }

    public Map<SDNWiseNodeId, Integer> getNeighborRSSI() {
        return neighborRSSI;
    }

    public void setNeighborRSSI(Map<SDNWiseNodeId, Integer> neighborRSSI) {
        this.neighborRSSI = neighborRSSI;
    }

    public void addNeighborRSSI(SDNWiseNodeId neighbor, int rssi) {
        this.neighborRSSI.put(neighbor, rssi);
    }

    public Integer getNeighborRSSI(SDNWiseNodeId neighbor) {
        return this.neighborRSSI.get(neighbor);
    }

    public IpAddress sinkIp() {
        return this.sinkIpAddress;
    }

    public PortNumber sinkPort() {
        return this.sinkPortNumber;
    }
}
