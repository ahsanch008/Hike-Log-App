package com.example.myapplication.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Hike;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.VH> {
    public interface OnHikeClick { void onHikeSelected(Hike hike); }
    public interface OnHikeDelete { void onHikeDelete(Hike hike); }
    private final List<Hike> items = new ArrayList<>();
    private final OnHikeClick listener;
    private final OnHikeDelete deleteListener;
    private final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
    private final java.util.HashSet<Long> expanded = new java.util.HashSet<>();

    public HikeAdapter(OnHikeClick listener, OnHikeDelete deleteListener) {
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    public void submitList(List<Hike> list) {
        items.clear();
        expanded.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hike, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Hike h = items.get(position);
        holder.title.setText(h.getTrailName());
        holder.meta.setText(df.format(new Date(h.getDateMillis())) + " • " + h.getDurationMin() + "m");
        holder.itemView.setOnClickListener(v -> listener.onHikeSelected(h));
        holder.diffPill.setText(h.getDifficulty());
        if ("Easy".equals(h.getDifficulty())) holder.diffPill.setBackgroundResource(R.drawable.bg_pill_easy);
        else if ("Medium".equals(h.getDifficulty())) holder.diffPill.setBackgroundResource(R.drawable.bg_pill_medium);
        else holder.diffPill.setBackgroundResource(R.drawable.bg_pill_hard);
        boolean isExpanded = expanded.contains(h.getId());
        holder.deleteRow.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.expand.setRotation(isExpanded ? 90f : 270f);
        holder.expand.setOnClickListener(v -> {
            if (expanded.contains(h.getId())) expanded.remove(h.getId()); else expanded.add(h.getId());
            notifyItemChanged(position);
        });
        holder.deleteRow.setOnClickListener(v -> deleteListener.onHikeDelete(h));
        holder.deleteText.setOnClickListener(v -> deleteListener.onHikeDelete(h));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, meta, diffPill, deleteText;
        View deleteRow;
        android.widget.ImageView expand;
        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.hike_title);
            meta = itemView.findViewById(R.id.hike_meta);
            diffPill = itemView.findViewById(R.id.hike_difficulty_pill);
            deleteRow = itemView.findViewById(R.id.action_delete_container);
            deleteText = itemView.findViewById(R.id.action_delete);
            expand = itemView.findViewById(R.id.btn_expand);
        }
    }
}
