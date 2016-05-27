package com.worldpay.converters.populators;

import com.worldpay.model.WorldpayAavResponseModel;
import com.worldpay.service.model.PaymentReply;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayAavResponsePopulatorTest {

    private static final String AAV_ADDRESS_RESULT_CODE = "aavAddressResultCode";
    private static final String AAV_POSTAL_CODE_RESULT_CODE = "aavPostslCodeResultCode";
    private static final String AAV_EMAIL_RESULT_CODE = "aavEmailResultCode";
    private static final String AAV_CARDHOLDER_NAME_RESULT_CODE = "aavCardholderNameResultCode";
    private static final String AAV_TELEPHONE_RESULT_CODE = "aavTelephoneResultCode";

    @InjectMocks
    private WorldpayAavResponsePopulator testObj = new WorldpayAavResponsePopulator();

    @Mock
    private PaymentReply paymentReplyMock;

    private WorldpayAavResponseModel worldpayAavResponseModel;

    @Before
    public void setup() {
        worldpayAavResponseModel = new WorldpayAavResponseModel();
        when(paymentReplyMock.getAavAddressResultCode()).thenReturn(AAV_ADDRESS_RESULT_CODE);
        when(paymentReplyMock.getAavCardholderNameResultCode()).thenReturn(AAV_CARDHOLDER_NAME_RESULT_CODE);
        when(paymentReplyMock.getAavEmailResultCode()).thenReturn(AAV_EMAIL_RESULT_CODE);
        when(paymentReplyMock.getAavPostcodeResultCode()).thenReturn(AAV_POSTAL_CODE_RESULT_CODE);
        when(paymentReplyMock.getAavTelephoneResultCode()).thenReturn(AAV_TELEPHONE_RESULT_CODE);
    }

    @Test
    public void shouldPopulateAavAddressResultCode() throws Exception {

        testObj.populate(paymentReplyMock, worldpayAavResponseModel);

        assertEquals(AAV_ADDRESS_RESULT_CODE, worldpayAavResponseModel.getAavAddressResultCode());
    }
    @Test
    public void shouldPopulateAavCardholderNameResultCode() throws Exception {

        testObj.populate(paymentReplyMock, worldpayAavResponseModel);

        assertEquals(AAV_CARDHOLDER_NAME_RESULT_CODE, worldpayAavResponseModel.getAavCardholderNameResultCode());
    }
    @Test
    public void shouldPopulateAavEmailResultCode() throws Exception {

        testObj.populate(paymentReplyMock, worldpayAavResponseModel);

        assertEquals(AAV_EMAIL_RESULT_CODE, worldpayAavResponseModel.getAavEmailResultCode());
    }
    @Test
    public void shouldPopulateAavPostcodeResultCode() throws Exception {

        testObj.populate(paymentReplyMock, worldpayAavResponseModel);

        assertEquals(AAV_POSTAL_CODE_RESULT_CODE, worldpayAavResponseModel.getAavPostcodeResultCode());
    }
    @Test
    public void shouldPopulateAavTelephoneResultCode() throws Exception {

        testObj.populate(paymentReplyMock, worldpayAavResponseModel);

        assertEquals(AAV_TELEPHONE_RESULT_CODE, worldpayAavResponseModel.getAavTelephoneResultCode());
    }
}