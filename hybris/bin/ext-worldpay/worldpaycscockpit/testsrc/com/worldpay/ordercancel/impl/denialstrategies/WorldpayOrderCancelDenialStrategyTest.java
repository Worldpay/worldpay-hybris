package com.worldpay.ordercancel.impl.denialstrategies;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
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
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayOrderCancelDenialStrategyTest {

    @Spy
    @InjectMocks
    private WorldpayOrderCancelDenialStrategy testObj = new WorldpayOrderCancelDenialStrategy();

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

    @Before
    public void setUp() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(paymentTransaction1Mock));
        when(paymentTransaction1Mock.getEntries()).thenReturn(Arrays.asList(authTransactionEntryMock, capturedTransactionEntryMock));
        when(authTransactionEntryMock.getType()).thenReturn(AUTHORIZATION);
        when(capturedTransactionEntryMock.getType()).thenReturn(CAPTURE);
        doReturn(denialReasonMock).when(testObj).getReason();
    }

    @Test
    public void shouldReturnNullWhenCaptureTransactionEntryIsPending() throws Exception {
        when(authTransactionEntryMock.getPending()).thenReturn(false);
        when(capturedTransactionEntryMock.getPending()).thenReturn(true);

        final OrderCancelDenialReason result = testObj.getCancelDenialReason(orderCancelConfigModelMock, orderModelMock, principalModelMock, false, false);

        verify(orderModelMock).getPaymentTransactions();
        verify(paymentTransaction1Mock).getEntries();
        assertNull(result);
    }

    @Test
    public void shouldReturnNullWhenPaymentTransactionsAreNull() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(null);

        final OrderCancelDenialReason result = testObj.getCancelDenialReason(orderCancelConfigModelMock, orderModelMock, principalModelMock, false, false);

        assertNull(result);
    }

    @Test
    public void shouldReturnDenialReasonWhenCaptureTransactionEntryIsNotPending() throws Exception {
        when(authTransactionEntryMock.getPending()).thenReturn(false);
        when(capturedTransactionEntryMock.getPending()).thenReturn(false);

        final OrderCancelDenialReason result = testObj.getCancelDenialReason(orderCancelConfigModelMock, orderModelMock, principalModelMock, false, false);

        verify(orderModelMock).getPaymentTransactions();
        verify(paymentTransaction1Mock).getEntries();
        assertNotNull(result);
        assertEquals(denialReasonMock, result);
    }

    @Test
    public void shouldReturnDenialReasonWhenOneCaptureTransactionEntryIsNotPending() throws Exception {
        when(orderModelMock.getPaymentTransactions()).thenReturn(Arrays.asList(paymentTransaction1Mock, paymentTransaction1Mock));

        when(authTransactionEntryMock.getPending()).thenReturn(false);
        when(capturedTransactionEntryMock.getPending()).thenReturn(true).thenReturn(false);

        final OrderCancelDenialReason result = testObj.getCancelDenialReason(orderCancelConfigModelMock, orderModelMock, principalModelMock, false, false);

        verify(orderModelMock).getPaymentTransactions();
        // Loops through the transactions in the order.
        verify(paymentTransaction1Mock, times(2)).getEntries();
        assertNotNull(result);
        assertEquals(denialReasonMock, result);
    }
}