package com.worldpay.core.dao;

import com.worldpay.model.WorldpayAPMConfigurationModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

/**
 * DAO class for the {@link WorldpayAPMConfigurationModel}
 */
public class WorldpayAPMConfigurationDao extends DefaultGenericDao<WorldpayAPMConfigurationModel> {

    /**
     * Constructor that initialises the DAO with the type {@link WorldpayAPMConfigurationModel}
     */
    public WorldpayAPMConfigurationDao() {
        super(WorldpayAPMConfigurationModel._TYPECODE);
    }
}
