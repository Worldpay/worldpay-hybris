package com.worldpay.worldpayresponsemock.responses.impl;

import com.worldpay.exception.WorldpayException;
import com.worldpay.internal.model.AccountTx;
import com.worldpay.internal.model.Address;
import com.worldpay.internal.model.Address1;
import com.worldpay.internal.model.Address2;
import com.worldpay.internal.model.Address3;
import com.worldpay.internal.model.Balance;
import com.worldpay.internal.model.CardAddress;
import com.worldpay.internal.model.CardDetails;
import com.worldpay.internal.model.Date;
import com.worldpay.internal.model.Derived;
import com.worldpay.internal.model.Journal;
import com.worldpay.internal.model.Notify;
import com.worldpay.internal.model.OrderStatusEvent;
import com.worldpay.internal.model.Payment;
import com.worldpay.internal.model.PaymentInstrument;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.PaymentTokenExpiry;
import com.worldpay.internal.model.Token;
import com.worldpay.internal.model.TokenDetails;
import com.worldpay.internal.model.TokenReason;
import com.worldpay.service.marshalling.impl.DefaultPaymentServiceMarshaller;
import com.worldpay.worldpayresponsemock.form.ResponseForm;
import de.hybris.bootstrap.annotations.UnitTest;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.worldpayresponsemock.responses.impl.DefaultWorldpayNotificationResponseBuilder.REFUND_WEBFORM_ISSUED;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayNotificationResponseBuilderTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String CC = "CC";
    private static final String TOKEN = "Token";
    private static final String RMM = "RMM";
    private static final String RG = "RG";
    private static final String LAST_EVENT = "lastEvent";
    private static final String JOURNAL_TYPE = "journalType";
    private static final String CURRENT_DAY = "19";
    private static final String CURRENT_MONTH = "9";
    private static final String CURRENT_YEAR = "2015";
    private static final String CC_PAYMENT_TYPE = "ccPaymentType";
    private static final String APM_PAYMENT_TYPE = "apmPaymentType";
    private static final String CARD_MONTH = "1";
    private static final String CARD_YEAR = "2019";
    private static final String TEST_CREDIT_CARD = "1111111111111111";
    private static final String TRANSACTION_AMOUNT = "100";
    private static final String CURRENCY_CODE = "GBP";
    private static final int EXPONENT = 2;
    private static final String RISK_VALUE = "99";
    private static final String FINAL_SCORE = "22";
    private static final String APM = "APM";
    private static final String RESPONSE_DESCRIPTION = "responseDescription";
    private static final int RESPONSE_CODE = 20;
    private static final String AAV_TELEPHONE = "aavTelephone";
    private static final String AAV_POSTCODE = "aavPostcode";
    private static final String AAV_EMAIL = "aavEmail";
    private static final String AAV_CARD_HOLDER = "aavCardHolder";
    private static final String AAV_ADDRESS_VALUE = "aavAddressValue";
    private static final String TOKEN_EXPIRY_DAY_VALUE = "tokenExpiryDayValue";
    private static final String TOKEN_EXPIRY_MONTH_VALUE = "tokenExpiryMonthValue";
    private static final String TOKEN_EXPIRY_YEAR_VALUE = "tokenExpiryYearValue";
    private static final String PAYMENT_TOKEN_ID_VALUE = "paymentTokenIdValue";
    private static final String TOKEN_EVENT_VALUE = "tokenEventValue";
    private static final String TOKEN_EVENT_DETAILS_REFERENCE_VALUE = "tokenEventDetailsReferenceValue";
    private static final String TOKEN_DETAILS_REASON_VALUE = "tokenDetailsReasonValue";
    private static final String ADDRESS_1_VALUE = "address1Value";
    private static final String ADDRESS_2_VALUE = "address2Value";
    private static final String ADDRESS_3_VALUE = "address3Value";
    private static final String ADDRESS_LAST_NAME_VALUE = "addressLastNameValue";
    private static final String ADDRESS_POSTAL_CODE_VALUE = "addressPostalCodeValue";
    private static final String ADDRESS_CITY_VALUE = "addressCityValue";
    private static final String ADDRESS_COUNTRY_CODE_VALUE = "addressCountryCodeValue";
    private static final String CARD_EXPIRY_YEAR_VALUE = "cardExpiryYearValue";
    private static final String CARD_EXPIRY_MONTH_VALUE = "cardExpiryMonthValue";
    private static final String CARD_HOLDER_NAME_VALUE = "cardHolderNameValue";
    private static final String DERIVED_CARD_SUB_BRAND_VALUE = "derivedCardSubBrandValue";
    private static final String DERIVED_CARD_BRAND_VALUE = "derivedCardBrandValue";
    private static final String DERIVED_OBFUSCATED_PAN_VALUE = "derivedObfuscatedPANValue";
    private static final String DERIVED_ISSUER_COUNTRY_CODE_VALUE = "derivedIssuerCountryCodeValue";
    private static final String AUTHENTICATED_SHOPPER_ID_VALUE = "authenticatedShopperIdValue";
    private static final String TOKEN_EVENT_REFERENCE_VALUE = "tokenEventReferenceValue";
    private static final String TOKEN_REASON_VALUE = "tokenReasonValue";

    @Mock
    private ResponseForm responseFormMock;
    @Mock
    private DefaultPaymentServiceMarshaller paymentServiceMarshallerMock;
    @Captor
    private ArgumentCaptor<PaymentService> paymentServiceCaptor;

    @Spy
    @InjectMocks
    private DefaultWorldpayNotificationResponseBuilder testObj = new DefaultWorldpayNotificationResponseBuilder();

    @Before
    public void setUp() {
        when(responseFormMock.getSelectedPaymentMethod()).thenReturn(CC);
        when(responseFormMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(responseFormMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(responseFormMock.getLastEvent()).thenReturn(LAST_EVENT);
        when(responseFormMock.getJournalType()).thenReturn(JOURNAL_TYPE);
        when(responseFormMock.getCurrentDay()).thenReturn(CURRENT_DAY);
        when(responseFormMock.getCurrentMonth()).thenReturn(CURRENT_MONTH);
        when(responseFormMock.getCurrentYear()).thenReturn(CURRENT_YEAR);
        when(responseFormMock.getTransactionAmount()).thenReturn(TRANSACTION_AMOUNT);
        when(responseFormMock.getCurrencyCode()).thenReturn(CURRENCY_CODE);
        when(responseFormMock.getExponent()).thenReturn(EXPONENT);
        when(responseFormMock.getRiskValue()).thenReturn(RISK_VALUE);
        when(responseFormMock.getFinalScore()).thenReturn(FINAL_SCORE);
        when(responseFormMock.getResponseCode()).thenReturn(RESPONSE_CODE);
        when(responseFormMock.getResponseDescription()).thenReturn(RESPONSE_DESCRIPTION);
        when(responseFormMock.getCardHolderName()).thenReturn(CARD_HOLDER_NAME_VALUE);
        doReturn(paymentServiceMarshallerMock).when(testObj).getPaymentServiceMarshaller();
    }

    @Test
    public void buildResponseShouldReturnAnXMLWithCreditCardDataAndNoRiskScore() throws WorldpayException {
        when(responseFormMock.getSelectedRiskScore()).thenReturn(StringUtils.EMPTY);
        when(responseFormMock.getCcPaymentType()).thenReturn(CC_PAYMENT_TYPE);
        when(responseFormMock.getCardHolderName()).thenReturn(CARD_HOLDER_NAME_VALUE);
        when(responseFormMock.getCardMonth()).thenReturn(CARD_MONTH);
        when(responseFormMock.getCardYear()).thenReturn(CARD_YEAR);
        when(responseFormMock.getTestCreditCard()).thenReturn(TEST_CREDIT_CARD);

        testObj.buildResponse(responseFormMock);

        verify(paymentServiceMarshallerMock).marshal(paymentServiceCaptor.capture());
        final PaymentService paymentService = paymentServiceCaptor.getValue();
        verifyCommonFieldsForAPMAndCreditCards(paymentService);

        final Notify notify = (Notify) paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final OrderStatusEvent orderStatusEvent = (OrderStatusEvent) notify.getOrderStatusEventOrReport().get(0);
        final Payment payment = orderStatusEvent.getPayment();

        assertEquals(CC_PAYMENT_TYPE, payment.getPaymentMethod());
        assertEquals(CARD_HOLDER_NAME_VALUE, payment.getCardHolderName().getvalue());
        assertEquals(CARD_MONTH, payment.getPaymentMethodDetail().getCard().getExpiryDate().getDate().getMonth());
        assertEquals(CARD_YEAR, payment.getPaymentMethodDetail().getCard().getExpiryDate().getDate().getYear());
        assertEquals(TEST_CREDIT_CARD, payment.getPaymentMethodDetail().getCard().getNumber());
        assertNull(payment.getRiskScore());
    }

    @Test
    public void buildResponseShouldReturnAnXMLWithCreditCardDataAndRMMScore() throws WorldpayException {
        when(responseFormMock.getCcPaymentType()).thenReturn(CC_PAYMENT_TYPE);
        when(responseFormMock.getCardHolderName()).thenReturn(CARD_HOLDER_NAME_VALUE);
        when(responseFormMock.getCardMonth()).thenReturn(CARD_MONTH);
        when(responseFormMock.getCardYear()).thenReturn(CARD_YEAR);
        when(responseFormMock.getTestCreditCard()).thenReturn(TEST_CREDIT_CARD);
        when(responseFormMock.getSelectedRiskScore()).thenReturn(RMM);

        testObj.buildResponse(responseFormMock);

        verify(paymentServiceMarshallerMock).marshal(paymentServiceCaptor.capture());
        final PaymentService paymentService = paymentServiceCaptor.getValue();
        verifyCommonFieldsForAPMAndCreditCards(paymentService);

        final Notify notify = (Notify) paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final OrderStatusEvent orderStatusEvent = (OrderStatusEvent) notify.getOrderStatusEventOrReport().get(0);
        final Payment payment = orderStatusEvent.getPayment();

        assertEquals(CC_PAYMENT_TYPE, payment.getPaymentMethod());
        assertEquals(CARD_HOLDER_NAME_VALUE, payment.getCardHolderName().getvalue());
        assertEquals(CARD_MONTH, payment.getPaymentMethodDetail().getCard().getExpiryDate().getDate().getMonth());
        assertEquals(CARD_YEAR, payment.getPaymentMethodDetail().getCard().getExpiryDate().getDate().getYear());
        assertEquals(TEST_CREDIT_CARD, payment.getPaymentMethodDetail().getCard().getNumber());
        assertEquals(RISK_VALUE, payment.getRiskScore().getValue());
        assertNull(payment.getRiskScore().getFinalScore());
    }

    @Test
    public void buildResponseShouldReturnAnXMLWithCreditCardDataAndRGScore() throws WorldpayException {
        when(responseFormMock.getCcPaymentType()).thenReturn(CC_PAYMENT_TYPE);
        when(responseFormMock.getCardHolderName()).thenReturn(CARD_HOLDER_NAME_VALUE);
        when(responseFormMock.getCardMonth()).thenReturn(CARD_MONTH);
        when(responseFormMock.getCardYear()).thenReturn(CARD_YEAR);
        when(responseFormMock.getTestCreditCard()).thenReturn(TEST_CREDIT_CARD);
        when(responseFormMock.getSelectedRiskScore()).thenReturn(RG);

        testObj.buildResponse(responseFormMock);

        verify(paymentServiceMarshallerMock).marshal(paymentServiceCaptor.capture());
        final PaymentService paymentService = paymentServiceCaptor.getValue();
        verifyCommonFieldsForAPMAndCreditCards(paymentService);

        final Notify notify = (Notify) paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final OrderStatusEvent orderStatusEvent = (OrderStatusEvent) notify.getOrderStatusEventOrReport().get(0);
        final Payment payment = orderStatusEvent.getPayment();

        assertEquals(CC_PAYMENT_TYPE, payment.getPaymentMethod());
        assertEquals(CARD_HOLDER_NAME_VALUE, payment.getCardHolderName().getvalue());
        assertEquals(CARD_MONTH, payment.getPaymentMethodDetail().getCard().getExpiryDate().getDate().getMonth());
        assertEquals(CARD_YEAR, payment.getPaymentMethodDetail().getCard().getExpiryDate().getDate().getYear());
        assertEquals(TEST_CREDIT_CARD, payment.getPaymentMethodDetail().getCard().getNumber());
        assertEquals(FINAL_SCORE, payment.getRiskScore().getFinalScore());
        assertNull(payment.getRiskScore().getValue());
    }

    @Test
    public void buildResponseShouldReturnAnXMLWithAPM() throws WorldpayException {
        when(responseFormMock.getSelectedPaymentMethod()).thenReturn(APM);
        when(responseFormMock.getApmPaymentType()).thenReturn(APM_PAYMENT_TYPE);
        when(responseFormMock.getSelectedRiskScore()).thenReturn(StringUtils.EMPTY);

        testObj.buildResponse(responseFormMock);

        verify(paymentServiceMarshallerMock).marshal(paymentServiceCaptor.capture());
        final PaymentService paymentService = paymentServiceCaptor.getValue();
        verifyCommonFieldsForAPMAndCreditCards(paymentService);

        final Notify notify = (Notify) paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final OrderStatusEvent orderStatusEvent = (OrderStatusEvent) notify.getOrderStatusEventOrReport().get(0);
        final Payment payment = orderStatusEvent.getPayment();

        assertEquals(APM_PAYMENT_TYPE, payment.getPaymentMethod());
    }

    @Test
    public void buildResponseShouldReturnAnXMLWithAavResponse() throws WorldpayException {
        when(responseFormMock.getAavAddress()).thenReturn(AAV_ADDRESS_VALUE);
        when(responseFormMock.getAavCardholderName()).thenReturn(AAV_CARD_HOLDER);
        when(responseFormMock.getAavEmail()).thenReturn(AAV_EMAIL);
        when(responseFormMock.getAavPostcode()).thenReturn(AAV_POSTCODE);
        when(responseFormMock.getAavTelephone()).thenReturn(AAV_TELEPHONE);

        testObj.buildResponse(responseFormMock);

        verify(paymentServiceMarshallerMock).marshal(paymentServiceCaptor.capture());
        final PaymentService paymentService = paymentServiceCaptor.getValue();
        final Notify notify = (Notify) paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final OrderStatusEvent orderStatusEvent = (OrderStatusEvent) notify.getOrderStatusEventOrReport().get(0);
        final Payment payment = orderStatusEvent.getPayment();

        assertEquals(AAV_ADDRESS_VALUE, payment.getAAVAddressResultCode().getDescription().get(0));
        assertEquals(AAV_CARD_HOLDER, payment.getAAVCardholderNameResultCode().getDescription().get(0));
        assertEquals(AAV_EMAIL, payment.getAAVEmailResultCode().getDescription().get(0));
        assertEquals(AAV_POSTCODE, payment.getAAVPostcodeResultCode().getDescription().get(0));
        assertEquals(AAV_TELEPHONE, payment.getAAVTelephoneResultCode().getDescription().get(0));
    }

    @Test
    public void shouldCreateAndPopulateToken() throws WorldpayException {
        when(responseFormMock.getSelectToken()).thenReturn(TOKEN);

        when(responseFormMock.getTokenExpiryDay()).thenReturn(TOKEN_EXPIRY_DAY_VALUE);
        when(responseFormMock.getTokenExpiryMonth()).thenReturn(TOKEN_EXPIRY_MONTH_VALUE);
        when(responseFormMock.getTokenExpiryYear()).thenReturn(TOKEN_EXPIRY_YEAR_VALUE);
        when(responseFormMock.getTokenDetailsReason()).thenReturn(TOKEN_DETAILS_REASON_VALUE);
        when(responseFormMock.getAddress1()).thenReturn(ADDRESS_1_VALUE);
        when(responseFormMock.getAddress2()).thenReturn(ADDRESS_2_VALUE);
        when(responseFormMock.getAddress3()).thenReturn(ADDRESS_3_VALUE);
        when(responseFormMock.getLastName()).thenReturn(ADDRESS_LAST_NAME_VALUE);
        when(responseFormMock.getPostalCode()).thenReturn(ADDRESS_POSTAL_CODE_VALUE);
        when(responseFormMock.getCity()).thenReturn(ADDRESS_CITY_VALUE);
        when(responseFormMock.getCountryCode()).thenReturn(ADDRESS_COUNTRY_CODE_VALUE);
        when(responseFormMock.getCardExpiryMonth()).thenReturn(CARD_EXPIRY_MONTH_VALUE);
        when(responseFormMock.getCardExpiryYear()).thenReturn(CARD_EXPIRY_YEAR_VALUE);
        when(responseFormMock.getTokenCardHolderName()).thenReturn(CARD_HOLDER_NAME_VALUE);
        when(responseFormMock.getCardSubBrand()).thenReturn(DERIVED_CARD_SUB_BRAND_VALUE);
        when(responseFormMock.getCardBrand()).thenReturn(DERIVED_CARD_BRAND_VALUE);
        when(responseFormMock.getObfuscatedPAN()).thenReturn(DERIVED_OBFUSCATED_PAN_VALUE);
        when(responseFormMock.getIssuerCountry()).thenReturn(DERIVED_ISSUER_COUNTRY_CODE_VALUE);
        when(responseFormMock.getAuthenticatedShopperId()).thenReturn(AUTHENTICATED_SHOPPER_ID_VALUE);
        when(responseFormMock.getTokenEventReference()).thenReturn(TOKEN_EVENT_REFERENCE_VALUE);
        when(responseFormMock.getTokenReason()).thenReturn(TOKEN_REASON_VALUE);
        when(responseFormMock.getPaymentTokenId()).thenReturn(PAYMENT_TOKEN_ID_VALUE);
        when(responseFormMock.getTokenEvent()).thenReturn(TOKEN_EVENT_VALUE);
        when(responseFormMock.getTokenDetailsEventReference()).thenReturn(TOKEN_EVENT_DETAILS_REFERENCE_VALUE);

        testObj.buildResponse(responseFormMock);

        verify(paymentServiceMarshallerMock).marshal(paymentServiceCaptor.capture());
        final PaymentService paymentService = paymentServiceCaptor.getValue();
        final Notify notify = (Notify) paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final OrderStatusEvent orderStatusEvent = (OrderStatusEvent) notify.getOrderStatusEventOrReport().get(0);
        final Token token = orderStatusEvent.getToken();
        for (Object tokenElement : token.getTokenReasonOrTokenDetailsOrPaymentInstrumentOrError()) {
            if (tokenElement instanceof TokenDetails) {
                TokenDetails tokenDetails = (TokenDetails) tokenElement;
                assertEquals(TOKEN_DETAILS_REASON_VALUE, tokenDetails.getTokenReason().getvalue());
                final PaymentTokenExpiry paymentTokenExpiry = tokenDetails.getPaymentTokenExpiry();

                assertEquals(TOKEN_EXPIRY_DAY_VALUE, paymentTokenExpiry.getDate().getDayOfMonth());
                assertEquals(TOKEN_EXPIRY_MONTH_VALUE, paymentTokenExpiry.getDate().getMonth());
                assertEquals(TOKEN_EXPIRY_YEAR_VALUE, paymentTokenExpiry.getDate().getYear());

                assertEquals(PAYMENT_TOKEN_ID_VALUE, tokenDetails.getPaymentTokenID());
                assertEquals(TOKEN_EVENT_VALUE, tokenDetails.getTokenEvent());
                assertEquals(TOKEN_EVENT_DETAILS_REFERENCE_VALUE, tokenDetails.getTokenEventReference());
            } else if (tokenElement instanceof PaymentInstrument) {
                final PaymentInstrument paymentInstrument = (PaymentInstrument) tokenElement;
                final CardDetails cardDetails = (CardDetails) paymentInstrument.getCardDetailsOrPaypal().get(0);
                assertEquals(CARD_HOLDER_NAME_VALUE, cardDetails.getCardHolderName().getvalue());

                final Derived derived = cardDetails.getDerived();
                assertEquals(DERIVED_CARD_SUB_BRAND_VALUE, derived.getCardSubBrand());
                assertEquals(DERIVED_CARD_BRAND_VALUE, derived.getCardBrand());
                assertEquals(DERIVED_OBFUSCATED_PAN_VALUE, derived.getObfuscatedPAN());
                assertEquals(DERIVED_ISSUER_COUNTRY_CODE_VALUE, derived.getIssuerCountryCode());

                final Date expiryDate = cardDetails.getExpiryDate().getDate();
                assertEquals(CARD_EXPIRY_MONTH_VALUE, expiryDate.getMonth());
                assertEquals(CARD_EXPIRY_YEAR_VALUE, expiryDate.getYear());

                final CardAddress cardDetailAddress = cardDetails.getCardAddress();
                final Address address = cardDetailAddress.getAddress();

                assertEquals(ADDRESS_LAST_NAME_VALUE, address.getLastName());
                assertEquals(ADDRESS_POSTAL_CODE_VALUE, address.getPostalCode());
                assertEquals(ADDRESS_CITY_VALUE, address.getCity());
                assertEquals(ADDRESS_COUNTRY_CODE_VALUE, address.getCountryCode());
                for (Object addressElement : address.getStreetOrHouseNameOrHouseNumberOrHouseNumberExtensionOrAddress1OrAddress2OrAddress3()) {
                    if (addressElement instanceof Address1) {
                        assertEquals(ADDRESS_1_VALUE, ((Address1) addressElement).getvalue());
                    } else if (addressElement instanceof Address2) {
                        assertEquals(ADDRESS_2_VALUE, ((Address2) addressElement).getvalue());
                    } else if (addressElement instanceof Address3) {
                        assertEquals(ADDRESS_3_VALUE, ((Address3) addressElement).getvalue());
                    }
                }
            } else if (tokenElement instanceof TokenReason) {
                TokenReason tokenReason = (TokenReason) tokenElement;
                assertEquals(TOKEN_REASON_VALUE, tokenReason.getvalue());
            }
        }

        assertEquals(AUTHENTICATED_SHOPPER_ID_VALUE, token.getAuthenticatedShopperID());
        assertEquals(TOKEN_EVENT_REFERENCE_VALUE, token.getTokenEventReference());
    }

    @Test
    public void shouldNotCreateTokenWhenNoTokenInResponse() throws WorldpayException {
        testObj.buildResponse(responseFormMock);

        verify(paymentServiceMarshallerMock).marshal(paymentServiceCaptor.capture());
        final PaymentService paymentService = paymentServiceCaptor.getValue();

        final Notify notify = (Notify) paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final OrderStatusEvent orderStatusEvent = (OrderStatusEvent) notify.getOrderStatusEventOrReport().get(0);

        assertNull(orderStatusEvent.getToken());
    }

    @Test
    public void shouldAddWebformWhenJournalTypeIsWebform() throws WorldpayException {
        when(responseFormMock.getJournalType()).thenReturn(REFUND_WEBFORM_ISSUED);

        testObj.buildResponse(responseFormMock);

        verify(testObj).createShopperWebformRefundDetails(responseFormMock);
    }

    private void verifyCommonFieldsForAPMAndCreditCards(final PaymentService paymentService) {
        final Notify notify = (Notify) paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final OrderStatusEvent orderStatusEvent = (OrderStatusEvent) notify.getOrderStatusEventOrReport().get(0);
        final Payment payment = orderStatusEvent.getPayment();
        final Journal journal = orderStatusEvent.getJournal();
        final Balance balance = payment.getBalance().get(0);
        final AccountTx accountTx = journal.getAccountTx().get(0);

        assertEquals(MERCHANT_CODE, paymentService.getMerchantCode());
        assertEquals(WORLDPAY_ORDER_CODE, orderStatusEvent.getOrderCode());
        assertEquals(LAST_EVENT, payment.getLastEvent());
        assertEquals(JOURNAL_TYPE, journal.getJournalType());
        assertEquals(CURRENT_DAY, journal.getBookingDate().getDate().getDayOfMonth());
        assertEquals(CURRENT_MONTH, journal.getBookingDate().getDate().getMonth());
        assertEquals(CURRENT_YEAR, journal.getBookingDate().getDate().getYear());
        assertEquals(TRANSACTION_AMOUNT, payment.getAmount().getValue());
        assertEquals(TRANSACTION_AMOUNT, balance.getAmount().getValue());
        assertEquals(TRANSACTION_AMOUNT, accountTx.getAmount().getValue());
        assertEquals(CURRENCY_CODE, payment.getAmount().getCurrencyCode());
        assertEquals(CURRENCY_CODE, balance.getAmount().getCurrencyCode());
        assertEquals(CURRENCY_CODE, accountTx.getAmount().getCurrencyCode());
        assertEquals(RESPONSE_CODE, Integer.valueOf(payment.getISO8583ReturnCode().getCode()).intValue());
        assertEquals(RESPONSE_DESCRIPTION, payment.getISO8583ReturnCode().getDescription());
        assertEquals(String.valueOf(EXPONENT), payment.getAmount().getExponent());
        assertEquals(String.valueOf(EXPONENT), balance.getAmount().getExponent());
        assertEquals(String.valueOf(EXPONENT), accountTx.getAmount().getExponent());
    }
}