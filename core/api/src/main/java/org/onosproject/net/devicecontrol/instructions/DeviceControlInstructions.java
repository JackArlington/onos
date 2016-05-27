package org.onosproject.net.devicecontrol.instructions;

import org.onosproject.net.SensorNode;
import org.onosproject.net.SensorNodeLocalization;
import org.onosproject.net.sensor.SensorNodeAddress;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Created by aca on 6/19/15.
 */
public final class DeviceControlInstructions {
    private DeviceControlInstructions() {}

    public static SetPowerInstruction createSetPower(SensorNodeAddress sinkNodeAddress, Double power) {
        return new SetPowerInstruction(sinkNodeAddress, power);
    }

    public static InstallFunctionInstruction createInstallFunction(SensorNodeAddress sinkNodeAddress,
                                                                   URI functionLocation, URI functionCallback) {
        return new InstallFunctionInstruction(sinkNodeAddress, functionLocation, functionCallback);
    }

    public static TurnOnInstruction createTurnOn(SensorNodeAddress sinkNodeAddress) {
        return new TurnOnInstruction(sinkNodeAddress);
    }

    public static TurnOffInstruction createTurnOff(SensorNodeAddress sinkNodeAddress, Long sleepTime) {
        return new TurnOffInstruction(sinkNodeAddress, sleepTime);
    }

    public static SetCoordinatesInstruction createSetCoordinates(SensorNodeAddress sinkNodeAddress,
                                                                 Double x, Double y, Double z) {
        return new SetCoordinatesInstruction(sinkNodeAddress, x, y, z);
    }

    public static SetNeighborsCoordinatesInstruction createSetNeighborsCoordinates(SensorNodeAddress sinkNodeAddress,
            final List<SensorNode> neighbors, SensorNodeLocalization localization) {
        return new SetNeighborsCoordinatesInstruction(sinkNodeAddress, neighbors, localization);
    }

    public static final class SetNeighborsCoordinatesInstruction implements DeviceControlInstruction {
        private List<SensorNode> neighbors;
        private SensorNodeLocalization localization;
        private SensorNodeAddress sinkNodeAddress;

        public SetNeighborsCoordinatesInstruction(SensorNodeAddress sinkNodeAddress,
                                                  final List<SensorNode> neighbors,
                                                  SensorNodeLocalization localization) {
            this.neighbors = new ArrayList<>();
            this.neighbors.addAll(neighbors);
            this.localization = localization;
            this.sinkNodeAddress = sinkNodeAddress;
        }

        public List<SensorNode> neighborhood() {
            return neighbors;
        }

        public SensorNodeLocalization localizationAlgorithm() {
            return localization;
        }

        @Override
        public Type type() {
            return Type.SET_NEIGHBOR_COORDINATES;
        }

        @Override
        public SensorNodeAddress sinkNodeAddress() {
            return sinkNodeAddress;
        }

        @Override
        public String toString() {
            return toStringHelper(type()).toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SetNeighborsCoordinatesInstruction)) {
                return false;
            }

            SetNeighborsCoordinatesInstruction that = (SetNeighborsCoordinatesInstruction) o;

