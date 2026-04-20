package com.example.exchangeratealerts.dtos;

import com.google.gson.annotations.SerializedName;

public class CurrencyTarget {
    @SerializedName("base_currency")
    public String baseCurrency;

    @SerializedName("quote_currency")
    public String quoteCurrency;

    @SerializedName("target_value")
    public String targetValue;
}
