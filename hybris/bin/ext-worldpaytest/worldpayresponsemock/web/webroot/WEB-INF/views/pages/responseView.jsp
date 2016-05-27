<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="responseMock" tagdir="/WEB-INF/tags" %>

<html>
<head>
    <title>Mock</title>
    <script type="text/javascript" src="https://code.jquery.com/jquery-2.2.1.min.js"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
    <style>
        body, html {
            height: 100%;
        }

        body {
            text-align: center;
            background: #f3f3f3;
        }

        form,
        .xml-response {
            display: inline-block;
            padding: 15px;
            text-align: left;
            width: 100%;
            float: left;
        }

        .xml-response pre,
        fieldset {
            background: #fff;
        }

        .xml-response pre {
            white-space: pre-line;
            font-size: 12px;
        }

        fieldset {
            padding: 20px 20px 10px;
            margin: 0 0 10px;
        }

        label {
            width: 100%;
            margin: 0 0 10px;
        }

        label span:first-of-type,
        label input,
        label select {
            width: 100%;
            display: inline-block;
            float: left;
        }

        label span:first-of-type {
            padding: 2px 10px 2px 0;
        }

        label input,
        label select {
            margin: 0;
            font-weight: normal;
        }

        .btn {
            float: right;
            width: 100%;
            margin: 10px 0 5px;
        }

        @media only screen and (min-width: 720px) {
            form {
                width: 60%;
            }

            .xml-response {
                width: 40%;
            }

            .xml-response {
                margin-top: 56px;
                padding-left: 0;
            }

            label span:first-of-type {
                float: left;
                width: 40%;
                padding: 2px 10px 0 0;
            }

            label input,
            label select {
                float: right;
                width: 60%;
            }

            .btn {
                width: auto;
            }
        }

        @media only screen and (min-width: 980px) {
            form,
            .xml-response {
                width: 50%;
            }

            .xml-response pre {
                white-space: pre-wrap;
            }
        }

        @media only screen and (min-width: 1200px) {
            form {
                width: 40%;
            }

            .xml-response {
                width: 60%;
            }
        }

        @media only screen and (min-width: 1600px) {
            form {
                width: 30%;
            }

            .xml-response {
                width: 70%;
            }
        }
    </style>
