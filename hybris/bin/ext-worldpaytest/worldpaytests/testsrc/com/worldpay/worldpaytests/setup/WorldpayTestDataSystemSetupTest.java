package com.worldpay.worldpaytests.setup;

import com.worldpay.worldpaytests.orders.WorldpayOrderTestData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.worldpaytests.constants.WorldpaytestsConstants.EXTENSIONNAME;
import static com.worldpay.worldpaytests.setup.WorldpayTestDataSystemSetup.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayTestDataSystemSetupTest {

    private static final String YES = "yes";
    private static final String NO = "no";

    @InjectMocks
    private WorldpayTestDataSystemSetup testObj = new WorldpayTestDataSystemSetup();
    @Mock
    private SystemSetupContext contextMock;
    @Mock
    private WorldpayOrderTestData worldpayOrderTestDataMock;
    @Mock
    private CronJobService cronJobServiceMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private CronJobModel cronJobMock;
    @Mock
    private ModelService modelServiceMock;

    @Before
    public void setup() {
        when(contextMock.getExtensionName()).thenReturn(EXTENSIONNAME);
        when(cronJobServiceMock.getCronJob(ORDER_MODIFICATION_PROCESSOR_JOB)).thenReturn(cronJobMock);
    }

    @Test
    public void shouldCreateProjectDataWhenFlagIsYes() throws Exception {
        when(contextMock.getParameter(contextMock.getExtensionName() + "_" + CREATE_PERFORMANCE_CRONJOB_TEST_DATA)).thenReturn(YES);

        testObj.createProjectData(contextMock);

        final InOrder inOrder = inOrder(cronJobMock, worldpayOrderTestDataMock, modelServiceMock);
        inOrder.verify(cronJobMock).setActive(false);
        inOrder.verify(modelServiceMock).save(cronJobMock);
        inOrder.verify(worldpayOrderTestDataMock).createPerformanceCronJobData();
    }

    @Test
    public void shouldNotCreateProjectDataWhenFlagIsNo() throws Exception {
        when(contextMock.getParameter(contextMock.getExtensionName() + "_" + CREATE_PERFORMANCE_CRONJOB_TEST_DATA)).thenReturn(NO);

        testObj.createProjectData(contextMock);

        verify(worldpayOrderTestDataMock, never()).createPerformanceCronJobData();
        verify(cronJobMock, never()).setActive(anyBoolean());
        verify(modelServiceMock, never()).save(cronJobMock);
    }

    @Test
    public void shouldEnableCronjobAndRunPerformanceTestWhenFlagIsYes() {
        when(contextMock.getParameter(contextMock.getExtensionName() + "_" + RUN_ORDER_MODIFICATION_CRONJOB_PERFORMANCE_TEST)).thenReturn(YES);
        when(cronJobMock.getResult()).thenReturn(CronJobResult.SUCCESS);

        testObj.createProjectData(contextMock);

        verify(cronJobServiceMock).getCronJob(ORDER_MODIFICATION_PROCESSOR_JOB);
        final InOrder inOrder = inOrder(cronJobMock, modelServiceMock, cronJobServiceMock);
        inOrder.verify(cronJobMock).setActive(true);
        inOrder.verify(modelServiceMock).save(cronJobMock);
        inOrder.verify(cronJobServiceMock).performCronJob(cronJobMock, true);
    }

    @Test
    public void shouldEnableCronjobAndRunPerformanceTestWhenFlagIsNo() {
        when(contextMock.getParameter(contextMock.getExtensionName() + "_" + RUN_ORDER_MODIFICATION_CRONJOB_PERFORMANCE_TEST)).thenReturn(NO);

        testObj.createProjectData(contextMock);

        verify(cronJobServiceMock).getCronJob(anyString());
        verify(cronJobMock, never()).setActive(anyBoolean());
        verify(cronJobServiceMock, never()).performCronJob(any(CronJobModel.class));
        verify(modelServiceMock, never()).save(cronJobMock);
    }
}