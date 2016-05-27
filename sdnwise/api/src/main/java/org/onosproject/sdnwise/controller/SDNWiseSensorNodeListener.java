package org.onosproject.sdnwise.controller;

import org.onosproject.sdnwise.protocol.SDNWiseNodeId;

/**
 * Created by aca on 3/15/15.
 */
public interface SDNWiseSensorNodeListener {
    public void sensorNodeAdded(SDNWiseNodeId nodeId);

    public void sensorNodeRemoved(SDNWiseNodeId nodeId);
}
