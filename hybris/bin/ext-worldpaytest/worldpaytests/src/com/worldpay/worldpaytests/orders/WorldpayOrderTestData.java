package com.worldpay.worldpaytests.orders;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.text.MessageFormat;

public class WorldpayOrderTestData {
    private static final Logger LOG = Logger.getLogger(WorldpayOrderTestData.class);

    protected static final String WORLDPAY_PERFORMANCE_TEST_USER_UID = "worldpayperformancetestuser";
    protected static final String CURRENCY_ISO = "USD";
    protected static final String PAYMENT_PROVIDER = "Mockup";
    protected static final String WORLDPAY_PERFORMANCE_TEST_NUMBER_OF_ORDERS = "worldpay.performance.test.number.of.orders";
    protected static final String ELECTRONICS = "electronics";
    protected static final String PRODUCT_CODE = "872912";
    protected static final String SECURITY_CODE = "123";

    private CMSAdminSiteService cmsAdminSiteService;
    private ImpersonationService impersonationService;
    private CartFacade cartFacade;
    private CartService cartService;
    private CheckoutFacade checkoutFacade;
    private CommerceCheckoutService commerceCheckoutService;
    private ModelService modelService;
    private CommonI18NService i18nService;
    private BaseStoreService baseStoreService;
    private ConfigurationService configurationService;
    private WorldpayPaymentTransactionTestData worldpayPaymentTransactionTestData;
    private WorldpayPaymentInfoTestData worldpayPaymentInfoTestData;
    private WorldpayCustomerTestData worldpayCustomerTestData;
    private UserService userService;

    public void createPerformanceCronJobData() {
        LOG.info("Creating performanceTestData");
        cleanUpPreviousRuns();

        final CMSSiteModel cmsSite = cmsAdminSiteService.getSiteForId(ELECTRONICS);
        final AddressModel addressModel = worldpayCustomerTestData.createAddressModel();
        final CustomerModel customer = worldpayCustomerTestData.createCustomer(addressModel);
        worldpayPaymentInfoTestData.createPaymentInfo(customer, CURRENCY_ISO, worldpayCustomerTestData.createVisaCardInfo(), worldpayCustomerTestData.createUkBillingInfo(), cmsSite);

        final CurrencyModel currency = i18nService.getCurrency(CURRENCY_ISO);
        final BaseStoreModel currentBaseStore = baseStoreService.getBaseStoreForUid(ELECTRONICS);
        currentBaseStore.setPaymentProvider(PAYMENT_PROVIDER);

        final int numberOfOrders = configurationService.getConfiguration().getInt(WORLDPAY_PERFORMANCE_TEST_NUMBER_OF_ORDERS);
        for (int i = 0; i < numberOfOrders; i++) {
            createSampleOrder(cmsSite, customer, currency, addressModel);
        }

        worldpayPaymentTransactionTestData.setRequestIdsAndCreateOrderModifications(customer);
    }

    protected void cleanUpPreviousRuns() {
        if (userService.isUserExisting(WORLDPAY_PERFORMANCE_TEST_USER_UID)) {
            LOG.info("Cleaning up previous runs. This could take a while, depending on the number of orders created in the previous runs");
            final UserModel userForUID = userService.getUserForUID(WORLDPAY_PERFORMANCE_TEST_USER_UID);
            userForUID.getOrders().forEach(modelService::remove);
            modelService.remove(userForUID);
            LOG.info("Finished cleaning up previous runs");
        }
    }

    protected void createSampleOrder(final CMSSiteModel cmsSite, final CustomerModel customer, final CurrencyModel currency, final AddressModel addressModel) {
        final ImpersonationContext ctx = createImpersonationContext();
        ctx.setSite(cmsSite);
        ctx.setUser(customer);
        ctx.setCurrency(currency);
        impersonationService.executeInContext(ctx, () -> WorldpayOrderTestData.this.createOrderInContext(addressModel));
    }

    protected Object createOrderInContext(final AddressModel addressModel) throws ImpersonationService.Nothing {
        try {
            cartService.removeSessionCart();

            try {
                cartFacade.addToCart(PRODUCT_CODE, 1L, ELECTRONICS);
            } catch (CommerceCartModificationException e) {
                LOG.error("Failed to add to cart", e);
            }
            final CartModel sessionCart = cartService.getSessionCart();
            final CommerceCheckoutParameter parameter = createCheckoutParameter(addressModel, sessionCart);

            if (!commerceCheckoutService.setDeliveryAddress(parameter)) {
                LOG.error("Failed to set delivery address on cart");
            }

            if (sessionCart.getDeliveryAddress() == null) {
                LOG.error("Failed to set delivery address");
            }

            // Set delivery mode
            checkoutFacade.setDeliveryModeIfAvailable();

            // Set payment info
            checkoutFacade.setPaymentInfoIfAvailable();

            // Checkout
            checkoutFacade.authorizePayment(SECURITY_CODE);

            final OrderData orderData = checkoutFacade.placeOrder();
            if (orderData == null) {
                LOG.error("Failed to placeOrder");
            } else {
                LOG.info(MessageFormat.format("Created order [{0}]", orderData.getCode()));
            }
        } catch (final Exception e) {
            LOG.error("Exception in createSampleOrder", e);
        }
        return null;
    }

    protected CommerceCheckoutParameter createCheckoutParameter(final AddressModel addressModel, final CartModel sessionCart) {
        final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
        parameter.setEnableHooks(true);
        parameter.setCart(sessionCart);
        parameter.setAddress(addressModel);
        parameter.setIsDeliveryAddress(false);
        return parameter;
    }

    protected ImpersonationContext createImpersonationContext() {
        return new ImpersonationContext();
    }

    @Required
    public void setI18nService(final CommonI18NService i18nService) {
        this.i18nService = i18nService;
    }

    @Required
    public void setCmsAdminSiteService(final CMSAdminSiteService cmsAdminSiteService) {
        this.cmsAdminSiteService = cmsAdminSiteService;
    }

    @Required
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    @Required
    public void setImpersonationService(final ImpersonationService siteImpersonationService) {
        this.impersonationService = siteImpersonationService;
    }

    @Required
    public void setCartFacade(final CartFacade cartFacade) {
        this.cartFacade = cartFacade;
    }

    @Required
    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    @Required
    public void setCheckoutFacade(final CheckoutFacade checkoutFacade) {
        this.checkoutFacade = checkoutFacade;
    }

    @Required
    public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService) {
        this.commerceCheckoutService = commerceCheckoutService;
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    @Required
    public void setBaseStoreService(BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Required
    public void setWorldpayPaymentTransactionTestData(WorldpayPaymentTransactionTestData worldpayPaymentTransactionTestData) {
        this.worldpayPaymentTransactionTestData = worldpayPaymentTransactionTestData;
    }

    @Required
    public void setWorldpayPaymentInfoTestData(WorldpayPaymentInfoTestData worldpayPaymentInfoTestData) {
        this.worldpayPaymentInfoTestData = worldpayPaymentInfoTestData;
    }

    @Required
    public void setWorldpayCustomerTestData(WorldpayCustomerTestData worldpayCustomerTestData) {
        this.worldpayCustomerTestData = worldpayCustomerTestData;
    }
}
