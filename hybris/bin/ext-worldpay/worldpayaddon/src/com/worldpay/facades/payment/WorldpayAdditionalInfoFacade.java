package com.worldpay.facades.payment;

import com.worldpay.order.data.WorldpayAdditionalInfoData;

import javax.servlet.http.HttpServletRequest;

/**
 * Exposes methods to create additional information required to make worldpay requests
 */
public interface WorldpayAdditionalInfoFacade {

    /**
     * Builds worldpayAdditionalInfoData required when making a request to worldpay
     * @param request the {@link HttpServletRequest}
     * @return the {@link WorldpayAdditionalInfoData}
     */
    WorldpayAdditionalInfoData createWorldpayAdditionalInfoData(final HttpServletRequest request);
}
