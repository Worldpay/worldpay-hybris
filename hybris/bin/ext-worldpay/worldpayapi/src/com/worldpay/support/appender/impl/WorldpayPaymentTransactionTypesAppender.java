package com.worldpay.support.appender.impl;

import com.worldpay.support.WorldpayCronJobSupportInformationService;
import com.worldpay.support.appender.WorldpaySupportEmailAppender;
import de.hybris.platform.core.Registry;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;

import java.util.Set;

/**
 * Implementation of {@see WorldpaySupportEmailAppender } to include the list of payment transactions processed.
 */
public class WorldpayPaymentTransactionTypesAppender implements WorldpaySupportEmailAppender {

    private static final Logger LOG = Logger.getLogger(WorldpayPaymentTransactionTypesAppender.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String appendContent() {
        final StringBuilder processedPaymentTransactionTypes = new StringBuilder();
        final WorldpayCronJobSupportInformationService worldpayCronJobSupportInformationService = getWorldpayCronJobSupportInformationService();
        if (worldpayCronJobSupportInformationService != null) {
            final Set<PaymentTransactionType> paymentTransactionTypes = worldpayCronJobSupportInformationService.getPaymentTransactionType();
            processedPaymentTransactionTypes.append(System.lineSeparator()).append("Payment Transaction Types: ").append(System.lineSeparator());
            for (final PaymentTransactionType paymentTransactionType : paymentTransactionTypes) {
                processedPaymentTransactionTypes.append(TAB).append(paymentTransactionType.getCode()).append(System.lineSeparator());
            }
        }
        return processedPaymentTransactionTypes.toString();
    }

    protected WorldpayCronJobSupportInformationService getWorldpayCronJobSupportInformationService() {
        try {
            return Registry.getApplicationContext().getBean("worldpayCronJobSupportInformationService", WorldpayCronJobSupportInformationService.class);
        } catch (final BeansException e) {
            LOG.info("The WorldpayCronJobSupportInformationService is not in the application context, so the payment transaction types processed cannot be added to the support email.", e);
            return null;
        }
    }
}
