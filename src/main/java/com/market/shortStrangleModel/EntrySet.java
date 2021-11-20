package com.market.shortStrangleModel;

import com.market.entity.HistoricData;
import lombok.Data;

@Data
public class EntrySet {
    HistoricData entryCall;
    HistoricData entryPut;
}
