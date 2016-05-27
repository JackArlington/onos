package org.onosproject.sdnwise.controller.driver;

import org.onosproject.sdnwise.protocol.SDNWiseMessage;
import org.onosproject.sdnwise.protocol.SDNWiseNode;
import org.onosproject.sdnwise.protocol.SDNWiseNodeId;

/**
 * Created by aca on 2/18/15.
 */
public interface SDNWiseAgent {
    public boolean addConnectedNode(SDNWiseNodeId id, SDNWiseNode node);

    public void removeConnectedNode(SDNWiseNodeId id);

    public void processMessage(SDNWiseMessage message);
}
