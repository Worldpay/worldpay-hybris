package com.worldpay.merchant;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.service.model.MerchantInfo;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

/**
 * This interface defines the methods to return the different merchants to be used in the application depending on the current site.
 *
 * @spring.bean WorldpayMerchantInfoService
 */
public interface WorldpayMerchantInfoService {

    /**
     * Returns the merchant configured to be used in the Customer Services application (cscockpit)
     * @return
     * @throws WorldpayConfigurationException
     */
    MerchantInfo getCustomerServicesMerchant() throws WorldpayConfigurationException;

    /**
     * Returns the merchant configured to be used depending on the UI experience level.
     * @param uiExperienceLevel
     * @return
     * @throws WorldpayConfigurationException
     */
    MerchantInfo getCurrentSiteMerchant(final UiExperienceLevel uiExperienceLevel) throws WorldpayConfigurationException;

    /**
     * Returns the merchantConfiguration for a known merchantCode.
     * @param merchantCode
     * @return
     * @throws WorldpayConfigurationException
     */
    MerchantInfo getMerchantInfoByCode(final String merchantCode) throws WorldpayConfigurationException;

    /**
     * Returns the merchantConfiguration for the merchant used in the transaction {@link PaymentTransactionModel}
     * @param paymentTransactionModel
     * @return
     * @throws WorldpayConfigurationException
     */
    MerchantInfo getMerchantInfoFromTransaction(final PaymentTransactionModel paymentTransactionModel) throws WorldpayConfigurationException;
}
