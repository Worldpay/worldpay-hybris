package com.worldpay.worldpayresponsemock.responses.impl;

import com.worldpay.enums.token.TokenEvent;
import com.worldpay.internal.model.CardDetails;
import com.worldpay.internal.model.CardHolderName;
import com.worldpay.internal.model.Date;
import com.worldpay.internal.model.Derived;
import com.worldpay.internal.model.ExpiryDate;
import com.worldpay.internal.model.PaymentInstrument;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.PaymentTokenCreate;
import com.worldpay.internal.model.PaymentTokenExpiry;
import com.worldpay.internal.model.Reply;
import com.worldpay.internal.model.Submit;
import com.worldpay.internal.model.Token;
import com.worldpay.internal.model.TokenDetails;
import com.worldpay.internal.model.TokenReason;
import org.joda.time.DateTime;

import java.util.UUID;

public class DefaultWorldpayTokenCreateResponseBuilder implements com.worldpay.worldpayresponsemock.responses.WorldpayTokenCreateResponseBuilder {

    protected static final String CC_OWNER = "ccOwner";
    protected static final String CARD_BRAND = "VISA";
    protected static final String CARD_SUB_BRAND = "VISA_CREDIT";
    protected static final String ISSUER_COUNTRY_CODE = "N/A";
    protected static final String OBFUSCATED_PAN = "4444********1111";

    @Override
    public PaymentService buildTokenResponse(final PaymentService paymentService) {

        final PaymentService response = new PaymentService();

        final Submit submit = (Submit) paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final PaymentTokenCreate paymentTokenCreate = (PaymentTokenCreate) submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate().get(0);
        final String authenticatedShopperID = paymentTokenCreate.getAuthenticatedShopperID();
        final String tokenEventReference = paymentTokenCreate.getCreateToken().getTokenEventReference();

        final Reply reply = new Reply();
        final Token token = new Token();
        token.setAuthenticatedShopperID(authenticatedShopperID);
        token.setTokenEventReference(tokenEventReference);
        final TokenDetails tokenDetails = new TokenDetails();
        tokenDetails.setTokenEventReference(tokenEventReference);
        tokenDetails.setTokenEvent(TokenEvent.NEW.name());
        final PaymentTokenExpiry paymentTokenExpiry = new PaymentTokenExpiry();
        final DateTime dateTime = DateTime.now();
        final Date paymentTokenExpiryDate = getExpiryDate(dateTime);
        paymentTokenExpiry.setDate(paymentTokenExpiryDate);
        tokenDetails.setPaymentTokenExpiry(paymentTokenExpiry);
        final TokenReason tokenReason = new TokenReason();
        tokenReason.setvalue("Reason for token");
        tokenDetails.setTokenReason(tokenReason);
        tokenDetails.setPaymentTokenID(UUID.randomUUID().toString());
        token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrError().add(tokenDetails);
        final PaymentInstrument paymentInstrument = new PaymentInstrument();
        final CardDetails cardDetails = new CardDetails();
        final ExpiryDate expiryDate = new ExpiryDate();
        final Date cardExpiryDate = getExpiryDate(dateTime);
        expiryDate.setDate(cardExpiryDate);
        cardDetails.setExpiryDate(expiryDate);
        final CardHolderName cardHolderName = new CardHolderName();
        cardHolderName.setvalue(CC_OWNER);
        cardDetails.setCardHolderName(cardHolderName);

        final Derived derived = new Derived();
        derived.setCardBrand(CARD_BRAND);
        derived.setCardSubBrand(CARD_SUB_BRAND);
        derived.setIssuerCountryCode(ISSUER_COUNTRY_CODE);
        derived.setObfuscatedPAN(OBFUSCATED_PAN);
        cardDetails.setDerived(derived);

        paymentInstrument.getCardDetailsOrPaypal().add(cardDetails);
        token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrError().add(paymentInstrument);
        reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken().add(token);
        response.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);

        return response;
    }

    private Date getExpiryDate(final DateTime dateTime) {
        final Date paymentTokenExpiryDate = new Date();
        paymentTokenExpiryDate.setDayOfMonth(String.valueOf(dateTime.getDayOfMonth()));
        paymentTokenExpiryDate.setMonth(String.valueOf(dateTime.getMonthOfYear()));
        paymentTokenExpiryDate.setYear(String.valueOf(dateTime.plusYears(5).getYear()));
        return paymentTokenExpiryDate;
    }
}
