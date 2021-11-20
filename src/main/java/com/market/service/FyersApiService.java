package com.market.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.market.fyers.model.HistoricDataResponse;
import com.market.fyers.model.ProfileDto;

@Service
@Slf4j
public class FyersApiService {

	@Autowired
	PropertyUtils propertyUtils;

	protected static String access_token = null;

	String app_id ="EQNDMNXYA2-100";
	
	protected final String GET_PROFILE_URL = "https://api.fyers.in/api/v2/profile";
	protected final String GET_MDEPTH_URL = "https://api.fyers.in/data-rest/v2/quotes/?symbols=";
	protected final String GET_HDATA_URL = "https://api.fyers.in/data-rest/v2/history/?symbol=";

	
	public ResponseEntity<ProfileDto> getMyProfile() {
		try {
			access_token=propertyUtils.getToken();
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders reqheader = new HttpHeaders();
		    //set up HTTP Basic Authentication
		    reqheader.add("Authorization", app_id+":"+access_token);
		    reqheader.add("Accept", MediaType.APPLICATION_JSON_VALUE);
			HttpEntity<String> requestEntity = new HttpEntity<>(null, reqheader);
			//ResponseEntity<String> result = restTemplate.exchange(GET_PROFILE_URL, HttpMethod.GET, requestEntity, String.class);
			ResponseEntity<ProfileDto> result = restTemplate.exchange(GET_PROFILE_URL, HttpMethod.GET, requestEntity, ProfileDto.class);
			log.info("Profile Info::"+result.getBody());
			return result;
		} catch(Exception e) {
			log.error("Error occured in getMyProfile of FyersApiService ::"+e.getMessage());
		}

		return null;
	}

	public void getMarketDepth(String symbol) {
		try {
			access_token=propertyUtils.getToken();
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders reqheader = new HttpHeaders();
		    //set up HTTP Basic Authentication 
			//symbol ="NSE:BANKNIFTY21AUG40100CE";
			reqheader.add("Authorization", app_id+":"+access_token);
		    reqheader.add("Accept", MediaType.APPLICATION_JSON_VALUE);
			HttpEntity<String> requestEntity = new HttpEntity<>(null, reqheader);
			//ResponseEntity<String> result = restTemplate.exchange(GET_PROFILE_URL, HttpMethod.GET, requestEntity, String.class);
			ResponseEntity<String> result = restTemplate.exchange(GET_MDEPTH_URL+symbol, HttpMethod.GET, requestEntity, String.class);
			System.out.println(result.getBody());
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public HistoricDataResponse getHistData(String symbol, String rangeFrom, String rangeTo, String resolution) {
		String dateFormat="1";
		try {
			access_token=propertyUtils.getToken();
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders reqheader = new HttpHeaders();
			reqheader.add("Authorization", app_id+":"+access_token);
		    reqheader.add("Accept", MediaType.APPLICATION_JSON_VALUE);
			HttpEntity<String> requestEntity = new HttpEntity<>(null, reqheader);
			String url=GET_HDATA_URL+symbol+"&resolution="+resolution+"&date_format="+dateFormat+"&range_from="+rangeFrom+"&range_to="+rangeTo+"&cont_flag=";
			log.info("Historic Data URL::"+url);
			ResponseEntity<HistoricDataResponse> result = restTemplate.exchange(url, HttpMethod.GET, requestEntity, HistoricDataResponse.class);
			log.info("Get Historic Data Fyers Api Response ::"+result.getStatusCode().toString());
			if(result.getStatusCodeValue()==200){
				result.getBody().setSymbol(symbol);
				log.info("Get Historic Data Fyers Api Response ::"+result.getBody());
				return result.getBody();
            }else{
                throw new Exception("Fyers Api Error");
            }
		} catch(Exception e) {
			log.error("Error in getHistData of  FyersApiService ::"+e.getMessage());
		}
		return null;
	}
}
