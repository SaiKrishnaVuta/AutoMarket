package com.market;


import com.market.service.PropertyUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

		PropertyUtils propertyUtils= new PropertyUtils();
		propertyUtils.getToken();
		
//		AppOperations app = new AppOperations();
//		app.launchApp();
		//FyersApiService service = new FyersApiService();
		//service.getHistData("NSE:BANKNIFTY2190238800CE", "2021-08-18", "2021-08-23");
		//service.getMyProfile();
		
//		service.getMarketDepth("NSE:BANKNIFTY21AUG40100CE");
//		FyersAppService fyersAppService= new FyersAppService();
//		fyersAppService.getHistoricStoredData();
//		fyersAppService.generateSymbolList("NSE:BANKNIFTY2190238800CE");
	}
}
