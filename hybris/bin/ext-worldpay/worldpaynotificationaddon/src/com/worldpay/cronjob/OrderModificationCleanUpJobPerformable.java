package com.worldpay.cronjob;

import com.worldpay.strategies.WorldpayOrderModificationCleanUpStrategy;
import com.worldpay.worldpaynotificationaddon.model.CleanUpProcessedOrderModificationsCronJobModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.springframework.beans.factory.annotation.Required;

/**
 * The Order Modification Clean Up Job Performable that cleans up processed order modifications after a certain amount of time.
 */
public class OrderModificationCleanUpJobPerformable extends AbstractJobPerformable<CleanUpProcessedOrderModificationsCronJobModel> {

    private WorldpayOrderModificationCleanUpStrategy worldpayOrderModificationCleanUpStrategy;

    @Override
    public PerformResult perform(final CleanUpProcessedOrderModificationsCronJobModel cleanUpOrderModificationsCronJobModel) {
        getWorldpayOrderModificationCleanUpStrategy().doCleanUp(cleanUpOrderModificationsCronJobModel.getDaysToWaitBeforeDeletion());
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    public WorldpayOrderModificationCleanUpStrategy getWorldpayOrderModificationCleanUpStrategy() {
        return worldpayOrderModificationCleanUpStrategy;
    }

    @Required
    public void setWorldpayOrderModificationCleanUpStrategy(WorldpayOrderModificationCleanUpStrategy worldpayOrderModificationCleanUpStrategy) {
        this.worldpayOrderModificationCleanUpStrategy = worldpayOrderModificationCleanUpStrategy;
    }
}
