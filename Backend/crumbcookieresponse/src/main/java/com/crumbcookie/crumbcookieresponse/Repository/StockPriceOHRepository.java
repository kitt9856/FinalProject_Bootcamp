package com.crumbcookie.crumbcookieresponse.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.crumbcookie.crumbcookieresponse.Entity.StockPriceOHEntity;

@Repository
public interface StockPriceOHRepository extends JpaRepository<StockPriceOHEntity, Long> {

  
}
