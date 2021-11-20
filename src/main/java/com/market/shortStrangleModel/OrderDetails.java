package com.market.shortStrangleModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class OrderDetails {
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale = "hi-IN", timezone = "Asia/Kolkata")
    private Date initialdatetime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale = "hi-IN", timezone = "Asia/Kolkata")
    private Date currentdatetime;
    private String symbol;
    private int no_of_lots;
    private Double buy_price;
    private Double sell_price;
    private Double current_price;
    private Double profit_or_loss;
    private String status;
}
