package com.hasanjaved.reportmate.activity;// OcrActivity.java
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.hasanjaved.reportmate.R;

public class OcrActivity extends AppCompatActivity {

    private ExecutorService cameraExecutor;
    private PreviewView previewView;
    private TextRecognizer textRecognizer;
    private TextView tvTemperature, tvTime, tvHumidity, tvConfidence;

    // Data class equivalent for readings
    public static class MeterReading {
        public String temperature;
        public String time;
        public String humidity;
        public float confidence;

        public MeterReading(String temperature, String time, String humidity, float confidence) {
            this.temperature = temperature;
            this.time = time;
            this.humidity = humidity;
            this.confidence = confidence;
        }
    }

    // Request camera permission
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startCamera();
                } else {
                    Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        initializeViews();
        cameraExecutor = Executors.newSingleThreadExecutor();
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        // Check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void initializeViews() {
        previewView = findViewById(R.id.previewView);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvTime = findViewById(R.id.tvTime);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvConfidence = findViewById(R.id.tvConfidence);
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Preview use case
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Image analysis for OCR
                ImageAnalysis imageAnalyzer = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalyzer.setAnalyzer(cameraExecutor, new TemperatureAnalyzer(this::displayReading));

                // Camera selector
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer);

            } catch (Exception exc) {
                Log.e("CameraX", "Use case binding failed", exc);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /**
     * Displays the OCR reading results on the UI
     * Updates TextViews with temperature, time, humidity and confidence values
     *
     * @param reading MeterReading object containing detected values
     */
    public void displayReading(MeterReading reading) {
        // Ensure UI updates run on main thread
        runOnUiThread(() -> {
            try {
                // Update temperature display
                if (reading.temperature != null && !reading.temperature.isEmpty()) {
                    tvTemperature.setText("Temperature: " + reading.temperature + "°C");
                    tvTemperature.setTextColor(getColor(android.R.color.holo_green_dark));
                } else {
                    tvTemperature.setText("Temperature: --");
                    tvTemperature.setTextColor(getColor(android.R.color.darker_gray));
                }

                // Update time display
                if (reading.time != null && !reading.time.isEmpty()) {
                    tvTime.setText("Time: " + reading.time);
                    tvTime.setTextColor(getColor(android.R.color.holo_blue_dark));
                } else {
                    tvTime.setText("Time: --");
                    tvTime.setTextColor(getColor(android.R.color.darker_gray));
                }

                // Update humidity display
                if (reading.humidity != null && !reading.humidity.isEmpty()) {
                    tvHumidity.setText("Humidity: " + reading.humidity + "%");
                    tvHumidity.setTextColor(getColor(android.R.color.holo_orange_dark));
                } else {
                    tvHumidity.setText("Humidity: --");
                    tvHumidity.setTextColor(getColor(android.R.color.darker_gray));
                }

                // Update confidence with color coding
                int confidencePercent = Math.round(reading.confidence * 100);
                tvConfidence.setText("Confidence: " + confidencePercent + "%");

                // Color code confidence level
                if (confidencePercent >= 80) {
                    tvConfidence.setTextColor(getColor(android.R.color.holo_green_dark));
                } else if (confidencePercent >= 50) {
                    tvConfidence.setTextColor(getColor(android.R.color.holo_orange_dark));
                } else {
                    tvConfidence.setTextColor(getColor(android.R.color.holo_red_dark));
                }

                // Log the results for debugging
                Log.d("OCR_RESULT", String.format(
                        "Temperature: %s°C, Time: %s, Humidity: %s%%, Confidence: %d%%",
                        reading.temperature != null ? reading.temperature : "--",
                        reading.time != null ? reading.time : "--",
                        reading.humidity != null ? reading.humidity : "--",
                        confidencePercent
                ));

                // Show toast for high confidence readings
                if (reading.confidence >= 0.8f &&
                        (reading.temperature != null || reading.time != null || reading.humidity != null)) {

                    StringBuilder toastMessage = new StringBuilder("Detected: ");
                    if (reading.temperature != null) toastMessage.append(reading.temperature).append("°C ");
                    if (reading.time != null) toastMessage.append(reading.time).append(" ");
                    if (reading.humidity != null) toastMessage.append(reading.humidity).append("% ");

                    Toast.makeText(this, toastMessage.toString().trim(), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Log.e("OCR_DISPLAY", "Error updating display", e);
                Toast.makeText(this, "Error displaying reading", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        textRecognizer.close();
    }
}

// Image Analyzer for OCR processing
class TemperatureAnalyzer implements ImageAnalysis.Analyzer {

    public interface OnReadingDetectedListener {
        void onReadingDetected(OcrActivity.MeterReading reading);
    }

    private final TextRecognizer textRecognizer;
    private final OnReadingDetectedListener onReadingDetected;
    private int frameSkipCounter = 0;

    public TemperatureAnalyzer(OnReadingDetectedListener listener) {
        this.onReadingDetected = listener;
        this.textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        // Process every 30th frame to reduce CPU usage
        frameSkipCounter++;
        if (frameSkipCounter % 30 != 0) {
            imageProxy.close();
            return;
        }

        @androidx.camera.core.ExperimentalGetImage
        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

            // Preprocess image for better OCR
            InputImage processedImage = preprocessImage(image);

            textRecognizer.process(processedImage)
                    .addOnSuccessListener(visionText -> {
                        OcrActivity.MeterReading reading = extractMeterData(visionText.getText());
                        if (reading.temperature != null || reading.time != null || reading.humidity != null) {
                            onReadingDetected.onReadingDetected(reading);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("OCR", "Text recognition failed", e))
                    .addOnCompleteListener(task -> imageProxy.close());
        } else {
            imageProxy.close();
        }
    }

    private InputImage preprocessImage(InputImage inputImage) {
        // For simplicity, return original image
        // In production, you would enhance contrast and convert to grayscale
        return inputImage;
    }

    private OcrActivity.MeterReading extractMeterData(String text) {
        Log.d("OCR_RAW", "Raw text: " + text);

        String temperature = null;
        String time = null;
        String humidity = null;
        float confidence = 0f;

        // Clean the text
        String cleanText = text.replaceAll("\\s+", " ").trim();

        // Enhanced temperature patterns for specific readings (31.3, 30.1, etc.)
        Pattern[] tempPatterns = {
                Pattern.compile("(3[0-9]\\.\\d)"),                    // 30.1, 31.3, etc.
                Pattern.compile("([2-4]\\d\\.\\d)"),                  // General range 20-49.9
                Pattern.compile("(\\d{1,2}\\.\\d)\\s*[°C]?"),         // Any decimal with optional °C
                Pattern.compile("(\\d{1,2})\\s*[°C]")                 // Whole numbers with °C
        };

        for (Pattern pattern : tempPatterns) {
            Matcher matcher = pattern.matcher(cleanText);
            if (matcher.find()) {
                String temp = matcher.group(1);
                try {
                    double tempValue = Double.parseDouble(temp);
                    // Expanded range for readings (10-60°C area)
                    if (tempValue >= 10.0 && tempValue <= 60.0) {
                        temperature = temp;
                        confidence += 0.3f;
                        break;
                    }
                } catch (NumberFormatException e) {
                    // Continue to next pattern
                }
            }
        }

        // Time patterns (HH:MM format) - looking for 12:46 pattern
        Pattern[] timePatterns = {
                Pattern.compile("(1[0-2]:[0-5]\\d)"),                 // 12:46 specific format
                Pattern.compile("(\\d{1,2}[:\\.]\\d{2})"),            // General HH:MM or HH.MM
                Pattern.compile("(\\d{1,2}\\s\\d{2})")                // HH MM with space
        };

        for (Pattern pattern : timePatterns) {
            Matcher matcher = pattern.matcher(cleanText);
            if (matcher.find()) {
                String timeMatch = matcher.group(1).replace(".", ":");
                // Validate time format
                String[] timeParts = timeMatch.split(":");
                if (timeParts.length == 2) {
                    try {
                        int hours = Integer.parseInt(timeParts[0]);
                        int minutes = Integer.parseInt(timeParts[1]);
                        if (hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59) {
                            time = timeMatch;
                            confidence += 0.3f;
                            break;
                        }
                    } catch (NumberFormatException e) {
                        // Continue to next pattern
                    }
                }
            }
        }

        // Humidity patterns - looking for 61% type readings
        Pattern[] humidityPatterns = {
                Pattern.compile("(6[0-9])\\s*[%％]"),                 // 60-69% range specifically
                Pattern.compile("([5-9]\\d)\\s*[%％]"),               // 50-99% range
                Pattern.compile("(\\d{1,3})\\s*[%％]"),               // Any percentage
                Pattern.compile("(\\d{1,2})(?=\\s|$)")               // Numbers that could be humidity
        };

        for (Pattern pattern : humidityPatterns) {
            Matcher matcher = pattern.matcher(cleanText);
            if (matcher.find()) {
                String hum = matcher.group(1);
                try {
                    int humValue = Integer.parseInt(hum);
                    if (humValue >= 20 && humValue <= 100) {
                        humidity = hum;
                        confidence += 0.4f;
                        break;
                    }
                } catch (NumberFormatException e) {
                    // Continue to next pattern
                }
            }
        }

        // Specific pattern for HTC-1 device layout
        // Temperature (31.3), Time (12:46), Humidity (61%)
        Pattern[] htcSpecificPatterns = {
                // Pattern for 3X.X temperature readings
                Pattern.compile("(3\\d\\.\\d).*?(1[0-2]:[0-5]\\d).*?(\\d{1,2})\\s*%?", Pattern.DOTALL),
                // General pattern for any temp with time and humidity
                Pattern.compile("(\\d{1,2}\\.\\d).*?(\\d{1,2}[:\\.]\\d{2}).*?(\\d{1,2})\\s*%?", Pattern.DOTALL)
        };

        for (Pattern pattern : htcSpecificPatterns) {
            Matcher deviceMatcher = pattern.matcher(cleanText);
            if (deviceMatcher.find()) {
                String detectedTemp = deviceMatcher.group(1);
                String detectedTime = deviceMatcher.group(2);
                if (detectedTime != null) {
                    detectedTime = detectedTime.replace(".", ":");
                }
                String detectedHumidity = deviceMatcher.group(3);

                // Validate detected values
                try {
                    double tempVal = Double.parseDouble(detectedTemp);
                    if (tempVal >= 10.0 && tempVal <= 60.0) {
                        temperature = detectedTemp;
                        time = detectedTime;
                        humidity = detectedHumidity;
                        confidence = 0.9f;
                        break;
                    }
                } catch (NumberFormatException e) {
                    // Continue to next pattern
                }
            }
        }

        // Additional validation for specific readings
        if (temperature != null) {
            try {
                double tempVal = Double.parseDouble(temperature);
                // Boost confidence for temperature readings in the 30-32°C range (actual readings)
                if (tempVal >= 30.0 && tempVal <= 32.0) {
                    confidence += 0.1f;
                }
            } catch (NumberFormatException e) {
                // Ignore parsing error
            }
        }

        return new OcrActivity.MeterReading(
                temperature,
                time,
                humidity,
                Math.min(confidence, 1.0f)
        );
    }
}

/*
activity_main.xml:

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <androidx.camera.view.PreviewView
        android:id="@+id/previewView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <TextView
            android:id="@+id/tvTemperature"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Temperature: --"
            android:textSize="18sp" />

        <TextView
            android:id="@+id/tvTime"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Time: --"
            android:textSize="18sp" />

        <TextView
            android:id="@+id/tvHumidity"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Humidity: --"
            android:textSize="18sp" />

        <TextView
            android:id="@+id/tvConfidence"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Confidence: 0%"
            android:textSize="14sp" />

    </LinearLayout>

</LinearLayout>

build.gradle (Module: app) dependencies:
implementation 'com.google.mlkit:text-recognition:16.0.0'
implementation "androidx.camera:camera-camera2:1.3.0"
implementation "androidx.camera:camera-lifecycle:1.3.0"
implementation "androidx.camera:camera-view:1.3.0"
implementation "androidx.camera:camera-core:1.3.0"

AndroidManifest.xml permissions:
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="true" />
*/