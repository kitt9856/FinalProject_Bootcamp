package com.crumbcookie.crumbcookieresponse.model;

import java.util.List;

import com.crumbcookie.crumbcookieresponse.dto.YahooStockDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuoteResponse {
    //for golbal exception handle, but not use this verison
    private List<YahooStockDTO> result;
    private Object error;

    
    public List<YahooStockDTO> getResult() {
        return result;
    }

    public void setResult(List<YahooStockDTO> result) {
        this.result = result;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }
    
}
