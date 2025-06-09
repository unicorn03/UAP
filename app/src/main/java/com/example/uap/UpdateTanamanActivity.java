package com.example.uap;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateTanamanActivity extends AppCompatActivity {
    EditText etNama, etHarga, etDeskripsi;
    Button btnUpdate;
    ImageView imgTanaman;
    ProgressBar progressBar;
    ApiService apiService;
    String namaLama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_tanaman);

        // Initialize API service
        apiService = ApiClient.getApiService();

        // Initialize views
        etNama = findViewById(R.id.etNama);
        etHarga = findViewById(R.id.etHarga);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        btnUpdate = findViewById(R.id.btnUpdate);
        imgTanaman = findViewById(R.id.imgTanaman);
        progressBar = findViewById(R.id.progressBar); // Tambahkan di layout

        // Get data from intent
        loadDataFromIntent();

        btnUpdate.setOnClickListener(v -> {
            if (validateInput()) {
                updateTanaman();
            }
        });
    }

    private void loadDataFromIntent() {
        namaLama = getIntent().getStringExtra("nama");
        String harga = getIntent().getStringExtra("harga");
        String deskripsi = getIntent().getStringExtra("deskripsi");
        int gambarResId = getIntent().getIntExtra("gambarResId", R.drawable.logo_tree);

        // Set data to views
        etNama.setText(namaLama);
        etHarga.setText(harga);
        etDeskripsi.setText(deskripsi);
        imgTanaman.setImageResource(gambarResId);
    }

    private boolean validateInput() {
        String nama = etNama.getText().toString().trim();
        String harga = etHarga.getText().toString().trim();
        String deskripsi = etDeskripsi.getText().toString().trim();

        if (nama.isEmpty()) {
            etNama.setError("Nama tanaman wajib diisi");
            etNama.requestFocus();
            return false;
        }

        if (harga.isEmpty()) {
            etHarga.setError("Harga wajib diisi");
            etHarga.requestFocus();
            return false;
        }

        if (deskripsi.isEmpty()) {
            etDeskripsi.setError("Deskripsi wajib diisi");
            etDeskripsi.requestFocus();
            return false;
        }

        return true;
    }

    private void updateTanaman() {
        String namaBaru = etNama.getText().toString().trim();
        String hargaBaru = etHarga.getText().toString().trim();
        String deskripsiBaru = etDeskripsi.getText().toString().trim();

        Tanaman tanamanBaru = new Tanaman(namaBaru, hargaBaru, deskripsiBaru, R.drawable.tanaman_dua);

        showLoading(true);
        btnUpdate.setEnabled(false);

        // Gunakan nama lama sebagai identifier untuk update
        Call<Tanaman> call = apiService.updateTanaman(namaLama, tanamanBaru);
        call.enqueue(new Callback<Tanaman>() {
            @Override
            public void onResponse(Call<Tanaman> call, Response<Tanaman> response) {
                showLoading(false);
                btnUpdate.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(UpdateTanamanActivity.this,
                            "Data berhasil diupdate", Toast.LENGTH_SHORT).show();
                    finish(); // Kembali ke DashboardActivity
                } else {
                    Toast.makeText(UpdateTanamanActivity.this,
                            "Gagal mengupdate data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tanaman> call, Throwable t) {
                showLoading(false);
                btnUpdate.setEnabled(true);
                Toast.makeText(UpdateTanamanActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}