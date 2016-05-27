<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayaddon/mobile/checkout/multi" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/mobile/checkout/multi" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

     <jsp:attribute name="pageScripts">
        <script type="text/javascript" src="https://payments.worldpay.com/resources/cse/js/worldpay-cse-1.0.1.min.js"></script>
        <script>
            $(document).ready(function () {
                $("#worldpayBillingAddressForm").attr("data-worldpay", "payment-form");
                Worldpay.setPublicKey("${csePublicKey}");
                ACCMOB.worldpayCSE.initForm();
            });
        </script>
    </jsp:attribute>

    <jsp:body>
        <div id="globalMessages">
            <common:globalMessages/>
        </div>

        <multi-checkout:checkoutProgressBar steps="${checkoutSteps}" progressBarId="${progressBarId}"/>
        <div class="span-20 last multicheckout silent-order-post-page">
            <div class="item_container_holder">
                <div class="item_container">

                    <div class="payment_details_left_col">
                        <c:url value="/checkout/multi/worldpay/cse/place-order" var="addCseDataUrl"/>
                        <form:form id="worldpayCsePaymentForm" commandName="csePaymentForm" method="post"
                                   action="${addCseDataUrl}" class="create_update_payment_form">

                            <wp-multi-checkout:worldpayCSECardDetails/>
                            <wp-multi-checkout:termsAndConditions/>

                            <span class="clear_fix">
                                <button data-theme="c" class="form submit_cseDetails" tabindex="19">
                                    <spring:theme code="mobile.checkout.multi.button.submit" text="Submit"/>
                                </button>
                            </span>
                        </form:form>
                    </div>

                </div>
            </div>
        </div>
    </jsp:body>
</template:page>