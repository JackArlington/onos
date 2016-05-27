package org.onosproject.net.sensorflow;

import com.google.common.collect.ImmutableSet;
import org.onlab.packet.IpPrefix;
import org.onlab.packet.MacAddress;
import org.onlab.packet.VlanId;
import org.onosproject.net.PortNumber;
import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.criteria.Criterion;
import org.onosproject.net.sensor.SensorNodeAddress;
import org.onosproject.net.sensorflow.SensorFlowCriterion.SensorNodeCriterionMatchType;
import org.onosproject.net.sensorflow.SensorFlowCriterion.SensorNodeFields;
import org.onosproject.net.sensorpacket.SensorPacketTypeRegistry.SensorPacketType;

import java.util.HashSet;
import java.util.Set;

import static org.onosproject.net.sensorflow.SensorFlowCriteria.matchSensorNodeDstAddrCriterion;
import static org.onosproject.net.sensorflow.SensorFlowCriteria.matchSensorNodeFieldConstCriterion;
import static org.onosproject.net.sensorflow.SensorFlowCriteria.matchSensorNodeFieldsGenericCriterion;
import static org.onosproject.net.sensorflow.SensorFlowCriteria.matchSensorNodeMutlicastCurHopCriterior;
import static org.onosproject.net.sensorflow.SensorFlowCriteria.matchSensorNodeMutlicastPrevHopCriterior;
import static org.onosproject.net.sensorflow.SensorFlowCriteria.matchSensorNodePacketFieldsCriterion;
import static org.onosproject.net.sensorflow.SensorFlowCriteria.matchSensorNodeSrcAddrCriterion;
import static org.onosproject.net.sensorflow.SensorFlowCriteria.matchSensorNodeStateConstCriterion;
import static org.onosproject.net.sensorflow.SensorFlowCriteria.matchSensorNodeStateStateCriterion;
import static org.onosproject.net.sensorflow.SensorFlowCriteria.matchSensorPacketTypeCriterion;

/**
 * Created by aca on 3/2/15.
 */
public final class SensorEnabledTrafficSelector implements SensorTrafficSelector {
    private Set<Criterion> criteria;

    private SensorEnabledTrafficSelector(Set<Criterion> criteria) {
        this.criteria = ImmutableSet.copyOf(criteria);
    }

    @Override
    public Set<Criterion> criteria() {
        return this.criteria;
    }

    @Override
    public Criterion getCriterion(Criterion.Type type) {
        if (criteria != null) {
            for (Criterion criterion : criteria) {
                if ((criterion.type() != null) && (criterion.type() == type)) {
                    return criterion;
                }
            }
        }
        return null;
    }

    public static SensorTrafficSelector.Builder builder() {
        return new Builder(DefaultTrafficSelector.builder());
    }

    @Override
    public SensorFlowCriterion getSdnWiseCriterion(SensorFlowCriterion.SensorFlowCriterionType type) {
        if (criteria != null) {
            for (Criterion criterion : criteria) {
                if (criterion.type() == null) {
                    SensorFlowCriterion sensorFlowCriterion = (SensorFlowCriterion) criterion;
                    if ((sensorFlowCriterion.type() != null) && (sensorFlowCriterion.sdnWiseType() == type)) {
                        return sensorFlowCriterion;
                    }
                }
            }
        }
        return null;
    }

    public static final class Builder implements SensorTrafficSelector.Builder {
        private Set<Criterion> criteria = new HashSet<>();
        private TrafficSelector.Builder trafficSelectorBuilder;

        private Builder(TrafficSelector.Builder trafficSelectorBuilder) {
            this.trafficSelectorBuilder = trafficSelectorBuilder;
        }

        @Override
        public TrafficSelector.Builder add(Criterion criterion) {
            criteria.add(criterion);
            return this;
        }

        @Override
        public TrafficSelector.Builder matchInport(PortNumber portNumber) {
            return this.trafficSelectorBuilder.matchInport(portNumber);
        }

        @Override
        public TrafficSelector.Builder matchEthSrc(MacAddress macAddress) {
            return this.trafficSelectorBuilder.matchEthSrc(macAddress);
        }

        @Override
        public TrafficSelector.Builder matchEthDst(MacAddress macAddress) {
            return this.trafficSelectorBuilder.matchEthDst(macAddress);
        }

        @Override
        public TrafficSelector.Builder matchEthType(short i) {
            return this.trafficSelectorBuilder.matchEthType(i);
        }

        @Override
        public TrafficSelector.Builder matchVlanId(VlanId vlanId) {
            return this.trafficSelectorBuilder.matchVlanId(vlanId);
        }

        @Override
        public TrafficSelector.Builder matchVlanPcp(Byte aByte) {
            return this.trafficSelectorBuilder.matchVlanPcp(aByte);
        }

        @Override
        public TrafficSelector.Builder matchIPProtocol(Byte aByte) {
            return this.trafficSelectorBuilder.matchIPProtocol(aByte);
        }

        @Override
        public TrafficSelector.Builder matchIPSrc(IpPrefix ipPrefix) {
            return this.trafficSelectorBuilder.matchIPSrc(ipPrefix);
        }

