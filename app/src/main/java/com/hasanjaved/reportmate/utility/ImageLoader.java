package com.hasanjaved.reportmate.utility;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.hasanjaved.reportmate.R;

import java.io.File;

public class ImageLoader {

    private static final String TAG = "ImageLoader";
    private static final String REPORTMATE_DIRECTORY = "ReportMate";

    /**
     * Load image from public Documents/ReportMate folder using Glide
     * @param context Application context
     * @param imageView ImageView to display the image
     * @param imageName Name of the image file (e.g., "img.jpg")
     */
    public static void loadImageFromReportMate(Context context, ImageView imageView, String imageName) {
        try {
            // Get the image file path
            String imagePath = getImagePath(imageName);
            File imageFile = new File(imagePath);

            Log.d(TAG, "Looking for image at: " + imagePath);

            if (imageFile.exists()) {
                // Image exists - load with Glide
                loadImageWithGlide(context, imageView, imageFile);
                Log.d(TAG, "Image found and loaded: " + imageName);
            } else {
                // Image not found - show placeholder
                loadPlaceholder(context, imageView);
                Log.w(TAG, "Image not found: " + imagePath);
                Toast.makeText(context, "Image not found: " + imageName, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + imageName, e);
            loadErrorPlaceholder(context, imageView);
            Toast.makeText(context, "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public static void showImageFromStorage(Context context, ImageView imageView, String localImagePath) {
        try {

            Glide.with(context).clear(imageView);
            imageView.setImageDrawable(null);
            // Get the image file path
//            String imagePath = getImagePath(imageName);
            File imageFile = new File(localImagePath);

            Utility.showLog( "Looking for image at: " + localImagePath);

            if (imageFile.exists()) {
                // Image exists - load with Glide
                loadImageWithGlide(context, imageView, imageFile);
                Utility.showLog( "Image found and loaded: " + localImagePath);
            } else {
                // Image not found - show placeholder
                loadPlaceholder(context, imageView);
                Utility.showLog(  "Image not found: " + localImagePath);
                Toast.makeText(context, "Image not found: " + localImagePath, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Utility.showLog(  "Error loading image: " + localImagePath + e);
            loadErrorPlaceholder(context, imageView);
            Toast.makeText(context, "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get the full path to image in public Documents/ReportMate folder
     * @param imageName Name of the image file
     * @return Full path to the image
     */
    public static String getImagePath(String imageName) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY);
        File imageFile = new File(reportMateDir, imageName);
        return imageFile.getAbsolutePath();
    }

    /**
     * Load image using Glide with proper configuration
     * @param context Application context
     * @param imageView Target ImageView
     * @param imageFile Image file to load
     */
    private static void loadImageWithGlide(Context context, ImageView imageView, File imageFile) {

        String signature = imageFile.lastModified() + "_" + imageFile.length();

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_image_24)
                .error(R.drawable.ic_image_24)
                .signature(new ObjectKey(signature));

        Glide.with(context)
                .load(imageFile)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .apply(options)
                .into(imageView);

        //        RequestOptions options = new RequestOptions()
//                .centerCrop() // or .fitCenter() depending on your needs
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(android.R.drawable.ic_menu_gallery) // Placeholder while loading
//                .error(android.R.drawable.ic_menu_close_clear_cancel); // Error placeholder
//
//
//        Glide.with(context)
//                .load(imageFile)
//                .skipMemoryCache(true)       // skip memory cache
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .apply(options)
//                .into(imageView);
    }

    /**
     * Load placeholder when image is not found
     * @param context Application context
     * @param imageView Target ImageView
     */
    private static void loadPlaceholder(Context context, ImageView imageView) {
        Glide.with(context)
                .load(R.drawable.ic_image_24)
                .into(imageView);
    }

    /**
     * Load error placeholder when there's an error
     * @param context Application context
     * @param imageView Target ImageView
     */
    private static void loadErrorPlaceholder(Context context, ImageView imageView) {
        Glide.with(context)
                .load(R.drawable.ic_image_24)
                .into(imageView);
    }

    /**
     * Load image with custom placeholder and error images
     * @param context Application context
     * @param imageView Target ImageView
     * @param imageName Name of the image file
     * @param placeholderResId Resource ID for placeholder image
     * @param errorResId Resource ID for error image
     */
    public static void loadImageFromReportMateWithCustomPlaceholders(Context context, ImageView imageView,
                                                                     String imageName, int placeholderResId, int errorResId) {
        try {
            String imagePath = getImagePath(imageName);
            File imageFile = new File(imagePath);

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(placeholderResId)
                    .error(errorResId);

            if (imageFile.exists()) {
                Glide.with(context)
                        .load(imageFile)
                        .apply(options)
                        .into(imageView);
                Log.d(TAG, "Image loaded with custom placeholders: " + imageName);
            } else {
                Glide.with(context)
                        .load(placeholderResId)
                        .into(imageView);
                Log.w(TAG, "Image not found, showing placeholder: " + imagePath);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading image with custom placeholders: " + imageName, e);
            Glide.with(context)
                    .load(errorResId)
                    .into(imageView);
        }
    }

    /**
     * Load image with specific size constraints
     * @param context Application context
     * @param imageView Target ImageView
     * @param imageName Name of the image file
     * @param width Desired width in pixels
     * @param height Desired height in pixels
     */
    public static void loadImageFromReportMateWithSize(Context context, ImageView imageView,
                                                       String imageName, int width, int height) {
        try {
            String imagePath = getImagePath(imageName);
            File imageFile = new File(imagePath);

            RequestOptions options = new RequestOptions()
                    .override(width, height)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_image_24)
                    .error(R.drawable.ic_image_24);

            if (imageFile.exists()) {
                Glide.with(context)
                        .load(imageFile)
                        .apply(options)
                        .into(imageView);
                Log.d(TAG, "Image loaded with size constraints: " + imageName);
            } else {
                loadPlaceholder(context, imageView);
                Log.w(TAG, "Image not found for sized loading: " + imagePath);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading image with size: " + imageName, e);
            loadErrorPlaceholder(context, imageView);
        }
    }

    /**
     * Load image using URI (for MediaStore compatibility)
     * @param context Application context
     * @param imageView Target ImageView
     * @param imageUri URI of the image
     */
    public static void loadImageFromUri(Context context, ImageView imageView, Uri imageUri) {
        try {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_image_24)
                    .error(R.drawable.ic_image_24);

            Glide.with(context)
                    .load(imageUri)
                    .apply(options)
                    .into(imageView);

            Log.d(TAG, "Image loaded from URI: " + imageUri);

        } catch (Exception e) {
            Log.e(TAG, "Error loading image from URI: " + imageUri, e);
            loadErrorPlaceholder(context, imageView);
        }
    }

    /**
     * Check if image exists in ReportMate folder
     * @param imageName Name of the image file
     * @return true if image exists, false otherwise
     */
    public static boolean doesImageExist(String imageName) {
        try {
            String imagePath = getImagePath(imageName);
            File imageFile = new File(imagePath);
            boolean exists = imageFile.exists();
            Log.d(TAG, "Image " + imageName + " exists: " + exists + " at " + imagePath);
            return exists;
        } catch (Exception e) {
            Log.e(TAG, "Error checking if image exists: " + imageName, e);
            return false;
        }
    }

    /**
     * Get all image files in ReportMate folder
     * @return Array of image file names
     */
    public static String[] getAllImagesInReportMate() {
        try {
            File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY);

            if (reportMateDir.exists() && reportMateDir.isDirectory()) {
                File[] files = reportMateDir.listFiles((dir, name) -> {
                    String lowerName = name.toLowerCase();
                    return lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") ||
                            lowerName.endsWith(".png") || lowerName.endsWith(".gif") ||
                            lowerName.endsWith(".bmp");
                });

                if (files != null) {
                    String[] imageNames = new String[files.length];
                    for (int i = 0; i < files.length; i++) {
                        imageNames[i] = files[i].getName();
                    }
                    Log.d(TAG, "Found " + imageNames.length + " images in ReportMate folder");
                    return imageNames;
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting images from ReportMate folder", e);
        }

        return new String[0];
    }
}

// Usage Examples:
/*
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = findViewById(R.id.imageView);

        // Basic usage - load img.jpg from Documents/ReportMate/
        ImageLoader.loadImageFromReportMate(this, imageView, "img.jpg");

        // Load with custom placeholders
        ImageLoader.loadImageFromReportMateWithCustomPlaceholders(
            this, imageView, "img.jpg",
            R.drawable.placeholder_image,
            R.drawable.error_image
        );

        // Load with specific size
        ImageLoader.loadImageFromReportMateWithSize(this, imageView, "img.jpg", 500, 300);

        // Check if image exists before loading
        if (ImageLoader.doesImageExist("img.jpg")) {
            ImageLoader.loadImageFromReportMate(this, imageView, "img.jpg");
        } else {
            Toast.makeText(this, "Image img.jpg not found in ReportMate folder", Toast.LENGTH_SHORT).show();
        }

        // Get all images in ReportMate folder
        String[] allImages = ImageLoader.getAllImagesInReportMate();
        for (String imageName : allImages) {
            Log.d("Images", "Found: " + imageName);
        }
    }
}
*/

// Required dependency in app/build.gradle:
/*
dependencies {
    implementation 'com.github.bumptech.glide:glide:4.16.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.16.0'
}
*/

// Required permission in AndroidManifest.xml (for reading external storage):
/*
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
                 android:maxSdkVersion="32" />
*/