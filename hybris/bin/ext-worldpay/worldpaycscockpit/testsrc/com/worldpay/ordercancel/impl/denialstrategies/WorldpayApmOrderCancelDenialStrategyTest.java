package com.worldpay.ordercancel.impl.denialstrategies;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.ordercancel.DefaultOrderCancelDenialReason;
import de.hybris.platform.ordercancel.OrderCancelDenialReason;
import de.hybris.platform.ordercancel.model.OrderCancelConfigModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayApmOrderCancelDenialStrategyTest {

    private static final String PAYMENT_TYPE = "paymentType";
    private static final String ORDER_CODE = "orderCode";

    @Spy
    @InjectMocks
    private WorldpayApmOrderCancelDenialStrategy testObj = new WorldpayApmOrderCancelDenialStrategy();
    @Mock
    private PrincipalModel principalModelMock;
    @Mock
    private OrderCancelConfigModel orderCancelConfigModelMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private PaymentTransactionModel paymentTransaction1Mock;
    @Mock
    private PaymentTransactionEntryModel capturedTransactionEntryMock;
    @Mock
    private PaymentTransactionEntryModel authTransactionEntryMock;
    @Mock
    private DefaultOrderCancelDenialReason denialReasonMock;
    @Mock
    private PaymentInfoModel paymentInfoMock;

    @Before
    public void setUp() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(paymentTransaction1Mock));
        when(paymentTransaction1Mock.getEntries()).thenReturn(Arrays.asList(authTransactionEntryMock, capturedTransactionEntryMock));
        when(authTransactionEntryMock.getType()).thenReturn(AUTHORIZATION);
        when(capturedTransactionEntryMock.getType()).thenReturn(CAPTURE);
        when(orderModelMock.getCode()).thenReturn(ORDER_CODE);
        doReturn(denialReasonMock).when(testObj).getReason();
    }

    @Test
    public void shouldReturnNullWhenPaymentIsNotAPM() throws Exception {
        when(paymentTransaction1Mock.getEntries()).thenReturn(singletonList(authTransactionEntryMock));
        when(paymentTransaction1Mock.getInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getIsApm()).thenReturn(false);
        when(paymentInfoMock.getPaymentType()).thenReturn(PAYMENT_TYPE);

        final OrderCancelDenialReason result = testObj.getCancelDenialReason(orderCancelConfigModelMock, orderModelMock, principalModelMock, false, false);

        verify(orderModelMock).getPaymentTransactions();
        verify(paymentTransaction1Mock).getInfo();
        assertNull(result);
    }

    @Test
    public void shouldReturnDenialReasonWhenPaymentInfoIsNull() {
        when(paymentTransaction1Mock.getInfo()).thenReturn(null);

        final OrderCancelDenialReason result = testObj.getCancelDenialReason(orderCancelConfigModelMock, orderModelMock, principalModelMock, false, false);

        assertNotNull(result);
        assertEquals(denialReasonMock, result);
    }

    @Test
    public void shouldReturnNullWhenPaymentTransactionsAreNull() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(null);

        final OrderCancelDenialReason result = testObj.getCancelDenialReason(orderCancelConfigModelMock, orderModelMock, principalModelMock, false, false);

        assertNull(result);
    }

    @Test
    public void shouldReturnDenialReasonWhenPaymentMethodIsUnknown() {
        when(paymentTransaction1Mock.getInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getPaymentType()).thenReturn(null);

        final OrderCancelDenialReason result = testObj.getCancelDenialReason(orderCancelConfigModelMock, orderModelMock, principalModelMock, false, false);

        verify(paymentInfoMock).getPaymentType();
        assertNotNull(result);
        assertEquals(denialReasonMock, result);
    }


    @Test
    public void shouldReturnDenialReasonWhenPaymentIsAPMAndTransactionIsAuthorised() throws Exception {
        when(paymentTransaction1Mock.getEntries()).thenReturn(singletonList(authTransactionEntryMock));
        when(paymentTransaction1Mock.getInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getIsApm()).thenReturn(true);

        final OrderCancelDenialReason result = testObj.getCancelDenialReason(orderCancelConfigModelMock, orderModelMock, principalModelMock, false, false);

        verify(orderModelMock).getPaymentTransactions();
        verify(paymentTransaction1Mock).getInfo();
        assertNotNull(result);
        assertEquals(denialReasonMock, result);
    }

    @Test
    public void shouldReturnDenialReasonWhenOneOfMultipleTransactionsIsApm() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(Arrays.asList(paymentTransaction1Mock, paymentTransaction1Mock));
        when(paymentTransaction1Mock.getInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getIsApm()).thenReturn(false).thenReturn(true);
        when(paymentInfoMock.getPaymentType()).thenReturn(PAYMENT_TYPE);

        final OrderCancelDenialReason result = testObj.getCancelDenialReason(orderCancelConfigModelMock, orderModelMock, principalModelMock, false, false);

        verify(orderModelMock).getPaymentTransactions();
        // Loops through the transactions in the order.
        verify(paymentTransaction1Mock, times(2)).getInfo();
        verify(paymentInfoMock, times(2)).getIsApm();

        assertNotNull(result);
        assertEquals(denialReasonMock, result);
    }
}