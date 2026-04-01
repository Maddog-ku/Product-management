package com.example.inventoryapp.ui;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventoryapp.R;
import com.example.inventoryapp.data.ProductListItem;
import com.example.inventoryapp.repository.InventoryRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SellActivity extends AppCompatActivity {

    private InventoryRepository repository;
    private SellAdapter adapter;
    private final List<ProductListItem> currentProducts = new ArrayList<>();
    private final Map<Integer, Integer> cartQuantities = new HashMap<>();

    private TextView tvTotalAmount;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.TAIWAN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        repository = InventoryRepository.getInstance(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recyclerSellProducts);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        MaterialButton btnCheckout = findViewById(R.id.btnCheckout);

        adapter = new SellAdapter(this::updateCartTotals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        repository.getProducts().observe(this, products -> {
            currentProducts.clear();
            currentProducts.addAll(products);
            adapter.submitList(products);
            updateCartTotals(cartQuantities);
        });

        btnCheckout.setOnClickListener(v -> processCheckout());
    }

    private void updateCartTotals(Map<Integer, Integer> quantities) {
        cartQuantities.clear();
        cartQuantities.putAll(quantities);

        double total = 0;
        for (ProductListItem item : currentProducts) {
            Integer qty = cartQuantities.get(item.getId());
            if (qty != null && qty > 0) {
                total += item.getSalePrice() * qty;
            }
        }
        tvTotalAmount.setText("總計: " + currencyFormat.format(total));
    }

    private void processCheckout() {
        boolean hasItems = false;
        for (Integer qty : cartQuantities.values()) {
            if (qty > 0) {
                hasItems = true;
                break;
            }
        }

        if (!hasItems) {
            Toast.makeText(this, "購物車為空", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("結帳確認")
                .setMessage("確定要結帳並扣除庫存嗎？")
                .setPositiveButton("確定結帳", (dialog, which) -> {
                    // Update database stocks
                    for (ProductListItem item : currentProducts) {
                        Integer qtyToDeduct = cartQuantities.get(item.getId());
                        if (qtyToDeduct != null && qtyToDeduct > 0) {
                            int newStock = item.getStock() - qtyToDeduct;
                            repository.updateStock(item.getId(), newStock);
                        }
                    }
                    
                    Toast.makeText(this, "結帳成功，庫存已更新", Toast.LENGTH_SHORT).show();
                    adapter.clearCart();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}