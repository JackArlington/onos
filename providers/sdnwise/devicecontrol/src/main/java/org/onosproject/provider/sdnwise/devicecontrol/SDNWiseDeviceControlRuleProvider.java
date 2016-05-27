package org.onosproject.provider.sdnwise.devicecontrol;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onosproject.net.DeviceId;
import org.onosproject.net.SensorNode;
import org.onosproject.net.SensorNodeLocalization;
import org.onosproject.net.devicecontrol.DeviceControlProviderService;
import org.onosproject.net.devicecontrol.DeviceControlRule;
import org.onosproject.net.devicecontrol.DeviceControlRuleProvider;
import org.onosproject.net.devicecontrol.DeviceControlRuleProviderRegistry;
import org.onosproject.net.devicecontrol.DeviceTreatment;
import org.onosproject.net.devicecontrol.instructions.DeviceControlInstruction;
import org.onosproject.net.devicecontrol.instructions.DeviceControlInstructions;
import org.onosproject.net.provider.AbstractProvider;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.net.sensor.SensorNodeAddress;
import org.onosproject.net.sensor.SensorNodeService;
import org.onosproject.sdnwise.controller.SDNWiseController;
import org.onosproject.sdnwise.protocol.SDNWiseFunctionLoadMessage;
import org.onosproject.sdnwise.protocol.SDNWiseMessage;
import org.onosproject.sdnwise.protocol.SDNWiseNeighborsCoordinatesMessage;
import org.onosproject.sdnwise.protocol.SDNWiseNode;
import org.onosproject.sdnwise.protocol.SDNWiseNodeCoordinatesMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

/**
 * Created by aca on 6/19/15.
 */
@Component(immediate = true)
public class SDNWiseDeviceControlRuleProvider extends AbstractProvider implements DeviceControlRuleProvider {
    private static final Logger LOG = LoggerFactory.getLogger(SDNWiseDeviceControlRuleProvider.class);

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceControlRuleProviderRegistry providerRegistry;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected SDNWiseController controller;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected SensorNodeService sensorNodeService;

    private DeviceControlProviderService providerService;

    public SDNWiseDeviceControlRuleProvider() {
        super(new ProviderId("sdnwise", "org.onosproject.provider.sdnwise"));
    }

    @Activate
    public void activate() {
        providerService = providerRegistry.register(this);
    }

    @Deactivate
    public void deactivate() {
        providerRegistry.unregister(this);
        providerService = null;
    }

    @Override
    public void applyDeviceControlRule(DeviceControlRule deviceControlRule) {
        DeviceId deviceId = deviceControlRule.deviceId();
        SensorNode dst = sensorNodeService.getSensorNode(deviceId);
        SensorNodeAddress dstAddr = new SensorNodeAddress((byte) dst.netId(), dst.addr());
        SDNWiseNode node = controller.getNode(deviceId);
//        SDNWiseNode node = controller.getNode(DeviceId.deviceId("sdnwise:00:00:00:01:00:01"));

        DeviceTreatment deviceTreatment = deviceControlRule.deviceTreatment();
        List<DeviceControlInstruction> instructions = deviceTreatment.instructions();
        if ((instructions != null) && (instructions.size() > 0)) {
            for (DeviceControlInstruction instruction : instructions) {
                SDNWiseMessage message = null;
                SensorNodeAddress sinkAddr = instruction.sinkNodeAddress();
                switch (instruction.type()) {
                    case INSTALL_FUNCTION:
                        DeviceControlInstructions.InstallFunctionInstruction installFunctionInstruction =
                                (DeviceControlInstructions.InstallFunctionInstruction) instruction;
                        URI functionFileLocation = installFunctionInstruction.getFunctionLocation();
                        URI functionCallback = installFunctionInstruction.getFunctionCallback();
                        byte functionId = Byte.valueOf(functionCallback.getPath()).byteValue();

                        message = new SDNWiseFunctionLoadMessage(sinkAddr, dstAddr, functionId, functionFileLocation);
//
//                        int totalSize = message.getNetworkPackets().stream().mapToInt(value -> value.getLen()).sum();
//                        LOG.info("Function {} load is {}", functionFileLocation, totalSize);

                        break;
                    case SET_COORDINATES:
                        DeviceControlInstructions.SetCoordinatesInstruction coordinatesInstruction =
                                (DeviceControlInstructions.SetCoordinatesInstruction) instruction;
                        Double xCoord = coordinatesInstruction.x();
                        Double yCoord = coordinatesInstruction.y();
                        Double zCoord = coordinatesInstruction.z();

                        Integer x = xCoord != null ? Integer.valueOf(xCoord.intValue()) : null;
                        Integer y = xCoord != null ? Integer.valueOf(yCoord.intValue()) : null;
                        Integer z = xCoord != null ? Integer.valueOf(zCoord.intValue()) : null;

                        message = new SDNWiseNodeCoordinatesMessage(sinkAddr, dstAddr, x, y, z);
                        break;
                    case SET_NEIGHBOR_COORDINATES:
                        DeviceControlInstructions.SetNeighborsCoordinatesInstruction neighborsCoordinatesInstruction =
                                (DeviceControlInstructions.SetNeighborsCoordinatesInstruction) instruction;
                        List<SensorNode> neighborhood = neighborsCoordinatesInstruction.neighborhood();
                        SensorNodeLocalization localizationAlgo =
                                neighborsCoordinatesInstruction.localizationAlgorithm();
                        if ((neighborhood != null) && (neighborhood.size() > 0)) {
                            message = new SDNWiseNeighborsCoordinatesMessage(sinkAddr, dstAddr);
                            for (SensorNode neighbor : neighborhood) {
                                double[] coordinates = neighbor.xyzCoordinates(localizationAlgo);
                                int xCoordinate = (int) coordinates[0];
                                int yCoordinate = (int) coordinates[1];
                                int zCoordinate = (int) coordinates[2];

                                SensorNodeAddress neighborAddr =
                                        new SensorNodeAddress((byte) neighbor.netId(), neighbor.addr());

                                ((SDNWiseNeighborsCoordinatesMessage) message).addNeighborCoordinates(neighborAddr,
                                        xCoordinate, yCoordinate, zCoordinate);
                            }
                        }
                        break;
                    default:
                        break;
                }
                if (message != null) {
                    LOG.info("Sending device control message {} to node {}", message.toString(), node.getId().uri());
//                    LOG.info("Serialized messages is {}", Arrays.toString(message.getNetworkPacket().toByteArray()));
                    node.sendMsg(message);
                }
            }
        }
    }
}
