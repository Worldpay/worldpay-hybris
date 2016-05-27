package com.worldpay.components.impl;

import com.worldpay.cscockpit.services.search.generic.query.DefaultCartSearchQueryBuilder;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import com.worldpay.service.model.MerchantInfo;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.cscockpit.model.data.DataObject;
import de.hybris.platform.cscockpit.services.search.SearchException;
import de.hybris.platform.cscockpit.services.search.impl.DefaultCsTextSearchCommand;
import de.hybris.platform.cscockpit.widgets.controllers.search.SearchCommandController;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.zhtml.Text;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Div;
import org.zkoss.zul.Window;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.worldpay.service.model.AuthorisedStatus.AUTHORISED;
import static java.text.MessageFormat.format;


public class HopResponse {

    private static final Logger LOG = Logger.getLogger(HopResponse.class);

    protected static final String AUTHORISED_PAYMENT_TEXT = "Payment has been authorised. Please now close the window and place the order.";
    protected static final String DECLINED_PAYMENT_TEXT = "Payment has been declined, or could not be taken at this time. Please try again later.";

    private WorldpayRedirectOrderService worldpayRedirectOrderService;
    private WorldpayMerchantInfoService worldpayMerchantInfoService;
    private SearchCommandController<DefaultCsTextSearchCommand> searchCommandController;
    private Converter<Map<String, String>, RedirectAuthoriseResult> redirectAuthoriseResultConverter;

    public void showResponse(final Window window) {

        final Map resultMap = getCurrentExecution().getParameterMap();
        final Set keySet = resultMap.keySet();
        final Div div = new Div();

        final Map<String, String> params = extractParams(keySet);

        final DefaultCsTextSearchCommand command = new DefaultCsTextSearchCommand();
        final RedirectAuthoriseResult authoriseResult = getRedirectAuthoriseResultConverter().convert(params);

        command.setText(DefaultCartSearchQueryBuilder.TextField.CartId, getCartCode(authoriseResult.getOrderKey()));

        CartModel cartModel = null;
        Text text;

        try {
            getSearchCommandController().search(command);
            final List<DataObject<TypedObject>> currentPageResults = getSearchCommandController().getCurrentPageResults();
            if (CollectionUtils.isNotEmpty(currentPageResults)) {
                cartModel = (CartModel) currentPageResults.get(0).getItem().getObject();
            }

            final MerchantInfo merchantConfig = getWorldpayMerchantInfoService().getCustomerServicesMerchant();

            final String paymentStatus = authoriseResult.getPaymentStatus();

            if (paymentStatus.equalsIgnoreCase(AUTHORISED.getCode())) {
                text = new Text(AUTHORISED_PAYMENT_TEXT);
                worldpayRedirectOrderService.completeRedirectAuthorise(authoriseResult, merchantConfig.getMerchantCode(), cartModel);
            } else {
                text = new Text(DECLINED_PAYMENT_TEXT);
            }
        } catch (final SearchException e) {
            LOG.error(format("Search exception error: {0}", e.getMessage()), e);
            text = new Text("An error has occurred. Please try again later.");
        } catch (WorldpayConfigurationException e) {
            LOG.error(format("WorldpayConfigurationException exception error: {0}", e.getMessage()), e);
            text = new Text("An error has occurred. Please check the configuration for the merchants.");
        }

        text.setParent(div);
        text.setVisible(true);
        div.setVisible(true);
        div.setParent(window);
    }

    protected Execution getCurrentExecution() {
        return Executions.getCurrent();
    }

    protected Map<String, String> extractParams(Set keySet) {
        final Map<String, String> params = new HashMap<>();
        for (final Object paramName : keySet) {
            final String paramValue = getCurrentExecution().getParameter((String) paramName);
            params.put((String) paramName, paramValue);
        }
        return params;
    }

    private String getCartCode(final String orderKey) {
        // Example of key: MERCHANT_OWNER^MERCHANTCODE^00002000-1431098192721
        return orderKey.substring(orderKey.indexOf('^', orderKey.indexOf('^') + 1) + 1, orderKey.indexOf('-'));
    }

    @Required
    public void setWorldpayRedirectOrderService(final WorldpayRedirectOrderService worldpayRedirectOrderService) {
        this.worldpayRedirectOrderService = worldpayRedirectOrderService;
    }

    public WorldpayMerchantInfoService getWorldpayMerchantInfoService() {
        return worldpayMerchantInfoService;
    }

    @Required
    public void setWorldpayMerchantInfoService(final WorldpayMerchantInfoService worldpayMerchantInfoService) {
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
    }

    public SearchCommandController<DefaultCsTextSearchCommand> getSearchCommandController() {
        return searchCommandController;
    }

    @Required
    public void setSearchCommandController(final SearchCommandController<DefaultCsTextSearchCommand> searchCommandController) {
        this.searchCommandController = searchCommandController;
    }

    public Converter<Map<String, String>, RedirectAuthoriseResult> getRedirectAuthoriseResultConverter() {
        return redirectAuthoriseResultConverter;
    }

    @Required
    public void setRedirectAuthoriseResultConverter(Converter<Map<String, String>, RedirectAuthoriseResult> redirectAuthoriseResultConverter) {
        this.redirectAuthoriseResultConverter = redirectAuthoriseResultConverter;
    }
}
