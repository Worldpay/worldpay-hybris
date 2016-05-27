package com.worldpay.worldpaynotificationaddon.controllers.order.notification;

import com.worldpay.core.event.OrderModificationEvent;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.marshalling.impl.DefaultPaymentServiceMarshaller;
import com.worldpay.service.notification.OrderNotificationMessage;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Set;

import static com.worldpay.worldpaynotificationaddon.controllers.WorldpaynotificationaddonControllerConstants.WorldpayNotificationAddon.Views.WORLDPAY_RESPONSE_OK_VIEW;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


/**
 * OrderModificationController
 * <p/>
 * <p>
 * The OrderModificationController receives the order notification messages from worldpay and in turn initiates a process to
 * write it to a queue from which it will be picked up and processed.
 * </p>
 */
@Controller
@RequestMapping (value = "/worldpay/merchant_callback")
public class OrderModificationController {

    private static final Logger LOG = Logger.getLogger(OrderModificationController.class);

    @Resource
    private Set<String> processableJournalTypeCodes;

    @Resource (name = "orderNotificationRequestToMessageConverter")
    private Converter<PaymentService, OrderNotificationMessage> orderModificationRequestConverter;

    @Resource
    private EventService eventService;

    /**
     * This is the entry point for the Order Notification process process. The page returned by this method contains the
     * unconditional "[OK]" that will be sent to worldpay.
     *
     * @param request - the HttpServletRequest carrying the order notification XML from worldpay.
     * @return - the world pay response page containing the unconditional [OK].
     */
    @RequestMapping (method = POST)
    public String processOrderNotification(final HttpServletRequest request) {
        try {
            final OrderNotificationMessage orderNotificationMessage = createOrderModificationMessageFromRequest(request);
            if (shouldProcessModificationMessage(orderNotificationMessage.getJournalReply().getJournalType().getCode())) {
                eventService.publishEvent(new OrderModificationEvent(orderNotificationMessage));
            }
        } catch (WorldpayModelTransformationException | JAXBException | IOException e) {
            LOG.error("Notification message transformation error", e);
        }
        return WORLDPAY_RESPONSE_OK_VIEW;
    }

    private OrderNotificationMessage createOrderModificationMessageFromRequest(HttpServletRequest request) throws WorldpayModelTransformationException, IOException, JAXBException {
        final PaymentService paymentService = getPaymentServiceMarshaller().unmarshal(request.getInputStream());
        return orderModificationRequestConverter.convert(paymentService);
    }

    protected DefaultPaymentServiceMarshaller getPaymentServiceMarshaller() {
        return DefaultPaymentServiceMarshaller.getInstance();
    }

    protected boolean shouldProcessModificationMessage(final String journalTypeCode) {
        return getProcessableJournalTypeCodes().contains(journalTypeCode);
    }

    public Set<String> getProcessableJournalTypeCodes() {
        return processableJournalTypeCodes;
    }

    public void setProcessableJournalTypeCodes(Set<String> processableJournalTypeCodes) {
        this.processableJournalTypeCodes = processableJournalTypeCodes;
    }
}
