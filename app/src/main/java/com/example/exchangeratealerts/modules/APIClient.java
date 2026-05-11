package com.example.exchangeratealerts.modules;

import com.example.exchangeratealerts.models.APIMessage;
import com.example.exchangeratealerts.models.JWT;
import com.example.exchangeratealerts.models.Login;

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
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://rararuf16.pythonanywhere.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        api = retrofit.create(APIInterface.class);
    }

    public interface LoginCallback {
        void onSuccess(JWT token);
        void onFailure(int httpCode);
    }
    public void Login(String username, String password, LoginCallback callback) {
        Login login = new Login();
        login.username = username;
        login.password = password;

        Call<JWT> loginCall = api.login(login);
        loginCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JWT> call, Response<JWT> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                }
                else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call call, Throwable throwable) {
                callback.onFailure(-1);
            }
        });
    }

    public interface RegisterCallback {
        void onSuccess();
        void onFailure(int httpCode);
    }

    public void Register(String username, String password, RegisterCallback callback) {
        Login login = new Login();
        login.username = username;
        login.password = password;

        Call<APIMessage> registerCall = api.register(login);
        registerCall.enqueue(new Callback<APIMessage>() {
            @Override
            public void onResponse(Call<APIMessage> call, Response<APIMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess();
                }
                else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call call, Throwable throwable) {
                callback.onFailure(-1);
            }
        });
    }

}
