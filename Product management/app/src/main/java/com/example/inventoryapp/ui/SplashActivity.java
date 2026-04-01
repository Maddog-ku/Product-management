package com.example.inventoryapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inventoryapp.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 設定 2 秒後進入商品列表頁面 (ProductListActivity)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, ProductListActivity.class);
            startActivity(intent);
            finish(); // 結束 SplashActivity，讓使用者按返回鍵不會回到載入畫面
        }, 2000);
    }
}