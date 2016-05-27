package com.worldpay.support.appender.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayUserDisplayNameAppenderTest {

    private static final String USER_NAME = "userName";

    @InjectMocks
    private WorldpayUserDisplayNameAppender testObj = new WorldpayUserDisplayNameAppender();

    @Mock
    private UserService userServiceMock;
    @Mock
    private UserModel userMock;

    @Test
    public void testAppendContent() throws Exception {
        when(userServiceMock.getCurrentUser()).thenReturn(userMock);
        when(userMock.getDisplayName()).thenReturn(USER_NAME);

        final String result = testObj.appendContent();

        assertTrue(result.contains("User: " + USER_NAME));
    }
}
    

