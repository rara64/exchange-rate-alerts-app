package com.example.exchangeratealerts.models;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class CurrencyAlert {
    @SerializedName("base_currency")
    public String baseCurrency;

    @SerializedName("quote_currency")
    public String quoteCurrency;

    @SerializedName("target_value")
    public String targetValue;

    @SerializedName("current_value")
    public String currentValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != CurrencyAlert.class) return false;

        CurrencyAlert c = (CurrencyAlert) o;

        return Objects.equals(baseCurrency, c.baseCurrency) &&
                Objects.equals(quoteCurrency, c.quoteCurrency) &&
                Objects.equals(targetValue, c.targetValue) &&
                Objects.equals(currentValue, c.currentValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseCurrency, quoteCurrency, targetValue, currentValue);
    }
}
