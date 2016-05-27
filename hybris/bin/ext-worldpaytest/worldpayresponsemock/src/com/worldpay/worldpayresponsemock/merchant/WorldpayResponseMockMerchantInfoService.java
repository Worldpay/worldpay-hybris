package com.worldpay.worldpayresponsemock.merchant;

import com.worldpay.merchant.WorldpayMerchantInfoService;

import java.util.List;

public interface WorldpayResponseMockMerchantInfoService extends WorldpayMerchantInfoService {

    /**
     * Returns all the merchant codes configured for the environment
     * @param siteUid The site uid the merchants are configured for
     */
    List<String> getAllMerchantCodes(final String siteUid);
}
