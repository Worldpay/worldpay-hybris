package com.worldpay.widgets.controllers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayOrderManagementActionsWidgetControllerTest {

    @Spy
    private WorldpayOrderManagementActionsWidgetController testObj = new WorldpayOrderManagementActionsWidgetController();
    @Mock
    private TypedObject typedObjectOrderMock;
    @Mock
    private OrderModel orderMock;
    @Mock
    private PaymentTransactionModel paymentTransaction1Mock;
    @Mock
    private PaymentTransactionModel paymentTransaction2Mock;
    @Mock
    private PaymentTransactionModel paymentTransaction3Mock;
    @Mock
    private PaymentTransactionEntryModel authorisationPaymentTransactionEntryMock;
    @Mock
    private PaymentTransactionEntryModel pendingCapturePaymentTransactionEntryMock;
    @Mock
    private PaymentTransactionEntryModel nonPendingCapturePaymentTransactionEntryMock;

    @Before
    public void setup() {
        doReturn(typedObjectOrderMock).when(testObj).getOrder();

        when(typedObjectOrderMock.getObject()).thenReturn(orderMock);
        when(paymentTransaction1Mock.getEntries()).thenReturn(singletonList(authorisationPaymentTransactionEntryMock));
        when(paymentTransaction2Mock.getEntries()).thenReturn(asList(authorisationPaymentTransactionEntryMock, pendingCapturePaymentTransactionEntryMock));
        when(paymentTransaction3Mock.getEntries()).thenReturn(asList(authorisationPaymentTransactionEntryMock, nonPendingCapturePaymentTransactionEntryMock));
        when(authorisationPaymentTransactionEntryMock.getType()).thenReturn(AUTHORIZATION);
        when(authorisationPaymentTransactionEntryMock.getPending()).thenReturn(false);
        when(pendingCapturePaymentTransactionEntryMock.getType()).thenReturn(CAPTURE);
        when(pendingCapturePaymentTransactionEntryMock.getPending()).thenReturn(true);
        when(nonPendingCapturePaymentTransactionEntryMock.getType()).thenReturn(CAPTURE);
        when(nonPendingCapturePaymentTransactionEntryMock.getPending()).thenReturn(false);
    }

    @Test
    public void isRefundPossibleShouldReturnSuperResultIfRefundNotPossible() throws Exception {
        doReturn(false).when(testObj).invokeSuperIsRefundPossible();

        final boolean result = testObj.isRefundPossible();

        assertFalse(result);
        verify(testObj, never()).getOrder();
    }

    @Test
    public void isRefundPossibleShouldReturnFalseIfThereAreNoCapturePaymentTransactionEntries() throws Exception {
        doReturn(true).when(testObj).invokeSuperIsRefundPossible();
        when(orderMock.getPaymentTransactions()).thenReturn(asList(paymentTransaction1Mock, paymentTransaction3Mock));

        final boolean result = testObj.isRefundPossible();

        assertFalse(result);
    }

    @Test
    public void isRefundPossibleShouldReturnFalseIfCapturePaymentTransactionEntryIsPending() throws Exception {
        doReturn(true).when(testObj).invokeSuperIsRefundPossible();
        when(orderMock.getPaymentTransactions()).thenReturn(singletonList(paymentTransaction2Mock));

        final boolean result = testObj.isRefundPossible();

        assertFalse(result);
    }

    @Test
    public void isRefundPossibleShouldReturnTrueIfCapturePaymentTransactionEntryIsNotPending() throws Exception {
        doReturn(true).when(testObj).invokeSuperIsRefundPossible();
        when(orderMock.getPaymentTransactions()).thenReturn(singletonList(paymentTransaction3Mock));

        final boolean result = testObj.isRefundPossible();

        assertTrue(result);
    }
}