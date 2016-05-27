package com.worldpay.commands.impl;

import static de.hybris.platform.payment.dto.TransactionStatus.ERROR;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.COMMUNICATION_PROBLEM;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Currency;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.Date;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.CaptureServiceRequest;
import com.worldpay.service.response.CaptureServiceResponse;
import de.hybris.platform.payment.commands.CaptureCommand;
import de.hybris.platform.payment.commands.request.CaptureRequest;
import de.hybris.platform.payment.commands.result.CaptureResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default Worldpay Capture command.
 * <p>
 * Communicates through the WorldpayServiceGateway to make the capture call to Worldpay
 * </p>
 */
public class DefaultWorldpayCaptureCommand extends WorldpayCommand implements CaptureCommand {
    private static final Logger LOG = Logger.getLogger(DefaultWorldpayCaptureCommand.class);

    private Converter<CaptureServiceResponse, CaptureResult> captureServiceResponseConverter;

    /**
     * {@inheritDoc}
     *
     * @see de.hybris.platform.payment.commands.Command#perform(java.lang.Object)
     */
    @Override
    public CaptureResult perform(final CaptureRequest request) {
        final String merchantCode = request.getRequestToken();
        final String orderCode = request.getRequestId();
        final BigDecimal amount = request.getTotalAmount();
        final Currency currency = request.getCurrency();

        try {
            final CaptureServiceRequest captureRequest = buildCaptureServiceRequest(orderCode, amount, currency);
            return capture(captureRequest);
        } catch (WorldpayException e) {
            LOG.error(MessageFormat.format("Error during capture of payment with ordercode [{0}] to merchant [{1}]", orderCode, merchantCode), e);
            return createErrorCaptureResult();
        }
    }

    private CaptureResult createErrorCaptureResult() {
        final CaptureResult captureResult = new CaptureResult();
        captureResult.setTransactionStatus(ERROR);
        captureResult.setTransactionStatusDetails(COMMUNICATION_PROBLEM);
        return captureResult;
    }

    /**
     * Make the call through to Worldpay to capture the funds
     *
     * @param captureRequest service request object
     * @return CaptureResult translated from the service response
     */
    private CaptureResult capture(final CaptureServiceRequest captureRequest) throws WorldpayException {

        final WorldpayServiceGateway gateway = getWorldpayServiceGatewayInstance();
        final CaptureServiceResponse captureResponse = gateway.capture(captureRequest);
        if (captureResponse == null) {
            throw new WorldpayException("Response from worldpay is empty");
        }

        final CaptureResult captureResult = getCaptureServiceResponseConverter().convert(captureResponse);
        captureResult.setRequestToken(captureRequest.getMerchantInfo().getMerchantCode());
        return captureResult;
    }

    /**
     * Build the capture service request
     *
     * @param worldpayOrderCode order code to be used in the service request
     * @param captureAmount     amount to be captured
     * @param currency          currency of the transaction
     * @return CaptureServiceRequest object
     */
    private CaptureServiceRequest buildCaptureServiceRequest(final String worldpayOrderCode, final BigDecimal captureAmount, final Currency currency) throws WorldpayException {
        final WorldpayConfig config = getWorldpayConfigLookupService().lookupConfig();

        final Amount amount = getWorldpayOrderService().createAmount(currency, captureAmount.doubleValue());
        final Date date = new Date(new java.util.Date());

        final MerchantInfo merchantInfo = getMerchantInfo(worldpayOrderCode);
        return CaptureServiceRequest.createCaptureRequest(config, merchantInfo, worldpayOrderCode, amount, date);
    }

    public Converter<CaptureServiceResponse, CaptureResult> getCaptureServiceResponseConverter() {
        return captureServiceResponseConverter;
    }

    @Required
    public void setCaptureServiceResponseConverter(Converter<CaptureServiceResponse, CaptureResult> captureServiceResponseConverter) {
        this.captureServiceResponseConverter = captureServiceResponseConverter;
    }
}
