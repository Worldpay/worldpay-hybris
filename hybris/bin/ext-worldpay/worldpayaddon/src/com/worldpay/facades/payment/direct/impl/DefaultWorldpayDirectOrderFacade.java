package com.worldpay.facades.payment.direct.impl;

import com.google.common.base.Preconditions;
import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.service.model.AuthorisedStatus;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.Request3DInfo;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import static com.worldpay.service.model.AuthorisedStatus.AUTHORISED;
import static com.worldpay.service.model.AuthorisedStatus.CANCELLED;
import static com.worldpay.service.model.AuthorisedStatus.REFUSED;
import static java.text.MessageFormat.format;

/**
 * Implementation of the authorise operations that enables the Client Side Encryption with Worldpay
 */
public class DefaultWorldpayDirectOrderFacade implements WorldpayDirectOrderFacade {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayDirectOrderFacade.class);

    protected static final String ERROR_AUTHORISING_ORDER = "There was a problem authorising the order with worldpayOrderCode [{0}]";
    protected static final String THERE_WAS_AN_ERROR_COMMUNICATING_WTH_WORLDPAY = "There was an error communicating wth Worldpay";
    protected static final String THERE_WAS_AN_ERROR_IN_THE_SERVICE_GATEWAY_MESSAGE = "There was an error in the service gateway:  [{0}]";

    private WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategy;
    private WorldpayDirectOrderService worldpayDirectOrderService;
    private CartService cartService;
    private UiExperienceService uiExperienceService;
    private WorldpayMerchantInfoService worldpayMerchantInfoService;
    private AcceleratorCheckoutFacade acceleratorCheckoutFacade;

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectResponseData authorise(final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        Preconditions.checkState(cartService.hasSessionCart(), "Cannot authorize payment where there is no cart");

        final CartModel cart = cartService.getSessionCart();
        try {
            final MerchantInfo merchantInfo = getCurrentMerchantInfo();
            final String authenticatedShopperId = worldpayAuthenticatedShopperIdStrategy.getAuthenticatedShopperId(cart.getUser());
            worldpayAdditionalInfoData.setAuthenticatedShopperId(authenticatedShopperId);
            worldpayDirectOrderService.createToken(merchantInfo, cart, cseAdditionalAuthInfo, worldpayAdditionalInfoData);

            final DirectAuthoriseServiceResponse serviceResponse = worldpayDirectOrderService.authorise(merchantInfo, cart, cseAdditionalAuthInfo, worldpayAdditionalInfoData);

            return handleDirectServiceResponse(serviceResponse, merchantInfo, cart);
        } catch (WorldpayConfigurationException e) {
            LOG.error("There is no configuration for the requested merchant. Please review your settings.");
            throw e;
        } catch (InvalidCartException e) {
            LOG.error(format("There was an error placing the order for cart [{0}]", cart.getCode()), e);
            throw e;
        }
    }

    @Override
    public DirectResponseData authoriseRecurringPayment(final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {

        final CartModel cart = cartService.getSessionCart();
        try {
            final MerchantInfo merchantInfo = getCurrentMerchantInfo();
            final String authenticatedShopperId = worldpayAuthenticatedShopperIdStrategy.getAuthenticatedShopperId(cart.getUser());
            worldpayAdditionalInfoData.setAuthenticatedShopperId(authenticatedShopperId);
            final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = worldpayDirectOrderService.authoriseRecurringPayment(merchantInfo, cart, worldpayAdditionalInfoData);
            return handleDirectServiceResponse(directAuthoriseServiceResponse, merchantInfo, cart);
        } catch (final WorldpayConfigurationException e) {
            LOG.error("There is no configuration for the requested merchant. Please review your settings.");
            throw e;
        } catch (InvalidCartException e) {
            LOG.error(format("There was an error placing the order for cart [{0}]", cart.getCode()), e);
            throw e;
        }
    }

    @Override
    public String authoriseBankTransferRedirect(final BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfo,
                                                final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException {
        final CartModel cart = cartService.getSessionCart();
        try {
            final MerchantInfo merchantInfo = getCurrentMerchantInfo();
            final DirectAuthoriseServiceResponse serviceResponse = worldpayDirectOrderService.authoriseBankTransfer(merchantInfo, cart, bankTransferAdditionalAuthInfo, worldpayAdditionalInfoData);
            return handleAuthoriseBankTransferServiceResponse(serviceResponse);
        } catch (WorldpayConfigurationException e) {
            LOG.error("There is no configuration for the requested merchant. Please review your settings.");
            throw e;
        }
    }

    protected String handleAuthoriseBankTransferServiceResponse(final DirectAuthoriseServiceResponse serviceResponse) throws WorldpayException {
        if (shouldProcessBankTransferRedirect(serviceResponse)) {
            return serviceResponse.getRedirectReference().getUrl();
        } else if (serviceResponse.getErrorDetail() != null) {
            final String errorMessage = format(THERE_WAS_AN_ERROR_IN_THE_SERVICE_GATEWAY_MESSAGE, serviceResponse.getErrorDetail().getMessage());
            LOG.error(errorMessage);
            throw new WorldpayException(errorMessage);
        } else {
            throw new WorldpayException(THERE_WAS_AN_ERROR_COMMUNICATING_WTH_WORLDPAY);
        }
    }

    @Override
    public DirectResponseData authorise3DSecure(final String paResponse, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        Preconditions.checkState(cartService.hasSessionCart(), "Cannot authorize payment where there is no cart");
        final MerchantInfo merchantInfo = getCurrentMerchantInfo();
        final CartModel cartModel = cartService.getSessionCart();
        final String authenticatedShopperId = worldpayAuthenticatedShopperIdStrategy.getAuthenticatedShopperId(cartModel.getUser());
        worldpayAdditionalInfoData.setAuthenticatedShopperId(authenticatedShopperId);

        final DirectAuthoriseServiceResponse serviceResponse = worldpayDirectOrderService.authorise3DSecure(merchantInfo, cartModel, worldpayAdditionalInfoData, paResponse);
        if (shouldProcessResponse(serviceResponse)) {
            return handle3DSecureResponse(serviceResponse, merchantInfo);
        } else if (serviceResponse.getErrorDetail() != null) {
            final String errorMessage = format(THERE_WAS_AN_ERROR_IN_THE_SERVICE_GATEWAY_MESSAGE, serviceResponse.getErrorDetail().getMessage());
            LOG.error(errorMessage);
            throw new WorldpayException(errorMessage);
        } else {
            throw new WorldpayException(THERE_WAS_AN_ERROR_COMMUNICATING_WTH_WORLDPAY);
        }
    }

    protected DirectResponseData handleDirectServiceResponse(final DirectAuthoriseServiceResponse serviceResponse, final MerchantInfo merchantInfo, final CartModel cart)
            throws InvalidCartException, WorldpayException {
        if (shouldProcessResponse(serviceResponse)) {
            return processDirectResponse(serviceResponse, cart, merchantInfo);
        } else if (serviceResponse.getErrorDetail() != null) {
            final String errorMessage = format(THERE_WAS_AN_ERROR_IN_THE_SERVICE_GATEWAY_MESSAGE, serviceResponse.getErrorDetail().getMessage());
            LOG.error(errorMessage);
            throw new WorldpayException(errorMessage);
        } else {
            throw new WorldpayException(THERE_WAS_AN_ERROR_COMMUNICATING_WTH_WORLDPAY);
        }
    }

    protected DirectResponseData handle3DSecureResponse(final DirectAuthoriseServiceResponse serviceResponse, final MerchantInfo merchantInfo) throws InvalidCartException, WorldpayException {
        final DirectResponseData response = new DirectResponseData();
        final PaymentReply paymentReply = serviceResponse.getPaymentReply();
        final AuthorisedStatus authStatus = paymentReply.getAuthStatus();
        if (AUTHORISED.equals(authStatus)) {
            worldpayDirectOrderService.completeAuthorise3DSecure(serviceResponse, merchantInfo);
            handleAuthorisedResponse(response);
        } else if (REFUSED.equals(authStatus)) {
            handleRefusedResponse(response, paymentReply.getReturnCode());
        } else {
            final String errorMessage = format(ERROR_AUTHORISING_ORDER, serviceResponse.getOrderCode());
            LOG.error(errorMessage);
            throw new WorldpayException(errorMessage);
        }
        return response;
    }

    protected DirectResponseData processDirectResponse(final DirectAuthoriseServiceResponse serviceResponse, final CartModel cart,
                                                       final MerchantInfo merchantInfo) throws InvalidCartException, WorldpayException {
        final DirectResponseData response = new DirectResponseData();
        final PaymentReply paymentReply = serviceResponse.getPaymentReply();
        final Request3DInfo request3DInfo = serviceResponse.getRequest3DInfo();
        if (paymentReply != null) {
            final AuthorisedStatus status = paymentReply.getAuthStatus();
            if (AUTHORISED.equals(status)) {
                worldpayDirectOrderService.completeAuthorise(serviceResponse, cart, merchantInfo.getMerchantCode());
                handleAuthorisedResponse(response);
            } else if (REFUSED.equals(status)) {
                handleRefusedResponse(response, paymentReply.getReturnCode());
            } else if (CANCELLED.equals(status)) {
                handleCancelledResponse(response);
            }
        } else if (request3DInfo != null) {
            handle3DInfoRequest(response, request3DInfo);
        } else {
            final String errorMessage = format(ERROR_AUTHORISING_ORDER, cart.getWorldpayOrderCode());
            throwWorldpayException(errorMessage);
        }
        return response;
    }

    private void throwWorldpayException(final String errorMessage) throws WorldpayException {
        LOG.error(errorMessage);
        throw new WorldpayException(errorMessage);
    }

    protected boolean shouldProcessResponse(final DirectAuthoriseServiceResponse serviceResponse) {
        return serviceResponse.getPaymentReply() != null || serviceResponse.getRequest3DInfo() != null;
    }

    protected boolean shouldProcessBankTransferRedirect(final DirectAuthoriseServiceResponse serviceResponse) {
        return serviceResponse.getRedirectReference() != null;
    }

    protected void handleAuthorisedResponse(final DirectResponseData response) throws InvalidCartException {
        final OrderData orderData = acceleratorCheckoutFacade.placeOrder();
        response.setOrderData(orderData);
        response.setTransactionStatus(TransactionStatus.AUTHORISED);
    }

    protected void handleRefusedResponse(final DirectResponseData response, final Integer returnCode) {
        response.setTransactionStatus(TransactionStatus.REFUSED);
        response.setReturnCode(returnCode);
    }

    protected void handleCancelledResponse(final DirectResponseData response) {
        response.setTransactionStatus(TransactionStatus.CANCELLED);
    }

    protected void handle3DInfoRequest(final DirectResponseData response, final Request3DInfo request3DInfo) {
        response.setTransactionStatus(TransactionStatus.AUTHENTICATION_REQUIRED);
        response.setIssuerURL(request3DInfo.getIssuerUrl());
        response.setPaRequest(request3DInfo.getPaRequest());
    }

    private MerchantInfo getCurrentMerchantInfo() throws WorldpayConfigurationException {
        final UiExperienceLevel uiExperienceLevel = uiExperienceService.getUiExperienceLevel();
        return worldpayMerchantInfoService.getCurrentSiteMerchant(uiExperienceLevel);
    }

    @Required
    public void setWorldpayDirectOrderService(WorldpayDirectOrderService worldpayDirectOrderService) {
        this.worldpayDirectOrderService = worldpayDirectOrderService;
    }

    @Required
    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    @Required
    public void setUiExperienceService(UiExperienceService uiExperienceService) {
        this.uiExperienceService = uiExperienceService;
    }

    @Required
    public void setWorldpayMerchantInfoService(WorldpayMerchantInfoService worldpayMerchantInfoService) {
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
    }

    @Required
    public void setAcceleratorCheckoutFacade(AcceleratorCheckoutFacade acceleratorCheckoutFacade) {
        this.acceleratorCheckoutFacade = acceleratorCheckoutFacade;
    }

    @Required
    public void setWorldpayAuthenticatedShopperIdStrategy(final WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategy) {
        this.worldpayAuthenticatedShopperIdStrategy = worldpayAuthenticatedShopperIdStrategy;
    }
}
