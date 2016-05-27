package com.worldpay.worldpaytests.setup;

import com.worldpay.worldpaytests.constants.WorldpaytestsConstants;
import com.worldpay.worldpaytests.orders.WorldpayOrderTestData;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormat;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

import static de.hybris.platform.core.initialization.SystemSetup.Process.ALL;
import static de.hybris.platform.cronjob.enums.CronJobResult.SUCCESS;
import static java.text.MessageFormat.format;


/**
 * This class provides hooks into the system's initialization and update processes.
 *
 * @see "https://wiki.hybris.com/display/release5/Hooks+for+Initialization+and+Update+Process"
 */
@SystemSetup (extension = WorldpaytestsConstants.EXTENSIONNAME)
public class WorldpayTestDataSystemSetup extends AbstractSystemSetup {
    private static final Logger LOG = Logger.getLogger(WorldpayTestDataSystemSetup.class);

    protected static final String CREATE_PERFORMANCE_CRONJOB_TEST_DATA = "createPerformanceCronjobTestData";
    protected static final String RUN_ORDER_MODIFICATION_CRONJOB_PERFORMANCE_TEST = "runOrderModificationCronjobPerformanceTest";
    protected static final String ORDER_MODIFICATION_PROCESSOR_JOB = "orderModificationProcessorJob";

    private WorldpayOrderTestData worldpayOrderTestData;
    private CronJobService cronjobService;
    private ModelService modelService;

    /**
     * Generates the Dropdown and Multi-select boxes for the projectdata import
     */
    @Override
    @SystemSetupParameterMethod
    public List<SystemSetupParameter> getInitializationOptions() {
        final List<SystemSetupParameter> params = new ArrayList<>();
        params.add(createBooleanSystemSetupParameter(CREATE_PERFORMANCE_CRONJOB_TEST_DATA, "Create Performance Cronjob Test Data (this will disable the cronjob)", false));
        params.add(createBooleanSystemSetupParameter(RUN_ORDER_MODIFICATION_CRONJOB_PERFORMANCE_TEST, "Run performance test on the orderModificationProcessorJob", false));
        return params;
    }

    /**
     * Implement this method to create data that is used in your project. This method will be called during the system
     * initialization.
     *
     * @param context the context provides the selected parameters and values
     */
    @SystemSetup (type = Type.PROJECT, process = ALL)
    public void createProjectData(final SystemSetupContext context) {
        final CronJobModel cronJob = cronjobService.getCronJob(ORDER_MODIFICATION_PROCESSOR_JOB);
        if (getBooleanSystemSetupParameter(context, CREATE_PERFORMANCE_CRONJOB_TEST_DATA)) {
            cronJob.setActive(false);
            modelService.save(cronJob);

            final DateTime dateTime = new DateTime();
            LOG.info(format("Current Time: [{0}]", dateTime));
            LOG.info("Creating orders for performance testing the OrderModificationProcessorJobPerformable");

            worldpayOrderTestData.createPerformanceCronJobData();

            final DateTime passedDateTime = new DateTime();
            final Duration duration = new Duration(dateTime.getMillis(), passedDateTime.getMillis());
            LOG.info(format("Passed Time [{0}]", PeriodFormat.getDefault().print(duration.toPeriod())));
        }
        if (getBooleanSystemSetupParameter(context, RUN_ORDER_MODIFICATION_CRONJOB_PERFORMANCE_TEST)) {
            cronJob.setActive(true);
            modelService.save(cronJob);

            cronjobService.performCronJob(cronJob, true);

            logResults(cronJob);
        }
    }

    private void logResults(final CronJobModel cronJob) {
        LOG.info(format("orderModificationProcessorJob finished with status [{0}]", cronJob.getStatus()));
        final Duration duration = new Duration(cronJob.getStartTime().getTime(), cronJob.getEndTime().getTime());
        LOG.info("############################################## orderModificationProcessorJob ################");
        LOG.info(format("Running the orderModificationProcessorJob took [{0}]", PeriodFormat.getDefault().print(duration.toPeriod())));
        LOG.info("############################################## orderModificationProcessorJob ################");

        if (!cronJob.getResult().equals(SUCCESS)) {
            LOG.error(format("orderModificationProcessorJob finished with result [{0}]. Check the logs through HMC for details", cronJob.getResult()));
        }
    }

    @Required
    public void setWorldpayOrderTestData(WorldpayOrderTestData worldpayOrderTestData) {
        this.worldpayOrderTestData = worldpayOrderTestData;
    }

    @Required
    public void setCronjobService(CronJobService cronjobService) {
        this.cronjobService = cronjobService;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }
}

