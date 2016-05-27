package com.worldpay.fulfilmentprocess.actions.order;

import static de.hybris.platform.core.enums.OrderStatus.PAYMENT_CAPTURED;
import static de.hybris.platform.core.enums.OrderStatus.PAYMENT_NOT_CAPTURED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractAction;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class WorldpayTakePaymentAction extends AbstractAction<OrderProcessModel> {
    private static final Logger LOG = Logger.getLogger(WorldpayTakePaymentAction.class);

    private PaymentService paymentService;
    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;

    public enum Transition {
        OK, NOK, WAIT;

        public static Set<String> getStringValues() {
            final Set<String> res = new HashSet<>();
            for (final Transition transitions : Transition.values()) {
                res.add(transitions.toString());
            }
            return res;
        }
    }

    @Override
    public Set<String> getTransitions() {
        return Transition.getStringValues();
    }

    //assumption that there is only ever one capture per transaction
    @Override
    public String execute(OrderProcessModel process) {
        final OrderModel order = process.getOrder();
        for (final PaymentTransactionModel paymentTransaction : order.getPaymentTransactions()) {
            final List<PaymentTransactionEntryModel> entries = worldpayPaymentTransactionService.filterPaymentTransactionEntriesOfType(paymentTransaction, CAPTURE);
            if (entries.isEmpty()) {
                return handleEmptyTransactionEntries(paymentTransaction);
            } else if (entries.size() != 1) {
                return handleMultipleTransactionEntries(process, order, paymentTransaction, entries);
            }
            if (checkForNotAccepted(order, paymentTransaction)) {
                return Transition.NOK.name();
            }
            if (checkForPendingStatus(order, paymentTransaction)) {
                return Transition.WAIT.name();
            }
        }
        setOrderStatus(order, PAYMENT_CAPTURED);
        return Transition.OK.name();
    }

    protected String handleMultipleTransactionEntries(OrderProcessModel process, OrderModel order, PaymentTransactionModel paymentTransaction, List<PaymentTransactionEntryModel> entries) {
        LOG.error(MessageFormat.format("Found {0} PaymentTransactionEntries with " +
                "type CAPTURE for PaymentTransaction with code {1} and orderProcess " +
                "with code {2}. There should be exactly 1", entries.size(), paymentTransaction.getCode(), process.getCode()));
        setOrderStatus(order, PAYMENT_NOT_CAPTURED);
        return Transition.NOK.name();
    }

    protected String handleEmptyTransactionEntries(PaymentTransactionModel paymentTransaction) {
        final PaymentInfoModel paymentTransactionInfo = paymentTransaction.getInfo();
        if (paymentTransactionInfo == null || !paymentTransactionInfo.getIsApm()) {
            paymentService.capture(paymentTransaction);
        }
        // Always go back to wait-state, as the notification will not have been received at this point.
        return Transition.WAIT.name();
    }

    protected boolean checkForPendingStatus(OrderModel order, PaymentTransactionModel txn) {
        // wait if any capture entries are still pending
        if (worldpayPaymentTransactionService.isPaymentTransactionPending(txn, CAPTURE)) {
            LOG.info(MessageFormat.format("Still waiting for capture on order: {0} ", order));
            return true;
        }
        return false;
    }

    protected boolean checkForNotAccepted(OrderModel order, PaymentTransactionModel txn) {
        // NOK if all capture entries have not been accepted
        if (!worldpayPaymentTransactionService.areAllPaymentTransactionsAcceptedForType(order, CAPTURE)) {
            LOG.error("The payment transaction capture has failed. Order: " + order.getCode() + ". Txn: " + txn.getCode());
            setOrderStatus(order, PAYMENT_NOT_CAPTURED);
            return true;
        }
        return false;
    }

    @Required
    public void setPaymentService(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Required
    public void setWorldpayPaymentTransactionService(WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }
}

