package com.worldpay.converters;

import com.worldpay.service.response.ServiceResponse;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.payment.commands.result.AbstractResult;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Abstract converter to be used to transform a {@link ServiceResponse} from Worldpay to an {@link AbstractResult} in hybris in a payment transactions.
 * @param <S> object that extends ServiceResponse
 * @param <T> object that extends AbstractResult
 */
public abstract class WorldpayAbstractServiceResponseConverter<S extends ServiceResponse, T extends AbstractResult> extends AbstractConverter<S, T> {

    /**
     * Returns a BigDecimal with the proper value after applying to the value returned from Worldpay the fraction digits from the {@link Currency} used.
     * @param value    contains the string value of the amount without indication of fraction digits.
     * @param currency the {@link Currency} used in the transaction.
     * @return a BigDecimal
     */
    protected BigDecimal getTotalAmount(final String value, final Currency currency) {
        return new BigDecimal(value).movePointLeft(currency.getDefaultFractionDigits());
    }
}
