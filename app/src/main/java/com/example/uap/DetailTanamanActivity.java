package com.example.uap;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailTanamanActivity extends AppCompatActivity {
    ImageView imgTanaman;
    TextView txtNama, txtHarga, txtDeskripsi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tanaman);

        imgTanaman = findViewById(R.id.imgTanaman);
        txtNama = findViewById(R.id.txtNama);
        txtHarga = findViewById(R.id.txtHarga);
        txtDeskripsi = findViewById(R.id.txtDeskripsi);

        // Get data from intent
        String nama = getIntent().getStringExtra("nama");
        String harga = getIntent().getStringExtra("harga");
        String deskripsi = getIntent().getStringExtra("deskripsi");
        int gambarResId = getIntent().getIntExtra("gambarResId", R.drawable.tanaman_dua);

        // Set data to views
        txtNama.setText(nama);
        txtHarga.setText(harga);
        txtDeskripsi.setText(deskripsi);
        imgTanaman.setImageResource(gambarResId);
    }
}