<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script type="text/javascript">
	var getTermsAndConditionsUrl = "${getTermsAndConditionsUrl}";
</script>

<span id="termsAndConditions" class="termsCheck">
	<spring:theme code="text.headertext.error" var="headerText"/>
	<form:checkbox id="Terms1" name="Terms1" path="termsCheck" data-theme="d"  data-headerText='${headerText}'/>
	<label for="Terms1"><spring:theme code="checkout.summary.placeOrder.readTermsAndConditions"/></label>
</span>