package org.onosproject.net.sensorflow;

import com.google.common.base.MoreObjects;
import org.onosproject.net.Link;
import org.onosproject.net.Path;
import org.onosproject.net.flow.instructions.Instruction;
import org.slf4j.Logger;

import java.util.List;

import static com.google.common.base.MoreObjects.toStringHelper;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by aca on 3/12/15.
 */
public class SensorFlowOpenPathInstruction implements SensorFlowInstruction {
    private final Logger log = getLogger(getClass());
    private Path path;

    public SensorFlowOpenPathInstruction(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return this.path;
    }

    @Override
    public Type getSensorFlowInstructionType() {
        return Type.OPEN_PATH;
    }

    @Override
    public boolean multimatch() {
        return false;
    }

    @Override
    public Instruction.Type type() {
        return null;
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper stringHelper =
                toStringHelper(getSensorFlowInstructionType().toString());

        List<Link> pathLinks = path.links();
        String deviceIds = "[ ";
        for (Link link : pathLinks) {
            deviceIds = deviceIds + link.dst().deviceId() + " ";
        }
        deviceIds = deviceIds + "]";

        stringHelper.add("path", deviceIds);
        return stringHelper.toString();
    }
}
