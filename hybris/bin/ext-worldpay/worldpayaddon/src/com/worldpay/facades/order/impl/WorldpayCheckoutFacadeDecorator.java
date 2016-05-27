package com.worldpay.facades.order.impl;

import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorservices.enums.CheckoutFlowEnum;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

public class WorldpayCheckoutFacadeDecorator implements CheckoutFlowFacade {

    private Converter<AddressModel, AddressData> addressConverter;
    private CartService cartService;
    private CheckoutFlowFacade checkoutFlowFacade;

    public AddressData getBillingAddress() {
        final CartModel cartModel = getSessionCart();
        if (cartModel == null) {
            return null;
        }
        final AddressModel addressModel = cartModel.getPaymentAddress();
        if (addressModel != null) {
            return addressConverter.convert(addressModel);
        }
        return null;
    }

    protected CartModel getSessionCart() {
        if (getCheckoutFlowFacade().hasCheckoutCart()) {
            return getCartService().getSessionCart();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CCPaymentInfoData createPaymentSubscription(CCPaymentInfoData paymentInfoData) {
        return getCheckoutFlowFacade().createPaymentSubscription(paymentInfoData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean authorizePayment(final String securityCode) {
        return getCheckoutFlowFacade().authorizePayment(securityCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNoPaymentInfo() {
        final CartData cartData = getCheckoutCart();
        return cartData == null || (cartData.getPaymentInfo() == null && cartData.getWorldpayAPMPaymentInfo() == null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CheckoutFlowEnum getCheckoutFlow() {
        return getCheckoutFlowFacade().getCheckoutFlow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderData placeOrder() throws InvalidCartException {
        return getCheckoutFlowFacade().placeOrder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CartData getCheckoutCart() {
        return getCheckoutFlowFacade().getCheckoutCart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends AddressData> getSupportedDeliveryAddresses(boolean visibleAddressesOnly) {
        return getCheckoutFlowFacade().getSupportedDeliveryAddresses(visibleAddressesOnly);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setCheapestDeliveryModeForCheckout() {
        return getCheckoutFlowFacade().setCheapestDeliveryModeForCheckout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTaxEstimationEnabledForCart() {
        return getCheckoutFlowFacade().isTaxEstimationEnabledForCart();
    }

    @Override
    public boolean isNewAddressEnabledForCart() {
        return getCheckoutFlowFacade().isNewAddressEnabledForCart();
    }

    @Override
    public boolean isRemoveAddressEnabledForCart() {
        return getCheckoutFlowFacade().isRemoveAddressEnabledForCart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setPaymentDetails(String paymentInfoId) {
        final boolean result = getCheckoutFlowFacade().setPaymentDetails(paymentInfoId);
        final CartModel sessionCart = getSessionCart();
        sessionCart.setPaymentAddress(sessionCart.getPaymentInfo().getBillingAddress());
        cartService.saveOrder(sessionCart);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasCheckoutCart() {
        return getCheckoutFlowFacade().hasCheckoutCart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasShippingItems() {
        return getCheckoutFlowFacade().hasShippingItems();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PointOfServiceData> getConsolidatedPickupOptions() {
        return getCheckoutFlowFacade().getConsolidatedPickupOptions();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends DeliveryModeData> getSupportedDeliveryModes() {
        return getCheckoutFlowFacade().getSupportedDeliveryModes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeDeliveryMode() {
        return getCheckoutFlowFacade().removeDeliveryMode();
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    public CountryData getCountryForIsocode(String countryIso) {
        return getCheckoutFlowFacade().getCountryForIsocode(countryIso);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setDeliveryMode(String deliveryModeCode) {
        return getCheckoutFlowFacade().setDeliveryMode(deliveryModeCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CountryData> getDeliveryCountries() {
        return getCheckoutFlowFacade().getDeliveryCountries();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setDeliveryAddressIfAvailable() {
        return getCheckoutFlowFacade().setDeliveryAddressIfAvailable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsTaxValues() {
        return getCheckoutFlowFacade().containsTaxValues();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CardTypeData> getSupportedCardTypes() {
        return getCheckoutFlowFacade().getSupportedCardTypes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setDeliveryModeIfAvailable() {
        return getCheckoutFlowFacade().setDeliveryModeIfAvailable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setDefaultPaymentInfoForCheckout() {
        return getCheckoutFlowFacade().setDefaultPaymentInfoForCheckout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CheckoutPciOptionEnum getSubscriptionPciOption() {
        return getCheckoutFlowFacade().getSubscriptionPciOption();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNoDeliveryAddress() {
        return getCheckoutFlowFacade().hasNoDeliveryAddress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExpressCheckoutAllowedForCart() {
        return getCheckoutFlowFacade().isExpressCheckoutAllowedForCart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CartModificationData> consolidateCheckoutCart(String pickupPointOfServiceName) throws CommerceCartModificationException {
        return getCheckoutFlowFacade().consolidateCheckoutCart(pickupPointOfServiceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CountryData> getBillingCountries() {
        return getCheckoutFlowFacade().getBillingCountries();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNoDeliveryMode() {
        return getCheckoutFlowFacade().hasNoDeliveryMode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AddressData getDeliveryAddressForCode(String code) {
        return getCheckoutFlowFacade().getDeliveryAddressForCode(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setPaymentInfoIfAvailable() {
        return getCheckoutFlowFacade().setPaymentInfoIfAvailable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setDeliveryAddress(AddressData address) {
        return getCheckoutFlowFacade().setDeliveryAddress(address);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AddressData getAddressDataForId(String addressId, boolean visibleAddressesOnly) {
        return getCheckoutFlowFacade().getAddressDataForId(addressId, visibleAddressesOnly);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExpressCheckoutEnabledForStore() {
        return getCheckoutFlowFacade().isExpressCheckoutEnabledForStore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setDefaultDeliveryAddressForCheckout() {
        return getCheckoutFlowFacade().setDefaultDeliveryAddressForCheckout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AcceleratorCheckoutFacade.ExpressCheckoutResult performExpressCheckout() {
        return getCheckoutFlowFacade().performExpressCheckout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCheckoutFlowGroupForCheckout() {
        return getCheckoutFlowFacade().getCheckoutFlowGroupForCheckout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeDeliveryAddress() {
        return getCheckoutFlowFacade().removeDeliveryAddress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPickUpItems() {
        return getCheckoutFlowFacade().hasPickUpItems();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareCartForCheckout() {
        getCheckoutFlowFacade().prepareCartForCheckout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasValidCart() {
        return getCheckoutFlowFacade().hasValidCart();
    }

    /**
     * Gets checkout flow facade.
     *
     * @return the checkout flow facade
     */
    public CheckoutFlowFacade getCheckoutFlowFacade() {
        return checkoutFlowFacade;
    }

    @Required
    public void setCheckoutFlowFacade(CheckoutFlowFacade checkoutFlowFacade) {
        this.checkoutFlowFacade = checkoutFlowFacade;
    }

    public CartService getCartService() {
        return cartService;
    }

    @Required
    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    @Required
    public void setAddressConverter(Converter<AddressModel, AddressData> addressConverter) {
        this.addressConverter = addressConverter;
    }
}
