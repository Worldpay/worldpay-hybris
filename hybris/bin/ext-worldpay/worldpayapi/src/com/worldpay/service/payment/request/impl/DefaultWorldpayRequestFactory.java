package com.worldpay.service.payment.request.impl;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.config.WorldpayConfigLookupService;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.model.Address;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.BasicOrderInfo;
import com.worldpay.service.model.Browser;
import com.worldpay.service.model.Date;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.Session;
import com.worldpay.service.model.Shopper;
import com.worldpay.service.model.payment.Cse;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.payment.PaymentBuilder;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.Token;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpayTokenEventReferenceCreationStrategy;
import com.worldpay.service.payment.request.WorldpayRequestFactory;
import com.worldpay.service.request.CreateTokenServiceRequest;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.strategy.WorldpayDeliveryAddressStrategy;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.strategies.GenerateMerchantTransactionCodeStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

import static org.joda.time.DateTime.now;

public class DefaultWorldpayRequestFactory implements WorldpayRequestFactory {

    private WorldpayTokenEventReferenceCreationStrategy worldpayTokenEventReferenceCreationStrategy;
    private WorldpayOrderService worldpayOrderService;
    private Converter<AddressModel, Address> worldpayAddressConverter;
    private WorldpayConfigLookupService worldpayConfigLookupService;
    private CustomerEmailResolutionService customerEmailResolutionService;
    private GenerateMerchantTransactionCodeStrategy worldpayGenerateMerchantTransactionCodeStrategy;
    private WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategy;

    protected static final String TOKEN_UPDATED = "Token updated ";
    protected static final String TOKEN_UPDATE_DATE_FORMAT = "YYYY-MM-dd";

