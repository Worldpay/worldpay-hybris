package com.worldpay.widgets.renderers.impl;

import com.worldpay.exception.WorldpayException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import com.worldpay.util.WorldpayUtil;
import com.worldpay.widgets.controllers.WorldpayCardPaymentController;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.widgets.impl.DefaultListboxWidget;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cscockpit.exceptions.PaymentException;
import de.hybris.platform.cscockpit.exceptions.ValidationException;
import de.hybris.platform.cscockpit.utils.LabelUtils;
import de.hybris.platform.cscockpit.widgets.controllers.CheckoutController;
import de.hybris.platform.cscockpit.widgets.models.impl.CheckoutPaymentWidgetModel;
import de.hybris.platform.cscockpit.widgets.renderers.impl.CheckoutPaymentWidgetRenderer;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;

import static java.math.BigDecimal.ZERO;

public class WorldpayCheckoutPaymentWidgetRenderer extends CheckoutPaymentWidgetRenderer {

    private static final Logger LOG = Logger.getLogger(WorldpayCheckoutPaymentWidgetRenderer.class);

    protected static final String ACCEPT_HEADER = "Accept";
    protected static final String USER_AGENT_HEADER = "User-Agent";
    protected static final String EVENT_ON_CLOSE = "onClose";
    protected static final String MESSAGE_BOX_CLASS = "z-msgbox z-msgbox-error";
    private WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategy;

    /**
     * {@inheritDoc}
     * @see de.hybris.platform.cscockpit.widgets.renderers.impl.CheckoutPaymentWidgetRenderer#handlePayUsingStoredCardEvent(de.hybris.platform.cockpit.widgets.impl.DefaultListboxWidget,
     * org.zkoss.zk.ui.event.Event, de.hybris.platform.cockpit.model.meta.TypedObject, org.zkoss.zul.Decimalbox,
     * org.zkoss.zul.Textbox)
     */
    @Override
    protected void handlePayUsingStoredCardEvent(final DefaultListboxWidget<CheckoutPaymentWidgetModel, CheckoutController> widget, final Event event,
                                                 final TypedObject item, final Decimalbox amountInput, final Textbox cv2Input) throws Exception {
        final String cv2 = cv2Input.getValue();
        final BigDecimal amount = amountInput.getValue();

        if (!shouldHandlePayUsingStoredCardEvent(item, cv2, amount)) {
            return;
        }

        // We need to add information about the client for the call through to worldpay. This is done by serializing the information into the cv2 field.

        final CustomerModel currentCustomer = (CustomerModel) ((CartModel) widget.getWidgetController().getBasketController().getCart().getObject()).getUser();
        final WorldpayAdditionalInfoData worldpayAdditionalInfo = createWorldpayAdditionalInfo(cv2, currentCustomer, getExecutionFromEvent(event));

        // Now follow standard approach of processPayment but with the cv2 substituted with the serialized info
        try {
            if (widget.getWidgetController().processPayment(item, amount, serializeAdditionalInfo(worldpayAdditionalInfo))) {
                widget.getWidgetModel().notifyListeners();
                widget.getWidgetController().dispatchEvent(null, widget, null);
            }
        } catch (final PaymentException e) {
            handlePaymentException(widget, e);
        } catch (final ValidationException e) {
            handleValidationException(widget, amount, e);
        } catch (final Exception e) {
            LOG.error("Failed to use existing stored card", e);
            throw e;
        }
    }

    protected boolean shouldHandlePayUsingStoredCardEvent(TypedObject item, String cv2, BigDecimal amount) {
        return amount != null && amount.compareTo(ZERO) > 0 && StringUtils.isNotBlank(cv2) && item.getObject() != null;
    }

    protected Execution getExecutionFromEvent(Event event) {
        return event.getTarget().getDesktop().getExecution();
    }

    protected String serializeAdditionalInfo(WorldpayAdditionalInfoData info) {
        return WorldpayUtil.serializeWorldpayAdditionalInfo(info);
    }

    protected void handleValidationException(DefaultListboxWidget<CheckoutPaymentWidgetModel, CheckoutController> widget, BigDecimal amount, ValidationException e) {
        LOG.info(MessageFormat.format("ValidationException thrown while processing payment for amount [{0}]", amount), e);
        final StringBuilder message = new StringBuilder(e.getMessage());
        if (e.getCause() != null) {
            message.append(" - ").append(e.getCause().getMessage());
        }
        try {
            Messagebox.show(message.toString(), LabelUtils.getLabel(widget, "failedToValidate"), 1, MESSAGE_BOX_CLASS);
        } catch (InterruptedException interruptedException) {
            LOG.error(interruptedException.getMessage());
        }
    }

