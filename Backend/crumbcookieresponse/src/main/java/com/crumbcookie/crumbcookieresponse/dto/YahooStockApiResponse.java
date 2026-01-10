package com.crumbcookie.crumbcookieresponse.dto;

import com.crumbcookie.crumbcookieresponse.model.QuoteResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YahooStockApiResponse  {
    //已停用
    private QuoteResponse quoteResponse;

    public QuoteResponse getQuoteResponse() {
        return quoteResponse;
    }

    public void setQuoteResponse(QuoteResponse quoteResponse) {
        this.quoteResponse = quoteResponse;
    }
}


