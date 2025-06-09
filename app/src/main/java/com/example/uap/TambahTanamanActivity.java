package com.example.uap;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.uap.models.TanamanRequest;
import com.example.uap.models.TanamanSingleResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahTanamanActivity extends AppCompatActivity {
    EditText etNama, etHarga, etDeskripsi;
    Button btnTambah;
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
        btnTambah = findViewById(R.id.btnSimpan); // Fixed ID to match layout
        imgTanaman = findViewById(R.id.imgTanaman);
        progressBar = findViewById(R.id.progressBar);

        // Set default image
        imgTanaman.setImageResource(R.drawable.tanaman_dua);

        btnTambah.setOnClickListener(v -> {
            if (validateInput()) {
                tambahTanaman();
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

    private void tambahTanaman() {
        String nama = etNama.getText().toString().trim();
        String harga = etHarga.getText().toString().trim();
        String deskripsi = etDeskripsi.getText().toString().trim();

        // Create TanamanRequest object
        TanamanRequest newPlant = new TanamanRequest(nama, deskripsi, harga);

        showLoading(true);
        btnTambah.setEnabled(false);

        // Use Call<Void> to match ApiService definition
        Call<Void> call = apiService.createPlant(newPlant);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showLoading(false);
                btnTambah.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(TambahTanamanActivity.this,
                            "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Toast.makeText(TambahTanamanActivity.this,
                                "Gagal menambahkan data: " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(TambahTanamanActivity.this,
                                "Gagal menambahkan data: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showLoading(false);
                btnTambah.setEnabled(true);
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