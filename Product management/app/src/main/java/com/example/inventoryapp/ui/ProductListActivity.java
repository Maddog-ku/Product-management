package com.example.inventoryapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.inventoryapp.R;
import com.example.inventoryapp.data.Category;
import com.example.inventoryapp.data.ProductListItem;
import com.example.inventoryapp.repository.InventoryRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductListActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "extra_product_id";

    private InventoryRepository repository;
    private ProductAdapter adapter;
    private final List<ProductListItem> allProducts = new ArrayList<>();
    private final List<Category> allCategories = new ArrayList<>();
    private String selectedCategoryName = "";
    private EditText etSearch;
    private DrawerLayout drawerLayout;
    private LinearLayout navMenuContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        repository = InventoryRepository.getInstance(this);

        drawerLayout = findViewById(R.id.drawerLayout);
        navMenuContainer = findViewById(R.id.navMenuContainer);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        
        // 設置 Drawer 漢堡選單圖示
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        
        // 點擊返回鍵處理 drawer 關閉
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        etSearch = findViewById(R.id.etSearchProduct);
        RecyclerView recyclerView = findViewById(R.id.recyclerProducts);
        FloatingActionButton fabAddProduct = findViewById(R.id.fabAddProduct);
        FloatingActionButton fabSellProduct = findViewById(R.id.fabSellProduct);

        adapter = new ProductAdapter(item -> {
            Intent intent = new Intent(this, AddEditProductActivity.class);
            intent.putExtra(EXTRA_PRODUCT_ID, item.getId());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabAddProduct.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditProductActivity.class)));
        
        fabSellProduct.setOnClickListener(v ->
                startActivity(new Intent(this, SellActivity.class)));

        etSearch.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                applyFilter(s.toString(), selectedCategoryName);
            }
        });

        repository.getProducts().observe(this, products -> {
            allProducts.clear();
            allProducts.addAll(products);
            applyFilter(etSearch.getText().toString(), selectedCategoryName);
        });

        repository.getCategories().observe(this, categories -> {
            allCategories.clear();
            allCategories.addAll(categories);
            if (selectedCategoryName.isEmpty()) {
                selectedCategoryName = getString(R.string.all_categories);
            }
            updateNavigationMenu();
        });
    }

    private void updateNavigationMenu() {
        navMenuContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        // 加入 "所有分類"
        View allCategoriesView = inflater.inflate(R.layout.item_nav_menu, navMenuContainer, false);
        TextView tvAllTitle = allCategoriesView.findViewById(R.id.tvNavTitle);
        tvAllTitle.setText(R.string.all_categories);
        allCategoriesView.setOnClickListener(v -> {
            selectedCategoryName = getString(R.string.all_categories);
            applyFilter(etSearch.getText().toString(), selectedCategoryName);
            drawerLayout.closeDrawer(GravityCompat.START);
        });
        navMenuContainer.addView(allCategoriesView);

        // 加入從資料庫拿到的群組
        for (Category category : allCategories) {
            View categoryView = inflater.inflate(R.layout.item_nav_menu, navMenuContainer, false);
            TextView tvTitle = categoryView.findViewById(R.id.tvNavTitle);
            tvTitle.setText(category.getName());
            
            categoryView.setOnClickListener(v -> {
                selectedCategoryName = category.getName();
                applyFilter(etSearch.getText().toString(), selectedCategoryName);
                drawerLayout.closeDrawer(GravityCompat.START);
            });
            
            categoryView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("刪除分類")
                        .setMessage("確定要刪除「" + category.getName() + "」分類嗎？")
                        .setPositiveButton("刪除", (dialog, which) -> {
                            repository.deleteCategory(category);
                            if (selectedCategoryName.equals(category.getName())) {
                                selectedCategoryName = getString(R.string.all_categories);
                                applyFilter(etSearch.getText().toString(), selectedCategoryName);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                return true;
            });
            
            navMenuContainer.addView(categoryView);
        }
    }

    private void applyFilter(String keyword, String categoryName) {
        // 搜尋與分類先走前端過濾，資料量不大時維護成本最低。
        List<ProductListItem> filtered = new ArrayList<>();
        String query = keyword == null ? "" : keyword.trim().toLowerCase(Locale.getDefault());
        for (ProductListItem item : allProducts) {
            boolean matchKeyword = item.getName() != null
                    && item.getName().toLowerCase(Locale.getDefault()).contains(query);
            boolean matchCategory = categoryName == null
                    || categoryName.isEmpty()
                    || categoryName.equals(getString(R.string.all_categories))
                    || categoryName.equals(item.getCategoryName());
            if (matchKeyword && matchCategory) {
                filtered.add(item);
            }
        }
        adapter.submitList(filtered);
    }

    abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }
}
