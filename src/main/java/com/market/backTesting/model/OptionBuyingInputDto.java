package com.market.backTesting.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class OptionBuyingInputDto {
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale = "hi-IN", timezone = "Asia/Kolkata")
    Date cDate;
    Integer lots;
}