            return !(neighbors != null ? !neighbors.equals(that.neighbors) : that.neighbors != null);

        }

        @Override
        public int hashCode() {
            return neighbors != null ? neighbors.hashCode() : 0;
        }
    }

    public static final class SetCoordinatesInstruction implements DeviceControlInstruction {
        private Double xCoord;
        private Double yCoord;
        private Double zCoord;
        private SensorNodeAddress sinkNodeAddress;

        public SetCoordinatesInstruction(SensorNodeAddress sinkNodeAddress,
                                         Double x, Double y, Double z) {
            this.xCoord = x;
            this.yCoord = y;
            this.zCoord = z;
            this.sinkNodeAddress = sinkNodeAddress;
        }

        public Double x() {
            return xCoord;
        }

        public Double y() {
            return yCoord;
        }

        public Double z() {
            return zCoord;
        }

        @Override
        public Type type() {
            return Type.SET_COORDINATES;
        }

        @Override
        public SensorNodeAddress sinkNodeAddress() {
            return sinkNodeAddress;
        }

        @Override
        public String toString() {
            return toStringHelper(type())
                    .add("x", xCoord)
                    .add("y", yCoord)
                    .add("z", zCoord)
                    .toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SetCoordinatesInstruction)) {
                return false;
            }

            SetCoordinatesInstruction that = (SetCoordinatesInstruction) o;

            if (xCoord != null ? !xCoord.equals(that.xCoord) : that.xCoord != null) {
                return false;
            }
            if (yCoord != null ? !yCoord.equals(that.yCoord) : that.yCoord != null) {
                return false;
            }
            return !(zCoord != null ? !zCoord.equals(that.zCoord) : that.zCoord != null);

        }

        @Override
        public int hashCode() {
            int result = xCoord != null ? xCoord.hashCode() : 0;
            result = 31 * result + (yCoord != null ? yCoord.hashCode() : 0);
            result = 31 * result + (zCoord != null ? zCoord.hashCode() : 0);
            return result;
        }
    }

    public static final class TurnOffInstruction implements DeviceControlInstruction {
        private Long sleepTime;
        private SensorNodeAddress sinkNodeAddress;

        public TurnOffInstruction(SensorNodeAddress sinkNodeAddress, Long sleepTime) {
            this.sinkNodeAddress = sinkNodeAddress;
            this.sleepTime = sleepTime;
        }

        public Long getSleepTime() {
            return sleepTime;
        }

        @Override
        public Type type() {
            return Type.TURN_OFF;
        }

        @Override
        public SensorNodeAddress sinkNodeAddress() {
            return sinkNodeAddress;
        }

        @Override
        public String toString() {
            return toStringHelper(type())
                    .add("sleepTime", sleepTime)
                    .toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof TurnOffInstruction)) {
                return false;
            }

            TurnOffInstruction that = (TurnOffInstruction) o;

            return sleepTime == that.sleepTime;

        }

        @Override
        public int hashCode() {
            return (int) (sleepTime ^ (sleepTime >>> 32));
        }
    }

    public static final class TurnOnInstruction implements DeviceControlInstruction {
        private SensorNodeAddress sinkNodeAddress;

        public TurnOnInstruction(SensorNodeAddress sinkNodeAddress) {
            this.sinkNodeAddress = sinkNodeAddress;
        }

        @Override
        public Type type() {
            return Type.TURN_ON;
        }

        @Override
        public SensorNodeAddress sinkNodeAddress() {
            return sinkNodeAddress;
        }

        @Override
        public String toString() {
            return toStringHelper(type()).toString();
        }

        @Override
        public int hashCode() {
            return Objects.hash(type());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof TurnOnInstruction) {
                TurnOnInstruction that = (TurnOnInstruction) obj;
                return Objects.equals(type(), that.type());

            }
            return false;
        }
    }

    public static final class InstallFunctionInstruction implements DeviceControlInstruction {
        private URI functionLocation;
        private URI functionCallback;
        private SensorNodeAddress sinkNodeAddress;

        public InstallFunctionInstruction(SensorNodeAddress sinkNodeAddress,
                                          URI functionLocation, URI functionCallback) {
            this.functionCallback = functionCallback;
            this.functionLocation = functionLocation;
            this.sinkNodeAddress = sinkNodeAddress;
        }

        public URI getFunctionLocation() {
            return functionLocation;
        }

        public URI getFunctionCallback() {
            return functionCallback;
        }

        @Override
        public Type type() {
            return Type.INSTALL_FUNCTION;
        }

        @Override
        public SensorNodeAddress sinkNodeAddress() {
            return sinkNodeAddress;
        }

        @Override
        public String toString() {
            return toStringHelper(type().toString())
                    .add("location", functionLocation.toASCIIString())
                    .toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof InstallFunctionInstruction)) {
                return false;
            }

            InstallFunctionInstruction that = (InstallFunctionInstruction) o;

            if (functionLocation != null ? !functionLocation.equals(that.functionLocation) : that.functionLocation !=
                    null) {
                return false;
            }
            return !(functionCallback != null ? !functionCallback.equals(that.functionCallback) : that
                    .functionCallback !=
                    null);

        }

        @Override
        public int hashCode() {
            int result = functionLocation != null ? functionLocation.hashCode() : 0;
            result = 31 * result + (functionCallback != null ? functionCallback.hashCode() : 0);
            return result;
        }
    }

    public static final class SetPowerInstruction implements DeviceControlInstruction {
        private Double power;
        private SensorNodeAddress sinkNodeAddress;

        public SetPowerInstruction(SensorNodeAddress sinkNodeAddress, Double power) {
            this.power = power;
            this.sinkNodeAddress = sinkNodeAddress;
        }

        public Double power() {
            return power;
        }

        @Override
        public Type type() {
            return Type.SET_POWER;
        }

        @Override
        public SensorNodeAddress sinkNodeAddress() {
            return sinkNodeAddress;
        }

        @Override
        public String toString() {
            return toStringHelper(type().toString())
                    .add("power", power)
                    .toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SetPowerInstruction)) {
                return false;
            }

            SetPowerInstruction that = (SetPowerInstruction) o;

            return !(power != null ? !power.equals(that.power) : that.power != null);

        }

        @Override
        public int hashCode() {
            return power != null ? power.hashCode() : 0;
        }
    }
}
