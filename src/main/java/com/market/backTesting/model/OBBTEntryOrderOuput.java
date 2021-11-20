package com.market.backTesting.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.market.entity.Orders;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OBBTEntryOrderOuput {
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale = "hi-IN", timezone = "Asia/Kolkata")
    private Date date;
    private Double bankNifty_currentValue;
    private List<OBBTOrderDetails> ordersList;
    private Double profit_or_Loss;
    private String remarks;
}
