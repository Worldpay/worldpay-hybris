ACC.worldpay = {

    spinner: $("<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />"),

    bindCountrySelector: function () {
        $('select[id^="billingAddress\\.country"]').on("change", function () {
            $('#wpUseDeliveryAddress').prop('checked', false);
        });
    },

    bindUseDeliveryAddress: function () {
        var wpUseDeliveryAddress = $("#wpUseDeliveryAddress");

        wpUseDeliveryAddress.on("change", function () {
            if (wpUseDeliveryAddress.is(":checked")) {
                var options = {
                    'countryIsoCode': $('#wpUseDeliveryAddress').data("countryisocode"),
                    'useDeliveryAddress': true
                };
                ACC.worldpay.enableAddressForm();
                ACC.worldpay.displayCreditCardAddressForm(options, ACC.worldpay.useDeliveryAddressSelected);
                ACC.worldpay.disableAddressForm();
            } else {
                ACC.worldpay.clearAddressForm();
                ACC.worldpay.enableAddressForm();
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
    },

    useDeliveryAddressSelected: function () {
        var wpUseDeliveryAddress = $("#wpUseDeliveryAddress");
        if (wpUseDeliveryAddress.is(":checked")) {
            $('select[id^="billingAddress\\.country"]').val(wpUseDeliveryAddress.data('countryisocode'));
            ACC.worldpay.disableAddressForm();
        }
        else {
            this.clearAddressForm();
            this.enableAddressForm();
        }
    },

    bindCreditCardAddressForm: function () {
        $("#wpBillingCountrySelector").find(":input").on("change", function () {
            var countrySelection = $(this).val();
            var options = {
                'countryIsoCode': countrySelection,
                'useDeliveryAddress': false
            };
            ACC.worldpay.displayCreditCardAddressForm(options);
        })
    },

    displayCreditCardAddressForm: function (options, callback) {
        $.ajax({
            url: ACC.config.encodedContextPath + '/checkout/multi/worldpay/billingaddressform',
            data: options,
            dataType: "html",
            beforeSend: function () {
                $("#wpBillingAddress").html(ACC.worldpay.spinner);
            }
        }).done(function (data) {
            $("#wpBillingAddress").html($(data).html());
            if (typeof callback == 'function') {
                callback.call();
            }
        });
    },

    populateDeclineCodeTimeout: function () {
        var waitTimer = ACC.worldpayDeclineMessageWaitTimerSeconds * 1000;
        setTimeout(function () {
            populateDeclineCode();
        }, waitTimer);
        function populateDeclineCode() {
            $.ajax({
                url: ACC.config.encodedContextPath + "/checkout/multi/worldpay/choose-payment-method/getDeclineMessage",
                type: "GET",
                success: function (data) {
                    if (data) {
                        var row = $('#hop');
                        var container = row.parent();

                        var declineCodeHTMLContent = "<div class='global-alerts'>" +
                            "<div class='alert alert-danger alert-dismissable'>" +
                            "<button class='close' aria-hidden='true' data-dismiss='alert' type='button'>×</button>"
                            + data +
                            "</div>";

                        var globalAlerts = container.find(".global-alerts");
                        if (globalAlerts) {
                            globalAlerts.last().append(declineCodeHTMLContent);
                        } else {
                            container.prepend(declineCodeHTMLContent);
                        }
                    }
                },
                error: function (err) {
                    console.log(err);
                }
            });
        }
    },

    hideOrShowSaveDetails: function () {
        $(".cms-payment-button").on("change", function () {
            if ($("#paymentMethod_CC").is(":checked") || $("#paymentMethod_ONLINE").val() == "ONLINE") {
                $(".save_payment_details").removeClass("hidden");
            }
            else if (!$(".cms-payment-button").length) {
                $(".save_payment_details").removeClass("hidden");
            }
            else {
                $("#SaveDetails").prop('checked', false);
                $(".save_payment_details").addClass("hidden");
            }
        });
    },

    populateBankListByAPM: function (selectedAPM, callback) {
        $.get(ACC.config.encodedContextPath + "/worldpay/" + selectedAPM + "/banks", function (response) {
            var placeHolderMessage = ACC.addons.worldpayaddon["worldpayaddon.checkout.paymentMethod.shopperbankcode.default"];
            $("#shopperBankCode").empty().append($("<option></option>")
                .attr("selected", true).attr("disabled", true).text(placeHolderMessage));

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
                ACC.worldpay.populateBankListByAPM(selectedAPM.val());
            } else {
                $("#bankElement").addClass("hidden");
            }
        });
    },

    initForm: function () {
        this.bindUseDeliveryAddress();
        this.bindCountrySelector();
        this.bindCreditCardAddressForm();
        if (ACC.paymentStatus == "REFUSED") {
            this.populateDeclineCodeTimeout();
        }
        this.hideOrShowSaveDetails();
        this.bindBanks();
        this.checkPreviouslySelectedPaymentMethod();
    }
};

$(document).ready(function () {
    ACC.worldpay.initForm();
});