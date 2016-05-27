package org.onosproject.net.devicecontrol.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.event.AbstractListenerRegistry;
import org.onosproject.net.devicecontrol.DeviceControlEvent;
import org.onosproject.net.devicecontrol.DeviceControlProviderService;
import org.onosproject.net.devicecontrol.DeviceControlRule;
import org.onosproject.net.devicecontrol.DeviceControlRuleListener;
import org.onosproject.net.devicecontrol.DeviceControlRuleProvider;
import org.onosproject.net.devicecontrol.DeviceControlRuleProviderRegistry;
import org.onosproject.net.devicecontrol.DeviceControlRuleService;
import org.onosproject.net.provider.AbstractProviderRegistry;
import org.onosproject.net.provider.AbstractProviderService;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by aca on 6/19/15.
 */
@Component(immediate = true)
@Service
public class DeviceControlRuleManager
        extends AbstractProviderRegistry<DeviceControlRuleProvider, DeviceControlProviderService>
        implements DeviceControlRuleService, DeviceControlRuleProviderRegistry {
    private final Logger log = getLogger(getClass());

    private final AbstractListenerRegistry<DeviceControlEvent, DeviceControlRuleListener>
            listenerRegistry = new AbstractListenerRegistry<>();

    @Activate
    public void activate() {
        log.info("Activated");
    }

    @Deactivate
    public void deactivate() {
        log.info("Deactivated");
    }

    @Override
    protected DeviceControlProviderService createProviderService(DeviceControlRuleProvider provider) {
        return new InternalDeviceControlRuleProviderService(provider);
    }

    @Override
    public void applyDeviceControlRules(DeviceControlRule... deviceControlRules) {
        if ((deviceControlRules != null) && (deviceControlRules.length > 0)) {
            final DeviceControlRuleProvider deviceControlRuleProvider = getProvider(deviceControlRules[0].deviceId());
            for (DeviceControlRule deviceControlRule : deviceControlRules) {
                deviceControlRuleProvider.applyDeviceControlRule(deviceControlRule);
            }
        }
    }

    @Override
    public void addListener(DeviceControlRuleListener listener) {
        listenerRegistry.addListener(listener);
    }

    @Override
    public void removeListener(DeviceControlRuleListener listener) {
        listenerRegistry.removeListener(listener);
    }

    private class InternalDeviceControlRuleProviderService
            extends AbstractProviderService<DeviceControlRuleProvider>
            implements DeviceControlProviderService {

        /**
         * Creates a provider service on behalf of the specified provider.
         *
         * @param provider provider to which this service is being issued
         */
        protected InternalDeviceControlRuleProviderService(DeviceControlRuleProvider provider) {
            super(provider);
        }
    }
}
