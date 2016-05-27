package com.worldpay.core.services.impl;

import com.worldpay.core.dao.WorldpayCartDao;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayCartServiceTest {

    private static final String WORLDPAY_ORDER_CODE = "orderCode";
    private static final int DECLINE_CODE = 19;


    @InjectMocks
    private DefaultWorldpayCartService testObj = new DefaultWorldpayCartService();

    @Mock
    private WorldpayCartDao worldpayCartDaoMock;

    @Mock
    private CartModel cartModelMock;
    @Mock
    private CartModel cartModelMock2;
    @Mock
    private ModelService modelServiceMock;


    private List<CartModel> cartModelList = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        when(worldpayCartDaoMock.findCartsByWorldpayOrderCode(WORLDPAY_ORDER_CODE)).thenReturn(cartModelList);
    }

    @Test
    public void setWorldpayDeclineCodeOnCartShouldSetDeclineCode() {
        cartModelList.add(cartModelMock);

        testObj.setWorldpayDeclineCodeOnCart(WORLDPAY_ORDER_CODE, DECLINE_CODE);

        verify(worldpayCartDaoMock).findCartsByWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(cartModelMock).setWorldpayDeclineCode(DECLINE_CODE);
        verify(modelServiceMock).save(cartModelMock);
    }

    @Test
    public void setWorldpayDeclineCodeOnCartShouldNotSetDeclineCodeWhenNoCartsFound() {
        testObj.setWorldpayDeclineCodeOnCart(WORLDPAY_ORDER_CODE, DECLINE_CODE);

        verify(worldpayCartDaoMock).findCartsByWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(cartModelMock, never()).setWorldpayDeclineCode(DECLINE_CODE);
        verify(modelServiceMock, never()).save(cartModelMock);
    }

    @Test
    public void setWorldpayDeclineCodeOnCartShouldNotSetDeclineCodeWhenMultipleCartsFound() {
        cartModelList.add(cartModelMock);
        cartModelList.add(cartModelMock2);

        testObj.setWorldpayDeclineCodeOnCart(WORLDPAY_ORDER_CODE, DECLINE_CODE);

        verify(worldpayCartDaoMock).findCartsByWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(cartModelMock2, never()).setWorldpayDeclineCode(DECLINE_CODE);
        verify(cartModelMock2, never()).setWorldpayDeclineCode(DECLINE_CODE);
        verify(modelServiceMock, never()).save(cartModelMock);
        verify(modelServiceMock, never()).save(cartModelMock2);
    }

    @Test
    public void findCartsByWorldpayOrderCodeShouldUseWorldpay() {
        final List<CartModel> result = testObj.findCartsByWorldpayOrderCode(WORLDPAY_ORDER_CODE);

        verify(worldpayCartDaoMock).findCartsByWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        assertEquals(cartModelList, result);
    }
}