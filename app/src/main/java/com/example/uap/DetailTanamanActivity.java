package com.example.uap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class DetailTanamanActivity extends AppCompatActivity {
    ImageView imgTanaman;
    TextView txtNama, txtHarga, txtDeskripsi;
    Button btnUpdate;

    private ActivityResultLauncher<Intent> updateTanamanLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tanaman);

        // Setup activity launcher for update result
        setupActivityLauncher();

        imgTanaman = findViewById(R.id.imgTanaman);
        txtNama = findViewById(R.id.txtNama);
        txtHarga = findViewById(R.id.txtHarga);
        txtDeskripsi = findViewById(R.id.txtDeskripsi);
        btnUpdate = findViewById(R.id.btnUpdate);

        String nama = getIntent().getStringExtra("nama");
        String harga = getIntent().getStringExtra("harga");
        String deskripsi = getIntent().getStringExtra("deskripsi");
        int gambarResId = getIntent().getIntExtra("gambarResId", R.drawable.tanaman_dua);

        txtNama.setText(nama);
        txtHarga.setText(harga);
        txtDeskripsi.setText(deskripsi);
        imgTanaman.setImageResource(gambarResId);

        // Set click listener for update button
        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(DetailTanamanActivity.this, UpdateTanamanActivity.class);
            intent.putExtra("nama", nama);
            intent.putExtra("harga", harga);
            intent.putExtra("deskripsi", deskripsi);
            intent.putExtra("gambarResId", gambarResId);
            updateTanamanLauncher.launch(intent);
        });
    }

    private void setupActivityLauncher() {
        updateTanamanLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Data berhasil diupdate, tutup activity detail dan kembali ke dashboard
                        setResult(RESULT_OK);
                        finish();
                    }
                }
        );
    }
}