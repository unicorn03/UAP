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

import com.example.uap.models.Plant;
import com.example.uap.models.PlantResponse;
import com.example.uap.models.PlantSingleResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";
    RecyclerView recyclerViewTanaman;
    Button btnTambahList;
    SwipeRefreshLayout swipeRefresh;
    ProgressBar progressBar;
    ArrayList<Plant> dataList;
    PlantAdapter adapter;
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
        apiService = ApiClient.getClient().create(ApiService.class);

        // Initialize views
        recyclerViewTanaman = findViewById(R.id.recyclerViewTanaman);
        btnTambahList = findViewById(R.id.btnTambahList);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);

        dataList = new ArrayList<>();

        adapter = new PlantAdapter(this, dataList, new PlantAdapter.OnItemClickListener() {
            @Override
            public void onHapus(int pos) {
                deleteTanaman(pos);
            }

            @Override
            public void onDetail(int pos) {
                Plant plant = dataList.get(pos);
                Intent intent = new Intent(DashboardActivity.this, DetailTanamanActivity.class);
                intent.putExtra("nama", plant.getPlant_name());
                intent.putExtra("harga", plant.getPrice());
                intent.putExtra("deskripsi", plant.getDescription());
                intent.putExtra("gambarResId", R.drawable.tanaman_dua);
                startActivity(intent);
            }

            @Override
            public void onUpdate(int pos) {
                Plant plant = dataList.get(pos);
                Intent intent = new Intent(DashboardActivity.this, UpdateTanamanActivity.class);
                intent.putExtra("nama", plant.getPlant_name());
                intent.putExtra("harga", plant.getPrice());
                intent.putExtra("deskripsi", plant.getDescription());
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

        Call<PlantResponse> call = apiService.getAllPlants();
        call.enqueue(new Callback<PlantResponse>() {
            @Override
            public void onResponse(Call<PlantResponse> call, Response<PlantResponse> response) {
                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response body: " + (response.body() != null ? response.body().toString() : "null"));

                showLoading(false);
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    PlantResponse plantResponse = response.body();
                    Log.d(TAG, "Response message: " + plantResponse.getMessage());
                    Log.d(TAG, "Data size from API: " + (plantResponse.getData() != null ? plantResponse.getData().size() : 0));

                    dataList.clear();
                    if (plantResponse.getData() != null) {
                        dataList.addAll(plantResponse.getData());
                        for (Plant plant : dataList) {
                            Log.d(TAG, "Plant: " + plant.getPlant_name() + ", Price: " + plant.getPrice() + ", Desc: " + plant.getDescription());
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
            public void onFailure(Call<PlantResponse> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                showLoading(false);
                swipeRefresh.setRefreshing(false);
                Toast.makeText(DashboardActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteTanaman(int position) {
        Plant plant = dataList.get(position);

        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Yakin ingin menghapus " + plant.getPlant_name() + "?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    performDelete(plant, position);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void performDelete(Plant plant, int position) {
        Call<PlantSingleResponse> call = apiService.deletePlant(plant.getPlant_name());
        call.enqueue(new Callback<PlantSingleResponse>() {
            @Override
            public void onResponse(Call<PlantSingleResponse> call, Response<PlantSingleResponse> response) {
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
            public void onFailure(Call<PlantSingleResponse> call, Throwable t) {
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