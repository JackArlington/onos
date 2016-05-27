package org.onosproject.net.sensorflow;

import com.google.common.base.MoreObjects;
import org.onosproject.net.flow.criteria.Criteria;
import org.onosproject.net.sensor.SensorNodeAddress;
import org.onosproject.net.sensorflow.SensorFlowCriterion.SensorNodeCriterionMatchType;
import org.onosproject.net.sensorflow.SensorFlowCriterion.SensorNodeFields;

import java.util.Arrays;

import static com.google.common.base.MoreObjects.toStringHelper;
import static org.onosproject.net.sensorflow.SensorFlowCriterion.SensorFlowCriterionType.PACKET_FIELDS_ANY;
import static org.onosproject.net.sensorflow.SensorFlowCriterion.SensorFlowCriterionType.PACKET_FIELD_CONST;
import static org.onosproject.net.sensorflow.SensorFlowCriterion.SensorFlowCriterionType.STATE_CONST;
import static org.onosproject.net.sensorflow.SensorFlowCriterion.SensorFlowCriterionType.STATE_STATE;
import static org.onosproject.net.sensorpacket.SensorPacketTypeRegistry.SensorPacketType;

/**
 * Created by aca on 3/2/15.
 */
public class SensorFlowCriteria {
    private Criteria criteria;

    public SensorFlowCriteria() {

    }

    public SensorFlowCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public Criteria getCriteria() {
        return this.criteria;
    }

    public static SensorFlowCriterion matchSDNWiseBasicCriterion(int size, boolean usePreviousState,
                                                              int address, byte[] value) {
        return new SensorFlowBasicCriterion(size, usePreviousState, address, value);
    }

    public static SensorFlowCriterion matchSensorNodeSrcAddrCriterion(SensorNodeAddress addr) {
        return new SensorNodeSrcAddrCriterion(addr);
    }

    public static SensorFlowCriterion matchSensorNodeSrcAddrCriterion(SensorNodeAddress addr,
                                                                   SensorNodeCriterionMatchType matchType) {
        return new SensorNodeSrcAddrCriterion(addr, matchType);
    }

    public static SensorFlowCriterion matchSensorNodeDstAddrCriterion(SensorNodeAddress addr) {
        return new SensorNodeDstAddrCriterion(addr);
    }

    public static SensorFlowCriterion matchSensorNodeDstAddrCriterion(SensorNodeAddress addr,
                                                                   SensorNodeCriterionMatchType matchType) {
        return new SensorNodeDstAddrCriterion(addr, matchType);
    }

    public static SensorFlowCriterion matchSensorPacketTypeCriterion(SensorPacketType packetType) {
        return new SensorPacketTypeCriterion(packetType);
    }

    public static SensorFlowCriterion matchSensorPacketTypeCriterion(SensorNodeCriterionMatchType matchType,
                                                                     SensorPacketType packetType) {
        return new SensorPacketTypeCriterion(matchType, packetType);
    }

    public static SensorFlowCriterion matchSensorNodeMutlicastPrevHopCriterior(SensorNodeAddress prevNode) {
        return new SensorNodeMutlicastPrevHopCriterior(prevNode);
    }

    public static SensorFlowCriterion matchSensorNodeMutlicastPrevHopCriterior(SensorNodeAddress prevNode,
                                                                            SensorNodeCriterionMatchType matchType) {
        return new SensorNodeMutlicastPrevHopCriterior(prevNode, matchType);
    }

    public static SensorFlowCriterion matchSensorNodeMutlicastCurHopCriterior(SensorNodeAddress curNode) {
        return new SensorNodeMulticastCurHopCriterion(curNode);
    }

    public static SensorFlowCriterion matchSensorNodeMutlicastCurHopCriterior(SensorNodeAddress curNode,
                                                                           SensorNodeCriterionMatchType matchType) {
        return new SensorNodeMulticastCurHopCriterion(curNode, matchType);
    }

    public static SensorFlowCriterion matchSensorNodePacketFieldsCriterion(SensorNodeFields field1,
                                                                        SensorNodeFields field2) {
        return new SensorNodePacketFieldsCriterion(field1, field2);
    }

