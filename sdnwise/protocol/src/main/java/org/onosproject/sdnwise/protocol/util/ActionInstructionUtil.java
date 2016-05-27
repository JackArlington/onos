package org.onosproject.sdnwise.protocol.util;

import com.github.sdnwiselab.sdnwise.flowtable.AbstractAction;
import com.github.sdnwiselab.sdnwise.flowtable.AskAction;
import com.github.sdnwiselab.sdnwise.flowtable.DropAction;
import com.github.sdnwiselab.sdnwise.flowtable.FunctionAction;
import com.github.sdnwiselab.sdnwise.flowtable.MatchAction;
import com.github.sdnwiselab.sdnwise.flowtable.SetAction;
import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import org.onosproject.net.sensorflow.SensorFlowForwardUpInstruction;
import org.onosproject.net.sensorflow.SensorFlowInstruction;
import org.onosproject.net.sensorflow.SensorFlowSetDstAddrInstruction;
import org.onosproject.net.sensorflow.SensorFlowSetPacketLengthInstruction;
import org.onosproject.net.sensorflow.SensorFlowSetPacketValueConstInstruction;
import org.onosproject.net.sensorflow.SensorFlowSetStateValueConstInstruction;
import org.onosproject.net.sensorflow.SensorFlowSetStateValuePacketInstruction;
import org.onosproject.net.sensorflow.SensorFlowSetStateValueWithOpConstInstruction;
import org.onosproject.net.sensorflow.SensorFlowSetStateValueWithOpPacketInstruction;
import org.onosproject.net.sensorflow.SensorFlowSetStateValueWithOpStateInstruction;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.github.sdnwiselab.sdnwise.flowtable.FlowTableInterface.*;
import static com.github.sdnwiselab.sdnwise.flowtable.SetAction.ADD;

/**
 * Created by aca on 9/26/15.
 */
