package com.worldpay.worldpayoms.fulfilmentprocess.actions.order;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.model.ReturnProcessModel;
import de.hybris.platform.warehousing.returns.service.RefundAmountCalculationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static de.hybris.platform.basecommerce.enums.ReturnStatus.PAYMENT_REVERSAL_FAILED;
import static de.hybris.platform.basecommerce.enums.ReturnStatus.PAYMENT_REVERSAL_PENDING;
import static de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition.NOK;
import static de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition.OK;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayCaptureRefundActionTest {

    private static final BigDecimal CUSTOM_REFUND_AMOUNT = new BigDecimal(19);
    private static final BigDecimal ORIGINAL_REFUND_AMOUNT = new BigDecimal(17);

    @InjectMocks
    private WorldpayCaptureRefundAction testObj = new WorldpayCaptureRefundAction();

    @Mock
    private ReturnProcessModel returnProcessModelMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private ReturnRequestModel returnRequestMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private PaymentTransactionModel paymentTransactionMock;
    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryMock;
    @Mock
    private RefundAmountCalculationService refundAmountCalculationServiceMock;

    @Before
    public void setup() {
        when(returnProcessModelMock.getReturnRequest()).thenReturn(returnRequestMock);
    }

    @Test
    public void shouldReturnNOKWhenThereAreNoTransactions() throws Exception {
        when(returnRequestMock.getOrder().getPaymentTransactions()).thenReturn(emptyList());

        final Transition transition = testObj.executeAction(returnProcessModelMock);

        verify(returnRequestMock).setStatus(PAYMENT_REVERSAL_FAILED);
        verify(modelServiceMock).save(returnRequestMock);
        assertEquals(NOK, transition);
    }

    @Test
    public void shouldReturnOKWhenRefundFollowOnIsSuccessfulWithCustomRefundAmount() throws Exception {
        when(returnRequestMock.getOrder().getPaymentTransactions()).thenReturn(singletonList(paymentTransactionMock));
        when(refundAmountCalculationServiceMock.getCustomRefundAmount(returnRequestMock)).thenReturn(CUSTOM_REFUND_AMOUNT);
        when(paymentServiceMock.refundFollowOn(paymentTransactionMock, CUSTOM_REFUND_AMOUNT)).thenReturn(paymentTransactionEntryMock);

        final Transition transition = testObj.executeAction(returnProcessModelMock);

        verify(returnRequestMock).setStatus(PAYMENT_REVERSAL_PENDING);
        verify(returnRequestMock).setPaymentTransactionEntry(paymentTransactionEntryMock);
        verify(modelServiceMock).save(returnRequestMock);
        assertEquals(OK, transition);
    }

    @Test
    public void shouldReturnOKWhenRefundFollowOnIsSuccessfulWithOriginalAmount() throws Exception {
        when(returnRequestMock.getOrder().getPaymentTransactions()).thenReturn(singletonList(paymentTransactionMock));
        when(refundAmountCalculationServiceMock.getCustomRefundAmount(returnRequestMock)).thenReturn(null);
        when(refundAmountCalculationServiceMock.getOriginalRefundAmount(returnRequestMock)).thenReturn(ORIGINAL_REFUND_AMOUNT);
        when(paymentServiceMock.refundFollowOn(paymentTransactionMock, ORIGINAL_REFUND_AMOUNT)).thenReturn(paymentTransactionEntryMock);

        final Transition transition = testObj.executeAction(returnProcessModelMock);

        verify(returnRequestMock).setStatus(PAYMENT_REVERSAL_PENDING);
        verify(returnRequestMock).setPaymentTransactionEntry(paymentTransactionEntryMock);
        verify(modelServiceMock).save(returnRequestMock);
        assertEquals(OK, transition);
    }

    @Test
    public void shouldReturnNOKWhenRefundFollowOnFails() throws Exception {
        when(returnRequestMock.getOrder().getPaymentTransactions()).thenReturn(singletonList(paymentTransactionMock));
        when(refundAmountCalculationServiceMock.getCustomRefundAmount(returnRequestMock)).thenReturn(null);
        when(refundAmountCalculationServiceMock.getOriginalRefundAmount(returnRequestMock)).thenReturn(ORIGINAL_REFUND_AMOUNT);
        when(paymentServiceMock.refundFollowOn(paymentTransactionMock, ORIGINAL_REFUND_AMOUNT)).thenThrow(new AdapterException("errorMessage"));

        final Transition transition = testObj.executeAction(returnProcessModelMock);

        verify(returnRequestMock).setStatus(PAYMENT_REVERSAL_FAILED);
        verify(returnRequestMock, never()).setPaymentTransactionEntry(any(PaymentTransactionEntryModel.class));
        verify(modelServiceMock).save(returnRequestMock);
        assertEquals(NOK, transition);
    }
}