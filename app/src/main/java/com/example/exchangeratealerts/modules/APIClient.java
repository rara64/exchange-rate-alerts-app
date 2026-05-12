package com.example.exchangeratealerts.modules;

import com.example.exchangeratealerts.BuildConfig;
import com.example.exchangeratealerts.models.APIMessage;
import com.example.exchangeratealerts.models.CurrencyAlert;
import com.example.exchangeratealerts.models.CurrencyTarget;
import com.example.exchangeratealerts.models.JWT;
import com.example.exchangeratealerts.models.Login;

import org.json.JSONException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static APIInterface api;

    public APIClient() {
        var httpClientBuilder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(httpLoggingInterceptor);
        }

        OkHttpClient httpClient = httpClientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        api = retrofit.create(APIInterface.class);
    }

    public interface ResponseCallback<T> {
        void onSuccess(T body);
        void onFailure(int httpCode);
    }

    private void handleResponse(Response response, ResponseCallback callback) {
        if (response.isSuccessful() && response.body() != null) {
            callback.onSuccess(response.body());
        }
        else {
            callback.onFailure(response.code());
        }
    }

    public void Login(String username, String password, ResponseCallback<JWT> callback) {
        Login login = new Login();
        login.username = username;
        login.password = password;

        Call<JWT> loginCall = api.login(login);
        loginCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JWT> call, Response<JWT> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call call, Throwable throwable) {
                callback.onFailure(-1);
            }
        });
    }

    public void Register(String username, String password, ResponseCallback<APIMessage> callback) {
        Login login = new Login();
        login.username = username;
        login.password = password;

        Call<APIMessage> registerCall = api.register(login);
        registerCall.enqueue(new Callback<APIMessage>() {
            @Override
            public void onResponse(Call<APIMessage> call, Response<APIMessage> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call call, Throwable throwable) {
                callback.onFailure(-1);
            }
        });
    }

    public void GetCurrencyTargets(String token, ResponseCallback<CurrencyTarget[]> callback) {
        Call<CurrencyTarget[]> targetsCall = api.getCurrentTargets("Bearer " + token);
        targetsCall.enqueue(new Callback<CurrencyTarget[]>() {
            @Override
            public void onResponse(Call<CurrencyTarget[]> call, Response<CurrencyTarget[]> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<CurrencyTarget[]> call, Throwable throwable) {
                callback.onFailure(-1);
            }
        });
    }

    public void GetCurrencyAlerts(String token, ResponseCallback<CurrencyAlert[]> callback) {
        Call<CurrencyAlert[]> alertsCall = api.getAlerts("Bearer " + token);
        alertsCall.enqueue(new Callback<CurrencyAlert[]>() {
            @Override
            public void onResponse(Call<CurrencyAlert[]> call, Response<CurrencyAlert[]> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<CurrencyAlert[]> call, Throwable throwable) {
                callback.onFailure(-1);
            }
        });
    }

    public void SetCurrencyTarget(String token, CurrencyTarget target, ResponseCallback<CurrencyTarget> callback) {
        Call<CurrencyTarget> targetCall = api.setTarget("Bearer " + token, target);
        targetCall.enqueue(new Callback<CurrencyTarget>() {
            @Override
            public void onResponse(Call<CurrencyTarget> call, Response<CurrencyTarget> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<CurrencyTarget> call, Throwable throwable) {
                callback.onFailure(-1);
            }
        });
    }

    public void DeleteCurrencyTarget(String token, CurrencyTarget target, ResponseCallback<CurrencyTarget> callback) {
        Call<CurrencyTarget> deleteCall = api.deleteTarget("Bearer " + token, target);
        deleteCall.enqueue(new Callback<CurrencyTarget>() {
            @Override
            public void onResponse(Call<CurrencyTarget> call, Response<CurrencyTarget> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<CurrencyTarget> call, Throwable throwable) {
                callback.onFailure(-1);
            }
        });
    }

}
