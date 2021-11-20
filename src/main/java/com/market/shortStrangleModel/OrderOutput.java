package com.market.shortStrangleModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.market.entity.Orders;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrderOutput {
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale = "hi-IN", timezone = "Asia/Kolkata")
    private Date date;
    private String remarks;
    private Double BankNifty_currentValue;
    private List<OrderDetails> sSOrdersList;
    private Double profit_or_Loss;
}
