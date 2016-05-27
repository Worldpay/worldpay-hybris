package com.worldpay.core.services.impl;

import com.worldpay.core.dao.WorldpayPaymentTransactionDao;
import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.notification.processors.OrderNotificationProcessorStrategy;
import com.worldpay.service.model.AuthorisedStatus;
import com.worldpay.service.notification.OrderNotificationMessage;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;

import static java.text.MessageFormat.format;

/**
 * Default implementation of {@link OrderNotificationService}
 */
public class DefaultOrderNotificationService implements OrderNotificationService {

    private static final Logger LOG = Logger.getLogger(DefaultOrderNotificationService.class);

    private WorldpayPaymentTransactionDao worldpayPaymentTransactionDao;
    private Map<AuthorisedStatus, OrderNotificationProcessorStrategy> journalTypeToNotificationProcessorStrategyMap;

    @Override
    public void processOrderNotificationMessage(final OrderNotificationMessage orderNotificationMessage) {
        final AuthorisedStatus journalTypeCode = orderNotificationMessage.getJournalReply().getJournalType();
        final OrderNotificationProcessorStrategy orderNotificationProcessorStrategy = journalTypeToNotificationProcessorStrategyMap.get(journalTypeCode);

        if (orderNotificationProcessorStrategy != null) {
            final PaymentTransactionModel paymentTransactionModel = worldpayPaymentTransactionDao.findPaymentTransactionByRequestIdFromOrdersOnly(orderNotificationMessage.getOrderCode());
            orderNotificationProcessorStrategy.processNotificationMessage(paymentTransactionModel, orderNotificationMessage);
        } else {
            LOG.warn(format("Could not find notification processor for journal type code [{0}]. " +
                    "It's either an unsupported journal type or there is a configuration problem.", journalTypeCode.getCode()));
        }
    }

    @Required
    public void setWorldpayPaymentTransactionDao(final WorldpayPaymentTransactionDao worldpayPaymentTransactionDao) {
        this.worldpayPaymentTransactionDao = worldpayPaymentTransactionDao;
    }

    @Required
    public void setJournalTypeToNotificationProcessorStrategyMap(final Map<AuthorisedStatus, OrderNotificationProcessorStrategy> journalTypeToNotificationProcessorStrategyMap) {
        this.journalTypeToNotificationProcessorStrategyMap = journalTypeToNotificationProcessorStrategyMap;
    }
}
