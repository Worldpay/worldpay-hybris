package com.worldpay.service.impl;


import com.worldpay.service.WorldpayUrlService;

/**
 * Abstract implementation of the {@link WorldpayUrlService} allowing for success, pending, failure and cancel paths to
 * be configured
 */
public abstract class AbstractWorldpayUrlService implements WorldpayUrlService {

    private String successPath;
    private String pendingPath;
    private String failurePath;
    private String cancelPath;
    private String threeDSecureTermPath;
    private String errorPath;

    public String getSuccessPath() {
        return successPath;
    }

    public void setSuccessPath(final String successPath) {
        this.successPath = successPath;
    }

    public String getPendingPath() {
        return pendingPath;
    }

    public void setPendingPath(final String pendingPath) {
        this.pendingPath = pendingPath;
    }

    public String getFailurePath() {
        return failurePath;
    }

    public void setFailurePath(final String failurePath) {
        this.failurePath = failurePath;
    }

    public String getCancelPath() {
        return cancelPath;
    }

    public void setCancelPath(final String cancelPath) {
        this.cancelPath = cancelPath;
    }

    public void setThreeDSecureTermPath(String threeDSecureTermPath) {
        this.threeDSecureTermPath = threeDSecureTermPath;
    }

    public String getThreeDSecureTermPath() {
        return threeDSecureTermPath;
    }

    public String getErrorPath() {
        return errorPath;
    }

    public void setErrorPath(final String errorPath) {
        this.errorPath = errorPath;
    }
}
