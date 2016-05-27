package com.worldpay.fulfilmentprocess.actions.order;

import com.worldpay.exception.WorldpayException;
import de.hybris.platform.basecommerce.enums.FraudStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.fraud.impl.FraudServiceResponse;
import de.hybris.platform.fraud.impl.FraudSymptom;
import de.hybris.platform.fraud.model.FraudReportModel;
import de.hybris.platform.fraud.model.FraudSymptomScoringModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Worldpay Abstract Fraud Check Action creates fraud reports and history logs.
 *
 * @param <T> extends {@link OrderProcessModel}
 */
public abstract class WorldpayAbstractFraudCheckAction<T extends OrderProcessModel> extends WorldpayAbstractOrderAction<T> {

    public static final String FRAUD_CHECK_TEXT = "Fraud check [";

    protected FraudReportModel createFraudReport(String providerName, FraudServiceResponse response, OrderModel order, FraudStatus status) {
        final FraudReportModel fraudReport = this.modelService.create(FraudReportModel.class);
        fraudReport.setOrder(order);
        fraudReport.setStatus(status);
        fraudReport.setProvider(providerName);
        fraudReport.setTimestamp(getTimeService().getCurrentTime());
        int reportNumber = 0;
        if (order.getFraudReports() != null && !order.getFraudReports().isEmpty()) {
            reportNumber = order.getFraudReports().size();
        }

        fraudReport.setCode(order.getCode() + "_FR" + reportNumber);

        final List<FraudSymptomScoringModel> symptomScoringList = new ArrayList<>();
        final List<FraudSymptom> symptoms = response.getSymptoms();
        for (final FraudSymptom fraudSymptom : symptoms) {
            final FraudSymptomScoringModel symptomScoring = this.modelService.create(FraudSymptomScoringModel.class);
            symptomScoring.setFraudReport(fraudReport);
            symptomScoring.setName(fraudSymptom.getSymptom());
            symptomScoring.setExplanation(fraudSymptom.getExplanation());
            symptomScoring.setScore(fraudSymptom.getScore());
            symptomScoringList.add(symptomScoring);
        }
        fraudReport.setFraudSymptomScorings(symptomScoringList);
        return fraudReport;
    }

    protected OrderHistoryEntryModel createHistoryLog(String providerName, OrderModel order, FraudStatus status, String code) {
        String description;
        if (status.equals(FraudStatus.OK)) {
            description = MessageFormat.format(FRAUD_CHECK_TEXT + "{0} ]: OK", providerName);
        } else {
            description = MessageFormat.format(FRAUD_CHECK_TEXT + "{0} ]: {1}. Check the fraud report : {2}", providerName, status.toString(), code);
        }

        return this.createHistoryLog(description, order);
    }

    @Override
    public Set<String> getTransitions() {
        return WorldpayAbstractFraudCheckAction.Transition.getStringValues();
    }

    @Override
    public final String execute(T process) throws WorldpayException {
        return this.executeAction(process).toString();
    }

    public abstract WorldpayAbstractFraudCheckAction.Transition executeAction(T var1) throws WorldpayException;

    public enum Transition {
        OK,
        POTENTIAL;

        Transition() {
        }

        public static Set<String> getStringValues() {
            final Set<String> response = new HashSet<>();
            final WorldpayAbstractFraudCheckAction.Transition[] transitionsArray = values();
            for (final Transition transition : transitionsArray) {
                response.add(transition.toString());
            }
            return response;
        }
    }
}
