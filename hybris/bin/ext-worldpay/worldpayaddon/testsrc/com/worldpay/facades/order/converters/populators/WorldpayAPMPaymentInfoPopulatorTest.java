package com.worldpay.facades.order.converters.populators;

import com.worldpay.facades.order.data.WorldpayAPMPaymentInfoData;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayAPMPaymentInfoPopulatorTest {

    public static final String APM_NAME = "apmName";
    @InjectMocks
    private WorldpayAPMPaymentInfoPopulator testObj = new WorldpayAPMPaymentInfoPopulator();

    @Mock
    private AbstractOrderModel abstractOrderModelMock;
    @Mock
    private AbstractOrderData abstractOrderDataMock;
    @Mock
    private WorldpayAPMPaymentInfoData worldpayAPMPaymentInfoDataMock;
    @Mock
    private WorldpayAPMPaymentInfoModel worldpayAPMPaymentInfoModelMock;
    @Mock
    private WorldpayAPMConfigurationModel worldpayAPMConfigurationModelMock;

    @Test
    public void populateShouldNotSetApmNameIfNoApm() {
        when(abstractOrderModelMock.getPaymentInfo()).thenReturn(worldpayAPMPaymentInfoModelMock);
        when(worldpayAPMPaymentInfoModelMock.getApmConfiguration()).thenReturn(null);

        testObj.populate(abstractOrderModelMock, abstractOrderDataMock);

        verify(worldpayAPMPaymentInfoModelMock).getApmConfiguration();
        verify(worldpayAPMPaymentInfoDataMock, never()).setName(anyString());
    }

    @Test
    public void populateShouldSetApmNameIfApmIsPresent() {
        when(abstractOrderModelMock.getPaymentInfo()).thenReturn(worldpayAPMPaymentInfoModelMock);
        when(worldpayAPMPaymentInfoModelMock.getApmConfiguration()).thenReturn(worldpayAPMConfigurationModelMock);
        when(worldpayAPMConfigurationModelMock.getName()).thenReturn(APM_NAME);

        testObj.populate(abstractOrderModelMock, abstractOrderDataMock);

        verify(worldpayAPMPaymentInfoModelMock).getApmConfiguration();
        verify(abstractOrderDataMock).setWorldpayAPMPaymentInfo(any(WorldpayAPMPaymentInfoData.class));
    }
}