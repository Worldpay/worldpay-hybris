package com.worldpay.worldpayresponsemock.responses.impl;

import com.worldpay.exception.WorldpayException;
import com.worldpay.internal.model.AAVAddressResultCode;
import com.worldpay.internal.model.AAVCardholderNameResultCode;
import com.worldpay.internal.model.AAVEmailResultCode;
import com.worldpay.internal.model.AAVPostcodeResultCode;
import com.worldpay.internal.model.AAVTelephoneResultCode;
import com.worldpay.internal.model.AccountTx;
import com.worldpay.internal.model.Address;
import com.worldpay.internal.model.Amount;
import com.worldpay.internal.model.ISO8583ReturnCode;
import com.worldpay.internal.model.Journal;
import com.worldpay.internal.model.Notify;
import com.worldpay.internal.model.OrderStatusEvent;
import com.worldpay.internal.model.Payment;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.ShopperWebformRefundDetails;
import com.worldpay.internal.model.Token;
import com.worldpay.service.marshalling.impl.DefaultPaymentServiceMarshaller;
import com.worldpay.worldpayresponsemock.builders.WebformRefundBuilder;
import com.worldpay.worldpayresponsemock.form.ResponseForm;
import com.worldpay.worldpayresponsemock.responses.WorldpayNotificationResponseBuilder;
import org.apache.log4j.Logger;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

import static com.worldpay.worldpayresponsemock.builders.AddressBuilder.anAddressBuilder;
import static com.worldpay.worldpayresponsemock.builders.AmountBuilder.anAmountBuilder;
import static com.worldpay.worldpayresponsemock.builders.JournalBuilder.aJournalBuilder;
import static com.worldpay.worldpayresponsemock.builders.PaymentBuilder.aPaymentBuilder;
import static com.worldpay.worldpayresponsemock.builders.TokenBuilder.aTokenBuilder;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

public class DefaultWorldpayNotificationResponseBuilder implements WorldpayNotificationResponseBuilder {

    private static final Logger LOG = Logger.getLogger(WorldpayNotificationResponseBuilder.class);

    protected static final String REFUND_WEBFORM_ISSUED = "REFUND_WEBFORM_ISSUED";
    private static final String IN_PROCESS_AUTHORISED = "IN_PROCESS_AUTHORISED";
    private static final String TOKEN = "Token";

    @Override
    public String buildResponse(ResponseForm responseForm) throws WorldpayException {
        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(responseForm.getMerchantCode());
        final Notify notify = new Notify();
        final OrderStatusEvent orderStatusEvent = new OrderStatusEvent();
        // Payment
        orderStatusEvent.setOrderCode(responseForm.getWorldpayOrderCode());
        final Amount amount = anAmountBuilder().withAmount(responseForm.getTransactionAmount()).withCurrencyCode(responseForm.getCurrencyCode()).build();

        final Payment payment = createPayment(responseForm);
        orderStatusEvent.setPayment(payment);
        final Journal journal = createJournal(responseForm, amount);
        orderStatusEvent.setJournal(journal);

        if (equalsIgnoreCase(responseForm.getSelectToken(), DefaultWorldpayNotificationResponseBuilder.TOKEN)) {
            final Token token = createToken(responseForm);
            orderStatusEvent.setToken(token);
        }

        if (responseForm.getJournalType().equalsIgnoreCase(DefaultWorldpayNotificationResponseBuilder.REFUND_WEBFORM_ISSUED)) {
            final ShopperWebformRefundDetails shopperWebformRefundDetails = createShopperWebformRefundDetails(responseForm);
            orderStatusEvent.setShopperWebformRefundDetails(shopperWebformRefundDetails);
        }

        final ISO8583ReturnCode iso8583ReturnCode = generateReturnCode(responseForm);
        notify.getOrderStatusEventOrReport().add(orderStatusEvent);
        payment.setISO8583ReturnCode(iso8583ReturnCode);
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(notify);
        return getPaymentServiceMarshaller().marshal(paymentService);
    }

    @Override
    public String prettifyXml(final String responseXML) {
        try {
            final Source xmlInput = new StreamSource(new StringReader(
                    responseXML));
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            final StreamResult prettyXml = new StreamResult(new StringWriter());
            transformer.transform(xmlInput, prettyXml);
            return prettyXml.getWriter().toString();
        } catch (TransformerException e) {
            LOG.warn("Couldn't prettify the xml to show it in the responseMock", e);
            return "There was an error prettifying your xml - Sorry \n" + responseXML;
        }
    }

    protected ShopperWebformRefundDetails createShopperWebformRefundDetails(ResponseForm responseForm) {
        return WebformRefundBuilder.aWebformRefundBuilder().
                withExponent(String.valueOf(responseForm.getExponent())).
                withCurrencyCode(responseForm.getCurrencyCode()).
                withPaymentId(responseForm.getPaymentId()).
                withRefundId(responseForm.getRefundId()).
                withTransactionAmount(responseForm.getTransactionAmount()).
                withRefundReason(responseForm.getRefundReason()).
                withWebformId(responseForm.getWebformId()).
                withWebformStatus(responseForm.getWebformStatus()).
                withWebformURL(responseForm.getWebformURL()).build();
    }

