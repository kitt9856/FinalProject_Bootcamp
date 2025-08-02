package com.springfront.bc_xfin_web.lib;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

@Component
public  class Mathoperation {

    public static double roundPrice(double value) {
        BigDecimal bigDecimalprice = BigDecimal.valueOf(value).setScale(3, RoundingMode.HALF_UP);
        return bigDecimalprice.doubleValue();
    }
    
}
