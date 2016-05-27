package com.worldpay.converters;

import com.worldpay.service.response.CancelServiceResponse;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.payment.commands.result.VoidResult;

import java.util.Date;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.SUCCESFULL;

/**
 * Converter to be used to transform a {@link CancelServiceResponse} from Worldpay to an {@link VoidResult} in hybris in a payment transactions.
 */
public class WorldpayVoidServiceResponseConverter extends AbstractConverter<CancelServiceResponse, VoidResult> {

    @Override
    public void populate(final CancelServiceResponse cancelServiceResponse, final VoidResult target) {
        target.setRequestId(cancelServiceResponse.getOrderCode());
        target.setRequestTime(new Date());
        target.setTransactionStatus(ACCEPTED);
        target.setTransactionStatusDetails(SUCCESFULL);

    }
}
