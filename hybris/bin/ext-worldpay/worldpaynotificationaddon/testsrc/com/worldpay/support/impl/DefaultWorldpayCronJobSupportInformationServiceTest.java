package com.worldpay.support.impl;

import com.worldpay.worldpaynotificationaddon.model.OrderModificationCronJobModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;
import static de.hybris.platform.testframework.Assert.assertCollection;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayCronJobSupportInformationServiceTest {

    @InjectMocks
    private DefaultWorldpayCronJobSupportInformationService testObj = new DefaultWorldpayCronJobSupportInformationService();

    @Mock
    private FlexibleSearchService flexibleSearchServiceMock;

    @Mock
    private OrderModificationCronJobModel orderModificationCronJobModelForAuthMock;
    @Mock
    private OrderModificationCronJobModel orderModificationCronJobModelForCaptureMock;

    @Test
    public void shouldReturnPaymentTypesCheckedByAllOrderModificationCronjobs() {
        when(flexibleSearchServiceMock.getModelsByExample(any(OrderModificationCronJobModel.class))).thenReturn(asList(orderModificationCronJobModelForAuthMock, orderModificationCronJobModelForCaptureMock));
        when(orderModificationCronJobModelForAuthMock.getTypeOfPaymentTransactionToProcessSet()).thenReturn(singleton(AUTHORIZATION));
        when(orderModificationCronJobModelForCaptureMock.getTypeOfPaymentTransactionToProcessSet()).thenReturn(singleton(CAPTURE));

        final Set<PaymentTransactionType> result = testObj.getPaymentTransactionType();

        assertCollection(result, asList(AUTHORIZATION, CAPTURE));
    }
}