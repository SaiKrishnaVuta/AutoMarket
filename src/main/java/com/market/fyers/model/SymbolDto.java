package com.market.fyers.model;

import lombok.Data;

@Data
public class SymbolDto {
	String fullSymbol;
	String market;
	String month;
	String date;
	String year;
	String strike;
	String optionType;
	String instrument;
}
