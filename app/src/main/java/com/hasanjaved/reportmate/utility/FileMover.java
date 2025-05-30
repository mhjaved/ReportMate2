package com.hasanjaved.reportmate.utility;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileMover {

    private static final String TAG = "FileMover";
    private Context context;

    public FileMover(Context context) {
        this.context = context;
    }

    /**
     * Moves an image file from source path to destination directory
     * @param sourceFilePath Source file path (e.g., "/storage/emulated/0/Pictures/1748629149938.jpg")
     * @param destinationDirectoryPath Destination directory path
     * @param newFileName Optional new filename (null to keep original name)
     * @param copyInsteadOfMove If true, copies file instead of moving
     * @return FileOperationResult containing operation status and details
     */
    public FileOperationResult moveImageFile(String sourceFilePath, String destinationDirectoryPath,
                                             String newFileName, boolean copyInsteadOfMove) {
        try {
            // Validate input parameters
            if (sourceFilePath == null || sourceFilePath.trim().isEmpty()) {
                return new FileOperationResult(false, null, "Source file path cannot be empty");
            }

            if (destinationDirectoryPath == null || destinationDirectoryPath.trim().isEmpty()) {
                return new FileOperationResult(false, null, "Destination directory path cannot be empty");
            }

            // Clean paths
            sourceFilePath = sourceFilePath.trim();
            destinationDirectoryPath = destinationDirectoryPath.trim();

            // Check if source file exists
            File sourceFile = new File(sourceFilePath);
            if (!sourceFile.exists()) {
                return new FileOperationResult(false, null, "Source file does not exist: " + sourceFilePath);
            }

            if (!sourceFile.isFile()) {
                return new FileOperationResult(false, null, "Source path is not a file: " + sourceFilePath);
            }

            // Validate file is an image
            if (!isImageFile(sourceFile)) {
                return new FileOperationResult(false, null, "File is not a valid image format");
            }

            // Check if destination directory exists
            File destinationDir = new File(destinationDirectoryPath);
            if (!destinationDir.exists()) {
                return new FileOperationResult(false, null, "Destination directory does not exist: " + destinationDirectoryPath);
            }

            if (!destinationDir.isDirectory()) {
                return new FileOperationResult(false, null, "Destination path is not a directory: " + destinationDirectoryPath);
            }

            // Determine destination file name
            String fileName;
            if (newFileName != null && !newFileName.trim().isEmpty()) {
                fileName = newFileName.trim();
                // Add extension if not present
                if (!fileName.contains(".")) {
                    String originalExtension = getFileExtension(sourceFile.getName());
                    if (!originalExtension.isEmpty()) {
                        fileName += "." + originalExtension;
                    }
                }
            } else {
                fileName = sourceFile.getName();
            }

            // Create destination file path
            String destinationFilePath = destinationDirectoryPath + File.separator + fileName;
            File destinationFile = new File(destinationFilePath);

            // Handle file name conflicts
            if (destinationFile.exists()) {
                fileName = generateUniqueFileName(destinationDirectoryPath, fileName);
                destinationFilePath = destinationDirectoryPath + File.separator + fileName;
                destinationFile = new File(destinationFilePath);
            }

            // Perform the operation
            boolean success;
            String operation;

            if (copyInsteadOfMove) {
                success = copyFile(sourceFile, destinationFile);
                operation = "copied";
            } else {
                success = moveFile(sourceFile, destinationFile);
                operation = "moved";
            }

            if (success) {
                String message = String.format("File %s successfully from %s to %s",
                        operation, sourceFilePath, destinationFilePath);
                return new FileOperationResult(true, destinationFilePath, message);
            } else {
                return new FileOperationResult(false, null, "Failed to " + operation.substring(0, operation.length()-1) + " file");
            }

        } catch (SecurityException e) {
            return new FileOperationResult(false, null, "Permission denied: " + e.getMessage());
        } catch (Exception e) {
            return new FileOperationResult(false, null, "Error moving file: " + e.getMessage());
        }
    }

    /**
     * Moves multiple image files to a destination directory
     * @param sourceFilePaths Array of source file paths
     * @param destinationDirectoryPath Destination directory path
     * @param copyInsteadOfMove If true, copies files instead of moving
     * @return BatchOperationResult containing results for all files
     */
    public BatchOperationResult moveMultipleImageFiles(String[] sourceFilePaths,
                                                       String destinationDirectoryPath,
                                                       boolean copyInsteadOfMove) {
        if (sourceFilePaths == null || sourceFilePaths.length == 0) {
            return new BatchOperationResult(false, null, "No source files provided");
        }

        BatchOperationResult batchResult = new BatchOperationResult(true, null, "Batch operation completed");

        for (String sourceFilePath : sourceFilePaths) {
            FileOperationResult result = moveImageFile(sourceFilePath, destinationDirectoryPath, null, copyInsteadOfMove);
            batchResult.addResult(sourceFilePath, result);

            if (!result.isSuccess()) {
                batchResult.setSuccess(false);
            }
        }

        return batchResult;
    }

    /**
     * Moves file with automatic timestamp naming
     * @param sourceFilePath Source file path
     * @param destinationDirectoryPath Destination directory path
     * @param prefix Optional prefix for filename
     * @return FileOperationResult
     */
    public FileOperationResult moveImageWithTimestamp(String sourceFilePath, String destinationDirectoryPath, String prefix) {
        try {
            File sourceFile = new File(sourceFilePath);
            String extension = getFileExtension(sourceFile.getName());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String timestamp = sdf.format(new Date());

            String newFileName = (prefix != null ? prefix + "_" : "") + timestamp;
            if (!extension.isEmpty()) {
                newFileName += "." + extension;
            }

            return moveImageFile(sourceFilePath, destinationDirectoryPath, newFileName, false);

        } catch (Exception e) {
            return new FileOperationResult(false, null, "Error creating timestamp filename: " + e.getMessage());
        }
    }

    // Helper methods
    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                name.endsWith(".png") || name.endsWith(".gif") ||
                name.endsWith(".bmp") || name.endsWith(".webp") ||
                name.endsWith(".tiff") || name.endsWith(".tif");
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    private String generateUniqueFileName(String directoryPath, String fileName) {
        String nameWithoutExt = fileName;
        String extension = "";

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            nameWithoutExt = fileName.substring(0, lastDotIndex);
            extension = fileName.substring(lastDotIndex);
        }

        int counter = 1;
        String newFileName;
        File testFile;

        do {
            newFileName = nameWithoutExt + "_" + counter + extension;
            testFile = new File(directoryPath + File.separator + newFileName);
            counter++;
        } while (testFile.exists());

        return newFileName;
    }

    private boolean copyFile(File sourceFile, File destinationFile) {
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            inStream = new FileInputStream(sourceFile);
            outStream = new FileOutputStream(destinationFile);
            inChannel = inStream.getChannel();
            outChannel = outStream.getChannel();

            inChannel.transferTo(0, inChannel.size(), outChannel);
            return true;

        } catch (IOException e) {
            Log.e(TAG, "Error copying file", e);
            return false;
        } finally {
            try {
                if (inChannel != null) inChannel.close();
                if (outChannel != null) outChannel.close();
                if (inStream != null) inStream.close();
                if (outStream != null) outStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing streams", e);
            }
        }
    }

    private boolean moveFile(File sourceFile, File destinationFile) {
        // Try simple rename first (works if on same filesystem)
        if (sourceFile.renameTo(destinationFile)) {
            return true;
        }

        // If rename fails, copy and delete
        if (copyFile(sourceFile, destinationFile)) {
            return sourceFile.delete();
        }

        return false;
    }
}

