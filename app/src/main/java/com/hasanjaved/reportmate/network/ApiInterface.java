package com.hasanjaved.reportmate.network;

import com.hasanjaved.reportmate.model.LoginResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.POST;
import retrofit2.http.FormUrlEncoded;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("ocr/login")
    Call<LoginResponse> login(@FieldMap Map<String, String> fields);
}
