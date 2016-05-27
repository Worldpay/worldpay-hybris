package com.worldpay.converters;

import com.worldpay.service.model.Amount;
import com.worldpay.service.response.RefundServiceResponse;
import de.hybris.platform.payment.commands.result.RefundResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;

import java.util.Currency;

/**
 * {@see WorldpayAbstractServiceResponseConverter}
 */
public class WorldpayRefundServiceResponseConverter extends WorldpayAbstractServiceResponseConverter<RefundServiceResponse, RefundResult> {

    @Override
    public void populate(final RefundServiceResponse refundServiceResponse, final RefundResult refundResult) {
        final Amount amount = refundServiceResponse.getAmount();
        final String currencyCode = amount.getCurrencyCode();
        final Currency currency = Currency.getInstance(currencyCode);
        refundResult.setCurrency(currency);
        refundResult.setTotalAmount(getTotalAmount(amount.getValue(), currency));
        refundResult.setRequestId(refundServiceResponse.getOrderCode());
        refundResult.setRequestTime(new java.util.Date());
        refundResult.setTransactionStatus(TransactionStatus.ACCEPTED);
        refundResult.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL);
    }
}
