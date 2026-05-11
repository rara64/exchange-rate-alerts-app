package com.example.exchangeratealerts.models;

import com.google.gson.annotations.SerializedName;

public class Login {
    @SerializedName("username")
    public String username;

    @SerializedName("password")
    public String password;
}
