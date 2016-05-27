<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi" %>
<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayaddon/responsive/checkout/multi" %>
<%@ taglib prefix="wp-address" tagdir="/WEB-INF/tags/addons/worldpayaddon/responsive/address" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

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
            <c:url var="currentServerUrl" value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}"/>
            <c:url var="fullContextPath" value="${currentServerUrl}${requestScope.contextPath}"/>
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
                });
            </script>
        </c:if>
    </jsp:attribute>

    <jsp:body>
        <div id="hop" class="row">
            <div class="col-sm-6">
                <div class="checkout-headline">
                    <span class="glyphicon glyphicon-lock"></span>
                    <spring:theme code="checkout.multi.secure.checkout"/>
                </div>
                <multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
                <ycommerce:testId code="checkoutStepThree">
                <div class="checkout-paymentmethod">
                    <div class="checkout-indent">
                        <div id="checkoutContentPanel">
                            <ycommerce:testId code="paymentDetailsForm">
                                <div class="clearfix">
                                    <c:if test="${not empty paymentInfos}">
                                        <div class="form-group">
                                            <c:if test="${not empty paymentInfos}">
                                                <button type="button" class="btn btn-default btn-block js-saved-payments"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.useSavedCard"/></button>
                                            </c:if>
                                        </div>
                                        <wp-multi-checkout:savedPaymentInfos/>
                                    </c:if>

                                    <c:url value="/checkout/multi/worldpay/iframe/add-payment-details" var="addPaymentAddressUrl"/>
                                    <form:form id="worldpayBillingAddressForm" commandName="paymentDetailsForm" method="post"
                                               action="${addPaymentAddressUrl}" class="create_update_payment_form">

                                        <wp-multi-checkout:paymentButtons/>
                                        <wp-multi-checkout:bankSelect/>

                                        <sec:authorize ifNotGranted="ROLE_ANONYMOUS">
                                            <div class="save_payment_details checkbox">
                                                <label for="SaveDetails">
                                                    <form:checkbox id="SaveDetails" path="saveInAccount" tabindex="19"/>
                                                    <spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.savePaymentDetailsInAccount"/>
                                                </label>
                                            </div>
                                        </sec:authorize>

                                        <wp-multi-checkout:billingAddress/>
                                        <div class="form-additionals">
                                        </div>
                                        <wp-multi-checkout:termsAndConditions/>
                                        <div class="form-actions">
                                            <button class="btn btn-primary btn-block submit_worldpayHopForm checkout-next" tabindex="20">
                                                <spring:theme code="checkout.multi.paymentMethod.continue" text="Continue"/>
                                            </button>
                                        </div>
                                    </form:form>
                                </div>
                            </ycommerce:testId>
                        </div>
                        <div id='custom-html'></div>
                    </div>
                    </ycommerce:testId>
                    </multi-checkout:checkoutSteps>
                </div>
            </div>
            <div class="col-sm-6 hidden-xs">
                <multi-checkout:checkoutOrderDetails cartData="${cartData}" showDeliveryAddress="true" showPaymentInfo="false" showTaxEstimate="false" showTax="true"/>
            </div>
            <div class="col-sm-12 col-lg-12">
                <cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
                    <cms:component component="${feature}"/>
                </cms:pageSlot>
            </div>
        </div>
    </jsp:body>
</template:page>
