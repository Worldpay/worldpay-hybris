package com.worldpay.service.payment.impl;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.BasicOrderInfo;
import com.worldpay.service.model.Browser;
import com.worldpay.service.model.Session;
import com.worldpay.service.model.Shopper;
import com.worldpay.service.model.payment.AlternativeShopperBankCodePayment;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.token.TokenRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Currency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayOrderServiceTest {

    private static final double AMOUNT = 19.3;
    private static final String BANK_CODE = "bankCode";
    private static final String FULL_SUCCESS_URL = "fullSuccessURL";
    private static final String SESSION_ID = "sessionId";
    private static final String IP_ADDRESS = "ipAddress";
    private static final String DEVICE_TYPE = "deviceType";
    private static final String ACCEPT_HEADER = "acceptHeader";
    private static final String USER_AGENT_HEADER = "userAgentHeader";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String ORDER_DESCRIPTION = "orderDescription";
    private static final String GBP = "GBP";
    private static final String CUSTOMER_EMAIL = "customerEmail";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperID";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";
    private static final String TOKEN_REASON = "tokenReason";

    @InjectMocks
    private DefaultWorldpayOrderService testObj = new DefaultWorldpayOrderService();

    @Mock
    private CommonI18NService commonI18NServiceMock;
    @Mock
    private WorldpayUrlService worldpayUrlService;
    @Mock
    private CurrencyModel currencyModelMock;
    @Mock
    private Session sessionMock;
    @Mock
    private Browser browserMock;
    @Mock
    private Amount amountMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;

    @Test
    public void shouldFormatValue() {
        final Currency currency = Currency.getInstance(GBP);

        testObj.createAmount(currency, AMOUNT);

        verify(commonI18NServiceMock).convertAndRoundCurrency(1, Math.pow(10, currency.getDefaultFractionDigits()), 0, AMOUNT);
    }

    @Test
    public void shouldFormatTotal() {
        when(currencyModelMock.getIsocode()).thenReturn(GBP);

        testObj.createAmount(currencyModelMock, AMOUNT);

        verify(commonI18NServiceMock).convertAndRoundCurrency(1, Math.pow(10, 2), 0, AMOUNT);
    }

    @Test
    public void shouldCreateSession() {
        when(worldpayAdditionalInfoDataMock.getSessionId()).thenReturn(SESSION_ID);
        when(worldpayAdditionalInfoDataMock.getCustomerIPAddress()).thenReturn(IP_ADDRESS);

        final Session result = testObj.createSession(worldpayAdditionalInfoDataMock);

        assertEquals(SESSION_ID, result.getId());
        assertEquals(IP_ADDRESS, result.getShopperIPAddress());
    }

    @Test
    public void shouldCreateBrowser() {
        when(worldpayAdditionalInfoDataMock.getDeviceType()).thenReturn(DEVICE_TYPE);
        when(worldpayAdditionalInfoDataMock.getAcceptHeader()).thenReturn(ACCEPT_HEADER);
        when(worldpayAdditionalInfoDataMock.getUserAgentHeader()).thenReturn(USER_AGENT_HEADER);

        final Browser result = testObj.createBrowser(worldpayAdditionalInfoDataMock);

        assertEquals(DEVICE_TYPE, result.getDeviceType());
        assertEquals(ACCEPT_HEADER, result.getAcceptHeader());
        assertEquals(USER_AGENT_HEADER, result.getUserAgentHeader());
    }

    @Test
    public void shouldCreateBasicOrderInfo() {
        final BasicOrderInfo result = testObj.createBasicOrderInfo(WORLDPAY_ORDER_CODE, ORDER_DESCRIPTION, amountMock);

        assertEquals(amountMock, result.getAmount());
        assertEquals(ORDER_DESCRIPTION, result.getDescription());
        assertEquals(WORLDPAY_ORDER_CODE, result.getOrderCode());
    }

    @Test
    public void shouldCreateShopperWithoutAuthenticatedShopperID() {
        final Shopper result = testObj.createShopper(CUSTOMER_EMAIL, sessionMock, browserMock);
        assertEquals(CUSTOMER_EMAIL, result.getShopperEmailAddress());
        assertEquals(sessionMock, result.getSession());
        assertEquals(browserMock, result.getBrowser());
        assertNull(result.getAuthenticatedShopperID());
    }

    @Test
    public void shouldCreateShopperWithAuthenticatedShopperID() {
        final Shopper result = testObj.createAuthenticatedShopper(CUSTOMER_EMAIL, AUTHENTICATED_SHOPPER_ID, sessionMock, browserMock);
        assertEquals(CUSTOMER_EMAIL, result.getShopperEmailAddress());
        assertEquals(sessionMock, result.getSession());
        assertEquals(browserMock, result.getBrowser());
        assertEquals(AUTHENTICATED_SHOPPER_ID, result.getAuthenticatedShopperID());
    }

    @Test
    public void shouldCreateTokenRequest() {
        final TokenRequest result = testObj.createTokenRequest(TOKEN_EVENT_REFERENCE, TOKEN_REASON);
        assertEquals(TOKEN_EVENT_REFERENCE, result.getTokenEventReference());
        assertEquals(TOKEN_REASON, result.getTokenReason());
    }

    @Test
    public void shouldCreateTokenRequestWithoutTokenReason() {
        final TokenRequest result = testObj.createTokenRequest(TOKEN_EVENT_REFERENCE, null);
        assertEquals(TOKEN_EVENT_REFERENCE, result.getTokenEventReference());
        assertEquals(null, result.getTokenReason());
    }

    @Test
    public void shouldCreatePaymentForIdealSSL() throws WorldpayConfigurationException {
        when(worldpayUrlService.getFullSuccessURL()).thenReturn(FULL_SUCCESS_URL);

        final AlternativeShopperBankCodePayment result = (AlternativeShopperBankCodePayment) testObj.createPayment("IDEAL-SSL", BANK_CODE, null);

        assertEquals(BANK_CODE, result.getShopperBankCode());
        assertEquals(PaymentType.IDEAL, result.getPaymentType());
        assertEquals(FULL_SUCCESS_URL, result.getSuccessURL());
    }

    @Test
    public void shouldReturnNullWhenPaymentTypeIsNotFound() throws WorldpayConfigurationException {
        when(worldpayUrlService.getFullSuccessURL()).thenReturn(FULL_SUCCESS_URL);

        final AlternativeShopperBankCodePayment result = (AlternativeShopperBankCodePayment) testObj.createPayment("notfound", BANK_CODE, null);

        assertNull(result);
    }
}