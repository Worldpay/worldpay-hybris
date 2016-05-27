package com.worldpay.checkout.steps.validation;

import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.AbstractCheckoutStepValidator;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class WorldpayCheckoutStepValidator extends AbstractCheckoutStepValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationResults validateOnEnter(RedirectAttributes redirectAttributes) {

        final CheckoutPciOptionEnum subscriptionPciOption = getCheckoutFlowFacade().getSubscriptionPciOption();
        if (!CheckoutPciOptionEnum.HOP.equals(subscriptionPciOption)) {
            LOG.error("unexpected PCI option for worldpay [" + subscriptionPciOption + "]");
            return ValidationResults.REDIRECT_TO_CART;
        }

        if (!getCheckoutFlowFacade().hasValidCart()) {
            LOG.info("Missing, empty or unsupported cart");
            return ValidationResults.REDIRECT_TO_CART;
        }

        if (getCheckoutFlowFacade().hasNoDeliveryAddress()) {
            GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
                    "checkout.multi.deliveryAddress.notprovided");
            return ValidationResults.REDIRECT_TO_DELIVERY_ADDRESS;
        }

        if (getCheckoutFlowFacade().hasNoDeliveryMode()) {
            GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
                    "checkout.multi.deliveryMethod.notprovided");
            return ValidationResults.REDIRECT_TO_DELIVERY_METHOD;
        }
        return ValidationResults.SUCCESS;
    }
}
