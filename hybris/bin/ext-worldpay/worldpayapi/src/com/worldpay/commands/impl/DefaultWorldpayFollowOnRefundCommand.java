
package com.worldpay.commands.impl;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.RefundServiceRequest;
import com.worldpay.service.response.RefundServiceResponse;
import de.hybris.platform.payment.commands.FollowOnRefundCommand;
import de.hybris.platform.payment.commands.request.FollowOnRefundRequest;
import de.hybris.platform.payment.commands.result.RefundResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Currency;

import static de.hybris.platform.payment.dto.TransactionStatus.ERROR;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.COMMUNICATION_PROBLEM;


/**
 * Default Worldpay Refund Follow On command.
 * <p>
 * Communicates through the WorldpayServiceGateway to make the refund call to Worldpay
 * </p>
 */
public class DefaultWorldpayFollowOnRefundCommand extends WorldpayCommand implements FollowOnRefundCommand<FollowOnRefundRequest> {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayFollowOnRefundCommand.class);

    private Converter<RefundServiceResponse, RefundResult> refundServiceResponseConverter;

    /**
     * {@inheritDoc}
     *
     * @see de.hybris.platform.payment.commands.Command#perform(java.lang.Object)
     */
    @Override
    public RefundResult perform(final FollowOnRefundRequest request) {
        final String orderCode = request.getRequestId();
        final BigDecimal amount = request.getTotalAmount();
        final Currency currency = request.getCurrency();
        try {
            final RefundServiceRequest refundRequest = buildRefundServiceRequest(orderCode, amount, currency, request.getMerchantTransactionCode());
            return refund(refundRequest);
        } catch (WorldpayException e) {
            LOG.error(MessageFormat.format("Exception raised while issuing a refundRequest: [{0}]", e.getMessage()), e);
            return createErrorRefundResult();
        }
    }

    private RefundResult createErrorRefundResult() {
        final RefundResult refundResult = new RefundResult();
        refundResult.setTransactionStatus(ERROR);
        refundResult.setTransactionStatusDetails(COMMUNICATION_PROBLEM);
        return refundResult;
    }

    /**
     * Make the call through to Worldpay to refund the customer
     *
     * @param refundRequest service request object
     * @return RefundResult translated from the service response
     */
    private RefundResult refund(final RefundServiceRequest refundRequest) throws WorldpayException {

        final WorldpayServiceGateway gateway = getWorldpayServiceGatewayInstance();
        final RefundServiceResponse refundResponse = gateway.refund(refundRequest);
        if (refundResponse == null) {
            throw new WorldpayException("Response from worldpay is empty");
        }
        final RefundResult refundResult = refundServiceResponseConverter.convert(refundResponse);
        refundResult.setRequestToken(refundRequest.getMerchantInfo().getMerchantCode());
        return refundResult;
    }

    /**
     * Build the refund service request
     *
     * @param worldpayOrderCode order code to be used in the service request
     * @param refundAmount      amount to be captured
     * @param currency          currency of the transaction
     * @param returnReference   code of the return request associated to the refund
     * @return RefundServiceRequest object
     */
    protected RefundServiceRequest buildRefundServiceRequest(final String worldpayOrderCode, final BigDecimal refundAmount,
                                                             final Currency currency, final String returnReference) throws WorldpayException {
        final WorldpayConfig config = getWorldpayConfigLookupService().lookupConfig();
        final Amount amount = getWorldpayOrderService().createAmount(currency, refundAmount.doubleValue());
        final MerchantInfo merchantInfo = getMerchantInfo(worldpayOrderCode);
        return RefundServiceRequest.createRefundRequest(config, merchantInfo, worldpayOrderCode, amount, returnReference, Boolean.FALSE);
    }

    @Required
    public void setRefundServiceResponseConverter(Converter<RefundServiceResponse, RefundResult> refundServiceResponseConverter) {
        this.refundServiceResponseConverter = refundServiceResponseConverter;
    }
}