public class ActionInstructionUtil {
    public List<AbstractAction> getAction(SensorFlowInstruction instruction) {
        List<AbstractAction> actionSet = new ArrayList<>();
        switch (instruction.getSensorFlowInstructionType()) {
            case DROP:
                DropAction actionDrop = new DropAction();

                actionSet.add(actionDrop);
                break;
            case SET_DST_ADDR:
                SensorFlowSetDstAddrInstruction setDstAddrInstruction =
                        (SensorFlowSetDstAddrInstruction) instruction;

                byte[] addr = setDstAddrInstruction.getDstAddr().getAddr();
                SetAction setPktDstAddrHAction = new SetAction();
                setPktDstAddrHAction.setLhsLocation(CONST);
                setPktDstAddrHAction.setLhs(0);
                setPktDstAddrHAction.setRhsLocation(CONST);
                setPktDstAddrHAction.setRhs(addr[0]);
                setPktDstAddrHAction.setResLocation(PACKET);
                setPktDstAddrHAction.setRes(NetworkPacket.DST_INDEX);

                SetAction setPktDstAddrLAction = new SetAction();
                setPktDstAddrLAction.setLhsLocation(CONST);
                setPktDstAddrLAction.setLhs(0);
                setPktDstAddrLAction.setRhsLocation(CONST);
                setPktDstAddrLAction.setRhs(addr[1]);
                setPktDstAddrLAction.setResLocation(PACKET);
                setPktDstAddrLAction.setRes(NetworkPacket.DST_INDEX + 1);

                actionSet.add(setPktDstAddrHAction);
                actionSet.add(setPktDstAddrLAction);
                break;
            case SET_PKT_LEN:
                SensorFlowSetPacketLengthInstruction setPacketLengthInstruction =
                        (SensorFlowSetPacketLengthInstruction) instruction;

                SetAction setPktLenAction = new SetAction();
                setPktLenAction.setLhsLocation(CONST);
                setPktLenAction.setLhs(setPacketLengthInstruction.length());
                setPktLenAction.setResLocation(PACKET);
                setPktLenAction.setRes(NetworkPacket.LEN_INDEX);

                actionSet.add(setPktLenAction);
                break;
            case SET_STATE_VALUE_CONST:
                SensorFlowSetStateValueConstInstruction setStateValueInstruction =
                        (SensorFlowSetStateValueConstInstruction) instruction;

                SetAction setStateValueAction = new SetAction();
                setStateValueAction.setLhsLocation(CONST);
                setStateValueAction.setLhs(0);
                setStateValueAction.setRhsLocation(CONST);
                setStateValueAction.setRhs(setStateValueInstruction.value());
                setStateValueAction.setOperator(ADD);
                setStateValueAction.setResLocation(STATUS);
                setStateValueAction.setRes(setStateValueInstruction.beginPosition());

                actionSet.add(setStateValueAction);
                break;
            case ASK_CONTROLLER:
                actionSet.add(new AskAction());
                break;
            case REMATCH_PACKET:
                actionSet.add(new MatchAction());
                break;
            case SET_PKT_VAL_CONST:
                SensorFlowSetPacketValueConstInstruction setPacketValueConstInstruction =
                        (SensorFlowSetPacketValueConstInstruction) instruction;

                SetAction setPktValueConst = new SetAction();
                setPktValueConst.setLhsLocation(CONST);
                setPktValueConst.setLhs(0);
                setPktValueConst.setOperator(ADD);
                setPktValueConst.setRhsLocation(CONST);
                setPktValueConst.setRhs(setPacketValueConstInstruction.value());
                setPktValueConst.setResLocation(PACKET);
                setPktValueConst.setRes(setPacketValueConstInstruction.packetPosition());

                actionSet.add(setPktValueConst);
                break;
            case SET_STATE_VALUE_PACKET:
                SensorFlowSetStateValuePacketInstruction setStateValuePacketInstruction =
                        (SensorFlowSetStateValuePacketInstruction) instruction;

                SetAction setStateValuePacketAction = new SetAction();
                setStateValuePacketAction.setLhsLocation(CONST);
                setStateValuePacketAction.setLhs(0);
                setStateValuePacketAction.setRhsLocation(PACKET);
                setStateValuePacketAction.setRhs(setStateValuePacketInstruction.packetPosition());
                setStateValuePacketAction.setOperator(ADD);
                setStateValuePacketAction.setResLocation(STATUS);
                setStateValuePacketAction.setRes(setStateValuePacketInstruction.beginPosition());

                actionSet.add(setStateValuePacketAction);
                break;
            case STATE_CONST_OP:
                SensorFlowSetStateValueWithOpConstInstruction setStateValueWithOpConstInstruction =
                        (SensorFlowSetStateValueWithOpConstInstruction) instruction;

                SetAction setStateValueOpConstAction = new SetAction();
                setStateValueOpConstAction.setLhsLocation(CONST);
                setStateValueOpConstAction.setLhs(setStateValueWithOpConstInstruction.constValue());
                setStateValueOpConstAction.setOperator(
                        translateOperator(setStateValueWithOpConstInstruction.getOperator()));
                setStateValueOpConstAction.setRhsLocation(STATUS);
                setStateValueOpConstAction.setRhs(setStateValueWithOpConstInstruction.stateOperandPos());
                setStateValueOpConstAction.setResLocation(STATUS);
                setStateValueOpConstAction.setRes(setStateValueWithOpConstInstruction.stateResultPos());

                actionSet.add(setStateValueOpConstAction);
                break;
            case STATE_PACKET_OP:
                SensorFlowSetStateValueWithOpPacketInstruction setStateValueWithOpPacketInstruction =
                        (SensorFlowSetStateValueWithOpPacketInstruction) instruction;

                SetAction setStateValueOpPacketAction = new SetAction();
                setStateValueOpPacketAction.setLhsLocation(STATUS);
                setStateValueOpPacketAction.setLhs(setStateValueWithOpPacketInstruction.stateOperandPos());
                setStateValueOpPacketAction.setOperator(
                        translateOperator(setStateValueWithOpPacketInstruction.getOperator()));
                setStateValueOpPacketAction.setRhsLocation(PACKET);
                setStateValueOpPacketAction.setRhs(setStateValueWithOpPacketInstruction.packetPos());
                setStateValueOpPacketAction.setResLocation(STATUS);
                setStateValueOpPacketAction.setRes(setStateValueWithOpPacketInstruction.stateResultPos());

                actionSet.add(setStateValueOpPacketAction);
                break;
            case STATE_STATE_OP:
                SensorFlowSetStateValueWithOpStateInstruction setStateValueWithOpStateInstruction =
                        (SensorFlowSetStateValueWithOpStateInstruction) instruction;

                SetAction setStateValueOpStateAction = new SetAction();
                setStateValueOpStateAction.setLhsLocation(STATUS);
                setStateValueOpStateAction.setLhs(setStateValueWithOpStateInstruction.stateOperand1Pos());
                setStateValueOpStateAction.setOperator(
                        translateOperator(setStateValueWithOpStateInstruction.getOperator()));
                setStateValueOpStateAction.setRhsLocation(STATUS);
                setStateValueOpStateAction.setRhs(setStateValueWithOpStateInstruction.stateOperand2Pos());
                setStateValueOpStateAction.setResLocation(STATUS);
                setStateValueOpStateAction.setRes(setStateValueWithOpStateInstruction.stateResultPos());

                actionSet.add(setStateValueOpStateAction);
                break;
            case FORWARD_UP:
                SensorFlowForwardUpInstruction forwardUpInstruction =
                        (SensorFlowForwardUpInstruction) instruction;
                int[] args = forwardUpInstruction.getArgs();

                FunctionAction functionAction = new FunctionAction("FUNCTION 1 9 8 7 6 5 4 3 2 1");
                functionAction.setId(forwardUpInstruction.getFunctionId());
                if (args != null) {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(args.length * 4);
                    IntBuffer intBuffer = byteBuffer.asIntBuffer();
                    intBuffer.put(args);

                    functionAction.setArgs(byteBuffer.array());
                }

                actionSet.add(functionAction);
                break;
            default:
                break;
        }
        return actionSet;
    }

    private byte translateOperator(SensorFlowInstruction.Operator operator) {
        switch (operator) {
            case ADD:
                return SetAction.ADD;
            case AND:
                return SetAction.AND;
            case MULTIPLY:
                return SetAction.MUL;
            case OR:
                return SetAction.OR;
            case XOR:
                return SetAction.XOR;
            default:
                return -1;
        }
    }
}
