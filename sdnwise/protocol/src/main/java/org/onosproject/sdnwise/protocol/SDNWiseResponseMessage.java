package org.onosproject.sdnwise.protocol;

import com.github.sdnwiselab.sdnwise.flowtable.AbstractAction;
import com.github.sdnwiselab.sdnwise.flowtable.FlowTableEntry;
import com.github.sdnwiselab.sdnwise.flowtable.Window;
import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import com.github.sdnwiselab.sdnwise.packet.ResponsePacket;
import com.github.sdnwiselab.sdnwise.util.NodeAddress;
import org.onosproject.net.flow.criteria.Criterion;
import org.onosproject.net.sensorflow.SensorFlowCriterion.SensorNodeFields;
import org.onosproject.net.sensorflow.SensorFlowInstruction;
import org.onosproject.net.sensorflow.SensorTrafficSelector;
import org.onosproject.net.sensorflow.SensorTrafficTreatment;
import org.onosproject.sdnwise.protocol.util.ActionInstructionUtil;
import org.onosproject.sdnwise.protocol.util.WindowCriterionUtil;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.github.sdnwiselab.sdnwise.flowtable.Window.W_SIZE_1;
import static com.github.sdnwiselab.sdnwise.flowtable.Window.W_SIZE_2;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by aca on 6/18/15.
 */
public class SDNWiseResponseMessage extends SDNWiseMessage {
    private final Logger log = getLogger(getClass());
    private List<Window> flowTableWindows;
    private List<AbstractAction> flowTableActions;

    public SDNWiseResponseMessage(SDNWiseNodeId srcAddr, SDNWiseNodeId dstAddr) {
        super.setDestination(dstAddr);
        super.setSource(srcAddr);
        super.setId(dstAddr.netId());
        flowTableWindows = new ArrayList<>();
        flowTableActions = new ArrayList<>();
    }

    public void setTrafficSelector(SensorTrafficSelector trafficSelector) {
        Set<Criterion> criteria = trafficSelector.criteria();
        WindowCriterionUtil windowCriterionUtil = new WindowCriterionUtil();
        if ((criteria != null) && (criteria.size() > 0)) {
            for (Criterion criterion : criteria) {
                List<Window> windows = windowCriterionUtil.getWindow(criterion);
                if ((windows != null) && (windows.size() > 0)) {
                    flowTableWindows.addAll(windows);
                }
            }
        }
    }

    public void setTrafficTreatment(SensorTrafficTreatment trafficTreatment) {
        List<SensorFlowInstruction> instructions = trafficTreatment.sensorFlowInstructions();
        ActionInstructionUtil actionInstructionUtil = new ActionInstructionUtil();
        if ((instructions != null) && (instructions.size() > 0)) {
            for (SensorFlowInstruction instruction : instructions) {
                List<AbstractAction> actions = actionInstructionUtil.getAction(instruction);
                if ((actions != null) && (actions.size() > 0)) {
                    flowTableActions.addAll(actions);
                }
            }
        }
    }


    @Override
    public NetworkPacket getNetworkPacket() {
        // A dummy network packet for init purposes alone
        NetworkPacket networkPacket = new NetworkPacket(0, new NodeAddress(0), new NodeAddress(0));

        ResponsePacket responsePacket = new ResponsePacket(networkPacket);
        responsePacket.setTyp(NetworkPacket.RESPONSE);
        responsePacket.setNet((byte) super.getId());
        responsePacket.setDst(new NodeAddress(super.getDestination().address()));
        responsePacket.setSrc(new NodeAddress(super.getSource().address()));
        responsePacket.setTtl((byte) 100);
        responsePacket.setNxh(new NodeAddress(getNxHop().address()));

        FlowTableEntry flowTableEntry = new FlowTableEntry();

        flowTableWindows.forEach(flowTableEntry::addWindow);
        flowTableActions.forEach(flowTableEntry::addAction);

        responsePacket.setRule(flowTableEntry);
        super.setRawDataPayload(getPayload(responsePacket));

        return responsePacket;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.getNetworkPacket().toByteArray());
    }


    private byte getWindowSize(SensorNodeFields sensorNodeFields) {
        switch (sensorNodeFields) {
            case DST_ADDR:
                return W_SIZE_2;
            case MUL_CUR_ADDR:
                return W_SIZE_2;
            case MUL_GROUP_ID:
                return W_SIZE_2;
            case MUL_INIT_ADDR:
                return W_SIZE_2;
            case MUL_PRV_ADDR:
                return W_SIZE_2;
            case PKT_TYPE:
                return W_SIZE_1;
            case SRC_ADDR:
                return W_SIZE_2;
            default:
                return -1;
        }
    }
}
