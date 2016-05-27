package org.onosproject.provider.sdnwise.packet.impl;

import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import org.onosproject.net.packet.DefaultPacketContext;
import org.onosproject.net.packet.InboundPacket;
import org.onosproject.net.packet.OutboundPacket;
import org.onosproject.sdnwise.protocol.SDNWiseMessage;
import org.onosproject.sdnwise.protocol.SDNWiseNode;

/**
 * Created by aca on 2/26/15.
 */
public class SDNWiseCorePacketContext extends DefaultPacketContext {
    private final SDNWiseNode sdnWiseNode;

    protected SDNWiseCorePacketContext(long time, InboundPacket inPkt,
                                       OutboundPacket outPkt, boolean block,
                                       SDNWiseNode sdnWiseNode) {
        super(time, inPkt, outPkt, block);
        this.sdnWiseNode = sdnWiseNode;
    }

    @Override
    public void send() {
        NetworkPacket networkPacket = new NetworkPacket(inPacket().unparsed().array());
        SDNWiseMessage sdnWiseMessage = SDNWiseMessage.getMessageFromPacket(networkPacket);
        sdnWiseMessage.writeTo(sdnWiseNode);
    }
}
