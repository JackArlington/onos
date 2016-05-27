package org.onosproject.net.sensorflow;

import com.google.common.collect.ImmutableList;
import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onlab.packet.VlanId;
import org.onosproject.net.Path;
import org.onosproject.net.PortNumber;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.flow.instructions.Instruction;
import org.onosproject.net.sensor.SensorNodeAddress;
import org.onosproject.net.sensorpacket.SensorPacketTypeRegistry.SensorPacketType;

import java.util.ArrayList;
import java.util.List;

import static org.onosproject.net.sensorflow.SensorFlowInstruction.Operator;

/**
 * Created by aca on 3/3/15.
 */
public final class SensorEnabledTrafficTreatment implements SensorTrafficTreatment {
    private List<Instruction> allInstructions = new ArrayList<>();
    private List<Instruction> ofInstructions = new ArrayList<>();
    private List<SensorFlowInstruction> sensorFlowInstructions = new ArrayList<>();

    private static TrafficTreatment.Builder ofTTBuilder;

    @Override
    public List<Instruction> instructions() {
        return allInstructions;
    }


    @Override
    public List<SensorFlowInstruction> sensorFlowInstructions() {
        return sensorFlowInstructions;
    }

    @Override
    public List<Instruction> ofInstructions() {
        return ofInstructions;
    }

    private SensorEnabledTrafficTreatment(List<SensorFlowInstruction> sensorFlowInstructions) {
        this.sensorFlowInstructions = ImmutableList.copyOf(sensorFlowInstructions);
        this.allInstructions.addAll(ImmutableList.copyOf(sensorFlowInstructions));
    }

    private SensorEnabledTrafficTreatment(List<SensorFlowInstruction> sensorFlowInstructions,
                                          List<Instruction> ofInstructions) {
        this.sensorFlowInstructions = ImmutableList.copyOf(sensorFlowInstructions);
        this.ofInstructions = ImmutableList.copyOf(ofInstructions);
        this.allInstructions.addAll(ImmutableList.copyOf(sensorFlowInstructions));
        this.allInstructions.addAll(ImmutableList.copyOf(ofInstructions));
    }

    public static SensorTrafficTreatment.Builder builder() {
        return new Builder();
    }

    public static final class Builder implements SensorTrafficTreatment.Builder {

        private List<SensorFlowInstruction> sensorFlowInstructions1 = new ArrayList<>();
        private List<Instruction> ofInstructions = new ArrayList<>();
        private TrafficTreatment.Builder builder = DefaultTrafficTreatment.builder();
        private TrafficTreatment trafficTreatment = null;

        @Override
        public SensorTrafficTreatment.Builder addSensorFlow(SensorFlowInstruction instruction) {
            sensorFlowInstructions1.add(instruction);
            return this;
        }

        @Override
        public SensorTrafficTreatment.Builder setUnicastDst(byte[] dst) {
            return addSensorFlow(SensorFlowInstructions.forwardUnicast(dst));
        }

        @Override
        public SensorTrafficTreatment.Builder setOpenPath(Path openPath) {
            return addSensorFlow(SensorFlowInstructions.forwardOpenPath(openPath));
        }

        @Override
        public SensorTrafficTreatment.Builder setPacketSrcAddress(SensorNodeAddress srcAddress) {
            return addSensorFlow(SensorFlowInstructions.setSrcAddress(srcAddress));
        }

        @Override
        public SensorTrafficTreatment.Builder setPacketDstAddress(SensorNodeAddress dstAddress) {
            return addSensorFlow(SensorFlowInstructions.setDstAddress(dstAddress));
        }

        @Override
        public SensorTrafficTreatment.Builder setForwardFunction(byte function, boolean isMultimatch) {
            return addSensorFlow(SensorFlowInstructions.forwardToApplication(function, isMultimatch));
        }

        @Override
        public SensorTrafficTreatment.Builder setForwardFunction(byte function, boolean isMultimatch, int[] args) {
            return addSensorFlow(SensorFlowInstructions.forwardToApplication(function, isMultimatch, args));
        }

        @Override
        public SensorTrafficTreatment.Builder setGeoNxHopCoordinates(byte[] coordinates) {
            return addSensorFlow(SensorFlowInstructions.setGeoNxHopCoordinates(coordinates));
        }

        @Override
        public SensorTrafficTreatment.Builder setGeoNxHopAddress(SensorNodeAddress geoNxHopAddress) {
            return addSensorFlow(SensorFlowInstructions.setGeoNxHop(geoNxHopAddress));
        }

        @Override
        public SensorTrafficTreatment.Builder setGeoPrvHopAddress(SensorNodeAddress geoPrvHopAddress) {
            return addSensorFlow(SensorFlowInstructions.setGeoPrvHop(geoPrvHopAddress));
        }

        @Override
        public SensorTrafficTreatment.Builder setPacketType(SensorPacketType packetType, boolean isMultimatch) {
            return addSensorFlow(SensorFlowInstructions.setPacketType(packetType, isMultimatch));
        }

        @Override
        public SensorTrafficTreatment.Builder setPacketLength(int length) {
            return addSensorFlow(SensorFlowInstructions.setPacketLength(length));
        }

