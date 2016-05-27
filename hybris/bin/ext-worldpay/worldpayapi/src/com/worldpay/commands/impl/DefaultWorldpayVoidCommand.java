
package com.worldpay.commands.impl;

import static de.hybris.platform.payment.dto.TransactionStatus.ERROR;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.COMMUNICATION_PROBLEM;
import static java.text.MessageFormat.format;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.converters.WorldpayVoidServiceResponseConverter;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.CancelServiceRequest;
import com.worldpay.service.response.CancelServiceResponse;
import de.hybris.platform.payment.commands.VoidCommand;
import de.hybris.platform.payment.commands.request.VoidRequest;
import de.hybris.platform.payment.commands.result.VoidResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.log4j.Logger;


/**
 * Default worldpay Cancel command.
 * <p>
 * Communicates through the worldpayServiceGateway to make the cancel call to worldpay
 * </p>
 */
public class DefaultWorldpayVoidCommand extends WorldpayCommand implements VoidCommand {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayVoidCommand.class);
    private Converter<CancelServiceResponse, VoidResult> voidServiceResponseConverter;

    /**
     * {@inheritDoc}
     *
     * @see de.hybris.platform.payment.commands.Command#perform(java.lang.Object)
     */
    @Override
    public VoidResult perform(final VoidRequest request) {
        try {
            final CancelServiceRequest cancelRequest = buildCancelServiceRequest(request.getRequestId());
            return cancel(cancelRequest);
        } catch (WorldpayException e) {
            LOG.error(format("Exception raised while issuing a cancelServiceRequest: [{0}]", e.getMessage()), e);
            return createErrorVoidResult();
        }
    }

    /**
     * Make the call through to worldpay to cancel the authorisation
     *
     * @param cancelServiceRequest service request object
     * @return VoidResult translated from the service response
     */
    private VoidResult cancel(final CancelServiceRequest cancelServiceRequest) throws WorldpayException {
        final WorldpayServiceGateway gateway = getWorldpayServiceGatewayInstance();
        final CancelServiceResponse cancelResponse = gateway.cancel(cancelServiceRequest);
        if (cancelResponse == null) {
            throw new WorldpayException("Response from worldpay is empty");
        }
        final VoidResult voidResult = voidServiceResponseConverter.convert(cancelResponse);
        voidResult.setRequestToken(cancelServiceRequest.getMerchantInfo().getMerchantCode());
        return voidResult;
    }

    protected VoidResult createErrorVoidResult() {
        final VoidResult result = new VoidResult();
        result.setTransactionStatus(ERROR);
        result.setTransactionStatusDetails(COMMUNICATION_PROBLEM);
        return result;
    }

    /**
     * Build the cancel service request
     *
     * @param worldpayOrderCode order code to be used in the service request
     * @return CancelServiceRequest object
     */
    private CancelServiceRequest buildCancelServiceRequest(final String worldpayOrderCode) throws WorldpayException {
        final MerchantInfo merchantInfo = getMerchantInfo(worldpayOrderCode);
        final WorldpayConfig config = getWorldpayConfigLookupService().lookupConfig();
        return CancelServiceRequest.createCancelRequest(config, merchantInfo, worldpayOrderCode);
    }

    public void setVoidServiceResponseConverter(WorldpayVoidServiceResponseConverter voidServiceResponseConverter) {
        this.voidServiceResponseConverter = voidServiceResponseConverter;
    }
}
