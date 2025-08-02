package com.crumbcookie.crumbcookieresponse.Entity;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PriceHistoryList")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockPriceOHEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  //private String symbol;
  private Double marketPrice;
  private LocalDate tradeDate;
  private Double PerMarketChangePercent;
  private Double open;
  private Double high;
  private Double low;
  private Double close;
  private Long tradtimestamp;
  
  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "stocksEntity_id")
  @Setter
  private StocksEntity stocksEntity;

}
