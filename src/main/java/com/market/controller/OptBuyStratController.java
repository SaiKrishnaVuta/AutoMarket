package com.market.controller;


import com.market.backTesting.model.OBBTEntryOrderOuput;
import com.market.backTesting.model.OptionBuyingInputDto;
import com.market.fyersBackTesting.OptionBuyingStrategyService;
import com.market.shortStrangleModel.EntryOrderOutput;
import com.market.shortStrangleModel.EntryRequestInputDto;
import com.market.shortStrangleModel.OrderOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/OBBT")
@Slf4j
public class OptBuyStratController {

    @Autowired
    OptionBuyingStrategyService optionBuyingStrategyService;

    @PostMapping(value = "/observe/entry")
    public EntryOrderOutput entrySetup(@RequestBody OptionBuyingInputDto input){
        log.info("Input :: "+input);
        return optionBuyingStrategyService.entrySetup(input.getCDate(), input.getLots());
    }

    @GetMapping(value = "/observeforentry/{min}/mins")
    public OBBTEntryOrderOuput observeForEntry(@PathVariable("min") String n){
        log.info("No of Mins ::"+n);
        Integer mins=Integer.valueOf(n);
        return optionBuyingStrategyService.observeNEnter(mins);
    }

    @GetMapping(value = "/observeforexit/{min}/mins")
    public OBBTEntryOrderOuput observeForExit(@PathVariable("min") String n){
        log.info("No of Mins ::"+n);
        Integer mins=Integer.valueOf(n);
        return optionBuyingStrategyService.observeNexit(mins);
    }
}
