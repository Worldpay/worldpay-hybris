package com.worldpay.notification.processors.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.notification.processors.OrderNotificationProcessorStrategy;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.support.TransactionOperations;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static java.text.MessageFormat.format;

/**
 * The implementation of {@link OrderNotificationProcessorStrategy} that processes Authorised notifications.
 */
public class DefaultAuthorisedOrderNotificationProcessorStrategy implements OrderNotificationProcessorStrategy {

    private static final Logger LOG = Logger.getLogger(DefaultAuthorisedOrderNotificationProcessorStrategy.class);

    private ModelService modelService;
    private TransactionOperations transactionTemplate;
    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    private WorldpayPaymentInfoService worldpayPaymentInfoService;

    /**
     * {@inheritDoc}
     *
     * @see OrderNotificationProcessorStrategy#processNotificationMessage(PaymentTransactionModel, OrderNotificationMessage)
     */
    @Override

    public void processNotificationMessage(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage) {
        if (LOG.isDebugEnabled()) {
            final String orderCode = orderNotificationMessage.getOrderCode();
            LOG.debug(format("Message for order having code {0} is a success, saving card and changing transaction status to not pending.", orderCode));
        }

        final BigDecimal plannedAmount = convertAmount(orderNotificationMessage.getPaymentReply().getAmount());
        paymentTransactionModel.setPlannedAmount(plannedAmount);
        final AbstractOrderModel orderModel = paymentTransactionModel.getOrder();

        final List<PaymentTransactionEntryModel> paymentTransactionEntries = worldpayPaymentTransactionService.getPendingPaymentTransactionEntriesForType(paymentTransactionModel, AUTHORIZATION);
        transactionTemplate.execute(transactionStatus -> {
            updatePaymentTransactionEntry(paymentTransactionModel, orderNotificationMessage, paymentTransactionEntries, ACCEPTED.name());
            worldpayPaymentInfoService.setPaymentInfoModel(paymentTransactionModel, orderModel, orderNotificationMessage);
            return null;
        });
    }

    protected BigDecimal convertAmount(final Amount amount) {
        final Currency currency = Currency.getInstance(amount.getCurrencyCode());
        return new BigDecimal(amount.getValue()).movePointLeft(currency.getDefaultFractionDigits());
    }

    protected void updatePaymentTransactionEntry(PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage,
                                                 final List<PaymentTransactionEntryModel> paymentTransactionEntries, final String transactionStatus) {
        final PaymentReply paymentReply = orderNotificationMessage.getPaymentReply();
        worldpayPaymentTransactionService.addRiskScore(paymentTransactionModel, paymentReply);
        paymentTransactionModel.setApmOpen(Boolean.FALSE);

        paymentTransactionEntries.forEach(paymentTransactionEntryModel -> worldpayPaymentTransactionService.addAavFields(paymentTransactionEntryModel, paymentReply));
        worldpayPaymentTransactionService.updateEntriesStatus(paymentTransactionEntries, transactionStatus);
        worldpayPaymentTransactionService.updateEntriesAmount(paymentTransactionEntries, orderNotificationMessage.getPaymentReply().getAmount());
        modelService.save(paymentTransactionModel);
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    @Required
    public void setWorldpayPaymentInfoService(WorldpayPaymentInfoService worldpayPaymentInfoService) {
        this.worldpayPaymentInfoService = worldpayPaymentInfoService;
    }

    @Required
    public void setWorldpayPaymentTransactionService(WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }

    @Required
    public void setTransactionTemplate(TransactionOperations transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}
