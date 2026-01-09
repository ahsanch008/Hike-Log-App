package com.example.myapplication.ui.trails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.PackingItem;

import java.util.ArrayList;
import java.util.List;

public class PackingAdapter extends RecyclerView.Adapter<PackingAdapter.PackingViewHolder> {
    private List<PackingItem> items = new ArrayList<>();

    public void submitList(List<PackingItem> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PackingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_packing, parent, false);
        return new PackingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PackingViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PackingViewHolder extends RecyclerView.ViewHolder {
        private CheckBox packedCheckbox;
        private TextView name;
        private TextView category;
        private TextView quantity;
        private ImageView essentialIcon;

        public PackingViewHolder(@NonNull View itemView) {
            super(itemView);
            packedCheckbox = itemView.findViewById(R.id.item_packed);
            name = itemView.findViewById(R.id.item_name);
            category = itemView.findViewById(R.id.item_category);
            quantity = itemView.findViewById(R.id.item_quantity);
            essentialIcon = itemView.findViewById(R.id.item_essential);
        }

        public void bind(PackingItem item) {
            name.setText(item.getItemName());
            category.setText(item.getCategory());
            quantity.setText(String.valueOf(item.getQuantityDefault()));
            packedCheckbox.setEnabled(false);
            essentialIcon.setVisibility(item.isEssential() ? View.VISIBLE : View.GONE);
        }
    }
}
