package com.example.myapplication.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.WildlifeSighting;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class WildlifeAdapter extends RecyclerView.Adapter<WildlifeAdapter.WildlifeViewHolder> {
    private List<WildlifeSighting> sightings = new ArrayList<>();
    private OnSightingDeleteListener deleteListener;

    public interface OnSightingDeleteListener {
        void onDelete(WildlifeSighting sighting);
    }

    public WildlifeAdapter(OnSightingDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void submitList(List<WildlifeSighting> newSightings) {
        this.sightings = newSightings != null ? newSightings : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WildlifeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wildlife, parent, false);
        return new WildlifeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WildlifeViewHolder holder, int position) {
        holder.bind(sightings.get(position), deleteListener);
    }

    @Override
    public int getItemCount() {
        return sightings.size();
    }

    static class WildlifeViewHolder extends RecyclerView.ViewHolder {
        private TextView animal;
        private TextView species;
        private TextView quantity;
        private TextView notes;
        private ImageButton deleteButton;

        public WildlifeViewHolder(@NonNull View itemView) {
            super(itemView);
            animal = itemView.findViewById(R.id.wildlife_animal);
            species = itemView.findViewById(R.id.wildlife_species);
            quantity = itemView.findViewById(R.id.wildlife_quantity);
            notes = itemView.findViewById(R.id.wildlife_notes);
            deleteButton = itemView.findViewById(R.id.btn_delete_wildlife);
        }

        public void bind(WildlifeSighting sighting, OnSightingDeleteListener deleteListener) {
            animal.setText(sighting.getAnimalName());
            species.setText(sighting.getSpecies() != null ? sighting.getSpecies() : "");
            species.setVisibility(sighting.getSpecies() != null && !sighting.getSpecies().isEmpty() ? View.VISIBLE : View.GONE);
            quantity.setText("x" + sighting.getQuantity());
            notes.setText(sighting.getNotes() != null ? sighting.getNotes() : "");
            notes.setVisibility(sighting.getNotes() != null && !sighting.getNotes().isEmpty() ? View.VISIBLE : View.GONE);

            deleteButton.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDelete(sighting);
                }
            });
        }
    }
}