        @Override
        public TrafficSelector.Builder matchIPDst(IpPrefix ipPrefix) {
            return this.trafficSelectorBuilder.matchIPDst(ipPrefix);
        }

        @Override
        public TrafficSelector.Builder matchTcpSrc(Short aShort) {
            return this.trafficSelectorBuilder.matchTcpSrc(aShort);
        }

        @Override
        public TrafficSelector.Builder matchTcpDst(Short aShort) {
            return this.trafficSelectorBuilder.matchTcpDst(aShort);
        }

        @Override
        public TrafficSelector.Builder matchMplsLabel(Integer integer) {
            return this.trafficSelectorBuilder.matchMplsLabel(integer);
        }

        @Override
        public TrafficSelector.Builder matchLambda(Short aShort) {
            return this.trafficSelectorBuilder.matchLambda(aShort);
        }

        @Override
        public TrafficSelector.Builder matchOpticalSignalType(Short aShort) {
            return this.trafficSelectorBuilder.matchOpticalSignalType(aShort);
        }

        @Override
        public SensorTrafficSelector build() {
            return new SensorEnabledTrafficSelector(criteria);
        }

        @Override
        public SensorTrafficSelector.Builder add(SensorFlowCriterion sensorFlowCriterion) {
            criteria.add(sensorFlowCriterion);
            return this;
        }

        @Override
        public SensorTrafficSelector.Builder matchPacketFields(SensorNodeFields field1,
                                                                SensorNodeFields field2) {
            return add(matchSensorNodePacketFieldsCriterion(field1, field2));
        }

        @Override
        public SensorTrafficSelector.Builder matchPacketFields(SensorNodeFields field1, SensorNodeFields field2,
                                                                SensorNodeCriterionMatchType matchType) {
            return add(matchSensorNodePacketFieldsCriterion(field1, field2, matchType));
        }

        @Override
        public SensorTrafficSelector.Builder matchPacketFields(int pos1, int pos2, SensorNodeCriterionMatchType
                matchType) {
            return add(matchSensorNodeFieldsGenericCriterion(pos1, pos2, matchType));
        }

        @Override
        public SensorTrafficSelector.Builder matchPacketFieldWithConst(int packetPos, int value, int offset,
                                                                      SensorNodeCriterionMatchType matchType) {
            return add(matchSensorNodeFieldConstCriterion(packetPos, value, offset, matchType));
        }

        @Override
        public SensorTrafficSelector.Builder matchStateConst(int beginPos, int endPos, int value,
                                                             SensorNodeCriterionMatchType matchType) {
            return add(matchSensorNodeStateConstCriterion(beginPos, endPos, value, matchType));
        }

        @Override
        public SensorTrafficSelector.Builder matchStateState(int operand1Pos, int operand2Pos, int offset,
                                                             SensorNodeCriterionMatchType matchType) {
            return add(matchSensorNodeStateStateCriterion(operand1Pos, operand2Pos, offset, matchType));
        }

        @Override
        public SensorTrafficSelector.Builder matchNodeSrcAddr(SensorNodeAddress addr) {
            return add(matchSensorNodeSrcAddrCriterion(addr));
        }

        @Override
        public SensorTrafficSelector.Builder matchNodeSrcAddr(SensorNodeAddress addr,
                                                               SensorNodeCriterionMatchType matchType) {
            return add(matchSensorNodeSrcAddrCriterion(addr, matchType));
        }

        @Override
        public SensorTrafficSelector.Builder matchNodeDstAddr(SensorNodeAddress addr) {
            return add(matchSensorNodeDstAddrCriterion(addr));
        }

        @Override
        public SensorTrafficSelector.Builder matchNodeDstAddr(SensorNodeAddress addr,
                                                               SensorNodeCriterionMatchType matchType) {
            return add(matchSensorNodeDstAddrCriterion(addr, matchType));
        }

        @Override
        public SensorTrafficSelector.Builder matchSensorPacketType(SensorPacketType packetType) {
            return add(matchSensorPacketTypeCriterion(packetType));
        }

        @Override
        public SensorTrafficSelector.Builder matchSensorPacketType(SensorNodeCriterionMatchType matchType,
                                                                   SensorPacketType packetType) {
            return add(matchSensorPacketTypeCriterion(matchType, packetType));
        }

        @Override
        public SensorTrafficSelector.Builder matchSensorNodeMutlicastPrevHop(SensorNodeAddress prevNode) {
            return add(matchSensorNodeMutlicastPrevHopCriterior(prevNode));
        }

        @Override
        public SensorTrafficSelector.Builder matchSensorNodeMutlicastPrevHop(SensorNodeAddress prevNode,
                                                                              SensorNodeCriterionMatchType matchType) {
            return add(matchSensorNodeMutlicastPrevHopCriterior(prevNode, matchType));
        }

        @Override
        public SensorTrafficSelector.Builder matchSensorNodeMutlicastCurHop(SensorNodeAddress curNode) {
            return add(matchSensorNodeMutlicastCurHopCriterior(curNode));
        }

        @Override
        public SensorTrafficSelector.Builder matchSensorNodeMutlicastCurHop(SensorNodeAddress curNode,
                                                                             SensorNodeCriterionMatchType matchType) {
            return add(matchSensorNodeMutlicastCurHopCriterior(curNode, matchType));
        }


    }
}
