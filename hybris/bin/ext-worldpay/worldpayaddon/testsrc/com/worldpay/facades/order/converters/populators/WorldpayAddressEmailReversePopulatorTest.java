package com.worldpay.facades.order.converters.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayAddressEmailReversePopulatorTest {

    public static final String EMAIL_ADDRESS = "test@test.com";
    @InjectMocks
    private WorldpayAddressEmailReversePopulator testObj = new WorldpayAddressEmailReversePopulator();
    @Mock
    private AddressData addressDataMock;
    @Mock
    private AddressModel addressModelMock;

    @Test
    public void testPopulateEmail() {
        when(addressDataMock.getEmail()).thenReturn(EMAIL_ADDRESS);

        testObj.populate(addressDataMock, addressModelMock);

        verify(addressModelMock).setEmail(EMAIL_ADDRESS);
    }
}