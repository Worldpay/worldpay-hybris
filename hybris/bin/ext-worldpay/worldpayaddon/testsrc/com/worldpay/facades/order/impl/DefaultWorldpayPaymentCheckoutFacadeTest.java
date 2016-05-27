package com.worldpay.facades.order.impl;

import com.worldpay.core.checkout.WorldpayCheckoutService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayPaymentCheckoutFacadeTest {

    public static final String ADDRESS_DATA_ID = "0";
    @InjectMocks
    private DefaultWorldpayPaymentCheckoutFacade testObj = new DefaultWorldpayPaymentCheckoutFacade();
    @Mock
    private AddressData addressDataMock;
    @Mock
    private CheckoutFacade checkoutFacade;
    @Mock
    private CartService cartService;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private WorldpayCheckoutService worldpayCheckoutServiceMock;
    @Mock
    private AddressModel addressModelMock;
    @Mock
    private DeliveryService deliveryServiceMock;
    @Mock
    private AddressModel paymentAddressMock;


    @Before
    public void setUp() {
        when(checkoutFacade.hasCheckoutCart()).thenReturn(true);
        when(cartService.getSessionCart()).thenReturn(cartModelMock);
        when(deliveryServiceMock.getSupportedDeliveryAddressesForOrder(cartModelMock, false)).thenReturn(Collections.singletonList(addressModelMock));
    }

    @Test
    public void setBillingDetailsSetsPaymentAddressWhenPkMatchesAddressDataId() {
        when(addressDataMock.getId()).thenReturn(ADDRESS_DATA_ID);
        when(addressModelMock.getPk()).thenReturn(PK.NULL_PK);

        testObj.setBillingDetails(addressDataMock);

        Mockito.verify(worldpayCheckoutServiceMock).setPaymentAddress(cartModelMock, addressModelMock);
    }

    @Test
    public void setBillingDetailsDoesNotSetPaymentAddressWhenPkDoesNotMatchesAddressDataId() {
        when(addressDataMock.getId()).thenReturn(ADDRESS_DATA_ID);
        when(addressModelMock.getPk()).thenReturn(PK.BIG_PK);

        testObj.setBillingDetails(addressDataMock);

        Mockito.verify(worldpayCheckoutServiceMock, never()).setPaymentAddress(cartModelMock, addressModelMock);
    }

    @Test
    public void setBillingDetailsDoesNotSetPaymentAddressWhenNoDeliveryAddressesReturned() {
        when(addressDataMock.getId()).thenReturn(ADDRESS_DATA_ID);
        when(deliveryServiceMock.getSupportedDeliveryAddressesForOrder(cartModelMock, false)).thenReturn(Collections.EMPTY_LIST);

        testObj.setBillingDetails(addressDataMock);

        Mockito.verify(worldpayCheckoutServiceMock, never()).setPaymentAddress(cartModelMock, addressModelMock);
    }

    @Test (expected = IllegalArgumentException.class)
    public void setBillingDetailsThrowsIllegalArgumentExceptionWhenAddressDataIdIsNull() {
        when(addressDataMock.getId()).thenReturn(null);
        when(addressModelMock.getPk()).thenReturn(PK.BIG_PK);

        testObj.setBillingDetails(addressDataMock);
    }

    @Test
    public void hasBillingDetailsReturnsFalseWhenCartIsNull() {
        when(cartService.getSessionCart()).thenReturn(null);
        final boolean result = testObj.hasBillingDetails();
        assertFalse(result);
    }

    @Test
    public void hasBillingDetailsReturnsFalseWhenPaymentAddressIsNull() {
        when(cartModelMock.getPaymentAddress()).thenReturn(null);
        final boolean result = testObj.hasBillingDetails();
        assertFalse(result);
    }

    @Test
    public void hasBillingDetailsReturnsTrueWhenCartModelAndPaymentAddressAreNotNull() {
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressMock);
        final boolean result = testObj.hasBillingDetails();
        assertTrue(result);
    }
}