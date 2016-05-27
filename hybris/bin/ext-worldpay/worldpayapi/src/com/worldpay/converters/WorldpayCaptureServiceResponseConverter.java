package com.worldpay.converters;

import com.worldpay.service.model.Amount;
import com.worldpay.service.response.CaptureServiceResponse;
import de.hybris.platform.payment.commands.result.CaptureResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;

import java.util.Currency;

/**
 * {@see WorldpayAbstractServiceResponseConverter}
 */
public class WorldpayCaptureServiceResponseConverter extends WorldpayAbstractServiceResponseConverter<CaptureServiceResponse, CaptureResult> {

    @Override
    public void populate(final CaptureServiceResponse captureServiceResponse, final CaptureResult captureResult) {
        final Amount amount = captureServiceResponse.getAmount();
        final String currencyCode = amount.getCurrencyCode();
        final Currency currency = Currency.getInstance(currencyCode);

        captureResult.setCurrency(currency);
        captureResult.setTotalAmount(getTotalAmount(amount.getValue(), currency));
        captureResult.setRequestId(captureServiceResponse.getOrderCode());
        captureResult.setRequestTime(new java.util.Date());
        captureResult.setTransactionStatus(TransactionStatus.ACCEPTED);
        captureResult.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL);
    }
}
