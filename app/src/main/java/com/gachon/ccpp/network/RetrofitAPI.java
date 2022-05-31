package com.gachon.ccpp.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RetrofitAPI {

    @Headers("Accept-Language: ko-KR,ko;q=0.9")
    @FormUrlEncoded
    @POST("/login.php")
    Call<ResponseBody> login (
            @Field("username") String username,
            @Field("password") String password
    );

    @Headers("Accept-Language: ko-KR,ko;q=0.9")
    @GET("/local/ubion/setting/syllabus.php")
    Call<ResponseBody> syllabus (
            @Query("id") String id
    );

    @Headers("Accept-Language: ko-KR,ko;q=0.9")
    @GET("/grade/report/user/index.php")
    Call<ResponseBody> grade (
            @Query("id") String id
    );

    @Headers("Accept-Language: ko-KR,ko;q=0.9")
    @GET("/mod/assign/index.php")
    Call<ResponseBody> assignment (
            @Query("id") String id
    );

    @Headers("Accept-Language: ko-KR,ko;q=0.9")
    @FormUrlEncoded
    @POST("/local/ubmessage/action.php")
    Call<ResponseBody> send_message (
            @Field("type") String type,
            @Field("sesskey") String sesskey,
            @Field("to") String to,
            @Field("returnurl") String returnurl,
            @Field("message") String message
    );

    @Headers("Accept-Language: ko-KR,ko;q=0.9")
    @GET
    Call<ResponseBody> getUri(@Url String uri);
}
