package com.worldpay.service.payment.impl;

import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import com.worldpay.service.payment.request.WorldpayRequestFactory;
import com.worldpay.service.request.CreateTokenServiceRequest;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.service.response.UpdateTokenResponse;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.session.SessionService;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.util.Currency;

import static com.worldpay.enums.token.TokenEvent.CONFLICT;


/**
 * Default implementation of {@link WorldpayDirectOrderService}
 */
public class DefaultWorldpayDirectOrderService extends AbstractWorldpayOrderService implements WorldpayDirectOrderService {

    protected static final String THREE_D_SECURE_ECHO_DATA_PARAM = "3DSecureEchoData";
    protected static final String THREE_D_SECURE_COOKIE_PARAM = "3DSecureCookie";

    private SessionService sessionService;
    private CartService cartService;
    private WorldpayRequestFactory worldpayRequestFactory;

    /**
     * {@inheritDoc}
     *
     * @see WorldpayDirectOrderService#authorise(MerchantInfo, CartModel, CSEAdditionalAuthInfo, WorldpayAdditionalInfoData)
     */
    @Override
    public DirectAuthoriseServiceResponse authorise(final MerchantInfo merchantInfo, final CartModel cartModel, final CSEAdditionalAuthInfo cseAdditionalAuthInfo,
                                                    final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {

        final DirectAuthoriseServiceRequest directAuthoriseRequest = worldpayRequestFactory.buildDirectAuthoriseRequest(merchantInfo, cartModel, worldpayAdditionalInfoData);
        final DirectAuthoriseServiceResponse response = getWorldpayOrderService().getWorldpayServiceGateway().directAuthorise(directAuthoriseRequest);
        if (response.getRequest3DInfo() != null) {
          /*
          In case the transaction requires 3d secure, two strings need to be placed in the users session: echoData and a cookie.
          These are needed to successfully reference the initial transaction in Worldpay when the user comes back from the 3d secure page.
          Example values:
              echoData=148556494881709
              cookie=machine=0ab20014;path=/
           */
            sessionService.setAttribute(THREE_D_SECURE_COOKIE_PARAM, response.getCookie());
            sessionService.setAttribute(THREE_D_SECURE_ECHO_DATA_PARAM, response.getEchoData());
        }
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse authoriseRecurringPayment(final MerchantInfo merchantInfo, final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {
        final DirectAuthoriseServiceRequest directAuthoriseRequest = worldpayRequestFactory.buildDirectAuthoriseRecurringPayment(merchantInfo, cartModel, worldpayAdditionalInfoData);
        final DirectAuthoriseServiceResponse response = getWorldpayOrderService().getWorldpayServiceGateway().directAuthorise(directAuthoriseRequest);
        if (response.getRequest3DInfo() != null) {
            sessionService.setAttribute(THREE_D_SECURE_COOKIE_PARAM, response.getCookie());
            sessionService.setAttribute(THREE_D_SECURE_ECHO_DATA_PARAM, response.getEchoData());
        }
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectAuthoriseServiceResponse authoriseBankTransfer(final MerchantInfo merchantInfo, final CartModel cartModel,
                                                                final BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfo,
                                                                final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {
        final DirectAuthoriseServiceRequest directAuthoriseBankTransferRequest = worldpayRequestFactory.buildDirectAuthoriseBankTransferRequest(
                merchantInfo, cartModel, bankTransferAdditionalAuthInfo, worldpayAdditionalInfoData);
        return getWorldpayOrderService().getWorldpayServiceGateway().directAuthorise(directAuthoriseBankTransferRequest);
    }

    /**
     * {@inheritDoc}
     *
     * @see WorldpayDirectOrderService#createToken(MerchantInfo, CartModel, CSEAdditionalAuthInfo, WorldpayAdditionalInfoData)
     */
    @Override
    public void createToken(final MerchantInfo merchantInfo, final CartModel cartModel, final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData)
            throws WorldpayException {
        final CreateTokenServiceRequest createTokenRequest = worldpayRequestFactory.buildTokenRequest(merchantInfo, cartModel, cseAdditionalAuthInfo, worldpayAdditionalInfoData);
        final CreateTokenResponse createTokenResponse = getWorldpayOrderService().getWorldpayServiceGateway().createToken(createTokenRequest);
        CreditCardPaymentInfoModel creditCardPaymentInfoModel;
        if (createTokenResponse.isError()) {
            throw new WorldpayException(createTokenResponse.getErrorDetail().getMessage());
        }
        if (createTokenRepliesWithConflict(createTokenResponse)) {
            final UpdateTokenServiceRequest updateTokenServiceRequest = worldpayRequestFactory.buildTokenUpdateRequest(merchantInfo, cseAdditionalAuthInfo, worldpayAdditionalInfoData, createTokenResponse);
            final UpdateTokenResponse updateTokenResponse = getWorldpayOrderService().getWorldpayServiceGateway().updateToken(updateTokenServiceRequest);
            if (updateTokenResponse.isError()) {
                throw new WorldpayException(updateTokenResponse.getErrorDetail().getMessage());
            }
            creditCardPaymentInfoModel = getWorldpayPaymentInfoService().updateCreditCardPaymentInfo(cartModel, updateTokenServiceRequest);
        } else {
            creditCardPaymentInfoModel = getWorldpayPaymentInfoService().createCreditCardPaymentInfo(cartModel, createTokenResponse, cseAdditionalAuthInfo.getSaveCard());
        }
        if (creditCardPaymentInfoModel != null) {
            cartModel.setPaymentInfo(creditCardPaymentInfoModel);
            getModelService().save(cartModel);
        }
    }

    protected boolean createTokenRepliesWithConflict(final CreateTokenResponse createTokenResponse) {
        return CONFLICT.name().equals(createTokenResponse.getToken().getTokenDetails().getTokenEvent());
    }

    /**
     * {@inheritDoc}DefaultWorldpayOrderService.java
     *
     * @see WorldpayDirectOrderService#authorise3DSecure(MerchantInfo, CartModel, WorldpayAdditionalInfoData, String)
     */
    @Override
    public DirectAuthoriseServiceResponse authorise3DSecure(final MerchantInfo merchantInfo, final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                            final String paResponse) throws WorldpayException {
        final String echoData = getAndRemoveSessionAttribute(THREE_D_SECURE_ECHO_DATA_PARAM);
        final String cookie = getAndRemoveSessionAttribute(THREE_D_SECURE_COOKIE_PARAM);

        final DirectAuthoriseServiceRequest directAuthoriseServiceRequest = worldpayRequestFactory.build3dDirectAuthoriseRequest(
                merchantInfo, cartModel, worldpayAdditionalInfoData, paResponse, echoData, cookie);
        return getWorldpayOrderService().getWorldpayServiceGateway().directAuthorise(directAuthoriseServiceRequest);
    }

    /**
     * {@inheritDoc}
     *
     * @see WorldpayDirectOrderService#completeAuthorise(DirectAuthoriseServiceResponse, CartModel, String)
     */
    @Override
    public void completeAuthorise(final DirectAuthoriseServiceResponse serviceResponse, final CartModel cartModel, final String merchantCode) {
        final PaymentInfoModel paymentInfoModel = cartModel.getPaymentInfo();

        cloneAndSetBillingAddressFromCart(cartModel, paymentInfoModel);
        final BigDecimal authorisationAmount = convertAmount(serviceResponse.getPaymentReply().getAmount());
        final CommerceCheckoutParameter commerceCheckoutParameter = createCommerceCheckoutParameter(cartModel, paymentInfoModel, authorisationAmount);
        getCommerceCheckoutService().setPaymentInfo(commerceCheckoutParameter);

        final PaymentTransactionModel paymentTransaction = getWorldpayPaymentTransactionService().createPaymentTransaction(false, merchantCode, commerceCheckoutParameter);
        getWorldpayPaymentTransactionService().addRiskScore(paymentTransaction, serviceResponse.getPaymentReply());

        final PaymentTransactionEntryModel transactionEntry = getWorldpayPaymentTransactionService().createNonPendingAuthorisePaymentTransactionEntry(paymentTransaction, merchantCode, cartModel);
        getWorldpayPaymentTransactionService().addAavFields(transactionEntry, serviceResponse.getPaymentReply());

        if (paymentInfoModel instanceof CreditCardPaymentInfoModel) {
            getWorldpayPaymentInfoService().setCreditCardType((CreditCardPaymentInfoModel) paymentInfoModel, serviceResponse.getPaymentReply());
        }
        getWorldpayPaymentInfoService().updateAndAttachPaymentInfoModel(paymentTransaction, cartModel, paymentInfoModel);
    }

    /**
     * {@inheritDoc}
     *
     * @see WorldpayDirectOrderService#completeAuthorise3DSecure(DirectAuthoriseServiceResponse, MerchantInfo)
     */
    @Override
    public void completeAuthorise3DSecure(final DirectAuthoriseServiceResponse serviceResponse, final MerchantInfo merchantInfo) {
        final CartModel cartModel = cartService.getSessionCart();
        completeAuthorise(serviceResponse, cartModel, merchantInfo.getMerchantCode());
    }

    private String getAndRemoveSessionAttribute(final String param) {
        final String attribute = sessionService.getAttribute(param);
        sessionService.removeAttribute(param);
        return attribute;
    }

    protected BigDecimal convertAmount(final Amount amount) {
        final Currency currency = Currency.getInstance(amount.getCurrencyCode());
        return new BigDecimal(amount.getValue()).movePointLeft(currency.getDefaultFractionDigits());
    }

    @Required
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Required
    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    @Required
    public void setWorldpayRequestFactory(final WorldpayRequestFactory worldpayRequestFactory) {
        this.worldpayRequestFactory = worldpayRequestFactory;
    }
}
