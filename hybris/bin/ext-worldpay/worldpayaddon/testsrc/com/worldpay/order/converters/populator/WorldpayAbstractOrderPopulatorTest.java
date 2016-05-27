package com.worldpay.order.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayAbstractOrderPopulatorTest {

    public static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    public static final int WORLDPAY_DECLINE_CODE = 19;

    @InjectMocks
    private WorldpayAbstractOrderPopulator testObj = new WorldpayAbstractOrderPopulator();

    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoMock;
    @Mock (name = "addressConverter")
    private Converter<AddressModel, AddressData> addressConverterMock;
    @Mock
    private AbstractOrderModel abstractOrderModelMock;
    @Mock
    private PaymentInfoModel paymentInfoMock;
    @Mock
    private AbstractOrderData abstractOrderDataMock;

    @Test
    public void populate() {
        when(abstractOrderModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(abstractOrderModelMock.getWorldpayDeclineCode()).thenReturn(WORLDPAY_DECLINE_CODE);

        testObj.populate(abstractOrderModelMock, abstractOrderDataMock);

        verify(abstractOrderDataMock).setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(abstractOrderDataMock).setWorldpayDeclineCode(WORLDPAY_DECLINE_CODE);
    }

    @Test
    public void populateShouldNotSetTargetWorldpayPaymentInfoWhenSourcePaymentInfoIsNull() throws Exception {
        when(abstractOrderModelMock.getPaymentInfo()).thenReturn(null);

        testObj.populate(abstractOrderModelMock, abstractOrderDataMock);

        verify(abstractOrderDataMock, never()).setPaymentInfo(any(CCPaymentInfoData.class));
    }

    @Test
    public void populateShouldSetTargetWorldpayPaymentInfoWhenSourcePaymentInfoIsNotCreditCardPaymentInfo() throws Exception {
        when(abstractOrderModelMock.getPaymentInfo()).thenReturn(paymentInfoMock);

        testObj.populate(abstractOrderModelMock, abstractOrderDataMock);

        verify(addressConverterMock).convert(any(AddressModel.class));
        verify(abstractOrderDataMock).setPaymentInfo(any(CCPaymentInfoData.class));
    }

    @Test
    public void populateShouldSetTargetWorldpayPaymentInfoWhenSourcePaymentInfoIsCreditCardPaymentInfo() throws Exception {
        when(abstractOrderModelMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoMock);

        testObj.populate(abstractOrderModelMock, abstractOrderDataMock);

        verify(abstractOrderDataMock, never()).setPaymentInfo(any(CCPaymentInfoData.class));
    }
}