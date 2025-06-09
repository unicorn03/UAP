package com.example.uap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class    DashboardActivity extends AppCompatActivity {
    RecyclerView recyclerViewTanaman;
    Button btnTambahList;
    SwipeRefreshLayout swipeRefresh;
    ProgressBar progressBar;
    ArrayList<Tanaman> dataList;
    TanamanAdapter adapter;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize API service
        apiService = ApiClient.getApiService();

        // Initialize views
        recyclerViewTanaman = findViewById(R.id.recyclerViewTanaman);
        btnTambahList = findViewById(R.id.btnTambahList);
        swipeRefresh = findViewById(R.id.swipeRefresh); // Tambahkan di layout
        progressBar = findViewById(R.id.progressBar); // Tambahkan di layout

        dataList = new ArrayList<>();

        adapter = new TanamanAdapter(this, dataList, new TanamanAdapter.OnItemClickListener() {
            @Override
            public void onHapus(int pos) {
                deleteTanaman(pos);
            }

            @Override
            public void onDetail(int pos) {
                Tanaman tanaman = dataList.get(pos);
                Intent intent = new Intent(DashboardActivity.this, DetailTanamanActivity.class);
                intent.putExtra("nama", tanaman.getNama());
                intent.putExtra("harga", tanaman.getHarga());
                intent.putExtra("deskripsi", tanaman.getDeskripsi());
                intent.putExtra("gambarResId", tanaman.getGambarResId());
                startActivity(intent);
            }

            @Override
            public void onUpdate(int pos) {
                Tanaman tanaman = dataList.get(pos);
                Intent intent = new Intent(DashboardActivity.this, UpdateTanamanActivity.class);
                intent.putExtra("nama", tanaman.getNama());
                intent.putExtra("harga", tanaman.getHarga());
                intent.putExtra("deskripsi", tanaman.getDeskripsi());
                intent.putExtra("gambarResId", tanaman.getGambarResId());
                startActivity(intent);
            }
        });

        recyclerViewTanaman.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTanaman.setAdapter(adapter);

        // Setup swipe to refresh
        swipeRefresh.setOnRefreshListener(this::loadTanamanData);

        btnTambahList.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, TambahTanamanActivity.class);
            startActivity(intent);
        });

        // Load initial data
        loadTanamanData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from other activities
        loadTanamanData();
    }

    private void loadTanamanData() {
        showLoading(true);

        Call<List<Tanaman>> call = apiService.getAllTanaman();
        call.enqueue(new Callback<List<Tanaman>>() {
            @Override
            public void onResponse(Call<List<Tanaman>> call, Response<List<Tanaman>> response) {
                showLoading(false);
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    dataList.clear();
                    dataList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(DashboardActivity.this,
                            "Gagal memuat data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Tanaman>> call, Throwable t) {
                showLoading(false);
                swipeRefresh.setRefreshing(false);
                Toast.makeText(DashboardActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteTanaman(int position) {
        Tanaman tanaman = dataList.get(position);

        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Yakin ingin menghapus " + tanaman.getNama() + "?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    performDelete(tanaman, position);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void performDelete(Tanaman tanaman, int position) {
        Call<Void> call = apiService.deleteTanaman(tanaman.getNama()); // atau gunakan ID
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    dataList.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(DashboardActivity.this,
                            "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DashboardActivity.this,
                            "Gagal menghapus data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DashboardActivity.this,
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