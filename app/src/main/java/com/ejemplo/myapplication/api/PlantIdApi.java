package com.ejemplo.myapplication.api;

import com.ejemplo.myapplication.models.PlantIdentificationRequest;
import com.ejemplo.myapplication.models.PlantIdentificationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PlantIdApi {
    @Headers("Content-Type: application/json")
    @POST("v2/identify")
    Call<PlantIdentificationResponse> identifyPlant(@Body PlantIdentificationRequest request);
}