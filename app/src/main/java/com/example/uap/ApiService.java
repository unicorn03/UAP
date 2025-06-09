package com.example.uap;

import com.example.uap.models.Tanaman;
import com.example.uap.models.TanamanRequest;
import com.example.uap.models.TanamanResponse;
import com.example.uap.models.TanamanSingleResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("plant/all")
    Call<TanamanResponse> getAllPlants();
    @POST("plant/new")
    Call<Void> createPlant(@Body TanamanRequest plant);
    @PUT("plant/{name}")
    Call<TanamanSingleResponse> updatePlant(@Path("name") String plantName, @Body Tanaman updatedPlant);
    @GET("plant/{name}")
    Call<TanamanSingleResponse> getPlantByName(@Path("name") String plantName);
    @DELETE("plant/{name}")
    Call<TanamanSingleResponse> deletePlant(@Path("name") String name);

}