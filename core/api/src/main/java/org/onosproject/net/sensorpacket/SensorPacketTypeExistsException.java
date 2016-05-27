package org.onosproject.net.sensorpacket;

/**
 * Created by aca on 9/26/15.
 */
public class SensorPacketTypeExistsException extends Exception {
    public SensorPacketTypeExistsException(int givenId, String name) {
        super("Packet " + name + " with id " + givenId + " already exists");
    }
}
