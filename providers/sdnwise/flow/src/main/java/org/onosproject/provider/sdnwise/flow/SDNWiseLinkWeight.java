package org.onosproject.provider.sdnwise.flow;

import org.onosproject.net.Link;
import org.onosproject.net.topology.LinkWeight;
import org.onosproject.net.topology.TopologyEdge;

/**
 * Created by aca on 3/6/15.
 */
public class SDNWiseLinkWeight implements LinkWeight {
    @Override
    public double weight(TopologyEdge edge) {
        Link link = edge.link();
        String rssi = link.annotations().value("rssi");
        return Double.valueOf(rssi);
    }
}
