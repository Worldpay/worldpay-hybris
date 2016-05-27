package com.worldpay.core.checkout;

import static org.mockito.Mockito.*;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayCheckoutServiceTest {

    @InjectMocks
    private DefaultWorldpayCheckoutService testObj;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private AddressModel addressModelMock;
    @Mock
    private ModelService modelServiceMock;

    @Test
    public void testSetPaymentAddress() throws Exception {

        testObj.setPaymentAddress(cartModelMock, addressModelMock);

        verify(modelServiceMock).save(cartModelMock);
        verify(modelServiceMock).refresh(cartModelMock);
        verify(cartModelMock).setPaymentAddress(addressModelMock);
    }
}