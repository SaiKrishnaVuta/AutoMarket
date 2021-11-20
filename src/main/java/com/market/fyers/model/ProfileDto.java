package com.market.fyers.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {
	private String s;
	private int code;
	private String message;
	private ProfileData data;
}
