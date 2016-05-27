package org.onosproject.provider.sdnwise.flow;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onosproject.core.ApplicationId;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Link;
import org.onosproject.net.Path;
import org.onosproject.net.SensorNode;
import org.onosproject.net.SensorNodeId;
import org.onosproject.net.flow.BatchOperation;
import org.onosproject.net.flow.CompletedBatchOperation;
import org.onosproject.net.flow.FlowRule;
import org.onosproject.net.flow.FlowRuleBatchEntry;
import org.onosproject.net.flow.FlowRuleProvider;
import org.onosproject.net.flow.FlowRuleProviderRegistry;
import org.onosproject.net.flow.FlowRuleProviderService;
import org.onosproject.net.provider.AbstractProvider;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.net.sensor.SensorNodeService;
import org.onosproject.net.sensorflow.SensorFlowInstruction;
import org.onosproject.net.sensorflow.SensorFlowOpenPathInstruction;
import org.onosproject.net.sensorflow.SensorTrafficSelector;
import org.onosproject.net.sensorflow.SensorTrafficTreatment;
import org.onosproject.sdnwise.controller.SDNWiseController;
import org.onosproject.sdnwise.controller.SDNWiseEventListener;
import org.onosproject.sdnwise.controller.SDNWiseNodeListener;
import org.onosproject.sdnwise.protocol.SDNWiseMessage;
import org.onosproject.sdnwise.protocol.SDNWiseNode;
import org.onosproject.sdnwise.protocol.SDNWiseNodeId;
import org.onosproject.sdnwise.protocol.SDNWiseOpenPathMessage;
import org.onosproject.sdnwise.protocol.SDNWiseResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.onosproject.net.sensorflow.SensorFlowInstruction.Type.OPEN_PATH;

/**
 * Created by aca on 3/2/15.
 */
@Component(immediate = true)
public class SDNWiseFlowRuleProvider extends AbstractProvider implements FlowRuleProvider {
    private static final Logger LOG = LoggerFactory.getLogger(SDNWiseFlowRuleProvider.class);

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowRuleProviderRegistry providerRegistry;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected SDNWiseController controller;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected SensorNodeService sensorNodeService;

    private FlowRuleProviderService providerService;

    private InternalFlowProvider listener = new InternalFlowProvider();

    public SDNWiseFlowRuleProvider() {
        super(new ProviderId("sdnwise", "org.onosproject.provider.sdnwise"));
        LOG.info("Initializing SDN WISE Rule Provider");
    }

    @Activate
    public void activate() {
        providerService = providerRegistry.register(this);
        controller.addListener(listener);
        controller.addEventListener(listener);

        LOG.info("Sarted SDN-WISE Flow Rule Provider");
    }

    @Deactivate
    public void deactivate() {
        providerRegistry.unregister(this);
        providerService = null;

        LOG.info("Stopped SDN-WISE Flow Rule Provider");
    }

    @Override
    public void applyFlowRule(FlowRule... flowRules) {
        for (FlowRule flowRule : flowRules) {
            applyRule(flowRule);
        }
    }

