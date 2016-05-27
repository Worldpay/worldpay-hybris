<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title>3-D Secure helper page</title>
</head>
<body onload="submitForm()">
<spring:theme code="checkout.multi.3dsecure.autosubmit.form.text1"/>
<spring:theme code="checkout.multi.3dsecure.autosubmit.form.text2"/>

<form name="theForm" method="POST" action="${issuerURL}">
    <input type="hidden" name="PaReq" value="${paRequest}"/>
    <input type="hidden" name="TermUrl" value="${termURL}"/>
    <input type="hidden" name="MD" value="${merchantData}"/>
    <input type="submit"/>
</form>
<script type="text/javascript">
    function submitForm() {
        document.theForm.submit();
    }
</script>
</body>
</html>