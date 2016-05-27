ACCMOB.worldpay = {

    spinner: $("<img src='" + ACCMOB.config.commonResourcePath + "/images/spinner.gif' />"),

    bindCountrySelector: function () {
        $('select[id^="billingAddress\\.country"]').on("change", function () {
            $('#wpUseDeliveryAddress').prop('checked', false);
        });
    },

    bindUseDeliveryAddress: function () {
        var wpUseDeliveryAddress = $("#wpUseDeliveryAddress");
        wpUseDeliveryAddress.on("change", function () {
            if ($("#wpUseDeliveryAddress").is(":checked")) {
                var options = {
                    'countryIsoCode': $('#useDeliveryAddressFields').data("countryisocode"),
                    'useDeliveryAddress': true
                };
                ACCMOB.worldpay.enableAddressForm();
                ACCMOB.worldpay.displayCreditCardAddressForm(options, ACCMOB.worldpay.useDeliveryAddressSelected);
                ACCMOB.worldpay.disableAddressForm();
            } else {
                ACCMOB.worldpay.clearAddressForm();
                ACCMOB.worldpay.enableAddressForm();
            }
        });
        if (wpUseDeliveryAddress.is(":checked")) {
            this.disableAddressForm();
        }
    },

    disableAddressForm: function () {
        $('input[id^="billingAddress\\."]').prop('disabled', true);
        $('select[id^="billingAddress\\."]').prop('disabled', true);
    },

    enableAddressForm: function () {
        $('input[id^="billingAddress\\."]').prop('disabled', false);
        $('select[id^="billingAddress\\."]').prop('disabled', false);
    },

    clearAddressForm: function () {
        $('input[id^="billingAddress\\."]').val("");
        $('select[id^="billingAddress\\."]').val("");
        $('#billingAddress\\.country').selectmenu("refresh");
        $('#billingAddress\\.region').selectmenu("refresh");
    },

    useDeliveryAddressSelected: function () {
        if ($("#wpUseDeliveryAddress").is(":checked")) {
            $("#billingAddress\\.country").val($('#useDeliveryAddressFields').data('countryisocode'));
            ACCMOB.worldpay.updateAddressFormElements();
            ACCMOB.worldpay.disableAddressForm();
        }
        else {
            ACCMOB.worldpay.clearAddressForm();
            ACCMOB.worldpay.enableAddressForm();
        }
    },

    bindCreditCardAddressForm: function () {
        $("#wpBillingCountrySelector").find(":input").on("change", function () {
            var countrySelection = $(this).val();
            var options = {
                'countryIsoCode': countrySelection,
                'useDeliveryAddress': false
            };
            ACCMOB.worldpay.displayCreditCardAddressForm(options, ACCMOB.worldpay.updateAddressFormElements);
        });
    },

    bindPaymentButtons: function () {
        $(".cms-payment-button").on("change", function () {
            ACCMOB.worldpay.hideOrShowSaveDetails();
        });
    },

    hideOrShowSaveDetails: function () {
        if ($("#paymentMethod_CC").is(":checked") || $("#paymentMethod_ONLINE").val() == "ONLINE") {
            $(".save_payment_details").removeClass("hidden");
            $(".cse #termsAndConditions").children().addClass("hidden");
        }
        else if (!$(".cms-payment-button").length) {
            $(".save_payment_details").removeClass("hidden");
        }
        else {
            $("#SaveDetails").prop('checked', false);
            $(".save_payment_details").addClass("hidden");
            $(".cse #termsAndConditions").children().removeClass("hidden");
        }
    },

    displayCreditCardAddressForm: function (options, callback) {
        $.ajax({
            url: ACCMOB.config.encodedContextPath + '/checkout/multi/worldpay/billingaddressform',
            async: true,
            data: options,
            dataType: "html",
            beforeSend: function () {
                $("#wpBillingAddress").html(ACCMOB.worldpay.spinner);
            }
        }).done(function (data) {
            $("#wpBillingAddress").html($(data).html());
            if (typeof callback == 'function') {
                callback.call();
            }
        });
    },

    updateAddressFormElements: function () {
        $('#billingAddress\\.country').selectmenu("refresh");
        var wpBillingAddress = $("#wpBillingAddress");
        wpBillingAddress.find("input[type='checkbox']").checkboxradio();
        wpBillingAddress.find("input[type='text']").textinput();
        wpBillingAddress.find("[data-role=button]").button();
        wpBillingAddress.find("fieldset").controlgroup();
        wpBillingAddress.find("select").selectmenu();
    },

    populateDeclineCodeTimeout: function () {
        var waitTimer = ACCMOB.worldpayDeclineMessageWaitTimerSeconds * 1000;
        setTimeout(function () {
            populateDeclineCode();
        }, waitTimer);
        function populateDeclineCode() {
            $.ajax({
                url: ACCMOB.config.encodedContextPath + "/checkout/multi/worldpay/choose-payment-method/getDeclineMessage",
                type: "GET",
                success: function (data) {
                    if (data != "") {
                        $(".declineMessage").remove();
                        $(".error.mFormList").append("<li class=\"declineMessage\">" + data + "</li>");
                    }
                },
                error: function (err) {
                    console.log(err);
                }
            });
        }
    },

    populateBankListByAPM: function (selectedAPM, callback) {
        $.get(ACCMOB.config.encodedContextPath + "/worldpay/" + selectedAPM + "/banks", function (response) {
            var placeHolderMessage = ACCMOB.addons.worldpayaddon["worldpayaddon.checkout.paymentMethod.shopperbankcode.default"];
            $("#shopperBankCode").empty().append($("<option></option>")
                .attr("selected", "").attr("disabled", true).text(placeHolderMessage)).selectmenu("refresh");

            $.each(response, function (key, value) {
                $("#shopperBankCode").append($("<option></option>")
                    .attr("value", value.bankCode).text(value.bankName));
            });
            $("#bankElement").removeClass("hidden");
            if (callback != null) {
                callback();
            }
        });
    },

    checkPreviouslySelectedBank: function () {
        var selectedAPM = $("[name='paymentMethod']:checked");
        if (selectedAPM.data("isbank")) {
            this.populateBankListByAPM(selectedAPM.val(), function () {
                var shopperBankCodeSelectElem = $("#shopperBankCode");
                var selectedBankCode = shopperBankCodeSelectElem.data("bankcode");
                if (selectedBankCode != undefined) {
                    shopperBankCodeSelectElem.val(selectedBankCode);
                    shopperBankCodeSelectElem.selectmenu("refresh");
                }
            });
        }
    },

    checkPreviouslySelectedPaymentMethod: function () {
        var paymentMethodCode = $("#paymentButtons").data("paymentmethod");
        $('#paymentMethod_' + paymentMethodCode).prop('checked', true);
        this.checkPreviouslySelectedBank();
    },

    bindBanks: function () {
        $(".cms-payment-button").on("change", function () {
            var selectedAPM = $("[name='paymentMethod']:checked");
            if (selectedAPM.data("isbank")) {
                ACCMOB.worldpay.populateBankListByAPM(selectedAPM.val());
            } else {
                $("#bankElement").addClass("hidden");
            }
        });
    },

    initForm: function () {
        this.bindUseDeliveryAddress();
        this.bindCountrySelector();
        this.bindCreditCardAddressForm();
        this.bindPaymentButtons();
        if (ACCMOB.paymentStatus == "REFUSED") {
            this.populateDeclineCodeTimeout();
        }
        this.hideOrShowSaveDetails();
        this.bindBanks();
        this.checkPreviouslySelectedPaymentMethod();
    }
};

$(document).ready(function () {
    ACCMOB.worldpay.initForm();
});
