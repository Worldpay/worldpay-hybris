package com.worldpay.service.apm.strategy.impl;

import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.apm.strategy.APMAvailabilityStrategy;
import com.worldpay.strategy.WorldpayDeliveryAddressStrategy;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.CartModel;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@see APMAvailabilityStrategy}
 * <p>
 * Strategy that checks the availability of an APM based on the countries defined in an {@link WorldpayAPMConfigurationModel}
 */
public class APMAvailabilityCountryStrategy implements APMAvailabilityStrategy {

    private WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategy;

    /**
     * {@inheritDoc}
     * <p>
     * Checks the availability of the {@param apmConfiguration} depending on the countries the APM is configured.
     * <p>
     * Follows the rules, in order:
     * 1. always available if no countries specified in the apmConfiguration
     * 2. available if the billing address country matches at least one apmConfiguration country
     * 3. if at least one country in apmConfiguration and billing address was not matched, the APM is not available
     *
     * @param apmConfiguration The configuration of the APM to check {@link WorldpayAPMConfigurationModel}
     * @param cartModel        The current order {@link CartModel} used to get the country to send the order to.
     * @return
     */
    @Override
    public boolean isAvailable(final WorldpayAPMConfigurationModel apmConfiguration, final CartModel cartModel) {

        // rule 1. always available if no countries specified in the apmConfiguration
        if (CollectionUtils.isEmpty(apmConfiguration.getCountries())) {
            return true;
        }

        // rule 2. available if the shipping address country matches at least one apmConfiguration country
        final String shippingCountryIsoCode = worldpayDeliveryAddressStrategy.getDeliveryAddress(cartModel).getCountry().getIsocode();
        for (final CountryModel countryModel : apmConfiguration.getCountries()) {
            if (shippingCountryIsoCode.equals(countryModel.getIsocode())) {
                return true;
            }
        }

        // rule 3. if at least one country in apmConfiguration and billing address was not matched return false
        return false;
    }

    @Required
    public void setWorldpayDeliveryAddressStrategy(final WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategy) {
        this.worldpayDeliveryAddressStrategy = worldpayDeliveryAddressStrategy;
    }
}
