package com.market.fyers.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileData {
	private String name;
	private String image;
	private String display_name;
	private String email_id;
	private String PAN;
	private String fy_id;
	private String pwd_change_date;
	private int pwd_to_expire;
	
}
