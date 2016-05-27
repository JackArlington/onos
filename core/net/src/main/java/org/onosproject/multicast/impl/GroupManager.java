package org.onosproject.multicast.impl;

import com.google.common.collect.LinkedListMultimap;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Host;
import org.onosproject.net.HostId;
import org.onosproject.net.SensorNode;
import org.onosproject.net.SensorNodeId;
import org.onosproject.net.host.HostService;
import org.onosproject.net.multicast.Group;
import org.onosproject.net.multicast.GroupManagementProvider;
import org.onosproject.net.multicast.GroupManagementProviderRegistry;
import org.onosproject.net.multicast.GroupManagementProviderService;
import org.onosproject.net.multicast.GroupManagementService;
import org.onosproject.net.provider.AbstractProviderRegistry;
import org.onosproject.net.provider.AbstractProviderService;
import org.onosproject.net.sensor.SensorNodeAddress;
import org.onosproject.net.sensor.SensorNodeService;
import org.slf4j.Logger;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by aca on 4/30/15.
 */
@Component(immediate = true)
@Service
public class GroupManager
        extends AbstractProviderRegistry<GroupManagementProvider, GroupManagementProviderService>
        implements GroupManagementService, GroupManagementProviderRegistry {
    private final Logger log = getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected HostService hostService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected SensorNodeService sensorNodeService;

    private List<Group> groups = new ArrayList<>();
    private Map<URI, List<Host>> hosts = new ConcurrentHashMap<>();
//    private Map<URI, List<SensorNode>> sensorNodes = new ConcurrentHashMap<>();
    private LinkedListMultimap<URI, SensorNode> sensorNodes;

    @Activate
    public void activate() {
        sensorNodes = LinkedListMultimap.create();
        log.info("Group Manager activated");
    }

    @Deactivate
    public void deactivate() {
        log.info("Group Manager deactivated");
    }

    @Override
    protected GroupManagementProviderService createProviderService(GroupManagementProvider provider) {
        return new InternalGroupManagementroviderService(provider);
    }

    @Override
    public List<Group> groups() {
        return groups;
    }

    @Override
    public List<Host> hosts(URI groupId) {
        return hosts.get(groupId);
    }

    @Override
    public List<SensorNode> sensorNodes(URI groupId) {
        return sensorNodes.get(groupId);
    }

    @Override
    public List<Host> hosts(IpAddress groupIp) {
        Group group = new Group(groupIp);
        return hosts.get(group.getId());
    }

    @Override
    public List<SensorNode> sensorNodes(IpAddress groupIp) {
        Group group = new Group(groupIp);
        return sensorNodes.get(group.getId());
    }

    @Override
    public List<Host> hosts(MacAddress groupMac) {
        Group group = new Group(groupMac);
        return hosts.get(group.getId());
    }

    @Override
    public List<SensorNode> sensorNodes(MacAddress groupMac) {
        Group group = new Group(groupMac);
        return sensorNodes.get(group.getId());
    }

    private class InternalGroupManagementroviderService
            extends AbstractProviderService<GroupManagementProvider>
            implements GroupManagementProviderService {

        /**
         * Creates a provider service on behalf of the specified provider.
         *
         * @param provider provider to which this service is being issued
         */
        protected InternalGroupManagementroviderService(GroupManagementProvider provider) {
            super(provider);
        }

        @Override
        public void hostAdded(HostId hostId, IpAddress multicastAddress) {
            Host host = hostService.getHost(hostId);
            Group group = new Group(multicastAddress);
            if (!groups.contains(group)) {
                groups.add(group);
            }

            List<Host> hostList = hosts.get(group.getId());
            if (hostList == null) {
                hostList = new ArrayList<>();
            }
            hostList.add(host);
            hosts.put(group.getId(), hostList);
        }

        @Override
        public void hostRemoved(HostId hostId, IpAddress multicastAddress) {
            Host host = hostService.getHost(hostId);
            Group group = new Group(multicastAddress);

            List<Host> hostList = hosts.get(group.getId());
            if ((hostList != null) && (hostList.size() > 0)) {
                hostList.remove(host);
                hosts.put(group.getId(), hostList);
            }
        }

        @Override
        public void sensorNodeAdded(SensorNodeId sensorNodeId, SensorNodeAddress multicastAddress) {
            SensorNode sensorNode = sensorNodeService.getSensorNode(sensorNodeId);
            Group group = new Group(multicastAddress);
            if (!groups.contains(group)) {
                groups.add(group);
            }

            synchronized (sensorNodes) {
                sensorNodes.put(group.getId(), sensorNode);
//                List<SensorNode> tmp = sensorNodes.get(group.getId());
//                tmp.forEach(sensorNode1 -> log.info("Group {} has node {}", multicastAddress.toString(),
//                        sensorNode1.deviceId().toString()));
            }


//            List<SensorNode> sensorNodeList = sensorNodes.get(group.getId());
//            if (sensorNodeList == null) {
////                sensorNodeList = new ArrayList<>();
//                sensorNodeList = Collections.synchronizedList(new ArrayList<>());
//            }
//            synchronized (sensorNodeList) {
//                sensorNodeList.add(sensorNode);
//                sensorNodes.put(group.getId(), sensorNodeList);
//                log.info("Sensor {} address to group address {}", sensorNodeId, multicastAddress.toString());
//
//                List<SensorNode> tmpSensorNodeList = ImmutableList.copyOf(sensorNodeList);
//                for (SensorNode node : tmpSensorNodeList) {
//                    log.info("Group {} has node {}", multicastAddress.toString(), node.deviceId().toString());
//                }
//            }
        }

        @Override
        public void sensorNodeRemoved(SensorNodeId sensorNodeId, SensorNodeAddress multicastAddress) {
            SensorNode sensorNode = sensorNodeService.getSensorNode(sensorNodeId);
            Group group = new Group(multicastAddress);

//            List<SensorNode> sensorNodeList = sensorNodes.get(group.getId());
//            if ((sensorNodeList != null) && (sensorNodeList.size() > 0)) {
//                sensorNodeList.remove(sensorNode);
//                sensorNodes.put(group.getId(), sensorNodeList);
//            }
        }

        @Override
        public void deviceAdded(DeviceId deviceId, MacAddress multicastAddress) {

        }

        @Override
        public void deviceRemoved(DeviceId deviceId, MacAddress multicastAddress) {

        }
    }
}
