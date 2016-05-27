package com.worldpay.service.apm.strategy.impl;

import com.worldpay.core.services.WorldpayBankConfigurationLookupService;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.model.WorldpayBankConfigurationModel;
import com.worldpay.service.apm.strategy.APMAvailabilityStrategy;
import de.hybris.platform.core.model.order.CartModel;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

/**
 * {@see APMAvailabilityStrategy}
 * <p>
 * Strategy that checks the availability of an APM based on the countries defined in an {@link WorldpayAPMConfigurationModel}
 */
public class APMAvailabilityBankStrategy implements APMAvailabilityStrategy {

    private WorldpayBankConfigurationLookupService worldpayBankConfigurationLookupService;

    /**
     * @param apmConfiguration The configuration of the APM to check {@link WorldpayAPMConfigurationModel}
     * @param cartModel        The current order {@link CartModel} used to get the country to send the order to.
     * @return
     */
    @Override
    public boolean isAvailable(final WorldpayAPMConfigurationModel apmConfiguration, final CartModel cartModel) {
        if (!apmConfiguration.getBank()) {
            return true;
        } else {
            final List<WorldpayBankConfigurationModel> activeBankConfigurationsForCode = worldpayBankConfigurationLookupService.getActiveBankConfigurationsForCode(apmConfiguration.getCode());
            return !activeBankConfigurationsForCode.isEmpty();
        }
    }

    @Required
    public void setWorldpayBankConfigurationLookupService(WorldpayBankConfigurationLookupService worldpayBankConfigurationLookupService) {
        this.worldpayBankConfigurationLookupService = worldpayBankConfigurationLookupService;
    }
}
