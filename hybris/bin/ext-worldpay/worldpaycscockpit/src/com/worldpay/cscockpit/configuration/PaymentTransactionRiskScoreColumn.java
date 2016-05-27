package com.worldpay.cscockpit.configuration;

import com.worldpay.model.WorldpayRiskScoreModel;
import de.hybris.platform.cockpit.model.meta.BaseType;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.impl.ItemAttributePropertyDescriptor;
import de.hybris.platform.cockpit.services.meta.TypeService;
import de.hybris.platform.cockpit.services.values.*;
import de.hybris.platform.cockpit.session.UISession;
import de.hybris.platform.cockpit.session.UISessionUtils;
import de.hybris.platform.cscockpit.services.config.impl.AbstractSimpleCustomColumnConfiguration;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;

public class PaymentTransactionRiskScoreColumn extends AbstractSimpleCustomColumnConfiguration<String, PaymentTransactionEntryModel> {

    private static final Logger LOG = Logger.getLogger(PaymentTransactionRiskScoreColumn.class);

    public static final String VALUE_SEPARATOR = ", ";
    public static final String EQUALS = "=";

    protected UISession getCurrentSession() {
        return UISessionUtils.getCurrentSession();
    }

    @Override
    protected String getItemValue(PaymentTransactionEntryModel paymentTransactionEntryModel, Locale locale) throws ValueHandlerException {
        final PaymentTransactionModel paymentTransaction = paymentTransactionEntryModel.getPaymentTransaction();
        if (paymentTransaction != null) {
            WorldpayRiskScoreModel riskScore = paymentTransaction.getRiskScore();
            return riskScore != null ? generateRiskScoreItemValue(riskScore, locale) : null;
        }
        return null;
    }

    protected String generateRiskScoreItemValue(WorldpayRiskScoreModel riskScore, Locale locale) {
        final Set<PropertyDescriptor> declaredPropertyDescriptors = getDeclaredPropertyDescriptors(riskScore);
        final List<String> riskScoreValuesList = getRiskScoreFieldsAndValues(riskScore, locale, declaredPropertyDescriptors.iterator());
        return StringUtils.join(riskScoreValuesList, VALUE_SEPARATOR);
    }

    protected List<String> getRiskScoreFieldsAndValues(WorldpayRiskScoreModel riskScore, Locale locale, Iterator<PropertyDescriptor> iterator) {
        final List<String> riskScoreValuesList = new ArrayList<>();
        while (iterator.hasNext()) {
            PropertyDescriptor descriptor = iterator.next();
            String itemValue = buildItemValueFromDescriptor(riskScore, descriptor, locale);
            if (StringUtils.isNotEmpty(itemValue)) {
                riskScoreValuesList.add(itemValue);
            }
        }
        return riskScoreValuesList;
    }

    protected String buildItemValueFromDescriptor(WorldpayRiskScoreModel riskScore, PropertyDescriptor propertyDescriptor, Locale locale) {
        final String attributeName = ((ItemAttributePropertyDescriptor) propertyDescriptor).getAttributeQualifier();
        final ObjectValueContainer objectValueContainer = getObjectValueContainer(riskScore);
        final String localeIso = propertyDescriptor.isLocalized() ? locale.getLanguage() : null;
        final Object attributeValue = objectValueContainer.getValue(propertyDescriptor, localeIso).getOriginalValue();

        return attributeValue == null ? null : attributeName + EQUALS + attributeValue;
    }

    protected ObjectValueContainer getObjectValueContainer(Object object) {
        final BaseType currentBaseType = getRiskScoreBaseType((WorldpayRiskScoreModel) object);
        final ObjectValueContainer objectValueContainer = createObjectValueContainer(object, currentBaseType);
        final ObjectValueHandlerRegistry valueHandlerRegistry = getCurrentSession().getValueHandlerRegistry();
        final List<ObjectValueHandler> valueHandlerChain = valueHandlerRegistry.getValueHandlerChain(currentBaseType);
        valueHandlerChain.forEach(objectValueHandler -> {
            try {
                objectValueHandler.loadValues(objectValueContainer, currentBaseType, object, currentBaseType.getPropertyDescriptors(), getAvailableLanguageIsoCodes());
            } catch (ValueHandlerPermissionException e) {
                LOG.error("Not sufficient privileges!", e);
            } catch (ValueHandlerException e) {
                LOG.error("Error loading object values", e);
            }
        });
        return objectValueContainer;
    }

    protected ObjectValueContainer createObjectValueContainer(final Object object, final BaseType currentBaseType) {
        return new ObjectValueContainer(currentBaseType, object);
    }

    private Set<String> getAvailableLanguageIsoCodes() {
        return getCurrentSession().getSystemService().getAvailableLanguageIsos();
    }

    private TypeService getCockpitTypeService() {
        return getCurrentSession().getTypeService();
    }

    private Set<PropertyDescriptor> getDeclaredPropertyDescriptors(WorldpayRiskScoreModel riskScoreModel) {
        return getRiskScoreBaseType(riskScoreModel).getDeclaredPropertyDescriptors();
    }

    private BaseType getRiskScoreBaseType(WorldpayRiskScoreModel riskScoreModel) {
        return getCockpitTypeService().wrapItem(riskScoreModel).getType();
    }
}
