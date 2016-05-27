package com.worldpay.facades.impl;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.core.services.WorldpayBankConfigurationLookupService;
import com.worldpay.facades.BankConfigurationData;
import com.worldpay.facades.WorldpayBankConfigurationFacade;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.model.WorldpayBankConfigurationModel;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayBankConfigurationFacade implements WorldpayBankConfigurationFacade {

    private APMConfigurationLookupService apmConfigurationLookupService;
    private WorldpayBankConfigurationLookupService worldpayBankConfigurationLookupService;
    private Converter<WorldpayBankConfigurationModel, BankConfigurationData> bankConfigurationModelBankConfigurationDataConverter;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BankConfigurationData> getBankConfigurationForAPMCode(final String apmCode) {
        final List<WorldpayBankConfigurationModel> activeBankConfigurationsForCode = worldpayBankConfigurationLookupService.getActiveBankConfigurationsForCode(apmCode);
        return Converters.convertAll(activeBankConfigurationsForCode, bankConfigurationModelBankConfigurationDataConverter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBankTransferApm(final String paymentMethod) {
        final WorldpayAPMConfigurationModel apmConfigurationForCode = apmConfigurationLookupService.getAPMConfigurationForCode(paymentMethod);
        return apmConfigurationForCode == null ? false : apmConfigurationForCode.getBank();
    }

    @Required
    public void setWorldpayBankConfigurationLookupService(WorldpayBankConfigurationLookupService worldpayBankConfigurationLookupService) {
        this.worldpayBankConfigurationLookupService = worldpayBankConfigurationLookupService;
    }

    @Required
    public void setBankConfigurationModelBankConfigurationDataConverter(Converter<WorldpayBankConfigurationModel, BankConfigurationData> bankConfigurationModelBankConfigurationDataConverter) {
        this.bankConfigurationModelBankConfigurationDataConverter = bankConfigurationModelBankConfigurationDataConverter;
    }

    @Required
    public void setApmConfigurationLookupService(APMConfigurationLookupService apmConfigurationLookupService) {
        this.apmConfigurationLookupService = apmConfigurationLookupService;
    }
}
