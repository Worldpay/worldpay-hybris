package com.worldpay.service;

import com.worldpay.commands.WorldpaySubscriptionAuthorizeResult;
import com.worldpay.service.model.AuthorisedStatus;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.platform.payment.commands.result.AbstractResult;

/**
 * Interface that provides methods to update the AuthorisationResult of a transaction.
 */
public interface WorldpayAuthorisationResultService {

    /**
     * Sets the AbstractResult with a TransactionStatus ERROR and the TransactionStatusDetail as GENERAL_SYSTEM_ERROR
     * @param target the {@link AbstractResult} to update
     */
    void setAuthoriseResultAsError(final AbstractResult target);

    /**
     * Sets the AbstractResult with the proper TransactionStatus and TransactionStatusDetail correspinding to the AuthorisedStatus
     * @param target    the {@link AbstractResult} to update
     * @param status    the {@link AuthorisedStatus} obtained
     * @param orderCode the orderCode defining the transaction
     */
    void setAuthoriseResultByTransactionStatus(final AbstractResult target, final AuthorisedStatus status, final String orderCode);

    /**
     * Sets the {@link WorldpaySubscriptionAuthorizeResult} with the proper TransactionStatus, TransactionStatusDetail and the URL where the
     * customer was redirected to.
     * @param response
     * @param result
     */
    void setAuthorizeResultForAPM(final DirectAuthoriseServiceResponse response, final WorldpaySubscriptionAuthorizeResult result);
}
