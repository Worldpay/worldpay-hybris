package com.worldpay.hostedorderpage.converters;

import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response.AbstractResultPopulator;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Implementation of {@link AbstractResultPopulator} to convert a map of String objects to a
 * {@link RedirectAuthoriseResult}
 */
public class RedirectAuthoriseResultPopulator implements Populator<Map<String, String>, RedirectAuthoriseResult> {

    public static final String PAYMENT_STATUS = "paymentStatus";
    public static final String STATUS = "status";
    public static final String SAVE_PAYMENT_INFO = "savePaymentInfo";

    /**
     * Populates the {@link RedirectAuthoriseResult} with the values received in the URL from Worldpay.
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException
     */
    @Override
    public void populate(final Map<String, String> source, final RedirectAuthoriseResult target) throws ConversionException {
        // Validate parameters
        validateParameterNotNull(source, "Parameter source (Map<String, String>) cannot be null");
        validateParameterNotNull(target, "Parameter target (RedirectAuthoriseResult) cannot be null");

        // Example of key: MERCHANT_OWNER^MERCHANTCODE^00002000-1431098192721

        final String orderKey = source.get("orderKey");
        target.setOrderKey(orderKey);
        setOrderCode(target, orderKey);
        target.setPending(false);
        //in case of Credit Card payment - extract paymentStatus, APM payment - extract status parameter
        target.setPaymentStatus(source.get(PAYMENT_STATUS) != null ? source.get(PAYMENT_STATUS) : source.get(STATUS));
        target.setSaveCard(Boolean.valueOf(source.get(SAVE_PAYMENT_INFO)));
    }

    protected void setOrderCode(final RedirectAuthoriseResult target, final String orderKey) {
        if (orderKey != null) {
            final String[] orderKeyParts = orderKey.split("\\^");
            if (orderKeyParts.length >= 3) {
                target.setOrderCode(orderKeyParts[2]);
            }
        }
    }
}
