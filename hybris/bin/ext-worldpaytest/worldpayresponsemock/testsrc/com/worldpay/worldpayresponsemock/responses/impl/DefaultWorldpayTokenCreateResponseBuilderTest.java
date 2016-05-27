package com.worldpay.worldpayresponsemock.responses.impl;

import com.worldpay.enums.token.TokenEvent;
import com.worldpay.internal.model.CardDetails;
import com.worldpay.internal.model.Date;
import com.worldpay.internal.model.Derived;
import com.worldpay.internal.model.PaymentInstrument;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.PaymentTokenCreate;
import com.worldpay.internal.model.Reply;
import com.worldpay.internal.model.Submit;
import com.worldpay.internal.model.Token;
import com.worldpay.internal.model.TokenDetails;
import de.hybris.bootstrap.annotations.UnitTest;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static com.worldpay.worldpayresponsemock.responses.impl.DefaultWorldpayTokenCreateResponseBuilder.CARD_BRAND;
import static com.worldpay.worldpayresponsemock.responses.impl.DefaultWorldpayTokenCreateResponseBuilder.CARD_SUB_BRAND;
import static com.worldpay.worldpayresponsemock.responses.impl.DefaultWorldpayTokenCreateResponseBuilder.CC_OWNER;
import static com.worldpay.worldpayresponsemock.responses.impl.DefaultWorldpayTokenCreateResponseBuilder.ISSUER_COUNTRY_CODE;
import static com.worldpay.worldpayresponsemock.responses.impl.DefaultWorldpayTokenCreateResponseBuilder.OBFUSCATED_PAN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayTokenCreateResponseBuilderTest {

    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";

    @InjectMocks
    private DefaultWorldpayTokenCreateResponseBuilder testObj = new DefaultWorldpayTokenCreateResponseBuilder();
    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private Submit submitMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private PaymentTokenCreate paymentTokenCreateMock;

    @Test
    public void buildTokenResponse() throws Exception {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(Collections.singletonList(submitMock));
        when(submitMock.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate()).thenReturn(Collections.singletonList(paymentTokenCreateMock));
        when(paymentTokenCreateMock.getAuthenticatedShopperID()).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(paymentTokenCreateMock.getCreateToken().getTokenEventReference()).thenReturn(TOKEN_EVENT_REFERENCE);

        final PaymentService result = testObj.buildTokenResponse(paymentServiceMock);

        final Reply reply = (Reply) result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final Token token = (Token) reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken().get(0);

        TokenDetails tokenDetails = null;
        PaymentInstrument paymentInstrument = null;
        for (final Object o : token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrError()) {
            if (o instanceof TokenDetails) {
                tokenDetails = (TokenDetails) o;
            } else if (o instanceof PaymentInstrument) {
                paymentInstrument = (PaymentInstrument) o;
            }
        }
        final CardDetails cardDetails = (CardDetails) paymentInstrument.getCardDetailsOrPaypal().get(0);
        final Derived derived = cardDetails.getDerived();
        final Date expiryDate = cardDetails.getExpiryDate().getDate();

        assertEquals(AUTHENTICATED_SHOPPER_ID, token.getAuthenticatedShopperID());
        assertEquals(TOKEN_EVENT_REFERENCE, token.getTokenEventReference());

        assertEquals(CARD_BRAND, derived.getCardBrand());
        assertEquals(CARD_SUB_BRAND, derived.getCardSubBrand());
        assertEquals(ISSUER_COUNTRY_CODE, derived.getIssuerCountryCode());
        assertEquals(OBFUSCATED_PAN, derived.getObfuscatedPAN());
        assertEquals(DateTime.now().monthOfYear().getAsString(), expiryDate.getMonth());
        assertEquals(DateTime.now().plusYears(5).year().getAsString(), expiryDate.getYear());
        assertFalse(StringUtils.isEmpty(tokenDetails.getPaymentTokenID()));
        assertEquals(TokenEvent.NEW.name(), tokenDetails.getTokenEvent());
        assertEquals(CC_OWNER, cardDetails.getCardHolderName().getvalue());
    }
}