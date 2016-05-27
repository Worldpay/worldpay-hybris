package com.worldpay.merchant.strategies.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.data.AdditionalAuthInfo;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayOrderInfoStrategyTest {

    private static final String INSTALLATION_ID = "1043270";
    private static final String STATEMENT_NARRATIVE_TEXT = "STATEMENT NARRATIVE TEXT";
    private static final String ORDER_CONTENT = "orderContent";

    @InjectMocks
    private DefaultWorldpayOrderInfoStrategy testObj = new DefaultWorldpayOrderInfoStrategy();

    @Mock
    private WorldpayMerchantConfigData worldpayMerchantConfigDataMock;
    @Mock
    private AdditionalAuthInfo additionalAuthInfoMock;

    @Test
    public void shouldPopulateAdditionalAuthInfo() {
        when(worldpayMerchantConfigDataMock.getOrderContent()).thenReturn(ORDER_CONTENT);
        when(worldpayMerchantConfigDataMock.getInstallationId()).thenReturn(INSTALLATION_ID);
        when(worldpayMerchantConfigDataMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE_TEXT);

        testObj.populateAdditionalAuthInfo(additionalAuthInfoMock, worldpayMerchantConfigDataMock);

        verify(additionalAuthInfoMock).setOrderContent(ORDER_CONTENT);
        verify(additionalAuthInfoMock).setInstallationId(INSTALLATION_ID);
        verify(additionalAuthInfoMock).setStatementNarrative(STATEMENT_NARRATIVE_TEXT);
    }

    @Test
    public void shouldGetAdditionalAuthInfo() {
        when(worldpayMerchantConfigDataMock.getOrderContent()).thenReturn(ORDER_CONTENT);
        when(worldpayMerchantConfigDataMock.getInstallationId()).thenReturn(INSTALLATION_ID);
        when(worldpayMerchantConfigDataMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE_TEXT);

        final AdditionalAuthInfo result = testObj.getAdditionalAuthInfo(worldpayMerchantConfigDataMock);

        assertEquals(ORDER_CONTENT, result.getOrderContent());
        assertEquals(INSTALLATION_ID, result.getInstallationId());
        assertEquals(STATEMENT_NARRATIVE_TEXT, result.getStatementNarrative());
    }
}
