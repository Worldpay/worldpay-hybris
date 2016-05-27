package com.worldpay.widgets.controllers.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.merchant.strategies.WorldpayOrderInfoStrategy;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import com.worldpay.widgets.controllers.WorldpayCardPaymentController;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.cscockpit.widgets.controllers.impl.DefaultCheckoutController;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.text.MessageFormat;

public class DefaultWorldpayCheckoutController extends DefaultCheckoutController implements WorldpayCardPaymentController {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayCheckoutController.class);

    private PaymentData paymentData;
    private WorldpayRedirectOrderService worldpayRedirectOrderService;
    private WorldpayMerchantInfoService worldpayMerchantInfoService;
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;
    private WorldpayOrderInfoStrategy worldpayOrderInfoStrategy;

    /**
     * {@inheritDoc}
     */
    @Override
    public void redirectAuthorise() throws WorldpayException {
        final CartModel cart = getCartModel();
        final MerchantInfo merchantInfo = getWorldpayMerchantInfoService().getCustomerServicesMerchant();
        final AdditionalAuthInfo additionalAuthInfo = getAdditionalAuthInfo();
        try {
            final PaymentData hostedOrderPageData = getWorldpayRedirectOrderService().redirectAuthorise(merchantInfo, cart, additionalAuthInfo);
            setPaymentData(hostedOrderPageData);
        } catch (final WorldpayException e) {
            LOG.error(MessageFormat.format("WorldpayException: {0}", e.getMessage()), e);
            throw e;
        }
    }

    private AdditionalAuthInfo getAdditionalAuthInfo() {
        final WorldpayMerchantConfigData worldpayMerchantConfigData = worldpayMerchantConfigDataFacade.getCustomerServiceMerchantConfigData();
        final AdditionalAuthInfo additionalAuthInfo = getWorldpayOrderInfoStrategy().getAdditionalAuthInfo(worldpayMerchantConfigData);
        additionalAuthInfo.setUsingShippingAsBilling(false);
        additionalAuthInfo.setSaveCard(true);
        additionalAuthInfo.setPaymentMethod(PaymentType.ONLINE.getMethodCode());
        return additionalAuthInfo;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPaymentData(PaymentData hopData) {
        this.paymentData = hopData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentData getPaymentData() {
        return paymentData;
    }

    public WorldpayOrderInfoStrategy getWorldpayOrderInfoStrategy() {
        return worldpayOrderInfoStrategy;
    }

    @Required
    public void setWorldpayOrderInfoStrategy(WorldpayOrderInfoStrategy worldpayOrderInfoStrategy) {
        this.worldpayOrderInfoStrategy = worldpayOrderInfoStrategy;
    }

    @Required
    public void setWorldpayMerchantConfigDataFacade(WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade) {
        this.worldpayMerchantConfigDataFacade = worldpayMerchantConfigDataFacade;
    }
}
