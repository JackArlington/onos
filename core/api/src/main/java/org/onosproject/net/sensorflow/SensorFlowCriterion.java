package org.onosproject.net.sensorflow;

import org.onosproject.net.flow.criteria.Criterion;

/**
 * Created by aca on 3/2/15.
 */
public interface SensorFlowCriterion extends Criterion {
    enum SensorFlowCriterionType {
        SRC_NODE_ADDR,
        DEST_NODE_ADDR,
        PACKET_TYPE,
        MULTICAST_GROUP_ID,
        MULTICAST_PREV_NODE,
        MULTICAST_CUR_NODE,
        MULTICAST_INIT_NODE,
        PACKET_FIELDS,
        PACKET_FIELDS_ANY,
        PACKET_FIELD_CONST,
        STATE_CONST,
        STATE_STATE,
        OTHER
    }

    enum SensorNodeCriterionMatchType {
        EQUAL,
        NOT_EQUAL,
        LESS_THAN,
        GREATER_THAN,
        LESS_EQUAL,
        GREATER_EQUAL
    }

    enum SensorNodeFields {
        SRC_ADDR,
        DST_ADDR,
        PKT_TYPE,
        MUL_PRV_ADDR,
        MUL_CUR_ADDR,
        MUL_INIT_ADDR,
        MUL_GROUP_ID
    }

    SensorFlowCriterionType sdnWiseType();

    SensorNodeCriterionMatchType matchType();
}
