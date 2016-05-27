package com.worldpay.fulfilmentprocess.actions.order;

import static de.hybris.platform.core.enums.OrderStatus.FRAUD_CHECKED;

import java.util.HashSet;
import java.util.Set;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;

public class WorldpayOrderManualCheckedAction extends WorldpayAbstractOrderAction<OrderProcessModel> {
    public enum Transition {
        OK, NOK, UNDEFINED;

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

    @Override
    public final String execute(final OrderProcessModel process) {
        ServicesUtil.validateParameterNotNull(process, "Process cannot be null");

        final OrderModel order = process.getOrder();
        ServicesUtil.validateParameterNotNull(order, "Order in process cannot be null");
        if (order.getFraudulent() != null) {
            final OrderHistoryEntryModel historyLog = createHistoryLog(
                    "Order Manually checked by CSA - Fraud = " + order.getFraudulent(), order);
            modelService.save(historyLog);
            if (order.getFraudulent().booleanValue()) {
                setOrderStatus(order, OrderStatus.SUSPENDED);
                return Transition.NOK.toString();
            }
            setOrderStatus(order, FRAUD_CHECKED);
            return Transition.OK.toString();
        }
        return Transition.UNDEFINED.toString();
    }
}
