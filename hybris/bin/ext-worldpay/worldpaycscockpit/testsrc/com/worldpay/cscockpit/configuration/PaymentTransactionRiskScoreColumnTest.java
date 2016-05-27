package com.worldpay.cscockpit.configuration;

import com.worldpay.model.WorldpayRiskScoreModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.model.meta.BaseType;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.impl.ItemAttributePropertyDescriptor;
import de.hybris.platform.cockpit.services.meta.TypeService;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer;
import de.hybris.platform.cockpit.services.values.ObjectValueHandler;
import de.hybris.platform.cockpit.services.values.ObjectValueHandlerRegistry;
import de.hybris.platform.cockpit.session.UISession;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;
import static java.util.Locale.UK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class PaymentTransactionRiskScoreColumnTest {

    public static final String PROP_DESCR_0_NAME = "propDescr0Name";
    public static final String PROP_DESCR_1_NAME = "propDescr1Name";
    public static final String PROP_DESCR_1_VALUE = "propDescr1Value";
    public static final String PROP_DESCR_2_NAME = "propDescr2Name";
    public static final String PROP_DESCR_3_NAME = "propDescr3Name";
    public static final String PROP_DESCR_3_VALUE = "propDescr3Value";
    public static final String PROP_DESCR_4_NAME = "propDescr4Name";
    public static final String EXPECTED_RESULT_NOT_NULL_VALUES = PROP_DESCR_1_NAME + "=" + PROP_DESCR_1_VALUE + ", "
            + PROP_DESCR_3_NAME + "=" + PROP_DESCR_3_VALUE;

    @Spy
    private PaymentTransactionRiskScoreColumn testObj = new PaymentTransactionRiskScoreColumn();

    @Mock
    private PaymentTransactionEntryModel modelMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private WorldpayRiskScoreModel worldpayRiskScoreMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private TypeService typeServiceMock;
    @Mock
    private ItemAttributePropertyDescriptor propDescriptor0Mock, propDescriptor1Mock, propDescriptor2Mock, propDescriptor3Mock, propDescriptor4Mock;
    @Mock
    private ObjectValueContainer objectValueContainerMock;
    @Mock
    private ObjectValueContainer.ObjectValueHolder objectValueHolder0Mock, objectValueHolder1Mock, objectValueHolder2Mock, objectValueHolder3Mock, objectValueHolder4Mock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private UISession uiSessionMock;
    @Mock
    private BaseType baseTypeMock;
    @Mock
    private ObjectValueHandlerRegistry valueHandlerRegistryMock;
    @Mock
    private ObjectValueHandler objectValueHandlerMock;

    private List<PropertyDescriptor> propertyDescriptors;

    @Before
    public void setup() throws Exception {
        propertyDescriptors = Arrays.asList((PropertyDescriptor) propDescriptor0Mock,
                propDescriptor1Mock, propDescriptor2Mock, propDescriptor3Mock, propDescriptor4Mock);

        final Set<PropertyDescriptor> propertyDescriptorsSet = new LinkedHashSet<>(propertyDescriptors);

        final Set<String> languageIsos = new LinkedHashSet<>(Collections.singletonList(UK.getLanguage()));

        doReturn(uiSessionMock).when(testObj).getCurrentSession();

        when(uiSessionMock.getTypeService()).thenReturn(typeServiceMock);
        when(uiSessionMock.getSystemService().getAvailableLanguageIsos()).thenReturn(languageIsos);
        when(uiSessionMock.getValueHandlerRegistry()).thenReturn(valueHandlerRegistryMock);

        when(typeServiceMock.wrapItem(worldpayRiskScoreMock).getType()).thenReturn(baseTypeMock);
        when(baseTypeMock.getDeclaredPropertyDescriptors()).thenReturn(propertyDescriptorsSet);

        when(Boolean.valueOf(propDescriptor0Mock.isLocalized())).thenReturn(TRUE);
        when(Boolean.valueOf(propDescriptor1Mock.isLocalized())).thenReturn(TRUE);
        when(Boolean.valueOf(propDescriptor2Mock.isLocalized())).thenReturn(TRUE);
        when(Boolean.valueOf(propDescriptor3Mock.isLocalized())).thenReturn(TRUE);
        when(Boolean.valueOf(propDescriptor4Mock.isLocalized())).thenReturn(TRUE);

        doReturn(objectValueContainerMock).when(testObj).createObjectValueContainer(anyObject(), any(BaseType.class));

        when(objectValueContainerMock.getValue(propDescriptor0Mock, UK.getLanguage())).thenReturn(objectValueHolder0Mock);
        when(objectValueContainerMock.getValue(propDescriptor1Mock, UK.getLanguage())).thenReturn(objectValueHolder1Mock);
        when(objectValueContainerMock.getValue(propDescriptor2Mock, UK.getLanguage())).thenReturn(objectValueHolder2Mock);
        when(objectValueContainerMock.getValue(propDescriptor3Mock, UK.getLanguage())).thenReturn(objectValueHolder3Mock);
        when(objectValueContainerMock.getValue(propDescriptor4Mock, UK.getLanguage())).thenReturn(objectValueHolder4Mock);

        when(propDescriptor0Mock.getAttributeQualifier()).thenReturn(PROP_DESCR_0_NAME);
        when(propDescriptor1Mock.getAttributeQualifier()).thenReturn(PROP_DESCR_1_NAME);
        when(propDescriptor2Mock.getAttributeQualifier()).thenReturn(PROP_DESCR_2_NAME);
        when(propDescriptor3Mock.getAttributeQualifier()).thenReturn(PROP_DESCR_3_NAME);
        when(propDescriptor4Mock.getAttributeQualifier()).thenReturn(PROP_DESCR_4_NAME);

        when(objectValueHolder1Mock.getOriginalValue()).thenReturn(PROP_DESCR_1_VALUE);
        when(objectValueHolder3Mock.getOriginalValue()).thenReturn(PROP_DESCR_3_VALUE);

        when(valueHandlerRegistryMock.getValueHandlerChain(baseTypeMock)).thenReturn(singletonList(objectValueHandlerMock));
    }

    @Test
    public void getItemValueShouldReturnNullIfPaymentTransactionModelNotPaymentTransactionModel() throws Exception {
        when(modelMock.getPaymentTransaction()).thenReturn(new PaymentTransactionModel());

        final String result = testObj.getItemValue(modelMock, UK);

        assertNull(result);
        verify(objectValueHandlerMock, never()).loadValues(any(ObjectValueContainer.class), any(BaseType.class), anyObject(), anySet(), anySet());
    }

    @Test
    public void getItemValueShouldReturnNullIfNoRiskScore() throws Exception {
        when(modelMock.getPaymentTransaction()).thenReturn(paymentTransactionModelMock);

        final String result = testObj.getItemValue(modelMock, UK);

        assertNull(result);
        verify(objectValueHandlerMock, never()).loadValues(any(ObjectValueContainer.class), any(BaseType.class), anyObject(), anySet(), anySet());
    }

    @Test
    public void getItemValueShouldReturnValuesForNotNullFields() throws Exception {
        when(modelMock.getPaymentTransaction()).thenReturn(paymentTransactionModelMock);
        when(paymentTransactionModelMock.getRiskScore()).thenReturn(worldpayRiskScoreMock);

        final String result = testObj.getItemValue(modelMock, UK);

        assertEquals(EXPECTED_RESULT_NOT_NULL_VALUES, result);
        verify(objectValueHandlerMock, times(propertyDescriptors.size())).loadValues(any(ObjectValueContainer.class), any(BaseType.class), anyObject(), anySet(), anySet());
    }
}
