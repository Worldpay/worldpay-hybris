package com.worldpay.hostedorderpage.service.impl;

import com.worldpay.exception.WorldpayException;
import com.worldpay.hostedorderpage.service.WorldpayURIService;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayURIService implements WorldpayURIService {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayURIService.class);
    protected static final String UNABLE_TO_PARSE_URL = "Unable to parse url [{0}]";
    protected static final String UNABLE_TO_PARSE_URL_REDIRECT_URL = "Unable to parse url redirect url";

    /**
     * {@inheritDoc}
     */
    @Override
    public void extractUrlParamsToMap(final String redirectReferenceUrl, final Map<String, String> params) throws WorldpayException {
        extractParametersFromUrl(redirectReferenceUrl).stream().forEach(nameValuePair -> params.put(nameValuePair.getName(), nameValuePair.getValue()));
    }

    protected List<NameValuePair> extractParametersFromUrl(final String redirectUrl) throws WorldpayException {
        try {
            URIBuilder uriBuilder = new URIBuilder(redirectUrl);
            return uriBuilder.getQueryParams();
        } catch (URISyntaxException e) {
            LOG.error(MessageFormat.format(UNABLE_TO_PARSE_URL, redirectUrl), e);
            throw new WorldpayException(UNABLE_TO_PARSE_URL_REDIRECT_URL, e);
        }
    }
}
