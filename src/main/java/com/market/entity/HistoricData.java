package com.market.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "historicdata")
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(HistoricKey.class)
public class HistoricData {
	@Id
	@Column(name = "symbol", nullable = false)
	private String symbol;
	@Id
	@Column(name = "datetime", nullable = false)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale = "hi-IN", timezone = "Asia/Kolkata")
	private Date datetime;
	@Column(name = "open")
	private Double open;
	@Column(name = "high")
	private Double high;
	@Column(name = "low")
	private Double low;
	@Column(name = "close")
	private Double close;
	@Column(name = "volume")
	private Double volume;
}

