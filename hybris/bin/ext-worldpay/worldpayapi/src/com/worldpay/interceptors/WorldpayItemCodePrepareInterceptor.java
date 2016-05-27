package com.worldpay.interceptors;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.springframework.beans.factory.annotation.Required;


/**
 * Adds a unique generated code to object if none exists.
 * This is done when ItemModel is saved using {@link ModelService#save(Object)}.
 */
public class WorldpayItemCodePrepareInterceptor implements PrepareInterceptor<ItemModel> {

    private KeyGenerator keyGenerator;
    private TypeService typeService;
    private String fieldName;

    /**
     * Adds a unique code to model before save if model.save is called on object
     *
     * @param itemModel                 Item model to be saved
     * @param interceptorContext        Interceptor context
     * @throws InterceptorException
     */
    @Override
    public void onPrepare(final ItemModel itemModel, final InterceptorContext interceptorContext) throws InterceptorException {

        final ComposedTypeModel type = typeService.getComposedTypeForCode(itemModel.getItemtype());
        if (typeService.hasAttribute(type, fieldName)) {
            final ModelService modelService = interceptorContext.getModelService();
            if (modelService.getAttributeValue(itemModel, fieldName) == null) {
                modelService.setAttributeValue(itemModel, fieldName, keyGenerator.generate());
            }
        }
    }

    @Required
    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    @Required
    public void setTypeService(final TypeService typeService) {
        this.typeService = typeService;
    }

    @Required
    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }
}
