package com.worldpay.strategy.impl;

import com.worldpay.exception.WorldpayConfigurationException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayDeliveryAddressStrategyTest {

    @InjectMocks
    private DefaultWorldpayDeliveryAddressStrategy testObj = new DefaultWorldpayDeliveryAddressStrategy();

    @Mock
    private CartModel cartModelMock;
    @Mock
    private AddressModel deliveryAddressModelMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private AbstractOrderEntryModel orderEntryModelMock;
    @Mock
    private AddressModel pickupAddressModelMock;

    @Test
    public void shouldReturnDeliveryAddress() throws Exception {
        when(cartModelMock.getDeliveryAddress()).thenReturn(deliveryAddressModelMock);

        final AddressModel result = testObj.getDeliveryAddress(cartModelMock);

        assertEquals(deliveryAddressModelMock, result);
    }

    @Test
    public void shouldReturnPickUpAddressIfDeliveryAddressIsNull() throws WorldpayConfigurationException {
        when(cartModelMock.getDeliveryAddress()).thenReturn(null);
        when(cartModelMock.getEntries()).thenReturn(singletonList(orderEntryModelMock));
        when(orderEntryModelMock.getDeliveryPointOfService().getAddress()).thenReturn(pickupAddressModelMock);

        final AddressModel result = testObj.getDeliveryAddress(cartModelMock);

        assertEquals(pickupAddressModelMock, result);
    }
}