package com.worldpay.worldpayapi.hmc;

import com.worldpay.support.WorldpaySupportService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.hmc.util.action.ActionEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;


@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayAddonInfoToolbarActionTest {

    @Spy
    @InjectMocks
    private WorldpayAddonInfoToolbarAction testObj = new WorldpayAddonInfoToolbarAction();

    @Mock
    private ActionEvent actionEventMock;
    @Mock
    private WorldpaySupportService worldpaySupportServiceMock;

    @Test
    public void performShouldSendEmailToConfiguredAddress() throws Exception {
        doReturn(worldpaySupportServiceMock).when(testObj).getWorldpaySupportService();

        testObj.perform(actionEventMock);

        verify(worldpaySupportServiceMock).sendSupportEmail();
    }
}