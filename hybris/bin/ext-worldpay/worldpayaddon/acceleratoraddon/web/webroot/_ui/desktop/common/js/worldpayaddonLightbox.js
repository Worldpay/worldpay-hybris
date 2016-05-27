ACC.worldpayLightbox = {

    openLightbox: function () {
        libraryObject = new WPCL.Library();
        libraryObject.setup(customOptions);
        $("#custom-trigger").click();
    },

    setUpCustomOptions: function (data) {
        customOptions["successURL"] = data.parameters.successURL;
        customOptions["failureURL"] = data.parameters.failureURL;
        customOptions["pendingURL"] = data.parameters.pendingURL;
        customOptions["errorURL"] = data.parameters.errorURL;
        customOptions["cancelURL"] = data.parameters.cancelURL;
        customOptions["url"] = data.postUrl;
    },

    requestLightboxConfiguration: function () {
        if ($("#globalMessages").children().length == 0 && $(".control-group.error").length == 0) {
            $.get(
                ACC.config.encodedContextPath + "/checkout/multi/worldpay/lightbox/payment-data",
                function (data) {
                    if (data.postUrl == null) {
                        $("#globalMessages").html("<div class='alert negative'>" + data.parameters.errorMessage + "</div>");
                    } else {
                        $("#nextGenHopPlaceHolder").html(data);
                        ACC.worldpayLightbox.setUpCustomOptions(data);
                        ACC.worldpayLightbox.openLightbox();
                    }
                }
            );
        }
    },

    getGlobalErrors: function () {
        $.get(
            ACC.config.encodedContextPath + "/checkout/multi/worldpay/lightbox/check-global-errors",
            function (data) {
                $("#globalMessages").html(data);
                ACC.worldpayLightbox.requestLightboxConfiguration();
            }
        );
    },

    submitAjaxForm: function () {
        $(".wpBillingAddress").filter(":hidden").remove();
        ACC.worldpay.enableAddressForm();
        $.post(
            ACC.config.encodedContextPath + "/checkout/multi/worldpay/lightbox/add-payment-details",
            $("#worldpayBillingAddressForm").serialize(),
            function (data) {
                $("#wpPaymentDetailsFormPlaceHolder").html(data);
                ACC.worldpayLightbox.getGlobalErrors();
                ACC.worldpay.initForm();
                ACC.worldpayLightbox.initForm();
            }
        );
    },

    bindSubmitPaymentAndBillingForm: function () {
        $(".submit_worldpayHopForm").click(function (event) {
            event.preventDefault();
            if ($("#paymentMethod_CC").is(":checked") || $("#paymentMethod_ONLINE").val() == "ONLINE") {
                $("#globalMessages").html("");
                ACC.worldpayLightbox.submitAjaxForm();
            } else {
                ACC.common.blockFormAndShowProcessingMessage($(this));
                $(".wpBillingAddress").filter(":hidden").remove();
                ACC.worldpay.enableAddressForm();
                $("#worldpayBillingAddressForm").submit();
            }
        });
    },

    initForm: function () {
        this.bindSubmitPaymentAndBillingForm();
    }
};