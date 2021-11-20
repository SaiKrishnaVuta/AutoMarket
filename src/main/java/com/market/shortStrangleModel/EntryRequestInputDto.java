package com.market.shortStrangleModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class EntryRequestInputDto {
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale = "hi-IN", timezone = "Asia/Kolkata")
    private Date entryDate;
    private String symbol;
    private Double desired;
    private Integer lots;
}
