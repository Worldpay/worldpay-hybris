package com.worldpay.core.services.impl;

import com.worldpay.core.dao.WorldpayBankConfigurationDao;
import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.core.services.WorldpayBankConfigurationLookupService;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.model.WorldpayBankConfigurationModel;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayBankConfigurationLookupService implements WorldpayBankConfigurationLookupService {

    private WorldpayBankConfigurationDao worldpayBankConfigurationDao;
    private APMConfigurationLookupService apmConfigurationLookupService;

    /**
     * {@inheritDoc}
     * @param apmCode string representing the APM code
     * @return
     */
    @Override
    public List<WorldpayBankConfigurationModel> getActiveBankConfigurationsForCode(final String apmCode) {
        final WorldpayAPMConfigurationModel apmConfigurationForCode = apmConfigurationLookupService.getAPMConfigurationForCode(apmCode);
        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(WorldpayBankConfigurationModel.APM, apmConfigurationForCode);
        queryParams.put(WorldpayBankConfigurationModel.ACTIVE, Boolean.TRUE);
        return worldpayBankConfigurationDao.find(queryParams);
    }

    @Required
    public void setWorldpayBankConfigurationDao(WorldpayBankConfigurationDao worldpayBankConfigurationDao) {
        this.worldpayBankConfigurationDao = worldpayBankConfigurationDao;
    }

    @Required
    public void setApmConfigurationLookupService(APMConfigurationLookupService apmConfigurationLookupService) {
        this.apmConfigurationLookupService = apmConfigurationLookupService;
    }
}
