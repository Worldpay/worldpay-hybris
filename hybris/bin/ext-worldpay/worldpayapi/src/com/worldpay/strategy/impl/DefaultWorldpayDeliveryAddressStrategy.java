package com.worldpay.strategy.impl;

import com.worldpay.strategy.WorldpayDeliveryAddressStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.exceptions.ConfigurationException;

import java.util.List;

import static java.text.MessageFormat.format;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayDeliveryAddressStrategy implements WorldpayDeliveryAddressStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public AddressModel getDeliveryAddress(final CartModel cartModel) {
        if (cartModel.getDeliveryAddress() == null) {
            final List<AbstractOrderEntryModel> entries = cartModel.getEntries();
            for (final AbstractOrderEntryModel entry : entries) {
                if (entry.getDeliveryPointOfService() != null && entry.getDeliveryPointOfService().getAddress() != null) {
                    return entry.getDeliveryPointOfService().getAddress();
                }
            }
            throw new ConfigurationException(format("Checkout was attempted without a valid pickup or shipping address for cart with id [{0}]", cartModel.getCode()));
        } else {
            return cartModel.getDeliveryAddress();
        }
    }
}
