package com.example.uap;

import com.google.gson.annotations.SerializedName;

public class Tanaman {
    @SerializedName("id")
    private String id; // Tambahkan ID jika API menggunakan ID

    @SerializedName("nama")
    private String nama;

    @SerializedName("harga")
    private String harga;

    @SerializedName("deskripsi")
    private String deskripsi;

    @SerializedName("gambar_res_id")
    private int gambarResId;

    @SerializedName("image_url")
    private String imageUrl; // Jika API menggunakan URL gambar

    // Default constructor (diperlukan untuk Gson)
    public Tanaman() {}

    public Tanaman(String nama, String harga, String deskripsi, int gambarResId) {
        this.nama = nama;
        this.harga = harga;
        this.deskripsi = deskripsi;
        this.gambarResId = gambarResId;
    }

    // Constructor dengan ID
    public Tanaman(String id, String nama, String harga, String deskripsi, int gambarResId) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.deskripsi = deskripsi;
        this.gambarResId = gambarResId;
    }

    // Getters
    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getHarga() { return harga; }
    public String getDeskripsi() { return deskripsi; }
    public int getGambarResId() { return gambarResId; }
    public String getImageUrl() { return imageUrl; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setHarga(String harga) { this.harga = harga; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public void setGambarResId(int gambarResId) { this.gambarResId = gambarResId; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Override
    public String toString() {
        return "Tanaman{" +
                "id='" + id + '\'' +
                ", nama='" + nama + '\'' +
                ", harga='" + harga + '\'' +
                ", deskripsi='" + deskripsi + '\'' +
                ", gambarResId=" + gambarResId +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}