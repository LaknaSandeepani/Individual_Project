package com.lk.individual_project;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class FishActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FishAdapter fishAdapter;
    private List<Fish> fishList;
    private List<Fish> originalFishList;
    private DatabaseReference databaseReference;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish);

        recyclerView = findViewById(R.id.fishlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fishList = new ArrayList<>();
        originalFishList = new ArrayList<>(); // Initialize the original list

        fishAdapter = new FishAdapter(this, fishList);
        recyclerView.setAdapter(fishAdapter);

        searchView = findViewById(R.id.searchview);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the fishList based on the search text
                filterFishList(newText);
                return true;
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("fish");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fishList.clear();
                originalFishList.clear(); // Clear the original list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Fish fish = snapshot.getValue(Fish.class);
                    fishList.add(fish);
                    originalFishList.add(fish); // Populate the original list
                }
                fishAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private void filterFishList(String searchText) {
        fishList.clear(); // Clear the current list

        for (Fish fish : originalFishList) {
            if (fish.getName().toLowerCase().contains(searchText.toLowerCase())) {
                fishList.add(fish);
            }
        }

        fishAdapter.notifyDataSetChanged();
    }
}
