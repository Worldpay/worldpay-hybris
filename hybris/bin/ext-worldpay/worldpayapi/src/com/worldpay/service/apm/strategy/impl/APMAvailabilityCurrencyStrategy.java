package com.worldpay.service.apm.strategy.impl;

import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.apm.strategy.APMAvailabilityStrategy;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import org.apache.commons.collections.CollectionUtils;

/**
 * {@see APMAvailabilityStrategy}
 *
 * Strategy that checks the availability of an APM based on the currencies defined in an {@link WorldpayAPMConfigurationModel}
 */
public class APMAvailabilityCurrencyStrategy implements APMAvailabilityStrategy {

    /**
     * {@inheritDoc}
     * <p>
     * Checks the availability of the {@param apmConfiguration} depending on the currencies the APM is configured.
     * <p>
     * Follows the rules, in order:
     * 1. always available if no currencies specified in the apmConfiguration
     * 2. available if the cart currency matches at least one apmConfiguration currency
     * 3. if at least one currency in apmConfiguration and cart was not matched the APM is not available
     *
     * @param apmConfiguration The configuration of the APM to check {@link WorldpayAPMConfigurationModel}
     * @param cartModel        The current order {@link CartModel} used to get the country to send the order to.
     * @return
     */
    @Override
    public boolean isAvailable(final WorldpayAPMConfigurationModel apmConfiguration, final CartModel cartModel) {
        // rule 1. always available if no currencies specified in the apmConfiguration
        if (CollectionUtils.isEmpty(apmConfiguration.getCurrencies())) {
            return true;
        }

        // rule 2. available if the cart currency matches at least one apmConfiguration currency
        final String cartCurrencyIso = cartModel.getCurrency().getIsocode();
        for (final CurrencyModel currencyModel : apmConfiguration.getCurrencies()) {
            if (cartCurrencyIso.equals(currencyModel.getIsocode())) {
                return true;
            }
        }

        // rule 3. if at least one currency in apmConfiguration and cart was not matched return false
        return false;
    }
}
