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
    @GET("/user/user_edit.php")
    Call<ResponseBody> info (
            @Query("id") String id
    );

    @Headers("Accept-Language: ko-KR,ko;q=0.9")
    @GET("/course/view.php")
    Call<ResponseBody> course (
            @Query("id") String id
    );

    @Headers("Accept-Language: ko-KR,ko;q=0.9")
    @GET
    Call<ResponseBody> getUri(@Url String uri);
}
