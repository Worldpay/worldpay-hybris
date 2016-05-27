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
        $(".cms-payment-button").on("change", function () {
            if ($("#paymentMethod_CC").is(":checked") || $("#paymentMethod_ONLINE").val() == "ONLINE") {
                $(".terms").hide();
            }
            else {
                $(".terms").show();
            }
        });
    },

    errorHandler: function (errorCodes) {
        for (var index in errorCodes) {
            var errorCode = errorCodes[index].toString();
            ACC.worldpayCSE.showError($("#" + ACC.worldpayCSE.errorCodeMap[errorCode]), errorCode);
        }
    },

    showError: function (errorMessageField, errorCode) {
        errorMessageField.html(ACC.addons.worldpayaddon["worldpayaddon.CSE.validation.error." + errorCode]);
        errorMessageField.removeClass("hidden");
        errorMessageField.addClass("help-inline");
        errorMessageField.closest(".control-group").addClass("error");
    },

    hideError: function (errorMessageField) {
        errorMessageField.addClass("hidden");
        errorMessageField.removeClass("help-inline");
        errorMessageField.closest(".control-group").removeClass("error");
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
                var globalMessages = $("#globalMessages");
                globalMessages.html("");
                ACC.worldpayCSE.clearCSEErrorFields();

                var submit = ACC.worldpayCSE.encryptCardDetails();
                if (!$("#Terms1").is(':checked')) {
                    globalMessages.append('<div class="alert negative">' + ACC.addons.worldpayaddon["worldpayaddon.checkout.error.terms.not.accepted"] + "</div>");
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