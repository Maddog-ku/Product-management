package com.example.inventoryapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductDao {

    @Insert
    long insert(Product product);

    @Update
    void update(Product product);

    @Query("DELETE FROM products WHERE id = :productId")
    void deleteById(int productId);

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    Product getById(int productId);

    @Query("UPDATE products SET stock = :newStock, updatedAt = :updatedAt WHERE id = :productId")
    void updateStock(int productId, int newStock, long updatedAt);

    @Query("SELECT p.*, c.name AS categoryName FROM products p " +
            "LEFT JOIN categories c ON p.categoryId = c.id " +
            "ORDER BY p.updatedAt DESC")
    LiveData<List<ProductListItem>> getAllWithCategory();

    @Query("SELECT * FROM products")
    List<Product> getAllSync();
}
