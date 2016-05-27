ACCMOB.worldpayRedirect = {

    bindSubmitBillingAddressForm: function () {
        $(".submit_worldpayRedirectForm").click(function (event) {
            event.preventDefault();
            ACCMOB.common.showPageLoadingMsg();
            $(".wpBillingAddress").filter(":hidden").remove();
            ACCMOB.worldpay.enableAddressForm();
            $("#worldpayBillingAddressForm").submit();
        });
    },

    initForm: function () {
        this.bindSubmitBillingAddressForm();
    }
};


