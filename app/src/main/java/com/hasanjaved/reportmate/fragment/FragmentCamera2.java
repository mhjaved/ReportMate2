package com.hasanjaved.reportmate.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.common.util.concurrent.ListenableFuture;
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
        Utility.showLog("imageName  " + imageName);
    }

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
                if (result) {
                    startCamera(cameraFacing);
                } else {
                    Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
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
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            startCamera(cameraFacing);
        }

        // Match FragmentCamera behavior exactly
        binding.btnRetake.setOnClickListener(view1 -> binding.ivShowImage.setVisibility(View.GONE));

        binding.btnPerformOCR.setOnClickListener(view1 -> {
            fragmentClickListener.onSaveButtonPressed(imageViewForSettingImage, imageLink, imageName, subFolder);
            closeFragment();
        });

        binding.btnBack.setOnClickListener(view1 -> closeFragment());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void closeFragment() {
        getParentFragmentManager().popBackStack();
    }

    private void startCamera(int cameraFacing) {
        int aspectRatio = aspectRatio(binding.imagePreview.getWidth(), binding.imagePreview.getHeight());
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder()
                        .setTargetAspectRatio(aspectRatio)
                        .build();

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .setTargetAspectRatio(aspectRatio)
                        .setTargetRotation(requireActivity().getWindowManager().getDefaultDisplay().getRotation())
                        .build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing).build();

                cameraProvider.unbindAll();
                Camera camera = cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview, imageCapture);

                binding.btnCapture.setOnClickListener(v -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
                            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        takePicture(imageCapture);
                    } else {
                        permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                });

                preview.setSurfaceProvider(binding.imagePreview.getSurfaceProvider());

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }


    // Match FragmentCamera showImage method exactly
    private void showImage(String imageLink) {
        binding.ivShowImage.setVisibility(View.VISIBLE);
        String fileLocation = "file:" + imageLink;
        Utility.showLog(fileLocation);

        binding.btnRetake.setEnabled(true);
        binding.btnPerformOCR.setEnabled(true);

        Glide.with(activity)
                .load(Uri.parse(fileLocation))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        String errorMsg = e != null ? e.getMessage() : "Unknown error";
                        Utility.showLog("Glide failed to load image: " + errorMsg);

                        // Fallback: try loading with File directly
                        Glide.with(activity)
                                .load(new File(imageLink))
                                .into(binding.ivShowImage);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Utility.showLog("Image loaded successfully with Glide");
                        return false;
                    }
                })
                .into(binding.ivShowImage);
    }

    private void takePicture(ImageCapture imageCapture) {
        // Save to Pictures directory like FragmentCamera (no subfolder)
//        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                System.currentTimeMillis() + ".jpg");

        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File targetDir = new File(documentsDir, subFolder);
        if (!targetDir.exists()) {
            targetDir.mkdirs(); // create if not exists
        }

        File file = new File(targetDir, imageName + ".jpg");
        if (file.exists()) {
            file.delete(); // replace existing image
        }


        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(file).build();

        imageCapture.takePicture(outputOptions, Executors.newSingleThreadExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        // Process the image with improvements, then update UI like FragmentCamera
                        processImageAndUpdateUI(file.getPath());
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
//                        requireActivity().runOnUiThread(() ->
//                                Toast.makeText(requireContext(), "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show());
                        startCamera(cameraFacing);
                    }
                });
    }

    private void processImageAndUpdateUI(String originalImagePath) {
        new Thread(() -> {
            try {
                // Load the original image
                Bitmap originalBitmap = BitmapFactory.decodeFile(originalImagePath);
                if (originalBitmap == null) {
                    // If processing fails, just use the original image
                    updateUIWithImage(originalImagePath);
                    return;
                }

                // Apply improvements: rotation, cropping, and timestamp
                Bitmap rotatedBitmap = rotateImageIfRequired(originalBitmap, originalImagePath);
                Bitmap croppedBitmap = cropToPreviewAspectRatio(rotatedBitmap);
                Bitmap finalBitmap = addTimestampOverlay(croppedBitmap);

                // Save the processed image back to the same file
                saveProcessedImage(finalBitmap, originalImagePath);

                // Update UI on main thread (exactly like FragmentCamera)
                updateUIWithImage(originalImagePath);

                // Clean up bitmaps
                if (originalBitmap != rotatedBitmap) originalBitmap.recycle();
                if (rotatedBitmap != croppedBitmap) rotatedBitmap.recycle();
                if (croppedBitmap != finalBitmap) croppedBitmap.recycle();
                finalBitmap.recycle();

            } catch (Exception e) {
                e.printStackTrace();
                // If processing fails, still show the original image
                updateUIWithImage(originalImagePath);
            }
        }).start();
    }

    // Match FragmentCamera UI update behavior exactly
    private void updateUIWithImage(String filePath) {
        requireActivity().runOnUiThread(() -> {
//            Toast.makeText(requireContext(), "Saved to: ", Toast.LENGTH_SHORT).show();
            SharedPreferences prefs = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
            prefs.edit().putString(Utility.ImageToken, filePath).apply();
            imageLink = filePath;
            Utility.showLog(" file path " + filePath);
            showImage(filePath);
        });
        startCamera(cameraFacing);
    }

    private Bitmap rotateImageIfRequired(Bitmap bitmap, String imagePath) {
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
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    private Bitmap cropToPreviewAspectRatio(Bitmap bitmap) {
        // Get PreviewView dimensions
        int previewWidth = binding.imagePreview.getWidth();
        int previewHeight = binding.imagePreview.getHeight();

        if (previewWidth == 0 || previewHeight == 0) {
            // Fallback to common aspect ratio if PreviewView not measured yet
            previewWidth = 16;
            previewHeight = 9;
        }

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
    }

    private Bitmap addTimestampOverlay(Bitmap bitmap) {
        // Create a mutable copy of the bitmap
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        // Get current date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy h:mm:ss a", Locale.getDefault());
        String currentDateTime = dateFormat.format(new Date());

        // Add index number
        String indexText = "Index number: " + (System.currentTimeMillis() % 1000);

        // Set up paint for text
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(bitmap.getWidth() * 0.025f); // Responsive text size
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setAntiAlias(true);

        // Set up paint for background
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.argb(180, 0, 0, 0)); // Semi-transparent black
        backgroundPaint.setAntiAlias(true);

        // Calculate text dimensions
        Rect textBounds = new Rect();
        textPaint.getTextBounds(currentDateTime, 0, currentDateTime.length(), textBounds);

        Rect indexBounds = new Rect();
        textPaint.getTextBounds(indexText, 0, indexText.length(), indexBounds);

        // Position at bottom right with padding
        float padding = bitmap.getWidth() * 0.02f;
        float cornerRadius = padding / 2;

        // Calculate background rectangle
        float textWidth = Math.max(textBounds.width(), indexBounds.width());
        float textHeight = textBounds.height();
        float totalHeight = textHeight * 2 + padding; // Two lines of text

//        RectF backgroundRect = new RectF(
//                bitmap.getWidth() - textWidth - (padding * 2),
//                bitmap.getHeight() - totalHeight - padding,
//                bitmap.getWidth() - padding,
//                bitmap.getHeight() - padding
//        );

        // Draw background rectangle with rounded corners
//        canvas.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, backgroundPaint);

        // Draw date/time text
        canvas.drawText(currentDateTime,
                bitmap.getWidth() - textWidth - padding,
                bitmap.getHeight() - textHeight - padding * 1.5f,
                textPaint);

        // Draw index text
//        canvas.drawText(indexText,
//                bitmap.getWidth() - textWidth - padding,
//                bitmap.getHeight() - padding * 0.5f,
//                textPaint);

        return mutableBitmap;
    }

    private void saveProcessedImage(Bitmap bitmap, String originalPath) {
        try {
            FileOutputStream out = new FileOutputStream(originalPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int aspectRatio(int width, int height) {
        double ratio = (double) Math.max(width, height) / Math.min(width, height);
        return (Math.abs(ratio - 4.0 / 3.0) <= Math.abs(ratio - 16.0 / 9.0))
                ? AspectRatio.RATIO_4_3
                : AspectRatio.RATIO_16_9;
    }
}