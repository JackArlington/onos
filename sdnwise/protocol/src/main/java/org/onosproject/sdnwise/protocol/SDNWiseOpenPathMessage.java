package org.onosproject.sdnwise.protocol;

import com.github.sdnwiselab.sdnwise.flowtable.Window;
import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import com.github.sdnwiselab.sdnwise.packet.OpenPathPacket;
import com.github.sdnwiselab.sdnwise.util.NodeAddress;
import com.google.common.collect.Lists;
import org.onosproject.net.flow.criteria.Criterion;
import org.onosproject.net.sensorflow.SensorTrafficSelector;
import org.onosproject.sdnwise.protocol.util.WindowCriterionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.google.common.base.MoreObjects.toStringHelper;
import static org.onosproject.sdnwise.protocol.SDNWiseBuiltinMessageType.OPEN_PATH;

/**
 * Created by aca on 3/6/15.
 */
public class SDNWiseOpenPathMessage extends SDNWiseMessage {
    List<Window> conditions = null;
    Function<byte[], List<NodeAddress>> transform;
    private SDNWiseNode destination;
    private byte[] path;

    public SDNWiseOpenPathMessage(SDNWiseNode destination) {
        this.setMessageType(OPEN_PATH);
        this.destination = destination;

        super.setId(destination.getId().netId());
//        super.setSource(destination.getId());
        super.setDestination(destination.getId());

        transform = bytes -> {
            List<NodeAddress> nodeAddresses = Lists.newArrayList();
            int i = 0;
            while (i < bytes.length) {
                NodeAddress nodeAddress = new NodeAddress(new byte[]{bytes[i], bytes[i + 1]});
                nodeAddresses.add(nodeAddress);
                i += 2;
            }
            return nodeAddresses;
        };
    }

    public byte[] getPath() {
        return this.path;
    }

    public void setPath(byte[] path) {
        this.path = path;
    }

    public void setTrafficSelection(SensorTrafficSelector trafficSelection) {
        conditions = new ArrayList<>();
        WindowCriterionUtil windowCriterionUtil = new WindowCriterionUtil();
        Set<Criterion> criteria = trafficSelection.criteria();
        if ((criteria != null) && (criteria.size() > 0)) {
            for (Criterion criterion : criteria) {
                conditions.addAll(windowCriterionUtil.getWindow(criterion));
            }
        }
    }

    @Override
    public NetworkPacket getNetworkPacket() {
        NodeAddress dstAddr = new NodeAddress(new byte[]{path[0], path[1]});
        NodeAddress srcAddr = new NodeAddress(super.getSource().address());
        OpenPathPacket openPathPacket = new OpenPathPacket(destination.getId().netId(), srcAddr,
                dstAddr, transform.apply(path));
        if (conditions != null) {
            openPathPacket.setWindows(conditions);
        }
        super.setRawDataPayload(getPayload(openPathPacket));
        openPathPacket.setNxh(new NodeAddress(getNxHop().address()));
        return openPathPacket;
    }

    @Override
    public byte[] serialize() {
        return getNetworkPacket().toByteArray();
//        NetworkPacket networkPacket = super.getNetworkPacket();
//        networkPacket.setSrc(new NodeAddress(path[0], path[1]));
//        byte[] dstAddr = new byte[2];
//        dstAddr[0] = path[0];
//        dstAddr[1] = path[1];
//        networkPacket.setDst(new NodeAddress(dstAddr));
//        networkPacket.setLen((byte) (10 + path.length));
//        networkPacket.setPayload(path);
//        networkPacket.setNxhop(new NodeAddress(path[0], path[1]));
//        networkPacket.setNetId((byte) 1);
//        networkPacket.setTtl((byte) 100);
//        networkPacket.setType((byte) 5);

//        return networkPacket.toByteArray();
    }

    @Override
    public String toString() {
        return toStringHelper("OPEN_PATH")
                .add("message", Arrays.toString(serialize()))
                .toString();
    }
}
