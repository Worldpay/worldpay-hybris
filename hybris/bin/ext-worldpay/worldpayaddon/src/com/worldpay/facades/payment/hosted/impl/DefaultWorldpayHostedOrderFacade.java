package com.worldpay.facades.payment.hosted.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.merchant.strategies.WorldpayOrderInfoStrategy;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.text.MessageFormat;
import java.util.Map;

public class DefaultWorldpayHostedOrderFacade implements WorldpayHostedOrderFacade {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayHostedOrderFacade.class);
    protected static final String WORLDPAY_MERCHANT_CODE = "worldpayMerchantCode";

    private SessionService sessionService;
    private WorldpayRedirectOrderService worldpayRedirectOrderService;
    private WorldpayMerchantInfoService worldpayMerchantInfoService;
    private UiExperienceService uiExperienceService;
    private CartService cartService;
    private WorldpayOrderInfoStrategy worldpayOrderInfoStrategy;
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;

    /**
     * {@inheritDoc}
     *
     * @see WorldpayHostedOrderFacade#redirectAuthorise(AdditionalAuthInfo)
     */
    @Override
    public PaymentData redirectAuthorise(final AdditionalAuthInfo additionalAuthInfo) throws WorldpayException {
        final CartModel cart = getCartService().getSessionCart();
        final UiExperienceLevel uiExperienceLevel = getUiExperienceService().getUiExperienceLevel();
        final MerchantInfo merchantInfo = getWorldpayMerchantInfoService().getCurrentSiteMerchant(uiExperienceLevel);
        populateAdditionalAuthInfo(additionalAuthInfo, uiExperienceLevel);
        return getWorldpayRedirectOrderService().redirectAuthorise(merchantInfo, cart, additionalAuthInfo);
    }

    protected void populateAdditionalAuthInfo(final AdditionalAuthInfo additionalAuthInfo, final UiExperienceLevel uiExperienceLevel) {
        final WorldpayMerchantConfigData currentSiteMerchant = worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData(uiExperienceLevel);
        getWorldpayOrderInfoStrategy().populateAdditionalAuthInfo(additionalAuthInfo, currentSiteMerchant);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeRedirectAuthorise(final RedirectAuthoriseResult result) {
        final String merchantCode = getSessionService().getAttribute(WORLDPAY_MERCHANT_CODE);
        final CartModel cart = getCartService().getSessionCart();
        getWorldpayRedirectOrderService().completeRedirectAuthorise(result, merchantCode, cart);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateRedirectResponse(final Map<String, String> worldpayResponse) {
        final String merchantCode = getSessionService().getAttribute(WORLDPAY_MERCHANT_CODE);
        try {
            final MerchantInfo merchantInfo = getWorldpayMerchantInfoService().getMerchantInfoByCode(merchantCode);
            return getWorldpayRedirectOrderService().validateRedirectResponse(merchantInfo, worldpayResponse);
        } catch (WorldpayConfigurationException e) {
            LOG.error(MessageFormat.format("There was an error getting the configuration for the merchants: [{0}]", e.getMessage()), e);
        }
        return false;
    }

    public WorldpayRedirectOrderService getWorldpayRedirectOrderService() {
        return worldpayRedirectOrderService;
    }

    @Required
    public void setWorldpayRedirectOrderService(final WorldpayRedirectOrderService worldpayRedirectOrderService) {
        this.worldpayRedirectOrderService = worldpayRedirectOrderService;
    }

    public WorldpayMerchantInfoService getWorldpayMerchantInfoService() {
        return worldpayMerchantInfoService;
    }

    @Required
    public void setWorldpayMerchantInfoService(final WorldpayMerchantInfoService worldpayMerchantInfoService) {
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
    }

    protected UiExperienceService getUiExperienceService() {
        return uiExperienceService;
    }

    @Required
    public void setUiExperienceService(final UiExperienceService uiExperienceService) {
        this.uiExperienceService = uiExperienceService;
    }

    protected CartService getCartService() {
        return cartService;
    }

    @Required
    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    @Required
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public WorldpayOrderInfoStrategy getWorldpayOrderInfoStrategy() {
        return worldpayOrderInfoStrategy;
    }

    @Required
    public void setWorldpayOrderInfoStrategy(WorldpayOrderInfoStrategy worldpayOrderInfoStrategy) {
        this.worldpayOrderInfoStrategy = worldpayOrderInfoStrategy;
    }

    @Required
    public void setWorldpayMerchantConfigDataFacade(final WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade) {
        this.worldpayMerchantConfigDataFacade = worldpayMerchantConfigDataFacade;
    }
}
