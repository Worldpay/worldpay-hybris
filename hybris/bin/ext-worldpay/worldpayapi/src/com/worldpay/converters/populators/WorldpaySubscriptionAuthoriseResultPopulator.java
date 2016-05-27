package com.worldpay.converters.populators;

import com.worldpay.commands.WorldpaySubscriptionAuthorizeResult;
import com.worldpay.service.WorldpayAuthorisationResultService;
import com.worldpay.service.model.AuthorisedStatus;
import com.worldpay.service.model.ErrorDetail;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Populator that fills the necessary details on a {@link WorldpaySubscriptionAuthorizeResult} with the information of a {@link DirectAuthoriseServiceResponse}
 */
public class WorldpaySubscriptionAuthoriseResultPopulator implements Populator<DirectAuthoriseServiceResponse, WorldpaySubscriptionAuthorizeResult> {

    private static final Logger LOG = Logger.getLogger(WorldpaySubscriptionAuthoriseResultPopulator.class);

    private WorldpayAuthorisationResultService worldpayAuthorisationResultService;

    /**
     * Populates the data from the {@link DirectAuthoriseServiceResponse} to a {@link WorldpaySubscriptionAuthorizeResult}
     * @param source a {@link DirectAuthoriseServiceResponse} from Worldpay
     * @param target a {@link WorldpaySubscriptionAuthorizeResult} in hybris.
     * @throws ConversionException
     */
    @Override
    public void populate(final DirectAuthoriseServiceResponse source, final WorldpaySubscriptionAuthorizeResult target) throws ConversionException {

        final String orderCode = source.getOrderCode();
        final PaymentReply paymentReply = source.getPaymentReply();
        if (paymentReply == null) {
            // If Alternate Payment, then set redirect Url
            if (source.getRedirectReference() != null) {
                worldpayAuthorisationResultService.setAuthorizeResultForAPM(source, target);
            } else {
                LOG.warn("No PaymentReply in response from worldpay");
                final ErrorDetail errorDetail = source.getErrorDetail();
                if (errorDetail != null) {
                    LOG.error("Error message from Worldpay: " + errorDetail.getMessage());
                }
                worldpayAuthorisationResultService.setAuthoriseResultAsError(target);
            }
        } else {
            final AuthorisedStatus status = paymentReply.getAuthStatus();
            worldpayAuthorisationResultService.setAuthoriseResultByTransactionStatus(target, status, orderCode);
        }
    }

    @Required
    public void setWorldpayAuthorisationResultService(final WorldpayAuthorisationResultService worldpayAuthorisationResultService) {
        this.worldpayAuthorisationResultService = worldpayAuthorisationResultService;
    }
}
