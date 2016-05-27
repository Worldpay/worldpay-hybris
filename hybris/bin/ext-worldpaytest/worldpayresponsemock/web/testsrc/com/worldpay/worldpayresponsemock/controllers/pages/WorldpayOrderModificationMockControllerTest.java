package com.worldpay.worldpayresponsemock.controllers.pages;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.exception.WorldpayException;
import com.worldpay.worldpayresponsemock.form.ResponseForm;
import com.worldpay.worldpayresponsemock.merchant.WorldpayResponseMockMerchantInfoService;
import com.worldpay.worldpayresponsemock.mock.WorldpayMockConnector;
import com.worldpay.worldpayresponsemock.responses.WorldpayNotificationResponseBuilder;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.worldpay.worldpayresponsemock.controllers.WorldpayResponseMockControllerConstants.Pages.Views.RESPONSES;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.AVAILABLE_SITES;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.DEFAULT_CARD_HOLDER_NAME;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.DEFAULT_CARD_MONTH;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.DEFAULT_CARD_YEAR;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.DEFAULT_CURRENCY_CODE;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.DEFAULT_MERCHANT_CODE;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.DEFAULT_ORDER_CODE;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.DEFAULT_RISK_SCORE;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.DEFAULT_TRANSACTION_AMOUNT;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.FINAL_SCORE;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.MERCHANTS;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.PAYMENT_METHODS;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.PAYMENT_METHOD_APMS;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.POSSIBLE_EVENTS;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.RESPONSE_CODES;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.TEST_CREDIT_CARDS;
import static com.worldpay.worldpayresponsemock.controllers.pages.WorldpayOrderModificationMockController.XML_RESPONSE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayOrderModificationMockControllerTest {

    private static final Integer RESPONSE_CODE = 10;
    private static final String RESPONSE_DESCRIPTION = "responseDescription";
    private static final String MASKED_CREDIT_CARD_1 = "444433******1111";
    private static final String MASKED_CREDIT_CARD_2 = "630495********0000";
    private static final String SOME_RESPONSE = "some_response";
    private static final String HTTP_SCHEME = "http";
    private static final int SERVER_PORT = 80;
    private static final String SERVER_NAME = "serverName";
    private static final String SITEID = "siteid";
    private static final String CREDIT_CARD_NUMBER = "4444333322221111";
    private static final String CREDIT_CARD_NUMBER_2 = "630495060000000000";
    private static final String DEFAULT_AAV_RESULT = "B";

    private static final String SITE_1 = "site1";
    private static final String SITE_2 = "site2";
    private static final String PRETTY_XML = "prettyXML";
    private static final String MERCHANT_1 = "merchant1";
    private static final String MERCHANT_2 = "merchant2";
    private static final String APM_1 = "apm1";
    private static final String APM_2 = "apm2";
    private static final String RESPONSE_FORM = "responseForm";

    @InjectMocks
    private WorldpayOrderModificationMockController testObj = new WorldpayOrderModificationMockController();

    @Mock
    private ResponseForm responseFormMock;
    @Mock
    private ModelMap modelMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock (name = "ISO8583ResponseCodes")
    private Map<Integer, String> isoResponseCodesMock;
    @Mock
    private WorldpayNotificationResponseBuilder worldpayResponseBuilderMock;
    @Mock
    private APMConfigurationLookupService apmConfigurationLookupServiceMock;
    @Mock
    private BaseSiteService baseSiteServiceMock;
    @Mock
    private BaseSiteModel site1Mock;
    @Mock
    private BaseSiteModel site2Mock;
    @Mock
    private WorldpayResponseMockMerchantInfoService worldpayMerchantMockServiceMock;
    @Mock (name = "worldpayCreditCards")
    private Map<String, String> worldpayCreditCardsMock;
    @Mock (name = "worldpayPaymentMethods")
    private Map<String, String> worldpayPaymentMethodsMock;
    @Mock
    private Set<String> possibleEventsMock;
    @Mock
    private WorldpayMockConnector worldpayMockConnectorMock;

    private List<String> merchantList;

    @Before
    public void setup() throws WorldpayException {
        when(responseFormMock.getResponseCode()).thenReturn(RESPONSE_CODE);
        when(isoResponseCodesMock.get(RESPONSE_CODE)).thenReturn(RESPONSE_DESCRIPTION);
        when(responseFormMock.getTestCreditCard()).thenReturn(CREDIT_CARD_NUMBER);
        when(responseFormMock.getObfuscatedPAN()).thenReturn(CREDIT_CARD_NUMBER);
        when(worldpayResponseBuilderMock.buildResponse(responseFormMock)).thenReturn(SOME_RESPONSE);
        when(requestMock.getScheme()).thenReturn(HTTP_SCHEME);
        when(requestMock.getServerPort()).thenReturn(SERVER_PORT);
        when(requestMock.getServerName()).thenReturn(SERVER_NAME);
        when(responseFormMock.getSiteId()).thenReturn(SITEID);
        when(site1Mock.getUid()).thenReturn(SITE_1);
        when(site2Mock.getUid()).thenReturn(SITE_2);
        merchantList = Arrays.asList(MERCHANT_1, MERCHANT_2);
        when(worldpayMerchantMockServiceMock.getAllMerchantCodes(startsWith("site"))).thenReturn(merchantList);
        final List<BaseSiteModel> availableSites = Arrays.asList(site1Mock, site2Mock);
        when(baseSiteServiceMock.getAllBaseSites()).thenReturn(availableSites);
        when(apmConfigurationLookupServiceMock.getAllApmPaymentTypeCodes()).thenReturn(new HashSet<>(Arrays.asList(APM_1, APM_2)));
    }

    @Test
    public void testMaskCreditCardNumber() throws Exception {
        final String result = testObj.maskCreditCardNumber(CREDIT_CARD_NUMBER);
        assertEquals(MASKED_CREDIT_CARD_1, result);
    }

    @Test
    public void testMaskCreditCardNumberOtherlength() throws Exception {
        final String result = testObj.maskCreditCardNumber(CREDIT_CARD_NUMBER_2);
        assertEquals(MASKED_CREDIT_CARD_2, result);
    }

    @Test
    public void sendResponseShouldPostNotificationMessageToStorefront() throws Exception {
        when(worldpayResponseBuilderMock.prettifyXml(anyString())).thenReturn(PRETTY_XML);

        final String result = testObj.sendResponse(responseFormMock, modelMock, requestMock);

        assertEquals(RESPONSES, result);
        verify(worldpayMockConnectorMock).sendResponse(responseFormMock, requestMock, SOME_RESPONSE);
        verify(apmConfigurationLookupServiceMock).getAllApmPaymentTypeCodes();
        verify(responseFormMock).setResponseDescription(RESPONSE_DESCRIPTION);
        verify(modelMock).put(eq(PAYMENT_METHOD_APMS), anySetOf(String.class));
        verify(modelMock).put(XML_RESPONSE, PRETTY_XML);
        verify(modelMock).put(AVAILABLE_SITES, Arrays.asList(SITE_1, SITE_2));
        verify(modelMock).put(RESPONSE_CODES, isoResponseCodesMock);
        verify(modelMock).put(TEST_CREDIT_CARDS, worldpayCreditCardsMock);
        verify(modelMock).put(PAYMENT_METHODS, worldpayPaymentMethodsMock);
        verify(modelMock).put(POSSIBLE_EVENTS, possibleEventsMock);
        verify(modelMock).put(MERCHANTS, merchantList);
    }

    @Test
    public void getAllAnswersShouldPopulateModelWithDefaultSiteIdAndNotPost() {
        testObj.getAllAnswers(modelMock);

        verify(apmConfigurationLookupServiceMock).getAllApmPaymentTypeCodes();
        verify(modelMock).put(eq(PAYMENT_METHOD_APMS), anySetOf(String.class));
        verify(modelMock, never()).put(eq(XML_RESPONSE), anyString());
        verify(modelMock).put(AVAILABLE_SITES, Arrays.asList(SITE_1, SITE_2));
        verify(modelMock).put(RESPONSE_CODES, isoResponseCodesMock);
        verify(modelMock).put(TEST_CREDIT_CARDS, worldpayCreditCardsMock);
        verify(modelMock).put(PAYMENT_METHODS, worldpayPaymentMethodsMock);
        verify(modelMock).put(POSSIBLE_EVENTS, possibleEventsMock);
        verify(worldpayMerchantMockServiceMock).getAllMerchantCodes(SITE_1);
        verify(modelMock).put(MERCHANTS, merchantList);
    }

    @Test
    public void getAllAnswersShouldPopulateResponseForm() {
        final ModelAndView result = testObj.getAllAnswers(modelMock);
        assertNotNull(result);
        assertEquals(RESPONSES, result.getViewName());

        final ResponseForm responseForm = (ResponseForm) result.getModelMap().get(RESPONSE_FORM);
        assertEquals(DEFAULT_ORDER_CODE, responseForm.getWorldpayOrderCode());
        assertEquals(DEFAULT_MERCHANT_CODE, responseForm.getMerchantCode());
        assertEquals(DEFAULT_CARD_HOLDER_NAME, responseForm.getCardHolderName());
        assertEquals(DEFAULT_CARD_MONTH, responseForm.getCardMonth());
        assertEquals(DEFAULT_CARD_YEAR, responseForm.getCardYear());
        assertEquals(DEFAULT_CURRENCY_CODE, responseForm.getCurrencyCode());
        assertEquals(DEFAULT_RISK_SCORE, responseForm.getRiskValue());
        assertEquals(DEFAULT_TRANSACTION_AMOUNT, responseForm.getTransactionAmount());
        assertEquals(FINAL_SCORE, responseForm.getFinalScore());
        assertEquals(DEFAULT_AAV_RESULT, responseForm.getAavAddress());
        assertEquals(DEFAULT_AAV_RESULT, responseForm.getAavCardholderName());
        assertEquals(DEFAULT_AAV_RESULT, responseForm.getAavEmail());
        assertEquals(DEFAULT_AAV_RESULT, responseForm.getAavPostcode());
        assertEquals(DEFAULT_AAV_RESULT, responseForm.getAavTelephone());
    }

    @Test
    public void getAllMerchantsForSiteShouldReturnAListOfMerchants() {
        final List<String> merchantsBySite = testObj.getMerchantsBySite(SITE_1);

        verify(worldpayMerchantMockServiceMock).getAllMerchantCodes(SITE_1);
        assertTrue(merchantsBySite.contains(MERCHANT_1));
        assertTrue(merchantsBySite.contains(MERCHANT_2));
    }
}