<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/mobile/checkout/multi" %>
<%@ taglib prefix="wp-cms" tagdir="/WEB-INF/tags/addons/worldpayaddon/mobile/checkout/multi" %>
<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayaddon/mobile/checkout/multi" %>

<c:url value="${currentStepUrl}" var="choosePaymentMethodUrl"/>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
    <jsp:attribute name="pageScripts">
        <script>
            $(document).ready(function () {
                ACCMOB.worldpayCSE.initForm();
            });
        </script>
    </jsp:attribute>
    <jsp:body>
        <div id="globalMessages">
            <common:globalMessages/>
        </div>

        <multi-checkout:checkoutProgressBar steps="${checkoutSteps}" progressBarId="${progressBarId}"/>

        <div class="span-20 last multicheckout silent-order-post-page cse">
            <div class="item_container_holder">
                <div class="title_holder">
                    <div class="title">
                        <div class="title-top"><span></span></div>
                    </div>
                    <h2><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.header" text="Payment Details"/></h2>
                </div>

                <div class="item_container">
                    <c:if test="${not empty paymentInfos}">
                        <wp-multi-checkout:savedPaymentInfos/>
                    </c:if>

                    <div class="payment_details_left_col">
                        <c:url value="/checkout/multi/worldpay/cse/add-payment-address" var="addPaymentAddressUrl"/>
                        <form:form id="worldpayBillingAddressForm"
                                   commandName="paymentDetailsForm"
                                   method="POST"
                                   action="${addPaymentAddressUrl}"
                                   class="create_update_payment_form">

                            <wp-cms:paymentButtons/>
                            <wp-multi-checkout:bankSelect/>
                            <wp-multi-checkout:billingAddress/>
                            <wp-multi-checkout:termsAndConditions/>

                            <span class="clear_fix">
                                <button data-theme="c" class="form submit_worldpayCSEForm" tabindex="19">
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
