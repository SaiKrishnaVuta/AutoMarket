package com.market.backTesting.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
@Data
public class OBBTOrderDetails {
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale = "hi-IN", timezone = "Asia/Kolkata")
    private Date initialdatetime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale = "hi-IN", timezone = "Asia/Kolkata")
    private Date currentdatetime;
    private String symbol;
    private int no_of_lots;
    private Double buy_price;
    private Double sell_price;
    private Double change_In_Percent;
    private Double current_price;
    private Double initial_price;
    private String status;
    private Double profit_or_loss;
    private Double stoploss_percent;
    private Double target_percent;
}
