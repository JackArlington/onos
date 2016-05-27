package org.onosproject.net.sensorflow;

import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.sensor.SensorNodeAddress;
import org.onosproject.net.sensorflow.SensorFlowCriterion.SensorNodeCriterionMatchType;
import org.onosproject.net.sensorflow.SensorFlowCriterion.SensorNodeFields;

import static org.onosproject.net.sensorpacket.SensorPacketTypeRegistry.SensorPacketType;

/**
 * Created by aca on 3/2/15.
 */
public interface SensorTrafficSelector extends TrafficSelector {
    public SensorFlowCriterion getSdnWiseCriterion(SensorFlowCriterion.SensorFlowCriterionType type);

    interface Builder extends TrafficSelector.Builder {
        SensorTrafficSelector.Builder add(SensorFlowCriterion sensorFlowCriterion);

        SensorTrafficSelector.Builder matchPacketFields(SensorNodeFields field1, SensorNodeFields field2);

        SensorTrafficSelector.Builder matchPacketFields(SensorNodeFields field1, SensorNodeFields field2,
                                                         SensorNodeCriterionMatchType matchType);

        SensorTrafficSelector.Builder matchPacketFields(int pos1, int pos2,
                                                        SensorNodeCriterionMatchType matchType);

        SensorTrafficSelector.Builder matchPacketFieldWithConst(int packetPos, int value, int offset,
                                                        SensorNodeCriterionMatchType matchType);

        SensorTrafficSelector.Builder matchStateConst(int beginPos, int endPos, int value,
                                                      SensorNodeCriterionMatchType matchType);

        SensorTrafficSelector.Builder matchStateState(int operand1Pos, int operand2Pos, int offset,
                                                      SensorNodeCriterionMatchType matchType);

        SensorTrafficSelector.Builder matchNodeSrcAddr(SensorNodeAddress addr);

        SensorTrafficSelector.Builder matchNodeSrcAddr(SensorNodeAddress addr,
                                                        SensorNodeCriterionMatchType matchType);

        SensorTrafficSelector.Builder matchNodeDstAddr(SensorNodeAddress addr);

        SensorTrafficSelector.Builder matchNodeDstAddr(SensorNodeAddress addr,
                                                        SensorNodeCriterionMatchType matchType);

        SensorTrafficSelector.Builder matchSensorPacketType(SensorPacketType packetType);

        SensorTrafficSelector.Builder matchSensorPacketType(SensorNodeCriterionMatchType matchType,
                                                            SensorPacketType packetType);

        SensorTrafficSelector.Builder matchSensorNodeMutlicastPrevHop(SensorNodeAddress prevNode);

        SensorTrafficSelector.Builder matchSensorNodeMutlicastPrevHop(SensorNodeAddress prevNode,
                                                                       SensorNodeCriterionMatchType matchType);

        SensorTrafficSelector.Builder matchSensorNodeMutlicastCurHop(SensorNodeAddress curNode);

        SensorTrafficSelector.Builder matchSensorNodeMutlicastCurHop(SensorNodeAddress curNode,
                                                                      SensorNodeCriterionMatchType matchType);
    }
}
