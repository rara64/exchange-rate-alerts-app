package com.example.exchangeratealerts;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.exchangeratealerts.dtos.JWT;
import com.example.exchangeratealerts.dtos.Login;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        TextView textview = findViewById(R.id.textView);

        apiInterface = APIClient.getRetrofitClient().create(APIInterface.class);

        Login login = new Login();
        login.username = "testuser";
        login.password = "TestPass123";
        Call<JWT> loginUser = apiInterface.login(login);
        loginUser.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("TAG",response.code()+"");
            }

            @Override
            public void onFailure(Call call, Throwable throwable) {

            }
        });
    }
}