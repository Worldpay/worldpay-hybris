package com.worldpay.support.appender;

/**
 * Interface that exposes methods to append content to the support email.
 */
public interface WorldpaySupportEmailAppender {

    String TAB = "\t";

    /**
     * Returns the content to be appended to the email support service
     */
    String appendContent();
}
