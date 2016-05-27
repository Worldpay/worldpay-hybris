<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/desktop/checkout/multi" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayaddon/desktop/checkout/multi" %>

<c:url value="${currentStepUrl}" var="choosePaymentMethodUrl"/>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

    <jsp:attribute name="pageScripts">
        <script>
            $(document).ready(function(){
                ACC.worldpayCSE.initForm();
            });
        </script>
    </jsp:attribute>

    <jsp:body>
        <div id="globalMessages">
            <common:globalMessages/>
        </div>

        <multi-checkout:checkoutProgressBar steps="${checkoutSteps}" progressBarId="${progressBarId}"/>
        <div class="span-14 append-1 cse">
            <div id="checkoutContentPanel" class="clearfix">
                <div class="headline"><spring:theme code="checkout.multi.paymentMethod"/></div>

                <c:if test="${not empty paymentInfos}">
                    <button type="button" class="positive clear view-saved-payments" id="viewSavedPayments">
                        <spring:theme code="checkout.multi.paymentMethod.viewSavedPayments" text="View Saved Payments"/>
                    </button>
                    <wp-multi-checkout:savedPaymentInfos/>
                </c:if>

                <c:url value="/checkout/multi/worldpay/cse/add-payment-address" var="addPaymentAddressUrl"/>
                <form:form id="worldpayBillingAddressForm" commandName="paymentDetailsForm" method="post"
                           action="${addPaymentAddressUrl}" class="create_update_payment_form">

                    <wp-multi-checkout:paymentButtons/>
                    <wp-multi-checkout:bankSelect/>
                    <wp-multi-checkout:billingAddress/>
                    <div class="form-additionals">
                    </div>
                    <wp-multi-checkout:termsAndConditions/>

                    <div class="form-actions">
                        <c:url value="/checkout/multi/delivery-method/choose" var="chooseDeliveryMethodUrl"/>
                        <a class="button" href="${chooseDeliveryMethodUrl}"><spring:theme code="checkout.multi.cancel" text="Cancel"/></a>
                        <button class="positive right submit_worldpayCSEForm" tabindex="20">
                            <spring:theme code="checkout.multi.paymentMethod.continue" text="Continue"/>
                        </button>
                    </div>
                </form:form>
            </div>
        </div>

        <multi-checkout:checkoutOrderDetails cartData="${cartData}" showShipDeliveryEntries="true" showPickupDeliveryEntries="true" showTax="true"/>

        <cms:pageSlot position="SideContent" var="feature" element="div" class="span-24 side-content-slot cms_disp-img_slot">
            <cms:component component="${feature}"/>
        </cms:pageSlot>
    </jsp:body>
</template:page>