    private void applyRule(FlowRule flowRule) {
//        List<Instruction> instructions = flowRule.treatment().instructions();
        SDNWiseNode node = controller.getNode(flowRule.deviceId());


        SensorNode dstNode = sensorNodeService.getSensorNode(
                SensorNodeId.sensorNodeId(node.getId().generateMacAddress(), node.getId().netId()));
        SensorNode dstAssociatedSink = dstNode.associatedSink();

        SDNWiseResponseMessage sdnWiseResponseMessage = new SDNWiseResponseMessage(
                SDNWiseNodeId.fromMacAddress(dstAssociatedSink.mac()), node.getId());

        boolean isOpenPath = false;

        SensorTrafficSelector trafficSelector = (SensorTrafficSelector) flowRule.selector();
        SensorTrafficTreatment trafficTreatment = (SensorTrafficTreatment) flowRule.treatment();

        if (trafficTreatment != null) {
            SensorFlowInstruction sensorFlowInstruction = trafficTreatment.sensorFlowInstructions().get(0);
            if (sensorFlowInstruction.getSensorFlowInstructionType().equals(OPEN_PATH)) {
                isOpenPath = true;
//                if (node == null) {
//                    LOG.info("Node {} is not connected!!!", flowRule.deviceId().uri());
//                } else {
//                    LOG.info("Preparing OPEN_PATH for node {}", node.getId().uri());
//                }
//                        SensorNode dstNode = sensorNodeService.getSensorNode(flowRule.deviceId());
                SDNWiseOpenPathMessage sdnWiseOpenPathMessage =
                        new SDNWiseOpenPathMessage(node);
                SensorFlowOpenPathInstruction sensorFlowOpenPathInstruction =
                        (SensorFlowOpenPathInstruction) sensorFlowInstruction;



                Path path = sensorFlowOpenPathInstruction.getPath();
                List<Link> links = path.links();
                byte[] pathArray = new byte[2 * (links.size() + 1)];
                int k = 0;
                if ((links != null) && (links.size() > 0)) {
                    for (Link link : links) {
                        if (k == 0) {
                            DeviceId srcDeviceId = link.src().deviceId();
                            SensorNode sensorNode = sensorNodeService.getSensorNode(srcDeviceId);
                            pathArray[k++] = sensorNode.addr()[0];
                            pathArray[k++] = sensorNode.addr()[1];
                        }
                        DeviceId dstDeviceId = link.dst().deviceId();
//                        LOG.info("Adding device {} to path", dstDeviceId);
                        SensorNode sensorNode = sensorNodeService.getSensorNode(dstDeviceId);
                        if (sensorNode == null) {
                            LOG.warn("Got a non-SDNWISE device in OPEN PATH");
                            Iterator<Link> pathIterator = path.links().iterator();
                            while (pathIterator.hasNext()) {
                                Link myLink = pathIterator.next();
                                LOG.warn("Have link {}", myLink);
                            }
                        }
                        pathArray[k++] = sensorNode.addr()[0];
                        pathArray[k++] = sensorNode.addr()[1];
//                                SDNWiseNodeId dstNodeId = SDNWiseNodeId.fromUri(dstDeviceId.uri());
//                                pathArray[k++] = dstNodeId.address()[0];
//                                pathArray[k++] = dstNodeId.address()[1];
                    }
                    SensorTrafficSelector openPathTrafficSelector = (SensorTrafficSelector) flowRule.selector();
                    if (openPathTrafficSelector != null) {
                        sdnWiseOpenPathMessage.setTrafficSelection(openPathTrafficSelector);
                    }
                    sdnWiseOpenPathMessage.setPath(pathArray);
                    sdnWiseOpenPathMessage.setSource(
                            new SDNWiseNodeId(dstAssociatedSink.netId(), dstAssociatedSink.nodeAddress().getAddr()));
                    sdnWiseOpenPathMessage.setNxHop(SDNWiseNodeId.fromMacAddress(dstAssociatedSink.mac()));

//                            SDNWiseNode node = controller.getNode(flowRule.deviceId());

                    LOG.info("Sending message {} to node {}", sdnWiseOpenPathMessage.toString(), node.getID());
                    //TODO: FIXME
//                            node = controller.getNode(DeviceId.deviceId("sdnwise:00:00:00:01:00:02"));
                    node.sendMsg(sdnWiseOpenPathMessage);
                }
            } else {
                sdnWiseResponseMessage.setTrafficSelector(trafficSelector);
                sdnWiseResponseMessage.setTrafficTreatment(trafficTreatment);
                sdnWiseResponseMessage.setNxHop(SDNWiseNodeId.fromMacAddress(dstAssociatedSink.mac()));
            }
        }

//        for (Instruction instruction : instructions) {
//            if (instruction.type() != null) {
//                switch (instruction.type()) {
//                    case OUTPUT:
//                        LOG.info("SDNWise handling OF Type {}", instruction.type().name());
//                        break;
//                    default:
//                        break;
//                }
//            } else {
//
//
//            }
//        }

        if (!isOpenPath) {
            node.sendMsg(sdnWiseResponseMessage);
        }
    }


    @Override
    public void removeFlowRule(FlowRule... flowRules) {

    }

    @Override
    public void removeRulesById(ApplicationId applicationId, FlowRule... flowRules) {

    }

    @Override
    public Future<CompletedBatchOperation> executeBatch(BatchOperation<FlowRuleBatchEntry> batchOperation) {
//        LOG.info("Executing batch operation");
        if ((batchOperation != null) && (batchOperation.size() > 0)) {
            List<FlowRuleBatchEntry> flowRuleBatchEntries = batchOperation.getOperations();
            if ((flowRuleBatchEntries != null) && (flowRuleBatchEntries.size() > 0)) {
                for (FlowRuleBatchEntry flowRuleBatchEntry : flowRuleBatchEntries) {
                    FlowRule flowRule = flowRuleBatchEntry.getTarget();
//                    final SDNWiseNodeId nodeId = SDNWiseNodeId.dpid(flowRule.deviceId().uri());
                    SDNWiseNode node = controller.getNode(flowRule.deviceId());
                    if (node == null) {
                        InstallationFuture installationFuture = new InstallationFuture();
                        installationFuture.cancel(true);
                        return installationFuture;
                    }
                    applyRule(flowRule);
                }
                InstallationFuture installationFuture = new InstallationFuture();
                installationFuture.setCompleted();
                return installationFuture;
            }
        }
        return null;
    }

    private class InstallationFuture implements Future<CompletedBatchOperation> {
        private boolean cancelled = false;
        private boolean done = false;

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            this.cancelled = true;
            return true;
        }

        @Override
        public boolean isCancelled() {
            return this.cancelled;
        }

        public void setCompleted() {
            this.done = true;
        }

        @Override
        public boolean isDone() {
            return this.done;
        }

        @Override
        public CompletedBatchOperation get() throws InterruptedException, ExecutionException {
            return new CompletedBatchOperation(true, null);
        }

        @Override
        public CompletedBatchOperation get(long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            return new CompletedBatchOperation(true, null);
        }
    }

    private class InternalFlowProvider
            implements SDNWiseNodeListener, SDNWiseEventListener {

        @Override
        public void handleMessage(SDNWiseNodeId sdnWiseNodeId, SDNWiseMessage sdnWiseMessage) {

        }

        @Override
        public void sensorNodeAdded(SDNWiseNodeId nodeId) {

        }

        @Override
        public void sensorNodeRemoved(SDNWiseNodeId nodeId) {

        }
    }
}
