package com.worldpay.ordercancel.impl.denialstrategies;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.ordercancel.OrderCancelDenialReason;
import de.hybris.platform.ordercancel.OrderCancelDenialStrategy;
import de.hybris.platform.ordercancel.impl.denialstrategies.AbstractCancelDenialStrategy;
import de.hybris.platform.ordercancel.model.OrderCancelConfigModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.apache.log4j.Logger;

import java.util.List;

import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;
import static java.text.MessageFormat.format;

public class WorldpayOrderCancelDenialStrategy extends AbstractCancelDenialStrategy implements OrderCancelDenialStrategy {

    private static final Logger LOG = Logger.getLogger(WorldpayOrderCancelDenialStrategy.class);

    @Override
    public OrderCancelDenialReason getCancelDenialReason(final OrderCancelConfigModel orderCancelConfigModel, final OrderModel orderModel,
                                                         final PrincipalModel principalModel, boolean partialCancel, boolean partialEntryCancel) {
        final List<PaymentTransactionModel> paymentTransactions = orderModel.getPaymentTransactions();
        if (paymentTransactions != null) {
            for (final PaymentTransactionModel paymentTransaction : paymentTransactions) {
                final List<PaymentTransactionEntryModel> entries = paymentTransaction.getEntries();
                for (final PaymentTransactionEntryModel entry : entries) {
                    if (shouldReturnDenialReason(entry)) {
                        LOG.warn(format("The order {0} cannot be cancelled because the transaction entry with requestId {1} is captured", orderModel.getCode(), entry.getRequestId()));
                        return getReason();
                    }
                }
            }
        }
        return null;
    }

    protected boolean shouldReturnDenialReason(final PaymentTransactionEntryModel entry) {
        return !entry.getPending() && entry.getType().equals(CAPTURE);
    }
}
