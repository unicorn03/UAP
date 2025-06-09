package com.example.uap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.uap.models.Tanaman;
import com.example.uap.models.TanamanResponse;
import com.example.uap.models.TanamanSingleResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";
    RecyclerView recyclerViewTanaman;
    Button btnTambahList;
    SwipeRefreshLayout swipeRefresh;
    ProgressBar progressBar;
    ArrayList<Tanaman> dataList;
    TanamanAdapter adapter;
    ApiService apiService;

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> addTanamanLauncher;
    private ActivityResultLauncher<Intent> updateTanamanLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Activity Result Launchers
        setupActivityLaunchers();

        // Initialize API service
        apiService = ApiClient.getApiService();

        // Initialize views
        recyclerViewTanaman = findViewById(R.id.recyclerViewTanaman);
        btnTambahList = findViewById(R.id.btnTambahList);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);

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
                intent.putExtra("nama", tanaman.getPlant_name());
                intent.putExtra("harga", tanaman.getPrice());
                intent.putExtra("deskripsi", tanaman.getDescription());
                intent.putExtra("gambarResId", R.drawable.tanaman_dua);
                startActivity(intent);
            }

            @Override
            public void onUpdate(int pos) {
                Tanaman tanaman = dataList.get(pos);
                Intent intent = new Intent(DashboardActivity.this, UpdateTanamanActivity.class);
                intent.putExtra("nama", tanaman.getPlant_name());
                intent.putExtra("harga", tanaman.getPrice());
                intent.putExtra("deskripsi", tanaman.getDescription());
                intent.putExtra("gambarResId", R.drawable.tanaman_dua);
                updateTanamanLauncher.launch(intent);
            }
        });

        recyclerViewTanaman.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTanaman.setAdapter(adapter);

        // Setup swipe to refresh
        swipeRefresh.setOnRefreshListener(this::loadTanamanData);

        btnTambahList.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, TambahTanamanActivity.class);
            addTanamanLauncher.launch(intent);
        });

        // Load initial data
        loadTanamanData();
    }

    private void setupActivityLaunchers() {
        // Launcher for Add Tanaman Activity
        addTanamanLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d(TAG, "Add Tanaman Result: " + result.getResultCode());
                    if (result.getResultCode() == RESULT_OK) {
                        // Refresh data when returning from add activity
                        Log.d(TAG, "Data successfully added, refreshing list...");
                        loadTanamanData();
                        Toast.makeText(this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "Add Tanaman cancelled or failed");
                    }
                }
        );

        // Launcher for Update Tanaman Activity
        updateTanamanLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d(TAG, "Update Tanaman Result: " + result.getResultCode());
                    if (result.getResultCode() == RESULT_OK) {
                        // Refresh data when returning from update activity
                        Log.d(TAG, "Data successfully updated, refreshing list...");
                        loadTanamanData();
                        Toast.makeText(this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "Update Tanaman cancelled or failed");
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Only refresh if we're returning from another activity, not on first load
        if (dataList.size() > 0) {
            Log.d(TAG, "onResume: Refreshing data...");
            loadTanamanData();
        }
    }

    private void loadTanamanData() {
        Log.d(TAG, "Loading tanaman data...");
        showLoading(true);

        Call<TanamanResponse> call = apiService.getAllPlants();
        call.enqueue(new Callback<TanamanResponse>() {
            @Override
            public void onResponse(Call<TanamanResponse> call, Response<TanamanResponse> response) {
                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response body: " + (response.body() != null ? response.body().toString() : "null"));

                showLoading(false);
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    TanamanResponse tanamanResponse = response.body();
                    Log.d(TAG, "Response message: " + tanamanResponse.getMessage());
                    Log.d(TAG, "Data size from API: " + (tanamanResponse.getData() != null ? tanamanResponse.getData().size() : 0));

                    dataList.clear();
                    if (tanamanResponse.getData() != null) {
                        dataList.addAll(tanamanResponse.getData());
                        for (Tanaman tanaman : dataList) {
                            Log.d(TAG, "Plant: " + tanaman.getPlant_name() + ", Price: " + tanaman.getPrice() + ", Desc: " + tanaman.getDescription());
                        }
                    }
                    adapter.notifyDataSetChanged();

                    if (dataList.isEmpty()) {
                        Toast.makeText(DashboardActivity.this, "Tidak ada data tanaman", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Response not successful: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    Toast.makeText(DashboardActivity.this, "Gagal memuat data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TanamanResponse> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                showLoading(false);
                swipeRefresh.setRefreshing(false);
                Toast.makeText(DashboardActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteTanaman(int position) {
        Tanaman tanaman = dataList.get(position);

        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Yakin ingin menghapus " + tanaman.getPlant_name() + "?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    performDelete(tanaman, position);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void performDelete(Tanaman tanaman, int position) {
        Call<TanamanSingleResponse> call = apiService.deletePlant(tanaman.getPlant_name());
        call.enqueue(new Callback<TanamanSingleResponse>() {
            @Override
            public void onResponse(Call<TanamanSingleResponse> call, Response<TanamanSingleResponse> response) {
                if (response.isSuccessful()) {
                    dataList.remove(position);
                    adapter.notifyItemRemoved(position);
                    // Update positions for remaining items
                    adapter.notifyItemRangeChanged(position, dataList.size());
                    Toast.makeText(DashboardActivity.this,
                            "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DashboardActivity.this,
                            "Gagal menghapus data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TanamanSingleResponse> call, Throwable t) {
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