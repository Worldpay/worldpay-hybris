package com.worldpay.core.services.strategies.impl;

import de.hybris.platform.commerceservices.strategies.GenerateMerchantTransactionCodeStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Required;

/**
 * Strategy to generate the WorldpayOrderCode to send to Worldpay. Identifies an order in Worldpay.
 */
public class WorldpayGenerateMerchantTransactionCodeStrategy implements GenerateMerchantTransactionCodeStrategy {

    private ModelService modelService;
    private CartService cartService;

    /**
     * Generates the the merchantTransactionCode (referenced as orderCode in Worldpay) and sets it on the cart for future use.
     * @param cartModel The cart to generate the code from. If null, will get sessionCart
     * @return
     */
    public String generateCode(final CartModel cartModel) {
        CartModel parameterCart = cartModel;
        if (parameterCart == null) {
            parameterCart = cartService.getSessionCart();
        }
        String worldpayOrderCode = parameterCart.getCode() + "-" + getTime();
        parameterCart.setWorldpayOrderCode(worldpayOrderCode);
        modelService.save(parameterCart);
        return worldpayOrderCode;
    }

    protected long getTime() {
        return System.currentTimeMillis();
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    @Required
    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }
}
