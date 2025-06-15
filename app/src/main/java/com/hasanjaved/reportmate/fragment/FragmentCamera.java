package com.hasanjaved.reportmate.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.common.util.concurrent.ListenableFuture;
import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.databinding.FragmentCameraBinding;
import com.hasanjaved.reportmate.databinding.FragmentHomeBinding;
import com.hasanjaved.reportmate.listeners.CameraFragmentClickListener;
import com.hasanjaved.reportmate.listeners.FragmentClickListener;
import com.hasanjaved.reportmate.utility.Utility;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class FragmentCamera extends Fragment {

    private int cameraFacing = CameraSelector.LENS_FACING_BACK;

    private FragmentCameraBinding binding;
    private ImageCapture imageCapture;
    private Activity activity;

    private CameraFragmentClickListener fragmentClickListener;
    private String imageLink = "";
    private ImageView imageViewForSettingImage;
    private  String imageName, subFolder;

    public void setFragmentClickListener(CameraFragmentClickListener fragmentClickListener,ImageView imageViewForSettingImage,
                                         String imageName,String subFolder) {
        this.fragmentClickListener = fragmentClickListener;
        this.imageViewForSettingImage = imageViewForSettingImage;
        this.imageName = imageName;
        this.subFolder = subFolder;

        Utility.showLog("imageName  "+imageName);
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

    public static FragmentCamera newInstance(String param1, String param2) {
        FragmentCamera fragment = new FragmentCamera();
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
//        View view = inflater.inflate(R.layout.activity_camera2, container, false);

        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

//        previewView = view.findViewById(R.id.cameraPreview);
//        capture = view.findViewById(R.id.capture);
//        toggleFlash = view.findViewById(R.id.toggleFlash);
//        flipCamera = view.findViewById(R.id.flipCamera);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            startCamera(cameraFacing);
        }


        binding.btnRetake.setOnClickListener(view1 ->binding.ivShowImage.setVisibility(View.GONE));
        binding.btnPerformOCR.setOnClickListener(view1 -> {
            fragmentClickListener.onSaveButtonPressed(imageViewForSettingImage,imageLink,imageName, subFolder);
            closeFragment();
        });


        binding.btnBack.setOnClickListener(view1 -> closeFragment());
//        flipCamera.setOnClickListener(v -> {
//            cameraFacing = (cameraFacing == CameraSelector.LENS_FACING_BACK)
//                    ? CameraSelector.LENS_FACING_FRONT
//                    : CameraSelector.LENS_FACING_BACK;
//            startCamera(cameraFacing);
//        });

        return view;
    }

    private void closeFragment(){
        getParentFragmentManager().popBackStack();
    }
    private void startCamera(int cameraFacing) {
        int aspectRatio = aspectRatio(binding.imagePreview.getWidth(), binding.imagePreview.getHeight());
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
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

//                toggleFlash.setOnClickListener(v -> setFlashIcon(camera));
                preview.setSurfaceProvider(binding.imagePreview.getSurfaceProvider());

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void showImage(String imageLink){
        binding.ivShowImage.setVisibility(View.VISIBLE);
        String fileLocation = "file:"+imageLink;
        Utility.showLog(fileLocation);

        binding.btnRetake.setEnabled(true);
        binding.btnPerformOCR.setEnabled(true);

        Glide.with(activity)
                .load(Uri.parse(fileLocation))
                .into(binding.ivShowImage);

    }
    private void takePicture(ImageCapture imageCapture) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                System.currentTimeMillis() + ".jpg");

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(file).build();

        imageCapture.takePicture(outputOptions, Executors.newSingleThreadExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Saved to: " + file.getPath(), Toast.LENGTH_SHORT).show();
                            SharedPreferences prefs = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
                            prefs.edit().putString(Utility.ImageToken, file.getPath()).apply();
//                            fragmentClickListener.onSaveButtonPressed(imageViewForSettingImage,file.getPath());
                            imageLink = file.getPath();
                            Utility.showLog(" file path "+file.getPath());
//                            closeFragment();
                            showImage(file.getPath());
                        });
                        startCamera(cameraFacing);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show());
                        startCamera(cameraFacing);
                    }
                });
    }

//    private void setFlashIcon(Camera camera) {
//        if (camera.getCameraInfo().hasFlashUnit()) {
//            boolean isTorchOn = camera.getCameraInfo().getTorchState().getValue() == 1;
//            camera.getCameraControl().enableTorch(!isTorchOn);
//            toggleFlash.setImageResource(isTorchOn ? R.drawable.ic_demo_icon_2 : R.drawable.ic_demo_icon);
//        } else {
//            Toast.makeText(requireContext(), "Flash not available", Toast.LENGTH_SHORT).show();
//        }
//    }

    private int aspectRatio(int width, int height) {
        double ratio = (double) Math.max(width, height) / Math.min(width, height);
        return (Math.abs(ratio - 4.0 / 3.0) <= Math.abs(ratio - 16.0 / 9.0))
                ? AspectRatio.RATIO_4_3
                : AspectRatio.RATIO_16_9;
    }

}
