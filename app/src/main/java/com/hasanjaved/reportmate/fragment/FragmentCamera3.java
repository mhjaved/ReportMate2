//package com.hasanjaved.reportmate.fragment;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
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
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.camera.core.Camera;
//import androidx.camera.core.CameraSelector;
//import androidx.camera.core.ImageCapture;
//import androidx.camera.core.Preview;
//import androidx.camera.lifecycle.ProcessCameraProvider;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentFactory;
//import androidx.fragment.app.FragmentManager;
//
//import com.bumptech.glide.Glide;
//import com.google.common.util.concurrent.ListenableFuture;
//import com.hasanjaved.reportmate.R;
//import com.hasanjaved.reportmate.databinding.FragmentCameraBinding;
//import com.hasanjaved.reportmate.listeners.CameraFragmentClickListener;
//import com.hasanjaved.reportmate.utility.Utility;
//
//import java.lang.ref.WeakReference;
//import java.util.LinkedList;
//import java.util.Queue;
//import java.util.concurrent.ExecutionException;
//
//// BEST PRACTICE 2: Enhanced Fragment with Singleton Pattern for Heavy Objects
//public class FragmentCamera3 extends Fragment {
//
//    // Singleton camera provider to avoid repeated initialization
//    private static ProcessCameraProvider sCameraProvider;
//    private static final Object CAMERA_LOCK = new Object();
//
//    // Static preview configuration to reuse
//    private static Preview sPreviewConfig;
//    private static ImageCapture sImageCaptureConfig;
//
//    // Instance variables
//    private int cameraFacing = CameraSelector.LENS_FACING_BACK;
//    private FragmentCameraBinding binding;
//    private ImageCapture imageCapture;
//    private Activity activity;
//    private CameraFragmentClickListener fragmentClickListener;
//    private String imageLink = "";
//    private ImageView imageViewForSettingImage;
//    private String imageName, subFolder;
//
//    // BEST PRACTICE 3: Use WeakReference for Activity to prevent memory leaks
//    private WeakReference<Activity> activityRef;
//
//    // BEST PRACTICE 4: Fragment Pool for Reuse
//    private static final Queue<FragmentCamera3> fragmentPool = new LinkedList<>();
//    private static final int MAX_POOL_SIZE = 3;
//
//    public static FragmentCamera3 getInstance() {
//        synchronized (fragmentPool) {
//            if (!fragmentPool.isEmpty()) {
//                FragmentCamera3 fragment = fragmentPool.poll();
//                if (fragment != null) {
//                    fragment.reset(); // Reset fragment state
//                    return fragment;
//                }
//            }
//        }
//        return new FragmentCamera3();
//    }
//
//    public void returnToPool() {
//        synchronized (fragmentPool) {
//            if (fragmentPool.size() < MAX_POOL_SIZE) {
//                reset();
//                fragmentPool.offer(this);
//            }
//        }
//    }
//
//    private void reset() {
//        // Reset fragment state for reuse
//        imageLink = "";
//        imageName = null;
//        subFolder = null;
//        fragmentClickListener = null;
//        imageViewForSettingImage = null;
//        activityRef = null;
//    }
//
//    public void setFragmentClickListener(CameraFragmentClickListener fragmentClickListener,
//                                         ImageView imageViewForSettingImage,
//                                         String imageName, String subFolder) {
//        this.fragmentClickListener = fragmentClickListener;
//        this.imageViewForSettingImage = imageViewForSettingImage;
//        this.imageName = imageName;
//        this.subFolder = subFolder;
//        Utility.showLog("imageName: " + imageName);
//        Utility.showLog("subFolder: " + subFolder);
//    }
//
//    // BEST PRACTICE 5: Optimize Camera Initialization
//    private void initializeCameraProvider() {
//        synchronized (CAMERA_LOCK) {
//            if (sCameraProvider == null) {
//                ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
//                        ProcessCameraProvider.getInstance(requireContext());
//
//                try {
//                    sCameraProvider = cameraProviderFuture.get();
//                    Utility.showLog("Camera provider initialized");
//                } catch (ExecutionException | InterruptedException e) {
//                    Utility.showLog("Error initializing camera provider: " + e.getMessage());
//                }
//            }
//        }
//    }
//
//    // BEST PRACTICE 6: Reuse Camera Configurations
//    private Preview getOrCreatePreview() {
//        if (sPreviewConfig == null) {
//            int aspectRatio = aspectRatio(binding.imagePreview.getWidth(), binding.imagePreview.getHeight());
//            sPreviewConfig = new Preview.Builder()
//                    .setTargetAspectRatio(aspectRatio)
//                    .build();
//        }
//        return sPreviewConfig;
//    }
//
//    private ImageCapture getOrCreateImageCapture() {
//        if (sImageCaptureConfig == null) {
//            int aspectRatio = aspectRatio(binding.imagePreview.getWidth(), binding.imagePreview.getHeight());
//            sImageCaptureConfig = new ImageCapture.Builder()
//                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
//                    .setTargetAspectRatio(aspectRatio)
//                    .setTargetRotation(requireActivity().getWindowManager().getDefaultDisplay().getRotation())
//                    .build();
//        }
//        return sImageCaptureConfig;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        activity = getActivity();
//        activityRef = new WeakReference<>(activity);
//
//        // Initialize camera provider early
//        initializeCameraProvider();
//    }
//
//    // BEST PRACTICE 7: Efficient View Binding Management
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//
//        // Reuse binding if possible (for fragment reuse scenarios)
//        if (binding == null) {
//            binding = FragmentCameraBinding.inflate(inflater, container, false);
//        }
//
//        setupUI();
//
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            requestCameraPermission();
//        } else {
//            startCameraOptimized();
//        }
//
//        return binding.getRoot();
//    }
//
//    private void setupUI() {
//        // Retake button - hide image and allow new capture
//        binding.btnRetake.setOnClickListener(view1 -> {
//            binding.ivShowImage.setVisibility(View.GONE);
//            binding.btnRetake.setEnabled(false);
//            binding.btnPerformOCR.setEnabled(false);
//        });
//
//        // Save button - call the listener with image details
//        binding.btnPerformOCR.setOnClickListener(view1 -> {
//            if (!imageLink.isEmpty()) {
//                if (fragmentClickListener != null) {
//                    fragmentClickListener.onSaveButtonPressed(imageViewForSettingImage, imageLink, imageName, subFolder);
//                }
//                closeFragment();
//            } else {
//                Toast.makeText(getActivitySafe(), "No image to save", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // Back button - close fragment
//        binding.btnBack.setOnClickListener(view1 -> closeFragment());
//    }
//
//    // BEST PRACTICE 8: Safe Activity Access
//    private Activity getActivitySafe() {
//        if (activityRef != null) {
//            return activityRef.get();
//        }
//        return getActivity();
//    }
//
//    // BEST PRACTICE 9: Optimized Camera Start
//    private void startCameraOptimized() {
//        if (sCameraProvider != null) {
//            bindCameraUseCases();
//        } else {
//            // Fallback to original method if provider not ready
//            startCamera(cameraFacing);
//        }
//    }
//
//    private void bindCameraUseCases() {
//        try {
//            Preview preview = getOrCreatePreview();
//            imageCapture = getOrCreateImageCapture();
//
//            CameraSelector cameraSelector = new CameraSelector.Builder()
//                    .requireLensFacing(cameraFacing).build();
//
//            sCameraProvider.unbindAll();
//            Camera camera = sCameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview, imageCapture);
//
//            setupCaptureButton();
//            preview.setSurfaceProvider(binding.imagePreview.getSurfaceProvider());
//
//        } catch (Exception e) {
//            Utility.showLog("Error binding camera use cases: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private void setupCaptureButton() {
//        binding.btnCapture.setOnClickListener(v -> {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
//                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                            == PackageManager.PERMISSION_GRANTED) {
//                takePicture(imageCapture);
//            } else {
//                requestStoragePermission();
//            }
//        });
//    }
//
//    // BEST PRACTICE 10: Efficient Permission Handling
//    private final ActivityResultLauncher<String> cameraPermissionLauncher =
//            registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
//                if (result) {
//                    startCameraOptimized();
//                } else {
//                    Toast.makeText(getActivitySafe(), "Camera permission denied", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//    private final ActivityResultLauncher<String> storagePermissionLauncher =
//            registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
//                if (result && imageCapture != null) {
//                    takePicture(imageCapture);
//                } else {
//                    Toast.makeText(getActivitySafe(), "Storage permission denied", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//    private void requestCameraPermission() {
//        cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
//    }
//
//    private void requestStoragePermission() {
//        storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//    }
//
//    // BEST PRACTICE 11: Proper Cleanup
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//
//        // Clear Glide to free memory
//        if (getActivitySafe() != null) {
//            Glide.with(this).clear(binding.ivShowImage);
//        }
//
//        // Don't null the binding if using fragment pool
//        if (!isInPool()) {
//            binding = null;
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        // Return to pool instead of destroying completely
//        if (shouldReturnToPool()) {
//            returnToPool();
//        } else {
//            cleanup();
//        }
//    }
//
//    private boolean isInPool() {
//        synchronized (fragmentPool) {
//            return fragmentPool.contains(this);
//        }
//    }
//
//    private boolean shouldReturnToPool() {
//        // Only return to pool if fragment is in good state
//        return !isRemoving() && !isDetached() && getActivitySafe() != null;
//    }
//
//    private void cleanup() {
//        activityRef = null;
//        fragmentClickListener = null;
//        imageViewForSettingImage = null;
//
//        // Clear any static references if this is the last fragment
//        synchronized (fragmentPool) {
//            if (fragmentPool.isEmpty()) {
//                sPreviewConfig = null;
//                sImageCaptureConfig = null;
//                // Note: Don't null sCameraProvider as it might be expensive to recreate
//            }
//        }
//    }
//
//    private void closeFragment() {
//        // Unbind camera before closing
//        if (sCameraProvider != null) {
//            sCameraProvider.unbindAll();
//        }
//
//        getParentFragmentManager().popBackStack();
//    }
//
//    // Rest of your existing methods (takePicture, showImage, etc.) remain the same...
//    // Just ensure they use getActivitySafe() instead of direct activity reference
//}
//
//// BEST PRACTICE 12: Memory-Efficient Fragment Manager
//public class CameraFragmentManager {
//    private static final String CAMERA_FRAGMENT_TAG = "camera_fragment";
//    private static int fragmentCounter = 0;
//
//    public static void openCameraFragment(FragmentManager fragmentManager,
//                                          CameraFragmentClickListener listener,
//                                          ImageView imageView,
//                                          String imageName,
//                                          String subFolder) {
//
//        // Check if a camera fragment is already open
//        Fragment existingFragment = fragmentManager.findFragmentByTag(CAMERA_FRAGMENT_TAG);
//        if (existingFragment != null) {
//            Utility.showLog("Camera fragment already open, not creating new one");
//            return;
//        }
//
//        // Use fragment pool for better memory management
//        FragmentCamera3 fragment = FragmentCamera3.getInstance();
//        fragment.setFragmentClickListener(listener, imageView, imageName, subFolder);
//
//        fragmentCounter++;
//        Utility.showLog("Opening camera fragment #" + fragmentCounter);
//
//        fragmentManager
//                .beginTransaction()
//                .add(R.id.fragmentHolder, fragment, CAMERA_FRAGMENT_TAG)
//                .addToBackStack(CAMERA_FRAGMENT_TAG)
//                .commit();
//    }
//
//    public static void clearFragmentBackStack(FragmentManager fragmentManager) {
//        // Clear backstack if it gets too large
//        if (fragmentManager.getBackStackEntryCount() > 5) {
//            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        }
//    }
//}
//
//// BEST PRACTICE 13: Usage in Activity
////public class NewReportActivity extends AppCompatActivity {
////
////    @Override
////    public void openCamera(CameraFragmentClickListener cameraFragmentClickListener,
////                           ImageView imageView, String imageName, String subFolder) {
////
////        // Use the memory-efficient fragment manager
////        CameraFragmentManager.openCameraFragment(
////                getSupportFragmentManager(),
////                cameraFragmentClickListener,
////                imageView,
////                imageName,
////                subFolder
////        );
////
////        // Optionally clear backstack if it gets too large
////        CameraFragmentManager.clearFragmentBackStack(getSupportFragmentManager());
////    }
////}