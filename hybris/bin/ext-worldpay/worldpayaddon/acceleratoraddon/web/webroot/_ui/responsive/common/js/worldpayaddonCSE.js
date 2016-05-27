ACC.worldpayCSE = {

    errorCodeMap: {},

    encryptCardDetails: function () {
        var data = {
            cvc: $("#cvc").val(),
            cardHolderName: $("#nameOnCard").val(),
            cardNumber: $("#number").val(),
            expiryMonth: $("#exp-month").val(),
            expiryYear: $("#exp-year").val()
        };
        var encryptedData = Worldpay.encrypt(data, this.errorHandler);
        if (encryptedData) {
            $("#encryptedData").val(encryptedData);
            return true;
        } else {
            return false;
        }
    },

    bindPaymentButtons: function () {
        var paymentButtonsSlot = $(".cms-payment-button");
        if (paymentButtonsSlot.length && paymentButtonsSlot.children().length > 0) {
            paymentButtonsSlot.on("change", function () {
                if ($("#paymentMethod_CC").is(":checked") || $("#paymentMethod_ONLINE").val() == "ONLINE") {
                    $(".terms").addClass("hidden");
                }
                else {
                    $(".terms").removeClass("hidden");
                }
            });
        } else if (paymentButtonsSlot.length) {
            $(".terms").addClass("hidden");
        }
    },

    errorHandler: function (errorCodes) {
        for (var index in errorCodes) {
            var errorCode = errorCodes[index].toString();
            ACC.worldpayCSE.showError($("#" + ACC.worldpayCSE.errorCodeMap[errorCode]), errorCode);
        }
    },

    showError: function (errorMessageField, errorCode) {
        errorMessageField.html(ACC.addons.worldpayaddon["worldpayaddon.CSE.validation.error." + errorCode]);
        errorMessageField.parent().removeClass("hidden");
        errorMessageField.closest(".form-group").addClass("has-error");

    },

    hideError: function (errorMessageField) {
        errorMessageField.addClass("hidden");
        errorMessageField.closest(".form-group").removeClass("has-error");
    },

    clearCSEErrorFields: function () {
        this.hideError($('div[id^="error-"]'));
    },

    bindSubmitBillingAddressForm: function () {
        $(".submit_worldpayCSEForm").click(
            function (event) {
                event.preventDefault();
                ACC.common.blockFormAndShowProcessingMessage($(this));
                $(".wpBillingAddress").filter(":hidden").remove();
                ACC.worldpay.enableAddressForm();
                $("#worldpayBillingAddressForm").submit();
            }
        );
    },

    bindSubmitCseForm: function () {
        $(".submit_cseDetails").click(
            function (event) {
                event.preventDefault();
                var container = $(".checkout-headline").parent();
                container.find(".global-alerts").remove();

                ACC.worldpayCSE.clearCSEErrorFields();

                var submit = ACC.worldpayCSE.encryptCardDetails();
                if (!$("#Terms1").is(':checked')) {
                    container.prepend(
                        "<div class='global-alerts'>" +
                        "<div class='alert alert-danger alert-dismissable'>" +
                        "<button class='close' aria-hidden='true' data-dismiss='alert' type='button'>&times;</button>" +
                        ACC.addons.worldpayaddon["worldpayaddon.checkout.error.terms.not.accepted"] +
                        "</div></div>"
                    );
                } else if (submit) {
                    $("#worldpayCsePaymentForm").submit();
                }
            }
        );
    },

    populateErrorCodeMap: function () {
        this.errorCodeMap["101"] = "error-number";
        this.errorCodeMap["102"] = "error-number";
        this.errorCodeMap["103"] = "error-number";
        this.errorCodeMap["201"] = "error-cvc";
        this.errorCodeMap["301"] = "error-exp-date";
        this.errorCodeMap["302"] = "error-exp-date";
        this.errorCodeMap["303"] = "error-exp-date";
        this.errorCodeMap["304"] = "error-exp-date";
        this.errorCodeMap["305"] = "error-exp-date";
        this.errorCodeMap["306"] = "error-exp-date";
        this.errorCodeMap["401"] = "error-nameOnCard";
        this.errorCodeMap["402"] = "error-nameOnCard";
    },

    initForm: function () {
        this.bindSubmitBillingAddressForm();
        this.populateErrorCodeMap();
        this.bindSubmitCseForm();
        this.bindPaymentButtons();
    }
};