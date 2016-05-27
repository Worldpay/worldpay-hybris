package com.worldpay.fraud.symptoms;

import com.worldpay.constants.WorldpayapiConstants;
import com.worldpay.model.WorldpayRiskScoreModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.fraud.impl.FraudServiceResponse;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static java.util.Collections.singletonList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayRiskScoreFraudSymptomTest {

    public static final String CONFIGURED_LIMIT = "40";

    @InjectMocks
    private WorldpayRiskScoreFraudSymptom testObj = new WorldpayRiskScoreFraudSymptom();
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private PaymentTransactionModel worldpayPaymentAuthoriseTransactionModelMock;
    @Mock
    private WorldpayRiskScoreModel worldpayRiskScoreModelMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;

    private final Double configuredLimitValue = Double.valueOf(CONFIGURED_LIMIT);

    @Before
    public void setUp() {
        when(configurationServiceMock.getConfiguration().getString(WorldpayapiConstants.EXTENSIONNAME + ".fraud.scoreLimit")).thenReturn(CONFIGURED_LIMIT);
    }

    @Test
    public void recognizeSymptomShouldReturnAPositiveFraudServiceResponseWhenRiskScoreValueInPaymentTransactionHasScore() throws Exception {
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(worldpayPaymentAuthoriseTransactionModelMock));
        when(worldpayPaymentAuthoriseTransactionModelMock.getRiskScore()).thenReturn(worldpayRiskScoreModelMock);
        final double positiveRiskScoreValue = configuredLimitValue + 10;
        final String riskScoreValue = String.valueOf(positiveRiskScoreValue);
        when(worldpayRiskScoreModelMock.getValue()).thenReturn(riskScoreValue);
        final FraudServiceResponse fraudServiceResponse = new FraudServiceResponse(StringUtils.EMPTY);
        testObj.recognizeSymptom(fraudServiceResponse, orderModelMock);

        assertEquals(positiveRiskScoreValue, fraudServiceResponse.getScore());
        assertEquals(positiveRiskScoreValue, fraudServiceResponse.getSymptoms().get(0).getScore());
        assertEquals(positiveRiskScoreValue, testObj.getIncrement());
        verify(worldpayRiskScoreModelMock).getValue();
    }

    @Test
    public void recognizeSymptomShouldReturnTheSameFraudServiceResponseWhenRiskScoreIsRiskGuardian() throws Exception {
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(worldpayPaymentAuthoriseTransactionModelMock));
        when(worldpayPaymentAuthoriseTransactionModelMock.getRiskScore()).thenReturn(worldpayRiskScoreModelMock);
        final double negativeRiskScoreValue = configuredLimitValue - 10;
        final String riskScoreValue = String.valueOf(negativeRiskScoreValue);
        when(worldpayRiskScoreModelMock.getValue()).thenReturn(riskScoreValue);
        final FraudServiceResponse fraudServiceResponse = new FraudServiceResponse(StringUtils.EMPTY);

        testObj.recognizeSymptom(fraudServiceResponse, orderModelMock);

        assertEquals(0D, fraudServiceResponse.getScore());
        assertEquals(Collections.emptyList(), fraudServiceResponse.getSymptoms());
        verify(worldpayRiskScoreModelMock).getValue();
    }

    @Test
    public void recognizeSymptomShouldReturnTheSameFraudServiceResponseWhenRiskScoreIsNotConvertible() throws Exception {
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(worldpayPaymentAuthoriseTransactionModelMock));
        when(worldpayPaymentAuthoriseTransactionModelMock.getRiskScore()).thenReturn(worldpayRiskScoreModelMock);
        when(worldpayRiskScoreModelMock.getValue()).thenReturn("abd");
        final FraudServiceResponse fraudServiceResponse = new FraudServiceResponse(StringUtils.EMPTY);

        testObj.recognizeSymptom(fraudServiceResponse, orderModelMock);

        assertEquals(0D, fraudServiceResponse.getScore());
        assertEquals(Collections.emptyList(), fraudServiceResponse.getSymptoms());
        verify(worldpayRiskScoreModelMock).getValue();
    }

    @Test
    public void recognizeSymptomShouldReturnTheSameFraudServiceResponseWhenRiskScoreIsNull() throws Exception {
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(worldpayPaymentAuthoriseTransactionModelMock));
        when(worldpayPaymentAuthoriseTransactionModelMock.getRiskScore()).thenReturn(worldpayRiskScoreModelMock);
        when(worldpayRiskScoreModelMock.getValue()).thenReturn(null);
        final FraudServiceResponse fraudServiceResponse = new FraudServiceResponse(StringUtils.EMPTY);

        testObj.recognizeSymptom(fraudServiceResponse, orderModelMock);

        assertEquals(0D, fraudServiceResponse.getScore());
        assertEquals(Collections.emptyList(), fraudServiceResponse.getSymptoms());
        verify(worldpayRiskScoreModelMock).getValue();
    }

    @Test
    public void recognizeSymptomWithNullRiskScore() {
        final FraudServiceResponse fraudServiceResponse = new FraudServiceResponse(StringUtils.EMPTY);
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(worldpayPaymentAuthoriseTransactionModelMock));
        when(worldpayPaymentAuthoriseTransactionModelMock.getRiskScore()).thenReturn(null);

        testObj.recognizeSymptom(fraudServiceResponse, orderModelMock);

        assertEquals(0d, fraudServiceResponse.getScore());
    }
}