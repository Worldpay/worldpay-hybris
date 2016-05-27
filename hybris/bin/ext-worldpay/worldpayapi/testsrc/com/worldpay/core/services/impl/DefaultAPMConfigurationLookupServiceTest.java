package com.worldpay.core.services.impl;

import com.worldpay.core.dao.WorldpayAPMConfigurationDao;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.worldpay.service.model.payment.PaymentType.ALIPAY;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAPMConfigurationLookupServiceTest {

    private static final String NON_EXISTING_METHOD_CODE = "nonExistingMethodCode";
    private static final String PAYMENT_TYPE_CODE = "paymentTypeCode";
    private static final String PAYMENT_APM_NAME = "paymentAPMName";

    @InjectMocks
    private DefaultAPMConfigurationLookupService testObj = new DefaultAPMConfigurationLookupService();

    @Mock
    private WorldpayAPMConfigurationDao worldpayAPMConfigurationDaoMock;
    @Mock
    private WorldpayAPMConfigurationModel worldpayAPMConfigurationModelMock;
    @Mock
    private List<WorldpayAPMConfigurationModel> mockedList;

    @Test
    public void getAPMConfigReturnsAWorldpayAPMConfigModel() {
        when(worldpayAPMConfigurationDaoMock.find(Collections.singletonMap(WorldpayAPMConfigurationModel.CODE, PAYMENT_TYPE_CODE))).thenReturn(Collections.singletonList(worldpayAPMConfigurationModelMock));
        when(worldpayAPMConfigurationModelMock.getName()).thenReturn(PAYMENT_APM_NAME);

        final WorldpayAPMConfigurationModel result = testObj.getAPMConfigurationForCode(PAYMENT_TYPE_CODE);

        assertEquals(worldpayAPMConfigurationModelMock, result);
        assertEquals(worldpayAPMConfigurationModelMock.getName(), PAYMENT_APM_NAME);
    }

    @Test
    public void getAPMConfigDaoReturnsEmptyList() {
        when(worldpayAPMConfigurationDaoMock.find(Collections.singletonMap(WorldpayAPMConfigurationModel.CODE, PAYMENT_TYPE_CODE))).thenReturn(Collections.emptyList());

        final WorldpayAPMConfigurationModel result = testObj.getAPMConfigurationForCode(PAYMENT_TYPE_CODE);

        assertEquals(null, result);
    }

    @Test
    public void getAPMConfigDaoReturnsMoreThan1APM() {
        when(mockedList.size()).thenReturn(2);
        when(mockedList.get(0)).thenReturn(worldpayAPMConfigurationModelMock);

        when(worldpayAPMConfigurationDaoMock.find(Collections.singletonMap(WorldpayAPMConfigurationModel.CODE, PAYMENT_TYPE_CODE))).thenReturn(mockedList);

        final WorldpayAPMConfigurationModel result = testObj.getAPMConfigurationForCode(PAYMENT_TYPE_CODE);

        assertEquals(worldpayAPMConfigurationModelMock, result);
    }

    @Test
    public void getAllApmPaymentTypeCodesReturnsSetOfPaymentTypeCodes() {
        when(worldpayAPMConfigurationDaoMock.find()).thenReturn(Collections.singletonList(worldpayAPMConfigurationModelMock));
        final String alipayMethodCode = ALIPAY.getMethodCode();
        when(worldpayAPMConfigurationModelMock.getCode()).thenReturn(alipayMethodCode);

        final Set<String> result = testObj.getAllApmPaymentTypeCodes();

        assertNotNull(result);
        assertTrue(result.contains(alipayMethodCode));
    }

    @Test
    public void getAllApmPaymentTypeCodesReturns() {
        when(worldpayAPMConfigurationDaoMock.find()).thenReturn(Collections.singletonList(worldpayAPMConfigurationModelMock));
        when(worldpayAPMConfigurationModelMock.getCode()).thenReturn(NON_EXISTING_METHOD_CODE);

        final Set<String> result = testObj.getAllApmPaymentTypeCodes();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}