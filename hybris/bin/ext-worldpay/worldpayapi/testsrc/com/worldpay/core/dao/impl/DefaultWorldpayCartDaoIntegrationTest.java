package com.worldpay.core.dao.impl;

import com.worldpay.core.dao.WorldpayCartDao;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

@IntegrationTest
public class DefaultWorldpayCartDaoIntegrationTest extends ServicelayerTransactionalTest {

    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";

    @Resource
    private WorldpayCartDao worldpayCartDao;
    @Resource
    private UserService userService;
    @Resource
    private ModelService modelService;

    @Before
    public void setUp() throws Exception {
        importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
    }

    @Test
    public void testGetCartsByWorldpayOrderCode() throws Exception {
        final UserModel user = userService.getUserForUID("ahertz");
        final Collection<CartModel> cartModels = user.getCarts();
        final CartModel cartModel = cartModels.iterator().next();
        assertEquals(cartModel.getWorldpayDeclineCode(), null);
        assertEquals(0, worldpayCartDao.findCartsByWorldpayOrderCode(WORLDPAY_ORDER_CODE).size());
        cartModel.setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        modelService.save(cartModel);
        final List<CartModel> cartsByWorldpayOrderCode = worldpayCartDao.findCartsByWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        final CartModel cartModelFromDao = cartsByWorldpayOrderCode.iterator().next();
        assertEquals(cartModel, cartModelFromDao);
        assertEquals(WORLDPAY_ORDER_CODE, cartModelFromDao.getWorldpayOrderCode());
    }
}