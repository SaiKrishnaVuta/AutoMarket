package com.market.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "ssbt_oders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orders {
    @Id
    @Column(name = "symbol", nullable = false)
    private String symbol;
    @Column(name = "lots", nullable = false)
    private int lots;
    @Column(name = "datetime", nullable = false)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale = "hi-IN", timezone = "Asia/Kolkata")
    private Date datetime;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "buyprice")
    private Double buyprice;
    @Column(name = "sellprice")
    private Double sellprice;
    @Column(name = "currentprice")
    private Double currentprice;
}
