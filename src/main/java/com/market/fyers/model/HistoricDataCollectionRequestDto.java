package com.market.fyers.model;


import lombok.Data;

@Data
public class HistoricDataCollectionRequestDto {
	private String symbol;
	private String fromDate;
	private String toDate;
	private String resolution;
}
