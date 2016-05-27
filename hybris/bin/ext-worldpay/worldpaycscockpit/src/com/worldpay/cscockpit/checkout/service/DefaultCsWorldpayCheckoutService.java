package com.worldpay.cscockpit.checkout.service;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.cscockpit.exceptions.ResourceMessage;
import de.hybris.platform.cscockpit.exceptions.ValidationException;
import de.hybris.platform.cscockpit.services.checkout.impl.DefaultCsCheckoutService;
import de.hybris.platform.payment.TransactionInfoService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.util.ArrayList;
import java.util.List;

import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;

public class DefaultCsWorldpayCheckoutService extends DefaultCsCheckoutService {

    /**
     * (non-Javadoc)
     *
     * @see de.hybris.platform.cscockpit.services.checkout.impl.DefaultCsCheckoutService#validateCartForCreatePayments(CartModel)
     */
    @Override
    public void validateCartForCreatePayments(final CartModel cart) throws ValidationException {

        final List errorMessages = new ArrayList();

        invokeSuperValidateCartForCreatePayments(cart);

        if (paymentAddressRequired() && cart.getPaymentAddress() == null) {
            errorMessages.add(new ResourceMessage("placeOrder.validation.noPaymentAddress"));
        }

        if (!errorMessages.isEmpty()) {
            throw new ValidationException(errorMessages);
        }
    }

    protected boolean paymentAddressRequired() {
        return isPaymentAddressRequired();
    }

    protected void invokeSuperValidateCartForCreatePayments(CartModel cart) throws ValidationException {
        super.validateCartForCreatePayments(cart);
    }

    /**
     * (non-Javadoc)
     *
     * @see de.hybris.platform.cscockpit.services.checkout.impl.DefaultCsCheckoutService#getValidPaymentTransactions(CartModel)
     */
    @Override
    public List<PaymentTransactionModel> getValidPaymentTransactions(final CartModel cart) {
        final List<PaymentTransactionModel> paymentTransactionModels = new ArrayList<>();
        if (cart != null) {
            for (final PaymentTransactionModel paymentTransactionModel : cart.getPaymentTransactions()) {
                addValidPaymentTransactionEntries(paymentTransactionModels, paymentTransactionModel);
            }
        }
        return paymentTransactionModels;
    }

    protected void addValidPaymentTransactionEntries(final List<PaymentTransactionModel> paymentTransactionModels, final PaymentTransactionModel paymentTransactionModel) {
        for (final PaymentTransactionEntryModel paymentTransactionEntryModel : paymentTransactionModel.getEntries()) {
            if (paymentTransactionEntryIsValid(paymentTransactionEntryModel)) {
                paymentTransactionModels.add(paymentTransactionModel);
                break;
            }
        }
    }

    protected boolean paymentTransactionEntryIsValid(PaymentTransactionEntryModel paymentTransactionEntryModel) {
        return AUTHORIZATION == paymentTransactionEntryModel.getType() &&
                getTransactionInfoServiceFromSuper().isSuccessful(paymentTransactionEntryModel) &&
                getTransactionInfoServiceFromSuper().isValid(paymentTransactionEntryModel);
    }

    protected TransactionInfoService getTransactionInfoServiceFromSuper() {
        return getTransactionInfoService();
    }
}
