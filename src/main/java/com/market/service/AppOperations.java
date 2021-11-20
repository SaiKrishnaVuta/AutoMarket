package com.market.service;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Locale;
//import java.util.concurrent.TimeUnit;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.neovisionaries.ws.client.WebSocketException;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.SessionExpiryHook;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;
//import org.springframework.web.client.RestTemplate;

@Service
public class AppOperations {

	public void launchApp() {

		try {
			// First you should get request_token, public_token using kitconnect login and
			// then use request_token, public_token, api_secret to make any kiteConnect api
			// call.
			// Initialize KiteSdk with your apiKey.
			KiteConnect kiteConnect = new KiteConnect("xxxxyyyyzzzz");

			// If you wish to enable debug logs send true in the constructor, this will log
			// request and response.
			// KiteConnect kiteConnect = new KiteConnect("xxxxyyyyzzzz", true);

			// If you wish to set proxy then pass proxy as a second parameter in the
			// constructor with api_key. syntax:- new KiteConnect("xxxxxxyyyyyzzz", proxy).
			// KiteConnect kiteConnect = new KiteConnect("xxxxyyyyzzzz", userProxy, false);

			// Set userId
			kiteConnect.setUserId("xxxxx");

			// Get login url
			String url = kiteConnect.getLoginURL();

			// Set session expiry callback.
			kiteConnect.setSessionExpiryHook(new SessionExpiryHook() {
				@Override
				public void sessionExpired() {
					System.out.println("session expired");
				}
			});

			/*
			 * The request token can to be obtained after completion of login process. Check
			 * out https://kite.trade/docs/connect/v3/user/#login-flow for more information.
			 * A request token is valid for only a couple of minutes and can be used only
			 * once. An access token is valid for one whole day. Don't call this method for
			 * every app run. Once an access token is received it should be stored in
			 * preferences or database for further usage.
			 */
			User user = kiteConnect.generateSession("xxxxxtttyyy", "xxxxxxxyyyyy");
			kiteConnect.setAccessToken(user.accessToken);
			kiteConnect.setPublicToken(user.publicToken);

			// service service = new service();
			KiteConnectService service = new KiteConnectService();
			service.getProfile(kiteConnect);

			service.getMargins(kiteConnect);

			service.getMarginCalculation(kiteConnect);

		} catch (KiteException exception) {
			System.out.println(exception.message + " " + exception.code + " " + exception.getClass().getName());
		} catch (JSONException exception1) {
			exception1.printStackTrace();
		} catch (IOException e3) {
			e3.printStackTrace();
		}

	}

	public void runStrategy(KiteConnect kiteConnect, KiteConnectService service) {
		try {
			String[] instruments = {
					  "BANKNIFTY2181235000PE"
					, "BANKNIFTY2181235000PE"
					, "BANKNIFTY2181235000PE"
					, "BANKNIFTY2181235000PE"
					, "BANKNIFTY2181235000PE", };
			service.getLTP(kiteConnect, instruments);
			
			service.placeOrder(kiteConnect);

			service.modifyOrder(kiteConnect);

			service.cancelOrder(kiteConnect);

			service.placeBracketOrder(kiteConnect);

			service.modifyFirstLegBo(kiteConnect);

			service.modifySecondLegBoSLM(kiteConnect);

			service.modifySecondLegBoLIMIT(kiteConnect);

			service.exitBracketOrder(kiteConnect);

			service.getTriggerRange(kiteConnect);

			service.placeCoverOrder(kiteConnect);

			service.converPosition(kiteConnect);

			service.getHistoricalData(kiteConnect);

			service.getOrders(kiteConnect);

			service.getOrder(kiteConnect);

			service.getTrades(kiteConnect);

			service.getTradesWithOrderId(kiteConnect);

			service.getPositions(kiteConnect);

			service.getHoldings(kiteConnect);

			service.getAllInstruments(kiteConnect);

			service.getInstrumentsForExchange(kiteConnect);

			service.getQuote(kiteConnect);

			service.getOHLC(kiteConnect);

			service.getLTP(kiteConnect);

			service.getGTTs(kiteConnect);

			service.getGTT(kiteConnect);

			service.placeGTT(kiteConnect);

			service.modifyGTT(kiteConnect);

			service.cancelGTT(kiteConnect);

			service.getMFInstruments(kiteConnect);

			service.placeMFOrder(kiteConnect);

			service.cancelMFOrder(kiteConnect);

			service.getMFOrders(kiteConnect);

			service.getMFOrder(kiteConnect);

			service.placeMFSIP(kiteConnect);

			service.modifyMFSIP(kiteConnect);

			service.cancelMFSIP(kiteConnect);

			service.getMFSIPS(kiteConnect);

			service.getMFSIP(kiteConnect);

			service.getMFHoldings(kiteConnect);

			service.logout(kiteConnect);

			ArrayList<Long> tokens = new ArrayList<>();
			tokens.add(Long.parseLong("256265"));
			service.tickerUsage(kiteConnect, tokens);
		} catch (KiteException exception) {
			System.out.println(exception.message + " " + exception.code + " " + exception.getClass().getName());
		} catch (JSONException exception1) {
			exception1.printStackTrace();
		} catch (IOException e3) {
			e3.printStackTrace();
		} catch (WebSocketException e) {
			e.printStackTrace();
		}
	}
//	public void launchApplication() {
//		System.setProperty("webdriver.chrome.driver","C:/chromedriver.exe");
//
//        WebDriver dr1= new ChromeDriver();
//
//        dr1.get("https://kite.zerodha.com/");
//        dr1.manage().window().maximize();
//        WebElement userid=dr1.findElement(By.id("userid"));
//        WebElement password=dr1.findElement(By.id("password"));
//        
//        WebElement login = dr1.findElement(By.className("button-orange"));
//        userid.sendKeys("JH4958");
//        password.sendKeys("Rishi@1996");
//        login.click();
//        try {
//        	//dr1.wait(2L);
//        	dr1.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
//        	//System.out.println("exception occured here");
//        	WebElement pin=dr1.findElement(By.id("pin"));
//        	pin.sendKeys("171996");
//        	login.click();
//        }catch(Exception e) {
//        	System.out.println("exception occured" +e.getMessage());
//        }
//	}
//	
//	
//	public void getOIData() {
//		
//		System.setProperty("webdriver.chrome.driver","C:/chromedriver.exe");
//
//	    WebDriver dr1= new ChromeDriver();
//	
//	    dr1.get("https://web.sensibull.com/option-chain?expiry=2021-07-22&tradingsymbol=BANKNIFTY");
//	    dr1.manage().window().maximize();
//	}
//	
//	public void getOptionChainData() {
//		System.out.println("Enter the dragon");
//		try {
//			String url="https://www.nseindia.com/api/option-chain-indices?symbol=BANKNIFTY";
//			RestTemplate restTemplate = new RestTemplate();
//			HttpHeaders httpHeaders = new HttpHeaders();
//			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//			List<Locale.LanguageRange> locales = new ArrayList<Locale.LanguageRange>();
//			httpHeaders.setAcceptLanguage(locales);
//			//ht
//			HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
//			System.out.println("Dragon ready to race");
//			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
//			System.out.println("Data :"+responseEntity);
//		}catch(Exception e) {
//			System.out.println(e.getMessage());
//		}
//		System.out.println("Dragon died");
//	}
}
