package org.onosproject.net.sensorflow;

import org.onosproject.core.DefaultGroupId;
import org.onosproject.core.GroupId;
import org.onosproject.net.DeviceId;
import org.onosproject.net.flow.FlowId;
import org.onosproject.net.flow.FlowRule;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;

/**
 * Created by aca on 3/5/15.
 */
public class SensorDefaultFlowRule implements FlowRule {
    private final DeviceId deviceId;
    private final int priority;
    private final TrafficSelector selector;
    private final TrafficTreatment treatment;
    private final long created;

    private final FlowId id;

    private final short appId;

    private final int timeout;
    private final boolean permanent;
    private final GroupId groupId;

    public SensorDefaultFlowRule(DeviceId deviceId, TrafficSelector selector,
                                 TrafficTreatment treatment, int priority, long flowId,
                                 int timeout, boolean permanent) {
        this.deviceId = deviceId;
        this.priority = priority;
        this.selector = selector;
        this.treatment = treatment;
        this.timeout = timeout;
        this.permanent = permanent;
        this.created = System.currentTimeMillis();

        this.appId = (short) (flowId >>> 48);
        this.groupId = new DefaultGroupId((short) ((flowId >>> 32) & 0xFFFF));
        this.id = FlowId.valueOf(flowId);
    }

    @Override
    public FlowId id() {
        return this.id;
    }

    @Override
    public short appId() {
        return this.appId;
    }

    @Override
    public GroupId groupId() {
        return this.groupId;
    }

    @Override
    public int priority() {
        return this.priority;
    }

    @Override
    public DeviceId deviceId() {
        return this.deviceId;
    }

    @Override
    public TrafficSelector selector() {
        return this.selector;
    }

    @Override
    public TrafficTreatment treatment() {
        return this.treatment;
    }

    @Override
    public int timeout() {
        return this.timeout;
    }

    @Override
    public boolean isPermanent() {
        return this.permanent;
    }
}
