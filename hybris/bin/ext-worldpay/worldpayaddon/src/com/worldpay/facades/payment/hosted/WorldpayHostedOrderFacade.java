package com.worldpay.facades.payment.hosted;

import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;

import java.util.Map;


/**
 * worldpay Hosted Order facade interface. Service is responsible for organising the calls and request/response from
 * worldpay Hosted Order page implementation
 */
public interface WorldpayHostedOrderFacade {

    /**
     * Carries out the redirectAuthorise against the worldpayServiceGateway and then builds the data required to send to
     * worldpay Hosted Order Page
     *
     * @return carry out the redirect to worldpay Hosted Order Page
     * @throws com.worldpay.exception.WorldpayException if there are any issues found communicating with worldpay
     */
    PaymentData redirectAuthorise(final AdditionalAuthInfo additionalAuthInfo) throws WorldpayException;

    /**
     * Complete the redirectAuthorise if successful authorise response received. Create payment details and return back
     * to UI
     *
     * @param redirectParameters The resultMap of parameters returned from worldpay redirect
     */
    void completeRedirectAuthorise(RedirectAuthoriseResult redirectParameters);

    /**
     * Checks that the response is authentic and sent from Worldpay
     *
     * @param worldpayResponse
     * @return
     */
    boolean validateRedirectResponse(Map<String, String> worldpayResponse);
}
