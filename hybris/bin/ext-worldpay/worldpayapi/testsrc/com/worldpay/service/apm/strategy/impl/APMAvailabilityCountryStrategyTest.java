package com.worldpay.service.apm.strategy.impl;

import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.strategy.WorldpayDeliveryAddressStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

import static java.util.Locale.ITALY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class APMAvailabilityCountryStrategyTest {

    @InjectMocks
    private APMAvailabilityCountryStrategy testObj = new APMAvailabilityCountryStrategy();

    @Mock
    private WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategyMock;
    @Mock
    private WorldpayAPMConfigurationModel apmConfigurationMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private CountryModel countryModelUkMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private AddressModel deliveryAddressModelMock;

    @Test
    public void isAvailableReturnsTrueWhenNoCountriesSpecifiedInConfiguration() {
        when(apmConfigurationMock.getCountries()).thenReturn(Collections.emptySet());

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertTrue(result);
    }

    @Test
    public void isAvailableReturnsTrueWhenCountryListIsNullInConfiguration() {
        when(apmConfigurationMock.getCountries()).thenReturn(null);

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertTrue(result);
    }

    @Test
    public void isAvailableReturnsFalseWhenCountryNotSpecifiedInConfiguration() {
        final HashSet<CountryModel> countryModels = new HashSet<>();
        countryModels.add(countryModelUkMock);

        when(countryModelUkMock.getIsocode()).thenReturn(Locale.UK.getCountry());
        when(apmConfigurationMock.getCountries()).thenReturn(countryModels);
        when(worldpayDeliveryAddressStrategyMock.getDeliveryAddress(cartModelMock)).thenReturn(deliveryAddressModelMock);
        when(deliveryAddressModelMock.getCountry().getIsocode()).thenReturn(ITALY.getCountry());

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertFalse(result);
    }

    @Test
    public void isAvailableReturnsTrueWhenCountrySpecifiedInConfiguration() {
        final HashSet<CountryModel> countryModels = new HashSet<>();
        countryModels.add(countryModelUkMock);

        when(apmConfigurationMock.getCountries()).thenReturn(countryModels);
        when(countryModelUkMock.getIsocode()).thenReturn(Locale.UK.getCountry());
        when(worldpayDeliveryAddressStrategyMock.getDeliveryAddress(cartModelMock)).thenReturn(deliveryAddressModelMock);
        when(deliveryAddressModelMock.getCountry().getIsocode()).thenReturn(Locale.UK.getCountry());

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertTrue(result);
    }
}
