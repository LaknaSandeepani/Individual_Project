package com.lk.individual_project;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish);

        recyclerView = findViewById(R.id.fishlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fishList = new ArrayList<>();
        fishAdapter = new FishAdapter(this, fishList);
        recyclerView.setAdapter(fishAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("fish");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fishList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Fish fish = snapshot.getValue(Fish.class);
                    fishList.add(fish);
                }
                fishAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }
}
