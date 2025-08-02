package com.crumbcookie.crumbcookieresponse.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crumbcookie.crumbcookieresponse.Entity.StocksEntity;

@Repository
public interface StocksRepository extends JpaRepository<StocksEntity, Long> {
  
    Optional<StocksEntity> findBySymbol(String symbol);

    /* @Query(value= "Select * From stock_list where symbol Like %:keyword% OR longName LIKE %:keyword%", 
        nativeQuery= true)   */
    //@Query("SELECT s FROM StocksEntity s WHERE s.symbol LIKE LOWER(CONCAT('%', :keyword, '%')) OR s.longName LIKE LOWER(CONCAT('%', :keyword, '%'))")
    @Query("SELECT s FROM StocksEntity s WHERE LOWER(s.symbol) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.longName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<StocksEntity> findByKeyword(@Param("keyword") String keyword);
}
