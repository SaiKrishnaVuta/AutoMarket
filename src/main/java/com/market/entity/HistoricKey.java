package com.market.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class HistoricKey implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9007802182936659027L;
	private String symbol;
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale = "hi-IN", timezone = "Asia/Kolkata")
	private Date datetime;
	
	
	
}
