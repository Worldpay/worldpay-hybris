package com.worldpay.controllers;

import com.worldpay.facades.BankConfigurationData;
import com.worldpay.facades.WorldpayBankConfigurationFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping (value = "/worldpay")
/**
 * Controller for Bank configurations related to APMs for Bank Transfer
 */
public class WorldpayBankController {

    @Resource
    private WorldpayBankConfigurationFacade worldpayBankConfigurationFacade;

    @ResponseBody
    @RequestMapping (value = "/{apm}/banks", method = GET)
    /**
     * Returns the list of banks configured for the {@param apm}
     */
    public List<BankConfigurationData> getBanksForAPM(@PathVariable String apm) {
        return worldpayBankConfigurationFacade.getBankConfigurationForAPMCode(apm);
    }
}
