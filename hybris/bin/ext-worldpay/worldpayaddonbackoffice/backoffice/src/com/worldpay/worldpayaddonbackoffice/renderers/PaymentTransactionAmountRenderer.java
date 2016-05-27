package com.worldpay.worldpayaddonbackoffice.renderers;

import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.dataaccess.services.PropertyValueService;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.widgets.common.WidgetComponentRenderer;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.zul.Listcell;

import java.math.BigDecimal;

public class PaymentTransactionAmountRenderer implements WidgetComponentRenderer<Listcell, ListColumn, Object> {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentTransactionAmountRenderer.class);
    protected static final String PAYMENT_TRANSACTION = "PaymentTransaction";

    private TypeFacade typeFacade;
    private PropertyValueService propertyValueService;
    private LabelService labelService;
    private PermissionFacade permissionFacade;

    public void render(Listcell listCell, ListColumn columnConfiguration, Object object, DataType dataType, WidgetInstanceManager widgetInstanceManager) {
        String qualifier = columnConfiguration.getQualifier();

        try {
            dataType = typeFacade.load(PAYMENT_TRANSACTION);
            if (dataType != null && permissionFacade.canReadProperty(dataType.getCode(), qualifier)) {
                Object e = propertyValueService.readValue(object, qualifier);
                if (e == null) {
                    listCell.setLabel(StringUtils.EMPTY);
                } else {
                    BigDecimal paymentTransactionAmount = ((BigDecimal) e).setScale(((PaymentTransactionModel) object).getEntries().get(0).getCurrency().getDigits(), BigDecimal.ROUND_HALF_DOWN);
                    String amount = labelService.getObjectLabel(paymentTransactionAmount);
                    if (StringUtils.isBlank(amount)) {
                        amount = e.toString();
                    }

                    listCell.setLabel(amount);
                }
            }
        } catch (TypeNotFoundException e) {
            LOG.error("Could not render row......", e);
        }
    }

    @Required
    public void setPropertyValueService(PropertyValueService propertyValueService) {
        this.propertyValueService = propertyValueService;
    }

    @Required
    public void setLabelService(LabelService labelService) {
        this.labelService = labelService;
    }

    @Required
    public void setPermissionFacade(PermissionFacade permissionFacade) {
        this.permissionFacade = permissionFacade;
    }

    @Required
    public void setTypeFacade(TypeFacade typeFacade) {
        this.typeFacade = typeFacade;
    }
}
