package com.example.uap;

import com.example.uap.models.Plant;
import com.example.uap.models.PlantRequest;
import com.example.uap.models.PlantResponse;
import com.example.uap.models.PlantSingleResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("plant/all")
    Call<PlantResponse> getAllPlants();
    @POST("plant/new")
    Call<Void> createPlant(@Body PlantRequest plant);
    @PUT("plant/{name}")
    Call<PlantSingleResponse> updatePlant(@Path("name") String plantName, @Body Plant updatedPlant);
    @GET("plant/{name}")
    Call<PlantSingleResponse> getPlantByName(@Path("name") String plantName);
    @DELETE("plant/{name}")
    Call<PlantSingleResponse> deletePlant(@Path("name") String name);

}