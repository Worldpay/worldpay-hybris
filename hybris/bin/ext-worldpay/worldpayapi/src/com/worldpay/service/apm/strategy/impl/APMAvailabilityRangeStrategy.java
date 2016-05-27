package com.worldpay.service.apm.strategy.impl;

import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.model.WorldpayCurrencyRangeModel;
import com.worldpay.service.apm.strategy.APMAvailabilityStrategy;
import de.hybris.platform.core.model.order.CartModel;
import org.apache.commons.collections.CollectionUtils;

/**
 * {@see APMAvailabilityStrategy}
 *
 * Strategy that checks the availability of an APM based on the currency ranges defined in an {@link WorldpayAPMConfigurationModel}
 */
public class APMAvailabilityRangeStrategy implements APMAvailabilityStrategy {

    /**
     * {@inheritDoc}
     * <p>
     * Checks the availability of the {@param apmConfiguration} depending on the currency ranges the APM is configured.
     * <p>
     * Follows the rules, in order:
     * 1. always available if no currencies specified in the apmConfiguration
     * 2. available if the cart total price is within the defined range in the APM
     * 3. if there are no currency ranges for the selected currency, then the APM is available.
     *
     * @param apmConfiguration The configuration of the APM to check {@link WorldpayAPMConfigurationModel}
     * @param cartModel        The current order {@link CartModel} used to get the country to send the order to.
     * @return
     */
    @Override
    public boolean isAvailable(final WorldpayAPMConfigurationModel apmConfiguration, final CartModel cartModel) {
        // 1. always available if no currency ranges specified in the apmConfiguration
        if (CollectionUtils.isEmpty(apmConfiguration.getCurrencyRanges())) {
            return true;
        }

        // 2. available if the cart total price is within the defined range in the APM
        final String cartCurrencyIso = cartModel.getCurrency().getIsocode();
        for (final WorldpayCurrencyRangeModel currencyRangeModel : apmConfiguration.getCurrencyRanges()) {
            if (cartCurrencyIso.equals(currencyRangeModel.getCurrency().getIsocode())) {
                return isCartTotalWithinRange(cartModel.getTotalPrice(), currencyRangeModel.getMin(), currencyRangeModel.getMax());
            }
        }
        // 3. if there are no currency ranges for the selected currency, then the APM is available.
        return true;
    }

    private boolean isCartTotalWithinRange(final Double totalPrice, final Double min, final Double max) {
        return isWithinMinRange(totalPrice, min) && isWithinMaxRange(totalPrice, max);
    }

    private boolean isWithinMaxRange(final Double totalPrice, final Double max) {
        return max == null || totalPrice.compareTo(max) <= 0;
    }

    private boolean isWithinMinRange(final Double totalPrice, final Double min) {
        return min == null || totalPrice.compareTo(min) >= 0;
    }
}
