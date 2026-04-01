package com.example.inventoryapp.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventoryapp.R;
import com.example.inventoryapp.data.Category;
import com.example.inventoryapp.repository.InventoryRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class CategoryActivity extends AppCompatActivity {

    private InventoryRepository repository;
    private CategoryAdapter adapter;
    private TextInputEditText etCategoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        repository = InventoryRepository.getInstance(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etCategoryName = findViewById(R.id.etCategoryName);
        MaterialButton btnAddCategory = findViewById(R.id.btnAddCategory);
        RecyclerView recyclerView = findViewById(R.id.recyclerCategories);

        adapter = new CategoryAdapter(new CategoryAdapter.CategoryActionListener() {
            @Override
            public void onEdit(Category category) {
                showEditDialog(category);
            }

            @Override
            public void onDelete(Category category) {
                repository.deleteCategory(category);
                Toast.makeText(CategoryActivity.this, "分類已刪除", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAddCategory.setOnClickListener(v -> addCategory());

        repository.getCategories().observe(this, categories -> adapter.submitList(categories));
    }

    private void addCategory() {
        String name = etCategoryName.getText() == null ? "" : etCategoryName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            etCategoryName.setError("請輸入分類名稱");
            return;
        }
        repository.insertCategory(new Category(name));
        etCategoryName.setText("");
        Toast.makeText(this, "分類已新增", Toast.LENGTH_SHORT).show();
    }

    private void showEditDialog(Category category) {
        final TextInputEditText editText = new TextInputEditText(this);
        editText.setText(category.getName());
        new AlertDialog.Builder(this)
                .setTitle("修改分類")
                .setView(editText)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String name = editText.getText() == null ? "" : editText.getText().toString().trim();
                    if (!name.isEmpty()) {
                        category.setName(name);
                        repository.updateCategory(category);
                        Toast.makeText(this, "分類已更新", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
