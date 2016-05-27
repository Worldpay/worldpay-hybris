package com.worldpay.strategy;

import de.hybris.platform.core.model.user.UserModel;

/**
 * Exposes methods to create authenticated shopper Ids to be sent when payment is made through a tokenised card.
 */
public interface WorldpayAuthenticatedShopperIdStrategy {

    /**
     * Returns a unique authenticated shopper ID.
     *
     * @param userModel {@link UserModel}
     * @return AuthenticatedShopperId
     */
    String getAuthenticatedShopperId(UserModel userModel);
}
