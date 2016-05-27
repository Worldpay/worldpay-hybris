package com.worldpay.support.appender.impl;

import com.worldpay.support.appender.WorldpaySupportEmailAppender;
import de.hybris.platform.cluster.DefaultBroadcastService;

/**
 * Implementation of {@see WorldpaySupportEmailAppender } to include the cluster information
 */
public class WorldpayClusterInformationAppender implements WorldpaySupportEmailAppender {

    @Override
    public String appendContent() {
        StringBuilder clusterInformation = new StringBuilder();
        clusterInformation.append(System.lineSeparator()).append("Cluster Information:").append(System.lineSeparator());
        final DefaultBroadcastService broadcastService = getBroadCastServiceInstance();
        clusterInformation.append(TAB).append("Clustering Enabled: ").append(broadcastService.isClusteringEnabled()).append(System.lineSeparator());
        clusterInformation.append(TAB).append("Cluster Node Id: ").append(broadcastService.getClusterNodeID()).append(System.lineSeparator());
        clusterInformation.append(TAB).append("Cluster Island Id: ").append(broadcastService.getClusterIslandPK()).append(System.lineSeparator());
        return clusterInformation.toString();
    }

    protected DefaultBroadcastService getBroadCastServiceInstance() {
        return DefaultBroadcastService.getInstance();
    }
}
