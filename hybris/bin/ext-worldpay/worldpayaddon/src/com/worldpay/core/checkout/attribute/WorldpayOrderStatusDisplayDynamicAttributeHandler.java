package com.worldpay.core.checkout.attribute;

import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhandler.DynamicAttributesOrderStatusDisplayByMap;
import org.springframework.beans.factory.annotation.Required;

public class WorldpayOrderStatusDisplayDynamicAttributeHandler extends DynamicAttributesOrderStatusDisplayByMap {

    protected static final String APM_OPEN = "APM_OPEN";

    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(final OrderModel order) {
        String result;

        if (order != null && worldpayPaymentTransactionService.isAnyPaymentTransactionApmOpenForOrder(order)) {
            result = getStatusDisplayMap().get(APM_OPEN);
        } else {
            result = invokeSuperGet(order);
        }

        return result;
    }

    protected String invokeSuperGet(final OrderModel order) {
        return super.get(order);
    }

    @Required
    public void setWorldpayPaymentTransactionService(final WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }
}