    public static SensorFlowCriterion matchSensorNodePacketFieldsCriterion(SensorNodeFields field1,
                                                                        SensorNodeFields field2,
                                                                        SensorNodeCriterionMatchType matchType) {
        return new SensorNodePacketFieldsCriterion(field1, field2, matchType);
    }

    public static SensorFlowCriterion matchSensorNodeStateConstCriterion(int beginPos, int endPos, int value,
                                                                         SensorNodeCriterionMatchType matchType) {
        return new SensorNodeStateConstCriterion(beginPos, endPos, value, matchType);
    }

    public static SensorFlowCriterion matchSensorNodeFieldsGenericCriterion(int pos1, int pos2,
                                                                            SensorNodeCriterionMatchType matchType) {
        return new SensorNodePacketFieldsGenericCriterion(pos1, pos2, matchType);
    }

    public static SensorFlowCriterion matchSensorNodeFieldConstCriterion(int pos, int value, int offset,
                                                                         SensorNodeCriterionMatchType matchType) {
        return new SensorNodePacketFieldConstCriterion(pos, value, offset, matchType);
    }

    public static SensorFlowCriterion matchSensorNodeStateStateCriterion(int operand1Pos, int operand2Pos, int offset,
                                                                         SensorNodeCriterionMatchType matchType) {
        return new SensorNodeStateStateCriterion(operand1Pos, operand2Pos, offset, matchType);
    }

    public static final class SensorNodeStateStateCriterion implements SensorFlowCriterion {
        private int operand1Pos;
        private int operand2Pos;
        private int offset;
        private SensorNodeCriterionMatchType matchType;


        public SensorNodeStateStateCriterion(int operand1Pos, int operand2Pos, int offset,
                                             SensorNodeCriterionMatchType matchType) {
            this.operand1Pos = operand1Pos;
            this.operand2Pos = operand2Pos;
            this.offset = offset;
            this.matchType = matchType;
        }

        public int operand1Pos() {
            return operand1Pos;
        }

        public int operand2Pos() {
            return operand2Pos;
        }

        public int offset() {
            return offset;
        }

        @Override
        public SensorFlowCriterionType sdnWiseType() {
            return STATE_STATE;
        }

        @Override
        public SensorNodeCriterionMatchType matchType() {
            return matchType;
        }

        @Override
        public Type type() {
            return null;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(SensorNodeStateStateCriterion.class)
                    .add("operand1Pos", operand1Pos)
                    .add("operand2Pos", operand2Pos)
                    .add("offset", offset)
                    .add("matchType", matchType.name())
                    .toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SensorNodeStateStateCriterion)) {
                return false;
            }

            SensorNodeStateStateCriterion that = (SensorNodeStateStateCriterion) o;

