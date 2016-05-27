package com.worldpay.service.payment;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.BasicOrderInfo;
import com.worldpay.service.model.Browser;
import com.worldpay.service.model.Session;
import com.worldpay.service.model.Shopper;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.token.TokenRequest;
import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.util.Currency;

/**
 * Exposes utility methods to create objects used in the request creation.
 */
public interface WorldpayOrderService {

    /**
     * Creates a Worldpay {@link Amount} object using the {@link CurrencyModel} and the amount.
     * @param currencyModel currency to be used
     * @param amount        total amount of the order
     */
    Amount createAmount(final CurrencyModel currencyModel, final double amount);

    /**
     * Creates a Worldpay {@link Amount} object using the {@link Currency} and the amount.
     * @param currency
     * @param amount
     * @return Amount object required by DirectAuthoriseServiceRequest
     */
    Amount createAmount(final Currency currency, final double amount);

    /**
     * Creates a Worldpay {@link Session} object
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     */
    Session createSession(final WorldpayAdditionalInfoData worldpayAdditionalInfoData);

    /**
     * Creates a Worldpay {@link Browser} object
     * @param worldpayAdditionalInfoData Object that contains information about the current session, browser used, and cookies.
     */
    Browser createBrowser(final WorldpayAdditionalInfoData worldpayAdditionalInfoData);

    /**
     * Creates a Worldpay {@link BasicOrderInfo} object
     * @param worldpayOrderCode Identifier of the order in Worldpay
     * @param description       Description of the order
     * @param amount            Payable amount {@link Amount}
     */
    BasicOrderInfo createBasicOrderInfo(final String worldpayOrderCode, final String description, final Amount amount);

    /**
     * @return {@link WorldpayServiceGateway} used to communicate with Worldpay
     */
    WorldpayServiceGateway getWorldpayServiceGateway();

    /**
     * Creates a {@link Shopper} object to be used in the Requests
     * @param customerEmail customer email
     * @param session       session information
     * @param browser       browser information
     * @return Shopper object
     */
    Shopper createShopper(String customerEmail, final Session session, final Browser browser);

    /**
     * @param customerEmail          customer email
     * @param authenticatedShopperID unique identifier for the shopper
     * @param session                session information
     * @param browser                browser information
     * @return authenticated Shopper
     */
    Shopper createAuthenticatedShopper(final String customerEmail, final String authenticatedShopperID, final Session session, final Browser browser);

    /**
     * @param tokenEventReference unique identifier for the token transaction
     * @param tokenReason         refers to the seller so they can be tracked to the site/web.
     * @return Shopper object with an authenticatedShopperID
     */
    TokenRequest createTokenRequest(final String tokenEventReference, final String tokenReason);

    /**
     * Creates a payment element to be used in bank transfers
     * @param paymentMethod      indicates which payment method for bank transfer is going to be used (IDEAL-SSL,...)
     * @param shopperCountryCode indicates the country code of the shopper
     * @param shopperBankCode    indicates the selected bank by the user
     * @return Payment object
     */
    Payment createPayment(final String paymentMethod, final String shopperBankCode, final String shopperCountryCode) throws WorldpayConfigurationException;
}
