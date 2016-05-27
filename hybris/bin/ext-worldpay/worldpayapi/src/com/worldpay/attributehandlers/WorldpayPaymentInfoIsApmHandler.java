package com.worldpay.attributehandlers;

import com.worldpay.core.services.APMConfigurationLookupService;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import org.springframework.beans.factory.annotation.Required;

import java.util.Set;

/**
 * Dynamic attribute handler for the property isApm in the extended PaymentInfoModel
 */
public class WorldpayPaymentInfoIsApmHandler implements DynamicAttributeHandler<Boolean, PaymentInfoModel> {

    private APMConfigurationLookupService apmConfigurationLookupService;

    /**
     * Returns true if the paymentType of the PaymentInfoModel is a configured APM. False otherwise.
     * @param model
     * @return
     */
    @Override
    public Boolean get(final PaymentInfoModel model) {
        return getAllAPMCodes().contains(model.getPaymentType());
    }

    protected Set<String> getAllAPMCodes() {
        return apmConfigurationLookupService.getAllApmPaymentTypeCodes();
    }

    /**
     * Not implemented. This value must not be set.
     * @param model
     * @param aBoolean
     */
    @Override
    public void set(final PaymentInfoModel model, final Boolean aBoolean) {
        throw new UnsupportedOperationException("You can not set a value for this property");
    }

    @Required
    public void setApmConfigurationLookupService(final APMConfigurationLookupService apmConfigurationLookupService) {
        this.apmConfigurationLookupService = apmConfigurationLookupService;
    }
}