</head>
<body>
<form:form action="sendSelectedResponse" commandName="responseForm" method="POST">
    <h3>Worldpay Response Mock - Developer's only</h3>
    <fieldset>
        <label>General</label>
        <form:label path="worldpayOrderCode">
            <span>Worldpay order code:</span>
            <form:input path="worldpayOrderCode"/>
        </form:label>
        <form:label path="siteId">
            <span>Site:</span>
            <form:select path="siteId">
                <form:options items="${availableSites}"/>
            </form:select>
        </form:label>
        <form:label path="merchantCode">
            <span>Merchant Code:</span>
            <form:select path="merchantCode">
                <form:options items="${merchants}"/>
            </form:select>
        </form:label>
        <form:label path="selectedPaymentMethod">
            <span>Payment method</span>
            <form:select path="selectedPaymentMethod">
                <form:option value="CC">Credit/Debit Card</form:option>
                <form:option value="APM">APM</form:option>
            </form:select>
        </form:label>
    </fieldset>

    <fieldset id="sharedFields">
        <label>Common</label>
        <form:label path="transactionAmount">
            <span>Transaction Amount:<div title="The amount should be without decimal point" class="glyphicon glyphicon-info-sign"></div></span>
            <form:input path="transactionAmount"/>
        </form:label>
        <form:label path="currencyCode">
            <span>Currency Code:</span>
            <form:input path="currencyCode"/>
        </form:label>
        <form:label path="exponent">
            <span>Exponent:</span>
            <form:select path="exponent">
                <form:option value="2"/>
                <form:option value="0"/>
                <form:option value="3"/>
            </form:select>
        </form:label>
        <form:label path="lastEvent">
            <span>Last event:</span>
            <form:select path="lastEvent">
                <form:options items="${possibleEvents}"/>
            </form:select>
        </form:label>
        <form:label path="journalType">
            <span>Notification type:</span>
            <form:select path="journalType">
                <form:options items="${possibleEvents}"/>
            </form:select>
        </form:label>
        <form:label path="reference">
            <span>Reference:</span>
            <form:input path="reference"/>
        </form:label>
    </fieldset>

    <fieldset>
        <label>Risk</label>
        <label for="selectRiskScore">
            <span>Risk Score</span>
            <form:select path="selectedRiskScore" id="selectRiskScore">
                <option value="NoRisk">No risk score</option>
                <option value="RMM">Risk Management Module</option>
                <option value="RG">Risk Guardian</option>
            </form:select>
        </label>

        <fieldset id="RG" class="hide">
            <label>Risk Guardian</label>
            <form:label path="finalScore">
                <span>Final Score:</span>
                <form:input path="finalScore"/>
            </form:label>
        </fieldset>
        <fieldset id="RMM" class="hide">
            <label>Risk Management Module</label>
            <form:label path="riskValue">
                <span>Risk Value:</span>
                <form:input path="riskValue"/>
            </form:label>
        </fieldset>
    </fieldset>

    <fieldset>
        <label>Token</label>
        <label for="selectToken">
            <span>Select Token</span>
            <form:select path="selectToken" id="selectToken">
                <option value="NoToken">No Token</option>
                <option value="Token">Token</option>
            </form:select>
        </label>

        <fieldset id="Token" class="hide">
            <label>Token information</label>
            <form:label path="authenticatedShopperId">
                <span>Authenticated Shopper ID:</span>
                <form:input path="authenticatedShopperId"/>
            </form:label>
            <form:label path="tokenEventReference">
                <span>Token Event Reference:</span>
                <form:input path="tokenEventReference"/>
            </form:label>
            <form:label path="tokenReason">
                <span>Token Reason:</span>
                <form:input path="tokenReason"/>
            </form:label>
            <form:label path="tokenEvent">
                <span>Token Event:</span>
                <form:select path="tokenEvent">
                    <form:options items="${tokenEvents}"/>
                </form:select>
            </form:label>
            <form:label path="paymentTokenId">
                <span>Payment Token ID:</span>
                <form:input path="paymentTokenId"/>
            </form:label>
            <form:label path="tokenExpiryDay">
                <span>Token Expiry Day:</span>
                <form:input path="tokenExpiryDay"/>
            </form:label>
            <form:label path="tokenExpiryMonth">
                <span>Token Expiry Month:</span>
                <form:input path="tokenExpiryMonth"/>
            </form:label>
            <form:label path="tokenExpiryYear">
                <span>Token Expiry Year:</span>
                <form:input path="tokenExpiryYear"/>
            </form:label>
            <form:label path="tokenDetailsEventReference">
                <span>Token details event reference:</span>
                <form:input path="tokenDetailsEventReference"/>
            </form:label>
            <form:label path="tokenDetailsReason">
                <span>Token Details Reason:</span>
                <form:input path="tokenDetailsReason"/>
            </form:label>
            <form:label path="cardExpiryMonth">
                <span>Card Expiry Month:</span>
                <form:input path="cardExpiryMonth"/>
            </form:label>
            <form:label path="cardExpiryYear">
                <span>Card Expiry Year:</span>
                <form:input path="cardExpiryYear"/>
            </form:label>
            <form:label path="tokenCardHolderName">
                <span>Tokenised Cardholder Name:</span>
                <form:input path="tokenCardHolderName"/>
            </form:label>
            <form:label path="lastName">
                <span>Last Name:</span>
                <form:input path="lastName"/>
            </form:label>
            <form:label path="address1">
                <span>Address1:</span>
                <form:input path="address1"/>
            </form:label>
            <form:label path="address2">
                <span>Address2:</span>
                <form:input path="address2"/>
            </form:label>
            <form:label path="address3">
                <span>Address3:</span>
                <form:input path="address3"/>
            </form:label>
            <form:label path="postalCode">
                <span>Postal Code:</span>
                <form:input path="postalCode"/>
            </form:label>
            <form:label path="city">
                <span>City:</span>
                <form:input path="city"/>
            </form:label>
            <form:label path="countryCode">
                <span>Country Code:</span>
                <form:input path="countryCode"/>
            </form:label>
            <form:label path="cardBrand">
                <span>Card Brand:</span>
                <form:select path="cardBrand">
                    <form:options items="${paymentMethods}"/>
                </form:select>
            </form:label>
            <form:label path="cardSubBrand">
                <span>Card Sub Brand:</span>
                <form:input path="cardSubBrand"/>
            </form:label>
            <form:label path="issuerCountry">
                <span>Issuer Country:</span>
                <form:input path="issuerCountry"/>
            </form:label>
            <form:label path="obfuscatedPAN">
                <span>Obfuscated PAN:</span>
                <form:select path="obfuscatedPAN">
                    <form:options items="${testCreditCards}"/>
                </form:select>
            </form:label>
        </fieldset>
    </fieldset>

    <fieldset id="APMFields" class="hide">
        <label>APM</label>
        <form:label path="apmPaymentType">
            <span>Payment method:</span>
            <form:select path="apmPaymentType">
                <form:options items="${paymentMethodAPMs}"/>
            </form:select>
        </form:label>
    </fieldset>

    <fieldset id="creditCardFields">
        <label>Credit card</label>
        <form:label path="testCreditCard">
            <span>Credit Card Number:</span>
            <form:select path="testCreditCard">
                <form:options items="${testCreditCards}"/>
            </form:select>
        </form:label>
        <form:label path="ccPaymentType">
            <span>Payment method:</span>
            <form:select path="ccPaymentType">
                <form:options items="${paymentMethods}"/>
            </form:select>
        </form:label>
        <form:label path="cardMonth">
            <span>Card Month:</span>
            <form:input path="cardMonth"/>
        </form:label>
        <form:label path="cardYear">
            <span>Card Year:</span>
            <form:input path="cardYear"/>
        </form:label>
        <form:label path="cardHolderName">
            <span>Cardholder Name:</span>
            <form:input path="cardHolderName"/>
        </form:label>
        <form:label path="currentDay">
            <span>Current Day:</span>
            <form:input path="currentDay"/>
        </form:label>
        <form:label path="currentMonth">
            <span>Current Month:</span>
            <form:input path="currentMonth"/>
        </form:label>
        <form:label path="currentYear">
            <span>Current Year:</span>
            <form:input path="currentYear"/>
        </form:label>
        <form:label path="responseCode">
            <span>ISO8583 return code:</span>
            <form:select path="responseCode">
                <form:options items="${responseCodes}"/>
            </form:select>
        </form:label>
    </fieldset>

    <fieldset id="AmExAdvancedVerification">
        <label>AAV</label>
        <responseMock:aavSelectBox path="aavAddress" labelText="Aav Address" />
        <responseMock:aavSelectBox path="aavPostcode" labelText="Aav Postcode" />
        <responseMock:aavSelectBox path="aavCardholderName" labelText="Aav Cardholder Name" />
        <responseMock:aavSelectBox path="aavTelephone" labelText="Aav Telephone" />
        <responseMock:aavSelectBox path="aavEmail" labelText="Aav Email" />
    </fieldset>

    <fieldset id="shopperWebformRefundDetails" class="hide">
        <label>Shopper Webform Refund</label>
        <form:label path="webformId">
            <span>Webform Id:</span>
            <form:input path="webformId"/>
        </form:label>
        <form:label path="paymentId">
            <span>Payment Id:</span>
            <form:input path="paymentId"/>
        </form:label>
        <form:label path="webformStatus">
            <span>Webform Status:</span>
            <form:input path="webformStatus"/>
        </form:label>
        <form:label path="refundReason">
            <span>Refund reason:</span>
            <form:input path="refundReason"/>
        </form:label>
        <form:label path="webformURL">
            <span>Webform URL:</span>
            <form:input path="webformURL"/>
        </form:label>
        <form:label path="refundId">
            <span>Refund Id:</span>
            <form:input path="refundId"/>
        </form:label>
    </fieldset>

    <button type="submit" class="btn btn-primary btn-lg" aria-label="Send">
        <span class="glyphicon glyphicon-ok" aria-hidden="true"></span> Send
    </button>

