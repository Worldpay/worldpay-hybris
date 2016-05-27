package com.worldpay.payment.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayTransactionInfoServiceTest {

    @InjectMocks
    private WorldpayTransactionInfoService testObj = new WorldpayTransactionInfoService();
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;

    @Test
    public void isSuccessfulShouldReturnTrueWhenEntryIsAuthorisedAndAcceptedAndNotPending() throws Exception {
        when(paymentTransactionEntryModelMock.getType()).thenReturn(PaymentTransactionType.AUTHORIZATION);
        when(paymentTransactionEntryModelMock.getPending()).thenReturn(false);
        when(paymentTransactionEntryModelMock.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());

        final boolean result = testObj.isSuccessful(paymentTransactionEntryModelMock);

        assertTrue(result);
    }

    @Test
    public void isSuccessfulShouldReturnFalseWhenEntryIsNotAuthorisedAndPending() throws Exception {
        when(paymentTransactionEntryModelMock.getType()).thenReturn(PaymentTransactionType.CAPTURE);
        when(paymentTransactionEntryModelMock.getPending()).thenReturn(true);
        when(paymentTransactionEntryModelMock.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());

        final boolean result = testObj.isSuccessful(paymentTransactionEntryModelMock);

        assertFalse(result);
    }

    @Test
    public void isSuccessfulShouldReturnFalseWhenEntryIsNotAccepted() throws Exception {
        when(paymentTransactionEntryModelMock.getType()).thenReturn(PaymentTransactionType.AUTHORIZATION);
        when(paymentTransactionEntryModelMock.getPending()).thenReturn(false);
        when(paymentTransactionEntryModelMock.getTransactionStatus()).thenReturn(TransactionStatus.REJECTED.name());

        final boolean result = testObj.isSuccessful(paymentTransactionEntryModelMock);

        assertFalse(result);
    }

    @Test
    public void isSuccessfulShouldReturnFalseWhenTransactionStatusIsNull() {
        when(paymentTransactionEntryModelMock.getType()).thenReturn(PaymentTransactionType.AUTHORIZATION);
        when(paymentTransactionEntryModelMock.getPending()).thenReturn(false);
        when(paymentTransactionEntryModelMock.getTransactionStatus()).thenReturn(null);

        final boolean result = testObj.isSuccessful(paymentTransactionEntryModelMock);

        assertFalse(result);
    }
}