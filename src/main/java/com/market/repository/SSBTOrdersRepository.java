package com.market.repository;

import com.market.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface SSBTOrdersRepository extends JpaRepository<Orders, String> {
}


