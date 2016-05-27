package com.worldpay.facades.payment.merchant.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static de.hybris.platform.commerceservices.enums.UiExperienceLevel.MOBILE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayMerchantConfigDataFacadeTest {

    @InjectMocks
    private DefaultWorldpayMerchantConfigDataFacade testObj = new DefaultWorldpayMerchantConfigDataFacade();

    @Mock
    private WorldpayMerchantConfigData mobileMerchantConfigDataMock, customerServiceMerchantConfigDataMock;
    @Mock
    private WorldpayMerchantStrategy worldpayMerchantStrategyMock;

    @Before
    public void setup() {
        when(worldpayMerchantStrategyMock.getMerchant(MOBILE)).thenReturn(mobileMerchantConfigDataMock);
        when(worldpayMerchantStrategyMock.getCustomerServiceMerchant()).thenReturn(customerServiceMerchantConfigDataMock);
    }

    @Test
    public void shouldReturnWebSiteMerchantConfigData() throws Exception {

        final WorldpayMerchantConfigData result = testObj.getCurrentSiteMerchantConfigData(MOBILE);

        assertEquals(mobileMerchantConfigDataMock, result);
    }
    @Test
    public void shouldReturnCustomerServiceMerchantConfigData() throws Exception {

        final WorldpayMerchantConfigData result = testObj.getCustomerServiceMerchantConfigData();

        assertEquals(customerServiceMerchantConfigDataMock, result);
    }
}