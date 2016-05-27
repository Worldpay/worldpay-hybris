package com.worldpay.support.appender.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cluster.DefaultBroadcastService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayClusterInformationAppenderTest {

    private static final long CLUSTER_ISLAND_ID = 0L;
    private static final int CLUSTER_NODE_ID = 2;

    @Spy
    @InjectMocks
    private WorldpayClusterInformationAppender testObj = new WorldpayClusterInformationAppender();

    @Mock
    private DefaultBroadcastService broadcastServiceMock;

    @Test
    public void testAppendContent() throws Exception {
        doReturn(broadcastServiceMock).when(testObj).getBroadCastServiceInstance();

        when(broadcastServiceMock.getClusterIslandPK()).thenReturn(CLUSTER_ISLAND_ID);
        when(broadcastServiceMock.getClusterNodeID()).thenReturn(CLUSTER_NODE_ID);
        when(broadcastServiceMock.isClusteringEnabled()).thenReturn(Boolean.TRUE);

        final String result = testObj.appendContent();

        assertTrue(result.contains("Cluster Information:"));
        assertTrue(result.contains(String.valueOf(Boolean.TRUE)));
        assertTrue(result.contains(String.valueOf(CLUSTER_ISLAND_ID)));
        assertTrue(result.contains(String.valueOf(CLUSTER_NODE_ID)));
    }
}
    

