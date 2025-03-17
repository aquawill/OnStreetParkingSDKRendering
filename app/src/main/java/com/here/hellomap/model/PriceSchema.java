package com.here.hellomap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceSchema {
    private String currencyCode;
    private List<Price> prices;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public List<Price> getPrices() {
        return prices;
    }
}