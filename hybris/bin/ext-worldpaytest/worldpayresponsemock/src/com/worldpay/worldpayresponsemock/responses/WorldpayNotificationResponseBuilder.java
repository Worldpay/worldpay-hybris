package com.worldpay.worldpayresponsemock.responses;

import com.worldpay.exception.WorldpayException;
import com.worldpay.worldpayresponsemock.form.ResponseForm;

/**
 * Created by admin on 26/04/16.
 */
public interface WorldpayNotificationResponseBuilder {
    String buildResponse(ResponseForm responseForm) throws WorldpayException;

    String prettifyXml(String responseXML);
}
