package com.worldpay.facades.payment.impl;

import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.payment.WorldpayAdditionalInfoService;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayAdditionalInfoFacade implements WorldpayAdditionalInfoFacade {

    private WorldpayAdditionalInfoService worldpayAdditionalInfoService;

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldpayAdditionalInfoData createWorldpayAdditionalInfoData(final HttpServletRequest request) {
        return worldpayAdditionalInfoService.createWorldpayAdditionalInfoData(request);
    }

    @Required
    public void setWorldpayAdditionalInfoService(WorldpayAdditionalInfoService worldpayAdditionalInfoService) {
        this.worldpayAdditionalInfoService = worldpayAdditionalInfoService;
    }
}
