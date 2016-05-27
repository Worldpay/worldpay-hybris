package com.worldpay.voidprocess.actions.order;

import com.worldpay.voidprocess.model.WorldpayVoidProcessModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractAction;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashSet;
import java.util.Set;

import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;

public class WorldpayVoidOrderAction extends AbstractAction<WorldpayVoidProcessModel> {

    private PaymentService paymentService;

    @Override
    public String execute(final WorldpayVoidProcessModel worldpayVoidProcessModel) {
        final OrderModel order = worldpayVoidProcessModel.getOrder();
        boolean allCancelled = true;
        for (final PaymentTransactionModel paymentTransactionModel : order.getPaymentTransactions()) {
            boolean canCancel = true;
            for (final PaymentTransactionEntryModel paymentTransactionEntryModel : paymentTransactionModel.getEntries()) {
                if (!AUTHORIZATION.equals(paymentTransactionEntryModel.getType()) && !CAPTURE.equals(paymentTransactionEntryModel.getType())) {
                    canCancel = false;
                }
            }
            if (canCancel) {
                paymentService.cancel(paymentTransactionModel.getEntries().get(0));
            } else {
                allCancelled = false;
            }
        }
        return allCancelled ? "OK" : "NOK";
    }

    @Override
    public Set<String> getTransitions() {
        return Transition.getStringValues();
    }

    public enum Transition {
        OK, NOK;

        public static Set<String> getStringValues() {
            final Set<String> res = new HashSet<>();
            for (final Transition transitions : Transition.values()) {
                res.add(transitions.toString());
            }
            return res;
        }
    }

    @Required
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