    /**
     * {@inheritDoc}
     *
     * @see WorldpayRequestFactory#buildTokenRequest(MerchantInfo, CartModel, CSEAdditionalAuthInfo, WorldpayAdditionalInfoData)
     */
    @Override
    public CreateTokenServiceRequest buildTokenRequest(MerchantInfo merchantInfo, CartModel cartModel, CSEAdditionalAuthInfo cseAdditionalAuthInfo,
                                                       WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayConfigurationException {
        final WorldpayConfig worldpayConfig = getWorldpayConfigLookupService().lookupConfig();

        final Address billingAddress = getBillingAddress(cartModel, cseAdditionalAuthInfo);
        final String tokenEventReference = getWorldpayTokenEventReferenceCreationStrategy().createTokenEventReference();
        final TokenRequest tokenRequest = getWorldpayOrderService().createTokenRequest(tokenEventReference, null);
        final Cse csePayment = createCsePayment(cseAdditionalAuthInfo, billingAddress);
        return createTokenRequest(worldpayConfig, merchantInfo, worldpayAdditionalInfoData.getAuthenticatedShopperId(), csePayment, tokenRequest);
    }

    @Override
    public UpdateTokenServiceRequest buildTokenUpdateRequest(final MerchantInfo merchantInfo, final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final CreateTokenResponse createTokenResponse) throws WorldpayConfigurationException {
        final WorldpayConfig worldpayConfig = getWorldpayConfigLookupService().lookupConfig();
        final TokenRequest tokenRequest = getWorldpayOrderService().createTokenRequest(getWorldpayTokenEventReferenceCreationStrategy().createTokenEventReference(), TOKEN_UPDATED + now().toString(TOKEN_UPDATE_DATE_FORMAT));

        final String paymentTokenID = createTokenResponse.getToken().getTokenDetails().getPaymentTokenID();
        final CardDetails cardDetails = new CardDetails();
        final Date expiryDate = new Date(cseAdditionalAuthInfo.getExpiryMonth(), cseAdditionalAuthInfo.getExpiryYear());
        cardDetails.setExpiryDate(expiryDate);
        cardDetails.setCardHolderName(cseAdditionalAuthInfo.getCardHolderName());
        return createUpdateTokenServiceRequest(merchantInfo, worldpayAdditionalInfoData, worldpayConfig, tokenRequest, paymentTokenID, cardDetails);
    }

    /**
     * {@inheritDoc}
     *
     * @see WorldpayRequestFactory#buildDirectAuthoriseRequest(MerchantInfo, CartModel, WorldpayAdditionalInfoData)
     */
    @Override
    public DirectAuthoriseServiceRequest buildDirectAuthoriseRequest(MerchantInfo merchantInfo, CartModel cartModel, WorldpayAdditionalInfoData worldpayAdditionalInfoData)
            throws WorldpayConfigurationException {
        final String orderCode = getWorldpayGenerateMerchantTransactionCodeStrategy().generateCode(cartModel);
        final WorldpayConfig config = getWorldpayConfigLookupService().lookupConfig();
        final Amount amount = getWorldpayOrderService().createAmount(cartModel.getCurrency(), cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = getWorldpayOrderService().createBasicOrderInfo(orderCode, orderCode, amount);

        final Token token = createToken(((CreditCardPaymentInfoModel) cartModel.getPaymentInfo()).getSubscriptionId(), worldpayAdditionalInfoData.getSecurityCode());

        final CustomerModel customerModel = (CustomerModel) cartModel.getUser();

        final String shopperEmailAddress = getCustomerEmailResolutionService().getEmailForCustomer(customerModel);

        final Session session = getWorldpayOrderService().createSession(worldpayAdditionalInfoData);
        final Browser browser = getWorldpayOrderService().createBrowser(worldpayAdditionalInfoData);

        final Shopper authenticatedShopper = getWorldpayOrderService().createAuthenticatedShopper(shopperEmailAddress, worldpayAdditionalInfoData.getAuthenticatedShopperId(), session, browser);
        final AddressModel deliveryAddress = worldpayDeliveryAddressStrategy.getDeliveryAddress(cartModel);
        return createTokenisedDirectAuthoriseRequest(config, merchantInfo, orderInfo, token, authenticatedShopper, getWorldpayAddressConverter().convert(deliveryAddress));
    }

    /**
     * {@inheritDoc}
     *
     * @see WorldpayRequestFactory#build3dDirectAuthoriseRequest(MerchantInfo, CartModel, WorldpayAdditionalInfoData, String, String, String)
     */
    @Override
    public DirectAuthoriseServiceRequest build3dDirectAuthoriseRequest(MerchantInfo merchantInfo, CartModel cartModel,
                                                                       WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                                       String paRes, String echoData, String cookie) throws WorldpayConfigurationException {
        final String orderCode = cartModel.getWorldpayOrderCode();
        final WorldpayConfig config = getWorldpayConfigLookupService().lookupConfig();
        final Amount amount = getWorldpayOrderService().createAmount(cartModel.getCurrency(), cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = getWorldpayOrderService().createBasicOrderInfo(orderCode, orderCode, amount);

        final Token token = createToken(((CreditCardPaymentInfoModel) cartModel.getPaymentInfo()).getSubscriptionId(), worldpayAdditionalInfoData.getSecurityCode());

        final CustomerModel customerModel = (CustomerModel) cartModel.getUser();

        final String shopperEmailAddress = getCustomerEmailResolutionService().getEmailForCustomer(customerModel);

        final Session session = getWorldpayOrderService().createSession(worldpayAdditionalInfoData);
        final Browser browser = getWorldpayOrderService().createBrowser(worldpayAdditionalInfoData);

        final Shopper authenticatedShopper = getWorldpayOrderService().createAuthenticatedShopper(shopperEmailAddress, worldpayAdditionalInfoData.getAuthenticatedShopperId(), session, browser);
        final AddressModel deliveryAddress = worldpayDeliveryAddressStrategy.getDeliveryAddress(cartModel);

        final DirectAuthoriseServiceRequest directAuthoriseServiceRequest = createDirect3DAuthoriseRequest(config, merchantInfo, orderInfo, token,
                authenticatedShopper, session, paRes, echoData, getWorldpayAddressConverter().convert(deliveryAddress));
        directAuthoriseServiceRequest.setCookie(cookie);
        return directAuthoriseServiceRequest;
    }

    /**
     * {@inheritDoc}
     *
     * @see WorldpayRequestFactory#build3dDirectAuthoriseRequest(MerchantInfo, CartModel, WorldpayAdditionalInfoData, String, String, String)
     */
    @Override
    public DirectAuthoriseServiceRequest buildDirectAuthoriseBankTransferRequest(final MerchantInfo merchantInfo, final CartModel cartModel,
                                                                                 final BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfo,
                                                                                 final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayConfigurationException {
        final String orderCode = getWorldpayGenerateMerchantTransactionCodeStrategy().generateCode(cartModel);
        final WorldpayConfig config = getWorldpayConfigLookupService().lookupConfig();

        final Amount amount = getWorldpayOrderService().createAmount(cartModel.getCurrency(), cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = getWorldpayOrderService().createBasicOrderInfo(orderCode, orderCode, amount);

        final Address billingAddress = getBillingAddress(cartModel, bankTransferAdditionalAuthInfo);
        final AddressModel deliveryAddressModel = worldpayDeliveryAddressStrategy.getDeliveryAddress(cartModel);

        final Address shippingAddress = getWorldpayAddressConverter().convert(deliveryAddressModel);

        final Payment payment = getWorldpayOrderService().createPayment(bankTransferAdditionalAuthInfo.getPaymentMethod(),
                bankTransferAdditionalAuthInfo.getShopperBankCode(), shippingAddress.getCountryCode());

        final CustomerModel customerModel = (CustomerModel) cartModel.getUser();

        final String shopperEmailAddress = getCustomerEmailResolutionService().getEmailForCustomer(customerModel);
        final String statementNarrative = bankTransferAdditionalAuthInfo.getStatementNarrative();

        final Session session = getWorldpayOrderService().createSession(worldpayAdditionalInfoData);
        final Browser browser = getWorldpayOrderService().createBrowser(worldpayAdditionalInfoData);
        final Shopper shopper = getWorldpayOrderService().createShopper(shopperEmailAddress, session, browser);
        return createDirectAuthoriseRequest(config, merchantInfo, orderInfo, payment, shopper, shippingAddress, billingAddress, statementNarrative);
    }

    @Override
    public DirectAuthoriseServiceRequest buildDirectAuthoriseRecurringPayment(final MerchantInfo merchantInfo, final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayConfigurationException {
        final WorldpayConfig config = getWorldpayConfigLookupService().lookupConfig();
        final String worldpayOrderCode = getWorldpayGenerateMerchantTransactionCodeStrategy().generateCode(cartModel);
        final Amount amount = getWorldpayOrderService().createAmount(cartModel.getCurrency(), cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = getWorldpayOrderService().createBasicOrderInfo(worldpayOrderCode, worldpayOrderCode, amount);
        final Session session = getWorldpayOrderService().createSession(worldpayAdditionalInfoData);
        final Browser browser = getWorldpayOrderService().createBrowser(worldpayAdditionalInfoData);
        final String authenticatedShopperId = worldpayAdditionalInfoData.getAuthenticatedShopperId();
        final String customerEmail = getCustomerEmailResolutionService().getEmailForCustomer((CustomerModel) cartModel.getUser());
        final Shopper shopper = getWorldpayOrderService().createAuthenticatedShopper(customerEmail, authenticatedShopperId, session, browser);
        final Token token = createToken(((CreditCardPaymentInfoModel)cartModel.getPaymentInfo()).getSubscriptionId(), worldpayAdditionalInfoData.getSecurityCode());
        final Address shippingAddress = getWorldpayAddressConverter().convert(worldpayDeliveryAddressStrategy.getDeliveryAddress(cartModel));
        return createTokenisedDirectAuthoriseRequest(config, merchantInfo, orderInfo, token, shopper, shippingAddress);
    }

    protected Address getBillingAddress(final CartModel cartModel, final AdditionalAuthInfo additionalAuthInfo) {
        final AddressModel deliveryAddressModel = cartModel.getDeliveryAddress();
        if (deliveryAddressModel != null && additionalAuthInfo.getUsingShippingAsBilling()) {
            return getWorldpayAddressConverter().convert(deliveryAddressModel);
        } else {
            if (cartModel.getPaymentAddress() != null) {
                return getWorldpayAddressConverter().convert(cartModel.getPaymentAddress());
            }
        }
        return null;
    }

    protected UpdateTokenServiceRequest createUpdateTokenServiceRequest(final MerchantInfo merchantInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                                        final WorldpayConfig worldpayConfig, final TokenRequest tokenRequest, final String paymentTokenID,
                                                                        final CardDetails cardDetails) {
        return UpdateTokenServiceRequest.updateTokenRequest(worldpayConfig, merchantInfo, worldpayAdditionalInfoData.getAuthenticatedShopperId(), paymentTokenID, tokenRequest, cardDetails);
    }

    protected DirectAuthoriseServiceRequest createDirectAuthoriseRequest(final WorldpayConfig config, final MerchantInfo merchantInfo, final BasicOrderInfo orderInfo,
                                                                         final Payment payment, final Shopper shopper, final Address shippingAddress, final Address billingAddress,
                                                                         final String statementNarrative) {
        return DirectAuthoriseServiceRequest.createDirectAuthoriseRequest(config, merchantInfo, orderInfo, payment, shopper, shopper.getSession(), shippingAddress, billingAddress, statementNarrative);
    }

    protected Token createToken(final String subscriptionId, final String securityCode) {
        return PaymentBuilder.createToken(subscriptionId, securityCode);
    }

    protected Cse createCsePayment(final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final Address billingAddress) {
        return PaymentBuilder.createCSE(cseAdditionalAuthInfo.getEncryptedData(), billingAddress);
    }

    protected DirectAuthoriseServiceRequest createDirect3DAuthoriseRequest(final WorldpayConfig worldpayConfig, final MerchantInfo merchantInfo, final BasicOrderInfo basicOrderInfo, final Token token, final Shopper shopper, final Session session, final String paRes, final String echoData, final Address shippingAddress) {
        return DirectAuthoriseServiceRequest.createDirect3DAuthoriseRequest(worldpayConfig, merchantInfo, basicOrderInfo, token, shopper, session, paRes, echoData, shippingAddress, null, null);
    }

    protected DirectAuthoriseServiceRequest createTokenisedDirectAuthoriseRequest(final WorldpayConfig worldpayConfig, final MerchantInfo merchantInfo, final BasicOrderInfo basicOrderInfo, final Token token, final Shopper shopper, final Address shippingAddress) {
        return DirectAuthoriseServiceRequest.createTokenisedDirectAuthoriseRequest(worldpayConfig, merchantInfo, basicOrderInfo, token, shopper, shippingAddress, null);
    }

    protected CreateTokenServiceRequest createTokenRequest(final WorldpayConfig worldpayConfig, final MerchantInfo merchantInfo, final String authenticatedShopperId, final Payment csePayment, final TokenRequest tokenRequest) {
        return CreateTokenServiceRequest.createTokenRequest(worldpayConfig, merchantInfo, authenticatedShopperId, csePayment, tokenRequest);
    }

    private WorldpayTokenEventReferenceCreationStrategy getWorldpayTokenEventReferenceCreationStrategy() {
        return worldpayTokenEventReferenceCreationStrategy;
    }

    @Required
    public void setWorldpayTokenEventReferenceCreationStrategy(final WorldpayTokenEventReferenceCreationStrategy worldpayTokenEventReferenceCreationStrategy) {
        this.worldpayTokenEventReferenceCreationStrategy = worldpayTokenEventReferenceCreationStrategy;
    }

    private WorldpayOrderService getWorldpayOrderService() {
        return worldpayOrderService;
    }

    @Required
    public void setWorldpayOrderService(final WorldpayOrderService worldpayOrderService) {
        this.worldpayOrderService = worldpayOrderService;
    }

    private Converter<AddressModel, Address> getWorldpayAddressConverter() {
        return worldpayAddressConverter;
    }

    @Required
    public void setWorldpayAddressConverter(final Converter<AddressModel, Address> worldpayAddressConverter) {
        this.worldpayAddressConverter = worldpayAddressConverter;
    }

    private WorldpayConfigLookupService getWorldpayConfigLookupService() {
        return worldpayConfigLookupService;
    }

    @Required
    public void setWorldpayConfigLookupService(final WorldpayConfigLookupService worldpayConfigLookupService) {
        this.worldpayConfigLookupService = worldpayConfigLookupService;
    }

    private CustomerEmailResolutionService getCustomerEmailResolutionService() {
        return customerEmailResolutionService;
    }

    @Required
    public void setCustomerEmailResolutionService(final CustomerEmailResolutionService customerEmailResolutionService) {
        this.customerEmailResolutionService = customerEmailResolutionService;
    }

    private GenerateMerchantTransactionCodeStrategy getWorldpayGenerateMerchantTransactionCodeStrategy() {
        return worldpayGenerateMerchantTransactionCodeStrategy;
    }

    @Required
    public void setWorldpayGenerateMerchantTransactionCodeStrategy(final GenerateMerchantTransactionCodeStrategy worldpayGenerateMerchantTransactionCodeStrategy) {
        this.worldpayGenerateMerchantTransactionCodeStrategy = worldpayGenerateMerchantTransactionCodeStrategy;
    }

    @Required
    public void setWorldpayDeliveryAddressStrategy(final WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategy) {
        this.worldpayDeliveryAddressStrategy = worldpayDeliveryAddressStrategy;
    }
}
