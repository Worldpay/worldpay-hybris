package com.worldpay.cscockpit.checkout.service;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.cscockpit.exceptions.ValidationException;
import de.hybris.platform.payment.TransactionInfoService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCsWorldpayCheckoutServiceTest {

    @Spy
    @InjectMocks
    private DefaultCsWorldpayCheckoutService testObj = new DefaultCsWorldpayCheckoutService();

    @Mock
    private CartModel cartModelMock;
    @Mock
    private AddressModel paymentAddressMock;
    @Mock
    private PaymentTransactionModel invalidPaymentTransactionMock;
    @Mock
    private PaymentTransactionModel validPaymentTransactionMock;
    @Mock
    private PaymentTransactionEntryModel successfulInvalidAuthorisationEntryMock;
    @Mock
    private PaymentTransactionEntryModel successfulValidAuthorisationEntryMock;
    @Mock
    private PaymentTransactionEntryModel unsuccessfulValidAuthorisationEntryMock;
    @Mock
    private PaymentTransactionEntryModel capturedEntryMock;
    @Mock
    private TransactionInfoService transactionInfoServiceMock;

    @Before
    public void setup() {
        doReturn(transactionInfoServiceMock).when(testObj).getTransactionInfoServiceFromSuper();
        when(invalidPaymentTransactionMock.getEntries()).thenReturn(singletonList(capturedEntryMock));
        when(validPaymentTransactionMock.getEntries()).thenReturn(asList(successfulInvalidAuthorisationEntryMock, unsuccessfulValidAuthorisationEntryMock, successfulValidAuthorisationEntryMock));
        when(capturedEntryMock.getType()).thenReturn(CAPTURE);
        when(successfulValidAuthorisationEntryMock.getType()).thenReturn(AUTHORIZATION);
        when(successfulInvalidAuthorisationEntryMock.getType()).thenReturn(AUTHORIZATION);
        when(unsuccessfulValidAuthorisationEntryMock.getType()).thenReturn(AUTHORIZATION);
        when(transactionInfoServiceMock.isSuccessful(successfulValidAuthorisationEntryMock)).thenReturn(true);
        when(transactionInfoServiceMock.isValid(successfulValidAuthorisationEntryMock)).thenReturn(true);
        when(transactionInfoServiceMock.isSuccessful(successfulInvalidAuthorisationEntryMock)).thenReturn(true);
        when(transactionInfoServiceMock.isValid(successfulInvalidAuthorisationEntryMock)).thenReturn(false);
        when(transactionInfoServiceMock.isValid(unsuccessfulValidAuthorisationEntryMock)).thenReturn(true);
    }

    @Test(expected = ValidationException.class)
    public void validateCartForCreatePaymentsThrowsExceptionWhenNoPaymentAddressInCartButPaymentAddressRequired() throws ValidationException {
        doNothing().when(testObj).invokeSuperValidateCartForCreatePayments(cartModelMock);
        doReturn(true).when(testObj).paymentAddressRequired();
        when(cartModelMock.getPaymentAddress()).thenReturn(null);

        testObj.validateCartForCreatePayments(cartModelMock);

        verify(testObj).invokeSuperValidateCartForCreatePayments(cartModelMock);
        verify(cartModelMock).getPaymentAddress();
    }

    @Test
    public void validateCartForCreateDoesNotValidateWhenPaymentAddressNotRequired() throws ValidationException {
        doNothing().when(testObj).invokeSuperValidateCartForCreatePayments(cartModelMock);
        doReturn(false).when(testObj).paymentAddressRequired();

        testObj.validateCartForCreatePayments(cartModelMock);

        verify(testObj).invokeSuperValidateCartForCreatePayments(cartModelMock);
        verify(cartModelMock, never()).getPaymentAddress();
    }

    @Test
    public void validateCartForCreateDoesNotThrowExceptionWhenPaymentAddressRequiredAndCartHasPaymentAddress() throws ValidationException {
        doNothing().when(testObj).invokeSuperValidateCartForCreatePayments(cartModelMock);
        doReturn(true).when(testObj).paymentAddressRequired();
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressMock);

        testObj.validateCartForCreatePayments(cartModelMock);

        verify(testObj).invokeSuperValidateCartForCreatePayments(cartModelMock);
        verify(cartModelMock).getPaymentAddress();
    }

    @Test
    public void getValidPaymentTransactionsReturnsTransactionsWhichHaveSuccessfulAuthorisationEntries() {
        when(cartModelMock.getPaymentTransactions()).thenReturn(asList(invalidPaymentTransactionMock, validPaymentTransactionMock));

        final List<PaymentTransactionModel> result = testObj.getValidPaymentTransactions(cartModelMock);

        assertTrue(result.size() == 1);
        assertSame(validPaymentTransactionMock, result.get(0));
    }

    @Test
    public void getValidPaymentTransactionsReturnsEmptyResultWhenCartIsNull() {
        final List<PaymentTransactionModel> result = testObj.getValidPaymentTransactions(null);

        assertTrue(result.isEmpty());
        verify(cartModelMock, never()).getPaymentTransactions();
    }
}