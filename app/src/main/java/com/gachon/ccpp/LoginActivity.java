package com.gachon.ccpp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gachon.ccpp.network.RetrofitAPI;
import com.gachon.ccpp.network.RetrofitClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    RetrofitClient retrofitClient;
    RetrofitAPI api;
    EditText username,password;
    CheckBox autoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        retrofitClient = RetrofitClient.getInstance();
        api = RetrofitClient.getRetrofitInterface();

        username = findViewById(R.id.input_id);
        password = findViewById(R.id.input_password);
        autoLogin = findViewById(R.id.auto_login);

        if (PreferenceManager.getDefaultSharedPreferences(this).getString("username",null)!=null){
            tryGetCookie(PreferenceManager.getDefaultSharedPreferences(this).getString("username",null),
                    PreferenceManager.getDefaultSharedPreferences(this).getString("password",null));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isLoginSucceed();
        }

        Button login_button =findViewById(R.id.login);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryGetCookie(username.getText().toString(),password.getText().toString());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isLoginSucceed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(autoLogin.isChecked())saveData(username,password);
    }

    public void tryGetCookie(String username, String password) {
        Call<ResponseBody> login = api.login(username,password);
        login.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "onResponse 실패", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "onFailure " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void isLoginSucceed(){
        Call<ResponseBody> connect = api.getUri("");
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try{
                        Document html = Jsoup.parse(response.body().string());
                        Elements htmlLogin = html.select(".html_login");
                        if(htmlLogin.size() == 0) {
                            Toast.makeText(LoginActivity.this, "안녕하세요 " + html.select(".user_department.hidden-xs").text()+"님", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            Elements elements=html.select(".course-title h3");
                            String str = "";
                            for(Element e : elements){
                                str += e.text()+"\n";
                            }
                            intent.putExtra("html",str);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 다시 확인해 주세요",Toast.LENGTH_LONG).show();
                        }
                    }catch (IOException e){
                        Toast.makeText(LoginActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "onResponse 실패",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this,"onFailure "+t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void saveData(EditText username, EditText password){
        SharedPreferences.Editor autoLoginData = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
        autoLoginData.putString("username", username.getText().toString()).apply();
        autoLoginData.putString("password", password.getText().toString()).apply();
        autoLoginData.commit();
    }
}