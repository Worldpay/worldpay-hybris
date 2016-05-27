package com.worldpay.service;

import com.worldpay.exception.WorldpayConfigurationException;

/**
 * Interface defining the WorldpayUrlService. Implementing service needs to be able to provide fully qualified urls for
 * the success, pending, failure and cancel responses from redirect payment
 */
public interface WorldpayUrlService {

    /**
     * Get the fully qualified success url to provide to third party payment provider
     *
     * @return Fully qualified success url
     */
    String getFullSuccessURL() throws WorldpayConfigurationException;

    /**
     * Get the fully qualified pending url to provide to third party payment provider
     *
     * @return Fully qualified pending url
     */
    String getFullPendingURL() throws WorldpayConfigurationException;

    /**
     * Get the fully qualified failure url to provide to third party payment provider
     *
     * @return Fully qualified failure url
     */
    String getFullFailureURL() throws WorldpayConfigurationException;

    /**
     * Get the fully qualified cancel url to provide to third party payment provider
     *
     * @return Fully qualified cancel url
     */
    String getFullCancelURL() throws WorldpayConfigurationException;

    /**
     * Get the fully qualified error url to provide to third party payment provider
     *
     * @return Fully qualified error url
     */
    String getFullErrorURL() throws WorldpayConfigurationException;

    /**
     * Get the fully qualified 3D secure termURL to provide to card issuer
     *
     * @return Fully qualified 3D secure termURL
     */
    String getFullThreeDSecureTermURL() throws WorldpayConfigurationException;
}
