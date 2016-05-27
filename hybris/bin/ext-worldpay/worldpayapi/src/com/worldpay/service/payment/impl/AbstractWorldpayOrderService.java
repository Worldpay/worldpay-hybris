package com.worldpay.service.payment.impl;

import com.worldpay.config.WorldpayConfigLookupService;
import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.service.model.Address;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.strategies.GenerateMerchantTransactionCodeStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Abstract implementation of the {@link WorldpayRedirectOrderService} allows configuration of standard services required
 * by implementors
 */
public abstract class AbstractWorldpayOrderService {

    private CommonI18NService commonI18NService;
    private ModelService modelService;
    private CommerceCheckoutService commerceCheckoutService;
    private CustomerEmailResolutionService customerEmailResolutionService;
    private WorldpayConfigLookupService worldpayConfigLookupService;
    private GenerateMerchantTransactionCodeStrategy worldpayGenerateMerchantTransactionCodeStrategy;


    private WorldpayPaymentInfoService worldpayPaymentInfoService;
    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    private WorldpayOrderService worldpayOrderService;
    private Converter<AddressModel, Address> worldpayAddressConverter;

    /**
     * Creates a {@link CommerceCheckoutParameter} based on the passed {@link CartModel} and {@link PaymentInfoModel} given
     *
     * @param cartModel           The cart to base the commerceCheckoutParameter on
     * @param paymentInfoModel    The paymentInfo to base the commerceCheckoutParameter on
     * @param authorisationAmount The authorised amount by the payment provider
     */
    public CommerceCheckoutParameter createCommerceCheckoutParameter(final CartModel cartModel, final PaymentInfoModel paymentInfoModel, final BigDecimal authorisationAmount) {
        final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
        parameter.setEnableHooks(true);
        parameter.setCart(cartModel);
        parameter.setPaymentInfo(paymentInfoModel);
        parameter.setAuthorizationAmount(authorisationAmount);
        parameter.setPaymentProvider(commerceCheckoutService.getPaymentProvider());
        return parameter;
    }

    /**
     * Workaround: Extra address created when an order is placed
     * Potential bug in class: DefaultCommercePlaceOrderStrategy
     * Method: public CommerceOrderResult placeOrder(CommerceCheckoutParameter parameter) throws InvalidCartException {...}
     * Logic: if(cartModel.getPaymentInfo() != null && cartModel.getPaymentInfo().getBillingAddress() != null) {...}
     */
    public AddressModel cloneAndSetBillingAddressFromCart(final CartModel cartModel, final PaymentInfoModel paymentInfoModel) {
        final AddressModel paymentAddress = cartModel.getPaymentAddress();
        validateParameterNotNull(paymentAddress, "Payment Address cannot be null.");
        final AddressModel clonedAddress = modelService.clone(paymentAddress);
        clonedAddress.setBillingAddress(true);
        clonedAddress.setShippingAddress(false);
        clonedAddress.setOwner(paymentInfoModel);
        paymentInfoModel.setBillingAddress(clonedAddress);
        return clonedAddress;
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public CommerceCheckoutService getCommerceCheckoutService() {
        return commerceCheckoutService;
    }

    @Required
    public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService) {
        this.commerceCheckoutService = commerceCheckoutService;
    }

    public CustomerEmailResolutionService getCustomerEmailResolutionService() {
        return customerEmailResolutionService;
    }

    @Required
    public void setCustomerEmailResolutionService(final CustomerEmailResolutionService customerEmailResolutionService) {
        this.customerEmailResolutionService = customerEmailResolutionService;
    }

    public WorldpayConfigLookupService getWorldpayConfigLookupService() {
        return worldpayConfigLookupService;
    }

    @Required
    public void setWorldpayConfigLookupService(final WorldpayConfigLookupService worldpayConfigLookupService) {
        this.worldpayConfigLookupService = worldpayConfigLookupService;
    }


    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public WorldpayPaymentInfoService getWorldpayPaymentInfoService() {
        return worldpayPaymentInfoService;
    }

    @Required
    public void setWorldpayPaymentInfoService(WorldpayPaymentInfoService worldpayPaymentInfoService) {
        this.worldpayPaymentInfoService = worldpayPaymentInfoService;
    }

    public WorldpayPaymentTransactionService getWorldpayPaymentTransactionService() {
        return worldpayPaymentTransactionService;
    }

    @Required
    public void setWorldpayPaymentTransactionService(WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }

    public WorldpayOrderService getWorldpayOrderService() {
        return worldpayOrderService;
    }

    @Required
    public void setWorldpayOrderService(WorldpayOrderService worldpayOrderService) {
        this.worldpayOrderService = worldpayOrderService;
    }

    public Converter<AddressModel, Address> getWorldpayAddressConverter() {
        return worldpayAddressConverter;
    }

    @Required
    public void setWorldpayAddressConverter(Converter<AddressModel, Address> worldpayAddressConverter) {
        this.worldpayAddressConverter = worldpayAddressConverter;
    }

    public GenerateMerchantTransactionCodeStrategy getWorldpayGenerateMerchantTransactionCodeStrategy() {
        return worldpayGenerateMerchantTransactionCodeStrategy;
    }

    @Required
    public void setWorldpayGenerateMerchantTransactionCodeStrategy(final GenerateMerchantTransactionCodeStrategy worldpayGenerateMerchantTransactionCodeStrategy) {
        this.worldpayGenerateMerchantTransactionCodeStrategy = worldpayGenerateMerchantTransactionCodeStrategy;
    }
}
