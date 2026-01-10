package com.crumbcookie.crumbcookieresponse.model;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class   StockStore {
    //人手set 股票list
    private  List<String> targetSybmols = List.of("0388.HK", "2105.HK","0005.HK","BTC-USD","TSLA");


    

    
}
