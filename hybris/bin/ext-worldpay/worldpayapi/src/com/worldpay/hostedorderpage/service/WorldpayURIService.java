package com.worldpay.hostedorderpage.service;

import com.worldpay.exception.WorldpayException;

import java.util.Map;

/**
 * Defines the methods to use when handling URL with Worldpay.
 */
public interface WorldpayURIService {

    /**
     * Gets the parameters in the {@param redirectReferenceUrl} and adds them to the map {@param params}
     * @param redirectReferenceUrl The URL to extract the parameters from.
     * @param params               The map passed as a parameter to add the extracted parameters to.
     * @throws WorldpayException
     */
    void extractUrlParamsToMap(final String redirectReferenceUrl, final Map<String, String> params) throws WorldpayException;
}
