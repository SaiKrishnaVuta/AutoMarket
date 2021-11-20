package com.market.repository;

import com.market.entity.HistoricData;
import com.market.entity.HistoricKey;
import com.market.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SSBTTransactionReopsitory extends JpaRepository<Transactions, String>{
}