package com.worldpay.strategy.impl;

import com.google.common.base.Preconditions;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import org.apache.commons.lang3.StringUtils;

import static java.text.MessageFormat.format;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayAuthenticatedShopperIdStrategy implements WorldpayAuthenticatedShopperIdStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthenticatedShopperId(UserModel userModel) {
        Preconditions.checkNotNull(userModel, "The user is null");
        if (userModel instanceof CustomerModel) {
            final String customerID = ((CustomerModel) userModel).getCustomerID();
            return StringUtils.isNotBlank(customerID) ? customerID : ((CustomerModel) userModel).getOriginalUid();
        }
        throw new IllegalArgumentException(format("The user {0} is not of type Customer.", userModel));
    }
}
