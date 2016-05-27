package com.worldpay.facades.order.converters.populators;

import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.beans.factory.annotation.Required;

/**
 * Worldpay specific populator from OrderModel to OrderData.
 */
public class WorldpayOrderDataPopulator implements Populator<OrderModel, OrderData> {

    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;

    /**
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException Populates the target object with the isApmOpen field based on the paymentTransactions of the source
     */
    @Override
    public void populate(final OrderModel source, final OrderData target) throws ConversionException {
        target.setIsApmOpen(worldpayPaymentTransactionService.isAnyPaymentTransactionApmOpenForOrder(source));
    }

    @Required
    public void setWorldpayPaymentTransactionService(final WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }
}
