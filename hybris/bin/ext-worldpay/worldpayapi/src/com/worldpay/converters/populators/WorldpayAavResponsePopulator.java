package com.worldpay.converters.populators;

import com.worldpay.model.WorldpayAavResponseModel;
import com.worldpay.service.model.PaymentReply;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populator that fills the necessary details on a {@link WorldpayAavResponseModel} with the information of a {@link PaymentReply}
 */
public class WorldpayAavResponsePopulator implements Populator<PaymentReply, WorldpayAavResponseModel> {

    /**
     * Populates the data from the {@link PaymentReply} to a {@link WorldpayAavResponseModel}
     * @param source a {@link PaymentReply} from Worldpay
     * @param target a {@link WorldpayAavResponseModel} in hybris.
     * @throws ConversionException
     */
    @Override
    public void populate(final PaymentReply source, final WorldpayAavResponseModel target) throws ConversionException {
        target.setAavAddressResultCode(source.getAavAddressResultCode());
        target.setAavCardholderNameResultCode(source.getAavCardholderNameResultCode());
        target.setAavEmailResultCode(source.getAavEmailResultCode());
        target.setAavPostcodeResultCode(source.getAavPostcodeResultCode());
        target.setAavTelephoneResultCode(source.getAavTelephoneResultCode());
    }
}
