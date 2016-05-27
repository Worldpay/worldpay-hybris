package com.worldpay.controllers.cms;

import com.worldpay.facades.APMAvailabilityFacade;
import com.worldpay.model.WorldpayAPMComponentModel;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

import static com.worldpay.controllers.cms.WorldpayAPMComponentController.IS_AVAILABLE;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayAPMComponentControllerTest {

    @Spy
    @InjectMocks
    private WorldpayAPMComponentController testObj = new WorldpayAPMComponentController();

    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private Model modelMock;
    @Mock
    private AbstractCMSComponentModel componentMock;
    @Mock
    private WorldpayAPMComponentModel worldpayAPMComponentMock;
    @Mock
    private WorldpayAPMConfigurationModel apmConfigurationMock;
    @Mock
    private APMAvailabilityFacade apmAvailabilityFacadeMock;

    @Before
    public void setup() {
        doNothing().when(testObj).invokeSuperFillModel(requestMock, modelMock, componentMock);
        doNothing().when(testObj).invokeSuperFillModel(requestMock, modelMock, worldpayAPMComponentMock);
    }

    @Test
    public void fillModelShouldInvokeSuperClassMethod() {
        testObj.fillModel(requestMock, modelMock, componentMock);

        verify(testObj).invokeSuperFillModel(requestMock, modelMock, componentMock);
    }

    @Test
    public void fillModelShouldNotAddAvailabilityAttributeForWorldpayAPMComponent() {
        testObj.fillModel(requestMock, modelMock, componentMock);

        verify(apmAvailabilityFacadeMock, never()).isAvailable(any(WorldpayAPMConfigurationModel.class));
    }

    @Test
    public void fillModelShouldAddAvailabilityAttributeForWorldpayAPMComponent() {
        when(worldpayAPMComponentMock.getApmConfiguration()).thenReturn(apmConfigurationMock);
        when(apmAvailabilityFacadeMock.isAvailable(apmConfigurationMock)).thenReturn(true);

        testObj.fillModel(requestMock, modelMock, worldpayAPMComponentMock);

        verify(modelMock).addAttribute(IS_AVAILABLE, true);
        verify(apmAvailabilityFacadeMock).isAvailable(apmConfigurationMock);
    }

}