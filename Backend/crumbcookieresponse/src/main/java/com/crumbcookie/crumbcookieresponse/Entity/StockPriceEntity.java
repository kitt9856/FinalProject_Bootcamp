package com.crumbcookie.crumbcookieresponse.Entity;

import java.time.DayOfWeek;
import java.time.LocalDate;
//import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PriceList")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockPriceEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  //private String symbol;
  private Double marketPrice;
  private LocalDate tradeDate;
  private Double regularMarketChangePercent;
  private Double open;
  private Double high;
  private Double low;
  private Double bid;
  private Double ask;

  private String tradeWeek;
  
  private LocalTime priceUpdatetime;
  
  private LocalTime nowTime;

  @PrePersist
  @PreUpdate
  public void setNowTime() {
      this.nowTime = LocalTime.now().truncatedTo(ChronoUnit.SECONDS); 
  }


  @OneToOne
  @JoinColumn(name = "symbol")
  @Setter
  private StocksEntity stocksEntity;

  
}
