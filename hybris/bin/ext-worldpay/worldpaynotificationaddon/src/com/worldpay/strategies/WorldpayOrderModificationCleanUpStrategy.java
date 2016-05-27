package com.worldpay.strategies;

import com.worldpay.worldpaynotificationaddon.model.WorldpayOrderModificationModel;

/**
 * Worldpay Order Modification Clean Up Strategy interface.
 * The strategy is responsible for cleaning up {@link WorldpayOrderModificationModel}
 */
public interface WorldpayOrderModificationCleanUpStrategy {

    /**
     * Cleans up Worldpay Order Modifications created before specified time.
     *
     * @param days number of days to wait until the order modification is processed
     */
    void doCleanUp(int days);

}
