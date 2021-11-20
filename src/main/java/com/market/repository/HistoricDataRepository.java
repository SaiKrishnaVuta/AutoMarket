package com.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.market.entity.HistoricKey;
import com.market.entity.HistoricData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HistoricDataRepository extends JpaRepository<HistoricData, HistoricKey> {

    @Query(value = "SELECT open, high, low, close, volume, symbol, datetime\n" +
            "\tFROM public.historicdata where symbol like ( CONCAT('%',:symbolPart)) " +
            "and datetime in (:cDate)", nativeQuery = true)
    List<HistoricData> getHistoricDataEndsWith(String symbolPart, Date cDate);

}
