package com.worldpay.cscockpit.configuration;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import de.hybris.platform.cockpit.services.values.ValueHandlerException;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.cscockpit.services.config.impl.AbstractSimpleCustomColumnConfiguration;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import org.springframework.beans.factory.annotation.Required;

import java.util.Locale;

public class PaymentInfoPaymentTypeColumn extends AbstractSimpleCustomColumnConfiguration<String, PaymentTransactionEntryModel> {

    protected static final String UNKNOWN = "UNKNOWN";
    private APMConfigurationLookupService apmConfigurationLookupService;

    @Override
    protected String getItemValue(final PaymentTransactionEntryModel paymentTransactionEntryModel, final Locale locale) throws ValueHandlerException {
        final PaymentInfoModel paymentInfoModel = paymentTransactionEntryModel.getPaymentTransaction().getInfo();

        String paymentType = UNKNOWN;

        if (paymentInfoModel != null) {
            paymentType = paymentInfoModel.getPaymentType();
        }

        if (paymentInfoModel instanceof WorldpayAPMPaymentInfoModel) {
            final WorldpayAPMConfigurationModel apmConfiguration = apmConfigurationLookupService.getAPMConfigurationForCode(paymentType);
            paymentType = apmConfiguration.getName();
        }

        return paymentType;
    }

    @Required
    public void setApmConfigurationLookupService(final APMConfigurationLookupService apmConfigurationLookupService) {
        this.apmConfigurationLookupService = apmConfigurationLookupService;
    }
}
