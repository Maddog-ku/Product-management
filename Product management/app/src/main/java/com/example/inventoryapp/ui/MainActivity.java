package com.example.inventoryapp.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.inventoryapp.R;
import com.example.inventoryapp.worker.ExpiryNotificationWorker;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupWorkManager();

        MaterialButton btnProductManagement = findViewById(R.id.btnProductManagement);

        MaterialButton btnCategoryManagement = findViewById(R.id.btnCategoryManagement);
        MaterialButton btnStockTake = findViewById(R.id.btnStockTake);

        btnProductManagement.setOnClickListener(v ->
                startActivity(new Intent(this, ProductListActivity.class)));
        btnCategoryManagement.setOnClickListener(v ->
                startActivity(new Intent(this, CategoryActivity.class)));
        btnStockTake.setOnClickListener(v ->
                startActivity(new Intent(this, StockTakeActivity.class)));
    }

    private void setupWorkManager() {
        PeriodicWorkRequest expiryWorkRequest = new PeriodicWorkRequest.Builder(
                ExpiryNotificationWorker.class, 1, TimeUnit.DAYS)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "DailyExpiryCheck",
                ExistingPeriodicWorkPolicy.KEEP,
                expiryWorkRequest
        );
    }
}
