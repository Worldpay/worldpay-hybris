package com.worldpay.cscockpit.configuration;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.services.values.ValueHandlerException;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PaymentInfoPaymentTypeColumnTest {

    private static final String APM_PAYMENT_METHOD = "apmPaymentMethod";
    private static final String APM_PAYMENT_TYPE_CODE = "apmPaymentTypeCode";
    private static final String NON_APM_PAYMENT_TYPE_CODE = "nonApmPaymentTypeCode";

    @InjectMocks
    private PaymentInfoPaymentTypeColumn testObj = new PaymentInfoPaymentTypeColumn();

    @Mock
    private APMConfigurationLookupService apmConfigurationLookupServiceMock;

    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private WorldpayAPMPaymentInfoModel apmPaymentInfoModelMock;
    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoModel;
    @Mock
    private WorldpayAPMConfigurationModel worldpayAPMConfigurationModelMock;

    @Test
    public void getItemValueReturnsPaymentTypeNameFromCodeForAPM() throws ValueHandlerException {
        when(paymentTransactionEntryModelMock.getPaymentTransaction().getInfo()).thenReturn(apmPaymentInfoModelMock);
        when(apmPaymentInfoModelMock.getPaymentType()).thenReturn(APM_PAYMENT_TYPE_CODE);
        when(apmConfigurationLookupServiceMock.getAPMConfigurationForCode(APM_PAYMENT_TYPE_CODE)).thenReturn(worldpayAPMConfigurationModelMock);
        when(worldpayAPMConfigurationModelMock.getName()).thenReturn(APM_PAYMENT_METHOD);

        final String result = testObj.getItemValue(paymentTransactionEntryModelMock, Locale.UK);

        assertEquals(APM_PAYMENT_METHOD, result);
    }

    @Test
    public void getItemValueReturnsPaymentTypeCodeIfPaymentMethodIsNotAPM() throws ValueHandlerException {
        when(paymentTransactionEntryModelMock.getPaymentTransaction().getInfo()).thenReturn(creditCardPaymentInfoModel);
        when(creditCardPaymentInfoModel.getPaymentType()).thenReturn(NON_APM_PAYMENT_TYPE_CODE);

        final String result = testObj.getItemValue(paymentTransactionEntryModelMock, Locale.UK);

        assertEquals(NON_APM_PAYMENT_TYPE_CODE, result);
        verify(apmConfigurationLookupServiceMock, never()).getAPMConfigurationForCode(anyString());
    }

    @Test
    public void getItemValueReturnsUnknownIfPaymentMethodNull() throws ValueHandlerException {
        when(paymentTransactionEntryModelMock.getPaymentTransaction().getInfo()).thenReturn(null);
        when(creditCardPaymentInfoModel.getPaymentType()).thenReturn(NON_APM_PAYMENT_TYPE_CODE);

        final String result = testObj.getItemValue(paymentTransactionEntryModelMock, Locale.UK);

        assertEquals(PaymentInfoPaymentTypeColumn.UNKNOWN, result);
        verify(apmConfigurationLookupServiceMock, never()).getAPMConfigurationForCode(anyString());
    }

}