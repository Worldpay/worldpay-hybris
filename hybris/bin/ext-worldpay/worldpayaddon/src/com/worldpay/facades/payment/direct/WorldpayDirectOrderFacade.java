package com.worldpay.facades.payment.direct;

import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import de.hybris.platform.order.InvalidCartException;

/**
 * Interface that exposes the authorise operations that enables the Client Side Encryption with Worldpay
 */
public interface WorldpayDirectOrderFacade {

    /**
     * Performs a direct authorisation using Client Side Encryption with Worldpay.
     *
     * @param cseAdditionalAuthInfo      Object that contains additional authorisation information and the cseToken
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @return {@link DirectResponseData} Wrapper object containing information on the response from Worldpay
     */
    DirectResponseData authorise(final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException;

    /**
     * Performs a request to Worldpay to retrieve the URL to redirect to when using authorisation using Client Side Encryption with Worldpay.
     *
     * @param bankTransferAdditionalAuthInfo Object that contains additional authorisation information and the shopperBank
     * @param worldpayAdditionalInfoData     Object that contains information about the current session, browser used, and cookies.
     * @return {@link DirectResponseData} Wrapper object containing information on the response from Worldpay
     */
    String authoriseBankTransferRedirect(final BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException;

    /**
     * Performs authorisation of a transaction after it has been handled by 3dSecure issuer.
     *
     * @param paResponse                 The response from the 3dSecure issuer.
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @return {@link DirectResponseData} Wrapper object containing information on the response from Worldpay
     */
    DirectResponseData authorise3DSecure(final String paResponse, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException;

    /**
     * Performs a direct authorisation using a saved card with Worldpay.
     *
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     * @return {@link DirectResponseData} Wrapper object containing information on the response from Worldpay
     */
    DirectResponseData authoriseRecurringPayment(final WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayException, InvalidCartException;
}
