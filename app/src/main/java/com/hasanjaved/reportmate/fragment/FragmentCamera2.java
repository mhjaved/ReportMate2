package com.hasanjaved.reportmate.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.common.util.concurrent.ListenableFuture;
import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.databinding.FragmentCameraBinding;
import com.hasanjaved.reportmate.listeners.CameraFragmentClickListener;
import com.hasanjaved.reportmate.utility.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class FragmentCamera2 extends Fragment {

    private int cameraFacing = CameraSelector.LENS_FACING_BACK;
    private FragmentCameraBinding binding;
    private ImageCapture imageCapture;
    private Activity activity;
    private CameraFragmentClickListener fragmentClickListener;
    private String imageLink = "";
    private ImageView imageViewForSettingImage;
    private String imageName, subFolder;

    public void setFragmentClickListener(CameraFragmentClickListener fragmentClickListener, ImageView imageViewForSettingImage,
                                         String imageName, String subFolder) {
        this.fragmentClickListener = fragmentClickListener;
        this.imageViewForSettingImage = imageViewForSettingImage;
        this.imageName = imageName;
        this.subFolder = subFolder;
        Utility.showLog("imageName: " + imageName);
        Utility.showLog("subFolder: " + subFolder);
    }

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
                // Added try-catch for permission result handling
                try {
                    if (result) {
                        startCamera(cameraFacing);
                    } else {
                        Toast.makeText(activity, "Camera permission denied", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // Handle any crash in permission callback
                    Utility.showLog("Error in permission callback: " + e.getMessage());
                    showToastAndClose("Something went wrong. Please try again.");
                }
            });

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static FragmentCamera2 newInstance(String param1, String param2) {
        FragmentCamera2 fragment = new FragmentCamera2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Added try-catch for onCreate
        try {
            activity = getActivity();
        } catch (Exception e) {
            Utility.showLog("Error in onCreate: " + e.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Added try-catch for onCreateView to handle any UI setup crashes
        try {
            binding = FragmentCameraBinding.inflate(inflater, container, false);
            View view = binding.getRoot();

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.CAMERA);
            } else {
                startCamera(cameraFacing);
            }

            // Retake button - hide image and allow new capture
            binding.btnRetake.setOnClickListener(view1 -> {
                try {
                    binding.ivShowImage.setVisibility(View.GONE);
                    binding.btnRetake.setEnabled(false);
                    binding.btnPerformOCR.setEnabled(false);
                } catch (Exception e) {
                    // Handle crash in retake button click
                    Utility.showLog("Error in retake button: " + e.getMessage());
                    showToastAndClose("Something went wrong. Please try again.");
                }
            });

            // Save button - call the listener with image details
            binding.btnPerformOCR.setOnClickListener(view1 -> {
                try {
                    if (!imageLink.isEmpty()) {
                        fragmentClickListener.onSaveButtonPressed(imageViewForSettingImage, imageLink, imageName, subFolder);
                        closeFragment();
                    } else {
                        Toast.makeText(activity, "No image to save", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // Handle crash in save button click
                    Utility.showLog("Error in save button: " + e.getMessage());
                    showToastAndClose("Something went wrong. Please try again.");
                }
            });

            // Back button - close fragment
            binding.btnBack.setOnClickListener(view1 -> {
                try {
                    closeFragment();
                } catch (Exception e) {
                    // Handle crash in back button click
                    Utility.showLog("Error in back button: " + e.getMessage());
                    showToastAndClose("Something went wrong. Please try again.");
                }
            });

            return view;
        } catch (Exception e) {
            // Handle any crash during view creation
            Utility.showLog("Error in onCreateView: " + e.getMessage());
            showToastAndClose("Something went wrong. Please try again.");
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        // Added try-catch for onDestroyView
        try {
            super.onDestroyView();
            binding = null;
        } catch (Exception e) {
            Utility.showLog("Error in onDestroyView: " + e.getMessage());
        }
    }

    private void closeFragment() {
        // Added try-catch for fragment closure
        try {
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            Utility.showLog("Error closing fragment: " + e.getMessage());
            // If normal closure fails, try alternative method
            try {
                if (activity != null) {
                    activity.onBackPressed();
                }
            } catch (Exception ex) {
                Utility.showLog("Error with alternative fragment closure: " + ex.getMessage());
            }
        }
    }

    // Added helper method to show toast and close fragment
    private void showToastAndClose(String message) {
        try {
            if (activity != null) {
                activity.runOnUiThread(() -> {
                    try {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        closeFragment();
                    } catch (Exception e) {
                        Utility.showLog("Error showing toast and closing: " + e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            Utility.showLog("Error in showToastAndClose: " + e.getMessage());
        }
    }

    private void startCamera(int cameraFacing) {
        // Added comprehensive try-catch for camera initialization
        try {
            int aspectRatio = aspectRatio(binding.imagePreview.getWidth(), binding.imagePreview.getHeight());
            ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                    ProcessCameraProvider.getInstance(activity);

            cameraProviderFuture.addListener(() -> {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    Preview preview = new Preview.Builder()
                            .setTargetAspectRatio(aspectRatio)
                            .build();

                    imageCapture = new ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                            .setTargetAspectRatio(aspectRatio)
                            .setTargetRotation(activity.getWindowManager().getDefaultDisplay().getRotation())
                            .build();

                    CameraSelector cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(cameraFacing).build();

                    cameraProvider.unbindAll();
                    Camera camera = cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview, imageCapture);

//                    try {
//                        binding.btnFlash.setOnClickListener(view -> {
//                            camera.getCameraControl().enableTorch(true);
//                        });
//                    }catch (Exception e){
//                        Utility.showLog("excepiton "+e.toString());
//                    }

                    try {
                        // Track flash state
                        final boolean[] isFlashOn = {false};

                        binding.btnFlash.setOnClickListener(view -> {
                            try {
                                if (isFlashOn[0]) {
                                    // Turn flash off
                                    camera.getCameraControl().enableTorch(false);
                                    binding.btnFlash.setImageResource(R.drawable.ic_flash_on);
                                    isFlashOn[0] = false;
                                } else {
                                    // Turn flash on
                                    camera.getCameraControl().enableTorch(true);
                                    binding.btnFlash.setImageResource(R.drawable.ic_flash_off);
                                    isFlashOn[0] = true;
                                }
                            } catch (Exception e) {
                                Utility.showLog("Flash toggle error: " + e.toString());
                            }
                        });
                    } catch (Exception e) {
                        Utility.showLog("Flash setup exception: " + e.toString());
                    }

                    binding.btnCapture.setOnClickListener(v -> {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
                                    ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                takePicture(imageCapture);
                            } else {
                                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            }
                        } catch (Exception e) {
                            // Handle crash in capture button click
                            Utility.showLog("Error in capture button: " + e.getMessage());
                            showToastAndClose("Something went wrong. Please try again.");
                        }
                    });

                    preview.setSurfaceProvider(binding.imagePreview.getSurfaceProvider());

                } catch (ExecutionException | InterruptedException e) {
                    // Handle camera provider execution errors
                    Utility.showLog("Error setting up camera: " + e.getMessage());
                    e.printStackTrace();
                    showToastAndClose("Camera setup failed. Please try again.");
                } catch (Exception e) {
                    // Handle any other camera setup errors
                    Utility.showLog("Unexpected error in camera setup: " + e.getMessage());
                    showToastAndClose("Something went wrong. Please try again.");
                }
            }, ContextCompat.getMainExecutor(activity));
        } catch (Exception e) {
            // Handle crash in startCamera method
            Utility.showLog("Error starting camera: " + e.getMessage());
            showToastAndClose("Camera failed to start. Please try again.");
        }
    }

    private void takePicture(ImageCapture imageCapture) {
        // Added comprehensive try-catch for picture taking
        try {
            // Create Documents/subfolder directory
            File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File targetDir = new File(documentsDir, subFolder);
            if (!targetDir.exists()) {
                boolean created = targetDir.mkdirs();
                Utility.showLog("Target directory created: " + created + " - " + targetDir.getAbsolutePath());
            }

            // Create final file with the desired name
            File finalFile = new File(targetDir, imageName + ".jpg");

            // Delete existing file if it exists
            if (finalFile.exists()) {
                boolean deleted = finalFile.delete();
                Utility.showLog("Existing file deleted before capture: " + deleted);
            }

            Utility.showLog("Taking picture - Final path: " + finalFile.getAbsolutePath());

            ImageCapture.OutputFileOptions outputOptions =
                    new ImageCapture.OutputFileOptions.Builder(finalFile).build();

            imageCapture.takePicture(outputOptions, Executors.newSingleThreadExecutor(),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            try {
                                Utility.showLog("Image saved successfully to: " + finalFile.getAbsolutePath());

                                // Verify the file was created correctly
                                if (finalFile.exists()) {
                                    Utility.showLog("File verification - Name: " + finalFile.getName() + ", Size: " + finalFile.length());
                                }

                                // Process the image with improvements, then update UI
                                processImageAndUpdateUI(finalFile.getPath());
                            } catch (Exception e) {
                                // Handle crash in image saved callback
                                Utility.showLog("Error in onImageSaved callback: " + e.getMessage());
                                showToastAndClose("Image processing failed. Please try again.");
                            }
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            // Enhanced error handling for image capture
                            Utility.showLog("Error taking picture: " + exception.getMessage());
                            activity.runOnUiThread(() -> {
                                try {
                                    showToastAndClose("Error capturing image. Please try again.");
                                } catch (Exception e) {
                                    Utility.showLog("Error in capture error callback: " + e.getMessage());
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            // Handle crash in takePicture method
            Utility.showLog("Error in takePicture: " + e.getMessage());
            showToastAndClose("Failed to capture image. Please try again.");
        }
    }
    private void showImage(String imagePath) {
        // Added try-catch for image display
        try {
            binding.ivShowImage.setVisibility(View.VISIBLE);
            Utility.showLog("Showing image: " + imagePath);
            binding.btnRetake.setEnabled(true);
            binding.btnPerformOCR.setEnabled(true);

            // Simple and safe approach
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                Utility.showLog("Image file does not exist: " + imagePath);
                binding.ivShowImage.setImageResource(R.drawable.ic_image_24);
                // Don't close fragment here, just show placeholder
                return;
            }

            // Simplified Glide loading without RequestListener to avoid callback issues
            Glide.with(activity)
                    .load(imageFile)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_image_24)
                    .error(R.drawable.ic_image_24)
                    .into(binding.ivShowImage);

            Utility.showLog("Image loading initiated with Glide");

        } catch (Exception e) {
            // Handle crash in showImage method
            Utility.showLog("Error showing image: " + e.getMessage());
            showToastAndClose("Failed to display image. Please try again.");
        }
    }

    private void processImageAndUpdateUI(String originalImagePath) {
        // Added try-catch for image processing thread
        try {
            new Thread(() -> {
                try {
                    // Load the original image
                    Bitmap originalBitmap = BitmapFactory.decodeFile(originalImagePath);
                    if (originalBitmap == null) {
                        Utility.showLog("Failed to load bitmap from: " + originalImagePath);
                        updateUIWithImage(originalImagePath);
                        return;
                    }

                    // Apply improvements: rotation, cropping, and timestamp
                    Bitmap rotatedBitmap = rotateImageIfRequired(originalBitmap, originalImagePath);
                    Bitmap croppedBitmap = cropToPreviewAspectRatio(rotatedBitmap);
                    Bitmap finalBitmap = addTimestampOverlay(croppedBitmap);

                    // Save the processed image back to the same file
                    saveProcessedImage(finalBitmap, originalImagePath);

                    // Update UI on main thread
                    updateUIWithImage(originalImagePath);

                    // Clean up bitmaps
                    if (originalBitmap != rotatedBitmap) originalBitmap.recycle();
                    if (rotatedBitmap != croppedBitmap) rotatedBitmap.recycle();
                    if (croppedBitmap != finalBitmap) croppedBitmap.recycle();
                    finalBitmap.recycle();

                } catch (Exception e) {
                    // Handle crash in image processing thread
                    Utility.showLog("Error processing image: " + e.getMessage());
                    e.printStackTrace();
                    // Show error on main thread and close fragment
                    activity.runOnUiThread(() -> showToastAndClose("Image processing failed. Please try again."));
                }
            }).start();
        } catch (Exception e) {
            // Handle crash in thread creation
            Utility.showLog("Error creating image processing thread: " + e.getMessage());
            showToastAndClose("Image processing failed. Please try again.");
        }
    }

    private void updateUIWithImage(String filePath) {
        // Added try-catch for UI update
        try {
            activity.runOnUiThread(() -> {
                try {
                    imageLink = filePath;
                    Utility.showLog("Image ready to display: " + filePath);
                    showImage(filePath);
                } catch (Exception e) {
                    // Handle crash in UI update
                    Utility.showLog("Error updating UI with image: " + e.getMessage());
                    showToastAndClose("Something went wrong. Please try again.");
                }
            });
            startCamera(cameraFacing);
        } catch (Exception e) {
            // Handle crash in updateUIWithImage method
            Utility.showLog("Error in updateUIWithImage: " + e.getMessage());
            showToastAndClose("Something went wrong. Please try again.");
        }
    }

    private Bitmap rotateImageIfRequired(Bitmap bitmap, String imagePath) {
        // Added try-catch for image rotation
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    return bitmap;
            }

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (IOException | OutOfMemoryError e) {
            // Handle IO errors and out of memory errors
            Utility.showLog("Error rotating image: " + e.getMessage());
            e.printStackTrace();
            return bitmap; // Return original bitmap if rotation fails
        } catch (Exception e) {
            // Handle any other rotation errors
            Utility.showLog("Unexpected error rotating image: " + e.getMessage());
            return bitmap;
        }
    }

    private Bitmap cropToPreviewAspectRatio(Bitmap bitmap) {
        // Added try-catch for image cropping
        try {
            // Force 4:3 aspect ratio to match electrical panel image shape
            int previewWidth = 4;
            int previewHeight = 3;

            float previewAspectRatio = (float) previewWidth / previewHeight;
            float bitmapAspectRatio = (float) bitmap.getWidth() / bitmap.getHeight();

            int cropWidth, cropHeight;
            int startX = 0, startY = 0;

            if (bitmapAspectRatio > previewAspectRatio) {
                // Bitmap is wider than preview - crop width
                cropHeight = bitmap.getHeight();
                cropWidth = (int) (cropHeight * previewAspectRatio);
                startX = (bitmap.getWidth() - cropWidth) / 2;
            } else {
                // Bitmap is taller than preview - crop height
                cropWidth = bitmap.getWidth();
                cropHeight = (int) (cropWidth / previewAspectRatio);
                startY = (bitmap.getHeight() - cropHeight) / 2;
            }

            return Bitmap.createBitmap(bitmap, startX, startY, cropWidth, cropHeight);
        } catch (OutOfMemoryError e) {
            // Handle out of memory errors
            Utility.showLog("Out of memory error cropping image: " + e.getMessage());
            return bitmap; // Return original bitmap if cropping fails
        } catch (Exception e) {
            // Handle any other cropping errors
            Utility.showLog("Error cropping image: " + e.getMessage());
            return bitmap;
        }
    }

    private Bitmap addTimestampOverlay(Bitmap bitmap) {
        // Added try-catch for timestamp overlay
        try {
            // Create a mutable copy of the bitmap
            Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(mutableBitmap);

            // Get current date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy h:mm:ss a", Locale.getDefault());
            String currentDateTime = dateFormat.format(new Date());

            // Set up paint for text
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(bitmap.getWidth() * 0.025f); // Responsive text size
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            textPaint.setAntiAlias(true);

            // Calculate text dimensions
            Rect textBounds = new Rect();
            textPaint.getTextBounds(currentDateTime, 0, currentDateTime.length(), textBounds);

            // Position at bottom right with padding
            float padding = bitmap.getWidth() * 0.02f;

            // Draw date/time text
            canvas.drawText(currentDateTime,
                    bitmap.getWidth() - textBounds.width() - padding,
                    bitmap.getHeight() - padding,
                    textPaint);

            return mutableBitmap;
        } catch (OutOfMemoryError e) {
            // Handle out of memory errors
            Utility.showLog("Out of memory error adding timestamp: " + e.getMessage());
            return bitmap; // Return original bitmap if timestamp overlay fails
        } catch (Exception e) {
            // Handle any other timestamp overlay errors
            Utility.showLog("Error adding timestamp overlay: " + e.getMessage());
            return bitmap;
        }
    }

    private void saveProcessedImage(Bitmap bitmap, String originalPath) throws IOException {
        // Added try-catch for saving processed image
        try {
            FileOutputStream out = new FileOutputStream(originalPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Utility.showLog("Processed image saved to: " + originalPath);
        } catch (IOException e) {
            // Handle file IO errors
            Utility.showLog("Error saving processed image: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to be caught by calling method
        } catch (Exception e) {
            // Handle any other save errors
            Utility.showLog("Unexpected error saving processed image: " + e.getMessage());
            throw e;
        }
    }

    private int aspectRatio(int width, int height) {
        // Force 4:3 aspect ratio to match electrical panel image shape
        try {
            return AspectRatio.RATIO_4_3;
        } catch (Exception e) {
            // Handle calculation errors and return 4:3 aspect ratio
            Utility.showLog("Error calculating aspect ratio: " + e.getMessage());
            return AspectRatio.RATIO_4_3; // Default to 4:3 to match electrical panel
        }
    }
}