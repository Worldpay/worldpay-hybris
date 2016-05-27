package com.worldpay.worldpayresponsemock.controllers.pages;

import com.worldpay.exception.WorldpayException;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.util.WorldpayConstants;
import com.worldpay.worldpayresponsemock.facades.WorldpayMockFacade;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.POST;


@Controller
@RequestMapping (value = "/mock")
public class WorldpayMockController {

    private static final Logger LOG = Logger.getLogger(WorldpayMockController.class);

    @Resource
    private WorldpayMockFacade worldpayMockFacade;

    @RequestMapping (method = POST)
    @ResponseBody
    public String mockWorldpayResponse(final HttpServletRequest request) {
        try (BufferedReader reader = request.getReader()) {
            Unmarshaller unmarshaller = createUnmarshaller();
            PaymentService paymentService = (PaymentService) unmarshaller.unmarshal(reader);
            return worldpayMockFacade.buildResponse(paymentService, request);
        } catch (IOException e) {
            LOG.error("Failed to get bufferedReader from request", e);
            return "fail";
        } catch (WorldpayException | JAXBException e) {
            LOG.error("There was an error unmarshalling the request", e);
            return "fail";
        }
    }

    protected Unmarshaller createUnmarshaller() throws JAXBException {
        return WorldpayConstants.JAXB_CONTEXT.createUnmarshaller();
    }
}
