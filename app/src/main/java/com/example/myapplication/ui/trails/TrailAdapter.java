package com.example.myapplication.ui.trails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Trail;

import java.util.ArrayList;
import java.util.List;

public class TrailAdapter extends RecyclerView.Adapter<TrailAdapter.VH> {
    public interface OnTrailClick {
        void onTrailSelected(Trail trail);
    }

    private final List<Trail> items = new ArrayList<>();
    private final OnTrailClick listener;

    public TrailAdapter(OnTrailClick listener) {
        this.listener = listener;
    }

    public void submitList(List<Trail> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trail, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Trail t = items.get(position);
        holder.name.setText(t.getName());
        holder.meta.setText(t.getLocation());
        holder.difficulty.setText(t.getDifficulty());
        if ("Easy".equals(t.getDifficulty())) holder.difficulty.setBackgroundResource(R.drawable.bg_pill_easy); else if ("Medium".equals(t.getDifficulty())) holder.difficulty.setBackgroundResource(R.drawable.bg_pill_medium); else holder.difficulty.setBackgroundResource(R.drawable.bg_pill_hard);
        holder.kmChip.setText("KM");
        holder.kmValue.setText(String.format("%.1f", t.getDistanceKm()));
        int h = t.getEstimatedTimeMin() / 60; int m = t.getEstimatedTimeMin() % 60;
        holder.timeValue.setText(String.format("%dh %dm", h, m));
        holder.elevValue.setText(String.format("%dm", t.getElevationM()));
        if (holder.banner != null) {
            int resId = 0;
            String img = t.getImageName();
            if (img != null && !img.isEmpty()) {
                resId = holder.banner.getResources().getIdentifier(img, "drawable", holder.banner.getContext().getPackageName());
            }
            holder.banner.setImageResource(resId != 0 ? resId : imageForTrail(t));
        }
        holder.itemView.setOnClickListener(v -> listener.onTrailSelected(t));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, meta, difficulty, kmChip, kmValue, timeValue, elevValue;
        ImageView banner;
        VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.trail_name);
            meta = itemView.findViewById(R.id.trail_meta);
            difficulty = itemView.findViewById(R.id.difficulty_pill);
            kmChip = itemView.findViewById(R.id.chip_km);
            int idKm = itemView.getResources().getIdentifier("value_km", "id", itemView.getContext().getPackageName());
            int idTime = itemView.getResources().getIdentifier("value_time", "id", itemView.getContext().getPackageName());
            int idElev = itemView.getResources().getIdentifier("value_elev", "id", itemView.getContext().getPackageName());
            kmValue = itemView.findViewById(idKm);
            timeValue = itemView.findViewById(idTime);
            elevValue = itemView.findViewById(idElev);
            banner = itemView.findViewById(itemView.getResources().getIdentifier("trail_image", "id", itemView.getContext().getPackageName()));
        }
    }

    private int imageForTrail(Trail t) {
        String n = t.getName().toLowerCase();
        String loc = t.getLocation().toLowerCase();
        if (n.contains("margalla") || loc.contains("islamabad")) return R.drawable.ic_mountain;
        if (n.contains("mushkpuri") || loc.contains("nathia")) return R.drawable.ic_tree;
        if (n.contains("pipeline") || loc.contains("ayubia")) return R.drawable.ic_tree;
        if (n.contains("fairy meadows") || n.contains("nanga parbat")) return R.drawable.ic_mountain;
        if (n.contains("hunza") || n.contains("rakaposhi") || loc.contains("hunza")) return R.drawable.ic_mountain;
        if (n.contains("k2")) return R.drawable.ic_trophy;
        return R.drawable.ic_logo_mountain;
    }
}
