package com.worldpay.support.impl;

import com.worldpay.support.WorldpaySupportEmailService;
import com.worldpay.support.WorldpaySupportService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.mail.MailUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class DefaultWorldpaySupportService implements WorldpaySupportService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorldpaySupportService.class);

    protected static final String WORLDPAY_SUPPORT_EMAIL_CONFIG_KEY = "worldpay.support.email.address";
    protected static final String WORLDPAY_SUPPORT_EMAIL_DISPLAY_NAME_KEY = "worldpay.support.email.display.name";
    protected static final String EMAIL_SUBJECT_KEY = "worldpay.support.email.subject";

    protected static final String CUSTOMER_EMAIL_CONFIG_KEY = "customer.support.email.address";
    protected static final String CUSTOMER_EMAIL_DISPLAY_NAME_KEY = "customer.support.email.display.name";
    protected static final String CUSTOMER_REPLY_TO_ADDRESS = "customer.support.email.address.reply.to";

    private ConfigurationService configurationService;
    private WorldpaySupportEmailService worldpaySupportEmailService;

    @Override
    public void sendSupportEmail() {
        final String emailAddress = configurationService.getConfiguration().getString(WORLDPAY_SUPPORT_EMAIL_CONFIG_KEY);
        final String emailDisplayName = configurationService.getConfiguration().getString(WORLDPAY_SUPPORT_EMAIL_DISPLAY_NAME_KEY);

        final String customerEmailAddress = configurationService.getConfiguration().getString(CUSTOMER_EMAIL_CONFIG_KEY);
        final String customerEmailDisplayName = configurationService.getConfiguration().getString(CUSTOMER_EMAIL_DISPLAY_NAME_KEY);

        final String replyToAddress = configurationService.getConfiguration().getString(CUSTOMER_REPLY_TO_ADDRESS);
        final String subject = configurationService.getConfiguration().getString(EMAIL_SUBJECT_KEY);

        final Email email;
        try {
            email = getPreConfiguredEmail();
            email.addTo(emailAddress, emailDisplayName);
            email.setFrom(customerEmailAddress, customerEmailDisplayName);
            email.addReplyTo(replyToAddress);
            email.setSubject(subject);
            email.setMsg(worldpaySupportEmailService.createEmailBody());
            email.send();
        } catch (EmailException e) {
            LOG.error("Error creating or sending email", e);
        }
    }

    protected Email getPreConfiguredEmail() throws EmailException {
        return MailUtils.getPreConfiguredEmail();
    }

    @Required
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Required
    public void setWorldpaySupportEmailService(final WorldpaySupportEmailService worldpaySupportEmailService) {
        this.worldpaySupportEmailService = worldpaySupportEmailService;
    }
}
