package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.forms.CSEPaymentForm;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutGroup;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.PAYMENT_METHOD_STEP_NAME;
import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.WORLDPAY_PAYMENT_AND_BILLING_CHECKOUT_STEP_CMS_PAGE_LABEL;
import static com.worldpay.controllers.pages.checkout.steps.WorldpayChoosePaymentMethodCheckoutStepController.CHECKOUT_MULTI_PAYMENT_METHOD_BREADCRUMB;
import static de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants.BREADCRUMBS_KEY;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.emptyList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class AbstractWorldpayPaymentMethodCheckoutStepControllerTest {

    private static final String HAS_NO_PAYMENT_INFO = "hasNoPaymentInfo";
    private static final String NOINDEX_NOFOLLOW = "noindex,nofollow";
    private static final String META_ROBOTS = "metaRobots";
    private static final String CMS_PAGE_KEY = "cmsPage";
    private static final String RESOLVED_PAGE_TITLE = "resolvedPageTitle";
    private static final String CHECKOUT_GROUP = "checkoutGroup";
    private static final String EXPIRY_YEAR = "expiryYear";
    private static final String EXPIRY_MONTH = "expiryMonth";
    private static final String CARD_HOLDER_NAME = "cardHolderName";

    @InjectMocks
    private AbstractWorldpayPaymentMethodCheckoutStepController testObj = new TestWorldpayPaymentMethodCheckoutStepController();

    @Mock
    private RedirectAttributes redirectAttributes;
    @Mock
    private AcceleratorCheckoutFacade checkoutFacadeMock;
    @Mock
    private CheckoutFlowFacade checkoutFlowFacadeMock;
    @Mock
    private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;
    @Mock
    private ContentPageModel contentPageModelMock;
    @Mock
    private CMSPageService cmsPageServiceMock;
    @Mock
    private SiteConfigService siteConfigServiceMock;
    @Mock
    private CartFacade cartFacadeMock;
    @Mock
    private PageTitleResolver pageTitleResolverMock;
    @Mock (name = "checkoutFlowGroupMap")
    private Map<String, CheckoutGroup> checkoutFlowGroupMapMock;
    @Mock
    private Map<String, CheckoutStep> checkoutStepMapMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private CheckoutGroup checkoutGroupMock;
    @Mock
    private CheckoutStep checkoutStepMock;
    @Mock
    private CSEPaymentForm csePaymentFormMock;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;

    private List<CountryData> billingCountries = emptyList();
    private List<CardTypeData> supportedCardTypes = emptyList();
    private List breadcrumbs = EMPTY_LIST;
    private List<CountryData> deliveryCountries = Collections.emptyList();
    private Model model = new ExtendedModelMap();

    @Before
    public void setUp() {
        when(siteConfigServiceMock.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(cartFacadeMock.getDeliveryCountries()).thenReturn(deliveryCountries);
        when(pageTitleResolverMock.resolveContentPageTitle(anyString())).thenReturn(RESOLVED_PAGE_TITLE);
        when(checkoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(CHECKOUT_GROUP);
        when(checkoutFlowGroupMapMock.get(CHECKOUT_GROUP)).thenReturn(checkoutGroupMock);
        when(checkoutGroupMock.getCheckoutStepMap()).thenReturn(checkoutStepMapMock);
        when(checkoutStepMapMock.get(PAYMENT_METHOD_STEP_NAME)).thenReturn(checkoutStepMock);
        when(worldpayPaymentCheckoutFacadeMock.hasBillingDetails()).thenReturn(true);
    }

    @Test
    public void shouldPopulateModelWithBillingCountries() throws CommerceCartModificationException, CMSItemNotFoundException {
        when(checkoutFacadeMock.getBillingCountries()).thenReturn(billingCountries);

        final Collection<CountryData> result = testObj.getBillingCountries();

        assertEquals(billingCountries, result);
    }

    @Test
    public void shouldPopulateModelWithSupportedCardTypes() {
        when(checkoutFacadeMock.getSupportedCardTypes()).thenReturn(supportedCardTypes);

        final Collection<CardTypeData> result = testObj.getCardTypes();

        assertEquals(supportedCardTypes, result);
    }

    @Test
    public void shouldPopulateModelWithAllMonths() {
        final List<AbstractCheckoutController.SelectOption> result = testObj.getMonths();

        for (int i = 0; i < 11; i++) {
            if (i < 9) {
                assertEquals(String.format("0%s", i + 1), result.get(i).getCode());
            } else {
                assertEquals(String.format("%s", i + 1), result.get(i).getCode());
            }
            assertEquals(String.format("%s", i + 1), result.get(i).getName());
        }
    }

    @Test
    public void shouldPopulateModelWithExpiryYears() {

        final List<AbstractCheckoutController.SelectOption> result = testObj.getExpiryYears();
        final int currentYear = DateTime.now().getYear();

        assertEquals(11, result.size());

        for (int i = 0; i < result.size(); i++) {
            assertEquals(String.valueOf(currentYear + i), result.get(i).getCode());
            assertEquals(String.valueOf(currentYear + i), result.get(i).getName());
        }
    }

    @Test
    public void shouldAddNotAllowCheckoutPagesToBeIndexed() throws CommerceCartModificationException, CMSItemNotFoundException {
        testObj.enterStep(model, redirectAttributes);

        assertEquals(NOINDEX_NOFOLLOW, model.asMap().get(META_ROBOTS));
    }

    @Test
    public void shouldAddHasNoPaymentInfoToModel() throws CommerceCartModificationException, CMSItemNotFoundException {
        when(checkoutFlowFacadeMock.hasNoPaymentInfo()).thenReturn(true);

        testObj.enterStep(model, redirectAttributes);

        assertEquals(true, model.asMap().get(HAS_NO_PAYMENT_INFO));
    }

    @Test
    public void shouldAddBreadcrumbsToModel() throws CommerceCartModificationException, CMSItemNotFoundException {
        when(resourceBreadcrumbBuilder.getBreadcrumbs(CHECKOUT_MULTI_PAYMENT_METHOD_BREADCRUMB)).thenReturn(breadcrumbs);

        testObj.enterStep(model, redirectAttributes);

        assertEquals(breadcrumbs, model.asMap().get(BREADCRUMBS_KEY));
    }

    @Test
    public void shouldAddCmsPageToModel() throws CommerceCartModificationException, CMSItemNotFoundException {
        when(cmsPageServiceMock.getPageForLabelOrId(WORLDPAY_PAYMENT_AND_BILLING_CHECKOUT_STEP_CMS_PAGE_LABEL)).thenReturn(contentPageModelMock);

        testObj.enterStep(model, redirectAttributes);

        assertEquals(contentPageModelMock, model.asMap().get(CMS_PAGE_KEY));
    }

    @Test
    public void shouldPopulateCCFieldsToCseAdditionalAuthInfo() {
        when(csePaymentFormMock.getNameOnCard()).thenReturn(CARD_HOLDER_NAME);
        when(csePaymentFormMock.getExpiryMonth()).thenReturn(EXPIRY_MONTH);
        when(csePaymentFormMock.getExpiryYear()).thenReturn(EXPIRY_YEAR);

        final CSEAdditionalAuthInfo result = testObj.createCSEAdditionalAuthInfo(csePaymentFormMock);

        assertEquals(CARD_HOLDER_NAME, result.getCardHolderName());
        assertEquals(EXPIRY_MONTH, result.getExpiryMonth());
        assertEquals(EXPIRY_YEAR, result.getExpiryYear());
    }

    private class TestWorldpayPaymentMethodCheckoutStepController extends AbstractWorldpayPaymentMethodCheckoutStepController {
        @Override
        public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException, CommerceCartModificationException {
            setupAddPaymentPage(model);
            return null;
        }

        @Override
        public String back(final RedirectAttributes redirectAttributes) {
            return null;
        }

        @Override
        public String next(final RedirectAttributes redirectAttributes) {
            return null;
        }

        @Override
        protected void setUpMetaDataForContentPage(final Model model, final ContentPageModel contentPage) {
        }
    }
}