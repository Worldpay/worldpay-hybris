package com.worldpay.widgets.controllers;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cscockpit.widgets.controllers.impl.DefaultOrderManagementActionsWidgetController;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;

public class WorldpayOrderManagementActionsWidgetController extends DefaultOrderManagementActionsWidgetController {

    @Override
    public boolean isRefundPossible() {
        boolean isRefundPossible = invokeSuperIsRefundPossible();
        // Additional logic to check whether order is isRefundPossible
        // All payment transactions must be refundable
        if (isRefundPossible) {
            final OrderModel order = (OrderModel) getOrder().getObject();
            for (final PaymentTransactionModel paymentTransaction : order.getPaymentTransactions()) {
                if (!isPaymentTransactionRefundable(paymentTransaction)) {
                    isRefundPossible = false;
                    break;
                }
            }
        }
        return isRefundPossible;
    }

    protected boolean invokeSuperIsRefundPossible() {
        return super.isRefundPossible();
    }

    protected boolean isPaymentTransactionRefundable(PaymentTransactionModel paymentTransaction) {
        boolean isRefundable = false;
        // A payment transaction is refundable when a non-pending CAPTURE entry is found
        for (final PaymentTransactionEntryModel paymentTransactionEntry : paymentTransaction.getEntries()) {
            if (paymentTransactionEntry.getType().equals(CAPTURE) && !paymentTransactionEntry.getPending()) {
                isRefundable = true;
                break;
            }
        }
        return isRefundable;
    }
}
