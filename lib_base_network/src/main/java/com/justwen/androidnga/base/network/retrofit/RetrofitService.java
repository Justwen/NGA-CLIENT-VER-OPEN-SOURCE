package com.justwen.androidnga.base.network.retrofit;

import java.util.Map;

import gov.anzong.androidnga.common.base.JavaBean;
import io.reactivex.Observable;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by Justwen on 2017/10/10.
 */

public interface RetrofitService {

    @GET("nuke.php")
    Observable<String> get(@QueryMap Map<String, String> map);

    @GET("forum.php")
    Observable<String> getByForum(@QueryMap Map<String, String> map);

    @GET
    Observable<String> get(@Url String url);

    @POST
    Observable<String> post(@Url String url);

    @FormUrlEncoded
    @POST("nuke.php")
    Observable<String> post(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("nuke.php")
    Observable<String> post(@QueryMap Map<String, String> queryMap, @FieldMap Map<String, String> fieldMap);

    @FormUrlEncoded
    @POST("nuke.php")
    Observable<String> login(@FieldMap Map<String, String> map);

    @GET
    @Headers({"Referer:https://bbs.ngacn.cc/nuke.php?__lib=login&__act=login_ui",})
    Observable<ResponseBody> getAuthCodeImage(@Url String url);

    @POST("nuke.php?__lib=login&__act=login&raw=3")
    @Headers({"Referer:https://bbs.ngacn.cc/nuke.php?__lib=login&__act=login_ui",
            "Content-Type:application/x-www-form-urlencoded",
            "Origin: https://bbs.ngacn.cc",
            "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
            "Upgrade-Insecure-Requests:1"})
    Observable<String> login(@Body FormBody body);

    @POST("nuke.php")
    @Headers({"Referer:https://bbs.ngacn.cc/nuke/p2.htm?login",
            "Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryklQov1cm1BhZqEM4",})
    Observable<String> login(@Body MultipartBody multipartBody);


    @POST
    Observable<ResponseBody> uploadFile(@Url String url, @Body MultipartBody body);

    @GET
    Observable<String> get(@Url String url, @HeaderMap Map<String,String> map);

}
