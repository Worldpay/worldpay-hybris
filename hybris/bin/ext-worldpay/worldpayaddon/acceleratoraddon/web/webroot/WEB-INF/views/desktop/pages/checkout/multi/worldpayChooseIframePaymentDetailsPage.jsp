<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/desktop/checkout/multi" %>
<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayaddon/desktop/checkout/multi" %>
<%@ taglib prefix="wp-address" tagdir="/WEB-INF/tags/addons/worldpayaddon/desktop/address" %>
<%@ taglib prefix="wp-cms" tagdir="/WEB-INF/tags/addons/worldpayaddon/desktop/checkout/multi" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:url value="${currentStepUrl}" var="choosePaymentMethodUrl"/>
<c:set var="showNGPP" value="${empty showNGPPIframe ? false : showNGPPIframe}"/>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

    <jsp:attribute name="pageCss">
        <c:if test="${showNGPP}">
            <link rel='stylesheet' href='https://payments.worldpay.com/resources/hpp/integrations/embedded/css/hpp-embedded-integration-library.css'/>
        </c:if>
    </jsp:attribute>

    <jsp:attribute name="pageScripts">
        <c:if test="${showNGPP}">
            <c:url var="currentServerUrl" value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}" />
            <c:url var="fullContextPath" value="${currentServerUrl}${requestScope.contextPath}" />
            <script type="text/javascript" src='https://payments.worldpay.com/resources/hpp/integrations/embedded/js/hpp-embedded-integration-library.js'></script>
            <script type="text/javascript">
                var libraryObject;
                var customOptions = {
                    type: "iframe",
                    iframeIntegrationId: 'libraryObject',
                    iframeHelperURL: "${fullContextPath}/_ui/addons/worldpayaddon/common/worldpay/nextgen/helper.html",
                    iframeBaseURL: "${currentServerUrl}",
                    url: '${paymentData.postUrl}',
                    target: 'custom-html',
                    accessibility: ${cmsPage.accessibility},
                    debug: ${cmsPage.debug},
                    language: '${currentLanguage.isocode}',
                    successURL: "${paymentData.parameters.successURL}",
                    cancelURL: "${paymentData.parameters.cancelURL}",
                    failureURL: "${paymentData.parameters.failureURL}",
                    pendingURL: "${paymentData.parameters.pendingURL}",
                    errorURL: "${paymentData.parameters.errorURL}"
                };
                $(document).ready(function () {
                    libraryObject = new WPCL.Library();
                    libraryObject.setup(customOptions);
                    $("#checkoutContentPanel").remove();
                    $("#checkoutOrderDetails").addClass("right");
                });
            </script>
        </c:if>
    </jsp:attribute>

    <jsp:body>
        <div id="globalMessages">
            <common:globalMessages/>
        </div>

        <multi-checkout:checkoutProgressBar steps="${checkoutSteps}" progressBarId="${progressBarId}"/>
        <div id="billingWrapper" class="span-14 append-1">

            <div id="checkoutContentPanel" class="clearfix">
                <div class="headline">
                    <spring:theme code="checkout.multi.paymentMethod"/>
                </div>

                <c:if test="${not empty paymentInfos}">
                    <button type="button" class="positive clear view-saved-payments" id="viewSavedPayments">
                        <spring:theme code="checkout.multi.paymentMethod.viewSavedPayments" text="View Saved Payments"/>
                    </button>
                    <wp-multi-checkout:savedPaymentInfos/>
                </c:if>

                <c:url value="/checkout/multi/worldpay/iframe/add-payment-details" var="addPaymentAddressUrl"/>
                <form:form id="worldpayBillingAddressForm" commandName="paymentDetailsForm" method="post"
                           action="${addPaymentAddressUrl}" class="create_update_payment_form">

                    <wp-cms:paymentButtons/>
                    <div class="save_payment_details hidden">
                        <sec:authorize ifNotGranted="ROLE_ANONYMOUS">
                            <form:checkbox id="SaveDetails" path="saveInAccount" tabindex="19"/>
                            <label for="SaveDetails"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.savePaymentDetailsInAccount"/></label>
                        </sec:authorize>
                    </div>

                    <wp-multi-checkout:bankSelect/>
                    <wp-multi-checkout:billingAddress/>

                    <div class="form-additionals">
                    </div>
                    <wp-multi-checkout:termsAndConditions/>
                    <div class="form-actions">
                        <c:url value="/checkout/multi/delivery-method/choose" var="chooseDeliveryMethodUrl"/>
                        <a class="button" href="${chooseDeliveryMethodUrl}"><spring:theme code="checkout.multi.cancel" text="Cancel"/></a>
                        <button class="positive right submit_worldpayHopForm" tabindex="20">
                            <spring:theme code="checkout.multi.paymentMethod.continue" text="Continue"/>
                        </button>
                    </div>
                </form:form>
            </div>
            <div id='custom-html'></div>
        </div>

        <multi-checkout:checkoutOrderDetails cartData="${cartData}" showShipDeliveryEntries="true" showPickupDeliveryEntries="true" showTax="true"/>

        <cms:pageSlot position="SideContent" var="feature" element="div" class="span-24 side-content-slot cms_disp-img_slot">
            <cms:component component="${feature}"/>
        </cms:pageSlot>
    </jsp:body>
</template:page>
