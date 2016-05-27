package com.worldpay.service.apm.strategy.impl;

import com.worldpay.model.WorldpayAPMConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;

import static java.util.Locale.ITALY;
import static java.util.Locale.UK;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class APMAvailabilityCurrencyStrategyTest {

    @InjectMocks
    private APMAvailabilityCurrencyStrategy testObj = new APMAvailabilityCurrencyStrategy();

    @Mock
    private WorldpayAPMConfigurationModel apmConfigurationMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CartModel cartModelMock;
    @Mock
    private CurrencyModel currencyModelGBPMock;

    @Test
    public void isAvailableReturnsTrueWhenNoCurrenciesSpecifiedInConfiguration() {
        when(apmConfigurationMock.getCurrencies()).thenReturn(Collections.emptySet());

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertTrue(result);
    }

    @Test
    public void isAvailableReturnsTrueWhenCurrencyListIsNullInConfiguration() {
        when(apmConfigurationMock.getCurrencies()).thenReturn(null);

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertTrue(result);
    }

    @Test
    public void isAvailableReturnsFalseWhenCartCurrencyIsNotSpecifiedInConfiguration() {
        final HashSet<CurrencyModel> currencyModels = new HashSet<>();
        currencyModels.add(currencyModelGBPMock);

        when(apmConfigurationMock.getCurrencies()).thenReturn(currencyModels);
        when(currencyModelGBPMock.getIsocode()).thenReturn(Currency.getInstance(UK).getCurrencyCode());
        when(cartModelMock.getCurrency().getIsocode()).thenReturn(Currency.getInstance(ITALY).getCurrencyCode());

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertFalse(result);
    }

    @Test
    public void isAvailableReturnsTrueWhenCountrySpecifiedInConfiguration() {
        final HashSet<CurrencyModel> currencyModels = new HashSet<>();
        currencyModels.add(currencyModelGBPMock);

        when(apmConfigurationMock.getCurrencies()).thenReturn(currencyModels);
        when(currencyModelGBPMock.getIsocode()).thenReturn(Currency.getInstance(UK).getCurrencyCode());
        when(cartModelMock.getCurrency().getIsocode()).thenReturn(Currency.getInstance(UK).getCurrencyCode());

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertTrue(result);
    }
}