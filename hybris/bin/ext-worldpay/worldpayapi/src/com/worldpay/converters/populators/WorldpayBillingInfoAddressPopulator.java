package com.worldpay.converters.populators;

import com.worldpay.service.model.Address;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populator that fills the necessary details on a {@link Address} with the information of a {@link BillingInfo}
 */
public class WorldpayBillingInfoAddressPopulator implements Populator<BillingInfo, Address> {

    /**
     * Populates the data from the {@link BillingInfo} to a {@link Address}
     * @param source a {@link BillingInfo} from Worldpay
     * @param target a {@link Address} in hybris.
     * @throws ConversionException
     */
    @Override
    public void populate(final BillingInfo source, final Address target) throws ConversionException {
        target.setAddress1(source.getStreet1());
        target.setAddress2(source.getStreet2());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setCity(source.getCity());
        target.setCountryCode(source.getCountry());
        target.setState(source.getState());
        target.setTelephoneNumber(source.getPhoneNumber());
        target.setPostalCode(source.getPostalCode());
    }
}
