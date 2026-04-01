package com.example.inventoryapp.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventoryapp.R;
import com.example.inventoryapp.data.ProductListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockTakeAdapter extends RecyclerView.Adapter<StockTakeAdapter.StockViewHolder> {

    private final List<ProductListItem> products = new ArrayList<>();
    private final Map<Integer, Integer> pendingStockMap = new HashMap<>();

    public void submitList(List<ProductListItem> items) {
        products.clear();
        products.addAll(items);
        pendingStockMap.clear();
        for (ProductListItem item : items) {
            pendingStockMap.put(item.getId(), item.getStock());
        }
        notifyDataSetChanged();
    }

    public Map<Integer, Integer> getPendingStockMap() {
        return pendingStockMap;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock_take, parent, false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        ProductListItem item = products.get(position);
        holder.tvName.setText(item.getName());
        holder.tvCurrentStock.setText(holder.itemView.getContext().getString(R.string.stock) + ": " + item.getStock());
        if (holder.watcher != null) {
            holder.etNewStock.removeTextChangedListener(holder.watcher);
        }
        holder.etNewStock.setText(String.valueOf(pendingStockMap.get(item.getId())));
        holder.watcher = new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                String value = editable.toString().trim();
                int stock = value.isEmpty() ? 0 : Integer.parseInt(value);
                pendingStockMap.put(item.getId(), stock);
            }
        };
        holder.etNewStock.addTextChangedListener(holder.watcher);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class StockViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvCurrentStock;
        EditText etNewStock;
        SimpleTextWatcher watcher;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvStockProductName);
            tvCurrentStock = itemView.findViewById(R.id.tvCurrentStock);
            etNewStock = itemView.findViewById(R.id.etNewStock);
        }
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
