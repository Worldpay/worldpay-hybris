package com.worldpay.support.impl;

import com.worldpay.support.WorldpaySupportEmailService;
import com.worldpay.support.appender.WorldpaySupportEmailAppender;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

public class DefaultWorldpaySupportEmailService implements WorldpaySupportEmailService {

    private List<WorldpaySupportEmailAppender> emailAppenders;

    @Override
    public String createEmailBody() {
        final StringBuilder bodyBuilder = new StringBuilder();
        for (final WorldpaySupportEmailAppender emailAppender : emailAppenders) {
            bodyBuilder.append(emailAppender.appendContent());
        }
        return bodyBuilder.toString();
    }

    @Required
    public void setEmailAppenders(List<WorldpaySupportEmailAppender> emailAppenders) {
        this.emailAppenders = emailAppenders;
    }
}