</form:form>

<c:if test="${not empty xmlResponse}">
    <div class="xml-response">
			<pre>
				<c:out value="${xmlResponse}"/>
			</pre>
    </div>
</c:if>
<script type="text/javascript">

    $("#selectedPaymentMethod").change(function () {
        var paymentMethod = $("#selectedPaymentMethod").val();
        if (paymentMethod === "APM") {
            $("#creditCardFields").addClass("hide");
            $("#APMFields").removeClass("hide");
        } else if (paymentMethod === "CC") {
            $("#APMFields").addClass("hide");
            $("#creditCardFields").removeClass("hide");
        }
    });

    $("#selectRiskScore").change(function () {
        var riskHandler = $("#selectRiskScore").val();
        if (riskHandler === "NoRisk") {
            $("#RG").addClass("hide");
            $("#RMM").addClass("hide");
        } else if (riskHandler === "RMM") {
            $("#RG").addClass("hide");
            $("#RMM").removeClass("hide");
        } else if (riskHandler === "RG") {
            $("#RG").removeClass("hide");
            $("#RMM").addClass("hide");
        }
    });

    $("#selectToken").change(function(){
        var tokenHandler = $("#selectToken").val();
        if (tokenHandler === "NoToken"){
            $("#Token").addClass("hide");
        } else if (tokenHandler === "Token"){
            $("#Token").removeClass("hide");
        }
    });

    $("#journalType").change(function() {
        var journalType = $("#journalType").val();
        if (journalType === "REFUND_WEBFORM_ISSUED") {
            $("#shopperWebformRefundDetails").removeClass("hide");
        } else {
            $("#shopperWebformRefundDetails").addClass("hide");
        }
    })



    $("#siteId").change(function () {
        $.getJSON("/worldpayresponsemock/merchants/" + $("#siteId").val(), function (response) {
            $("#merchantCode").empty();
            $.each(response, function (key, value) {
                $("#merchantCode").append($("<option></option>")
                        .attr("value", value).text(value));
            });

        });
    });
</script>

</body>
</html>
