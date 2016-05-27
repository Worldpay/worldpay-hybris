package com.worldpay.worldpayasm.asm.impl;

import com.worldpay.worldpayasm.asm.WorldpayASMService;
import de.hybris.platform.assistedservicefacades.util.AssistedServiceSession;
import de.hybris.platform.servicelayer.session.SessionService;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.assistedservicefacades.constants.AssistedservicefacadesConstants.ASM_SESSION_PARAMETER;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayASMService implements WorldpayASMService {

    private SessionService sessionService;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isASMEnabled() {
        final AssistedServiceSession asmSession = sessionService.getAttribute(ASM_SESSION_PARAMETER);
        return asmSession != null && asmSession.getAgent() != null;
    }

    @Required
    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }
}
