package com.worldpay.service.impl;

import com.worldpay.commands.WorldpaySubscriptionAuthorizeResult;
import com.worldpay.service.WorldpayAuthorisationResultService;
import com.worldpay.service.model.AuthorisedStatus;
import com.worldpay.service.model.RedirectReference;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.platform.payment.commands.result.AbstractResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import org.apache.log4j.Logger;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.dto.TransactionStatus.ERROR;
import static de.hybris.platform.payment.dto.TransactionStatus.REJECTED;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.GENERAL_SYSTEM_ERROR;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.REVIEW_NEEDED;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.SUCCESFULL;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.UNKNOWN_CODE;

/**
 * Default implementation of {@link WorldpayAuthorisationResultService}
 */
public class DefaultWorldpayAuthorisationResultService implements WorldpayAuthorisationResultService {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayAuthorisationResultService.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAuthoriseResultAsError(final AbstractResult target) {
        setSubscriptionAuthoriseResult(target, ERROR, GENERAL_SYSTEM_ERROR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAuthoriseResultByTransactionStatus(final AbstractResult target, final AuthorisedStatus status, final String orderCode) {
        switch (status) {
            case ERROR:
                // Default Error response
                LOG.warn("Error Response from worldpay for transaction: " + orderCode);
                setSubscriptionAuthoriseResult(target, ERROR, GENERAL_SYSTEM_ERROR);
                break;
            case AUTHORISED:
                // Transaction is Authorised
                setSubscriptionAuthoriseResult(target, ACCEPTED, SUCCESFULL);
                break;
            default:
                LOG.debug(status + " Response from worldpay for transaction: " + orderCode);
                setSubscriptionAuthoriseResult(target, REJECTED, UNKNOWN_CODE);
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAuthorizeResultForAPM(final DirectAuthoriseServiceResponse response,
                                         final WorldpaySubscriptionAuthorizeResult result) {
        setSubscriptionAuthoriseResult(result, REJECTED, REVIEW_NEEDED);
        final RedirectReference redirectReference = response.getRedirectReference();
        result.setPaymentRedirectRequired(true);
        result.setPaymentRedirectUrl(redirectReference.getUrl());
    }

    private void setSubscriptionAuthoriseResult(final AbstractResult result, final TransactionStatus status, final TransactionStatusDetails statusDetails) {
        result.setTransactionStatus(status);
        result.setTransactionStatusDetails(statusDetails);
    }
}