// FileOperationResult.java
class FileOperationResult {
    private boolean success;
    private String destinationPath;
    private String message;

    public FileOperationResult(boolean success, String destinationPath, String message) {
        this.success = success;
        this.destinationPath = destinationPath;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "FileOperationResult{" +
                "success=" + success +
                ", destinationPath='" + destinationPath + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

// BatchOperationResult.java
class BatchOperationResult {
    private boolean success;
    private String message;
    private java.util.Map<String, FileOperationResult> results;

    public BatchOperationResult(boolean success, String destinationPath, String message) {
        this.success = success;
        this.message = message;
        this.results = new java.util.HashMap<>();
    }

    public void addResult(String sourceFile, FileOperationResult result) {
        results.put(sourceFile, result);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public java.util.Map<String, FileOperationResult> getResults() {
        return results;
    }

    public int getSuccessCount() {
        int count = 0;
        for (FileOperationResult result : results.values()) {
            if (result.isSuccess()) count++;
        }
        return count;
    }

    public int getFailureCount() {
        return results.size() - getSuccessCount();
    }
}



//public class MainActivity extends AppCompatActivity {
//
//    private static final int PERMISSION_REQUEST_CODE = 1;
//    private static final String[] REQUIRED_PERMISSIONS = {
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE
//    };
//
//    private EditText etSourceFilePath;
//    private EditText etDestinationPath;
//    private EditText etNewFileName;
//    private CheckBox cbCopyInsteadOfMove;
//    private Button btnMoveFile;
//    private Button btnMoveWithTimestamp;
//    private TextView tvResult;
//    private FileMover fileMover;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        initializeViews();
//        fileMover = new FileMover(this);
//
//        // Set default values
//        etSourceFilePath.setText("/storage/emulated/0/Pictures/1748629149938.jpg");
//        etDestinationPath.setText("/storage/emulated/0/Android/data/com.hasanjaved.reportmate/files/Documents/NEL-SWGRTS1");
//
//        btnMoveFile.setOnClickListener(v -> {
//            if (checkPermissions()) {
//                moveImageFile();
//            } else {
//                requestPermissions();
//            }
//        });
//
//        btnMoveWithTimestamp.setOnClickListener(v -> {
//            if (checkPermissions()) {
//                moveImageWithTimestamp();
//            } else {
//                requestPermissions();
//            }
//        });
//    }
//
//    private void initializeViews() {
//        etSourceFilePath = findViewById(R.id.etSourceFilePath);
//        etDestinationPath = findViewById(R.id.etDestinationPath);
//        etNewFileName = findViewById(R.id.etNewFileName);
//        cbCopyInsteadOfMove = findViewById(R.id.cbCopyInsteadOfMove);
//        btnMoveFile = findViewById(R.id.btnMoveFile);
//        btnMoveWithTimestamp = findViewById(R.id.btnMoveWithTimestamp);
//        tvResult = findViewById(R.id.tvResult);
//    }
//
//    private void moveImageFile() {
//        String sourceFilePath = etSourceFilePath.getText().toString().trim();
//        String destinationPath = etDestinationPath.getText().toString().trim();
//        String newFileName = etNewFileName.getText().toString().trim();
//        boolean copyInsteadOfMove = cbCopyInsteadOfMove.isChecked();
//
//        if (sourceFilePath.isEmpty()) {
//            showToast("Please enter source file path");
//            return;
//        }
//
//        if (destinationPath.isEmpty()) {
//            showToast("Please enter destination directory path");
//            return;
//        }
//
//        // Use null for newFileName if empty (to keep original name)
//        String fileName = newFileName.isEmpty() ? null : newFileName;
//
//        FileOperationResult result = fileMover.moveImageFile(sourceFilePath, destinationPath, fileName, copyInsteadOfMove);
//
//        displayResult(result);
//
//        if (result.isSuccess()) {
//            showToast(copyInsteadOfMove ? "File copied successfully!" : "File moved successfully!");
//
//            // Clear source path if moved (not copied)
//            if (!copyInsteadOfMove) {
//                etSourceFilePath.setText("");
//            }
//            etNewFileName.setText("");
//        } else {
//            showToast("Operation failed: " + result.getMessage());
//        }
//    }
//
//    private void moveImageWithTimestamp() {
//        String sourceFilePath = etSourceFilePath.getText().toString().trim();
//        String destinationPath = etDestinationPath.getText().toString().trim();
//
//        if (sourceFilePath.isEmpty()) {
//            showToast("Please enter source file path");
//            return;
//        }
//
//        if (destinationPath.isEmpty()) {
//            showToast("Please enter destination directory path");
//            return;
//        }
//
//        FileOperationResult result = fileMover.moveImageWithTimestamp(sourceFilePath, destinationPath, "IMG");
//
//        displayResult(result);
//
//        if (result.isSuccess()) {
//            showToast("File moved with timestamp successfully!");
//            etSourceFilePath.setText("");
//        } else {
//            showToast("Operation failed: " + result.getMessage());
//        }
//    }
//
//    private void displayResult(FileOperationResult result) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Operation Status: ").append(result.isSuccess() ? "SUCCESS" : "FAILED").append("\n");
//        sb.append("Message: ").append(result.getMessage()).append("\n");
//        if (result.getDestinationPath() != null) {
//            sb.append("Destination: ").append(result.getDestinationPath()).append("\n");
//        }
//        sb.append("Time: ").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
//                .format(new java.util.Date()));
//
//        tvResult.setText(sb.toString());
//    }
//
//    private boolean checkPermissions() {
//        for (String permission : REQUIRED_PERMISSIONS) {
//            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private void requestPermissions() {
//        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
//    }
//
//    private void showToast(String message) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            boolean allGranted = true;
//            for (int result : grantResults) {
//                if (result != PackageManager.PERMISSION_GRANTED) {
//                    allGranted = false;
//                    break;
//                }
//            }
//
//            if (allGranted) {
//                moveImageFile();
//            } else {
//                showToast("Storage permissions are required to move files");
//            }
//        }
//    }
//}

// ========== ADDITIONAL FILES NEEDED ==========

// activity_main.xml
/*
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Image File Mover"
            android:textSize="20sp"
            android:textStyle="bold"
            android:layout_marginBottom="24dp"
            android:layout_gravity="center_horizontal"/>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Source File Path:"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <EditText
            android:id="@+id/etSourceFilePath"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="e.g., /storage/emulated/0/Pictures/1748629149938.jpg"
            android:inputType="text"
            android:maxLines="2"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Destination Directory Path:"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <EditText
            android:id="@+id/etDestinationPath"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="e.g., /storage/emulated/0/Android/data/com.hasanjaved.reportmate/files/Documents"
            android:inputType="text"
            android:maxLines="2"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="New File Name (Optional):"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <EditText
            android:id="@+id/etNewFileName"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Leave empty to keep original name"
            android:inputType="text"
            android:layout_marginBottom="16dp"/>

        <CheckBox
            android:id="@+id/cbCopyInsteadOfMove"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Copy instead of move"
            android:layout_marginBottom="24dp"/>

        <Button
            android:id="@+id/btnMoveFile"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Move/Copy Image File"
            android:textSize="16sp"
            android:layout_marginBottom="12dp"/>

        <Button
            android:id="@+id/btnMoveWithTimestamp"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Move with Timestamp"
            android:textSize="16sp"
            android:layout_marginBottom="24dp"/>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Result:"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <TextView
            android:id="@+id/tvResult"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="No operation performed yet"
            android:background="#F5F5F5"
            android:padding="12dp"
            android:textSize="12sp"/>

    </LinearLayout>
</ScrollView>
*/

// Example usage in any class:
/*
FileMover fileMover = new FileMover(context);

// Move single file
String sourcePath = "/storage/emulated/0/Pictures/1748629149938.jpg";
String destPath = "/storage/emulated/0/Android/data/com.hasanjaved.reportmate/files/Documents/NEL-SWGRTS1";
FileOperationResult result = fileMover.moveImageFile(sourcePath, destPath, null, false);

// Move multiple files
String[] sourceFiles = {
    "/storage/emulated/0/Pictures/image1.jpg",
    "/storage/emulated/0/Pictures/image2.jpg"
};
BatchOperationResult batchResult = fileMover.moveMultipleImageFiles(sourceFiles, destPath, false);

// Move with timestamp
FileOperationResult timestampResult = fileMover.moveImageWithTimestamp(sourcePath, destPath, "TestImage");
*/