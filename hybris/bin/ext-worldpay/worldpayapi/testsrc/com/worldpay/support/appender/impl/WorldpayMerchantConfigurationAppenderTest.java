package com.worldpay.support.appender.impl;

import com.google.common.collect.ImmutableMap;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import de.hybris.bootstrap.annotations.UnitTest;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayMerchantConfigurationAppenderTest {

    private static final Boolean MAC_VALIDATION = Boolean.TRUE;
    private static final String MERCHANT_CODE = "merchantCode";
    private static final String INSTALLATION_ID = "installationId";
    private static final String PAYMENT_METHOD_1 = "paymentMethod1";
    private static final String PAYMENT_METHOD_2 = "paymentMethod2";
    private static final String PAYMENT_METHOD_3 = "paymentMethod3";
    private static final String PAYMENT_METHOD_4 = "paymentMethod4";
    private static final String WEB_BEAN_ID = "webBeanId";
    private static final String MOBILE_BEAN_ID = "mobileBeanId";

    @Spy
    @InjectMocks
    private WorldpayMerchantConfigurationAppender testObj = new WorldpayMerchantConfigurationAppender();

    @Mock
    private WorldpayMerchantConfigData webWorldpayMerchantConfigDataMock, mobileWorldpayMerchantConfigDataMock;

    @Test
    public void testAppendContent() throws Exception {
        final ImmutableMap<String, WorldpayMerchantConfigData> configuredMerchants = ImmutableMap.of(WEB_BEAN_ID, webWorldpayMerchantConfigDataMock, MOBILE_BEAN_ID, mobileWorldpayMerchantConfigDataMock);
        doReturn(configuredMerchants).when(testObj).getConfiguredMerchants();

        final List<String> includedPaymentMethods = asList(PAYMENT_METHOD_1, PAYMENT_METHOD_2);
        final List<String> excludedPaymentMethods = asList(PAYMENT_METHOD_3, PAYMENT_METHOD_4);

        when(webWorldpayMerchantConfigDataMock.getIncludedPaymentTypes()).thenReturn(includedPaymentMethods);
        when(webWorldpayMerchantConfigDataMock.getExcludedPaymentTypes()).thenReturn(excludedPaymentMethods);
        when(webWorldpayMerchantConfigDataMock.getMacValidation()).thenReturn(MAC_VALIDATION);
        when(webWorldpayMerchantConfigDataMock.getCode()).thenReturn(MERCHANT_CODE);
        when(webWorldpayMerchantConfigDataMock.getInstallationId()).thenReturn(INSTALLATION_ID);

        when(mobileWorldpayMerchantConfigDataMock.getIncludedPaymentTypes()).thenReturn(includedPaymentMethods);
        when(mobileWorldpayMerchantConfigDataMock.getExcludedPaymentTypes()).thenReturn(excludedPaymentMethods);
        when(mobileWorldpayMerchantConfigDataMock.getMacValidation()).thenReturn(MAC_VALIDATION);
        when(mobileWorldpayMerchantConfigDataMock.getCode()).thenReturn(MERCHANT_CODE);
        when(mobileWorldpayMerchantConfigDataMock.getInstallationId()).thenReturn(INSTALLATION_ID);

        final String result = testObj.appendContent();

        assertTrue(result.contains("Merchant Configuration"));
        assertTrue(result.contains(WEB_BEAN_ID));
        assertTrue(result.contains(MOBILE_BEAN_ID));

        assertEquals(2, StringUtils.countMatches(result, MERCHANT_CODE));
        assertEquals(2, StringUtils.countMatches(result, INSTALLATION_ID));
        assertEquals(2, StringUtils.countMatches(result, String.valueOf(MAC_VALIDATION)));

        assertTrue(result.contains("Included payment methods"));
        assertEquals(2, StringUtils.countMatches(result, String.valueOf(PAYMENT_METHOD_1)));
        assertEquals(2, StringUtils.countMatches(result, String.valueOf(PAYMENT_METHOD_2)));

        assertTrue(result.contains("Excluded payment methods"));
        assertEquals(2, StringUtils.countMatches(result, String.valueOf(PAYMENT_METHOD_3)));
        assertEquals(2, StringUtils.countMatches(result, String.valueOf(PAYMENT_METHOD_4)));
    }
}
    

