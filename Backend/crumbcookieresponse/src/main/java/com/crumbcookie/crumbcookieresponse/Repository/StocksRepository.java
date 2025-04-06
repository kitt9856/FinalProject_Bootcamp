package com.crumbcookie.crumbcookieresponse.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.crumbcookie.crumbcookieresponse.Entity.StocksEntity;

@Repository
public interface StocksRepository extends JpaRepository<StocksEntity, Long> {
  
}
