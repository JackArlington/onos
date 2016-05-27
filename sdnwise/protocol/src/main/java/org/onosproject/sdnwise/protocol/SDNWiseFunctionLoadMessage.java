package org.onosproject.sdnwise.protocol;

import com.github.sdnwiselab.sdnwise.packet.ConfigPacket;
import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import com.github.sdnwiselab.sdnwise.util.NodeAddress;
import com.google.common.io.ByteStreams;
import org.onosproject.net.sensor.SensorNodeAddress;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.onosproject.sdnwise.protocol.SDNWiseBuiltinMessageType.CONFIG;

/**
 * Created by aca on 6/18/15.
 */
public class SDNWiseFunctionLoadMessage extends SDNWiseMessage {


    /**
     * Function buffer size.
     */
    private static final int BUFF_SIZE = 16384;
    /**
     * First request delay.
     */
    private static final int DELAY = 200;
    /**
     * Fields and lengths.
     */
    private static final int FUNCTION_HEADER_LEN = 3, CONFIG_HEADER_LEN = 1,
            FUNCTION_PAYLOAD_LEN = NetworkPacket.MAX_PACKET_LENGTH - (NetworkPacket.DFLT_HDR_LEN
                    + FUNCTION_HEADER_LEN + CONFIG_HEADER_LEN);
    /**
     * Maximum number of parts for a function.
     */
    private static final int PARTS_MAX = 256;


    private byte[] classBuffer;
    private byte functionId;

    public SDNWiseFunctionLoadMessage(SensorNodeAddress sink, SensorNodeAddress dest,
                                      byte functionId, URI classLocation) {
        super.setMessageType(CONFIG);
        super.setSource(new SDNWiseNodeId(sink.getNetId(), sink.getAddr()));
        super.setDestination(new SDNWiseNodeId(dest.getNetId(), dest.getAddr()));
        this.functionId = functionId;
        try {
            URL classLocationUrl = classLocation.toURL();
            InputStream inputStream = classLocationUrl.openStream();
            classBuffer = ByteStreams.toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SDNWiseFunctionLoadMessage(SensorNodeAddress sink, SensorNodeAddress dest, byte[] classBuffer) {
        this.classBuffer = new byte[classBuffer.length];
        System.arraycopy(classBuffer, 0, this.classBuffer, 0, classBuffer.length);
        super.setMessageType(CONFIG);
        super.setSource(new SDNWiseNodeId(sink.getNetId(), sink.getAddr()));
        super.setDestination(new SDNWiseNodeId(dest.getNetId(), dest.getAddr()));
        this.functionId = classBuffer[2];
    }

    @Override
    public List<NetworkPacket> getNetworkPackets() {

        LinkedList<NetworkPacket> ll = new LinkedList<>();
        int packetNumber = classBuffer.length / FUNCTION_PAYLOAD_LEN;
        int remaining = classBuffer.length % FUNCTION_PAYLOAD_LEN;
        int totalPackets = packetNumber;
        if (remaining > 0) {
            totalPackets++;
        }
        int pointer = 0;
        int i = 0;

        if (packetNumber < PARTS_MAX) {
            if (packetNumber > 0) {
                for (i = 0; i < packetNumber; i++) {
                    byte[] payload = ByteBuffer.allocate(FUNCTION_PAYLOAD_LEN
                            + FUNCTION_HEADER_LEN)
                            .put(functionId)
                            .put((byte) (i + 1))
                            .put((byte) totalPackets)
                            .put(Arrays.copyOfRange(classBuffer, pointer, pointer
                                    + FUNCTION_PAYLOAD_LEN)).array();
                    pointer += FUNCTION_PAYLOAD_LEN;
                    ConfigPacket np = new ConfigPacket(super.getId(),
                            new NodeAddress(super.getSource().address()),
                            new NodeAddress(super.getDestination().address()),
                            ConfigPacket.ConfigProperty.ADD_FUNCTION, payload);
                    ll.add(np);
                }
            }

            if (remaining > 0) {
                byte[] payload = ByteBuffer.allocate(remaining
                        + FUNCTION_HEADER_LEN)
                        .put(functionId)
                        .put((byte) (i + 1))
                        .put((byte) totalPackets)
                        .put(Arrays.copyOfRange(classBuffer, pointer, pointer
                                + remaining)).array();
                ConfigPacket np = new ConfigPacket(super.getId(),
                        new NodeAddress(super.getSource().address()),
                        new NodeAddress(super.getDestination().address()),
                        ConfigPacket.ConfigProperty.ADD_FUNCTION, payload);
                ll.add(np);
            }
        }
        return ll;
    }
}
