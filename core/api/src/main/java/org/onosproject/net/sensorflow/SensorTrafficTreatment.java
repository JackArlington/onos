package org.onosproject.net.sensorflow;

import org.onosproject.net.Path;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.flow.instructions.Instruction;
import org.onosproject.net.sensor.SensorNodeAddress;
import org.onosproject.net.sensorflow.SensorFlowInstruction.Operator;
import org.onosproject.net.sensorpacket.SensorPacketTypeRegistry.SensorPacketType;

import java.util.List;

/**
 * Created by aca on 3/3/15.
 */
public interface SensorTrafficTreatment extends TrafficTreatment {
    List<SensorFlowInstruction> sensorFlowInstructions();

    List<Instruction> ofInstructions();

    interface Builder extends TrafficTreatment.Builder {
        Builder addSensorFlow(SensorFlowInstruction instruction);

        Builder setUnicastDst(byte[] dst);

        Builder setOpenPath(Path openPath);

        Builder setPacketSrcAddress(SensorNodeAddress srcAddress);

        Builder setPacketDstAddress(SensorNodeAddress dstAddress);

        Builder setForwardFunction(byte function, boolean isMultimatch);

        Builder setForwardFunction(byte function, boolean isMultimatch, int[] args);

        Builder setGeoNxHopCoordinates(byte[] coordinates);

        Builder setGeoNxHopAddress(SensorNodeAddress geoNxHopAddress);

        Builder setGeoPrvHopAddress(SensorNodeAddress geoPrvHopAddress);

        Builder setPacketType(SensorPacketType packetType, boolean isMultimatch);

        Builder setPacketLength(int length);

        Builder setPacketType(SensorPacketType packetType);

        Builder setPacketValueAtPosConst(int pos, int value);

        Builder reMatchPacket();

        Builder dropPacket();

        Builder askController(boolean isMultimatch);

        Builder dropPacket(boolean isMultimatch);

        Builder setStateValueConst(int beginPos, int offset, int value, boolean isMultimatch);

        Builder setStateValuePacket(int beginPos, int offset, int packetPos, boolean isMultimatch);

        Builder setStateValueWithOpPacket(int packetPos, int stateOperandPos, int stateResultPos, int offset,
                                          Operator operator);

        Builder setStateValueWithOpConst(int stateOperandPos, int stateResultPos, int constValue, int offset,
                                         Operator operator);

        Builder setStateValueWithOpState(int stateOperand1Pos, int stateOperand2Pos, int stateResultPos, int offset,
                                         Operator operator);

        Builder incrementStateValue(int pos, int incValue, boolean isMultimatch);

        SensorTrafficTreatment buildSensorFlow();
    }
}
