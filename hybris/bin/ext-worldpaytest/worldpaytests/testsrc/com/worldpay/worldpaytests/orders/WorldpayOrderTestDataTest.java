package com.worldpay.worldpaytests.orders;

import de.hybris.bootstrap.annotations.UnitTest;
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
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.BusinessException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.worldpaytests.orders.WorldpayOrderTestData.*;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayOrderTestDataTest {

    private static final int CONFIGURED_NUMBER_OF_ORDERS = 3;
    private static final String ORDER_CODE = "orderCode";

    @Spy
    @InjectMocks
    private WorldpayOrderTestData testObj = new WorldpayOrderTestData();
    @Mock
    private CartModel cartModelMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private CurrencyModel currencyModelMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private CustomerModel customerModelMock;
    @Mock
    private CMSSiteModel cmsSiteMock;
    @Mock
    private AddressModel addressModelMock;
    @Mock
    private BaseStoreModel baseStoreMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;
    @Mock
    private CardInfo cardInfoMock;
    @Mock
    private BillingInfo ukBillingInfoMock;
    @Mock
    private CommonI18NService i18nServiceMock;
    @Mock
    private BaseStoreService baseStoreServiceMock;
    @Mock
    private CMSAdminSiteService cmsAdminSiteServiceMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private UserService userServiceMock;
    @Mock
    private ImpersonationContext impersonationContextMock;
    @Mock
    private ImpersonationService impersonationServiceMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private CartFacade cartFacadeMock;
    @Mock
    private CommerceCheckoutParameter commerceCheckoutParameterMock;
    @Mock
    private CommerceCheckoutService commerceCheckoutServiceMock;
    @Mock
    private CheckoutFacade checkoutFacadeMock;
    @Mock
    private OrderData orderDataMock;
    @Mock
    private WorldpayCustomerTestData worldpayCustomerTestDataMock;
    @Mock
    private WorldpayPaymentTransactionTestData worldpayPaymentTransactionTestDataMock;
    @Mock
    private WorldpayPaymentInfoTestData worldpayPaymentInfoTestDataMock;

    @Test
    public void createPerformanceCronJobDataShouldCreateConfiguredAmountOfOrders() throws BusinessException {
        when(i18nServiceMock.getCurrency(CURRENCY_ISO)).thenReturn(currencyModelMock);
        when(baseStoreServiceMock.getBaseStoreForUid(ELECTRONICS)).thenReturn(baseStoreMock);
        when(configurationServiceMock.getConfiguration().getInt(WORLDPAY_PERFORMANCE_TEST_NUMBER_OF_ORDERS)).thenReturn(CONFIGURED_NUMBER_OF_ORDERS);
        when(cmsAdminSiteServiceMock.getSiteForId(ELECTRONICS)).thenReturn(cmsSiteMock);
        when(worldpayCustomerTestDataMock.createAddressModel()).thenReturn(addressModelMock);
        when(worldpayCustomerTestDataMock.createCustomer(addressModelMock)).thenReturn(customerModelMock);
        when(worldpayCustomerTestDataMock.createUkBillingInfo()).thenReturn(ukBillingInfoMock);
        when(worldpayCustomerTestDataMock.createVisaCardInfo()).thenReturn(cardInfoMock);

        testObj.createPerformanceCronJobData();

        verify(testObj).cleanUpPreviousRuns();
        verify(worldpayCustomerTestDataMock).createAddressModel();
        verify(worldpayCustomerTestDataMock).createCustomer(addressModelMock);
        verify(testObj, times(CONFIGURED_NUMBER_OF_ORDERS)).createSampleOrder(cmsSiteMock, customerModelMock, currencyModelMock, addressModelMock);
        verify(worldpayPaymentInfoTestDataMock).createPaymentInfo(customerModelMock, CURRENCY_ISO, cardInfoMock, ukBillingInfoMock, cmsSiteMock);
        verify(worldpayPaymentTransactionTestDataMock).setRequestIdsAndCreateOrderModifications(customerModelMock);
        verify(worldpayCustomerTestDataMock).createUkBillingInfo();
        verify(worldpayCustomerTestDataMock).createVisaCardInfo();
        verify(baseStoreMock).setPaymentProvider(PAYMENT_PROVIDER);
    }

    @Test
    public void cleanUpPreviousRunsShouldDeleteUserAndOrdersWhenUserExists() {
        when(userServiceMock.isUserExisting(WORLDPAY_PERFORMANCE_TEST_USER_UID)).thenReturn(true);
        when(userServiceMock.getUserForUID(WORLDPAY_PERFORMANCE_TEST_USER_UID)).thenReturn(customerModelMock);
        when(customerModelMock.getOrders()).thenReturn(singletonList(orderModelMock));

        testObj.cleanUpPreviousRuns();

        verify(userServiceMock).getUserForUID(WORLDPAY_PERFORMANCE_TEST_USER_UID);
        verify(modelServiceMock).remove(orderModelMock);
        verify(modelServiceMock).remove(customerModelMock);
    }

    @Test
    public void cleanUpPreviousRunsShouldDoNothingWhenUserDoesNotExists() {
        when(userServiceMock.isUserExisting(WORLDPAY_PERFORMANCE_TEST_USER_UID)).thenReturn(false);

        testObj.cleanUpPreviousRuns();

        verify(userServiceMock, never()).getUserForUID(WORLDPAY_PERFORMANCE_TEST_USER_UID);
        verify(modelServiceMock, never()).remove(orderModelMock);
        verify(modelServiceMock, never()).remove(customerModelMock);
    }

    @Test
    public void createSampleOrderShouldSetTheContextAndExecuteInContext() {
        doReturn(impersonationContextMock).when(testObj).createImpersonationContext();

        testObj.createSampleOrder(cmsSiteMock, customerModelMock, currencyModelMock, addressModelMock);

        verify(impersonationServiceMock).executeInContext(eq(impersonationContextMock), any());
        verify(testObj).createImpersonationContext();
        verify(impersonationContextMock).setSite(cmsSiteMock);
        verify(impersonationContextMock).setUser(customerModelMock);
        verify(impersonationContextMock).setCurrency(currencyModelMock);
    }

    @Test
    public void shouldCreateCartAndPlaceOrder() throws CommerceCartModificationException, InvalidCartException {
        when(baseStoreMock.getUid()).thenReturn(ELECTRONICS);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        doReturn(commerceCheckoutParameterMock).when(testObj).createCheckoutParameter(addressModelMock, cartModelMock);
        when(commerceCheckoutServiceMock.setDeliveryAddress(commerceCheckoutParameterMock)).thenReturn(true);
        when(cartModelMock.getDeliveryAddress()).thenReturn(addressModelMock);
        when(checkoutFacadeMock.placeOrder()).thenReturn(orderDataMock);
        when(orderDataMock.getCode()).thenReturn(ORDER_CODE);

        testObj.createOrderInContext(addressModelMock);

        verify(cartServiceMock).removeSessionCart();
        verify(cartFacadeMock).addToCart(WorldpayOrderTestData.PRODUCT_CODE, 1L, ELECTRONICS);
        verify(cartServiceMock).getSessionCart();
        verify(testObj).createCheckoutParameter(addressModelMock, cartModelMock);
        verify(commerceCheckoutServiceMock).setDeliveryAddress(commerceCheckoutParameterMock);
        verify(checkoutFacadeMock).setDeliveryModeIfAvailable();
        verify(checkoutFacadeMock).setPaymentInfoIfAvailable();
        verify(checkoutFacadeMock).authorizePayment(SECURITY_CODE);
        verify(checkoutFacadeMock).placeOrder();
    }

    @Test
    public void shouldCreateCheckoutParameter() {

        final CommerceCheckoutParameter result = testObj.createCheckoutParameter(addressModelMock, cartModelMock);

        assertTrue(result.isEnableHooks());
        assertEquals(cartModelMock, result.getCart());
        assertEquals(addressModelMock, result.getAddress());
        assertFalse(result.isIsDeliveryAddress());
    }
}