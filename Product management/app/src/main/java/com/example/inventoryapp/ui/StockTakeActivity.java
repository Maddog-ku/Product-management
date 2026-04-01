package com.example.inventoryapp.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventoryapp.R;
import com.example.inventoryapp.repository.InventoryRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.Map;

public class StockTakeActivity extends AppCompatActivity {

    private InventoryRepository repository;
    private StockTakeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_take);

        repository = InventoryRepository.getInstance(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recyclerStockTake);
        MaterialButton btnUpdateAllStock = findViewById(R.id.btnUpdateAllStock);

        adapter = new StockTakeAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        repository.getProducts().observe(this, adapter::submitList);

        btnUpdateAllStock.setOnClickListener(v -> updateAllStock());
    }

    private void updateAllStock() {
        // 一次提交目前頁面上的盤點結果。
        for (Map.Entry<Integer, Integer> entry : adapter.getPendingStockMap().entrySet()) {
            repository.updateStock(entry.getKey(), entry.getValue());
        }
        Toast.makeText(this, "庫存已更新", Toast.LENGTH_SHORT).show();
    }
}
