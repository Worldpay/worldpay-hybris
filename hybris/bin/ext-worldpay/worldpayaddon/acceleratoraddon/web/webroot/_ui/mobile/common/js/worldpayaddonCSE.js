ACCMOB.worldpayCSE = {

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

    errorHandler: function (errorCodes) {
        for (var index in errorCodes) {
            var errorCode = errorCodes[index].toString();
            $("#" + ACCMOB.worldpayCSE.errorCodeMap[errorCode]).addClass("form_field_error");
        }
    },

    clearCSEErrorFields: function () {
        $('span[id$="-span"]').removeClass("form_field_error");
    },

    bindSubmitBillingAddressForm: function () {
        $(".submit_worldpayCSEForm").click(function (event) {
            event.preventDefault();
            ACCMOB.common.showPageLoadingMsg();
            $(".wpBillingAddress").filter(":hidden").remove();
            ACCMOB.worldpay.enableAddressForm();
            $("#worldpayBillingAddressForm").submit();
        });
    },

    buildErrorMessage: function (errorMsgKey) {
        return "<ul class='error mFormList'><li>" + ACCMOB.addons.worldpayaddon[errorMsgKey] + "</li></ul>";
    },

    showErrorMessage: function (errorMessageKey, header) {
        $.mobile.easydialog({
            content: this.buildErrorMessage(errorMessageKey),
            header: header,
            type: 'error'
        });
    },

    bindSubmitCseForm: function () {
        $(".submit_cseDetails").click(
            function (event) {
                event.preventDefault();
                var termsAndConditions = $("#termsAndConditions").find("label").first();
                termsAndConditions.removeClass("errorBorder");
                ACCMOB.worldpayCSE.clearCSEErrorFields();

                var form = $("#worldpayCsePaymentForm");
                var submit = ACCMOB.worldpayCSE.encryptCardDetails(form);
                var termsAndConditionsElement = $("#Terms1");
                if (!termsAndConditionsElement.is(':checked')) {
                    ACCMOB.worldpayCSE.showErrorMessage("worldpayaddon.checkout.error.terms.not.accepted", termsAndConditionsElement.data('headertext'));
                } else if (submit) {
                    form.submit();
                }
            }
        );
    },

    populateErrorCodeMap: function () {
        this.errorCodeMap["101"] = "number-span";
        this.errorCodeMap["102"] = "number-span";
        this.errorCodeMap["103"] = "number-span";
        this.errorCodeMap["201"] = "cvc-span";
        this.errorCodeMap["301"] = "exp-date-span";
        this.errorCodeMap["302"] = "exp-date-span";
        this.errorCodeMap["303"] = "exp-date-span";
        this.errorCodeMap["304"] = "exp-date-span";
        this.errorCodeMap["305"] = "exp-date-span";
        this.errorCodeMap["306"] = "exp-date-span";
        this.errorCodeMap["401"] = "nameOnCard-span";
        this.errorCodeMap["402"] = "nameOnCard-span";
    },

    initForm: function () {
        this.populateErrorCodeMap();
        this.bindSubmitCseForm();
        this.bindSubmitBillingAddressForm();
    }
};