            if (operand1Pos != that.operand1Pos) {
                return false;
            }
            if (operand2Pos != that.operand2Pos) {
                return false;
            }
            if (offset != that.offset) {
                return false;
            }
            return matchType == that.matchType;

        }

        @Override
        public int hashCode() {
            int result = operand1Pos;
            result = 31 * result + operand2Pos;
            result = 31 * result + offset;
            result = 31 * result + matchType.hashCode();
            return result;
        }
    }

    public static final class SensorNodeStateConstCriterion implements SensorFlowCriterion {
        private SensorNodeCriterionMatchType matchType;
        private int beginPos;
        private int endPos;
        private int value;

        public SensorNodeStateConstCriterion(int beginPos, int endPos, int value,
                                             SensorNodeCriterionMatchType matchType) {
            this.matchType = matchType;
            this.beginPos = beginPos;
            this.endPos = endPos;
            this.value = value;
        }

        public int beginPosition() {
            return beginPos;
        }

        public int endPosition() {
            return endPos;
        }

        public int value() {
            return value;
        }

        @Override
        public SensorFlowCriterionType sdnWiseType() {
            return STATE_CONST;
        }

        @Override
        public SensorNodeCriterionMatchType matchType() {
            return matchType;
        }

        @Override
        public Type type() {
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SensorNodeStateConstCriterion)) {
                return false;
            }

            SensorNodeStateConstCriterion that = (SensorNodeStateConstCriterion) o;

            if (beginPos != that.beginPos) {
                return false;
            }
            if (endPos != that.endPos) {
                return false;
            }
            if (value != that.value) {
                return false;
            }
            return matchType == that.matchType;

        }

        @Override
        public int hashCode() {
            int result = matchType.hashCode();
            result = 31 * result + beginPos;
            result = 31 * result + endPos;
            result = 31 * result + value;
            return result;
        }
    }

    public static final class SensorNodePacketFieldConstCriterion implements SensorFlowCriterion {
        private SensorNodeCriterionMatchType matchType = SensorNodeCriterionMatchType.EQUAL;
        private int pos;
        private int value;
        private int offset;

        public SensorNodePacketFieldConstCriterion(int packetPos, int value, int offset,
                                                   SensorNodeCriterionMatchType matchType) {
            this.pos = packetPos;
            this.matchType = matchType;
            this.value = value;
        }

        public int packetPos() {
            return pos;
        }

        public int constValue() {
            return value;
        }

        public int offset() {
            return offset;
        }

        @Override
        public SensorFlowCriterionType sdnWiseType() {
            return PACKET_FIELD_CONST;
        }

        @Override
        public SensorNodeCriterionMatchType matchType() {
            return matchType;
        }

        @Override
        public Type type() {
            return null;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(SensorNodePacketFieldConstCriterion.class)
                    .add("matchType", matchType.name())
                    .add("packetPos", pos)
                    .add("const", value)
                    .toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SensorNodePacketFieldConstCriterion)) {
                return false;
            }

            SensorNodePacketFieldConstCriterion that = (SensorNodePacketFieldConstCriterion) o;

            if (pos != that.pos) {
                return false;
            }
            if (value != that.value) {
                return false;
            }
            return matchType == that.matchType;

        }

        @Override
        public int hashCode() {
            int result = matchType.hashCode();
            result = 31 * result + pos;
            result = 31 * result + value;
            return result;
        }
    }

    public static final class SensorNodePacketFieldsGenericCriterion implements SensorFlowCriterion {
        private SensorNodeCriterionMatchType matchType = SensorNodeCriterionMatchType.EQUAL;
        private int pos1;
        private int pos2;

        public SensorNodePacketFieldsGenericCriterion(int pos1, int pos2, SensorNodeCriterionMatchType matchType) {
            this.pos1 = pos1;
            this.pos2 = pos2;
            this.matchType = matchType;
        }

        public int fieldAtPos1() {
            return pos1;
        }

        public int fieldAtPos2() {
            return pos2;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(SensorNodePacketFieldsGenericCriterion.class)
                    .add("matchType", matchType.name())
                    .add("fieldPos1", pos1)
                    .add("fieldPos2", pos2)
                    .toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SensorNodePacketFieldsGenericCriterion)) {
                return false;
            }

            SensorNodePacketFieldsGenericCriterion that = (SensorNodePacketFieldsGenericCriterion) o;

            if (pos1 != that.pos1) {
                return false;
            }
            if (pos2 != that.pos2) {
                return false;
            }
            return matchType == that.matchType;

        }

        @Override
        public int hashCode() {
            int result = matchType.hashCode();
            result = 31 * result + pos1;
            result = 31 * result + pos2;
            return result;
        }

        @Override
        public SensorFlowCriterionType sdnWiseType() {
            return PACKET_FIELDS_ANY;
        }

        @Override
        public SensorNodeCriterionMatchType matchType() {
            return matchType;
        }

        @Override
        public Type type() {
            return null;
        }
    }

    public static final class SensorNodePacketFieldsCriterion implements SensorFlowCriterion {
        private SensorNodeCriterionMatchType matchType = SensorNodeCriterionMatchType.EQUAL;
        private SensorNodeFields field1;
        private SensorNodeFields field2;

        public SensorNodePacketFieldsCriterion(SensorNodeFields field1, SensorNodeFields field2) {
            this.field1 = field1;
            this.field2 = field2;
        }

        public SensorNodePacketFieldsCriterion(SensorNodeFields field1, SensorNodeFields field2,
                                               SensorNodeCriterionMatchType matchType) {
            this.field1 = field1;
            this.field2 = field2;
            this.matchType = matchType;
        }

        public SensorNodeFields field1() {
            return field1;
        }

        public SensorNodeFields field2() {
            return field2;
        }

        @Override
        public SensorFlowCriterionType sdnWiseType() {
            return SensorFlowCriterionType.PACKET_FIELDS;
        }

        @Override
        public SensorNodeCriterionMatchType matchType() {
            return matchType;
        }

        @Override
        public Type type() {
            return null;
        }
    }

    public static final class SensorNodeSrcAddrCriterion implements SensorFlowCriterion {
        private SensorNodeAddress address;
        private SensorNodeCriterionMatchType matchType = SensorNodeCriterionMatchType.EQUAL;

        public SensorNodeSrcAddrCriterion(SensorNodeAddress addr) {
            this.address = addr;
        }

        public SensorNodeSrcAddrCriterion(SensorNodeAddress addr, SensorNodeCriterionMatchType matchType) {
            this.address = addr;
            this.matchType = matchType;
        }

        @Override
        public SensorFlowCriterionType sdnWiseType() {
            return SensorFlowCriterionType.SRC_NODE_ADDR;
        }

        @Override
        public SensorNodeCriterionMatchType matchType() {
            return matchType;
        }

        @Override
        public Type type() {
            return null;
        }

        public SensorNodeAddress srcAddress() {
            return address;
        }

        @Override
        public String toString() {
            return toStringHelper(sdnWiseType().toString())
                    .add("src", address.toString()).toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SensorNodeSrcAddrCriterion)) {
                return false;
            }

            SensorNodeSrcAddrCriterion that = (SensorNodeSrcAddrCriterion) o;

            if (!address.equals(that.address)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return address.hashCode();
        }
    }

    public static final class SensorNodeMutlicastPrevHopCriterior implements SensorFlowCriterion {
        private SensorNodeAddress prevNode;
        private SensorNodeCriterionMatchType matchType = SensorNodeCriterionMatchType.EQUAL;

        public SensorNodeMutlicastPrevHopCriterior(SensorNodeAddress prevNode) {
            this.prevNode = prevNode;
        }

        public SensorNodeMutlicastPrevHopCriterior(SensorNodeAddress prevNode, SensorNodeCriterionMatchType matchType) {
            this.prevNode = prevNode;
            this.matchType = matchType;
        }

        public SensorNodeAddress getPrevNode() {
            return prevNode;
        }

        @Override
        public SensorFlowCriterionType sdnWiseType() {
            return SensorFlowCriterionType.MULTICAST_PREV_NODE;
        }

        @Override
        public SensorNodeCriterionMatchType matchType() {
            return matchType;
        }

        @Override
        public Type type() {
            return null;
        }

        @Override
        public String toString() {
            return toStringHelper(sdnWiseType().toString())
                    .add("prev", prevNode.toString())
                    .toString();
        }
    }

    public static final class SensorNodeMulticastCurHopCriterion implements SensorFlowCriterion {
        private SensorNodeAddress curNode;
        SensorNodeCriterionMatchType matchType = SensorNodeCriterionMatchType.EQUAL;

        public SensorNodeMulticastCurHopCriterion(SensorNodeAddress curNode) {
            this.curNode = curNode;
        }

        public SensorNodeMulticastCurHopCriterion(SensorNodeAddress curNode, SensorNodeCriterionMatchType matchType) {
            this.curNode = curNode;
            this.matchType = matchType;
        }

        public SensorNodeAddress curNode() {
            return curNode;
        }

        @Override
        public SensorFlowCriterionType sdnWiseType() {
            return SensorFlowCriterionType.MULTICAST_CUR_NODE;
        }

        @Override
        public SensorNodeCriterionMatchType matchType() {
            return matchType;
        }

        @Override
        public Type type() {
            return null;
        }

        @Override
        public String toString() {
            return toStringHelper(sdnWiseType().toString())
                    .add("cur", curNode.toString())
                    .toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SensorNodeMulticastCurHopCriterion)) {
                return false;
            }

            SensorNodeMulticastCurHopCriterion that = (SensorNodeMulticastCurHopCriterion) o;

            return !(curNode != null ? !curNode.equals(that.curNode) : that.curNode != null);

        }

        @Override
        public int hashCode() {
            return curNode != null ? curNode.hashCode() : 0;
        }
    }

    public static final class SensorPacketTypeCriterion implements SensorFlowCriterion {
        private SensorPacketType packetType;
        private SensorNodeCriterionMatchType matchType = SensorNodeCriterionMatchType.EQUAL;

        public SensorPacketTypeCriterion(SensorPacketType packetType) {
            this.packetType = packetType;
        }

        public SensorPacketTypeCriterion(SensorNodeCriterionMatchType matchType, SensorPacketType packetType) {
            this.packetType = packetType;
            this.matchType = matchType;
        }

        public SensorPacketType getPacketType() {
            return packetType;
        }

        @Override
        public SensorFlowCriterionType sdnWiseType() {
            return SensorFlowCriterionType.PACKET_TYPE;
        }

        @Override
        public SensorNodeCriterionMatchType matchType() {
            return matchType;
        }

        @Override
        public Type type() {
            return null;
        }

        @Override
        public String toString() {
            return toStringHelper(sdnWiseType().toString())
                    .add("packetType", packetType.toString())
                    .toString();
        }
    }

    public static final class SensorNodeDstAddrCriterion implements SensorFlowCriterion {
        private SensorNodeAddress address;
        private SensorNodeCriterionMatchType matchType = SensorNodeCriterionMatchType.EQUAL;

        public SensorNodeDstAddrCriterion(SensorNodeAddress addr) {
            this.address = addr;
        }

        public SensorNodeDstAddrCriterion(SensorNodeAddress addr, SensorNodeCriterionMatchType matchType) {
            this.address = addr;
            this.matchType = matchType;
        }

        @Override
        public SensorFlowCriterionType sdnWiseType() {
            return SensorFlowCriterionType.DEST_NODE_ADDR;
        }

        @Override
        public SensorNodeCriterionMatchType matchType() {
            return matchType;
        }

        @Override
        public Type type() {
            return null;
        }

        public SensorNodeAddress dstAddress() {
            return address;
        }

        @Override
        public String toString() {
            return toStringHelper(sdnWiseType().toString())
                    .add("dst", address.toString()).toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SensorNodeSrcAddrCriterion)) {
                return false;
            }

            SensorNodeSrcAddrCriterion that = (SensorNodeSrcAddrCriterion) o;

            if (!address.equals(that.address)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return address.hashCode();
        }
    }

    public static final class SensorFlowBasicCriterion implements SensorFlowCriterion {
        private int size;
        private boolean usePreviousState;
        private int address;
        private byte[] value;
        private SensorNodeCriterionMatchType matchType = SensorNodeCriterionMatchType.EQUAL;

        public SensorFlowBasicCriterion(int size, boolean usePreviousState,
                                        int address, byte[] value) {
            this.size = size;
            this.usePreviousState = usePreviousState;
            this.address = address;
            this.value = value;
        }

        public SensorFlowBasicCriterion(int size, boolean usePreviousState,
                                        int address, byte[] value, SensorNodeCriterionMatchType matchType) {
            this.size = size;
            this.usePreviousState = usePreviousState;
            this.address = address;
            this.value = value;
            this.matchType = matchType;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public boolean isUsePreviousState() {
            return usePreviousState;
        }

        public void setUsePreviousState(boolean usePreviousState) {
            this.usePreviousState = usePreviousState;
        }

        public int getAddress() {
            return address;
        }

        public void setAddress(int address) {
            this.address = address;
        }

        public byte[] getValue() {
            return value;
        }

        public void setValue(byte[] value) {
            this.value = value;
        }

        @Override
        public SensorFlowCriterionType sdnWiseType() {
            return SensorFlowCriterionType.OTHER;
        }

        @Override
        public SensorNodeCriterionMatchType matchType() {
            return matchType;
        }

        @Override
        public Type type() {
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SensorFlowBasicCriterion)) {
                return false;
            }

            SensorFlowBasicCriterion that = (SensorFlowBasicCriterion) o;

            if (address != that.address) {
                return false;
            }
            if (size != that.size) {
                return false;
            }
            if (usePreviousState != that.usePreviousState) {
                return false;
            }
            if (!Arrays.equals(value, that.value)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = size;
            result = 31 * result + (usePreviousState ? 1 : 0);
            result = 31 * result + address;
            result = 31 * result + (value != null ? Arrays.hashCode(value) : 0);
            return result;
        }
    }
}
