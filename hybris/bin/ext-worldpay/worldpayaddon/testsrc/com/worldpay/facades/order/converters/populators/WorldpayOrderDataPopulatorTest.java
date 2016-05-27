package com.worldpay.facades.order.converters.populators;

import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.OrderModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayOrderDataPopulatorTest {

    @InjectMocks
    private WorldpayOrderDataPopulator testObj = new WorldpayOrderDataPopulator();

    @Mock
    private OrderData orderDataMock;
    @Mock
    private OrderModel abstractOrderModelMock;
    @Mock
    private WorldpayPaymentTransactionService paymentTransactionServiceMock;

    @Test
    public void populateShouldSetPaymentPendingConfirmationTrueWhenPaymentTransactionIsMarkedAsAPMOpen() throws Exception {
        when(paymentTransactionServiceMock.isAnyPaymentTransactionApmOpenForOrder(abstractOrderModelMock)).thenReturn(true);

        testObj.populate(abstractOrderModelMock, orderDataMock);

        verify(orderDataMock).setIsApmOpen(true);
    }

    @Test
    public void populateShouldSetPaymentPendingConfirmationFalseWhenPaymentTransactionIsNotMarkedAsAPMOpen() throws Exception {
        when(paymentTransactionServiceMock.isAnyPaymentTransactionApmOpenForOrder(abstractOrderModelMock)).thenReturn(false);

        testObj.populate(abstractOrderModelMock, orderDataMock);

        verify(orderDataMock).setIsApmOpen(false);
    }
}