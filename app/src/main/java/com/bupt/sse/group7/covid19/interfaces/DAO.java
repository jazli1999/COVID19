package com.bupt.sse.group7.covid19.interfaces;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface DAO {
    @GET("{php}")
    Call<ResponseBody> executeGet(@Path("php") String php, @QueryMap Map<String, String> queryMap);

    @GET("{php}")
    Call<ResponseBody> executeGet(@Path("php") String php);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("{php}")
    Call<String> executePost(@Path("php") String php, @Body RequestBody data);

}
