package com.worldpay.service.payment;

import javax.servlet.http.HttpServletRequest;

import com.worldpay.order.data.WorldpayAdditionalInfoData;

/**
 *  Exposes methods to create data object required for worldpay requests
 */
public interface WorldpayAdditionalInfoService {

    /**
     * The creates additional data required for worldpay request. The information is provided to Worldpay for the fraud check it performs.
     * {@link WorldpayAdditionalInfoData}
     *
     * @param request {@link HttpServletRequest}
     * @return {@link WorldpayAdditionalInfoData}
     */
    WorldpayAdditionalInfoData createWorldpayAdditionalInfoData(final HttpServletRequest request);
}
