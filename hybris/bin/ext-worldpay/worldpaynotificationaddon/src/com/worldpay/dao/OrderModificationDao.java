package com.worldpay.dao;

import com.worldpay.worldpaynotificationaddon.model.WorldpayOrderModificationModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;

import java.util.Date;
import java.util.List;

/**
 * Order Modification DAO interface. The DAO is responsible for retrieving order modifications.
 */
public interface OrderModificationDao {

    /**
     * Finds unprocessed order modifications by payment transaction type
     *
     * @param paymentTransactionType {@link PaymentTransactionType}
     * @return the list of {@link WorldpayOrderModificationModel}
     */
    List<WorldpayOrderModificationModel> findUnprocessedOrderModificationsByType(final PaymentTransactionType paymentTransactionType);

    /**
     * Finds unprocessed and not notified order modifications created before the given date.
     *
     * @param date the latest creation date
     * @return the list of {@link WorldpayOrderModificationModel}
     */
    List<WorldpayOrderModificationModel> findUnprocessedAndNotNotifiedOrderModificationsBeforeDate(final Date date);

    /**
     * Finds processed order modifications created before the given date.
     *
     * @param date the latest creation date
     * @return the list of {@link WorldpayOrderModificationModel}
     */
    List<WorldpayOrderModificationModel> findProcessedOrderModificationsBeforeDate(final Date date);

    /**
     * Finds existing modifications similar to the passed as parameter.
     *
     * @param worldpayOrderModificationModel the latest creation date
     * @return the list of {@link WorldpayOrderModificationModel}
     */
    List<WorldpayOrderModificationModel> getExistingModifications(final WorldpayOrderModificationModel worldpayOrderModificationModel);
}
