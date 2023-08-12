package com.lk.individual_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ReportFishActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView reportFishImage;
    private Button selectImageButton;
    private EditText reportDescriptionEditText;
    private Button submitButton;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_fish);

        // Initialize your views
        reportFishImage = findViewById(R.id.reportFishImage);
        selectImageButton = findViewById(R.id.selectImageButton);
        reportDescriptionEditText = findViewById(R.id.reportDescription);
        submitButton = findViewById(R.id.submitButton);

        // Set click listener for the "Select Image" button
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        // Set click listener for the "Submit" button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReport();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void submitReport() {
        String description = reportDescriptionEditText.getText().toString();

        if (selectedImageUri != null && !description.isEmpty()) {
            // Upload the selected image to Firebase Storage
            uploadImageToStorage(selectedImageUri, description);
        } else {
            Toast.makeText(this, "Please select an image and provide a description", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToStorage(Uri imageUri, String description) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");

        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully
            // Now, get the download URL and store the report details in the database
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();

                // Store the report details in the database
                storeReportInDatabase(imageUrl, description);

                // Show a success message
                Toast.makeText(this, "Report submitted successfully", Toast.LENGTH_SHORT).show();

                // Clear fields
                clearFields();

                // Navigate to RegisterActivity
                navigateToRegisterActivity();
            });
        }).addOnFailureListener(e -> {
            // Handle failure
            Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
        });
    }

    private void storeReportInDatabase(String imageUrl, String description) {
        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("reports");
        String reportId = reportsRef.push().getKey();

        Report report = new Report(reportId, imageUrl, description);

        reportsRef.child(reportId).setValue(report)
                .addOnSuccessListener(aVoid -> {
                    // Report details stored successfully
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    private void clearFields() {
        reportDescriptionEditText.setText("");
        reportFishImage.setImageResource(0); // Clear image
        selectedImageUri = null;
    }

    private void navigateToRegisterActivity() {
        Intent intent = new Intent(this, HomeScreenActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            reportFishImage.setImageURI(selectedImageUri);
        }
    }
}
