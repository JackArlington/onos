package org.onosproject.net.sensorflow;

import org.onosproject.net.flow.instructions.Instruction;

/**
 * Created by aca on 3/2/15.
 */
public interface SensorFlowInstruction extends Instruction {
    enum Type {
        FORWARD_UNICAST,
        OPEN_PATH,
        FORWARD_BROADCAST,
        DROP,
        MODIFY,
        AGGREGATE,
        FORWARD_UP,
        SET_SRC_ADDR,
        SET_DST_ADDR,
        SET_GEO_NX_HOP_COORD,
        SET_GEO_NX_HOP,
        SET_GEO_PRV_HOP,
        SET_PACKET_TYPE,
        SET_STATE_VALUE_CONST,
        SET_STATE_VALUE_PACKET,
        INCREMENT_STATE_VALUE,
        ASK_CONTROLLER,
        STATE_PACKET_OP,
        STATE_CONST_OP,
        STATE_STATE_OP,
        SET_PKT_VAL_CONST,
        REMATCH_PACKET,
        SET_PKT_LEN
    }

    enum Operator {
        ADD,
        MULTIPLY,
        AND,
        OR,
        NOT,
        XOR
    }

    Type getSensorFlowInstructionType();

    boolean multimatch();

    default Operator getOperator() {
        return null;
    }
}
