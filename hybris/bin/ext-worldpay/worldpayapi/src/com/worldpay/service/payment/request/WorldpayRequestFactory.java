package com.worldpay.service.payment.request;

import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.CreateTokenServiceRequest;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import com.worldpay.service.response.CreateTokenResponse;
import de.hybris.platform.core.model.order.CartModel;


public interface WorldpayRequestFactory {

    /**
     * Builds a create token request to send to Worldpay
     *
     * @param merchantInfo               the merchantInfo
     * @param cartModel                  the session cart
     * @param cseAdditionalAuthInfo      contains cse form specific information for the request including encrypted payment information
     * @param worldpayAdditionalInfoData the worldpayAdditionalInfoData
     * @return
     * @throws WorldpayConfigurationException
     */
    CreateTokenServiceRequest buildTokenRequest(MerchantInfo merchantInfo, CartModel cartModel, CSEAdditionalAuthInfo cseAdditionalAuthInfo, WorldpayAdditionalInfoData worldpayAdditionalInfoData)
            throws WorldpayConfigurationException;

    /**
     * Builds an update token request to send to Worldpay
     *
     * @param merchantInfo               the merchantInfo
     * @param cseAdditionalAuthInfo      contains cse form specific information for the request including encrypted payment information
     * @param worldpayAdditionalInfoData the worldpayAdditionalInfoData
     * @param createTokenResponse
     * @return
     * @throws WorldpayConfigurationException
     */
    UpdateTokenServiceRequest buildTokenUpdateRequest(MerchantInfo merchantInfo, CSEAdditionalAuthInfo cseAdditionalAuthInfo, WorldpayAdditionalInfoData worldpayAdditionalInfoData, final CreateTokenResponse createTokenResponse)
            throws WorldpayConfigurationException;

    /**
     * Builds an authorise request to send to Worldpay
     *
     * @param merchantInfo               the merchantInfo
     * @param cartModel                  the session cart
     * @param worldpayAdditionalInfoData the worldpayAdditionalInfoData
     * @return
     * @throws WorldpayConfigurationException
     */
    DirectAuthoriseServiceRequest buildDirectAuthoriseRequest(MerchantInfo merchantInfo, CartModel cartModel, WorldpayAdditionalInfoData worldpayAdditionalInfoData)
            throws WorldpayConfigurationException;

    /**
     * Builds a 3D secure direct request to send to Worldpay
     *
     * @param merchantInfo               the merchantInfo
     * @param cartModel                  the session cart
     * @param worldpayAdditionalInfoData the worldpayAdditionalInfoData
     * @param paRes                      the payer Response required for 3D request
     * @param echoData                   the echo Data from the authorise response
     * @param cookie                     the cookie from the authorise response
     * @return
     * @throws WorldpayConfigurationException
     */
    DirectAuthoriseServiceRequest build3dDirectAuthoriseRequest(MerchantInfo merchantInfo, CartModel cartModel,
                                                                WorldpayAdditionalInfoData worldpayAdditionalInfoData,
                                                                String paRes, String echoData, String cookie) throws WorldpayConfigurationException;

    /**
     * Builds a direct authorise request to send to Worldpay
     *
     * @param merchantInfo
     * @param cartModel
     * @param bankTransferAdditionalAuthInfo
     * @param worldpayAdditionalInfoData
     * @return
     * @throws WorldpayConfigurationException
     */
    DirectAuthoriseServiceRequest buildDirectAuthoriseBankTransferRequest(MerchantInfo merchantInfo, CartModel cartModel,
                                                                          BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfo,
                                                                          WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws WorldpayConfigurationException;

    /**
     * Builds an authorise recurring payment request to send to Worldpay
     *
     * @param merchantInfo               the merchantInfo
     * @param cartModel                  the session cart
     * @param worldpayAdditionalInfoData the worldpayAdditionalInfoData
     * @return Built {@link DirectAuthoriseServiceRequest}
     * @throws WorldpayConfigurationException
     */
    DirectAuthoriseServiceRequest buildDirectAuthoriseRecurringPayment(MerchantInfo merchantInfo, CartModel cartModel, WorldpayAdditionalInfoData worldpayAdditionalInfoData)
                throws WorldpayConfigurationException;
}
