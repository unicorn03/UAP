package com.example.uap;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // Base URL - sesuaikan dengan API Anda
    String BASE_URL = "https://uappam.kuncipintu.my.id/";

    // GET - Ambil semua tanaman
    @GET("tanaman")
    Call<List<Tanaman>> getAllTanaman();

    // GET - Ambil tanaman berdasarkan ID/nama
    @GET("tanaman/{id}")
    Call<Tanaman> getTanamanById(@Path("id") String id);

    // POST - Tambah tanaman baru
    @POST("tanaman")
    Call<Tanaman> createTanaman(@Body Tanaman tanaman);

    // PUT - Update tanaman
    @PUT("tanaman/{id}")
    Call<Tanaman> updateTanaman(@Path("id") String id, @Body Tanaman tanaman);

    // DELETE - Hapus tanaman
    @DELETE("tanaman/{id}")
    Call<Void> deleteTanaman(@Path("id") String id);
}