package org.onosproject.sdnwise.controller.driver;

import org.jboss.netty.channel.Channel;
import org.onosproject.sdnwise.protocol.SDNWiseMessage;
import org.onosproject.sdnwise.protocol.SDNWiseNode;

import java.util.List;

/**
 * Created by aca on 2/19/15.
 */
public interface SDNWiseNodeDriver extends SDNWiseNode {
    public void setAgent(SDNWiseAgent agent);

    public void startDriverHandshake();

    public boolean isDriverHandshakeComplete();

    public boolean connectNode();

    public void removeConnectedNode();

    public int getNextTransactionId();

    public void setChannel(Channel channel);

    public void setConnected(boolean connected);

    public void write(SDNWiseMessage msg);

    public void write(List<SDNWiseMessage> msgs);
}
