package com.worldpay.converters;

import com.worldpay.internal.model.RiskScore;
import com.worldpay.model.WorldpayRiskScoreModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayRiskScoreConverterTest {

    private static final String MESSAGE = "message";
    private static final String FINAL_SCORE = "10.1";
    private static final String EXTENDED_RESPONSE = "extendedResponse";
    private static final String RGID = "11";
    private static final String PROVIDER = "provider";
    private static final String ID = "id";
    private static final String TRISK = "13.5";
    private static final String TSCORE = "19.19";
    private static final String VALUE = "value";
    @InjectMocks
    private WorldpayRiskScoreConverter testObj;

    @Mock
    private RiskScore riskScoreMock;

    @Mock
    private ModelService modelServiceMock;

    @Before
    public void setup() {
        when(modelServiceMock.create(WorldpayRiskScoreModel.class)).thenReturn(new WorldpayRiskScoreModel());
        when(riskScoreMock.getFinalScore()).thenReturn(FINAL_SCORE);
        when(riskScoreMock.getExtendedResponse()).thenReturn(EXTENDED_RESPONSE);
        when(riskScoreMock.getId()).thenReturn(ID);
        when(riskScoreMock.getMessage()).thenReturn(MESSAGE);
        when(riskScoreMock.getProvider()).thenReturn(PROVIDER);
        when(riskScoreMock.getRGID()).thenReturn(RGID);
        when(riskScoreMock.getTRisk()).thenReturn(TRISK);
        when(riskScoreMock.getTScore()).thenReturn(TSCORE);
        when(riskScoreMock.getValue()).thenReturn(VALUE);
    }

    @Test
    public void convertShouldConvertRiskScoreIntoWorldpayRiskScoreModel() {
        final WorldpayRiskScoreModel result = testObj.convert(riskScoreMock);

        assertEquals(Double.valueOf(FINAL_SCORE), result.getFinalScore());
        assertEquals(EXTENDED_RESPONSE, result.getExtendedResponse());
        assertEquals(ID, result.getId());
        assertEquals(MESSAGE, result.getMessage());
        assertEquals(PROVIDER, result.getProvider());
        assertEquals(Long.valueOf(RGID), result.getRgid());
        assertEquals(Double.valueOf(TRISK), result.getTRisk());
        assertEquals(Double.valueOf(TSCORE), result.getTScore());
        assertEquals(VALUE, result.getValue());
    }

    @Test
    public void convertShouldNotConvertNullValues() {
        when(riskScoreMock.getFinalScore()).thenReturn(null);
        when(riskScoreMock.getRGID()).thenReturn(null);
        when(riskScoreMock.getTRisk()).thenReturn(null);
        when(riskScoreMock.getTScore()).thenReturn(null);

        final WorldpayRiskScoreModel result = testObj.convert(riskScoreMock);

        assertEquals(null, result.getFinalScore());
        assertEquals(null, result.getRgid());
        assertEquals(null, result.getTRisk());
        assertEquals(null, result.getTScore());
    }
}