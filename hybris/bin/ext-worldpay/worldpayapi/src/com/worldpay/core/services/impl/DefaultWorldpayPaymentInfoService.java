package com.worldpay.core.services.impl;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.enums.token.TokenEvent;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.model.Date;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.payment.Card;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import com.worldpay.service.response.CreateTokenResponse;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.UUID;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Default implementation fo the {@link WorldpayPaymentInfoService}.
 * <p>
 * The implementation is responsible for creating the {@link WorldpayAPMPaymentInfoModel} or the {@link CreditCardPaymentInfoModel}
 * and for calculating the timeout date for APMs.
 * </p>
 */
public class DefaultWorldpayPaymentInfoService implements WorldpayPaymentInfoService {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayPaymentInfoService.class);
    protected static final String WORLDPAY_CREDIT_CARD_MAPPINGS = "worldpay.creditCard.mappings.";

    private ModelService modelService;
    private EnumerationService enumerationService;
    private APMConfigurationLookupService apmConfigurationLookupService;
    private ConfigurationService configurationService;

    /**
     * {@inheritDoc}
     *
     * @see WorldpayPaymentInfoService#savePaymentType(PaymentTransactionModel, String)
     */
    @Override
    public void savePaymentType(final PaymentTransactionModel transactionModel, final String methodCode) {
        final PaymentInfoModel transactionPaymentInfo = transactionModel.getInfo();
        final PaymentInfoModel orderPaymentInfo = transactionModel.getOrder().getPaymentInfo();
        transactionPaymentInfo.setPaymentType(methodCode);
        orderPaymentInfo.setPaymentType(methodCode);
        modelService.saveAll(orderPaymentInfo, transactionPaymentInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPaymentInfoModel(final PaymentTransactionModel paymentTransactionModel, final AbstractOrderModel orderModel, final OrderNotificationMessage orderNotificationMessage) {
        final PaymentInfoModel paymentInfo = getPaymentInfoModel(paymentTransactionModel, orderNotificationMessage);
        attachPaymentInfoModel(paymentTransactionModel, orderModel, paymentInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAndAttachPaymentInfoModel(final PaymentTransactionModel paymentTransactionModel, final AbstractOrderModel orderModel, final PaymentInfoModel paymentInfoModel) {
        updatePaymentInfo(orderModel, paymentInfoModel);
        attachPaymentInfoModel(paymentTransactionModel, orderModel, paymentInfoModel);
    }

    /**
     * {@inheritDoc}
     *
     * @see WorldpayPaymentInfoService#createWorldpayApmPaymentInfo(PaymentTransactionModel)
     */
    @Override
    public WorldpayAPMPaymentInfoModel createWorldpayApmPaymentInfo(final PaymentTransactionModel paymentTransactionModel) {
        final WorldpayAPMPaymentInfoModel apmPaymentInfoModel = modelService.clone(paymentTransactionModel.getInfo(), WorldpayAPMPaymentInfoModel.class);
        final WorldpayAPMConfigurationModel worldpayAPMConfigurationModel = apmConfigurationLookupService.getAPMConfigurationForCode(apmPaymentInfoModel.getPaymentType());
        apmPaymentInfoModel.setApmConfiguration(worldpayAPMConfigurationModel);
        apmPaymentInfoModel.setSaved(false);
        apmPaymentInfoModel.setTimeoutDate(calculateAPMTimeoutDate(paymentTransactionModel.getCreationtime(), apmPaymentInfoModel.getApmConfiguration()));
        modelService.save(apmPaymentInfoModel);
        return apmPaymentInfoModel;
    }

    /**
     * {@inheritDoc}
     *
     * @see WorldpayPaymentInfoService#createPaymentInfo(CartModel)
     */
    @Override
    public PaymentInfoModel createPaymentInfo(final CartModel cartModel) {
        validateParameterNotNull(cartModel, "CartModel cannot be null");
        final PaymentInfoModel paymentInfoModel = modelService.create(PaymentInfoModel.class);
        paymentInfoModel.setUser(cartModel.getUser());
        paymentInfoModel.setSaved(false);
        paymentInfoModel.setCode(generateCcPaymentInfoCode(cartModel));
        return updatePaymentInfo(cartModel, paymentInfoModel);
    }

    /**
     * {@inheritDoc}
     *
     * @see WorldpayPaymentInfoService#createCreditCardPaymentInfo(CartModel, CreateTokenResponse, boolean)
     */
    @Override
    public CreditCardPaymentInfoModel createCreditCardPaymentInfo(final CartModel cartModel, final CreateTokenResponse createTokenResponse, final boolean saveCard) {
        validateParameterNotNull(cartModel, "CartModel cannot be null");
        validateParameterNotNull(createTokenResponse, "Token response cannot be null");

        final TokenReply tokenReply = createTokenResponse.getToken();
        final TokenEvent tokenEvent = TokenEvent.valueOf(tokenReply.getTokenDetails().getTokenEvent().toUpperCase());
        if (tokenEvent.equals(TokenEvent.MATCH)) {
            final CreditCardPaymentInfoModel customerSavedCard = findMatchingTokenisedCard(cartModel.getUser(), tokenReply.getTokenDetails().getPaymentTokenID());
            if (customerSavedCard != null) {
                if (!customerSavedCard.isSaved()) {
                    customerSavedCard.setSaved(saveCard);
                }
                cartModel.setPaymentInfo(customerSavedCard);
                modelService.save(cartModel);
                return customerSavedCard;
            }
        }

        final CreditCardPaymentInfoModel creditCardPaymentInfoModel = modelService.create(CreditCardPaymentInfoModel.class);
        creditCardPaymentInfoModel.setCode(generateCcPaymentInfoCode(cartModel));
        creditCardPaymentInfoModel.setWorldpayOrderCode(cartModel.getWorldpayOrderCode());
        creditCardPaymentInfoModel.setUser(cartModel.getUser());
        creditCardPaymentInfoModel.setType(CreditCardType.TOKEN);
        updateCreditCardModel(tokenReply, creditCardPaymentInfoModel, saveCard);
        return creditCardPaymentInfoModel;
    }

    @Override
    public void setCreditCardType(final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final PaymentReply paymentReply) {
        updateCreditCardType(creditCardPaymentInfoModel, paymentReply);
        modelService.save(creditCardPaymentInfoModel);
    }

    @Override
    public CreditCardPaymentInfoModel updateCreditCardPaymentInfo(final CartModel cartModel, final UpdateTokenServiceRequest updateTokenServiceRequest) {
        final String paymentTokenId = updateTokenServiceRequest.getUpdateTokenRequest().getPaymentTokenId();

        final CreditCardPaymentInfoModel matchingTokenisedCard = findMatchingTokenisedCard(cartModel.getUser(), paymentTokenId);
        if (matchingTokenisedCard != null) {
            final CardDetails cardDetails = updateTokenServiceRequest.getUpdateTokenRequest().getCardDetails();
            matchingTokenisedCard.setCcOwner(cardDetails.getCardHolderName());
            matchingTokenisedCard.setValidToMonth(cardDetails.getExpiryDate().getMonth());
            matchingTokenisedCard.setValidToYear(cardDetails.getExpiryDate().getYear());
            modelService.save(matchingTokenisedCard);
            return matchingTokenisedCard;
        }
        return null;
    }

    private void updateCreditCardType(final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final PaymentReply paymentReply) {
        final String methodCode = paymentReply.getMethodCode();
        final String creditCardTypeValue = getHybrisCCTypeForWPCCType(methodCode);
        final CreditCardType cardType = enumerationService.getEnumerationValue(CreditCardType.class.getSimpleName(), creditCardTypeValue);
        creditCardPaymentInfoModel.setPaymentType(methodCode);
        creditCardPaymentInfoModel.setType(cardType);
    }

    protected PaymentInfoModel getPaymentInfoModel(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage) {
        final PaymentReply paymentReply = orderNotificationMessage.getPaymentReply();
        savePaymentType(paymentTransactionModel, paymentReply.getMethodCode());
        final PaymentInfoModel paymentTransactionModelInfo = paymentTransactionModel.getInfo();
        if (paymentTransactionModelInfo.getIsApm()) {
            return createWorldpayApmPaymentInfo(paymentTransactionModel);
        }
        return createAndSaveCreditCard(paymentTransactionModel, orderNotificationMessage);
    }

    protected CreditCardPaymentInfoModel findMatchingTokenisedCard(final UserModel userModel, final String paymentTokenId) {
        Collection<PaymentInfoModel> paymentInfos = userModel.getPaymentInfos();
        for (PaymentInfoModel paymentInfoModel : paymentInfos) {
            if (paymentInfoModel instanceof CreditCardPaymentInfoModel) {
                CreditCardPaymentInfoModel customerSavedCard = (CreditCardPaymentInfoModel) paymentInfoModel;
                if (paymentTokenId.equals(customerSavedCard.getSubscriptionId())) {
                    return customerSavedCard;
                }
            }
        }
        return null;
    }

    /**
     * This methods creates a CreditCardPaymentInfo with the information received from Worldpay.
     * If the response contains a token there are two cases to consider:
     * - The customer used the card details of an already tokenised card but not from the saved cards functionality:
     * -- The tokenEvent will contain the value "MATCH" or "CONFLICT" and the method searches for the CreditCardPaymentInfo that has that subscriptionId
     * -- If the card is not found in the customer's profile it will be created and attached to the customer.
     * - The customer used a non tokenised card:
     * -- The tokenEvent will contain the value "NEW" and a new CreditCardPaymentInfo will be created and associated to the customer's profile
     *
     * @param paymentTransactionModel  the {@PaymentTransactionModel} that belongs to the order
     * @param orderNotificationMessage the {@OrderNotificationMessage} received from Worldpay in the Redirect/HOP flow
     * @return
     */
    protected CreditCardPaymentInfoModel createAndSaveCreditCard(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage) {
        final PaymentReply paymentReply = orderNotificationMessage.getPaymentReply();

        final TokenReply tokenReply = orderNotificationMessage.getTokenReply();
        if (tokenReply != null) {
            return getCreditCardWithTokenInformation(paymentTransactionModel, paymentReply, tokenReply);
        } else {
            final PaymentInfoModel paymentInfoModel = paymentTransactionModel.getInfo();
            if (paymentInfoModel instanceof CreditCardPaymentInfoModel) {
                final CreditCardPaymentInfoModel creditCardPaymentInfoModel = (CreditCardPaymentInfoModel) paymentInfoModel;
                if (StringUtils.isNotBlank(creditCardPaymentInfoModel.getSubscriptionId())) {
                    return creditCardPaymentInfoModel;
                }
            }
            return cloneAndSaveCreditCardWithPaymentInformation(paymentTransactionModel, paymentReply);
        }
    }

    protected CreditCardPaymentInfoModel getCreditCardWithTokenInformation(final PaymentTransactionModel paymentTransactionModel, final PaymentReply paymentReply, final TokenReply tokenReply) {
        final TokenEvent tokenEvent = TokenEvent.valueOf(tokenReply.getTokenDetails().getTokenEvent().toUpperCase());
        if (shouldSearchForExistingToken(tokenEvent)) {
            final String paymentTokenID = tokenReply.getTokenDetails().getPaymentTokenID();
            final CreditCardPaymentInfoModel customerSavedCard = findMatchingTokenisedCard(paymentTransactionModel.getOrder().getUser(), paymentTokenID);
            if (customerSavedCard != null) {
                return customerSavedCard;
            }
        }
        return cloneAndSaveCreditCardWithTokenInformation(paymentTransactionModel, tokenReply, paymentReply);
    }

    protected boolean shouldSearchForExistingToken(final TokenEvent tokenEvent) {
        return tokenEvent.equals(TokenEvent.MATCH) || tokenEvent.equals(TokenEvent.CONFLICT);
    }

    protected CreditCardPaymentInfoModel cloneAndSaveCreditCardWithPaymentInformation(final PaymentTransactionModel paymentTransactionModel, final PaymentReply paymentReply) {
        final CreditCardPaymentInfoModel creditCardPaymentInfoModel = modelService.clone(paymentTransactionModel.getInfo(), CreditCardPaymentInfoModel.class);
        creditCardPaymentInfoModel.setOriginal(null);
        creditCardPaymentInfoModel.setDuplicate(false);
        final Card card = paymentReply.getCardDetails();
        final Date expiryDate = card.getExpiryDate();

        setCardInformation(creditCardPaymentInfoModel, card.getCardNumber(), expiryDate.getMonth(), expiryDate.getYear(), card.getCardHolderName());

        updateCreditCardType(creditCardPaymentInfoModel, paymentReply);

        modelService.save(creditCardPaymentInfoModel);
        return creditCardPaymentInfoModel;
    }

    protected CreditCardPaymentInfoModel cloneAndSaveCreditCardWithTokenInformation(final PaymentTransactionModel paymentTransactionModel,
                                                                                    final TokenReply tokenReply,
                                                                                    final PaymentReply paymentReply) {
        final CreditCardPaymentInfoModel creditCardPaymentInfoModel = modelService.clone(paymentTransactionModel.getInfo(), CreditCardPaymentInfoModel.class);
        updateCreditCardType(creditCardPaymentInfoModel, paymentReply);
        return updateCreditCardModel(tokenReply, creditCardPaymentInfoModel, true);
    }

    private void attachPaymentInfoModel(final PaymentTransactionModel paymentTransactionModel, final AbstractOrderModel orderModel, final PaymentInfoModel paymentInfoModel) {
        orderModel.setPaymentInfo(paymentInfoModel);
        paymentTransactionModel.setInfo(paymentInfoModel);
        modelService.saveAll(orderModel, paymentTransactionModel);
    }

    private CreditCardPaymentInfoModel updateCreditCardModel(final TokenReply tokenReply, final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final boolean saveCard) {
        creditCardPaymentInfoModel.setSubscriptionId(tokenReply.getTokenDetails().getPaymentTokenID());

        final Card card = tokenReply.getPaymentInstrument();
        final Date date = card.getExpiryDate();
        setCardInformation(creditCardPaymentInfoModel, card.getCardNumber(), date.getMonth(), date.getYear(), card.getCardHolderName());

        creditCardPaymentInfoModel.setSaved(saveCard);
        creditCardPaymentInfoModel.setAuthenticatedShopperID(tokenReply.getAuthenticatedShopperID());
        creditCardPaymentInfoModel.setEventReference(tokenReply.getTokenDetails().getTokenEventReference());
        final DateTime dt = getDateTime(tokenReply.getTokenDetails().getPaymentTokenExpiry());
        creditCardPaymentInfoModel.setExpiryDate(dt.toDate());
        modelService.save(creditCardPaymentInfoModel);
        return creditCardPaymentInfoModel;
    }

    private void setCardInformation(final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final String cardNumber, final String month, final String year, final String cardHolderName) {
        creditCardPaymentInfoModel.setNumber(cardNumber);
        creditCardPaymentInfoModel.setValidToMonth(month);
        creditCardPaymentInfoModel.setValidToYear(year);
        creditCardPaymentInfoModel.setCcOwner(cardHolderName);
    }

    protected String generateCcPaymentInfoCode(final AbstractOrderModel cartModel) {
        return cartModel.getCode() + "_" + UUID.randomUUID();
    }

    protected DateTime getDateTime(final Date paymentTokenExpiryDate) {
        return new DateTime().withDate(Integer.valueOf(paymentTokenExpiryDate.getYear()), Integer.valueOf(paymentTokenExpiryDate.getMonth()), Integer.valueOf(paymentTokenExpiryDate.getDayOfMonth()));
    }

    private PaymentInfoModel updatePaymentInfo(final AbstractOrderModel cartModel, final PaymentInfoModel paymentInfoModel) {
        paymentInfoModel.setWorldpayOrderCode(cartModel.getWorldpayOrderCode());
        paymentInfoModel.setPaymentAmount(cartModel.getTotalPrice());
        paymentInfoModel.setPaymentCurrency(cartModel.getCurrency());
        modelService.save(paymentInfoModel);
        return paymentInfoModel;
    }

    protected java.util.Date calculateAPMTimeoutDate(final java.util.Date creationTime, final WorldpayAPMConfigurationModel apmConfiguration) {
        final Integer autoCancelPendingTimeoutInMinutes = apmConfiguration.getAutoCancelPendingTimeoutInMinutes();
        if (autoCancelPendingTimeoutInMinutes == null) {
            LOG.warn(MessageFormat.format("No auto cancel pending timeout found for APM configuration with code [{0}]", apmConfiguration.getCode()));
            return null;
        }
        return addMinutesToDate(creationTime, autoCancelPendingTimeoutInMinutes);
    }

    protected java.util.Date addMinutesToDate(final java.util.Date creationTime, final Integer autoCancelPendingTimeoutInMinutes) {
        return DateUtils.addMinutes(creationTime, autoCancelPendingTimeoutInMinutes);
    }

    /**
     * Fetch the Hybris credit card type value for the corresponding Worldpay credit card type value.
     *
     * @param methodCode Worldpay Credit Card Type.
     * @return Corresponding Hybris credit card type.
     */
    protected String getHybrisCCTypeForWPCCType(final String methodCode) {
        return configurationService.getConfiguration().getString(WORLDPAY_CREDIT_CARD_MAPPINGS + methodCode);
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    @Required
    public void setApmConfigurationLookupService(final APMConfigurationLookupService apmConfigurationLookupService) {
        this.apmConfigurationLookupService = apmConfigurationLookupService;
    }

    @Required
    public void setEnumerationService(final EnumerationService enumerationService) {
        this.enumerationService = enumerationService;
    }

    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
