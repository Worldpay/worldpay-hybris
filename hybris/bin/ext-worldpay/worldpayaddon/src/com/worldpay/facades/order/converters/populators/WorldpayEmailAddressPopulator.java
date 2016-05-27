package com.worldpay.facades.order.converters.populators;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class WorldpayEmailAddressPopulator implements Populator<AddressModel, AddressData> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final AddressModel source, final AddressData target) throws ConversionException {
        target.setEmail(source.getEmail());
    }
}
