package com.worldpay.fulfilmentprocess.actions.order;

import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractAction;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashSet;
import java.util.Set;

import static com.worldpay.fulfilmentprocess.actions.order.WorldpayCheckAuthorizeOrderPaymentAction.Transition.NOK;
import static com.worldpay.fulfilmentprocess.actions.order.WorldpayCheckAuthorizeOrderPaymentAction.Transition.OK;
import static com.worldpay.fulfilmentprocess.actions.order.WorldpayCheckAuthorizeOrderPaymentAction.Transition.WAIT;
import static de.hybris.platform.core.enums.OrderStatus.PAYMENT_AUTHORIZED;
import static de.hybris.platform.core.enums.OrderStatus.PAYMENT_NOT_AUTHORIZED;
import static de.hybris.platform.core.enums.OrderStatus.PAYMENT_PENDING;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;

public class WorldpayCheckAuthorizeOrderPaymentAction extends AbstractAction<OrderProcessModel> {

    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;

    @Override
    public Set<String> getTransitions() {
        return Transition.getStringValues();
    }

    @Override
    public String execute(OrderProcessModel process) {
        final OrderModel order = process.getOrder();

        String returnTransition = null;

        if (order != null) {
            if (order.getPaymentInfo() instanceof InvoicePaymentInfoModel) {
                returnTransition = OK.toString();
            } else {

                if (!worldpayPaymentTransactionService.areAllPaymentTransactionsAcceptedForType(order, AUTHORIZATION)) {
                    setOrderStatus(order, PAYMENT_NOT_AUTHORIZED);
                    return NOK.toString();
                }

                boolean waitForAuthorisation = false;
                // All payment transactions must be authorised and not pending
                // otherwise we wait in the order-process
                for (final PaymentTransactionModel paymentTransaction : order.getPaymentTransactions()) {
                    if (worldpayPaymentTransactionService.isPaymentTransactionPending(paymentTransaction, AUTHORIZATION)) {
                        waitForAuthorisation = true;
                        break;
                    }
                }

                if (waitForAuthorisation) {
                    setOrderStatus(order, PAYMENT_PENDING);
                    returnTransition = WAIT.toString();
                } else {
                    setOrderStatus(order, PAYMENT_AUTHORIZED);
                    returnTransition = OK.toString();
                }
            }
        }
        return returnTransition;
    }

    @Required
    public void setPaymentTransactionService(WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }

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
}
