package com.worldpay.converters;

import com.worldpay.internal.model.RiskScore;
import com.worldpay.model.WorldpayRiskScoreModel;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Required;

/**
 * Converter to transform a {@link RiskScore} received from Worldpay to a {@link WorldpayRiskScoreModel}
 */
public class WorldpayRiskScoreConverter extends AbstractConverter<RiskScore, WorldpayRiskScoreModel> {

    private ModelService modelService;

    /**
     * Creates a {@link WorldpayRiskScoreModel} with the information from the {@link RiskScore}. Handles RMM and RiskGuardian(tm) RiskScores.
     * @param riskScore the {@link RiskScore} received from Worldpay
     * @return a created {@link WorldpayRiskScoreModel} with the information received.
     * @throws ConversionException
     */
    @Override
    public WorldpayRiskScoreModel convert(final RiskScore riskScore) throws ConversionException {
        final WorldpayRiskScoreModel worldpayRiskScoreModel = modelService.create(WorldpayRiskScoreModel.class);
        populate(riskScore,worldpayRiskScoreModel);
        return worldpayRiskScoreModel;
    }

    @Override
    public void populate(final RiskScore riskScore, final WorldpayRiskScoreModel worldpayRiskScoreModel) {
        worldpayRiskScoreModel.setFinalScore(riskScore.getFinalScore() != null ? Double.valueOf(riskScore.getFinalScore()) : null);
        worldpayRiskScoreModel.setExtendedResponse(riskScore.getExtendedResponse());
        worldpayRiskScoreModel.setId(riskScore.getId());
        worldpayRiskScoreModel.setMessage(riskScore.getMessage());
        worldpayRiskScoreModel.setProvider(riskScore.getProvider());
        worldpayRiskScoreModel.setRgid(riskScore.getRGID() != null ? Long.valueOf(riskScore.getRGID()) : null);
        worldpayRiskScoreModel.setTRisk(riskScore.getTRisk() != null ? Double.valueOf(riskScore.getTRisk()) : null);
        worldpayRiskScoreModel.setTScore(riskScore.getTScore() != null ? Double.valueOf(riskScore.getTScore()) : null);
        worldpayRiskScoreModel.setValue(riskScore.getValue());
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }
}
