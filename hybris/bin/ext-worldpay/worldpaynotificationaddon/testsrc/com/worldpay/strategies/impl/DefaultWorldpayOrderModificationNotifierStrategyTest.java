package com.worldpay.strategies.impl;

import com.worldpay.dao.OrderModificationDao;
import com.worldpay.strategies.WorldpayOrderModificationNotifierStrategy;
import com.worldpay.strategies.impl.DefaultWorldpayOrderModificationNotifierStrategy;
import com.worldpay.worldpaynotificationaddon.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketBusinessService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Date;

import static com.worldpay.strategies.WorldpayOrderModificationNotifierStrategy.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayOrderModificationNotifierStrategyTest {

    public static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    public static final int UNPROCESSED_DAYS = 5;
    public static final String THERE_ARE_UNPROCESSED_ORDERS = "thereAreUnprocessedOrders";
    public static final String UNPROCESSED_ORDERS = "unprocessedOrders";

    @InjectMocks
    private WorldpayOrderModificationNotifierStrategy testObj = new DefaultWorldpayOrderModificationNotifierStrategy();

    @Mock
    private TicketBusinessService ticketBusinessServiceMock;
    @Mock
    private OrderModificationDao orderModificationDaoMock;
    @Mock
    private WorldpayOrderModificationModel orderModificationModelMock;
    @Mock
    private ModelService modelServiceMock;
    @Captor
    private ArgumentCaptor<CsTicketModel> csTicketModelArgumentCaptor;
    @Captor
    private ArgumentCaptor<CsCustomerEventModel> csCustomerEventModelCaptor;
    @Mock
    private L10NService l10nService;

    @Before
    public void setUp() {
        when(orderModificationModelMock.getType()).thenReturn(PaymentTransactionType.AUTHORIZATION);
        when(orderModificationModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderModificationModelMock.getProcessed()).thenReturn(false);
        when(orderModificationModelMock.getNotified()).thenReturn(false);
        when(l10nService.getLocalizedString(WORLDPAYADDON_ERRORS_THERE_ARE_UNPROCESSED_ORDERS)).thenReturn(THERE_ARE_UNPROCESSED_ORDERS);
        when(l10nService.getLocalizedString(WORLDPAYADDON_ERRORS_UNPROCESSED_ORDERS)).thenReturn(UNPROCESSED_ORDERS);
    }

    @Test
    public void whenUnprocessedModificationReturnedThenPublishTicket() {
        when(orderModificationDaoMock.findUnprocessedAndNotNotifiedOrderModificationsBeforeDate(any(Date.class))).thenReturn(Collections.singletonList(orderModificationModelMock));
        when(modelServiceMock.create(any(Class.class))).thenReturn(new CsTicketModel());

        testObj.notifyThatOrdersHaveNotBeenProcessed(UNPROCESSED_DAYS);

        verify(ticketBusinessServiceMock).createTicket(csTicketModelArgumentCaptor.capture(), csCustomerEventModelCaptor.capture());
        verify(orderModificationModelMock).setNotified(true);
        verify(modelServiceMock).save(orderModificationModelMock);
        final CsTicketModel csTicketModel = csTicketModelArgumentCaptor.getValue();

        assertEquals(THERE_ARE_UNPROCESSED_ORDERS, csTicketModel.getHeadline());
        assertEquals(CsTicketCategory.PROBLEM, csTicketModel.getCategory());
        assertEquals(CsTicketPriority.HIGH, csTicketModel.getPriority());

        final CsCustomerEventModel csCustomerEventModel = csCustomerEventModelCaptor.getValue();
        assertTrue(csCustomerEventModel.getText().startsWith(UNPROCESSED_ORDERS));
    }

    @Test
    public void whenNoUnprocessedModificationsThenDoNotPublishTickets() {
        when(orderModificationDaoMock.findUnprocessedAndNotNotifiedOrderModificationsBeforeDate(new Date())).thenReturn(Collections.emptyList());

        testObj.notifyThatOrdersHaveNotBeenProcessed(5);

        verify(ticketBusinessServiceMock, never()).createTicket(any(), any());
    }
}