package com.worldpay.transaction.impl;

import com.worldpay.transaction.EntryCodeStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayEntryCodeStrategyImplTest {

    public static final String PAYMENT_TRANSACTION_CODE = "paymentTransactionCode";
    @InjectMocks
    private EntryCodeStrategy testObj = new WorldpayEntryCodeStrategyImpl();

    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private PaymentTransactionEntryModel transactionEntry1Mock;

    @Test
    public void generateNewEntryCodeShouldReturnCodeEndingInEntriesSizePlusOne() throws Exception {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(transactionEntry1Mock));
        when(paymentTransactionModelMock.getCode()).thenReturn(PAYMENT_TRANSACTION_CODE);

        final String result = testObj.generateCode(paymentTransactionModelMock);

        assertEquals(PAYMENT_TRANSACTION_CODE + "-2", result);
    }

    @Test
    public void generateNewEntryCodeShouldReturnCodeEndingIn1WhenEntriesIsNull() throws Exception {
        when(paymentTransactionModelMock.getEntries()).thenReturn(null);
        when(paymentTransactionModelMock.getCode()).thenReturn(PAYMENT_TRANSACTION_CODE);

        final String result = testObj.generateCode(paymentTransactionModelMock);

        assertEquals(PAYMENT_TRANSACTION_CODE + "-1", result);
    }
}