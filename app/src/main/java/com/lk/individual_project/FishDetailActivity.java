package com.lk.individual_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FishDetailActivity extends AppCompatActivity {

    private TextView fishNameTextView;
    private TextView fishDescriptionTextView;
    private ImageView fishImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_detail);

        // Initialize your views
        fishNameTextView = findViewById(R.id.detailFishName);
        fishDescriptionTextView = findViewById(R.id.detailFishDescription);
        fishImageView = findViewById(R.id.detailFishImage);

        // Get the fish data from the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("fishName")) {
            String fishName = intent.getStringExtra("fishName");
            // Fetch more details about the fish using fishName
            getFishDetailsByName(fishName);
        }
    }

    private void getFishDetailsByName(String fishName) {
        DatabaseReference fishRef = FirebaseDatabase.getInstance().getReference("fish");

        // Query the database to fetch the fish with the given name
        // Assuming you have a "name" field in your database to match the fish name
        fishRef.orderByChild("name").equalTo(fishName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the snapshot has data
                if (dataSnapshot.exists()) {
                    // Assuming you have only one match, get the first child
                    DataSnapshot fishSnapshot = dataSnapshot.getChildren().iterator().next();
                    Fish fish = fishSnapshot.getValue(Fish.class);

                    // Set the fish name to the TextView
                    fishNameTextView.setText(fish.getName());

                    // Set the fish description to the TextView
                    fishDescriptionTextView.setText(fish.getDescription());

                    // Load the fish image using Glide
                    Glide.with(FishDetailActivity.this).load(fish.getImageUrl()).into(fishImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
}
