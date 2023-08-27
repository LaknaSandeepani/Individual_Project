package com.lk.individual_project;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class CameraPreviewActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int REQUEST_IMAGE_PICK = 101;

    private ImageView photoImageView;
    private Interpreter tflite;

    private TextView predictionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);
        predictionTextView = findViewById(R.id.predictionTextView);
        photoImageView = findViewById(R.id.photoImageView);

        try {
            tflite = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button galleryButton = findViewById(R.id.galleryButton);
        Button cameraButton = findViewById(R.id.cameraButton);

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCameraPermission()) {
                    openCamera();
                } else {
                    requestCameraPermission();
                }
            }
        });
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd("fish_species_classifier.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bitmap photoBitmap = (Bitmap) data.getExtras().get("data");
                photoImageView.setImageBitmap(photoBitmap);

                // Preprocess the image and classify
                classifyImage(photoBitmap);
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri selectedImageUri = data.getData();
                photoImageView.setImageURI(selectedImageUri);

                // Preprocess the image and classify
                Bitmap photoBitmap = loadBitmapFromUri(selectedImageUri);
                classifyImage(photoBitmap);
            }
        }
    }

    private Bitmap loadBitmapFromUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void classifyImage(Bitmap bitmap) {
        if (tflite != null) {
            // Preprocess the image
            float[][][][] inputArray = preprocessImage(bitmap);

            float[][] outputArray = new float[1][31];


            tflite.run(inputArray, outputArray);

            // Process the output and display prediction
            int predictedClassIndex = argmax(outputArray[0]);
            float predictedPercentage = outputArray[0][predictedClassIndex] * 100;
            String predictedClassName = getClassName(predictedClassIndex);

            // Update the TextView with the predicted species and percentage
            String predictionText = "Predicted fish species: " + predictedClassName +
                    "\nConfidence: " + String.format("%.2f", predictedPercentage) + "%";
            predictionTextView.setText(predictionText);
        }
    }

    private float[][][][] preprocessImage(Bitmap bitmap) {

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);

        float[][][][] inputArray = new float[1][100][100][3];
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                int pixel = resizedBitmap.getPixel(x, y);
                // Normalize pixel values and set to input array
                inputArray[0][x][y][0] = (Color.red(pixel) / 255.0f);
                inputArray[0][x][y][1] = (Color.green(pixel) / 255.0f);
                inputArray[0][x][y][2] = (Color.blue(pixel) / 255.0f);
            }
        }
        return inputArray;
    }

    private int argmax(float[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private String getClassName(int classIndex) {

        String[] classLabels = {
                "Bangus", "Big Head Carp", "Black Spotted Barb", "Catfish(Poisonous Fish)", "Climbing Perch", "Fourfinger Threadfin",
                "Freshwater Eel", "Glass Perchlet", "Goby", "Gold Fish", "Gourami", "Grass Carp",
                "Green Spotted Puffer(Poisonous Fish)", "Indian Carp", "Indo-Pacific Tarpon", "Jaguar Gapote", "Janitor Fish(Non-Poisonous Fish)",
                "Knifefish(Poisonous Fish)", "Long-Snouted Pipefish", "Mosquito Fish", "Mudfish(Poisonous Fish)", "Mullet", "Pangasius",
                "Perch", "Scat Fish", "Silver Barb", "Silver Carp", "Silver Perch", "Snakehead(Poisonous Fish)", "Tenpounder", "Tilapia"
        };

        if (classIndex >= 0 && classIndex < classLabels.length) {
            return classLabels[classIndex];
        } else {
            return "Unknown";
        }
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tflite != null) {
            tflite.close();
        }
    }
}
