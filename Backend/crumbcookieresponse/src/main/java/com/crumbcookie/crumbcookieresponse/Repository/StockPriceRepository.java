package com.crumbcookie.crumbcookieresponse.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.crumbcookie.crumbcookieresponse.Entity.StockPriceEntity;
import com.crumbcookie.crumbcookieresponse.Entity.StocksEntity;

@Repository
public interface StockPriceRepository extends JpaRepository<StockPriceEntity, Long> {
  
}
