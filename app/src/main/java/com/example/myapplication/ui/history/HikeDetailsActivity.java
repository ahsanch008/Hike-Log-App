package com.example.myapplication.ui.history;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import com.example.myapplication.R;
import com.example.myapplication.data.db.HikeLogContract;
import com.example.myapplication.data.db.HikeLogDbHelper;
import com.example.myapplication.data.repository.HikeRepository;
import com.example.myapplication.data.repository.WildlifeRepository;
import com.example.myapplication.model.WildlifeSighting;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HikeDetailsActivity extends AppCompatActivity {
    private long hikeId;
    private WildlifeRepository wildlifeRepo;
    private WildlifeAdapter wildlifeAdapter;
    private LineChart elevationChart;
    private List<Entry> elevationEntries;
    private String elevationPointsJson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_details);
        hikeId = getIntent().getLongExtra("hike_id", -1);
        wildlifeRepo = new WildlifeRepository(this);

        TextView title = findViewById(R.id.hike_detail_title);
        TextView meta = findViewById(R.id.hike_detail_meta);
        TextView notes = findViewById(R.id.hike_detail_notes);
        load(title, meta, notes);
        setupWildlifeList();
        loadWildlife();

        Button delete = findViewById(R.id.btn_delete_hike);
        delete.setOnClickListener(v -> {
            SQLiteDatabase db = new HikeLogDbHelper(this).getWritableDatabase();
            db.delete(HikeLogContract.Hikes.TABLE, HikeLogContract.Hikes._ID + "=?", new String[]{String.valueOf(hikeId)});
            finish();
        });

        Button addWildlife = findViewById(R.id.btn_add_wildlife);
        if (addWildlife != null) {
            addWildlife.setOnClickListener(v -> showAddWildlifeDialog());
        }

        elevationChart = findViewById(R.id.elevation_chart);
        if (elevationChart != null) {
            setupElevationChart();
        }

        Button addElevation = findViewById(R.id.btn_add_elevation);
        if (addElevation != null) {
            addElevation.setOnClickListener(v -> showAddElevationDialog());
        }

        loadElevationData();
    }

    private void setupElevationChart() {
        elevationChart = findViewById(R.id.elevation_chart);
        elevationChart.getDescription().setEnabled(false);
        elevationChart.setTouchEnabled(true);
        elevationChart.setPinchZoom(true);
        elevationChart.setDrawGridBackground(false);

        Description desc = new Description();
        desc.setEnabled(false);
        elevationChart.setDescription(desc);
    }

    private void loadElevationData() {
        SQLiteDatabase db = new HikeLogDbHelper(this).getReadableDatabase();
        Cursor c = db.query(HikeLogContract.Hikes.TABLE, new String[]{HikeLogContract.Hikes.COL_ELEVATION_POINTS}, HikeLogContract.Hikes._ID + "=?", new String[]{String.valueOf(hikeId)}, null, null, null);
        elevationEntries = new ArrayList<>();
        if (c.moveToFirst()) {
            elevationPointsJson = c.getString(c.getColumnIndexOrThrow(HikeLogContract.Hikes.COL_ELEVATION_POINTS));
            if (elevationPointsJson != null && !elevationPointsJson.isEmpty()) {
                try {
                    JSONArray jsonArray = new JSONArray(elevationPointsJson);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject point = jsonArray.getJSONObject(i);
                        float distance = (float) point.getDouble("distance");
                        float elevation = (float) point.getDouble("elevation");
                        elevationEntries.add(new Entry(distance, elevation));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        c.close();

        if (elevationChart != null) {
            if (elevationEntries.isEmpty()) {
                elevationChart.setNoDataText("No elevation data recorded");
                elevationChart.clear();
                elevationChart.invalidate();
            } else {
                LineDataSet dataSet = new LineDataSet(elevationEntries, "Elevation");
                dataSet.setColor(0xFF2D8E6B);
                dataSet.setLineWidth(2f);
                dataSet.setCircleColor(0xFF2D8E6B);
                dataSet.setCircleRadius(4f);
                dataSet.setDrawValues(false);
                dataSet.setMode(LineDataSet.Mode.LINEAR);

                LineData lineData = new LineData(dataSet);
                lineData.setValueTextColor(0xFF6B7C75);
                elevationChart.setData(lineData);
                elevationChart.invalidate();
            }
        }
    }

    private void showAddElevationDialog() {
        Dialog dialog = new Dialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_elevation, null);
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        EditText distance = dialogView.findViewById(R.id.elevation_distance);
        EditText height = dialogView.findViewById(R.id.elevation_height);

        Button cancel = dialogView.findViewById(R.id.btn_cancel_elevation);
        Button submit = dialogView.findViewById(R.id.btn_submit_elevation);

        cancel.setOnClickListener(v -> dialog.dismiss());

        submit.setOnClickListener(v -> {
            String distanceText = distance.getText().toString().trim();
            String heightText = height.getText().toString().trim();

            if (distanceText.isEmpty() || heightText.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                float distanceVal = Float.parseFloat(distanceText);
                float elevationVal = Float.parseFloat(heightText);

                JSONObject newPoint = new JSONObject();
                newPoint.put("distance", distanceVal);
                newPoint.put("elevation", elevationVal);

                JSONArray jsonArray;
                if (elevationPointsJson != null && !elevationPointsJson.isEmpty()) {
                    jsonArray = new JSONArray(elevationPointsJson);
                } else {
                    jsonArray = new JSONArray();
                }

                boolean inserted = false;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject point = jsonArray.getJSONObject(i);
                    if (point.getDouble("distance") >= distanceVal) {
                        jsonArray.put(i, newPoint);
                        inserted = true;
                        break;
                    }
                }

                if (!inserted) {
                    jsonArray.put(newPoint);
                }

                elevationPointsJson = jsonArray.toString();

                HikeRepository hikeRepo = new HikeRepository(this);
                int updated = hikeRepo.updateElevationPoints(hikeId, elevationPointsJson);

                if (updated > 0) {
                    Toast.makeText(this, "Elevation point added", Toast.LENGTH_SHORT).show();
                    loadElevationData();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private void load(TextView title, TextView meta, TextView notesTv) {
        SQLiteDatabase db = new HikeLogDbHelper(this).getReadableDatabase();
        Cursor c = db.query(HikeLogContract.Hikes.TABLE, null, HikeLogContract.Hikes._ID + "=?", new String[]{String.valueOf(hikeId)}, null, null, null);
        if (!c.moveToFirst()) { c.close(); finish(); return; }
        String name = c.getString(c.getColumnIndexOrThrow(HikeLogContract.Hikes.COL_TRAIL_NAME));
        int duration = c.getInt(c.getColumnIndexOrThrow(HikeLogContract.Hikes.COL_DURATION_MIN));
        long date = c.getLong(c.getColumnIndexOrThrow(HikeLogContract.Hikes.COL_DATE));
        String notes = c.getString(c.getColumnIndexOrThrow(HikeLogContract.Hikes.COL_NOTES));
        c.close();
        java.text.DateFormat df = java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM);
        title.setText(name);
        meta.setText(df.format(new java.util.Date(date)) + " • " + duration + " min");
        notesTv.setText(notes == null ? "" : notes);
    }

    private void setupWildlifeList() {
        RecyclerView wildlifeList = findViewById(R.id.wildlife_list);
        if (wildlifeList != null) {
            wildlifeAdapter = new WildlifeAdapter(sighting -> {
                int deleted = wildlifeRepo.deleteSighting(sighting.getId());
                if (deleted > 0) {
                    Toast.makeText(this, "Sighting deleted", Toast.LENGTH_SHORT).show();
                    loadWildlife();
                } else {
                    Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            });
            wildlifeList.setAdapter(wildlifeAdapter);
        }
    }

    private void loadWildlife() {
        List<WildlifeSighting> sightings = wildlifeRepo.getSightingsForHike(hikeId);
        wildlifeAdapter.submitList(sightings);

        TextView emptyState = findViewById(R.id.wildlife_empty);
        if (emptyState != null) {
            emptyState.setVisibility(sightings.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private void showAddWildlifeDialog() {
        Dialog dialog = new Dialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_wildlife, null);
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        EditText animal = dialogView.findViewById(R.id.wildlife_animal);
        EditText species = dialogView.findViewById(R.id.wildlife_species);
        EditText quantity = dialogView.findViewById(R.id.wildlife_quantity);
        EditText notes = dialogView.findViewById(R.id.wildlife_notes);

        Button cancel = dialogView.findViewById(R.id.btn_cancel_wildlife);
        Button submit = dialogView.findViewById(R.id.btn_submit_wildlife);

        cancel.setOnClickListener(v -> dialog.dismiss());

        submit.setOnClickListener(v -> {
            String animalText = animal.getText().toString().trim();
            String speciesText = species.getText().toString().trim();
            String quantityText = quantity.getText().toString().trim();
            String notesText = notes.getText().toString().trim();

            if (animalText.isEmpty()) {
                Toast.makeText(this, "Please enter animal name", Toast.LENGTH_SHORT).show();
                return;
            }

            int qty = quantityText.isEmpty() ? 1 : Integer.parseInt(quantityText);

            long id = wildlifeRepo.addSighting(hikeId, animalText, speciesText, qty, notesText);
            if (id > 0) {
                Toast.makeText(this, "Sighting added", Toast.LENGTH_SHORT).show();
                loadWildlife();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
