package com.worldpay.core.dao;

import com.worldpay.model.WorldpayBankConfigurationModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

/**
 * DAO class for {@link WorldpayBankConfigurationModel}
 */
public class WorldpayBankConfigurationDao extends DefaultGenericDao<WorldpayBankConfigurationModel> {

    /**
     * Sets the type to {@link WorldpayBankConfigurationModel} to be used in this DAO class.
     */
    public WorldpayBankConfigurationDao() {
        super(WorldpayBankConfigurationModel._TYPECODE);
    }
}
