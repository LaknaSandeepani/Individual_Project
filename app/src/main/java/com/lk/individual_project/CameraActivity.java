package com.lk.individual_project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@ExperimentalGetImage public class CameraActivity extends AppCompatActivity {
    private PreviewView previewView;
    private Interpreter tfliteInterpreter;
    private static final int NUM_CLASSES = 31; // Replace with the number of fish species classes
    private String[] fishSpeciesLabels; // Replace with the array of fish species names

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private ProcessCameraProvider cameraProvider;
    private Executor cameraExecutor = Executors.newSingleThreadExecutor();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);

        // Load the TensorFlow Lite model
        try {
            tfliteInterpreter = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load the labels (fish species names)
        loadLabels();

        // Request camera permission
        checkCameraPermission();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                showErrorMessage("Camera permission denied.");
            }
        }
    }

    private void loadLabels() {
        // Load the fish species labels (names) from a text file in the assets folder
        try {
            fishSpeciesLabels = loadLabelsFromAssets("fish_species_labels.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] loadLabelsFromAssets(String filename) throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd(filename);
        FileInputStream fileInputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        fileChannel.close();
        return new String(buffer.array(), "UTF-8").split("\n");
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd("fish_species_classifier.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                // Get camera provider
                cameraProvider = cameraProviderFuture.get();

                // Set up the Preview use case with rotation
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider()); // Use PreviewView's SurfaceProvider

                // Set up the ImageAnalysis use case
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                // Add the @ExperimentalGetImage annotation here
                imageAnalysis.setAnalyzer(cameraExecutor, this::processImage);

                // Select the back camera
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Unbind any previous use cases before rebinding
                cameraProvider.unbindAll();

                // Bind the use cases to the camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                showErrorMessage("Error starting camera.");
            }
        }, ContextCompat.getMainExecutor(this));
    }



    private void releaseCamera() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
    }

    private void processImage(@NonNull ImageProxy imageProxy) {
        Bitmap capturedBitmap = imageProxyToBitmap(imageProxy);

        // Preprocess the image (e.g., resize, normalize) to match the model's input requirements
        Bitmap processedBitmap = preprocessImage(capturedBitmap);

        // Pass the processed image to the image recognition component (TensorFlow Lite model)
        runInference(processedBitmap);

        imageProxy.close();
    }

    private Bitmap imageToBitmap(Image image) {
        if (image == null) {
            return null;
        }

        // Convert the image to a byte array
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        // Decode the byte array into a Bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true; // Ensure the Bitmap is mutable for further processing
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    private Bitmap imageProxyToBitmap(ImageProxy imageProxy) {
        // Check if the imageProxy is null or not
        if (imageProxy == null || imageProxy.getImage() == null) {
            return null;
        }

        Image image = imageProxy.getImage();
        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
        Bitmap bitmap = imageToBitmap(image);

        if (bitmap == null) {
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    private void captureImage() {
        // Not needed for real-time identification
    }

    private Bitmap preprocessImage(Bitmap bitmap) {
        // Check if the input bitmap is null
        if (bitmap == null) {
            return null;
        }

        // Implement image preprocessing code here
        // For example, you can resize the image to the input size expected by the model
        int modelInputWidth = 100; // Replace with the model's input width
        int modelInputHeight = 100; // Replace with the model's input height

        // Ensure the bitmap is not null before attempting to resize
        if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
            return null;
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, modelInputWidth, modelInputHeight, false);

        // You may also need to normalize the pixel values to match the model's input requirements

        return resizedBitmap;
    }

    private void runInference(Bitmap bitmap) {
        // Convert the Bitmap to a ByteBuffer (matching the model's input requirements)
        ByteBuffer inputBuffer = preprocessImageAndConvertToByteBuffer(bitmap);

        // Check if the input buffer is not null before running the inference
        if (inputBuffer == null) {
            return;
        }

        // Allocate space for the output predictions
        float[][] output = new float[1][NUM_CLASSES];

        // Run inference
        tfliteInterpreter.run(inputBuffer, output);

        // Process the output to get the predicted class (fish species)
        int predictedClassIndex = processOutput(output);

        // Get the predicted fish species name based on the class index
        String predictedFishSpecies = getFishSpeciesName(predictedClassIndex);

        // Display the result or navigate to the ResultsActivity to show the identified fish species
        showResult(predictedFishSpecies);
    }


    private ByteBuffer preprocessImageAndConvertToByteBuffer(Bitmap bitmap) {
        // Check if the input bitmap is null
        if (bitmap == null) {
            return null;
        }

        // Implement image preprocessing code here
        // For example, you can normalize the pixel values and convert the Bitmap to a ByteBuffer
        int modelInputWidth = 100; // Replace with the model's input width
        int modelInputHeight = 100; // Replace with the model's input height
        int channels = 3; // Replace with the model's input channels (e.g., 3 for RGB)

        int bitmapSize = modelInputWidth * modelInputHeight * channels;

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bitmapSize * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        // Ensure the bitmap is not null before attempting to get its pixels
        if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
            return null;
        }

        int[] pixels = new int[modelInputWidth * modelInputHeight];
        bitmap.getPixels(pixels, 0, modelInputWidth, 0, 0, modelInputWidth, modelInputHeight);

        int pixel = 0;
        for (int i = 0; i < modelInputWidth; i++) {
            for (int j = 0; j < modelInputHeight; j++) {
                int pixelValue = pixels[pixel++];

                // Extract the RGB channels from the pixel value
                int r = (pixelValue >> 16) & 0xFF;
                int g = (pixelValue >> 8) & 0xFF;
                int b = pixelValue & 0xFF;

                // Normalize and convert the pixel values
                float normalizedR = (r / 255.0f);
                float normalizedG = (g / 255.0f);
                float normalizedB = (b / 255.0f);

                // Add the normalized pixel values to the ByteBuffer
                byteBuffer.putFloat(normalizedR);
                byteBuffer.putFloat(normalizedG);
                byteBuffer.putFloat(normalizedB);
            }
        }

        return byteBuffer;
    }


    private int processOutput(float[][] output) {
        // Implement post-processing code here
        // The output is usually an array of class probabilities
        // Find the index of the highest probability and return it as the predicted class index
        int maxIndex = 0;
        for (int i = 1; i < output[0].length; i++) {
            if (output[0][i] > output[0][maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private String getFishSpeciesName(int classIndex) {
        // Implement code to map class index to fish species name
        // Return the corresponding fish species name based on the index
        return fishSpeciesLabels[classIndex];
    }

    private void showResult(String predictedFishSpecies) {
        // Display the result or navigate to the ResultsActivity
        // You can choose how you want to show the identified fish species
        // For example, you can display it on a TextView, overlay it on the camera preview, etc.
        runOnUiThread(() -> Toast.makeText(this, "Predicted Fish Species: " + predictedFishSpecies, Toast.LENGTH_SHORT).show());
    }

    private void showErrorMessage(String message) {
        // Display an error message (e.g., as a Toast) for camera integration
        // You can modify this to show an appropriate error message on the screen
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }
}
