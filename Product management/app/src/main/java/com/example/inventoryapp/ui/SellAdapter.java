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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.inventoryapp.R;
import com.example.inventoryapp.data.ProductListItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SellAdapter extends RecyclerView.Adapter<SellAdapter.SellViewHolder> {

    public interface OnCartChangeListener {
        void onCartChanged(Map<Integer, Integer> cartQuantities);
    }

    private final List<ProductListItem> products = new ArrayList<>();
    private final Map<Integer, Integer> cartQuantities = new HashMap<>();
    private final OnCartChangeListener listener;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.TAIWAN);

    public SellAdapter(OnCartChangeListener listener) {
        this.listener = listener;
    }

    public void submitList(List<ProductListItem> items) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return products.size();
            }

            @Override
            public int getNewListSize() {
                return items.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return products.get(oldItemPosition).getId() == items.get(newItemPosition).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                ProductListItem oldItem = products.get(oldItemPosition);
                ProductListItem newItem = items.get(newItemPosition);
                return oldItem.getName().equals(newItem.getName()) &&
                       oldItem.getSalePrice() == newItem.getSalePrice() &&
                       oldItem.getStock() == newItem.getStock();
            }
        });

        products.clear();
        products.addAll(items);
        diffResult.dispatchUpdatesTo(this);
    }

    public void clearCart() {
        cartQuantities.clear();
        int size = products.size();
        if (size > 0) {
            notifyItemRangeChanged(0, size);
        }
        listener.onCartChanged(cartQuantities);
    }

    @NonNull
    @Override
    public SellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sell_product, parent, false);
        return new SellViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellViewHolder holder, int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class SellViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage, btnMinus, btnAdd;
        TextView tvName, tvPrice, tvStock, tvCartQuantity;

        SellViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvStock = itemView.findViewById(R.id.tvProductStock);
            tvCartQuantity = itemView.findViewById(R.id.tvCartQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }

        void bind(ProductListItem item) {
            tvName.setText(item.getName());
            tvPrice.setText(currencyFormat.format(item.getSalePrice()));
            tvStock.setText(itemView.getContext().getString(R.string.stock_format, item.getStock()));

            Integer mappedQty = cartQuantities.get(item.getId());
            int quantity = mappedQty != null ? mappedQty : 0;
            tvCartQuantity.setText(String.valueOf(quantity));

            if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(Uri.parse(item.getImagePath()))
                        .override(200, 200)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(ivImage);
            } else {
                Glide.with(itemView.getContext()).clear(ivImage);
                ivImage.setImageResource(R.drawable.ic_image_placeholder);
            }

            btnAdd.setOnClickListener(v -> {
                Integer currentMappedQty = cartQuantities.get(item.getId());
                int currentQty = currentMappedQty != null ? currentMappedQty : 0;
                if (currentQty < item.getStock()) {
                    cartQuantities.put(item.getId(), currentQty + 1);
                    tvCartQuantity.setText(String.valueOf(currentQty + 1));
                    listener.onCartChanged(cartQuantities);
                }
            });

            btnMinus.setOnClickListener(v -> {
                Integer currentMappedQty = cartQuantities.get(item.getId());
                int currentQty = currentMappedQty != null ? currentMappedQty : 0;
                if (currentQty > 0) {
                    cartQuantities.put(item.getId(), currentQty - 1);
                    tvCartQuantity.setText(String.valueOf(currentQty - 1));
                    listener.onCartChanged(cartQuantities);
                }
            });
        }
    }
}