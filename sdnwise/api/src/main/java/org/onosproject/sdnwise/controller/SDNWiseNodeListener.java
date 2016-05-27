package org.onosproject.sdnwise.controller;

import org.onosproject.sdnwise.protocol.SDNWiseNodeId;

/**
 * Created by aca on 2/13/15.
 */
public interface SDNWiseNodeListener {
    public void sensorNodeAdded(SDNWiseNodeId nodeId);

    public void sensorNodeRemoved(SDNWiseNodeId nodeId);
}
