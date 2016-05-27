package com.worldpay.support.appender.impl;

import com.worldpay.support.appender.WorldpaySupportEmailAppender;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSPageDao;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.repeat;

/**
 * Implementation of {@see WorldpaySupportEmailAppender } to include the list of configured flows
 */
public class WorldpayConfiguredFlowsAppender implements WorldpaySupportEmailAppender {

    protected static final String PAYMENT_AND_BILLING_LABEL = "worldpayPaymentAndBillingCheckoutStep";
    public static final String ONLINE_FLAG = "online";

    private CMSSiteService cmsSiteService;
    private CMSPageDao cmsPageDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public String appendContent() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(System.lineSeparator()).append("Active Payment Flow:").append(System.lineSeparator());
        final Collection<CMSSiteModel> sites = cmsSiteService.getSites();
        for (final CMSSiteModel site : sites) {
            final String siteName = site.getName();
            stringBuilder.append(TAB).append(siteName).append(System.lineSeparator());
            final List<CatalogVersionModel> contentCatalogModels = filterOnlineContentCatalogs(site);
            for (final CatalogVersionModel contentCatalogModel : contentCatalogModels) {
                final String contentCatalogName = contentCatalogModel.getCatalog().getName();
                stringBuilder.append(repeat(TAB, 2)).append(contentCatalogName).append(System.lineSeparator());
                final Collection<AbstractPageModel> allPagesByLabel = cmsPageDao.findAllPagesByLabel(PAYMENT_AND_BILLING_LABEL, singletonList(contentCatalogModel));
                for (final AbstractPageModel pageModel : allPagesByLabel) {
                    final String pageUid = pageModel.getUid();
                    stringBuilder.append(repeat(TAB, 3)).append("PageId: ").append(pageUid).append(System.lineSeparator());
                    final String template = pageModel.getMasterTemplate().getUid();
                    stringBuilder.append(repeat(TAB, 4)).append("Template: ").append(template).append(System.lineSeparator());
                }
            }
        }
        return stringBuilder.toString();
    }

    private List<CatalogVersionModel> filterOnlineContentCatalogs(final CMSSiteModel site) {
        final List<ContentCatalogModel> contentCatalogs = site.getContentCatalogs();
        return contentCatalogs.stream().filter(
                contentCatalog -> contentCatalog.getVersion().equalsIgnoreCase(ONLINE_FLAG)).map(ContentCatalogModel::getActiveCatalogVersion).collect(toList());
    }

    @Required
    public void setCmsSiteService(CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    @Required
    public void setCmsPageDao(CMSPageDao cmsPageDao) {
        this.cmsPageDao = cmsPageDao;
    }
}
