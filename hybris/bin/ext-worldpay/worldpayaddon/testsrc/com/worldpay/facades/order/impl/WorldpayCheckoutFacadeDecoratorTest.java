package com.worldpay.facades.order.impl;

import com.worldpay.facades.order.data.WorldpayAPMPaymentInfoData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorservices.order.AcceleratorCheckoutService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayCheckoutFacadeDecoratorTest {

    private static final String PICKUP_POINT_OF_SERVICE_NAME = "pickupPointOfServiceName";
    private static final String PAYMENT_INFO = "paymentInfo";
    private static final String COUNTRY_ISO = "en";
    private static final String DELIVERY_MODE_CODE = "deliveryModeCode";
    private static final String ADDRESS_CODE = "addressCode";

    @InjectMocks
    private WorldpayCheckoutFacadeDecorator testObj = new WorldpayCheckoutFacadeDecorator();
    @SuppressWarnings ("PMD")
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    private AddressModel sourceMock;
    @Mock
    private CheckoutFlowFacade checkoutFlowFacadeMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private CartService cartServiceMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CartModel cartModelMock;
    @Mock
    private Converter<AddressModel, AddressData> addressConverterMock;
    @Mock
    private AddressData addressDataMock;
    @Mock
    private CartData cartDataMock;
    @Mock
    private CCPaymentInfoData ccPaymentInfoDataMock;
    @Mock
    private WorldpayAPMPaymentInfoData apmPaymentInfoDataMock;
    @Mock
    private CustomerModel customerModelMock;
    @Mock
    private AddressModel addressModelMock;
    @Mock
    private CartFacade cartFacadeMock;
    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategyMock;
    @Mock
    private PointOfServiceService pointOfServiceServiceMock;
    @Mock
    private AcceleratorCheckoutService acceleratorCheckoutService;
    @Mock
    private PointOfServiceModel pointOfServiceModelMock;
    @Mock
    private CommerceCartModification commerceCartModificationMock;

    @Before
    public void setup() throws CommerceCartModificationException {
        when(addressConverterMock.convert(sourceMock)).thenReturn(addressDataMock);
        when(checkoutFlowFacadeMock.hasCheckoutCart()).thenReturn(true);
        when(cartFacadeMock.hasSessionCart()).thenReturn(true);
        when(checkoutCustomerStrategyMock.getCurrentUserForCheckout()).thenReturn(customerModelMock);
        when(pointOfServiceServiceMock.getPointOfServiceForName(PICKUP_POINT_OF_SERVICE_NAME)).thenReturn(pointOfServiceModelMock);
        when(acceleratorCheckoutService.consolidateCheckoutCart(cartModelMock, pointOfServiceModelMock)).thenReturn(Collections.singletonList(commerceCartModificationMock));
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartFacadeMock.getSessionCart()).thenReturn(cartDataMock);
    }

    @Test
    public void getBillingAddressReturnsCartPaymentAddress() {
        when(cartModelMock.getPaymentAddress()).thenReturn(sourceMock);

        final AddressData result = testObj.getBillingAddress();

        verify(addressConverterMock).convert(sourceMock);
        assertEquals(addressDataMock, result);
    }

    @Test
    public void getBillingAddressReturnsNullIfNoCartFound() {
        when(cartModelMock.getPaymentAddress()).thenReturn(sourceMock);
        when(cartServiceMock.getSessionCart()).thenReturn(null);

        final AddressData result = testObj.getBillingAddress();

        verify(addressConverterMock, never()).convert(sourceMock);
        assertNull(result);
    }

    @Test
    public void getBillingAddressReturnsNullIfCartHasNoPaymentAddress() {
        when(cartModelMock.getPaymentAddress()).thenReturn(null);

        final AddressData result = testObj.getBillingAddress();

        verify(addressConverterMock, never()).convert(sourceMock);
        assertNull(result);
    }

    @Test
    public void shouldReturnTrueWhenCartIsNull() {
        when(checkoutFlowFacadeMock.getCheckoutCart()).thenReturn(null);

        final boolean result = testObj.hasNoPaymentInfo();

        assertTrue(result);
    }

    @Test
    public void shouldReturnTrueWhenCartIsNotNullAndHasNoPaymentInfos() {
        when(checkoutFlowFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(null);
        when(cartDataMock.getWorldpayAPMPaymentInfo()).thenReturn(null);

        final boolean result = testObj.hasNoPaymentInfo();

        assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenCartDataIsNotNullAndHasPaymentInfoAndAPMPaymentInfo() {
        when(checkoutFlowFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(ccPaymentInfoDataMock);
        when(cartDataMock.getWorldpayAPMPaymentInfo()).thenReturn(apmPaymentInfoDataMock);

        final boolean result = testObj.hasNoPaymentInfo();

        assertFalse(result);
    }

    @Test
    public void shouldReturnFalseWhenCartHasNoCCPaymentInfoAndHasAPMPaymentInfo() {
        when(checkoutFlowFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(cartDataMock.getPaymentInfo()).thenReturn(null);
        when(cartDataMock.getWorldpayAPMPaymentInfo()).thenReturn(apmPaymentInfoDataMock);

        final boolean result = testObj.hasNoPaymentInfo();

        assertFalse(result);
    }

    @Test
    public void shouldReturnFalseWhenCartHasNoAPMPaymentInfo() {
        when(checkoutFlowFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(cartDataMock.getWorldpayAPMPaymentInfo()).thenReturn(null);
        when(cartDataMock.getPaymentInfo()).thenReturn(ccPaymentInfoDataMock);

        final boolean result = testObj.hasNoPaymentInfo();

        assertFalse(result);
    }

    @Test
    public void getBillingAddressReturnsNullIfCartModelIsNull() {
        final AddressData billingAddress = testObj.getBillingAddress();
        assertNull(billingAddress);
    }

    @Test
    public void getBillingAddressReturnsNullIfAddressModelIsNull() {
        final AddressData billingAddress = testObj.getBillingAddress();
        assertNull(billingAddress);
    }

    @Test
    public void getBillingAddressReturnsAddressDataIfCartAndAddressIsNotNull() {
        when(cartModelMock.getPaymentAddress()).thenReturn(addressModelMock);
        when(addressConverterMock.convert(addressModelMock)).thenReturn(addressDataMock);

        final AddressData billingAddress = testObj.getBillingAddress();

        assertEquals(addressDataMock, billingAddress);
        assertNotNull(addressModelMock);
        verify(addressConverterMock).convert(addressModelMock);
    }

    @Test
    public void getCartModelReturnsNullWhenThereIsNoSessionCart() {
        when(checkoutFlowFacadeMock.hasCheckoutCart()).thenReturn(false);

        final CartModel cartModel = testObj.getSessionCart();

        assertNull(cartModel);
    }

    @Test
    public void setPaymentDetailsShouldAddPaymentAddressToCart() {
        when(cartModelMock.getPaymentInfo().getBillingAddress()).thenReturn(addressModelMock);
        when(checkoutFlowFacadeMock.setPaymentDetails(PAYMENT_INFO)).thenReturn(true);

        final boolean result = testObj.setPaymentDetails(PAYMENT_INFO);

        final InOrder inOrder = inOrder(checkoutFlowFacadeMock, cartModelMock,cartServiceMock);
        inOrder.verify(checkoutFlowFacadeMock).setPaymentDetails(PAYMENT_INFO);
        inOrder.verify(cartModelMock).setPaymentAddress(addressModelMock);
        inOrder.verify(cartServiceMock).saveOrder(cartModelMock);
        assertTrue(result);
    }

    @Test
    public void createPaymentSubscription() {
        testObj.createPaymentSubscription(ccPaymentInfoDataMock);
        verify(checkoutFlowFacadeMock).createPaymentSubscription(ccPaymentInfoDataMock);
    }

    @Test
    public void getCheckoutFlow() {
        testObj.getCheckoutFlow();
        verify(checkoutFlowFacadeMock).getCheckoutFlow();
    }

    @Test
    public void placeOrder() throws InvalidCartException {
        testObj.placeOrder();
        verify(checkoutFlowFacadeMock).placeOrder();
    }

    @Test
    public void getSupportedDeliveryAddresses() {
        testObj.getSupportedDeliveryAddresses(true);
        verify(checkoutFlowFacadeMock).getSupportedDeliveryAddresses(true);
    }

    @Test
    public void setCheapestDeliveryModeForCheckout() {
        testObj.setCheapestDeliveryModeForCheckout();
        verify(checkoutFlowFacadeMock).setCheapestDeliveryModeForCheckout();
    }

    @Test
    public void isTaxEstimationEnabledForCart() {
        testObj.isTaxEstimationEnabledForCart();
        verify(checkoutFlowFacadeMock).isTaxEstimationEnabledForCart();
    }

    @Test
    public void hasCheckoutCart() {
        testObj.hasCheckoutCart();
        verify(checkoutFlowFacadeMock).hasCheckoutCart();
    }

    @Test
    public void hasShippingItems() {
        testObj.hasShippingItems();
        verify(checkoutFlowFacadeMock).hasShippingItems();
    }

    @Test
    public void getConsolidatedPickupOptions() {
        testObj.getConsolidatedPickupOptions();
        verify(checkoutFlowFacadeMock).getConsolidatedPickupOptions();
    }

    @Test
    public void getSupportedDeliveryModes() {
        testObj.getSupportedDeliveryModes();
        verify(checkoutFlowFacadeMock).getSupportedDeliveryModes();
    }

    @Test
    public void removeDeliveryMode() {
        testObj.removeDeliveryMode();
        verify(checkoutFlowFacadeMock).removeDeliveryMode();
    }

    @Test
    public void getCountryForIsocode() {
        testObj.getCountryForIsocode(COUNTRY_ISO);
        verify(checkoutFlowFacadeMock).getCountryForIsocode(COUNTRY_ISO);
    }

    @Test
    public void setDeliveryMode() {
        testObj.setDeliveryMode(DELIVERY_MODE_CODE);
        verify(checkoutFlowFacadeMock).setDeliveryMode(DELIVERY_MODE_CODE);
    }

    @Test
    public void getDeliveryCountries() {
        testObj.getDeliveryCountries();
        verify(checkoutFlowFacadeMock).getDeliveryCountries();
    }

    @Test
    public void setDeliveryAddressIfAvailable() {
        testObj.setDeliveryAddressIfAvailable();
        verify(checkoutFlowFacadeMock).setDeliveryAddressIfAvailable();
    }

    @Test
    public void containsTaxValues() {
        testObj.containsTaxValues();
        verify(checkoutFlowFacadeMock).containsTaxValues();
    }

    @Test
    public void getSupportedCardTypes() {
        testObj.getSupportedCardTypes();
        verify(checkoutFlowFacadeMock).getSupportedCardTypes();
    }

    @Test
    public void setDeliveryModeIfAvailable() {
        testObj.setDeliveryModeIfAvailable();
        verify(checkoutFlowFacadeMock).setDeliveryModeIfAvailable();
    }

    @Test
    public void setDefaultPaymentInfoForCheckout() {
        testObj.setDefaultPaymentInfoForCheckout();
        verify(checkoutFlowFacadeMock).setDefaultPaymentInfoForCheckout();
    }

    @Test
    public void getSubscriptionPciOption() {
        testObj.getSubscriptionPciOption();
        verify(checkoutFlowFacadeMock).getSubscriptionPciOption();
    }

    @Test
    public void hasNoDeliveryAddress() {
        testObj.hasNoDeliveryAddress();
        verify(checkoutFlowFacadeMock).hasNoDeliveryAddress();
    }

    @Test
    public void isExpressCheckoutAllowedForCart() {
        testObj.isExpressCheckoutAllowedForCart();
        verify(checkoutFlowFacadeMock).isExpressCheckoutAllowedForCart();
    }

    @Test
    public void consolidateCheckoutCart() throws CommerceCartModificationException {
        testObj.consolidateCheckoutCart(PICKUP_POINT_OF_SERVICE_NAME);
        verify(checkoutFlowFacadeMock).consolidateCheckoutCart(PICKUP_POINT_OF_SERVICE_NAME);
    }

    @Test
    public void getBillingCountries() {
        testObj.getBillingCountries();
        verify(checkoutFlowFacadeMock).getBillingCountries();
    }

    @Test
    public void hasNoDeliveryMode() {
        testObj.hasNoDeliveryMode();
        verify(checkoutFlowFacadeMock).hasNoDeliveryMode();
    }

    @Test
    public void getDeliveryAddressForCode() {
        testObj.getDeliveryAddressForCode(ADDRESS_CODE);
        verify(checkoutFlowFacadeMock).getDeliveryAddressForCode(ADDRESS_CODE);
    }

    @Test
    public void setPaymentInfoIfAvailable() {
        testObj.setPaymentInfoIfAvailable();
        verify(checkoutFlowFacadeMock).setPaymentInfoIfAvailable();
    }

    @Test
    public void setDeliveryAddress() {
        testObj.setDeliveryAddress(addressDataMock);
        verify(checkoutFlowFacadeMock).setDeliveryAddress(addressDataMock);
    }

    @Test
    public void getAddressDataForId() {
        testObj.getAddressDataForId(ADDRESS_CODE, true);
        verify(checkoutFlowFacadeMock).getAddressDataForId(ADDRESS_CODE, true);
    }

    @Test
    public void isExpressCheckoutEnabledForStore() {
        testObj.isExpressCheckoutEnabledForStore();
        verify(checkoutFlowFacadeMock).isExpressCheckoutEnabledForStore();
    }

    @Test
    public void setDefaultDeliveryAddressForCheckout() {
        testObj.setDefaultDeliveryAddressForCheckout();
        verify(checkoutFlowFacadeMock).setDefaultDeliveryAddressForCheckout();
    }

    @Test
    public void performExpressCheckout() {
        testObj.performExpressCheckout();
        verify(checkoutFlowFacadeMock).performExpressCheckout();
    }

    @Test
    public void getCheckoutFlowGroupForCheckout() {
        testObj.getCheckoutFlowGroupForCheckout();
        verify(checkoutFlowFacadeMock).getCheckoutFlowGroupForCheckout();
    }

    @Test
    public void removeDeliveryAddress() {
        testObj.removeDeliveryAddress();
        verify(checkoutFlowFacadeMock).removeDeliveryAddress();
    }

    @Test
    public void hasPickUpItems() {
        testObj.hasPickUpItems();
        verify(checkoutFlowFacadeMock).hasPickUpItems();
    }

    @Test
    public void prepareCartForCheckout() {
        testObj.prepareCartForCheckout();
        verify(checkoutFlowFacadeMock).prepareCartForCheckout();
    }

    @Test
    public void hasValidCart() {
        testObj.hasValidCart();
        verify(checkoutFlowFacadeMock).hasValidCart();
    }
}