package com.market.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

@Service
public class PropertyUtils {
    private static Logger logger = LoggerFactory.getLogger(PropertyUtils.class);
    public String getToken(){
        String token="";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy");
        Date currentDate= new Date();
        String today= simpleDateFormat.format(currentDate).toString();
        logger.info("Date::"+today);
        String fileLocation= "C:\\Users\\saikrishnavuta\\Desktop\\share\\fyers_logs\\token_logs\\"+today+".log";
        logger.info("Location::"+fileLocation);
        try{
            File file = new File(fileLocation);
            Scanner scanner= new Scanner(file);
            while (scanner.hasNextLine()){
                String linevalue=scanner.nextLine();
                logger.info("Inside File ::"+linevalue);
                token=linevalue.split(" ")[1];
            }
        }catch(FileNotFoundException fe){
            logger.error("FileNotFoundExceptionOccured::"+fe);
        }catch(IOException e){
            logger.error("IOException occured::"+e);
        }
        logger.info("Token ::"+token);
        return token;
    }

}
