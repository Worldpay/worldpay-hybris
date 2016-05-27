package com.worldpay.commands.impl;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Mockito.when;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayCommandTest {

    private static final String ORDER_CODE = "orderCode";

    @InjectMocks
    private WorldpayCommand testObj = new WorldpayCommand();
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;

    @Test
    public void shouldReturnKnownMerchantInfo() throws WorldpayConfigurationException {
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(ORDER_CODE)).thenReturn(paymentTransactionModelMock);
        when(worldpayMerchantInfoServiceMock.getMerchantInfoFromTransaction(paymentTransactionModelMock)).thenReturn(merchantInfoMock);

        final MerchantInfo result = testObj.getMerchantInfo(ORDER_CODE);

        assertEquals(merchantInfoMock, result);
    }
}