package com.worldpay.fraud.symptoms;

import com.worldpay.model.WorldpayRiskScoreModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.fraud.impl.FraudServiceResponse;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.List;

/**
 * The Worldpay Risk Score Fraud Symptom class creates the {@link WorldpayRiskScoreFraudSymptom} symptom with the fraud risk value.
 */
public class WorldpayRiskScoreFraudSymptom extends AbstractWorldpayOrderFraudSymptomDetection {

    private static final Logger LOG = Logger.getLogger(WorldpayRiskScoreFraudSymptom.class);

    @Override
    public FraudServiceResponse recognizeSymptom(FraudServiceResponse fraudServiceResponse, AbstractOrderModel abstractOrderModel) {
        List<PaymentTransactionModel> paymentTransactions = abstractOrderModel.getPaymentTransactions();
        // At this moment only the Authorisation transaction will be in the list of paymentTransactions
        final double scoreLimit = getScoreLimit();
        paymentTransactions.stream().filter(paymentTransaction -> paymentTransaction != null).forEach(paymentTransaction -> {
            WorldpayRiskScoreModel riskScore = paymentTransaction.getRiskScore();
            if(riskScore==null){
                LOG.warn("We did not get a risk score back, skipping risk check for: "+paymentTransaction);
                return;
            }
            String riskScoreValue = riskScore.getValue();
            try {
                if (riskScoreValue != null && Double.compare(Double.valueOf(riskScoreValue), scoreLimit) > 0) {
                    setIncrement(Double.valueOf(riskScoreValue));
                    fraudServiceResponse.addSymptom(createSymptom("RiskValue", true));
                }
            } catch (NumberFormatException e) {
                LOG.error(MessageFormat.format("riskScoreValue for order with code [{0}] was not a number: [{1}]. The RiskScore was not checked for fraud.",
                        abstractOrderModel.getCode(), riskScoreValue));
            }
        });
        return fraudServiceResponse;
    }
}
