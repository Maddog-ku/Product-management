package com.example.inventoryapp.ui;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.bumptech.glide.Glide;
import com.example.inventoryapp.R;
import com.example.inventoryapp.data.ProductListItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(ProductListItem item);
    }

    private final List<ProductListItem> products = new ArrayList<>();
    private final OnProductClickListener listener;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.TAIWAN);

    public ProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<ProductListItem> newItems) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return products.size();
            }

            @Override
            public int getNewListSize() {
                return newItems.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return products.get(oldItemPosition).getId() == newItems.get(newItemPosition).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                ProductListItem oldItem = products.get(oldItemPosition);
                ProductListItem newItem = newItems.get(newItemPosition);
                return oldItem.getName().equals(newItem.getName()) &&
                       oldItem.getSalePrice() == newItem.getSalePrice() &&
                       oldItem.getStock() == newItem.getStock();
            }
        });
        
        products.clear();
        products.addAll(newItems);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductListItem item = products.get(position);
        holder.tvName.setText(item.getName());
        holder.tvCategory.setText(item.getCategoryName() == null || item.getCategoryName().isEmpty()
                ? holder.itemView.getContext().getString(R.string.no_category)
                : item.getCategoryName());
        holder.tvPrice.setText(currencyFormat.format(item.getSalePrice()));
        holder.tvStock.setText(holder.itemView.getContext().getString(R.string.stock) + ": " + item.getStock());

        if (item.getImagePath() == null || item.getImagePath().isEmpty()) {
            Glide.with(holder.itemView.getContext()).clear(holder.ivProduct);
            holder.ivProduct.setImageResource(R.drawable.ic_image_placeholder);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(item.getImagePath()))
                    .centerCrop()
                    .override(200, 200) // 縮小圖片解析度，降低記憶體消耗
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // 確保快取，避免重複解碼
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(holder.ivProduct);
        }

        holder.itemView.setOnClickListener(v -> listener.onProductClick(item));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName;
        TextView tvCategory;
        TextView tvPrice;
        TextView tvStock;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvCategory = itemView.findViewById(R.id.tvCategoryName);
            tvPrice = itemView.findViewById(R.id.tvSalePrice);
            tvStock = itemView.findViewById(R.id.tvStock);
        }
    }
}
