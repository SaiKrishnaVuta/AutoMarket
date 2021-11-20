package com.market.controller;

import java.util.List;

import com.market.fyers.model.HistoricDataCollectionRequestDto;
import com.market.fyers.model.HistoricDataResponse;
import com.market.fyers.model.ProfileDto;
import com.market.service.FyersApiService;
import com.market.service.FyersAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.market.entity.HistoricData;
import com.market.repository.HistoricDataRepository;

@RestController
@RequestMapping("/product")
@Slf4j
public class CommonController {
	@Autowired
	public FyersAppService fyersAppService;

	@Autowired
	FyersApiService fyersApiService;

	@Autowired
	public HistoricDataRepository historicDataRepository;
	@GetMapping(value = "/findall")
	public List<HistoricData> getAll(){
		return historicDataRepository.findAll();
	}

	@GetMapping(value = "/getProfileData")
	public ProfileDto getProfileData(){
		return fyersApiService.getMyProfile().getBody();
	}

	@GetMapping(value = "/getHistoricData")
	public HistoricDataResponse getHistoricData(@RequestBody HistoricDataCollectionRequestDto historicDataCollectionRequestDto){
		return fyersApiService.getHistData(historicDataCollectionRequestDto.getSymbol(), historicDataCollectionRequestDto.getFromDate(),
				historicDataCollectionRequestDto.getToDate(), historicDataCollectionRequestDto.getResolution());
	}


	@PostMapping(value = "/saveData")
	public List<HistoricData> saveHistoricData(@RequestBody HistoricDataCollectionRequestDto historicDataCollectionRequestDto){
		return fyersAppService.storeHistoricData(historicDataCollectionRequestDto);
	}

	@PostMapping(value = "/saveIndexData")
	public List<HistoricData> saveIndexHistoricData(@RequestBody HistoricDataCollectionRequestDto historicDataCollectionRequestDto){
		return fyersAppService.storeHistoricDataIndex(historicDataCollectionRequestDto);
	}
	
}
