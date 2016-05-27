package com.worldpay.core.services.impl;

import com.worldpay.core.dao.WorldpayAPMConfigurationDao;
import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.model.payment.PaymentType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.text.MessageFormat;
import java.util.*;

/**
 * {@inheritDoc}
 */
public class DefaultAPMConfigurationLookupService implements APMConfigurationLookupService {

    private static final org.apache.log4j.Logger LOG = Logger.getLogger(DefaultAPMConfigurationLookupService.class);

    private WorldpayAPMConfigurationDao worldpayAPMConfigurationDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldpayAPMConfigurationModel getAPMConfigurationForCode(final String paymentTypeCode) {
        final Map<String, String> queryParams = Collections.singletonMap(WorldpayAPMConfigurationModel.CODE, paymentTypeCode);
        final List<WorldpayAPMConfigurationModel> worldpayAPMConfigurationModels = worldpayAPMConfigurationDao.find(queryParams);
        if (worldpayAPMConfigurationModels.isEmpty()) {
            LOG.error(MessageFormat.format("Could not find unique WorldpayAlternativePaymentMethod with code [{0}]", paymentTypeCode));
            return null;
        } else {
            final WorldpayAPMConfigurationModel worldpayAPMConfigurationModel = worldpayAPMConfigurationModels.get(0);
            if (worldpayAPMConfigurationModels.size() > 1) {
                LOG.error(MessageFormat.format("Multiple WorldpayAPMConfiguration with code [{0}], returning first [{1}]", paymentTypeCode, worldpayAPMConfigurationModel.getCode()));
                return worldpayAPMConfigurationModel;
            } else {
                return worldpayAPMConfigurationModel;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAllApmPaymentTypeCodes() {
        final Set<String> apmPaymentTypesCodes = new HashSet<>();
        for (final WorldpayAPMConfigurationModel worldpayAPMConfiguration : worldpayAPMConfigurationDao.find()) {
            final PaymentType paymentType = PaymentType.getPaymentType(worldpayAPMConfiguration.getCode());
            if (paymentType != null) {
                apmPaymentTypesCodes.add(paymentType.getMethodCode());
            }
        }
        return apmPaymentTypesCodes;
    }

    @Required
    public void setWorldpayAPMConfigurationDao(final WorldpayAPMConfigurationDao worldpayAPMConfigurationDao) {
        this.worldpayAPMConfigurationDao = worldpayAPMConfigurationDao;
    }
}
