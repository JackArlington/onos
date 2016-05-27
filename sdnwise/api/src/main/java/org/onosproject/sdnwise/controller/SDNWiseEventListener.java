package org.onosproject.sdnwise.controller;

import org.onosproject.sdnwise.protocol.SDNWiseMessage;
import org.onosproject.sdnwise.protocol.SDNWiseNodeId;

/**
 * Created by aca on 2/14/15.
 */
public interface SDNWiseEventListener {
    public void handleMessage(SDNWiseNodeId sdnWiseNodeId, SDNWiseMessage sdnWiseMessage);
}
