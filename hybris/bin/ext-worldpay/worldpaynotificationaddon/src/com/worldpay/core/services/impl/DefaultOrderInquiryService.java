package com.worldpay.core.services.impl;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.config.WorldpayConfigLookupService;
import com.worldpay.core.services.OrderInquiryService;
import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.OrderInquiryServiceRequest;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import static java.text.MessageFormat.format;

/**
 * Default implementation fo the {@link OrderInquiryService}.
 * <p>
 * Saves payment type depending on the method code retrieved from the {@link OrderInquiryServiceResponse}
 * and creates the {@link WorldpayAPMPaymentInfoModel} if it is an APM.
 * </p>
 */
public class DefaultOrderInquiryService implements OrderInquiryService {

    private static final Logger LOG = Logger.getLogger(DefaultOrderInquiryService.class);

    private WorldpayConfigLookupService worldpayConfigLookupService;
    private WorldpayPaymentInfoService worldpayPaymentInfoService;

    /**
     * {@inheritDoc}
     *
     * @see OrderInquiryService#inquirePaymentTransaction(MerchantInfo, PaymentTransactionModel)
     */
    @Override
    public OrderInquiryServiceResponse inquirePaymentTransaction(final MerchantInfo merchantConfig, final PaymentTransactionModel paymentTransactionModel) throws WorldpayException {
        final String orderCode = paymentTransactionModel.getRequestId();
        final OrderInquiryServiceRequest orderInquiryServiceRequest = createOrderInquiryServiceRequest(merchantConfig, orderCode);
        return getWorldpayServiceGateway().orderInquiry(orderInquiryServiceRequest);
    }

    /**
     * {@inheritDoc}
     *
     * @see OrderInquiryService#processOrderInquiryServiceResponse(PaymentTransactionModel, OrderInquiryServiceResponse)
     */
    @Override
    public void processOrderInquiryServiceResponse(final PaymentTransactionModel paymentTransactionModel, final OrderInquiryServiceResponse orderInquiryServiceResponse) {
        if (!orderInquiryServiceResponse.isError()) {
            final String methodCode = orderInquiryServiceResponse.getPaymentReply().getMethodCode();
            worldpayPaymentInfoService.savePaymentType(paymentTransactionModel, methodCode);
            if (paymentTransactionModel.getInfo().getIsApm()) {
                worldpayPaymentInfoService.createWorldpayApmPaymentInfo(paymentTransactionModel);
                LOG.info(format("Converting PaymentInfo to WorldpayAPMPaymentInfo and setting timeout-date for PaymentTransaction with code [{0}] on order with code [{1}]",
                        paymentTransactionModel.getCode(), paymentTransactionModel.getOrder().getCode()));
            }
        } else {
            LOG.error(format("Order inquiry service returned error [{0}]", orderInquiryServiceResponse.getErrorDetail().getMessage()));
        }
    }

    protected OrderInquiryServiceRequest createOrderInquiryServiceRequest(MerchantInfo merchantInfo, String orderCode) throws WorldpayConfigurationException {
        final WorldpayConfig worldpayConfig = worldpayConfigLookupService.lookupConfig();
        return OrderInquiryServiceRequest.createOrderInquiryRequest(worldpayConfig, merchantInfo, orderCode);
    }

    protected WorldpayServiceGateway getWorldpayServiceGateway() {
        return WorldpayServiceGateway.getInstance();
    }

    @Required
    public void setWorldpayConfigLookupService(final WorldpayConfigLookupService worldpayConfigLookupService) {
        this.worldpayConfigLookupService = worldpayConfigLookupService;
    }

    @Required
    public void setWorldpayPaymentInfoService(final WorldpayPaymentInfoService worldpayPaymentInfoService) {
        this.worldpayPaymentInfoService = worldpayPaymentInfoService;
    }
}
