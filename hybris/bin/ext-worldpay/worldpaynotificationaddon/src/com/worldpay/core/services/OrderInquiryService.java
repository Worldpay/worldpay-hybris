package com.worldpay.core.services;

import com.worldpay.exception.WorldpayException;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import de.hybris.platform.payment.model.PaymentTransactionModel;

/**
 * Order Inquiry Service interface. The service is responsible for sending Order Inquiries to Worldpay and processing the responses.
 */
public interface OrderInquiryService {

    /**
     * Carries out the Order Inquiry with the created Order Inquiry Service Request and returns the response.
     *
     * @param merchantConfig          The {@link MerchantInfo} object to be used with this call to Worldpay
     * @param paymentTransactionModel The {@link PaymentTransactionModel} object with the Worldpay Order Code
     * @return The {@link OrderInquiryServiceResponse} object with the response from Worldpay
     * @throws WorldpayException
     */
    OrderInquiryServiceResponse inquirePaymentTransaction(final MerchantInfo merchantConfig, final PaymentTransactionModel paymentTransactionModel) throws WorldpayException;

    /**
     * Updates the payment transaction with the information from the Order Inquiry Service Response from Worldpay.
     *
     * @param paymentTransactionModel     The {@link PaymentTransactionModel} object to be updated
     * @param orderInquiryServiceResponse The {@link OrderInquiryServiceResponse} with updated information
     */
    void processOrderInquiryServiceResponse(PaymentTransactionModel paymentTransactionModel, OrderInquiryServiceResponse orderInquiryServiceResponse);
}
