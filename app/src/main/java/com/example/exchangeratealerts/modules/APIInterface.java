package com.example.exchangeratealerts.modules;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;

import com.example.exchangeratealerts.models.APIMessage;
import com.example.exchangeratealerts.models.Login;
import com.example.exchangeratealerts.models.CurrencyTarget;
import com.example.exchangeratealerts.models.JWT;
import com.example.exchangeratealerts.models.CurrencyAlert;

public interface APIInterface {
    @POST("/login")
    Call<JWT> login(@Body Login login);

    @POST("/register")
    Call<APIMessage> register(@Body Login login);

    @POST("/targets")
    Call<CurrencyTarget> setTarget(@Header("Authorization") String token, @Body CurrencyTarget target);

    @GET("/targets")
    Call<CurrencyTarget[]> getCurrentTargets(@Header("Authorization") String token);

    @GET("/alerts")
    Call<CurrencyAlert[]> getAlerts(@Header("Authorization") String token);

    @HTTP(method = "DELETE", path = "/targets", hasBody = true)
    Call<CurrencyTarget> deleteTarget(@Header("Authorization") String token, @Body CurrencyTarget target);
}
