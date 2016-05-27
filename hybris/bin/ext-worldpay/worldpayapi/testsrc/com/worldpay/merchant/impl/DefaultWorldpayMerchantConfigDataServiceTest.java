package com.worldpay.merchant.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static com.worldpay.merchant.impl.DefaultWorldpayMerchantConfigDataService.WORLDPAY_MERCHANT_CONFIG;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayMerchantConfigDataServiceTest {

    private static final String MERCHANT_CONFIG_BEAN_NAME = "merchantConfigBeanName";

    @InjectMocks
    private DefaultWorldpayMerchantConfigDataService testObj = new DefaultWorldpayMerchantConfigDataService();

    @Mock
    private SiteConfigService siteConfigServiceMock;
    @Mock
    private ApplicationContext applicationContextMock;
    @Mock
    private Map<String, WorldpayMerchantConfigData> worldpayMerchantConfigurationMock;

    @Before
    public void setUp() {
        when(siteConfigServiceMock.getProperty(WORLDPAY_MERCHANT_CONFIG)).thenReturn(MERCHANT_CONFIG_BEAN_NAME);
        when(applicationContextMock.getBean(MERCHANT_CONFIG_BEAN_NAME)).thenReturn(worldpayMerchantConfigurationMock);
    }

    @Test
    public void testGetMerchantConfigurationWhenNotInAThreadContext() throws Exception {

        testObj.getMerchantConfiguration();

        verify(siteConfigServiceMock).getProperty(WORLDPAY_MERCHANT_CONFIG);
        verify(applicationContextMock).getBean(MERCHANT_CONFIG_BEAN_NAME);
    }
}