    protected void handlePaymentException(DefaultListboxWidget<CheckoutPaymentWidgetModel, CheckoutController> widget, PaymentException e) {
        try {
            Messagebox.show(e.getMessage(), LabelUtils.getLabel(widget, "failedToAuthorise"), 1, MESSAGE_BOX_CLASS);
        } catch (InterruptedException e1) {
            LOG.error(e.getMessage());
        }
        widget.getWidgetController().dispatchEvent(null, widget, null);
    }

    /**
     * Fill out the additional information required by Worldpay
     * @param cv2
     * @param customerModel
     * @param execution     Execution information
     */
    private WorldpayAdditionalInfoData createWorldpayAdditionalInfo(final String cv2, final CustomerModel customerModel, final Execution execution) {
        final WorldpayAdditionalInfoData info = new WorldpayAdditionalInfoData();
        info.setSecurityCode(cv2);

        final String customerEmail = customerModel.getContactEmail();
        info.setCustomerEmail(customerEmail);
        info.setAuthenticatedShopperId(worldpayAuthenticatedShopperIdStrategy.getAuthenticatedShopperId(customerModel));
        info.setSavedCardPayment(true);
        info.setSessionId(getSessionService().getCurrentSession().getSessionId());
        info.setCustomerIPAddress(execution.getRemoteAddr());
        info.setAcceptHeader(execution.getHeader(ACCEPT_HEADER));
        info.setUserAgentHeader(execution.getHeader(USER_AGENT_HEADER));
        return info;
    }

    /**
     * (non-Javadoc)
     * @see de.hybris.platform.cscockpit.widgets.renderers.impl.CheckoutPaymentWidgetRenderer#handleOpenNewPaymentOptionClickEvent
     * (de.hybris.platform.cockpit.widgets.impl.DefaultListboxWidget, org.zkoss.zk.ui.event.Event, org.zkoss.zul.Div)
     */
    @Override
    protected void handleOpenNewPaymentOptionClickEvent(
            final DefaultListboxWidget<CheckoutPaymentWidgetModel, CheckoutController> widget, final Event event, final Div container) {
        try {
            if (successfulAuthorization(widget)) {
                widget.getWidgetController().canCreatePayments();

                Window window = createPopupWidget(widget, container);

                window.addEventListener(EVENT_ON_CLOSE, closeEvent -> handleRefreshCheckoutTabEvent(widget));
            }
        } catch (final ValidationException ex) {
            handleValidationException(widget, ex);
        }
    }

    protected Window createPopupWidget(DefaultListboxWidget<CheckoutPaymentWidgetModel, CheckoutController> widget, Div container) {
        return getPopupWidgetHelper().createPopupWidget(container, "csCheckoutCardPaymentWidgetConfig",
                "csCheckoutCardPaymentWidgetConfig-Popup", "csCheckoutPaymentPopup",
                LabelUtils.getLabel(widget, "popup.paymentOptionCreateTitle"), 625, null, null);
    }

    protected void handleValidationException(DefaultListboxWidget<CheckoutPaymentWidgetModel, CheckoutController> widget, ValidationException ex) {
        try {
            LOG.info("ValidationException thrown when creating new payment", ex);
            Messagebox.show(ex.getMessage(), LabelUtils.getLabel(widget, "failedToCreatePayment"), 1,
                    MESSAGE_BOX_CLASS);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
    }

    protected boolean successfulAuthorization(DefaultListboxWidget<CheckoutPaymentWidgetModel, CheckoutController> widget) {
        try {
            final WorldpayCardPaymentController controller = (WorldpayCardPaymentController) widget.getWidgetController();
            controller.redirectAuthorise();
        } catch (final WorldpayException e) {
            LOG.error(MessageFormat.format("WorldpayException: {0}", e.getMessage()), e);
            showFailedToAuthorizeMessage(widget, e);
            return false;
        }
        return true;
    }

    protected void showFailedToAuthorizeMessage(DefaultListboxWidget<CheckoutPaymentWidgetModel, CheckoutController> widget, WorldpayException e) {
        try {
            Messagebox.show(LabelUtils.getLabel(widget, "failedToAuthorizeMessage", new Object[0]), LabelUtils.getLabel(widget, "failedToAuthorizeTitle"), 1, MESSAGE_BOX_CLASS);
        } catch (InterruptedException e1) {
            LOG.warn(MessageFormat.format("Failed to show messagebox with error: {0}", e.getMessage()), e);
        }
    }

    protected void handleRefreshCheckoutTabEvent(final DefaultListboxWidget<CheckoutPaymentWidgetModel, CheckoutController> widget) {
        Map<String, Object> data = Collections.singletonMap("refresh", (Object) Boolean.TRUE);
        widget.getWidgetController().dispatchEvent(null, null, data);
    }

    @Required
    public void setWorldpayAuthenticatedShopperIdStrategy(WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategy) {
        this.worldpayAuthenticatedShopperIdStrategy = worldpayAuthenticatedShopperIdStrategy;
    }
}
