package com.worldpay.interceptors;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayItemCodePrepareInterceptorTest {

    private static final String GENERATED_KEY = "key";
    private static final String FIELD_NAME = "field";
    public static final String TYPE = "type";

    @InjectMocks
    private WorldpayItemCodePrepareInterceptor testObj = new WorldpayItemCodePrepareInterceptor();
    @Mock
    private KeyGenerator keyGeneratorMock;
    @Mock
    private TypeService typeServiceMock;

    @Mock
    private InterceptorContext interceptorContextMock;
    @Mock
    private ItemModel itemModelMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private ComposedTypeModel composedTypeModelMock;

    @Before
    public void setUp() {
        testObj.setFieldName(FIELD_NAME);
    }

    @Test
    public void onPrepareShouldSetKeyIfItemHasFieldName() throws InterceptorException {
        when(itemModelMock.getItemtype()).thenReturn(TYPE);
        when(typeServiceMock.getComposedTypeForCode(TYPE)).thenReturn(composedTypeModelMock);
        when(typeServiceMock.hasAttribute(composedTypeModelMock, FIELD_NAME)).thenReturn(true);
        when(interceptorContextMock.getModelService()).thenReturn(modelServiceMock);
        when(modelServiceMock.getAttributeValue(itemModelMock, FIELD_NAME)).thenReturn(null);
        when(keyGeneratorMock.generate()).thenReturn(GENERATED_KEY);

        testObj.onPrepare(itemModelMock, interceptorContextMock);

        verify(modelServiceMock).setAttributeValue(itemModelMock, FIELD_NAME, GENERATED_KEY);
    }

    @Test
    public void onPrepareShouldNotSetKeyIfItemDoesNotHaveFieldName() throws InterceptorException {
        when(itemModelMock.getItemtype()).thenReturn(TYPE);
        when(typeServiceMock.getComposedTypeForCode(TYPE)).thenReturn(composedTypeModelMock);

        testObj.onPrepare(itemModelMock, interceptorContextMock);

        verify(modelServiceMock, never()).setAttributeValue(eq(itemModelMock), anyString(), anyString());
    }

    @Test
    public void onPrepareShouldNotSetKeyIfItemHasFieldAlreadySet() throws InterceptorException {
        when(itemModelMock.getItemtype()).thenReturn(TYPE);
        when(typeServiceMock.getComposedTypeForCode(TYPE)).thenReturn(composedTypeModelMock);
        when(typeServiceMock.hasAttribute(composedTypeModelMock, FIELD_NAME)).thenReturn(true);
        when(interceptorContextMock.getModelService()).thenReturn(modelServiceMock);
        when(modelServiceMock.getAttributeValue(itemModelMock, FIELD_NAME)).thenReturn(GENERATED_KEY);
        testObj.onPrepare(itemModelMock, interceptorContextMock);

        verify(modelServiceMock, never()).setAttributeValue(eq(itemModelMock), anyString(), anyString());
    }
}