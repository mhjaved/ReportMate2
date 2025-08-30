//package com.hasanjaved.reportmate.fragment;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.graphics.Typeface;
//import android.media.ExifInterface;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.camera.core.AspectRatio;
//import androidx.camera.core.Camera;
//import androidx.camera.core.CameraSelector;
//import androidx.camera.core.ImageCapture;
//import androidx.camera.core.ImageCaptureException;
//import androidx.camera.core.Preview;
//import androidx.camera.lifecycle.ProcessCameraProvider;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.google.common.util.concurrent.ListenableFuture;
//import com.hasanjaved.reportmate.R;
//import com.hasanjaved.reportmate.databinding.FragmentCameraOcrBinding;
//import com.hasanjaved.reportmate.listeners.CameraFragmentClickListener;
//import com.hasanjaved.reportmate.ocr_files.TesseractOcrHelper;
//import com.hasanjaved.reportmate.utility.Utility;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Executors;
//
//public class FragmentCameraOcr extends Fragment implements TesseractOcrHelper.OcrCallback {
//
//    private int cameraFacing = CameraSelector.LENS_FACING_BACK;
//    private FragmentCameraOcrBinding binding;
//    private ImageCapture imageCapture;
//    private Activity activity;
//    private CameraFragmentClickListener fragmentClickListener;
//    private String imageLink = "";
//    private ImageView imageViewForSettingImage;
//    private String imageName, subFolder;
//
//    // OCR Helper
//    private TesseractOcrHelper ocrHelper;
//
//    // OCR Results
//    private String detectedTemperature = "";
//    private String detectedHumidity = "";
//    private String rawOcrText = "";
//
//    public void setFragmentClickListener(CameraFragmentClickListener fragmentClickListener, ImageView imageViewForSettingImage,
//                                         String imageName, String subFolder) {
//        this.fragmentClickListener = fragmentClickListener;
//        this.imageViewForSettingImage = imageViewForSettingImage;
//        this.imageName = imageName;
//        this.subFolder = subFolder;
//        Utility.showLog("imageName: " + imageName);
//        Utility.showLog("subFolder: " + subFolder);
//    }
//
//    private final ActivityResultLauncher<String> permissionLauncher =
//            registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
//                try {
//                    if (result) {
//                        startCamera(cameraFacing);
//                    } else {
//                        Toast.makeText(activity, "Camera permission denied", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    Utility.showLog("Error in permission callback: " + e.getMessage());
//                    showToastAndClose("Something went wrong. Please try again.");
//                }
//            });
//
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    public static FragmentCameraOcr newInstance(String param1, String param2) {
//        FragmentCameraOcr fragment = new FragmentCameraOcr();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        try {
//            activity = getActivity();
//            // Initialize OCR Helper
//            ocrHelper = new TesseractOcrHelper(activity);
//            ocrHelper.setCallback(this);
//            ocrHelper.initialize();
//        } catch (Exception e) {
//            Utility.showLog("Error in onCreate: " + e.getMessage());
//        }
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//
//        try {
//            binding = FragmentCameraOcrBinding.inflate(inflater, container, false);
//            View view = binding.getRoot();
//
//            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
//                    != PackageManager.PERMISSION_GRANTED) {
//                permissionLauncher.launch(Manifest.permission.CAMERA);
//            } else {
//                startCamera(cameraFacing);
//            }
//
//            // Retake button - hide image and allow new capture
//            binding.btnRetake.setOnClickListener(view1 -> {
//                try {
//                    binding.ivShowImage.setVisibility(View.GONE);
//                    binding.btnRetake.setEnabled(false);
//                    binding.btnPerformOCR.setEnabled(false);
//                    // Clear previous OCR results
//                    clearOcrResults();
//                } catch (Exception e) {
//                    Utility.showLog("Error in retake button: " + e.getMessage());
//                    showToastAndClose("Something went wrong. Please try again.");
//                }
//            });
//
//            // OCR button - perform text recognition on captured image
//            binding.btnPerformOCR.setOnClickListener(view1 -> {
//                try {
//                    if (!imageLink.isEmpty()) {
//                        if (ocrHelper.isInitialized()) {
//                            performOCR(imageLink);
//                        } else {
//                            Toast.makeText(activity, "OCR engine is still initializing, please wait...", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(activity, "No image to process", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    Utility.showLog("Error in OCR button: " + e.getMessage());
//                    showToastAndClose("Something went wrong. Please try again.");
//                }
//            });
//
//            // Back button - close fragment
//            binding.btnBack.setOnClickListener(view1 -> {
//                try {
//                    closeFragment();
//                } catch (Exception e) {
//                    Utility.showLog("Error in back button: " + e.getMessage());
//                    showToastAndClose("Something went wrong. Please try again.");
//                }
//            });
//
//            return view;
//        } catch (Exception e) {
//            Utility.showLog("Error in onCreateView: " + e.getMessage());
//            showToastAndClose("Something went wrong. Please try again.");
//            return null;
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        try {
//            super.onDestroyView();
//            // Destroy OCR Helper
//            if (ocrHelper != null) {
//                ocrHelper.destroy();
//                ocrHelper = null;
//            }
//            binding = null;
//        } catch (Exception e) {
//            Utility.showLog("Error in onDestroyView: " + e.getMessage());
//        }
//    }
//
//    // OCR CALLBACK IMPLEMENTATIONS
//    @Override
//    public void onInitializationComplete(boolean success) {
//        try {
//            if (success) {
//                Utility.showLog("OCR Helper initialized successfully");
//                if (activity != null) {
//                    activity.runOnUiThread(() -> {
//                        Toast.makeText(activity, "OCR engine ready", Toast.LENGTH_SHORT).show();
//                    });
//                }
//            } else {
//                Utility.showLog("OCR Helper initialization failed");
//                if (activity != null) {
//                    activity.runOnUiThread(() -> {
//                        Toast.makeText(activity, "OCR engine initialization failed", Toast.LENGTH_SHORT).show();
//                    });
//                }
//            }
//        } catch (Exception e) {
//            Utility.showLog("Error in onInitializationComplete: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public void onOcrComplete(TesseractOcrHelper.OcrResult result) {
//        try {
//            if (activity != null) {
//                activity.runOnUiThread(() -> {
//                    try {
//                        if (result.success) {
//                            // Update OCR results
//                            rawOcrText = result.rawText;
//                            detectedTemperature = result.temperature;
//                            detectedHumidity = result.humidity;
//
//                            // Show results and save
//                            showOcrResults();
//                            saveImageWithOcrData();
//                        } else {
//                            Toast.makeText(activity, "OCR failed: " + result.error, Toast.LENGTH_LONG).show();
//                            Utility.showLog("OCR failed: " + result.error);
//                        }
//                    } catch (Exception e) {
//                        Utility.showLog("Error processing OCR results: " + e.getMessage());
//                        Toast.makeText(activity, "Error processing OCR results", Toast.LENGTH_SHORT).show();
//                    } finally {
//                        resetOcrButton();
//                    }
//                });
//            }
//        } catch (Exception e) {
//            Utility.showLog("Error in onOcrComplete: " + e.getMessage());
//        }
//    }
//
//    // OCR METHODS
//    private void performOCR(String imagePath) {
//        try {
//            // Show loading state
//            binding.btnPerformOCR.setEnabled(false);
//            binding.btnPerformOCR.setText("Processing...");
//
//            File imageFile = new File(imagePath);
//            if (!imageFile.exists()) {
//                Toast.makeText(activity, "Image file not found", Toast.LENGTH_SHORT).show();
//                resetOcrButton();
//                return;
//            }
//
//            // Use OCR Helper to perform OCR
//            ocrHelper.performOCR(imagePath);
//
//        } catch (Exception e) {
//            Utility.showLog("Error in performOCR: " + e.getMessage());
//            Toast.makeText(activity, "OCR processing failed", Toast.LENGTH_SHORT).show();
//            resetOcrButton();
//        }
//    }
//
//    private void showOcrResults() {
//        try {
//            StringBuilder results = new StringBuilder();
//            results.append("OCR Results:\n\n");
//
//            if (!detectedTemperature.isEmpty()) {
//                results.append("Temperature: ").append(detectedTemperature).append("\n");
//            } else {
//                results.append("Temperature: Not detected\n");
//            }
//
//            if (!detectedHumidity.isEmpty()) {
//                results.append("Humidity: ").append(detectedHumidity).append("\n");
//            } else {
//                results.append("Humidity: Not detected\n");
//            }
//
//            results.append("\nRaw Text:\n").append(rawOcrText);
//
//            // Show results in a toast
//            Toast.makeText(activity,
//                    String.format("Detected - Temp: %s, Humidity: %s",
//                            detectedTemperature.isEmpty() ? "N/A" : detectedTemperature,
//                            detectedHumidity.isEmpty() ? "N/A" : detectedHumidity),
//                    Toast.LENGTH_LONG).show();
//
//            Utility.showLog("OCR Results: " + results.toString());
//
//        } catch (Exception e) {
//            Utility.showLog("Error showing OCR results: " + e.getMessage());
//        }
//    }
//
//    private void saveImageWithOcrData() {
//        try {
//            if (!imageLink.isEmpty()) {
//                // Create OCR data bundle to pass with the image
//                Bundle ocrData = new Bundle();
//                ocrData.putString("temperature", detectedTemperature);
//                ocrData.putString("humidity", detectedHumidity);
//                ocrData.putString("rawText", rawOcrText);
//
//                // Call the listener with OCR data
//                if (fragmentClickListener != null) {
//                    fragmentClickListener.onSaveButtonPressed(imageViewForSettingImage, imageLink, imageName, subFolder);
//                }
//                closeFragment();
//            }
//        } catch (Exception e) {
//            Utility.showLog("Error saving image with OCR data: " + e.getMessage());
//        }
//    }
//
//    private void clearOcrResults() {
//        detectedTemperature = "";
//        detectedHumidity = "";
//        rawOcrText = "";
//    }
//
//    private void resetOcrButton() {
//        try {
//            if (binding != null) {
//                binding.btnPerformOCR.setEnabled(true);
//                binding.btnPerformOCR.setText("Perform OCR");
//            }
//        } catch (Exception e) {
//            Utility.showLog("Error resetting OCR button: " + e.getMessage());
//        }
//    }
//
//    private void closeFragment() {
//        try {
//            getParentFragmentManager().popBackStack();
//        } catch (Exception e) {
//            Utility.showLog("Error closing fragment: " + e.getMessage());
//            try {
//                if (activity != null) {
//                    activity.onBackPressed();
//                }
//            } catch (Exception ex) {
//                Utility.showLog("Error with alternative fragment closure: " + ex.getMessage());
//            }
//        }
//    }
//
//    private void showToastAndClose(String message) {
//        try {
//            if (activity != null) {
//                activity.runOnUiThread(() -> {
//                    try {
//                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
//                        closeFragment();
//                    } catch (Exception e) {
//                        Utility.showLog("Error showing toast and closing: " + e.getMessage());
//                    }
//                });
//            }
//        } catch (Exception e) {
//            Utility.showLog("Error in showToastAndClose: " + e.getMessage());
//        }
//    }
//
//    // CAMERA FUNCTIONALITY METHODS
//    private void startCamera(int cameraFacing) {
//        try {
//            int aspectRatio = aspectRatio(binding.imagePreview.getWidth(), binding.imagePreview.getHeight());
//            ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
//                    ProcessCameraProvider.getInstance(activity);
//
//            cameraProviderFuture.addListener(() -> {
//                try {
//                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
//                    Preview preview = new Preview.Builder()
//                            .setTargetAspectRatio(aspectRatio)
//                            .build();
//
//                    imageCapture = new ImageCapture.Builder()
//                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
//                            .setTargetAspectRatio(aspectRatio)
//                            .setTargetRotation(activity.getWindowManager().getDefaultDisplay().getRotation())
//                            .build();
//
//                    CameraSelector cameraSelector = new CameraSelector.Builder()
//                            .requireLensFacing(cameraFacing).build();
//
//                    cameraProvider.unbindAll();
//                    Camera camera = cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview, imageCapture);
//
//                    binding.btnCapture.setOnClickListener(v -> {
//                        try {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
//                                    ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                                takePicture(imageCapture);
//                            } else {
//                                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                            }
//                        } catch (Exception e) {
//                            Utility.showLog("Error in capture button: " + e.getMessage());
//                            showToastAndClose("Something went wrong. Please try again.");
//                        }
//                    });
//
//                    preview.setSurfaceProvider(binding.imagePreview.getSurfaceProvider());
//
//                } catch (ExecutionException | InterruptedException e) {
//                    Utility.showLog("Error setting up camera: " + e.getMessage());
//                    e.printStackTrace();
//                    showToastAndClose("Camera setup failed. Please try again.");
//                } catch (Exception e) {
//                    Utility.showLog("Unexpected error in camera setup: " + e.getMessage());
//                    showToastAndClose("Something went wrong. Please try again.");
//                }
//            }, ContextCompat.getMainExecutor(activity));
//        } catch (Exception e) {
//            Utility.showLog("Error starting camera: " + e.getMessage());
//            showToastAndClose("Camera failed to start. Please try again.");
//        }
//    }
//
//    private void takePicture(ImageCapture imageCapture) {
//        try {
//            File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
//            File targetDir = new File(documentsDir, subFolder);
//            if (!targetDir.exists()) {
//                boolean created = targetDir.mkdirs();
//                Utility.showLog("Target directory created: " + created + " - " + targetDir.getAbsolutePath());
//            }
//
//            File finalFile = new File(targetDir, imageName + ".jpg");
//
//            if (finalFile.exists()) {
//                boolean deleted = finalFile.delete();
//                Utility.showLog("Existing file deleted before capture: " + deleted);
//            }
//
//            Utility.showLog("Taking picture - Final path: " + finalFile.getAbsolutePath());
//
//            ImageCapture.OutputFileOptions outputOptions =
//                    new ImageCapture.OutputFileOptions.Builder(finalFile).build();
//
//            imageCapture.takePicture(outputOptions, Executors.newSingleThreadExecutor(),
//                    new ImageCapture.OnImageSavedCallback() {
//                        @Override
//                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                            try {
//                                Utility.showLog("Image saved successfully to: " + finalFile.getAbsolutePath());
//
//                                if (finalFile.exists()) {
//                                    Utility.showLog("File verification - Name: " + finalFile.getName() + ", Size: " + finalFile.length());
//                                }
//
//                                processImageAndUpdateUI(finalFile.getPath());
//                            } catch (Exception e) {
//                                Utility.showLog("Error in onImageSaved callback: " + e.getMessage());
//                                showToastAndClose("Image processing failed. Please try again.");
//                            }
//                        }
//
//                        @Override
//                        public void onError(@NonNull ImageCaptureException exception) {
//                            Utility.showLog("Error taking picture: " + exception.getMessage());
//                            activity.runOnUiThread(() -> {
//                                try {
//                                    showToastAndClose("Error capturing image. Please try again.");
//                                } catch (Exception e) {
//                                    Utility.showLog("Error in capture error callback: " + e.getMessage());
//                                }
//                            });
//                        }
//                    });
//        } catch (Exception e) {
//            Utility.showLog("Error in takePicture: " + e.getMessage());
//            showToastAndClose("Failed to capture image. Please try again.");
//        }
//    }
//
//    private void showImage(String imagePath) {
//        try {
//            binding.ivShowImage.setVisibility(View.VISIBLE);
//            Utility.showLog("Showing image: " + imagePath);
//            binding.btnRetake.setEnabled(true);
//            binding.btnPerformOCR.setEnabled(true);
//
//            File imageFile = new File(imagePath);
//            if (!imageFile.exists()) {
//                Utility.showLog("Image file does not exist: " + imagePath);
//                binding.ivShowImage.setImageResource(R.drawable.ic_image_24);
//                return;
//            }
//
//            Glide.with(activity)
//                    .load(imageFile)
//                    .skipMemoryCache(true)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .placeholder(R.drawable.ic_image_24)
//                    .error(R.drawable.ic_image_24)
//                    .into(binding.ivShowImage);
//
//            Utility.showLog("Image loading initiated with Glide");
//
//        } catch (Exception e) {
//            Utility.showLog("Error showing image: " + e.getMessage());
//            showToastAndClose("Failed to display image. Please try again.");
//        }
//    }
//
//    private void processImageAndUpdateUI(String originalImagePath) {
//        try {
//            new Thread(() -> {
//                try {
//                    Bitmap originalBitmap = BitmapFactory.decodeFile(originalImagePath);
//                    if (originalBitmap == null) {
//                        Utility.showLog("Failed to load bitmap from: " + originalImagePath);
//                        updateUIWithImage(originalImagePath);
//                        return;
//                    }
//
//                    Bitmap rotatedBitmap = rotateImageIfRequired(originalBitmap, originalImagePath);
//                    Bitmap croppedBitmap = cropToPreviewAspectRatio(rotatedBitmap);
//                    Bitmap finalBitmap = addTimestampOverlay(croppedBitmap);
//
//                    updateUIWithImage(originalImagePath);
//
//                    if (originalBitmap != rotatedBitmap) originalBitmap.recycle();
//                    if (rotatedBitmap != croppedBitmap) rotatedBitmap.recycle();
//                    if (croppedBitmap != finalBitmap) croppedBitmap.recycle();
//                    finalBitmap.recycle();
//
//                } catch (Exception e) {
//                    Utility.showLog("Error processing image: " + e.getMessage());
//                    e.printStackTrace();
//                    activity.runOnUiThread(() -> showToastAndClose("Image processing failed. Please try again."));
//                }
//            }).start();
//        } catch (Exception e) {
//            Utility.showLog("Error creating image processing thread: " + e.getMessage());
//            showToastAndClose("Image processing failed. Please try again.");
//        }
//    }
//
//    private void updateUIWithImage(String filePath) {
//        try {
//            activity.runOnUiThread(() -> {
//                try {
//                    imageLink = filePath;
//                    Utility.showLog("Image ready to display: " + filePath);
//                    showImage(filePath);
//                } catch (Exception e) {
//                    Utility.showLog("Error updating UI with image: " + e.getMessage());
//                    showToastAndClose("Something went wrong. Please try again.");
//                }
//            });
//            startCamera(cameraFacing);
//        } catch (Exception e) {
//            Utility.showLog("Error in updateUIWithImage: " + e.getMessage());
//            showToastAndClose("Something went wrong. Please try again.");
//        }
//    }
//
//    private Bitmap rotateImageIfRequired(Bitmap bitmap, String imagePath) {
//        try {
//            ExifInterface exif = new ExifInterface(imagePath);
//            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//            Matrix matrix = new Matrix();
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    matrix.postRotate(90);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    matrix.postRotate(180);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    matrix.postRotate(270);
//                    break;
//                default:
//                    return bitmap;
//            }
//
//            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//        } catch (IOException | OutOfMemoryError e) {
//            Utility.showLog("Error rotating image: " + e.getMessage());
//            e.printStackTrace();
//            return bitmap;
//        } catch (Exception e) {
//            Utility.showLog("Unexpected error rotating image: " + e.getMessage());
//            return bitmap;
//        }
//    }
//
//    private Bitmap cropToPreviewAspectRatio(Bitmap bitmap) {
//        try {
//            int previewWidth = binding.imagePreview.getWidth();
//            int previewHeight = binding.imagePreview.getHeight();
//
//            if (previewWidth == 0 || previewHeight == 0) {
//                previewWidth = 16;
//                previewHeight = 9;
//            }
//
//            float previewAspectRatio = (float) previewWidth / previewHeight;
//            float bitmapAspectRatio = (float) bitmap.getWidth() / bitmap.getHeight();
//
//            int cropWidth, cropHeight;
//            int startX = 0, startY = 0;
//
//            if (bitmapAspectRatio > previewAspectRatio) {
//                cropHeight = bitmap.getHeight();
//                cropWidth = (int) (cropHeight * previewAspectRatio);
//                startX = (bitmap.getWidth() - cropWidth) / 2;
//            } else {
//                cropWidth = bitmap.getWidth();
//                cropHeight = (int) (cropWidth / previewAspectRatio);
//                startY = (bitmap.getHeight() - cropHeight) / 2;
//            }
//
//            return Bitmap.createBitmap(bitmap, startX, startY, cropWidth, cropHeight);
//        } catch (OutOfMemoryError e) {
//            Utility.showLog("Out of memory error cropping image: " + e.getMessage());
//            return bitmap;
//        } catch (Exception e) {
//            Utility.showLog("Error cropping image: " + e.getMessage());
//            return bitmap;
//        }
//    }
//
//    private Bitmap addTimestampOverlay(Bitmap bitmap) {
//        try {
//            Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//            Canvas canvas = new Canvas(mutableBitmap);
//
//            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy h:mm:ss a", Locale.getDefault());
//            String currentDateTime = dateFormat.format(new Date());
//
//            Paint textPaint = new Paint();
//            textPaint.setColor(Color.WHITE);
//            textPaint.setTextSize(bitmap.getWidth() * 0.025f);
//            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
//            textPaint.setAntiAlias(true);
//
//            Rect textBounds = new Rect();
//            textPaint.getTextBounds(currentDateTime, 0, currentDateTime.length(), textBounds);
//
//            Paint backgroundPaint = new Paint();
//            backgroundPaint.setColor(Color.argb(128, 0, 0, 0));
//            backgroundPaint.setAntiAlias(true);
//
//            int padding = (int) (bitmap.getWidth() * 0.01f);
//            int x = bitmap.getWidth() - textBounds.width() - padding * 2;
//            int y = bitmap.getHeight() - padding * 2;
//
//            canvas.drawRect(
//                    x - padding,
//                    y - textBounds.height() - padding,
//                    bitmap.getWidth(),
//                    bitmap.getHeight(),
//                    backgroundPaint
//            );
//
//            canvas.drawText(currentDateTime, x, y - padding, textPaint);
//
//            return mutableBitmap;
//        } catch (OutOfMemoryError e) {
//            Utility.showLog("Out of memory error adding timestamp: " + e.getMessage());
//            return bitmap;
//        } catch (Exception e) {
//            Utility.showLog("Error adding timestamp: " + e.getMessage());
//            return bitmap;
//        }
//    }
//
//    private int aspectRatio(int width, int height) {
//        try {
//            double previewRatio = (double) Math.max(width, height) / Math.min(width, height);
//            if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
//                return AspectRatio.RATIO_4_3;
//            }
//            return AspectRatio.RATIO_16_9;
//        } catch (Exception e) {
//            Utility.showLog("Error calculating aspect ratio: " + e.getMessage());
//            return AspectRatio.RATIO_16_9;
//        }
//    }
//}