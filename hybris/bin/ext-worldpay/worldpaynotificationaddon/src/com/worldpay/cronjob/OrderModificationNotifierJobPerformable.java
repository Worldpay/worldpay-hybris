package com.worldpay.cronjob;

import com.worldpay.strategies.WorldpayOrderModificationNotifierStrategy;
import com.worldpay.worldpaynotificationaddon.model.NotifyUnprocessedOrderModificationsCronJobModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.springframework.beans.factory.annotation.Required;

/**
 * The Order Modification Notifier Job Performable that notifies about each unprocessed, stale order modification
 */
public class OrderModificationNotifierJobPerformable extends AbstractJobPerformable<NotifyUnprocessedOrderModificationsCronJobModel> {

    private WorldpayOrderModificationNotifierStrategy worldpayOrderModificationNotifierStrategy;

    @Override
    public PerformResult perform(final NotifyUnprocessedOrderModificationsCronJobModel cronJobModel) {
        getWorldpayOrderModificationNotifierStrategy().notifyThatOrdersHaveNotBeenProcessed(cronJobModel.getUnprocessedTimeInDays());
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    public WorldpayOrderModificationNotifierStrategy getWorldpayOrderModificationNotifierStrategy() {
        return worldpayOrderModificationNotifierStrategy;
    }

    @Required
    public void setWorldpayOrderModificationNotifierStrategy(WorldpayOrderModificationNotifierStrategy worldpayOrderModificationNotifierStrategy) {
        this.worldpayOrderModificationNotifierStrategy = worldpayOrderModificationNotifierStrategy;
    }
}
