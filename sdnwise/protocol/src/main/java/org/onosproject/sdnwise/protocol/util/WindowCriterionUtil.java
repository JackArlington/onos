package org.onosproject.sdnwise.protocol.util;

import com.github.sdnwiselab.sdnwise.flowtable.Window;
import com.github.sdnwiselab.sdnwise.util.NodeAddress;
import org.onosproject.net.flow.criteria.Criterion;
import org.onosproject.net.sensorflow.SensorFlowCriteria;
import org.onosproject.net.sensorflow.SensorFlowCriteria.SensorNodeSrcAddrCriterion;
import org.onosproject.net.sensorflow.SensorFlowCriterion;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.github.sdnwiselab.sdnwise.flowtable.FlowTableInterface.*;
import static com.github.sdnwiselab.sdnwise.flowtable.Window.W_SIZE_1;
import static com.github.sdnwiselab.sdnwise.flowtable.Window.W_SIZE_2;
import static com.github.sdnwiselab.sdnwise.packet.NetworkPacket.*;

/**
 * Created by aca on 9/26/15.
 */
public class WindowCriterionUtil {
    public List<Window> getWindow(Criterion criterion) {
        List<Window> flowTableWindows = new ArrayList<>();
        Window flowTableWindow;
        SensorFlowCriterion sensorFlowCriterion = (SensorFlowCriterion) criterion;
        switch (sensorFlowCriterion.sdnWiseType()) {
            case STATE_CONST:
                SensorFlowCriteria.SensorNodeStateConstCriterion stateFieldsCriterion =
                        (SensorFlowCriteria.SensorNodeStateConstCriterion) criterion;
                int windowSize = stateFieldsCriterion.endPosition() - stateFieldsCriterion.beginPosition();
                flowTableWindow = new Window();
                flowTableWindow.setOperator(translateWindowOperator(stateFieldsCriterion.matchType()))
                        .setSize(translateWindowSize(windowSize))
                        .setLhsLocation(STATUS)
                        .setLhs(stateFieldsCriterion.beginPosition())
                        .setRhsLocation(CONST)
                        .setRhs(stateFieldsCriterion.value());

                flowTableWindows.add(flowTableWindow);
                break;
            case PACKET_FIELD_CONST:
                SensorFlowCriteria.SensorNodePacketFieldConstCriterion packetFieldConstCriterion =
                        (SensorFlowCriteria.SensorNodePacketFieldConstCriterion) criterion;
                int value = packetFieldConstCriterion.constValue();
                flowTableWindow = new Window();
                flowTableWindow.setOperator(translateWindowOperator(packetFieldConstCriterion.matchType()))
                        .setSize(translateWindowSize(packetFieldConstCriterion.offset()))
                        .setLhsLocation(PACKET)
                        .setLhs(packetFieldConstCriterion.packetPos())
                        .setRhsLocation(CONST)
                        .setRhs(value);

                flowTableWindows.add(flowTableWindow);
                break;
            case PACKET_TYPE:
                SensorFlowCriteria.SensorPacketTypeCriterion packetTypeCriterion =
                        (SensorFlowCriteria.SensorPacketTypeCriterion) criterion;
                flowTableWindow = new Window();
                flowTableWindow.setOperator(translateWindowOperator(packetTypeCriterion.matchType()))
                        .setSize(W_SIZE_1)
                        .setLhsLocation(PACKET)
                        .setLhs(TYP_INDEX)
                        .setRhsLocation(CONST)
                        .setRhs(packetTypeCriterion.getPacketType().originalId());

                flowTableWindows.add(flowTableWindow);
                break;
//            case MULTICAST_PREV_NODE:
//                SensorFlowCriteria.SensorNodeMutlicastPrevHopCriterior mutlicastPrevHopCriterior =
//                        (SensorFlowCriteria.SensorNodeMutlicastPrevHopCriterior) criterion;
//                flowTableWindow = new Window();
//                flowTableWindow.setOperator(translateWindowOperator(mutlicastPrevHopCriterior.matchType()))
//                        .setSize(SDN_WISE_SIZE_2)
//                        .setLhsLocation(SDN_WISE_PACKET)
//                        .setLhs(14)
//                        .setRhsLocation(SDN_WISE_CONST)
//                        .setRhs((new NodeAddress(super.getDestination().address())).intValue());
//
//                break;
            case SRC_NODE_ADDR:
                SensorNodeSrcAddrCriterion srcAddrCriterion = (SensorNodeSrcAddrCriterion) criterion;
                byte[] srcAddr = srcAddrCriterion.srcAddress().getAddr();

                flowTableWindow = new Window();
                flowTableWindow.setOperator(translateWindowOperator(srcAddrCriterion.matchType()))
                        .setSize(W_SIZE_2)
                        .setLhsLocation(PACKET)
                        .setLhs(SRC_INDEX)
                        .setRhsLocation(CONST)
                        .setRhs(ByteBuffer.wrap(srcAddr).getShort());

                flowTableWindows.add(flowTableWindow);

//                flowTableWindow = new Window();
//                flowTableWindow.setOperator(translateWindowOperator(srcAddrCriterion.matchType()))
//                        .setSize(SDN_WISE_SIZE_1)
//                        .setLhsLocation(SDN_WISE_PACKET)
//                        .setLhs(SDN_WISE_SRC_L)
//                        .setRhsLocation(SDN_WISE_CONST)
//                        .setRhs(srcAddr[1]);
//
//                flowTableWindows.add(flowTableWindow);
                break;
            case DEST_NODE_ADDR:
                SensorFlowCriteria.SensorNodeDstAddrCriterion dstAddrCriterion =
                        (SensorFlowCriteria.SensorNodeDstAddrCriterion) criterion;
                byte[] dstAddr = dstAddrCriterion.dstAddress().getAddr();
                flowTableWindow = new Window();
                flowTableWindow.setOperator(translateWindowOperator(dstAddrCriterion.matchType()))
                        .setSize(W_SIZE_2)
                        .setLhsLocation(PACKET)
                        .setLhs(DST_INDEX)
                        .setRhsLocation(CONST)
                        .setRhs(ByteBuffer.wrap(dstAddr).getShort());

                flowTableWindows.add(flowTableWindow);

//                flowTableWindow = new Window();
//                flowTableWindow.setOperator(translateWindowOperator(dstAddrCriterion.matchType()))
//                        .setSize(SDN_WISE_SIZE_1)
//                        .setLhsLocation(SDN_WISE_PACKET)
//                        .setLhs(SDN_WISE_DST_L)
//                        .setRhsLocation(SDN_WISE_CONST)
//                        .setRhs(dstAddr[1]);
//
//                flowTableWindows.add(flowTableWindow);
                break;
            case MULTICAST_CUR_NODE:
                SensorFlowCriteria.SensorNodeMulticastCurHopCriterion curHopCriterion =
                        (SensorFlowCriteria.SensorNodeMulticastCurHopCriterion) criterion;
                flowTableWindow = new Window();
                flowTableWindow.setOperator(translateWindowOperator(curHopCriterion.matchType()))
                        .setSize(W_SIZE_2)
                        .setLhsLocation(PACKET)
                        .setLhs(16)
                        .setRhsLocation(CONST)
                        .setRhs((new NodeAddress(curHopCriterion.curNode().getAddr())).intValue());
                flowTableWindows.add(flowTableWindow);
                break;
//                    case PACKET_FIELDS:
//                        SensorNodePacketFieldsCriterion fieldsCriterion = (SensorNodePacketFieldsCriterion) criterion;
//                        flowTableWindow = new FlowTableWindow();
//                        flowTableWindow.setOperator(translateWindowOperator(fieldsCriterion.matchType()))
//                                .setLocation(SDN_WISE_PACKET)
//
//                        break;
            default:
                break;
        }

        return flowTableWindows;
    }

    private byte translateWindowOperator(SensorFlowCriterion.SensorNodeCriterionMatchType matchType) {
        switch (matchType) {
            case EQUAL:
                return Window.EQUAL;
            case GREATER_EQUAL:
                return Window.GREATER_OR_EQUAL;
            case GREATER_THAN:
                return Window.GREATER;
            case LESS_EQUAL:
                return Window.LESS_OR_EQUAL;
            case LESS_THAN:
                return Window.LESS;
            case NOT_EQUAL:
                return Window.NOT_EQUAL;
            default:
                return -1;
        }
    }

    private byte translateWindowSize(int size) {
        switch (size) {
            case 1:
                return Window.W_SIZE_1;
            case 2:
                return Window.W_SIZE_2;
            default:
                return 0;
        }
    }
}
