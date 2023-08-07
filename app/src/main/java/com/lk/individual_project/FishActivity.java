package com.lk.individual_project;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FishActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FishAdapter fishAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish);

        recyclerView = findViewById(R.id.fishlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample fish data (replace with your actual data)
        List<Fish> fishList = new ArrayList<>();
        fishList.add(new Fish("Clownfish", "Small colorful fish found in tropical seas."));
        fishList.add(new Fish("Angelfish", "Brightly colored and known for their graceful swimming."));
        // Add more fish items here...

        fishAdapter = new FishAdapter(fishList);
        recyclerView.setAdapter(fishAdapter);
    }
}
