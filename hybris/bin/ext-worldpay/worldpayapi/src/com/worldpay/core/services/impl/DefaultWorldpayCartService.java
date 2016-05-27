package com.worldpay.core.services.impl;

import com.worldpay.core.dao.WorldpayCartDao;
import com.worldpay.core.services.WorldpayCartService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.text.MessageFormat;
import java.util.List;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayCartService implements WorldpayCartService {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayCartService.class);
    private WorldpayCartDao worldpayCartDao;
    private ModelService modelService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWorldpayDeclineCodeOnCart(String worldpayOrderCode, int declineCode) {

        final List<CartModel> cartsByWorldpayOrderCode = findCartsByWorldpayOrderCode(worldpayOrderCode);

        if (CollectionUtils.isEmpty(cartsByWorldpayOrderCode)) {
            LOG.error(MessageFormat.format("No carts found for worldpayOrderCode {0}", worldpayOrderCode));
        } else if (cartsByWorldpayOrderCode.size() > 1) {
            LOG.error(MessageFormat.format("Found more than one cart for worldpayOrderCode = {0}", worldpayOrderCode));
        } else {
            final CartModel cartModel = cartsByWorldpayOrderCode.get(0);
            cartModel.setWorldpayDeclineCode(declineCode);
            modelService.save(cartModel);
        }
    }

    @Override
    public List<CartModel> findCartsByWorldpayOrderCode(String worldpayOrderCode) {
        return worldpayCartDao.findCartsByWorldpayOrderCode(worldpayOrderCode);
    }

    @Required
    public void setWorldpayCartDao(WorldpayCartDao worldpayCartDao) {
        this.worldpayCartDao = worldpayCartDao;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }
}
