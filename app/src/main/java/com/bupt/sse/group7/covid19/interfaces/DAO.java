package com.bupt.sse.group7.covid19.interfaces;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface DAO {
    @GET("{php}")
    Call<ResponseBody> getData(@Path("php") String php, @QueryMap Map<String,String> queryMap);
    @GET("{php}")
    Call<ResponseBody> getData(@Path("php") String php);

}
