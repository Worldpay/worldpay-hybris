package com.worldpay.worldpaytests.orders;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

public class WorldpayPaymentInfoTestData {

    private static final String PAYMENT_PROVIDER = "Mockup";

    private ImpersonationService impersonationService;
    private CustomerAccountService customerAccountService;
    private CommonI18NService i18nService;


    public void createPaymentInfo(final CustomerModel customer, final String currencyIso, final CardInfo cardInfo,
                                  final BillingInfo billingInfo, final CMSSiteModel cmsSite) {

        // Impersonate site and customer
        final ImpersonationContext ctx = createImpersonationContext();
        ctx.setSite(cmsSite);
        ctx.setUser(customer);
        ctx.setCurrency(i18nService.getCurrency(currencyIso));
        impersonationService.executeInContext(ctx, () -> createPaymentInfoInContext(customer, cardInfo, billingInfo));
    }

    protected Object createPaymentInfoInContext(final CustomerModel customer, final CardInfo cardInfo, final BillingInfo billingInfo) {
        // Check if the card info already exists
        final List<CreditCardPaymentInfoModel> storedCards = customerAccountService.getCreditCardPaymentInfos(customer, true);
        if (!containsCardInfo(storedCards, cardInfo)) {

            // Create payment subscription
            final String customerTitleCode = customer.getTitle().getCode();
            final CreditCardPaymentInfoModel creditCardPaymentInfoModel = customerAccountService
                    .createPaymentSubscription(customer, cardInfo, billingInfo, customerTitleCode, PAYMENT_PROVIDER, true);
            // Make this the default payment option
            customerAccountService.setDefaultPaymentInfo(customer, creditCardPaymentInfoModel);
        }
        return null;
    }

    protected boolean containsCardInfo(final List<CreditCardPaymentInfoModel> storedCards, final CardInfo cardInfo) {
        if (storedCards != null && !storedCards.isEmpty() && cardInfo != null) {
            for (final CreditCardPaymentInfoModel storedCard : storedCards) {
                if (matchesCardInfo(storedCard, cardInfo)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean matchesCardInfo(final CreditCardPaymentInfoModel storedCard, final CardInfo cardInfo) {
        return storedCard.getType().equals(cardInfo.getCardType()) && StringUtils.equals(storedCard.getCcOwner(),
                cardInfo.getCardHolderFullName());
    }


    protected ImpersonationContext createImpersonationContext() {
        return new ImpersonationContext();
    }

    @Required
    public void setI18nService(CommonI18NService i18nService) {
        this.i18nService = i18nService;
    }

    @Required
    public void setImpersonationService(ImpersonationService impersonationService) {
        this.impersonationService = impersonationService;
    }

    @Required
    public void setCustomerAccountService(CustomerAccountService customerAccountService) {
        this.customerAccountService = customerAccountService;
    }
}
