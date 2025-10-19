package com.example.tiwilanguageapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {

    public interface OnCategoryClick { void onClick(Category cat); }

    private final List<Category> data;
    private final OnCategoryClick onClick;

    public CategoryAdapter(List<Category> data, OnCategoryClick onClick) {
        this.data = data; this.onClick = onClick;
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img; TextView name;
        VH(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.imgIcon);
            name = v.findViewById(R.id.tvName);
        }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Category c = data.get(pos);
        h.img.setImageResource(c.imageRes);
        h.name.setText(c.name);
        h.itemView.setOnClickListener(v -> onClick.onClick(c));
    }

    @Override public int getItemCount() { return data.size(); }

    public void replaceData(List<Category> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

}
