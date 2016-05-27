package com.worldpay.components.impl;

import com.worldpay.cscockpit.services.search.generic.query.DefaultCartSearchQueryBuilder;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.model.MerchantInfo;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.cscockpit.model.data.DataObject;
import de.hybris.platform.cscockpit.services.search.SearchException;
import de.hybris.platform.cscockpit.services.search.impl.DefaultCsTextSearchCommand;
import de.hybris.platform.cscockpit.widgets.controllers.search.SearchCommandController;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zhtml.Text;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zul.Div;
import org.zkoss.zul.Window;

import java.util.*;

import static com.worldpay.components.impl.HopResponse.AUTHORISED_PAYMENT_TEXT;
import static com.worldpay.service.model.AuthorisedStatus.AUTHORISED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class HopResponseTest {

    public static final String PARAM_1 = "param1";
    public static final String VALUE_1 = "value1";
    public static final String PARAM_2 = "param2";
    public static final String VALUE_21 = "value21";
    public static final String VALUE_22 = "value22";
    public static final String CART_CODE = "00002000";
    public static final String ORDER_KEY = "MERCHANT_OWNER^MERCHANTCODE^" + "00002000-1431098192721";
    public static final String MERCHANT_CODE = "merchantCode";

    @Spy
    @InjectMocks
    private HopResponse testObj = new HopResponse();

    @Mock
    private Execution currentExecutionMock;
    @Mock
    private Converter<Map<String, String>, RedirectAuthoriseResult> redirectAuthoriseResultConverterMock;
    @Mock
    private SearchCommandController<DefaultCsTextSearchCommand> searchCommandControllerMock;
    @Mock
    private RedirectAuthoriseResult redirectAuthoriseResultMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;
    @Mock
    private MerchantInfo merchantConfigDataMock;
    @Mock
    private WorldpayRedirectOrderService worldpayRedirectOrderServiceMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private DataObject<TypedObject> typedObjectMock;
    @Captor
    private ArgumentCaptor<Map<String, String>> paramsMapCaptor;
    @Captor
    private ArgumentCaptor<DefaultCsTextSearchCommand> searchCommandCaptor;
    @Mock
    private TypedObject typedObjectItemMock;

    @Before
    public void setup() throws WorldpayConfigurationException {
        final Map currentExecutionParameterMap = new HashMap<>();
        currentExecutionParameterMap.put(PARAM_1, VALUE_1);
        currentExecutionParameterMap.put(PARAM_2, Arrays.asList(VALUE_21, VALUE_22));

        final List<DataObject<TypedObject>> currentPageResults = Collections.singletonList(typedObjectMock);

        doReturn(currentExecutionMock).when(testObj).getCurrentExecution();
        when(currentExecutionMock.getParameterMap()).thenReturn(currentExecutionParameterMap);
        when(currentExecutionMock.getParameter(PARAM_1)).thenReturn(VALUE_1);
        when(currentExecutionMock.getParameter(PARAM_2)).thenReturn(VALUE_21);
        when(redirectAuthoriseResultConverterMock.convert(paramsMapCaptor.capture())).thenReturn(redirectAuthoriseResultMock);
        when(redirectAuthoriseResultMock.getOrderKey()).thenReturn(ORDER_KEY);
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(AUTHORISED.getCode());
        when(worldpayMerchantInfoServiceMock.getCustomerServicesMerchant()).thenReturn(merchantConfigDataMock);
        when(merchantConfigDataMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(searchCommandControllerMock.getCurrentPageResults()).thenReturn(currentPageResults);
        when(typedObjectMock.getItem()).thenReturn(typedObjectItemMock);
        when(typedObjectItemMock.getObject()).thenReturn(cartModelMock);
    }

    @Test
    public void testShowResponse() throws SearchException, WorldpayConfigurationException {
        final Window window = new Window();

        testObj.showResponse(window);

        verify(redirectAuthoriseResultConverterMock).convert(paramsMapCaptor.capture());
        verify(searchCommandControllerMock).search(searchCommandCaptor.capture());
        verify(worldpayMerchantInfoServiceMock).getCustomerServicesMerchant();
        verify(worldpayRedirectOrderServiceMock).completeRedirectAuthorise(redirectAuthoriseResultMock, MERCHANT_CODE, cartModelMock);

        final Map<String, String> paramsMapCaptorValue = paramsMapCaptor.getValue();
        final DefaultCsTextSearchCommand searchCommandCaptorValue = searchCommandCaptor.getValue();

        assertEquals(CART_CODE, searchCommandCaptorValue.getText(DefaultCartSearchQueryBuilder.TextField.CartId));
        assertTrue(paramsMapCaptorValue.size() == 2);
        assertEquals(VALUE_1, paramsMapCaptorValue.get(PARAM_1));
        assertEquals(VALUE_21, paramsMapCaptorValue.get(PARAM_2));
        verify(worldpayMerchantInfoServiceMock).getCustomerServicesMerchant();

        final Div div = (Div) window.getChildren().get(0);
        final Text text = (Text) div.getFirstChild();

        assertTrue(window.getChildren().size() == 1);
        assertTrue(div.isVisible());
        assertTrue(text.isVisible());
        assertEquals(AUTHORISED_PAYMENT_TEXT, text.getValue());
    }
}