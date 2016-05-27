package com.worldpay.strategies.impl;

import com.worldpay.dao.ProcessDefinitionDao;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static de.hybris.platform.payment.dto.TransactionStatus.REJECTED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultPaymentTransactionRejectionStrategyTest {

    public static final String BUSINESS_PROCESS_CODE = "businessProcessCode";

    @InjectMocks
    private DefaultPaymentTransactionRejectionStrategy testObj = new DefaultPaymentTransactionRejectionStrategy();

    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private ProcessDefinitionDao processDefinitionDaoMock;
    @Mock
    private BusinessProcessService businessProcessServiceMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private AbstractOrderModel orderModelMock;
    @Mock
    private BusinessProcessModel businessProcessModelMock;

    @Test
    public void testExecuteRejection() throws Exception {
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
        when(processDefinitionDaoMock.findWaitingOrderProcesses(orderModelMock.getCode(), AUTHORIZATION)).thenReturn(Collections.singletonList(businessProcessModelMock));
        when(businessProcessModelMock.getCode()).thenReturn(BUSINESS_PROCESS_CODE);

        testObj.executeRejection(paymentTransactionModelMock);

        verify(worldpayPaymentTransactionServiceMock).updateEntriesStatus(paymentTransactionModelMock.getEntries(), REJECTED.name());
        verify(modelServiceMock).save(paymentTransactionModelMock);
        verify(businessProcessServiceMock).triggerEvent(BUSINESS_PROCESS_CODE + "_" + AUTHORIZATION);
    }

    @Test
    public void testExecuteRejectionShouldNotTriggerABusinessProcessWhenNoneFound() throws Exception {
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
        when(processDefinitionDaoMock.findWaitingOrderProcesses(orderModelMock.getCode(), AUTHORIZATION)).thenReturn(Collections.<BusinessProcessModel>emptyList());

        testObj.executeRejection(paymentTransactionModelMock);

        verify(worldpayPaymentTransactionServiceMock).updateEntriesStatus(paymentTransactionModelMock.getEntries(), REJECTED.name());
        verify(modelServiceMock).save(paymentTransactionModelMock);
        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
    }
}