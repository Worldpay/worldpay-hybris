package com.worldpay.service.payment;

import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.service.model.MerchantInfo;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.core.model.order.CartModel;

import java.util.Map;

/**
 * worldpay Hosted Order service interface.
 * <p>
 * Service implementation is responsible for organising the calls and request/response from
 * worldpay Hosted Order page
 */
public interface WorldpayRedirectOrderService {

    /**
     * Carries out the redirectAuthorise against the worldpayServiceGateway and then builds the data required to send to
     * worldpay Hosted Order Page.
     *
     * @param merchantInfo       The {@link MerchantInfo} object to be used with this call to worldpay
     * @param cartModel          The cart for the order
     * @param additionalAuthInfo The {@link AdditionalAuthInfo} to be supplied to worldpay. Includes installation id, order
     *                           content, included and excluded payment types etc.
     * @return a {@link PaymentData} object which contains all data required to carry out the redirect to worldpay Hosted Order Page.
     * @throws WorldpayException The {@link WorldpayException} if there are any issues found communicating with worldpay.
     */
    PaymentData redirectAuthorise(final MerchantInfo merchantInfo, final CartModel cartModel, final AdditionalAuthInfo additionalAuthInfo) throws WorldpayException;

    /**
     * Completes the redirectAuthorise if successful authorise response received. Creates payment details and returns back
     * to UI
     *
     * @param authoriseResult The {@link RedirectAuthoriseResult} containing the processed parameters returned from worldpay redirect
     * @param merchantCode    The merchantCode used in the authorise with worldpay
     * @param cartModel       The {@link CartModel} cart for the order
     */
    void completeRedirectAuthorise(final RedirectAuthoriseResult authoriseResult, final String merchantCode, final CartModel cartModel);

    /**
     * Checks that the response parameters that have been returned are valid. Ensures mac code is correct if supplied
     *
     * @param merchantInfo The {@link MerchantInfo} object to be used with this call to worldpay
     * @param resultMap    The object containing the parameters returned from worldpay redirect
     * @return true if the response is valid
     */
    boolean validateRedirectResponse(final MerchantInfo merchantInfo, final Map<String, String> resultMap);
}
