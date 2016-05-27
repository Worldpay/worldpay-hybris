package com.worldpay.worldpaynotificationaddon.controllers.order.notification;

import com.worldpay.core.event.OrderModificationEvent;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.marshalling.impl.DefaultPaymentServiceMarshaller;
import com.worldpay.service.model.AuthorisedStatus;
import com.worldpay.service.notification.OrderNotificationMessage;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

import static com.worldpay.worldpaynotificationaddon.controllers.WorldpaynotificationaddonControllerConstants.WorldpayNotificationAddon;
import static org.junit.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class OrderModificationControllerTest {

    private static final String AUTHORISED = "AUTHORISED";
    private static final String CAPTURED = "CAPTURED";

    private static final AuthorisedStatus JOURNAL_TYPE_NOT_HANDLED = AuthorisedStatus.CHARGED_BACK;
    private static final AuthorisedStatus JOURNAL_TYPE_HANDLED = AuthorisedStatus.AUTHORISED;

    @Spy
    @InjectMocks
    private OrderModificationController testObj = new OrderModificationController();

    @Mock
    private HttpServletRequest requestMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock (name = "orderNotificationRequestConverter")
    private Converter<PaymentService, OrderNotificationMessage> orderNotificationRequestConverterMock;
    @Mock
    private ServletInputStream requestInputStreamMock;
    @Mock
    private EventService eventServiceMock;
    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private DefaultPaymentServiceMarshaller paymentServiceMarshallerMock;
    @Captor
    private ArgumentCaptor<OrderModificationEvent> orderModificationEventArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        doReturn(paymentServiceMarshallerMock).when(testObj).getPaymentServiceMarshaller();
        when(paymentServiceMarshallerMock.unmarshal(requestInputStreamMock)).thenReturn(paymentServiceMock);
        final Set<String> processableJournalTypeCodes = new HashSet<>();
        processableJournalTypeCodes.add(AUTHORISED);
        processableJournalTypeCodes.add(CAPTURED);
        testObj.setProcessableJournalTypeCodes(processableJournalTypeCodes);

        when(requestMock.getInputStream()).thenReturn(requestInputStreamMock);
    }

    @Test
    public void processOrderNotificationShouldNotProcessTheNotificationIfNotificationNotProcessable() throws Exception {
        when(orderNotificationRequestConverterMock.convert(paymentServiceMock)).thenReturn(orderNotificationMessageMock);
        when(orderNotificationMessageMock.getJournalReply().getJournalType()).thenReturn(JOURNAL_TYPE_NOT_HANDLED);

        final String result = testObj.processOrderNotification(requestMock);

        assertEquals(WorldpayNotificationAddon.Views.WORLDPAY_RESPONSE_OK_VIEW, result);
        verify(eventServiceMock, never()).publishEvent(any(AbstractEvent.class));
    }

    @Test
    public void processOrderNotificationShouldProcessNotificationIfNotificationIsProcessable() {
        when(orderNotificationRequestConverterMock.convert(paymentServiceMock)).thenReturn(orderNotificationMessageMock);
        when(orderNotificationMessageMock.getJournalReply().getJournalType()).thenReturn(JOURNAL_TYPE_HANDLED);

        final String result = testObj.processOrderNotification(requestMock);

        assertEquals(WorldpayNotificationAddon.Views.WORLDPAY_RESPONSE_OK_VIEW, result);
        verify(eventServiceMock).publishEvent(orderModificationEventArgumentCaptor.capture());

        final OrderModificationEvent orderModificationEvent = orderModificationEventArgumentCaptor.getValue();
        assertEquals(orderNotificationMessageMock, orderModificationEvent.getOrderNotificationMessage());
    }
}