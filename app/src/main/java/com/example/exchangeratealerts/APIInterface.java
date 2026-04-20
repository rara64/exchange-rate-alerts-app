package com.example.exchangeratealerts;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import com.example.exchangeratealerts.dtos.Login;
import com.example.exchangeratealerts.dtos.CurrencyTarget;
import com.example.exchangeratealerts.dtos.JWT;
import com.example.exchangeratealerts.dtos.CurrencyAlert;

public interface APIInterface {
    @POST("/login")
    Call<JWT> login(@Body Login login);

    @POST("/register")
    Call<String> register(@Body Login login);

    @POST("/targets")
    Call<CurrencyTarget> setTarget(@Body CurrencyTarget target);

    @GET("/targets")
    Call<CurrencyTarget[]> getCurrentTargets();

    @GET("/alerts")
    Call<CurrencyAlert[]> getAlerts();
}