        @Override
        public SensorTrafficTreatment.Builder setPacketType(SensorPacketType packetType) {
            return addSensorFlow(SensorFlowInstructions.setPacketType(packetType, false));
        }

        @Override
        public SensorTrafficTreatment.Builder setPacketValueAtPosConst(int pos, int value) {
            return addSensorFlow(SensorFlowInstructions.setPacketValueConst(pos, value));
        }

        @Override
        public SensorTrafficTreatment.Builder reMatchPacket() {
            return addSensorFlow(SensorFlowInstructions.reMatchPacket());
        }

        @Override
        public SensorTrafficTreatment.Builder dropPacket() {
            return addSensorFlow(SensorFlowInstructions.dropPacket());
        }

        @Override
        public SensorTrafficTreatment.Builder askController(boolean isMultimatch) {
            return addSensorFlow(SensorFlowInstructions.askController(isMultimatch));
        }

        @Override
        public SensorTrafficTreatment.Builder dropPacket(boolean isMultimatch) {
            return addSensorFlow(SensorFlowInstructions.dropPacket(isMultimatch));
        }

        @Override
        public SensorTrafficTreatment.Builder setStateValueConst(int beginPos, int offset, int value,
                                                                  boolean isMultimatch) {
            return addSensorFlow(SensorFlowInstructions.setStateValueConst(beginPos, offset, value, isMultimatch));
        }

        @Override
        public SensorTrafficTreatment.Builder setStateValuePacket(int beginPos, int offset, int packetPos, boolean
                isMultimatch) {
            return addSensorFlow(SensorFlowInstructions.setStateValuePacket(beginPos, offset, packetPos, isMultimatch));
        }

        @Override
        public SensorTrafficTreatment.Builder setStateValueWithOpPacket(int packetPos, int stateOperandPos,
                                                                        int stateResultPos, int offset,
                                                                        Operator operator) {
            return addSensorFlow(SensorFlowInstructions.setStateValueWithOpPacket(packetPos, stateOperandPos,
                    stateResultPos, offset, operator));
        }

        @Override
        public SensorTrafficTreatment.Builder setStateValueWithOpConst(int stateOperandPos, int stateResultPos,
                                                                       int constValue, int offset,
                                                                       Operator operator) {
            return addSensorFlow(SensorFlowInstructions.setStateValueWithOpConst(stateOperandPos, stateResultPos,
                    constValue, offset, operator));
        }

        @Override
        public SensorTrafficTreatment.Builder setStateValueWithOpState(int stateOperand1Pos, int stateOperand2Pos,
                                                                       int stateResultPos, int offset,
                                                                       Operator operator) {
            return addSensorFlow(SensorFlowInstructions.setStateValueWithOpState(stateOperand1Pos, stateOperand2Pos,
                    stateResultPos, offset, operator));
        }

        @Override
        public SensorTrafficTreatment.Builder incrementStateValue(int pos, int incValue, boolean isMultimatch) {
            return addSensorFlow(SensorFlowInstructions.incrementStateValue(pos, incValue, isMultimatch));
        }

        @Override
        public SensorTrafficTreatment buildSensorFlow() {
            if (trafficTreatment != null) {
                ofInstructions = ImmutableList.copyOf(trafficTreatment.instructions());
                return new SensorEnabledTrafficTreatment(sensorFlowInstructions1, ofInstructions);
            }

            return new SensorEnabledTrafficTreatment(sensorFlowInstructions1);
        }

        @Override
        public TrafficTreatment.Builder add(Instruction instruction) {
            return builder.add(instruction);
        }

        @Override
        public void drop() {
            builder().drop();
        }

        @Override
        public TrafficTreatment.Builder setOutput(PortNumber portNumber) {
            return builder.setOutput(portNumber);
        }

        @Override
        public TrafficTreatment.Builder setEthSrc(MacAddress macAddress) {
            return builder.setEthSrc(macAddress);
        }

        @Override
        public TrafficTreatment.Builder setEthDst(MacAddress macAddress) {
            return builder.setEthDst(macAddress);
        }

        @Override
        public TrafficTreatment.Builder setVlanId(VlanId vlanId) {
            return builder.setVlanId(vlanId);
        }

        @Override
        public TrafficTreatment.Builder setVlanPcp(Byte aByte) {
            return builder.setVlanPcp(aByte);
        }

        @Override
        public TrafficTreatment.Builder setIpSrc(IpAddress ipAddress) {
            return builder.setIpSrc(ipAddress);
        }

        @Override
        public TrafficTreatment.Builder setIpDst(IpAddress ipAddress) {
            return builder.setIpDst(ipAddress);
        }

        @Override
        public TrafficTreatment.Builder pushMpls() {
            return builder.pushMpls();
        }

        @Override
        public TrafficTreatment.Builder popMpls() {
            return builder.popMpls();
        }

        @Override
        public TrafficTreatment.Builder setMpls(Integer integer) {
            return builder.setMpls(integer);
        }

        @Override
        public TrafficTreatment.Builder setLambda(short i) {
            return builder.setLambda(i);
        }

        @Override
        public TrafficTreatment build() {
            trafficTreatment = builder.build();
            return trafficTreatment;
        }
    }
}
