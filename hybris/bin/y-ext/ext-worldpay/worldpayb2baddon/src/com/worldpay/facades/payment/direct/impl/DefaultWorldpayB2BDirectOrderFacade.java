package com.worldpay.facades.payment.direct.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.direct.WorldpayB2BDirectOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import com.worldpay.service.payment.WorldpayJsonWebTokenService;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;

/**
 * Implementation of the authorise operations that enables the Client Side Encryption with Worldpay
 */
public class DefaultWorldpayB2BDirectOrderFacade extends DefaultWorldpayDirectOrderFacade implements WorldpayB2BDirectOrderFacade {

    private final B2BOrderService b2BOrderService;

    public DefaultWorldpayB2BDirectOrderFacade(final WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategy, final WorldpayDirectOrderService worldpayDirectOrderService, final WorldpayJsonWebTokenService worldpayJsonWebTokenService, final CartService cartService, final WorldpayMerchantInfoService worldpayMerchantInfoService, final AcceleratorCheckoutFacade acceleratorCheckoutFacade, final WorldpayPaymentInfoService worldpayPaymentInfoService, final WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade, final CartFacade cartFacade, final B2BOrderService b2BOrderService) {
        super(worldpayAuthenticatedShopperIdStrategy, worldpayDirectOrderService, worldpayJsonWebTokenService, cartService, worldpayMerchantInfoService, acceleratorCheckoutFacade, worldpayPaymentInfoService, worldpayMerchantConfigDataFacade, cartFacade);
        this.b2BOrderService = b2BOrderService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectResponseData authoriseRecurringPayment(final String orderCode,
                                                        final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        final AbstractOrderModel abstractOrderModel = b2BOrderService.getOrderForCode(orderCode);
        final MerchantInfo merchantInfo = getWorldpayMerchantInfoService().getCurrentSiteMerchant();
        return internalAuthoriseRecurringPayment(abstractOrderModel, worldpayAdditionalInfoData, merchantInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectResponseData authorise3DSecureOnOrder(final String orderCode,
                                                       final String paResponse,
                                                       final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException {
        final OrderModel orderModel = b2BOrderService.getOrderForCode(orderCode);
        return internalAuthorise3DSecure(orderModel, paResponse, worldpayAdditionalInfoData);
    }

    @Override
    protected void handleAuthorisedResponse(DirectResponseData response) {
        response.setTransactionStatus(TransactionStatus.AUTHORISED);
    }

}
