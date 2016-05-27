package com.worldpay.converters.populators;

import com.worldpay.facades.BankConfigurationData;
import com.worldpay.model.WorldpayBankConfigurationModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populator that adds the bankCode and bankName properties from the {@link WorldpayBankConfigurationModel} to the {@link BankConfigurationData}
 */
public class WorldpayBankConfigurationPopulator implements Populator<WorldpayBankConfigurationModel, BankConfigurationData> {

    @Override
    public void populate(final WorldpayBankConfigurationModel worldpayBankConfigurationModel, final BankConfigurationData bankConfigurationData) throws ConversionException {
        bankConfigurationData.setBankCode(worldpayBankConfigurationModel.getCode());
        bankConfigurationData.setBankName(worldpayBankConfigurationModel.getName());
    }
}
