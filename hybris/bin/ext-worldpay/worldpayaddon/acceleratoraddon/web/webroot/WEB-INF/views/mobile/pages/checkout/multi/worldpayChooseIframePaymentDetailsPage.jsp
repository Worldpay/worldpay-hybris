<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/mobile/checkout/multi" %>
<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayaddon/mobile/checkout/multi" %>
<%@ taglib prefix="wp-cms" tagdir="/WEB-INF/tags/addons/worldpayaddon/mobile/checkout/multi" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
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
                    $(".payment_details_left_col").remove();
                    $(".submit_button").remove();
                    $(".cms-payment-button").remove();
                });
            </script>
        </c:if>
    </jsp:attribute>

    <jsp:body>
        <div id="globalMessages">
            <common:globalMessages/>
        </div>

        <multi-checkout:checkoutProgressBar steps="${checkoutSteps}" progressBarId="${progressBarId}"/>

        <div class="span-20 last multicheckout silent-order-post-page">
            <div class="item_container_holder">
                <div class="title_holder">
                    <div class="title">
                        <div class="title-top"><span></span></div>
                    </div>
                    <h2><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.header" text="Payment Details"/></h2>
                </div>
                <div class="item_container">
                    <div class="payment_details_left_col">
                        <c:if test="${not empty paymentInfos}">
                            <wp-multi-checkout:savedPaymentInfos/>
                        </c:if>
                        <c:url value="/checkout/multi/worldpay/iframe/add-payment-details" var="addPaymentAddressUrl"/>
                        <form:form id="worldpayBillingAddressForm" commandName="paymentDetailsForm" method="POST"
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
                            <wp-multi-checkout:termsAndConditions/>

                            <span class="clear_fix">
                                <button data-theme="c" class="form submit_worldpayHopForm" tabindex="19">
                                    <spring:theme code="mobile.checkout.multi.button.submit" text="Submit"/>
                                </button>
                            </span>
                        </form:form>
                    </div>
                    <div style="display:none;" id="custom-trigger" data-showNGPPIframe="${showNGPPIframe}"></div>
                    <div id='custom-html'></div>
                </div>
            </div>
        </div>
    </jsp:body>
</template:page>
