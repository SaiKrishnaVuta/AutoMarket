package com.market.service;

import com.market.entity.HistoricData;
import com.market.fyers.model.HistoricDataCollectionRequestDto;
import com.market.fyers.model.HistoricDataResponse;
import com.market.fyers.model.SymbolDto;
import com.market.repository.HistoricDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FyersAppService {
	
	public static final Logger logger = null;
	
	@Autowired
	public HistoricDataRepository historicDataRepository;

	@Autowired
	public FyersApiService fyersApiService;
	
	
	public void getHistoricStoredData() {
		try {
			HistoricData h = new HistoricData();
			h.setSymbol("abc");
			Date d=new Date(); 
			h.setDatetime(d);
			HistoricData p=historicDataRepository.save(h);
			System.out.println("Ok it is :"+p+"ht");
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	

	
	public SymbolDto toSymbolDto(String symbol) {
		SymbolDto symbolDto= new SymbolDto();
		symbolDto.setFullSymbol(symbol);
		symbolDto.setMarket(symbol.substring(0, 3));
		symbolDto.setInstrument(symbol.substring(4, 13));
		if(symbolDto.getInstrument().equalsIgnoreCase("BANKNIFTY")) {
			String datePart=symbol.substring(13, 18);
			symbolDto.setYear(datePart.substring(0, 2));
			if(datePart.substring(2, 5).matches("[a-zA-Z]+")) {
				symbolDto.setMonth(datePart.substring(2, 5));
				symbolDto.setDate("-1");
			}
			else {
				symbolDto.setDate(datePart.substring(3, 5));
				symbolDto.setMonth(datePart.substring(2, 3));
			}
			symbolDto.setStrike(symbol.substring(18, 23));
			symbolDto.setOptionType(symbol.substring(23, 25));
			System.out.println(symbolDto);
			
		}
		return symbolDto;
	}
	
	public String toSymbol(SymbolDto symbolDto) {
		String symbol="";
		if(symbolDto.getDate().equals("-1")) {
			symbol=symbol.concat(symbolDto.getMarket()+":"+symbolDto.getInstrument()+symbolDto.getYear()
			+symbolDto.getMonth()+symbolDto.getStrike()+symbolDto.getOptionType());
		}
		else {
			symbol=symbol.concat(symbolDto.getMarket()+":"+symbolDto.getInstrument()+symbolDto.getYear()
			+symbolDto.getMonth()+symbolDto.getDate()+symbolDto.getStrike()+symbolDto.getOptionType());
		}
		return symbol;
	}
	
	
	public List<String> generateSymbolList(String symbol) {
		SymbolDto symbolDto=this.toSymbolDto(symbol);
		List<String> symbolList= new ArrayList<>();
		if(symbolDto.getStrike().matches("[0-9]+")) {
			Integer strike = Integer.valueOf(symbolDto.getStrike());
			Integer upStrike =0;
			Integer downStrike =0;
			symbolDto.setOptionType("CE");
			symbolList.add(this.toSymbol(symbolDto));
			symbolDto.setOptionType("PE");
			symbolList.add(this.toSymbol(symbolDto));
			for(int i=1; i<=10; i++) {
				upStrike=strike+(i*100);
				downStrike=strike-(i*100);
				symbolDto.setStrike(upStrike.toString());
				symbolDto.setOptionType("CE");
				symbolList.add(this.toSymbol(symbolDto));
				symbolDto.setOptionType("PE");
				symbolList.add(this.toSymbol(symbolDto));
				symbolDto.setStrike(downStrike.toString());
				symbolDto.setOptionType("CE");
				symbolList.add(this.toSymbol(symbolDto));
				symbolDto.setOptionType("PE");
				symbolList.add(this.toSymbol(symbolDto));
			}
		}else {
			throw(new ArithmeticException("Strike not a number"));
		}
		log.info("Generated Symbol List from symbol ::"+symbolList);
		return symbolList;
	}

	public List<HistoricData> saveHistoricDate(List<HistoricDataResponse> historicDataResponseList){
		List<HistoricData> historicDataList= new ArrayList<>();
		for(HistoricDataResponse historicDataResponse: historicDataResponseList){
			for(List<Double> candlestick:historicDataResponse.getCandles()){
				HistoricData candle= new HistoricData();
				Double epoch =candlestick.get(0);
				Date date = new java.util.Date ((long) (epoch*1000));
				candle.setDatetime(date);
				candle.setSymbol(historicDataResponse.getSymbol());
				candle.setOpen(candlestick.get(1));
				candle.setHigh(candlestick.get(2));
				candle.setLow(candlestick.get(3));
				candle.setClose(candlestick.get(4));
				candle.setVolume(candlestick.get(5));
				historicDataList.add(candle);
			}
		}
		if(!historicDataList.isEmpty()){
			return historicDataRepository.saveAll(historicDataList);
		}
		return null;
	}

	public List<HistoricData> storeHistoricDataIndex(HistoricDataCollectionRequestDto historicDataCollectionRequestDto){
		List<HistoricDataResponse> historicDataResponseList= new ArrayList<>();
		HistoricDataResponse historicDataResponse=fyersApiService.getHistData(historicDataCollectionRequestDto.getSymbol(), historicDataCollectionRequestDto.getFromDate(),
				historicDataCollectionRequestDto.getToDate(), historicDataCollectionRequestDto.getResolution());
		historicDataResponseList.add(historicDataResponse);
		if(!(historicDataResponseList.isEmpty()))
			return this.saveHistoricDate(historicDataResponseList);
		else
			return null;
	}

	public List<HistoricData> storeHistoricData(HistoricDataCollectionRequestDto historicDataCollectionRequestDto){
		List<String> symbolList=this.generateSymbolList(historicDataCollectionRequestDto.getSymbol());
		List<HistoricDataResponse> historicDataResponseList= new ArrayList<>();
		try{
			if(!(symbolList.isEmpty())){
				for(String symbol: symbolList){
					HistoricDataResponse historicDataResponse=fyersApiService.getHistData(symbol, historicDataCollectionRequestDto.getFromDate(),
							historicDataCollectionRequestDto.getToDate(), historicDataCollectionRequestDto.getResolution());
					historicDataResponseList.add(historicDataResponse);
					TimeUnit.SECONDS.sleep(1);
				}
			}
		}catch (InterruptedException e){
			log.error("Ëxception occured:: "+e.getMessage());
		}
		if(!(historicDataResponseList.isEmpty()))
			return this.saveHistoricDate(historicDataResponseList);
		else
			return null;
	}
	public Long isAround(double d){
		long l=((long) d+50)/100;
		return (l)*100;
	}
	public HistoricData isAround(List<HistoricData> strikeList, Double desired){
		Map<HistoricData, Double> strikeMap= new HashMap<>();
		Set<Double> differences = new HashSet<>();
		List<Double> differencesList = new ArrayList<>();
		Double difference = 0.0;
		log.info("Strike List ::"+strikeList);
		for(HistoricData strike: strikeList){
			difference=strike.getClose()-desired;
			if(difference<0)
				difference=difference*(-1);
			strikeMap.put(strike, difference);

			if(!(differencesList.contains(difference))){
				differencesList.add(difference);
			}
		}
		log.info("Differences List ::"+differencesList);
		Collections.sort(differencesList);
		return this.getKey(strikeMap, differencesList.get(0));
	}

	public HistoricData getKey(Map<HistoricData, Double> map, Double value)
	{
		for (Map.Entry<HistoricData, Double> entry: map.entrySet())
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		return null;
	}
}
