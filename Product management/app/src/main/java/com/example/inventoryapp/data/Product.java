package com.example.inventoryapp.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "products",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.SET_NULL
        ),
        indices = {@Index("categoryId")}
)
public class Product {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String code;
    private String imagePath;
    private Integer categoryId;
    private double costPrice;
    private double salePrice;
    private int stock;
    private String note;
    private Long expiryDate;
    private Integer notifyDays;
    private long createdAt;
    private long updatedAt;

    public Product(String name, String code, String imagePath, Integer categoryId, double costPrice,
                   double salePrice, int stock, String note, Long expiryDate, Integer notifyDays, long createdAt, long updatedAt) {
        this.name = name;
        this.code = code;
        this.imagePath = imagePath;
        this.categoryId = categoryId;
        this.costPrice = costPrice;
        this.salePrice = salePrice;
        this.stock = stock;
        this.note = note;
        this.expiryDate = expiryDate;
        this.notifyDays = notifyDays;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getNotifyDays() {
        return notifyDays;
    }

    public void setNotifyDays(Integer notifyDays) {
        this.notifyDays = notifyDays;
    }
}
