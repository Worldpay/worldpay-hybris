<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayaddon/desktop/checkout/multi" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/desktop/checkout/multi" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

     <jsp:attribute name="pageScripts">
        <script type="text/javascript" src="https://payments.worldpay.com/resources/cse/js/worldpay-cse-1.0.1.min.js"></script>
        <script>
            $(document).ready(function () {
                ACC.worldpayCSE.initForm();
                $("#worldpayBillingAddressForm").attr("data-worldpay", "payment-form");
                Worldpay.setPublicKey("${csePublicKey}");
            });
        </script>
    </jsp:attribute>

    <jsp:body>
        <div id="globalMessages">
            <common:globalMessages/>
        </div>

        <multi-checkout:checkoutProgressBar steps="${checkoutSteps}" progressBarId="${progressBarId}"/>

        <div class="span-14 append-1">
            <div id="checkoutContentPanel" class="clearfix">
                <c:url value="/checkout/multi/worldpay/cse/place-order" var="addCseDataUrl"/>
                <form:form id="worldpayCsePaymentForm" commandName="csePaymentForm" method="post"
                           action="${addCseDataUrl}" class="create_update_payment_form">

                    <wp-multi-checkout:worldpayCSECardDetails/>
                    <wp-multi-checkout:termsAndConditions/>

                </form:form>

                <div class="form-actions">
                    <c:url value="/checkout/multi/worldpay/choose-payment-method" var="choosePaymentMethodUrl"/>
                    <a class="button" href="${choosePaymentMethodUrl}"><spring:theme code="checkout.multi.cancel" text="Cancel"/></a>
                    <button class="positive right submit_cseDetails" tabindex="20">
                        <spring:theme code="checkout.multi.paymentMethod.continue" text="Continue"/>
                    </button>
                </div>
            </div>
        </div>

        <multi-checkout:checkoutOrderDetails cartData="${cartData}" showShipDeliveryEntries="true" showPickupDeliveryEntries="true" showTax="true"/>

        <cms:pageSlot position="SideContent" var="feature" element="div" class="span-24 side-content-slot cms_disp-img_slot">
            <cms:component component="${feature}"/>
        </cms:pageSlot>
    </jsp:body>
</template:page>