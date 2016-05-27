package com.worldpay.facades.order.impl;

import com.worldpay.core.checkout.WorldpayCheckoutService;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.util.List;


public class DefaultWorldpayPaymentCheckoutFacade implements WorldpayPaymentCheckoutFacade {

    private CheckoutFacade checkoutFacade;
    private WorldpayCheckoutService worldpayCheckoutService;
    private CartService cartService;
    private DeliveryService deliveryService;


    /**
     * {@inheritDoc}
     */
    @Override
    public void setBillingDetails(final AddressData addressData) {
        final CartModel cartModel = getCart();
        if (cartModel != null && addressData != null) {
            final AddressModel addressModel = getDeliveryAddressModelForCode(addressData.getId());
            worldpayCheckoutService.setPaymentAddress(cartModel, addressModel);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasBillingDetails() {
        final CartModel cartModel = getCart();
        return cartModel != null && cartModel.getPaymentAddress() != null;
    }

    protected CartModel getCart() {
        return checkoutFacade.hasCheckoutCart() ? cartService.getSessionCart() : null;
    }

    protected AddressModel getDeliveryAddressModelForCode(final String code) {
        Assert.notNull(code, "Parameter code cannot be null.");
        final CartModel cartModel = getCart();
        if (cartModel != null) {
            final List<AddressModel> addresses = getDeliveryService().getSupportedDeliveryAddressesForOrder(cartModel, false);
            if (CollectionUtils.isNotEmpty(addresses)) {
                return getMatchingAddressModel(code, addresses);
            }
        }
        return null;
    }

    protected AddressModel getMatchingAddressModel(final String code, final List<AddressModel> addresses) {
        for (final AddressModel address : addresses) {
            if (code.equals(address.getPk().toString())) {
                return address;
            }
        }
        return null;
    }

    @Required
    public void setCheckoutFacade(CheckoutFacade checkoutFacade) {
        this.checkoutFacade = checkoutFacade;
    }

    @Required
    public CartService getCartService() {
        return cartService;
    }

    @Required
    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public DeliveryService getDeliveryService() {
        return deliveryService;
    }

    @Required
    public void setDeliveryService(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Required
    public void setWorldpayCheckoutService(WorldpayCheckoutService worldpayCheckoutService) {
        this.worldpayCheckoutService = worldpayCheckoutService;
    }
}
