package org.onosproject.sdnwise.controller;

import org.onosproject.sdnwise.protocol.SDNWiseMessage;

/**
 * Created by aca on 2/16/15.
 */
public interface SDNWisePacketListener {
    public void handlePacket(SDNWiseMessage message);
}
