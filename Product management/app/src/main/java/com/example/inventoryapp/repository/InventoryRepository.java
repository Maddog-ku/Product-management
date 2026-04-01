package com.example.inventoryapp.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.inventoryapp.data.AppDatabase;
import com.example.inventoryapp.data.Category;
import com.example.inventoryapp.data.CategoryDao;
import com.example.inventoryapp.data.Product;
import com.example.inventoryapp.data.ProductDao;
import com.example.inventoryapp.data.ProductListItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InventoryRepository {

    public interface ProductCallback {
        void onLoaded(Product product);
    }

    private static volatile InventoryRepository INSTANCE;
    private final ProductDao productDao;
    private final CategoryDao categoryDao;
    private final ExecutorService executorService;

    private InventoryRepository(Context context) {
        // Repository 集中管理資料來源，讓 Activity 不直接碰 DAO。
        AppDatabase database = AppDatabase.getInstance(context);
        productDao = database.productDao();
        categoryDao = database.categoryDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public static InventoryRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (InventoryRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new InventoryRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<List<ProductListItem>> getProducts() {
        return productDao.getAllWithCategory();
    }

    public LiveData<List<Category>> getCategories() {
        return categoryDao.getAll();
    }

    public void insertCategory(Category category) {
        executorService.execute(() -> categoryDao.insert(category));
    }

    public void updateCategory(Category category) {
        executorService.execute(() -> categoryDao.update(category));
    }

    public void deleteCategory(Category category) {
        executorService.execute(() -> categoryDao.delete(category));
    }

    public void insertProduct(Product product) {
        executorService.execute(() -> productDao.insert(product));
    }

    public void updateProduct(Product product) {
        executorService.execute(() -> productDao.update(product));
    }

    public void deleteProduct(int productId) {
        executorService.execute(() -> productDao.deleteById(productId));
    }

    public void updateStock(int productId, int newStock) {
        executorService.execute(() -> productDao.updateStock(productId, newStock, System.currentTimeMillis()));
    }

    public void getProductById(int productId, ProductCallback callback) {
        executorService.execute(() -> callback.onLoaded(productDao.getById(productId)));
    }
}
