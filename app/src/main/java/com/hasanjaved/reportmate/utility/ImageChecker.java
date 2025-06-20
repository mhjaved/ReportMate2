//package com.hasanjaved.reportmate.utility;
//
//import android.content.Context;
//import android.os.Build;
//import android.os.Environment;
//import android.util.Log;
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class ImageChecker {
//
//    private static final String TAG = "ImageChecker";
//    private static final int REQUIRED_JPG_COUNT = 5;
//
//    /**
//     * Checks if exactly 5 JPG images are available in the specified subdirectory
//     * within the public Documents folder
//     *
//     * @param context Application context
//     * @param subdirectory The subdirectory name within Documents folder
//     * @return ImageCheckResult containing the result and details
//     */
//    public static ImageCheckResult checkJpgImages(Context context, String subdirectory) {
//        try {
//            // Get the public Documents directory
//            File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
//
//            // Create the subdirectory path
//            File targetDirectory = new File(documentsDir, subdirectory);
//
//            Log.d(TAG, "Checking directory: " + targetDirectory.getAbsolutePath());
//
//            // Check if directory exists
//            if (!targetDirectory.exists()) {
//                Log.w(TAG, "Directory does not exist: " + targetDirectory.getAbsolutePath());
//                return new ImageCheckResult(false, 0, new ArrayList<>(),
//                        "Directory does not exist: " + subdirectory);
//            }
//
//            if (!targetDirectory.isDirectory()) {
//                Log.w(TAG, "Path is not a directory: " + targetDirectory.getAbsolutePath());
//                return new ImageCheckResult(false, 0, new ArrayList<>(),
//                        "Path is not a directory: " + subdirectory);
//            }
//
//            // Get all JPG files in the directory
//            List<String> jpgFiles = getJpgFiles(targetDirectory);
//
//            int jpgCount = jpgFiles.size();
//            boolean hasExactlyFiveJpgs = (jpgCount == REQUIRED_JPG_COUNT);
//
//            String message = String.format("Found %d JPG images. Required: %d",
//                    jpgCount, REQUIRED_JPG_COUNT);
//
//            Log.i(TAG, message);
//            Log.i(TAG, "JPG files found: " + jpgFiles.toString());
//
//            return new ImageCheckResult(hasExactlyFiveJpgs, jpgCount, jpgFiles, message);
//
//        } catch (SecurityException e) {
//            Log.e(TAG, "Security exception: No permission to access external storage", e);
//            return new ImageCheckResult(false, 0, new ArrayList<>(),
//                    "Permission denied: Cannot access external storage");
//        } catch (Exception e) {
//            Log.e(TAG, "Error checking JPG images", e);
//            return new ImageCheckResult(false, 0, new ArrayList<>(),
//                    "Error: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Alternative method using MediaStore for better compatibility with Android 10+ (API 29+)
//     *
//     * @param context Application context
//     * @param subdirectory The subdirectory name within Documents folder
//     * @return ImageCheckResult containing the result and details
//     */
//    public static ImageCheckResult checkJpgImagesWithMediaStore(Context context, String subdirectory) {
//        try {
//            ContentResolver contentResolver = context.getContentResolver();
//
//            // Query for images in the Documents directory
//            String[] projection = {
//                    MediaStore.Images.Media.DISPLAY_NAME,
//                    MediaStore.Images.Media.RELATIVE_PATH,
//                    MediaStore.Images.Media.DATA
//            };
//
//            String selection = MediaStore.Images.Media.RELATIVE_PATH + " LIKE ?";
//            String[] selectionArgs = {"Documents/" + subdirectory + "%"};
//
//            Cursor cursor = contentResolver.query(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                    projection,
//                    selection,
//                    selectionArgs,
//                    null
//            );
//
//            List<String> jpgFiles = new ArrayList<>();
//
//            if (cursor != null) {
//                while (cursor.moveToNext()) {
//                    String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
//                    if (isJpgFile(fileName)) {
//                        jpgFiles.add(fileName);
//                    }
//                }
//                cursor.close();
//            }
//
//            int jpgCount = jpgFiles.size();
//            boolean hasExactlyFiveJpgs = (jpgCount == REQUIRED_JPG_COUNT);
//
//            String message = String.format("Found %d JPG images using MediaStore. Required: %d",
//                    jpgCount, REQUIRED_JPG_COUNT);
//
//            Log.i(TAG, message);
//
//            return new ImageCheckResult(hasExactlyFiveJpgs, jpgCount, jpgFiles, message);
//
//        } catch (Exception e) {
//            Log.e(TAG, "Error checking JPG images with MediaStore", e);
//            return new ImageCheckResult(false, 0, new ArrayList<>(),
//                    "Error: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Gets all JPG files from the specified directory
//     */
//    private static List<String> getJpgFiles(File directory) {
//        List<String> jpgFiles = new ArrayList<>();
//
//        File[] files = directory.listFiles();
//        if (files != null) {
//            for (File file : files) {
//                if (file.isFile() && isJpgFile(file.getName())) {
//                    jpgFiles.add(file.getName());
//                }
//            }
//        }
//
//        return jpgFiles;
//    }
//
//    /**
//     * Checks if a file is a JPG image based on its extension
//     */
//    private static boolean isJpgFile(String fileName) {
//        if (fileName == null) return false;
//
//        String lowerCaseFileName = fileName.toLowerCase();
//        return lowerCaseFileName.endsWith(".jpg") ||
//                lowerCaseFileName.endsWith(".jpeg");
//    }
//
//    /**
//     * Result class to hold the check results
//     */
//    public static class ImageCheckResult {
//        private final boolean hasExactlyFiveJpgs;
//        private final int jpgCount;
//        private final List<String> jpgFiles;
//        private final String message;
//
//        public ImageCheckResult(boolean hasExactlyFiveJpgs, int jpgCount,
//                                List<String> jpgFiles, String message) {
//            this.hasExactlyFiveJpgs = hasExactlyFiveJpgs;
//            this.jpgCount = jpgCount;
//            this.jpgFiles = new ArrayList<>(jpgFiles);
//            this.message = message;
//        }
//
//        public boolean hasExactlyFiveJpgs() {
//            return hasExactlyFiveJpgs;
//        }
//
//        public int getJpgCount() {
//            return jpgCount;
//        }
//
//        public List<String> getJpgFiles() {
//            return new ArrayList<>(jpgFiles);
//        }
//
//        public String getMessage() {
//            return message;
//        }
//
//        @Override
//        public String toString() {
//            return "ImageCheckResult{" +
//                    "hasExactlyFiveJpgs=" + hasExactlyFiveJpgs +
//                    ", jpgCount=" + jpgCount +
//                    ", jpgFiles=" + jpgFiles +
//                    ", message='" + message + '\'' +
//                    '}';
//        }
//    }
//}
//
//// Usage Example
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Example usage
//        checkImagesInDirectory("MySubfolder");
//    }
//
//    private void checkImagesInDirectory(String subdirectory) {
//        // Check using direct file access (works better on older Android versions)
//        ImageChecker.ImageCheckResult result = ImageChecker.checkJpgImages(this, subdirectory);
//
//        Log.d("MainActivity", "Result: " + result.toString());
//
//        if (result.hasExactlyFiveJpgs()) {
//            // All 5 JPG images are present
//            showToast("✓ All 5 JPG images found!");
//            // Proceed with your logic
//        } else {
//            // Not exactly 5 JPG images
//            showToast("✗ Expected 5 JPG images, found " + result.getJpgCount());
//            // Handle the missing images case
//        }
//
//        // For Android 10+ (API 29+), you might want to use MediaStore approach
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            ImageChecker.ImageCheckResult mediaStoreResult =
//                    ImageChecker.checkJpgImagesWithMediaStore(this, subdirectory);
//            Log.d("MainActivity", "MediaStore Result: " + mediaStoreResult.toString());
//        }
//    }
//
//    private void showToast(String message) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//    }
//}