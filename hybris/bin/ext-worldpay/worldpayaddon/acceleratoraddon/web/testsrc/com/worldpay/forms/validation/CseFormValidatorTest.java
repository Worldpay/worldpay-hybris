package com.worldpay.forms.validation;

import com.worldpay.forms.CSEPaymentForm;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class CseFormValidatorTest {

    public static final String CSE_TOKEN = "cseToken";
    @InjectMocks
    private CseFormValidator testObj = new CseFormValidator();
    @Mock
    private Errors errorsMock;
    @Mock
    private CSEPaymentForm csePaymentFormMock;

    @Test
    public void shouldNotRejectIfCseIsSet() throws Exception {
        when(csePaymentFormMock.getCseToken()).thenReturn(CSE_TOKEN);

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock, never()).reject(CseFormValidator.GLOBAL_MISSING_CSE_TOKEN);
    }

    @Test
    public void shouldRejectIfCseIsNotSet() throws Exception {
        when(csePaymentFormMock.getCseToken()).thenReturn("");

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock).reject(CseFormValidator.GLOBAL_MISSING_CSE_TOKEN);
    }

    @Test
    public void shouldNotRejectIfTermsIsChecked() throws Exception {
        when(csePaymentFormMock.isTermsCheck()).thenReturn(true);

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock, never()).reject(CseFormValidator.CHECKOUT_ERROR_TERMS_NOT_ACCEPTED);
    }

    @Test
    public void shouldRejectIfTermsIsNotChecked() throws Exception {
        when(csePaymentFormMock.isTermsCheck()).thenReturn(false);

        testObj.validate(csePaymentFormMock, errorsMock);

        verify(errorsMock).reject(CseFormValidator.CHECKOUT_ERROR_TERMS_NOT_ACCEPTED);
    }
}