package com.market.controller;

import com.market.fyers.model.ProfileDto;
import com.market.fyersBackTesting.ShortStrangleBackTestingService;
import com.market.service.FyersApiService;
import com.market.service.FyersAppService;
import com.market.shortStrangleModel.EntryOrderOutput;
import com.market.shortStrangleModel.EntryRequestInputDto;
import com.market.shortStrangleModel.EntrySet;
import com.market.shortStrangleModel.OrderOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/SSBT")
@Slf4j
public class SSBackTesingController {
    @Autowired
    public ShortStrangleBackTestingService shortStrangleBackTestingService;

    @PostMapping(value = "/entry")
    public EntryOrderOutput getProfileData(@RequestBody EntryRequestInputDto input){
        return shortStrangleBackTestingService.entry(input.getEntryDate(), input.getSymbol(), input.getDesired(), input.getLots());
    }

    @GetMapping(value = "/observeAfter/{min}/mins")
    public OrderOutput afterNMins(@PathVariable("min") String n){
        log.info("No of Mins ::"+n);
        Integer mins=Integer.valueOf(n);
        return shortStrangleBackTestingService.afterNMins(mins);
    }
}
