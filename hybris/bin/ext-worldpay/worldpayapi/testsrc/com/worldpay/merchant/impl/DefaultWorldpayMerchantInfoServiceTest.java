package com.worldpay.merchant.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.merchant.WorldpayMerchantConfigDataService;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Map;

import static de.hybris.platform.commerceservices.enums.UiExperienceLevel.DESKTOP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayMerchantInfoServiceTest {

    private static final String KNOWN_MERCHANT_CODE = "knownMerchantCode";
    private static final String MERCHANT_CODE = "merchantCode";
    private static final String MAC_SECRET = "macSecret";
    private static final String MERCHANT_PASSWORD = "password";
    private static final String SITE_UID = "siteUid";

    @Spy
    @InjectMocks
    private DefaultWorldpayMerchantInfoService testObj = new DefaultWorldpayMerchantInfoService();
    @Mock
    private Map<String, WorldpayMerchantConfigData> worldpayMerchantConfigurationMock;
    @Mock
    private WorldpayMerchantConfigData worldpayMerchantConfigDataMock;
    @Mock
    private WorldpayMerchantConfigDataService worldpayMerchantConfigDataServiceMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private PaymentTransactionModel paymentTransactionMock;
    @Mock
    private SessionService sessionServiceMock;
    @Mock
    private WorldpayMerchantStrategy worldpayMerchantStrategyMock;
    @Mock
    private WorldpayMerchantConfigData customerServiceMerchantConfigDataMock, webSiteMerchantConfigDataMock;
    @Mock
    private MerchantInfo customerServiceMerchantInfo, webSiteMerchantInfo;

    @Before
    public void setUp() {
        when(worldpayMerchantConfigurationMock.get(anyString())).thenReturn(worldpayMerchantConfigDataMock);
        when(worldpayMerchantConfigurationMock.values()).thenReturn(Collections.singletonList(worldpayMerchantConfigDataMock));
        when(worldpayMerchantConfigDataServiceMock.getMerchantConfiguration()).thenReturn(worldpayMerchantConfigurationMock);
    }

    @Test
    public void shouldCreateMerchantInfoFromCustomerServiceMerchantConfigData() throws WorldpayConfigurationException {
        when(worldpayMerchantStrategyMock.getCustomerServiceMerchant()).thenReturn(customerServiceMerchantConfigDataMock);
        doReturn(customerServiceMerchantInfo).when(testObj).createMerchantInfo(customerServiceMerchantConfigDataMock);

        final MerchantInfo result = testObj.getCustomerServicesMerchant();

        assertEquals(customerServiceMerchantInfo, result);
    }

    @Test
    public void shouldCreateMerchantInfoFromWebSiteMerchantConfigData() throws WorldpayConfigurationException {
        when(worldpayMerchantStrategyMock.getMerchant(DESKTOP)).thenReturn(webSiteMerchantConfigDataMock);
        doReturn(webSiteMerchantInfo).when(testObj).createMerchantInfo(webSiteMerchantConfigDataMock);

        final MerchantInfo result = testObj.getCurrentSiteMerchant(DESKTOP);

        assertEquals(webSiteMerchantInfo, result);
    }

    @Test
    public void getMerchantConfigShouldReturnAlreadyKnownMerchantCode() throws WorldpayConfigurationException {
        when(worldpayMerchantConfigDataMock.getCode()).thenReturn(KNOWN_MERCHANT_CODE);

        final MerchantInfo result = testObj.getMerchantInfoByCode(KNOWN_MERCHANT_CODE);

        // Comparison in the stream and creation of the MerchantInfo object
        verify(worldpayMerchantConfigDataMock, times(2)).getCode();
        assertEquals(KNOWN_MERCHANT_CODE, result.getMerchantCode());
        verify(worldpayMerchantConfigDataServiceMock).getMerchantConfiguration();
    }

    @Test (expected = IllegalArgumentException.class)
    public void getKnownMerchantShouldReturnExceptionWhenMerchantIsNotFound() throws WorldpayConfigurationException {
        testObj.getMerchantInfoByCode(KNOWN_MERCHANT_CODE);
        verify(worldpayMerchantConfigDataServiceMock).getMerchantConfiguration();
    }

    @Test
    public void testGetMerchantInfoFromTransaction() throws Exception {
        when(paymentTransactionMock.getOrder().getSite().getUid()).thenReturn(SITE_UID);
        when(paymentTransactionMock.getRequestToken()).thenReturn(MERCHANT_CODE);

        testObj.getMerchantInfoFromTransaction(paymentTransactionMock);

        verify(sessionServiceMock).executeInLocalView(any(SessionExecutionBody.class));
    }

    @Test
    public void shouldCreateMerchantInfoUsingMacValidation() throws WorldpayConfigurationException {
        when(webSiteMerchantConfigDataMock.getCode()).thenReturn(MERCHANT_CODE);
        when(webSiteMerchantConfigDataMock.getPassword()).thenReturn(MERCHANT_PASSWORD);
        when(webSiteMerchantConfigDataMock.getMacSecret()).thenReturn(MAC_SECRET);
        when(webSiteMerchantConfigDataMock.getMacValidation()).thenReturn(true);

        final MerchantInfo result = testObj.createMerchantInfo(webSiteMerchantConfigDataMock);

        assertEquals(MAC_SECRET, result.getMacSecret());
        assertEquals(MERCHANT_CODE, result.getMerchantCode());
        assertEquals(MERCHANT_PASSWORD, result.getMerchantPassword());
        assertEquals(true, result.isUsingMacValidation());
    }

    @Test
    public void shouldCreateMerchantInfoNotUsingMacValidation() throws WorldpayConfigurationException {
        when(webSiteMerchantConfigDataMock.getCode()).thenReturn(MERCHANT_CODE);
        when(webSiteMerchantConfigDataMock.getPassword()).thenReturn(MERCHANT_PASSWORD);
        when(webSiteMerchantConfigDataMock.getMacSecret()).thenReturn(MAC_SECRET);
        when(webSiteMerchantConfigDataMock.getMacValidation()).thenReturn(false);

        final MerchantInfo result = testObj.createMerchantInfo(webSiteMerchantConfigDataMock);

        assertEquals(MERCHANT_CODE, result.getMerchantCode());
        assertEquals(MERCHANT_PASSWORD, result.getMerchantPassword());
        assertNull(result.getMacSecret());
        assertEquals(false, result.isUsingMacValidation());
    }
}
