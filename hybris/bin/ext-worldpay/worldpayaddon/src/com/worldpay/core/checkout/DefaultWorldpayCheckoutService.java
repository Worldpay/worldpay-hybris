package com.worldpay.core.checkout;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Required;

/**
 * Provides specific behaviour to the commerce checkout service to set the merchant transaction code in the same style
 * as other places that use worldpay
 */
public class DefaultWorldpayCheckoutService implements WorldpayCheckoutService {

    private ModelService modelService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPaymentAddress(final CartModel cartModel, final AddressModel addressModel) {
        cartModel.setPaymentAddress(addressModel);
        getModelService().save(cartModel);

        getModelService().refresh(cartModel);
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }
}
