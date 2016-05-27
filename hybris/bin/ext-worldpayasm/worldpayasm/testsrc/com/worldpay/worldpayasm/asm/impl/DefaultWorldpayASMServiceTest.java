package com.worldpay.worldpayasm.asm.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.util.AssistedServiceSession;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static de.hybris.platform.assistedservicefacades.constants.AssistedservicefacadesConstants.ASM_SESSION_PARAMETER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayASMServiceTest {

    @InjectMocks
    private DefaultWorldpayASMService testObj = new DefaultWorldpayASMService();
    @Mock
    private SessionService sessionServiceMock;
    @Mock
    private AssistedServiceSession asmSessionMock;
    @Mock
    private UserModel customerAgentModelMock;

    @Test
    public void shoudReturnTrueWhenThereIsAnASMSessionAndAnAgent() throws Exception {
        when(sessionServiceMock.getAttribute(ASM_SESSION_PARAMETER)).thenReturn(asmSessionMock);
        when(asmSessionMock.getAgent()).thenReturn(customerAgentModelMock);

        final boolean result = testObj.isASMEnabled();

        assertTrue(result);
    }

    @Test
    public void shoudReturnFalseWhenThereIsAnASMSessionAndNoAgent() throws Exception {
        when(sessionServiceMock.getAttribute(ASM_SESSION_PARAMETER)).thenReturn(asmSessionMock);
        when(asmSessionMock.getAgent()).thenReturn(null);

        final boolean result = testObj.isASMEnabled();

        assertFalse(result);
    }

    @Test
    public void shoudReturnFalseWhenThereIsNoASMSession() throws Exception {
        when(sessionServiceMock.getAttribute(ASM_SESSION_PARAMETER)).thenReturn(null);

        final boolean result = testObj.isASMEnabled();

        assertFalse(result);
    }
}