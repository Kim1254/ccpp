package com.gachon.ccpp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gachon.ccpp.network.RetrofitAPI;
import com.gachon.ccpp.network.RetrofitClient;
import com.gachon.ccpp.parser.HtmlParser;

import org.json.JSONException;
import org.json.JSONObject;
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

    TextView description;
    LoadingDialog privateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle(R.string.LoginActivity_Title);

        retrofitClient = RetrofitClient.getInstance();
        api = RetrofitClient.getRetrofitInterface();

        username = findViewById(R.id.input_id);
        password = findViewById(R.id.input_password);
        autoLogin = findViewById(R.id.auto_login);

        description = findViewById(R.id.login_desc);
        privateDialog = new LoadingDialog(this);

        if (PreferenceManager.getDefaultSharedPreferences(this).getString("username",null)!=null) {
            tryGetCookie(PreferenceManager.getDefaultSharedPreferences(this).getString("username",null),
                    PreferenceManager.getDefaultSharedPreferences(this).getString("password",null));
        }

        Button login_button = findViewById(R.id.login);
        login_button.setOnClickListener(view -> {
            tryGetCookie(username.getText().toString(), password.getText().toString());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(autoLogin.isChecked())
            saveData(username, password);
    }

    public void tryGetCookie(String username, String password) {
        privateDialog.show("81217-locker.json", getString(R.string.LoadingDialog_TextLogin));

        Call<ResponseBody> login = api.login(username, password);
        login.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful())
                    checkLogin();
                else
                    Toast.makeText(LoginActivity.this,
                            R.string.LoginActivity_LoginNoResponse, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                String text = getString(R.string.LoginActivity_LoginOnFailure, t.getMessage());
                Toast.makeText(LoginActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void checkLogin() {
        Call<ResponseBody> connect = api.getUri("");
        connect.enqueue(new Callback<ResponseBody>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Document html = Jsoup.parse(response.body().string());
                        Elements htmlLogin = html.select(".html_login");
                        HtmlParser parser = new HtmlParser(html);
                        if (htmlLogin.size() == 0) {
                            String text = getString(R.string.LoginActivity_LoginSuccess,
                                    html.select(".user_department.hidden-xs").text());
                            Toast.makeText(LoginActivity.this, text, Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                            privateDialog.hide();

                            intent.putExtra("id", username.getText().toString());
                            intent.putExtra("courseList",parser.getCourseList());
                            startActivity(intent);
                            finish();
                        } else {
                            privateDialog.hide();

                            description.setText(R.string.LoginActivity_LoginIncorrect);
                            description.setTextColor(Color.RED);
                            /*Toast.makeText(LoginActivity.this,
                                    R.string.LoginActivity_LoginIncorrect,Toast.LENGTH_LONG).show();*/
                        }
                    } catch (IOException e) {
                        privateDialog.hide();
                        String text = getString(R.string.LoginActivity_LoginParseError, e.getMessage());
                        description.setText(text);
                        description.setTextColor(Color.RED);
                        //Toast.makeText(LoginActivity.this, text, Toast.LENGTH_LONG).show();
                    }
                } else {
                    privateDialog.hide();
                    description.setText(R.string.LoginActivity_LoginNoResponse);
                    description.setTextColor(Color.RED);
                    /*Toast.makeText(LoginActivity.this,
                            R.string.LoginActivity_LoginNoResponse, Toast.LENGTH_LONG).show();*/
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                privateDialog.hide();
                String text = getString(R.string.LoginActivity_LoginOnFailure, t.getMessage());
                description.setText(text);
                description.setTextColor(Color.RED);
                //Toast.makeText(LoginActivity.this, text, Toast.LENGTH_LONG).show();
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