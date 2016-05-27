package com.worldpay.worldpayoms.actions.returns;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.returns.ReturnService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static com.hybris.cockpitng.actions.ActionResult.StatusFlag.OBJECT_PERSISTED;
import static com.worldpay.worldpayoms.actions.returns.WorldpayCreateReturnRequestAction.SOCKET_OUT_CONTEXT;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;
import static de.hybris.platform.payment.enums.PaymentTransactionType.SETTLED;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayCreateReturnRequestActionTest {

    @Spy
    @InjectMocks
    private WorldpayCreateReturnRequestAction testObj = new WorldpayCreateReturnRequestAction();

    @Mock
    private ReturnService returnServiceMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;

    @Mock
    private ActionContext<OrderModel> actionContextMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private OrderModel orderModelMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private PaymentTransactionEntryModel capturePaymentTransactionEntryModelMock;
    @Mock
    private PaymentTransactionEntryModel settlePaymentTransactionEntryModelMock;
    @Mock
    private WorldpayAPMPaymentInfoModel worldpayAPMPaymentInfoModelMock;
    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoModelMock;
    @Mock
    private WorldpayAPMConfigurationModel apmConfigurationMock;
    @Mock
    private OrderEntryModel orderEntryModelMock;
    @Mock
    private ConsignmentModel consignmentModelMock;

    @Before
    public void setup() {
        doNothing().when(testObj).sendOutput(anyString(),anyString());
        when(actionContextMock.getData()).thenReturn(orderModelMock);
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(paymentTransactionModelMock));
        when(paymentTransactionModelMock.getInfo()).thenReturn(creditCardPaymentInfoModelMock);
        when(worldpayPaymentTransactionServiceMock.filterPaymentTransactionEntriesOfType(paymentTransactionModelMock, CAPTURE)).thenReturn(singletonList(capturePaymentTransactionEntryModelMock));
        when(worldpayPaymentTransactionServiceMock.filterPaymentTransactionEntriesOfType(paymentTransactionModelMock, SETTLED)).thenReturn(singletonList(settlePaymentTransactionEntryModelMock));
        when(returnServiceMock.getAllReturnableEntries(orderModelMock)).thenReturn(singletonMap(orderEntryModelMock, 1L));
        when(orderModelMock.getConsignments()).thenReturn(singleton(consignmentModelMock));
        when(consignmentModelMock.getStatus()).thenReturn(ConsignmentStatus.SHIPPED);
    }

    @Test
    public void canPerformShouldReturnTrueWhenOrderIsReturnableAndRefundable() {

        final boolean result = testObj.canPerform(actionContextMock);

        assertTrue(result);
    }

    @Test
    public void canPerformShouldReturnTrueWhenOrderIsReturnableAndRefundableAndConsignmentIsPickupComplete() {
        when(consignmentModelMock.getStatus()).thenReturn(ConsignmentStatus.PICKUP_COMPLETE);

        final boolean result = testObj.canPerform(actionContextMock);

        assertTrue(result);
    }

    @Test
    public void canPerformShouldReturnFalseWhenOrderIsNull() {
        when(actionContextMock.getData()).thenReturn(null);

        final boolean result = testObj.canPerform(actionContextMock);

        assertFalse(result);
    }

    @Test
    public void canPerformShouldReturnFalseWhenOrderEntriesIsNull() {
        when(orderModelMock.getEntries()).thenReturn(null);

        final boolean result = testObj.canPerform(actionContextMock);

        assertFalse(result);
    }

    @Test
    public void canPerformShouldReturnFalseWhenOrderConsignmentsIsEmpty() {
        when(orderModelMock.getConsignments()).thenReturn(null);

        final boolean result = testObj.canPerform(actionContextMock);

        assertFalse(result);
    }

    @Test
    public void canPerformShouldReturnFalseWhenAllReturnableEntriesIsEmpty() {
        when(returnServiceMock.getAllReturnableEntries(orderModelMock)).thenReturn(null);

        final boolean result = testObj.canPerform(actionContextMock);

        assertFalse(result);
    }

    @Test
    public void canPerformShouldReturnFalseWhenPaymentTransactionsIsEmpty() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(null);

        final boolean result = testObj.canPerform(actionContextMock);

        assertFalse(result);
    }

    @Test
    public void canPerformShouldReturnTrueWhenPaymentTransactionsIsForAnAPMThatSupportsAutomaticRefunds() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(paymentTransactionModelMock));
        when(paymentTransactionModelMock.getInfo()).thenReturn(worldpayAPMPaymentInfoModelMock);
        when(worldpayAPMPaymentInfoModelMock.getApmConfiguration()).thenReturn(apmConfigurationMock);
        when(apmConfigurationMock.getAutomaticRefunds()).thenReturn(true);

        final boolean result = testObj.canPerform(actionContextMock);

        assertTrue(result);
    }

    @Test
    public void canPerformShouldReturnFalseWhenPaymentTransactionsIsForAnAPMThatDoesNotSupportsAutomaticRefunds() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(paymentTransactionModelMock));
        when(paymentTransactionModelMock.getInfo()).thenReturn(worldpayAPMPaymentInfoModelMock);
        when(worldpayAPMPaymentInfoModelMock.getApmConfiguration()).thenReturn(apmConfigurationMock);
        when(apmConfigurationMock.getAutomaticRefunds()).thenReturn(false);

        final boolean result = testObj.canPerform(actionContextMock);

        assertFalse(result);
    }

    @Test
    public void canPerformShouldReturnFalseWhenPaymentTransactionsIsForAnAPMThatDoesSupportsAutomaticRefundsButHasNotBeenSettled() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(paymentTransactionModelMock));
        when(paymentTransactionModelMock.getInfo()).thenReturn(worldpayAPMPaymentInfoModelMock);
        when(worldpayAPMPaymentInfoModelMock.getApmConfiguration()).thenReturn(apmConfigurationMock);
        when(apmConfigurationMock.getAutomaticRefunds()).thenReturn(true);
        when(worldpayPaymentTransactionServiceMock.filterPaymentTransactionEntriesOfType(paymentTransactionModelMock, SETTLED)).thenReturn(null);

        final boolean result = testObj.canPerform(actionContextMock);

        assertFalse(result);
    }

    @Test
    public void canPerformShouldReturnFalseWhenPaymentTransactionsIsForACreditCardAndNoCaptureExists() {
        when(worldpayPaymentTransactionServiceMock.filterPaymentTransactionEntriesOfType(paymentTransactionModelMock, CAPTURE)).thenReturn(null);

        final boolean result = testObj.canPerform(actionContextMock);

        assertFalse(result);
    }

    @Test
    public void canPerformShouldReturnFalseWhenPaymentTransactionsIsForACreditCardAndCaptureIsPending() {
        when(worldpayPaymentTransactionServiceMock.isPaymentTransactionPending(paymentTransactionModelMock, CAPTURE)).thenReturn(true);

        final boolean result = testObj.canPerform(actionContextMock);

        assertFalse(result);
    }

    @Test
    public void shouldPerformAction() {
        final ActionResult<OrderModel> result = testObj.perform(actionContextMock);

        verify(testObj).sendOutput(SOCKET_OUT_CONTEXT, orderModelMock);
        assertTrue(result.getStatusFlags().contains(OBJECT_PERSISTED));
        assertEquals(ActionResult.SUCCESS, result.getResultCode());
    }

    @Test
    public void getConfirmationMessageShouldReturnLabel() throws Exception {
        final String result = testObj.getConfirmationMessage(actionContextMock);

        assertNull(result);
    }

    @Test
    public void needsConfirmationShouldReturnTrueIfModified() throws Exception {
        final boolean result = testObj.needsConfirmation(actionContextMock);

        assertFalse(result);
    }
}
