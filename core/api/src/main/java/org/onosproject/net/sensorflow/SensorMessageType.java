package org.onosproject.net.sensorflow;

@Deprecated
public class SensorMessageType {
    public static final int DATA = 0;
    public static final int BEACON = 1;
    public static final int REPORT = 2;
    public static final int REQUEST = 3;
    public static final int RESPONSE = 4;
    public static final int OPEN_PATH = 5;
    public static final int CONFIG = 6;
    public static final int DPID_CONNECTION = 7;
    public static final int MULTICAST_GROUP_JOIN = 8;
    public static final int MULTICAST_GROUP_LEAVE = 9;
    public static final int MULTICAST_DATA = 10;
    public static final int MULTICAST_GEO_COORDINATES = 11;
    public static final int REPORT_GEO_COORDINATES = 12;
    public static final int MULTICAST_CTRL_DATA = 13;
    public static final int MAP_PACKET = 20;
    public static final int DATA_REQUEST = 128;
    public static final int BEACON_REQUEST = 129;
    public static final int REPORT_REQUEST = 130;
    public static final int REQUEST_REQUEST = 131;
    public static final int RESPONE_REQUEST = 132;
    public static final int OPEN_PATH_REQUEST = 133;
    public static final int CONFIG_REQUEST = 134;
    public static final int MULTICAST_DATA_REQUEST = 138;
    public static final int MULTICAST_CTRL_DATA_REQUEST = 141;

    private final int value;

    public SensorMessageType(int type) {
        this.value = type;
    }

    public static SensorMessageType getType(int value) {
//        for (SensorMessageType type : SensorMessageType.values()) {
//            if (type.value == value) {
//                return type;
//            }
//        }

        return null;
    }

    public int value() {
        return this.value;
    }
}