    protected Payment createPayment(final ResponseForm responseForm) {
        final Payment payment = aPaymentBuilder().withPaymentMethod(responseForm.getSelectedPaymentMethod())
                .withApmPaymentMethod(responseForm.getApmPaymentType()).withCreditCardPaymentMethod(responseForm.getCcPaymentType())
                .withCardHolderName(responseForm.getCardHolderName()).withCardNumber(responseForm.getTestCreditCard())
                .withCurrencyCode(responseForm.getCurrencyCode()).withExpiryMonth(responseForm.getCardMonth()).withExponent(String.valueOf(responseForm.getExponent()))
                .withExpiryYear(responseForm.getCardYear())
                .withTransactionAmount(responseForm.getTransactionAmount()).withSelectedRiskScore(responseForm.getSelectedRiskScore()).withRiskValue(responseForm.getRiskValue())
                .withFinalScore(responseForm.getFinalScore()).withLastEvent(responseForm.getLastEvent()).withRefundReference(responseForm.getReference()).build();
        populateAavFields(responseForm, payment);
        return payment;
    }

    private void populateAavFields(final ResponseForm responseForm, final Payment payment) {
        final AAVTelephoneResultCode telephoneResultCode = new AAVTelephoneResultCode();
        telephoneResultCode.getDescription().add(responseForm.getAavTelephone());
        payment.setAAVTelephoneResultCode(telephoneResultCode);

        final AAVPostcodeResultCode postcodeResultCode = new AAVPostcodeResultCode();
        postcodeResultCode.getDescription().add(responseForm.getAavPostcode());
        payment.setAAVPostcodeResultCode(postcodeResultCode);

        final AAVAddressResultCode addressResultCode = new AAVAddressResultCode();
        addressResultCode.getDescription().add(responseForm.getAavAddress());
        payment.setAAVAddressResultCode(addressResultCode);

        final AAVCardholderNameResultCode cardholderNameResultCode = new AAVCardholderNameResultCode();
        cardholderNameResultCode.getDescription().add(responseForm.getAavCardholderName());
        payment.setAAVCardholderNameResultCode(cardholderNameResultCode);

        final AAVEmailResultCode emailResultCode = new AAVEmailResultCode();
        emailResultCode.getDescription().add(responseForm.getAavEmail());
        payment.setAAVEmailResultCode(emailResultCode);
    }

    protected Journal createJournal(final ResponseForm responseForm, final Amount amount) {
        final AccountTx accountTx = new AccountTx();
        accountTx.setAccountType(IN_PROCESS_AUTHORISED);
        accountTx.setAmount(amount);
        accountTx.setBatchId("19");

        return aJournalBuilder().withAmount(amount).withJournalType(responseForm.getJournalType())
                .withBookingDate(responseForm.getCurrentDay(), responseForm.getCurrentMonth(), responseForm.getCurrentYear())
                .build();
    }

    protected Token createToken(final ResponseForm responseForm) {
        return aTokenBuilder().withAuthenticatedShopperId(responseForm.getAuthenticatedShopperId())
                .withTokenExpiryDate(responseForm.getTokenExpiryDay(), responseForm.getTokenExpiryMonth(), responseForm.getTokenExpiryYear())
                .withTokenDetailsTokenReason(responseForm.getTokenDetailsReason()).withTokenId(responseForm.getPaymentTokenId())
                .withTokenDetailsTokenEventReference(responseForm.getTokenDetailsEventReference()).withTokenEvent(responseForm.getTokenEvent())
                .withTokenReason(responseForm.getTokenReason()).withTokenEventReference(responseForm.getTokenEventReference()).withCardBrand(responseForm.getCardBrand())
                .withCardSubBrand(responseForm.getCardSubBrand()).withIssuerCountryCode(responseForm.getIssuerCountry()).withObfuscatedPAN(responseForm.getObfuscatedPAN())
                .withCardExpiryDate(responseForm.getCardExpiryMonth(), responseForm.getCardExpiryYear()).withCardHolderName(responseForm.getCardHolderName())
                .withCardAddress(createAddressForCardDetails(responseForm)).build();
    }

    private Address createAddressForCardDetails(final ResponseForm responseForm) {
        return anAddressBuilder().withAddress1(responseForm.getAddress1()).withAddress2(responseForm.getAddress2())
                .withAddress3(responseForm.getAddress3()).withLastName(responseForm.getLastName()).withCity(responseForm.getCity())
                .withPostalCode(responseForm.getPostalCode()).withCountryCode(responseForm.getCountryCode()).build();
    }

    private ISO8583ReturnCode generateReturnCode(final ResponseForm responseForm) {
        final ISO8583ReturnCode iso8583ReturnCode = new ISO8583ReturnCode();
        iso8583ReturnCode.setCode(String.valueOf(responseForm.getResponseCode()));
        iso8583ReturnCode.setDescription(responseForm.getResponseDescription());
        return iso8583ReturnCode;
    }

    protected DefaultPaymentServiceMarshaller getPaymentServiceMarshaller() {
        return DefaultPaymentServiceMarshaller.getInstance();
    }
}
