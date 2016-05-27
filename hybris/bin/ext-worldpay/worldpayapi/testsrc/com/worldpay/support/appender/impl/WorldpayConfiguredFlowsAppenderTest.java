package com.worldpay.support.appender.impl;

import com.worldpay.model.WorldpayPaymentPageModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSPageDao;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.worldpay.support.appender.impl.WorldpayConfiguredFlowsAppender.PAYMENT_AND_BILLING_LABEL;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayConfiguredFlowsAppenderTest {

    private static final String ACTIVE_PAGE_TEMPLATE = "activeFlow";
    private static final String ONLINE = "online";
    private static final String STAGED = "staged";
    private static final String SITE_NAME = "siteName";
    private static final String CATALOG_NAME = "catalogName";

    @InjectMocks
    private WorldpayConfiguredFlowsAppender testObj = new WorldpayConfiguredFlowsAppender();

    @Mock
    private CMSSiteService cmsSiteServiceMock;
    @Mock
    private CMSSiteModel cmsSite1Mock, cmsSite2Mock;

    @Mock
    private ContentCatalogModel contentCatalogOnlineMock;
    @Mock
    private ContentCatalogModel contentCatalogStagedMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private CatalogVersionModel contentCatalogVersionMock;
    @Mock
    private CMSPageDao cmsPageDao;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private WorldpayPaymentPageModel cmsPageDesktopMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private WorldpayPaymentPageModel cmsPageMobileMock;

    @Test
    public void testAppendContent() throws Exception {
        when(cmsSiteServiceMock.getSites()).thenReturn(asList(cmsSite1Mock, cmsSite2Mock));

        when(cmsSite1Mock.getName()).thenReturn(SITE_NAME);
        when(cmsSite2Mock.getName()).thenReturn(SITE_NAME);

        when(cmsSite1Mock.getContentCatalogs()).thenReturn(asList(contentCatalogOnlineMock, contentCatalogStagedMock));
        when(cmsSite2Mock.getContentCatalogs()).thenReturn(singletonList(contentCatalogOnlineMock));

        when(contentCatalogOnlineMock.getVersion()).thenReturn(ONLINE);
        when(contentCatalogStagedMock.getVersion()).thenReturn(STAGED);

        when(contentCatalogOnlineMock.getActiveCatalogVersion()).thenReturn(contentCatalogVersionMock);
        when(contentCatalogVersionMock.getCatalog().getName()).thenReturn(CATALOG_NAME);

        final List<AbstractPageModel> allPaymentPages = asList(cmsPageDesktopMock, cmsPageMobileMock);
        when(cmsPageDao.findAllPagesByLabel(PAYMENT_AND_BILLING_LABEL, singletonList(contentCatalogVersionMock))).thenReturn(allPaymentPages);

        when(cmsPageDesktopMock.getMasterTemplate().getUid()).thenReturn(ACTIVE_PAGE_TEMPLATE);
        when(cmsPageMobileMock.getMasterTemplate().getUid()).thenReturn(ACTIVE_PAGE_TEMPLATE);

        final String result = testObj.appendContent();

        assertTrue(result.contains("Active Payment Flow:"));
        assertEquals(2, StringUtils.countMatches(result, SITE_NAME));
        assertEquals(2, StringUtils.countMatches(result, CATALOG_NAME));
        assertEquals(4, StringUtils.countMatches(result, ACTIVE_PAGE_TEMPLATE));
    }
}
    

