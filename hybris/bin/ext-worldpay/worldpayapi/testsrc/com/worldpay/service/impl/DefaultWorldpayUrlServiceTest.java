package com.worldpay.service.impl;

import com.worldpay.exception.WorldpayConfigurationException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayUrlServiceTest {

    public static final String FULL_SITE_URL = "fullSiteUrl";
    public static final String URL = "url";

    @Spy
    @InjectMocks
    private DefaultWorldpayUrlService testObj = new DefaultWorldpayUrlService();

    @Mock
    private BaseSiteService baseSiteServiceMock;
    @Mock
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;
    @Mock
    private BaseSiteModel currentBaseSiteMock;

    @Test
    public void testGetFullUrlSecure() throws Exception {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(currentBaseSiteMock);
        when(siteBaseUrlResolutionService.getWebsiteUrlForSite(currentBaseSiteMock, true, URL)).thenReturn(FULL_SITE_URL);

        final String result = testObj.getFullUrl(URL, true);

        assertEquals(FULL_SITE_URL, result);
    }

    @Test
    public void testGetFullUrlUnSecure() throws Exception {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(currentBaseSiteMock);
        when(siteBaseUrlResolutionService.getWebsiteUrlForSite(currentBaseSiteMock, false, URL)).thenReturn(FULL_SITE_URL);

        final String result = testObj.getFullUrl(URL, false);

        assertEquals(FULL_SITE_URL, result);
    }

    @Test (expected = WorldpayConfigurationException.class)
    public void testGetFullUrlShouldRaiseExceptionWhenServiceReturnsNull() throws Exception {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(currentBaseSiteMock);
        when(siteBaseUrlResolutionService.getWebsiteUrlForSite(currentBaseSiteMock, true, URL)).thenReturn(null);

        testObj.getFullUrl(URL, true);
    }

    @Test
    public void testSetOfAddresses() throws WorldpayConfigurationException {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(currentBaseSiteMock);
        // Returns the 3rd argument of the invocation, the Url passed in the getFullUrl invocation
        when(siteBaseUrlResolutionService.getWebsiteUrlForSite(eq(currentBaseSiteMock), eq(true), anyString())).thenAnswer(invocationOnMOck -> {
            Object[] args = invocationOnMOck.getArguments();
            return args[2];
        });

        doReturn("cancelPath").when(testObj).getCancelPath();
        testObj.getFullCancelURL();
        verify(testObj).getFullUrl("cancelPath", true);

        doReturn("successPath").when(testObj).getSuccessPath();
        testObj.getFullSuccessURL();
        verify(testObj).getFullUrl("successPath", true);

        doReturn("pendingPath").when(testObj).getPendingPath();
        testObj.getFullPendingURL();
        verify(testObj).getFullUrl("pendingPath", true);

        doReturn("failurePath").when(testObj).getFailurePath();
        testObj.getFullFailureURL();
        verify(testObj).getFullUrl("failurePath", true);

        doReturn("threeDPath").when(testObj).getThreeDSecureTermPath();
        testObj.getFullThreeDSecureTermURL();
        verify(testObj).getFullUrl("threeDPath", true);
    }
}