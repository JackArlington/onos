package org.onosproject.net.sensorpacket;

import com.google.common.base.MoreObjects;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Created by aca on 9/15/15.
 */
public final class SensorPacketTypeRegistry {
    private static final int AVAILABLE_PACKET_TYPES = 120;
    private static BitSet registeredIds = new BitSet(AVAILABLE_PACKET_TYPES);
    private static Map<Integer, SensorPacketType> generatedPacketTypes = new HashMap<>();

    protected SensorPacketTypeRegistry() {

    }

    public static SensorPacketType getPacketType(String name) {
        SensorPacketType sensorPacketType = null;
        for (int i = 0; i < AVAILABLE_PACKET_TYPES; i++) {
            if (!registeredIds.get(i)) {
                registeredIds.set(i);
                sensorPacketType = new SensorPacketType(i, name);
                generatedPacketTypes.put(i, sensorPacketType);
                break;
            }
        }

        return sensorPacketType;
    }

    public static SensorPacketType getPacketType(int givenId, String name) {
        SensorPacketType sensorPacketType = generatedPacketTypes.get(givenId);
        if (sensorPacketType == null) {
            if (!registeredIds.get(givenId)) {
                sensorPacketType = new SensorPacketType(givenId, name);
                generatedPacketTypes.put(givenId, sensorPacketType);
                registeredIds.set(givenId);
            }
        }

        return sensorPacketType;
    }

    public static final class SensorPacketType {
        private int givenId;
        private int id;
        private String name;

        private SensorPacketType(int id, String name) {
            this.givenId = id;
            this.id = id;
            this.name = name;
        }

        private SensorPacketType(int givenId, int id, String name) {
            this.givenId = givenId;
            this.id = id;
            this.name = name;
        }

        public String typeName() {
            return name;
        }

        public int originalId() {
            return givenId;
        }

        @Override
        public String toString() {
            MoreObjects.ToStringHelper stringHelper =
                    toStringHelper(this.getClass().getSimpleName());

            stringHelper.add("name", name);
            return stringHelper.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SensorPacketType)) {
                return false;
            }

            SensorPacketType that = (SensorPacketType) o;

            if (id != that.id) {
                return false;
            }
            return name.equals(that.name);

        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + name.hashCode();
            return result;
        }
    }


}