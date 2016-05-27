package com.worldpay.core.services.impl;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.payment.Card;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;

import static com.worldpay.core.services.impl.DefaultWorldpayPaymentInfoService.WORLDPAY_CREDIT_CARD_MAPPINGS;
import static de.hybris.platform.core.enums.CreditCardType.VISA;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.startsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayPaymentInfoServiceTest {

    private static final int TIMEOUT_IN_MINUTES = 19;
    private static final String PAYMENT_TOKEN_ID = "paymentTokenId";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String CARD_NUMBER = "cardNumber";
    private static final String CARD_EXPIRY_MONTH = "cardExpiryMonth";
    private static final String CARD_EXPIRY_YEAR = "cardExpiryYear";
    private static final String CARD_HOLDER_NAME = "cardHolderName";
    private static final String CARD_DETAILS_NUMBER = "cardDetailsNumber";
    private static final String CARD_DETAILS_EXPIRY_MONTH = "cardDetailsExpiryMonth";
    private static final String CARD_DETAILS_EXPIRY_YEAR = "cardDetailsExpiryYear";
    private static final String CARD_DETAILS_HOLDER_NAME = "cardDetailsHolderName";
    private static final Date CREATION_TIME = DateTime.now().toDate();
    private static final String ORDER_CODE = "orderCode";
    private static final Double TOTAL_PRICE = Double.valueOf("100.0");
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String TOKEN_REFERENCE = "tokenReference";
    private static final String APM_CODE = "apmCode";
    private static final String NEW = "NEW";
    private static final String MATCH = "MATCH";
    private static final String CONFLICT = "CONFLICT";
    private static final DateTime DATE_TIME = new DateTime();
    private static final String CC_PAYMENT_INFO_MODEL_CODE = "ccPaymentInfoModelCode";
    private static final String CARD_BRAND = "cardBrand";
    private static final String CARD_TYPE = "CardType";
    private static final String ANOTHER_SUBSCRIPTION_ID = "anotherPaymentId";

    @Spy
    @InjectMocks
    private DefaultWorldpayPaymentInfoService testObj = new DefaultWorldpayPaymentInfoService();

    @Mock
    private ModelService modelServiceMock;
    @Mock
    private EnumerationService enumerationServiceMock;
    @Mock
    private APMConfigurationLookupService apmConfigurationLookupServiceMock;
    @Mock
    private ConfigurationService configurationServiceMock;

    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoModelMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private PaymentInfoModel paymentTransactionPaymentInfoModelMock;
    @Mock
    private WorldpayAPMPaymentInfoModel worldpayAPMPaymentInfoModelMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private DirectAuthoriseServiceResponse directAuthoriseServiceResponseMock;
    @Mock
    private WorldpayAPMConfigurationModel worldpayAPMConfigurationModelMock;
    @Mock
    private PaymentInfoModel orderPaymentInfoModelMock;
    @Mock
    private CartModel cartModelMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private TokenReply tokenReplyMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private Card cardMock;
    @Mock
    private com.worldpay.service.model.Date dateMock;
    @Mock
    private CurrencyModel currencyModelMock;
    @Mock
    private UserModel userModelMock;
    @Mock
    private PaymentInfoModel paymentInfo1Mock;
    @Mock
    private CreditCardPaymentInfoModel savedPaymentInfoMock;
    @Mock
    private Configuration configurationMock;
    @Mock
    private CreateTokenResponse createTokenResponseMock;
    @Rule
    public ExpectedException thrown = ExpectedException.none(); //NOPMD @Rule annotated instance members must be public
    @Mock (answer = RETURNS_DEEP_STUBS)
    private UpdateTokenServiceRequest updateTokenServiceRequestMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private CardDetails cardDetailsMock;

    @Before
    public void setUp() {
        when(cartModelMock.getCode()).thenReturn(ORDER_CODE);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(cartModelMock.getTotalPrice()).thenReturn(TOTAL_PRICE);
        when(cartModelMock.getCurrency()).thenReturn(currencyModelMock);
        when(cartModelMock.getUser()).thenReturn(userModelMock);
        when(orderModelMock.getUser()).thenReturn(userModelMock);
        when(modelServiceMock.create(PaymentInfoModel.class)).thenReturn(orderPaymentInfoModelMock);
        when(modelServiceMock.create(CreditCardPaymentInfoModel.class)).thenReturn(creditCardPaymentInfoModelMock);
        when(paymentTransactionModelMock.getInfo()).thenReturn(paymentTransactionPaymentInfoModelMock);
        when(paymentTransactionModelMock.getOrder().getPaymentInfo()).thenReturn(orderPaymentInfoModelMock);
        when(cardMock.getCardNumber()).thenReturn(CARD_NUMBER);
        when(cardMock.getCardHolderName()).thenReturn(CARD_HOLDER_NAME);
        when(cardMock.getExpiryDate().getMonth()).thenReturn(CARD_EXPIRY_MONTH);
        when(cardMock.getExpiryDate().getYear()).thenReturn(CARD_EXPIRY_YEAR);
        when(cardDetailsMock.getCardNumber()).thenReturn(CARD_DETAILS_NUMBER);
        when(cardDetailsMock.getCardHolderName()).thenReturn(CARD_DETAILS_HOLDER_NAME);
        when(cardDetailsMock.getExpiryDate().getMonth()).thenReturn(CARD_DETAILS_EXPIRY_MONTH);
        when(cardDetailsMock.getExpiryDate().getYear()).thenReturn(CARD_DETAILS_EXPIRY_YEAR);
        when(enumerationServiceMock.getEnumerationValue(CreditCardType.class.getSimpleName(), VISA.getCode())).thenReturn(VISA);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(directAuthoriseServiceResponseMock.getToken()).thenReturn(tokenReplyMock);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(tokenReplyMock);
        when(orderNotificationMessageMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(tokenReplyMock.getTokenDetails().getTokenEvent()).thenReturn(NEW);
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
        when(paymentTransactionModelMock.getOrder().getPaymentInfo()).thenReturn(orderPaymentInfoModelMock);
        when(paymentReplyMock.getCardDetails()).thenReturn(cardMock);
        when(tokenReplyMock.getPaymentInstrument()).thenReturn(cardMock);
        when(tokenReplyMock.getAuthenticatedShopperID()).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(tokenReplyMock.getTokenDetails().getTokenEventReference()).thenReturn(TOKEN_REFERENCE);
        when(paymentReplyMock.getMethodCode()).thenReturn(PaymentType.VISA.getMethodCode());
        when(configurationServiceMock.getConfiguration()).thenReturn(configurationMock);
        when(configurationMock.getString(WORLDPAY_CREDIT_CARD_MAPPINGS + paymentReplyMock.getMethodCode())).thenReturn(VISA.getCode());
        doReturn(DATE_TIME).when(testObj).getDateTime(dateMock);
        when(tokenReplyMock.getTokenDetails().getPaymentTokenExpiry()).thenReturn(dateMock);
        when(tokenReplyMock.getTokenDetails().getPaymentTokenID()).thenReturn(PAYMENT_TOKEN_ID);
        when(modelServiceMock.clone(paymentTransactionPaymentInfoModelMock, WorldpayAPMPaymentInfoModel.class)).thenReturn(worldpayAPMPaymentInfoModelMock);
        when(apmConfigurationLookupServiceMock.getAPMConfigurationForCode(worldpayAPMPaymentInfoModelMock.getPaymentType())).thenReturn(worldpayAPMConfigurationModelMock);
        when(worldpayAPMPaymentInfoModelMock.getApmConfiguration()).thenReturn(worldpayAPMConfigurationModelMock);
        when(paymentTransactionModelMock.getCreationtime()).thenReturn(CREATION_TIME);
        when(worldpayAPMConfigurationModelMock.getAutoCancelPendingTimeoutInMinutes()).thenReturn(TIMEOUT_IN_MINUTES);
        when(userModelMock.getPaymentInfos()).thenReturn(Arrays.asList(paymentInfo1Mock, savedPaymentInfoMock));
        when(savedPaymentInfoMock.getSubscriptionId()).thenReturn(PAYMENT_TOKEN_ID);
    }

    @Test
    public void shouldSetPaymentMethodToPaymentInfoInTransactionAndOrder() {
        testObj.savePaymentType(paymentTransactionModelMock, PaymentType.VISA.getMethodCode());

        verify(paymentTransactionPaymentInfoModelMock).setPaymentType(PaymentType.VISA.getMethodCode());
        verify(orderPaymentInfoModelMock).setPaymentType(PaymentType.VISA.getMethodCode());
        verify(modelServiceMock).saveAll(orderPaymentInfoModelMock, paymentTransactionPaymentInfoModelMock);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenCartPassedToCreatePaymentInfoIsNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("CartModel cannot be null");

        final CartModel cartModel = null;
        testObj.createPaymentInfo(cartModel);
    }

    @Test
    public void shouldUseExistingCreditCardForTokenInformationAndSaveItToPaymentTransactionAndOrderWhenEventIsMatch() {
        when(tokenReplyMock.getTokenDetails().getTokenEvent()).thenReturn(MATCH);

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verifyCardNotCreated();
    }

    @Test
    public void shouldUseExistingCreditCardForTokenInformationAndSaveItToPaymentTransactionAndOrderWhenEventIsConflict() {
        when(tokenReplyMock.getTokenDetails().getTokenEvent()).thenReturn(CONFLICT);

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verifyCardNotCreated();
    }

    @Test
    public void shouldSavePaymentMethodToPaymentInfoInTransactionAndOrder() {
        testObj.savePaymentType(paymentTransactionModelMock, PaymentType.VISA.getMethodCode());

        verify(paymentTransactionPaymentInfoModelMock).setPaymentType(PaymentType.VISA.getMethodCode());
        verify(orderPaymentInfoModelMock).setPaymentType(PaymentType.VISA.getMethodCode());
        verify(modelServiceMock).saveAll(orderPaymentInfoModelMock, paymentTransactionPaymentInfoModelMock);
    }

    @Test
    public void shouldCreatePaymentInfo() {
        testObj.createPaymentInfo(cartModelMock);

        verify(modelServiceMock).create(PaymentInfoModel.class);
        verify(orderPaymentInfoModelMock).setSaved(false);
        verify(orderPaymentInfoModelMock).setCode(startsWith(ORDER_CODE + "_"));
        verify(orderPaymentInfoModelMock).setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(orderPaymentInfoModelMock).setPaymentAmount(TOTAL_PRICE);
        verify(orderPaymentInfoModelMock).setPaymentCurrency(currencyModelMock);
        verify(orderPaymentInfoModelMock).setUser(userModelMock);
        verify(modelServiceMock).save(orderPaymentInfoModelMock);
    }

    private void verifyCardNotCreated() {
        verify(testObj).savePaymentType(paymentTransactionModelMock, PaymentType.VISA.getMethodCode());
        verify(orderModelMock).setPaymentInfo(savedPaymentInfoMock);
        verify(paymentTransactionModelMock).setInfo(savedPaymentInfoMock);
        verify(modelServiceMock).saveAll(orderModelMock, paymentTransactionModelMock);
    }

    @Test
    public void shouldCreateCreditCardPaymentInfoModelWithoutTokenAndSaveItToPaymentTransactionAndOrderForRedirectOrderNotification() {
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(false);
        when(modelServiceMock.clone(paymentTransactionPaymentInfoModelMock, CreditCardPaymentInfoModel.class)).thenReturn(creditCardPaymentInfoModelMock);
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(null);

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verify(creditCardPaymentInfoModelMock).setOriginal(null);
        verify(creditCardPaymentInfoModelMock).setDuplicate(false);
        verify(creditCardPaymentInfoModelMock).setNumber(CARD_NUMBER);
        verify(creditCardPaymentInfoModelMock).setValidToMonth(CARD_EXPIRY_MONTH);
        verify(creditCardPaymentInfoModelMock).setValidToYear(CARD_EXPIRY_YEAR);
        verify(creditCardPaymentInfoModelMock).setCcOwner(CARD_HOLDER_NAME);
        verify(creditCardPaymentInfoModelMock).setType(VISA);
        verify(modelServiceMock).save(creditCardPaymentInfoModelMock);

        verify(testObj).savePaymentType(paymentTransactionModelMock, PaymentType.VISA.getMethodCode());
        verify(orderModelMock).setPaymentInfo(creditCardPaymentInfoModelMock);
        verify(paymentTransactionModelMock).setInfo(creditCardPaymentInfoModelMock);
        verify(modelServiceMock).saveAll(orderModelMock, paymentTransactionModelMock);
    }

    @Test
    public void shouldNotCreateCreditCardPaymentInfoModelWhenPaymentTransactionIsACreditCardPaymentInfoThatHasASubscriptionId() {
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(false);

        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(null);
        when(paymentTransactionModelMock.getInfo()).thenReturn(creditCardPaymentInfoModelMock);
        when(creditCardPaymentInfoModelMock.getSubscriptionId()).thenReturn(PAYMENT_TOKEN_ID);

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verify(modelServiceMock, never()).save(any(CreditCardPaymentInfoModel.class));

        verify(orderModelMock).setPaymentInfo(creditCardPaymentInfoModelMock);
        verify(paymentTransactionModelMock).setInfo(creditCardPaymentInfoModelMock);
        verify(modelServiceMock).saveAll(orderModelMock, paymentTransactionModelMock);
    }

    @Test
    public void shouldCreateCreditCardPaymentInfoModelWithTokenAndSaveItToPaymentTransactionAndOrderForRedirectOrderNotification() {
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(false);
        when(modelServiceMock.clone(paymentTransactionPaymentInfoModelMock, CreditCardPaymentInfoModel.class)).thenReturn(creditCardPaymentInfoModelMock);
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verify(creditCardPaymentInfoModelMock).setSubscriptionId(PAYMENT_TOKEN_ID);
        verify(creditCardPaymentInfoModelMock).setSaved(true);
        verify(creditCardPaymentInfoModelMock).setEventReference(TOKEN_REFERENCE);
        verify(creditCardPaymentInfoModelMock).setAuthenticatedShopperID(AUTHENTICATED_SHOPPER_ID);
        verify(creditCardPaymentInfoModelMock).setNumber(CARD_NUMBER);
        verify(creditCardPaymentInfoModelMock).setValidToMonth(CARD_EXPIRY_MONTH);
        verify(creditCardPaymentInfoModelMock).setValidToYear(CARD_EXPIRY_YEAR);
        verify(creditCardPaymentInfoModelMock).setCcOwner(CARD_HOLDER_NAME);
        verify(creditCardPaymentInfoModelMock).setType(VISA);
        verify(creditCardPaymentInfoModelMock).setExpiryDate(DATE_TIME.toDate());
        verify(modelServiceMock).save(creditCardPaymentInfoModelMock);

        verify(testObj).savePaymentType(paymentTransactionModelMock, PaymentType.VISA.getMethodCode());
        verify(orderModelMock).setPaymentInfo(creditCardPaymentInfoModelMock);
        verify(paymentTransactionModelMock).setInfo(creditCardPaymentInfoModelMock);
        verify(modelServiceMock).saveAll(orderModelMock, paymentTransactionModelMock);
    }

    @Test
    public void shouldUseExistingCreditCardForTokenInformationAndSaveItToPaymentTransactionAndOrderForRedirectOrderNotification() {
        when(tokenReplyMock.getTokenDetails().getTokenEvent()).thenReturn(MATCH);
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(paymentTransactionModelMock.getOrder().getUser()).thenReturn(userModelMock);
        when(savedPaymentInfoMock.getSubscriptionId()).thenReturn(PAYMENT_TOKEN_ID);
        when(savedPaymentInfoMock.isSaved()).thenReturn(true);

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verify(testObj).savePaymentType(paymentTransactionModelMock, PaymentType.VISA.getMethodCode());
        verify(orderModelMock).setPaymentInfo(savedPaymentInfoMock);
        verify(paymentTransactionModelMock).setInfo(savedPaymentInfoMock);
        verify(modelServiceMock).saveAll(orderModelMock, paymentTransactionModelMock);
    }

    @Test
    public void shouldCreateCreditCardForTokenInformationIfNotFoundAndSaveItToPaymentTransactionAndOrderForRedirectOrderNotification() {
        doReturn(DATE_TIME).when(testObj).getDateTime(dateMock);

        when(tokenReplyMock.getTokenDetails().getTokenEvent()).thenReturn(MATCH);
        when(modelServiceMock.clone(paymentTransactionPaymentInfoModelMock, CreditCardPaymentInfoModel.class)).thenReturn(creditCardPaymentInfoModelMock);
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(paymentTransactionModelMock.getOrder().getUser()).thenReturn(userModelMock);
        when(savedPaymentInfoMock.getSubscriptionId()).thenReturn(ANOTHER_SUBSCRIPTION_ID);

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verify(testObj).savePaymentType(paymentTransactionModelMock, PaymentType.VISA.getMethodCode());
        verify(orderModelMock).setPaymentInfo(creditCardPaymentInfoModelMock);
        verify(paymentTransactionModelMock).setInfo(creditCardPaymentInfoModelMock);
        verify(modelServiceMock).saveAll(orderModelMock, paymentTransactionModelMock);
    }

    @Test
    public void shouldCreateAPMPaymentInfoModelAndSaveItToPaymentTransactionAndOrderForRedirectOrderNotification() {
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(true);
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verify(worldpayAPMPaymentInfoModelMock).setApmConfiguration(worldpayAPMConfigurationModelMock);
        verify(worldpayAPMPaymentInfoModelMock).setSaved(false);
        verify(worldpayAPMPaymentInfoModelMock).setTimeoutDate(DateUtils.addMinutes(CREATION_TIME, TIMEOUT_IN_MINUTES));
        verify(testObj).savePaymentType(paymentTransactionModelMock, PaymentType.VISA.getMethodCode());
        verify(orderModelMock).setPaymentInfo(worldpayAPMPaymentInfoModelMock);
        verify(paymentTransactionModelMock).setInfo(worldpayAPMPaymentInfoModelMock);
        verify(modelServiceMock).saveAll(orderModelMock, paymentTransactionModelMock);
    }

    @Test
    public void shouldCreateAPMPaymentInfoModelAndSaveItToPaymentTransactionAndOrderForRedirectOrderNotificationWithNullTimeout() {
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(true);
        when(worldpayAPMConfigurationModelMock.getCode()).thenReturn(APM_CODE);
        when(worldpayAPMConfigurationModelMock.getAutoCancelPendingTimeoutInMinutes()).thenReturn(null);
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);

        testObj.setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);

        verify(worldpayAPMPaymentInfoModelMock).setApmConfiguration(worldpayAPMConfigurationModelMock);
        verify(worldpayAPMPaymentInfoModelMock).setSaved(false);
        verify(worldpayAPMPaymentInfoModelMock).setTimeoutDate(null);
        verify(testObj).savePaymentType(paymentTransactionModelMock, PaymentType.VISA.getMethodCode());
        verify(orderModelMock).setPaymentInfo(worldpayAPMPaymentInfoModelMock);
        verify(paymentTransactionModelMock).setInfo(worldpayAPMPaymentInfoModelMock);
        verify(modelServiceMock).saveAll(orderModelMock, paymentTransactionModelMock);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenCartPassedToCreateCreditCardPaymentInfoIsNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("CartModel cannot be null");

        final CartModel cartModel = null;
        testObj.createCreditCardPaymentInfo(cartModel, createTokenResponseMock, false);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenCreateTokenPassedToCreateCreditCardPaymentInfoIsNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Token response cannot be null");

        final CreateTokenResponse tokenResponse = null;
        testObj.createCreditCardPaymentInfo(cartModelMock, tokenResponse, false);
    }

    @Test
    public void shouldCreateAndPopulateCreditCardPaymentInfoForCreateTokenResponse() {
        doReturn(CC_PAYMENT_INFO_MODEL_CODE).when(testObj).generateCcPaymentInfoCode(cartModelMock);
        when(createTokenResponseMock.getToken()).thenReturn(tokenReplyMock);
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CREDIT_CARD_MAPPINGS + CARD_BRAND)).thenReturn(CARD_TYPE);
        when(enumerationServiceMock.getEnumerationValue(CreditCardType.class.getSimpleName(), CARD_TYPE)).thenReturn(CreditCardType.VISA);
        when(tokenReplyMock.getTokenDetails().getTokenEvent()).thenReturn(NEW);

        testObj.createCreditCardPaymentInfo(cartModelMock, createTokenResponseMock, false);

        verify(modelServiceMock).save(creditCardPaymentInfoModelMock);
        verify(creditCardPaymentInfoModelMock).setCode(CC_PAYMENT_INFO_MODEL_CODE);
        verify(creditCardPaymentInfoModelMock).setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(creditCardPaymentInfoModelMock).setUser(userModelMock);
        verify(creditCardPaymentInfoModelMock).setSaved(false);
        verify(creditCardPaymentInfoModelMock).setSubscriptionId(PAYMENT_TOKEN_ID);
        verify(creditCardPaymentInfoModelMock).setType(CreditCardType.TOKEN);
    }

    @Test
    public void shouldSetCreditCardTypeToTokenPaymentTypeIsNull() {
        doReturn(CC_PAYMENT_INFO_MODEL_CODE).when(testObj).generateCcPaymentInfoCode(cartModelMock);
        when(createTokenResponseMock.getToken()).thenReturn(tokenReplyMock);
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CREDIT_CARD_MAPPINGS + CARD_BRAND)).thenReturn(CARD_TYPE);
        when(enumerationServiceMock.getEnumerationValue(CreditCardType.class.getSimpleName(), CARD_TYPE)).thenReturn(CreditCardType.VISA);
        when(tokenReplyMock.getTokenDetails().getTokenEvent()).thenReturn(NEW);

        testObj.createCreditCardPaymentInfo(cartModelMock, createTokenResponseMock, false);

        verify(creditCardPaymentInfoModelMock).setType(CreditCardType.TOKEN);
    }

    @Test
    public void shouldUseExistingCreditCardForTokenInformationAndSaveItToOrderForCreateTokenResponse() {
        when(createTokenResponseMock.getToken()).thenReturn(tokenReplyMock);
        when(tokenReplyMock.getTokenDetails().getTokenEvent()).thenReturn(MATCH);
        when(savedPaymentInfoMock.isSaved()).thenReturn(false);

        testObj.createCreditCardPaymentInfo(cartModelMock, createTokenResponseMock, true);

        verify(cartModelMock).setPaymentInfo(savedPaymentInfoMock);
        verify(savedPaymentInfoMock).setSaved(true);
        verify(modelServiceMock).save(cartModelMock);
    }

    @Test
    public void shouldNotUpdateExistingCardIfAlreadySaved() {
        when(createTokenResponseMock.getToken()).thenReturn(tokenReplyMock);
        when(tokenReplyMock.getTokenDetails().getTokenEvent()).thenReturn(MATCH);
        when(savedPaymentInfoMock.isSaved()).thenReturn(true);

        testObj.createCreditCardPaymentInfo(cartModelMock, createTokenResponseMock, false);

        verify(savedPaymentInfoMock, never()).setSaved(anyBoolean());
    }

    @Test
    public void shouldUpdateAndAttachPaymentInfoToOrderAndTransaction() {
        doReturn(CC_PAYMENT_INFO_MODEL_CODE).when(testObj).generateCcPaymentInfoCode(cartModelMock);

        testObj.updateAndAttachPaymentInfoModel(paymentTransactionModelMock, cartModelMock, creditCardPaymentInfoModelMock);

        verify(creditCardPaymentInfoModelMock).setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(creditCardPaymentInfoModelMock).setPaymentAmount(TOTAL_PRICE);
        verify(creditCardPaymentInfoModelMock).setPaymentCurrency(currencyModelMock);

        verify(cartModelMock).setPaymentInfo(creditCardPaymentInfoModelMock);
        verify(paymentTransactionModelMock).setInfo(creditCardPaymentInfoModelMock);
        verify(modelServiceMock).save(creditCardPaymentInfoModelMock);
        verify(modelServiceMock).saveAll(cartModelMock, paymentTransactionModelMock);
    }

    @Test
    public void shouldCreateWorldpayApmPaymentInfo() {

        testObj.createWorldpayApmPaymentInfo(paymentTransactionModelMock);

        verify(worldpayAPMPaymentInfoModelMock).setApmConfiguration(worldpayAPMConfigurationModelMock);
        verify(worldpayAPMPaymentInfoModelMock).setSaved(false);
        verify(worldpayAPMPaymentInfoModelMock).setTimeoutDate(DateUtils.addMinutes(CREATION_TIME, TIMEOUT_IN_MINUTES));
        verify(modelServiceMock).save(worldpayAPMPaymentInfoModelMock);
    }

    @Test
    public void shouldSetCreditCardTypeOnPaymentInfo() {

        testObj.setCreditCardType(creditCardPaymentInfoModelMock, paymentReplyMock);

        verify(creditCardPaymentInfoModelMock).setPaymentType(PaymentType.VISA.getMethodCode());
        verify(creditCardPaymentInfoModelMock).setType(CreditCardType.VISA);
    }

    @Test
    public void shouldUpdateCreditCardPaymentInfo() {
        when(updateTokenServiceRequestMock.getUpdateTokenRequest().getCardDetails()).thenReturn(cardDetailsMock);
        when(updateTokenServiceRequestMock.getUpdateTokenRequest().getPaymentTokenId()).thenReturn(PAYMENT_TOKEN_ID);

        testObj.updateCreditCardPaymentInfo(cartModelMock, updateTokenServiceRequestMock);

        verify(savedPaymentInfoMock).setValidToMonth(CARD_DETAILS_EXPIRY_MONTH);
        verify(savedPaymentInfoMock).setValidToYear(CARD_DETAILS_EXPIRY_YEAR);
        verify(savedPaymentInfoMock).setCcOwner(CARD_DETAILS_HOLDER_NAME);
        verify(modelServiceMock).save(savedPaymentInfoMock);
        verify(modelServiceMock, never()).save(paymentInfo1Mock);
    }
}