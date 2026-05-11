package com.example.exchangeratealerts.models;

import com.google.gson.annotations.SerializedName;

public class CurrencyAlert {
    @SerializedName("base_currency")
    public String baseCurrency;

    @SerializedName("quote_currency")
    public String quoteCurrency;

    @SerializedName("target_value")
    public String targetValue;

    @SerializedName("current_value")
    public String currentValue;
}
