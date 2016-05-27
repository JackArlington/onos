package org.onosproject.net.sensorflow;

import org.onosproject.net.Path;
import org.onosproject.net.sensor.SensorNodeAddress;

import static org.onosproject.net.sensorflow.SensorFlowInstruction.*;
import static org.onosproject.net.sensorpacket.SensorPacketTypeRegistry.*;

/**
 * Created by aca on 3/2/15.
 */
public final class SensorFlowInstructions {
    private SensorFlowInstructions() {}

    public static SensorFlowForwardPacketUnicastInstruction forwardUnicast(byte[] dst) {
        return new SensorFlowForwardPacketUnicastInstruction(dst);
    }

    public static SensorFlowOpenPathInstruction forwardOpenPath(Path path) {
        return new SensorFlowOpenPathInstruction(path);
    }

    public static SensorFlowSetSrcAddrInstruction setSrcAddress(SensorNodeAddress srcAddr) {
        return new SensorFlowSetSrcAddrInstruction(srcAddr);
    }

    public static SensorFlowSetDstAddrInstruction setDstAddress(SensorNodeAddress dstAddr) {
        return new SensorFlowSetDstAddrInstruction(dstAddr);
    }

    public static SensorFlowForwardUpInstruction forwardToApplication(byte functionId, boolean isMultimatch) {
        return new SensorFlowForwardUpInstruction(functionId, isMultimatch);
    }

    public static SensorFlowForwardUpInstruction forwardToApplication(byte functionId, boolean isMultimatch,
                                                                   int[] args) {
        return new SensorFlowForwardUpInstruction(functionId, isMultimatch, args);
    }

    public static SensorFlowSetGeoNxHopCoordinatesInstruction setGeoNxHopCoordinates(byte[] coordinates) {
        return new SensorFlowSetGeoNxHopCoordinatesInstruction(coordinates);
    }

    public static SensorFlowSetGeoNxHopInstruction setGeoNxHop(SensorNodeAddress geoNxHopAddress) {
        return new SensorFlowSetGeoNxHopInstruction(geoNxHopAddress);
    }

    public static SensorFlowSetGeoPrvHopInstruction setGeoPrvHop(SensorNodeAddress geoPrvHopAddress) {
        return new SensorFlowSetGeoPrvHopInstruction(geoPrvHopAddress);
    }

    public static SensorFlowPacketTypeInstruction setPacketType(SensorPacketType packetType, boolean multimatch) {
        return new SensorFlowPacketTypeInstruction(packetType, multimatch);
    }

    public static SensorFlowSetPacketLengthInstruction setPacketLength(int length) {
        return new SensorFlowSetPacketLengthInstruction(length);
    }

    public static SensorFlowDropPacketInstruction dropPacket() {
        return new SensorFlowDropPacketInstruction();
    }

    public static SensorFlowDropPacketInstruction dropPacket(boolean isMultimatch) {
        return new SensorFlowDropPacketInstruction(isMultimatch);
    }

    public static SensorFlowSetStateValueConstInstruction setStateValueConst(int beginPos, int offset, int value,
                                                                             boolean multimatch) {
        return new SensorFlowSetStateValueConstInstruction(beginPos, offset, value, multimatch);
    }

    public static SensorFlowSetStateValuePacketInstruction setStateValuePacket(int beginPos, int offset, int packetPos,
                                                                            boolean multimatch) {
        return new SensorFlowSetStateValuePacketInstruction(beginPos, offset, packetPos, multimatch);
    }

    public static SensorFlowIncrementStateValueInstruction incrementStateValue(int pos, int incValue,
                                                                            boolean isMultimatch) {
        return new SensorFlowIncrementStateValueInstruction(pos, incValue, isMultimatch);
    }

    public static SensorFlowAskControllerInstruction askController(boolean isMultimatch) {
        return new SensorFlowAskControllerInstruction(isMultimatch);
    }

    public static SensorFlowSetStateValueWithOpPacketInstruction setStateValueWithOpPacket(int packetPos,
                                                                                           int stateOperandPos,
                                                                                           int stateResultPos,
                                                                                           int offset,
                                                                                           Operator operator) {
        return new SensorFlowSetStateValueWithOpPacketInstruction(packetPos, stateOperandPos, stateResultPos, offset,
                operator);
    }

    public static SensorFlowSetStateValueWithOpConstInstruction setStateValueWithOpConst(int stateOperandPos,
                                                                                         int stateResultPos,
                                                                                         int constValue,
                                                                                         int offset,
                                                                                         Operator operator) {
        return new SensorFlowSetStateValueWithOpConstInstruction(stateOperandPos, stateResultPos, constValue, offset,
                operator);
    }

    public static SensorFlowSetStateValueWithOpStateInstruction setStateValueWithOpState(int stateOperand1Pos,
                                                                                         int stateOperand2Pos,
                                                                                         int stateResultPos, int offset,
                                                                                         Operator operator) {
        return new SensorFlowSetStateValueWithOpStateInstruction(stateOperand1Pos, stateOperand2Pos, stateResultPos,
                offset, operator);
    }

    public static SensorFlowSetPacketValueConstInstruction setPacketValueConst(int packetPos, int val) {
        return new SensorFlowSetPacketValueConstInstruction(packetPos, val);
    }

    public static SensorFlowReMatchPacketInstruction reMatchPacket() {
        return new SensorFlowReMatchPacketInstruction();
    }
}
