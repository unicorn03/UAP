package com.example.uap;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uap.models.Plant;
import com.example.uap.models.PlantSingleResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateTanamanActivity extends AppCompatActivity {
    EditText etNama, etHarga, etDeskripsi;
    Button btnUpdate;
    ImageView imgTanaman;
    ProgressBar progressBar;
    ApiService apiService;
    String originalNama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_tanaman);
        apiService = ApiClient.getClient().create(ApiService.class);

        etNama = findViewById(R.id.etNama);
        etHarga = findViewById(R.id.etHarga);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        btnUpdate = findViewById(R.id.btnUpdate);
        imgTanaman = findViewById(R.id.imgTanaman);

        originalNama = getIntent().getStringExtra("nama");
        String harga = getIntent().getStringExtra("harga");
        String deskripsi = getIntent().getStringExtra("deskripsi");
        int gambarResId = getIntent().getIntExtra("gambarResId", R.drawable.tanaman_dua);

        etNama.setText(originalNama);
        etHarga.setText(harga);
        etDeskripsi.setText(deskripsi);
        imgTanaman.setImageResource(gambarResId);

        btnUpdate.setOnClickListener(v -> {
            if (validateInput()) {
                updateTanaman();
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

    private void updateTanaman() {
        String nama = etNama.getText().toString().trim();
        String harga = etHarga.getText().toString().trim();
        String deskripsi = etDeskripsi.getText().toString().trim();

        Plant updatedPlant = new Plant(nama, deskripsi, harga);

        showLoading(true);
        btnUpdate.setEnabled(false);

        Call<PlantSingleResponse> call = apiService.updatePlant(originalNama, updatedPlant);
        call.enqueue(new Callback<PlantSingleResponse>() {
            @Override
            public void onResponse(Call<PlantSingleResponse> call, Response<PlantSingleResponse> response) {
                showLoading(false);
                btnUpdate.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(UpdateTanamanActivity.this,
                            "Data berhasil diupdate", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(UpdateTanamanActivity.this,
                            "Gagal mengupdate data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlantSingleResponse> call, Throwable t) {
                showLoading(false);
                btnUpdate.setEnabled(true);
                Toast.makeText(UpdateTanamanActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (show) {
            btnUpdate.setText("Memperbarui...");
        } else {
            btnUpdate.setText("Update");
        }
    }
}