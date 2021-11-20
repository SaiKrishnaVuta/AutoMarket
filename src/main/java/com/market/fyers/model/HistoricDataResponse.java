package com.market.fyers.model;

import java.util.List;

import lombok.Data;

@Data
public class HistoricDataResponse {
	private String s;
    private String symbol;
    private List<List<Double>> candles;
}
