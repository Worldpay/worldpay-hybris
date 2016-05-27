package com.worldpay.service.apm.strategy.impl;

import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.model.WorldpayCurrencyRangeModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class APMAvailabilityRangeStrategyTest {

    @InjectMocks
    private APMAvailabilityRangeStrategy testObj = new APMAvailabilityRangeStrategy();

    @Mock
    private WorldpayAPMConfigurationModel apmConfigurationMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CartModel cartModelMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WorldpayCurrencyRangeModel worldpayCurrencyRangeModelUkMock;

    @Before
    public void setup() {
        final HashSet<WorldpayCurrencyRangeModel> worldpayCurrencyRangeModels = new HashSet<>();
        worldpayCurrencyRangeModels.add(worldpayCurrencyRangeModelUkMock);

        when(apmConfigurationMock.getCurrencyRanges()).thenReturn(worldpayCurrencyRangeModels);
        when(worldpayCurrencyRangeModelUkMock.getMin()).thenReturn(10d);
        when(worldpayCurrencyRangeModelUkMock.getMax()).thenReturn(20d);
        when(worldpayCurrencyRangeModelUkMock.getCurrency().getIsocode()).thenReturn(Currency.getInstance(Locale.UK).getCurrencyCode());
    }

    @Test
    public void isAvailableReturnsTrueIfNoRangesConfigured() {
        when(apmConfigurationMock.getCurrencyRanges()).thenReturn(Collections.emptySet());

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertTrue(result);
    }

    @Test
    public void isAvailableReturnsTrueIfRangeIsNullInConfiguration() {
        when(apmConfigurationMock.getCurrencyRanges()).thenReturn(null);

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertTrue(result);
    }


    @Test
    public void isAvailableReturnsTrueIfApmConfigHasRangesButAreNotInTheCartCurrency() {
        when(cartModelMock.getCurrency().getIsocode()).thenReturn(Currency.getInstance(Locale.ITALY).getCurrencyCode());

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertTrue(result);
    }

    @Test
    public void isAvailableReturnsTrueIfApmConfigHasRangesInTheCartCurrencyAndWithinTheSpecifiedRange() {
        when(cartModelMock.getCurrency().getIsocode()).thenReturn(Currency.getInstance(Locale.UK).getCurrencyCode());
        when(cartModelMock.getTotalPrice()).thenReturn(15d);

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertTrue(result);
    }

    @Test
    public void isAvailableReturnsFalseIfApmConfigHasRangesInTheCartCurrencyAndBelowWithinTheSpecifiedRange() {
        when(cartModelMock.getCurrency().getIsocode()).thenReturn(Currency.getInstance(Locale.UK).getCurrencyCode());
        when(cartModelMock.getTotalPrice()).thenReturn(5d);

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertFalse(result);
    }

    @Test
    public void isAvailableReturnsFalseIfApmConfigHasRangesInTheCartCurrencyAndAboveWithinTheSpecifiedRange() {
        when(cartModelMock.getCurrency().getIsocode()).thenReturn(Currency.getInstance(Locale.UK).getCurrencyCode());
        when(cartModelMock.getTotalPrice()).thenReturn(25d);

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertFalse(result);
    }

    @Test
    public void isAvailableReturnsTrueIfApmConfigHasRangesInTheCartCurrencyEqualToHigherBoundWithinTheSpecifiedRange() {
        when(cartModelMock.getCurrency().getIsocode()).thenReturn(Currency.getInstance(Locale.UK).getCurrencyCode());
        when(cartModelMock.getTotalPrice()).thenReturn(20d);

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertTrue(result);
    }

    @Test
    public void isAvailableReturnsTrueIfApmConfigHasRangesInTheCartCurrencyEqualToLowerBoundWithinTheSpecifiedRange() {
        when(cartModelMock.getCurrency().getIsocode()).thenReturn(Currency.getInstance(Locale.UK).getCurrencyCode());
        when(cartModelMock.getTotalPrice()).thenReturn(10d);

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertTrue(result);
    }

    @Test
    public void isAvailableReturnsTrueIfApmConfigHasRangesInTheCartCurrencyWhereLowerRangeIsNullAndHigherIsNull() {
        when(cartModelMock.getCurrency().getIsocode()).thenReturn(Currency.getInstance(Locale.UK).getCurrencyCode());
        when(cartModelMock.getTotalPrice()).thenReturn(10d);
        when(worldpayCurrencyRangeModelUkMock.getMin()).thenReturn(null);
        when(worldpayCurrencyRangeModelUkMock.getMax()).thenReturn(null);

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertTrue(result);
    }
}