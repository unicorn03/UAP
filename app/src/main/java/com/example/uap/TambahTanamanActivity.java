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

public class TambahTanamanActivity extends AppCompatActivity {
    EditText etNama, etHarga, etDeskripsi;
    Button btnSimpan;
    ImageView imgTanaman;
    ProgressBar progressBar;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_tanaman);

        // Initialize API service
        apiService = ApiClient.getApiService();

        // Initialize views
        etNama = findViewById(R.id.etNama);
        etHarga = findViewById(R.id.etHarga);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        btnSimpan = findViewById(R.id.btnSimpan);
        imgTanaman = findViewById(R.id.imgTanaman);
        progressBar = findViewById(R.id.progressBar); // Tambahkan di layout

        // Set default image
        imgTanaman.setImageResource(R.drawable.tanaman_dua);

        btnSimpan.setOnClickListener(v -> {
            if (validateInput()) {
                saveTanaman();
            }
        });
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

    private void saveTanaman() {
        String nama = etNama.getText().toString().trim();
        String harga = etHarga.getText().toString().trim();
        String deskripsi = etDeskripsi.getText().toString().trim();

        Tanaman tanaman = new Tanaman(nama, harga, deskripsi, R.drawable.tanaman_dua);

        showLoading(true);
        btnSimpan.setEnabled(false);

        Call<Tanaman> call = apiService.createTanaman(tanaman);
        call.enqueue(new Callback<Tanaman>() {
            @Override
            public void onResponse(Call<Tanaman> call, Response<Tanaman> response) {
                showLoading(false);
                btnSimpan.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(TambahTanamanActivity.this,
                            "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                    finish(); // Kembali ke DashboardActivity
                } else {
                    Toast.makeText(TambahTanamanActivity.this,
                            "Gagal menyimpan data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tanaman> call, Throwable t) {
                showLoading(false);
                btnSimpan.setEnabled(true);
                Toast.makeText(TambahTanamanActivity.this,
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