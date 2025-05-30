package com.hasanjaved.reportmate.utility;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;
import java.io.File;

public class DirectoryManager {

    private Context context;

    public DirectoryManager(Context context) {
        this.context = context;
    }

    /**
     * Creates a new directory inside the provided parent directory
     * @param parentDirectoryPath The parent directory path (e.g., "/storage/emulated/0/Android/data/com.hasanjaved.reportmate/files/Documents/NEL-SWGRTS1")
     * @param newDirectoryName The name of the new directory to create
     * @return DirectoryResult object containing success status and directory path
     */
    public DirectoryResult createDirectory(String parentDirectoryPath, String newDirectoryName) {
        try {
            // Validate input parameters
            if (parentDirectoryPath == null || parentDirectoryPath.trim().isEmpty()) {
                return new DirectoryResult(false, null, "Parent directory path cannot be empty");
            }

            if (newDirectoryName == null || newDirectoryName.trim().isEmpty()) {
                return new DirectoryResult(false, null, "New directory name cannot be empty");
            }

            // Remove any leading/trailing whitespace
            parentDirectoryPath = parentDirectoryPath.trim();
            newDirectoryName = newDirectoryName.trim();

            // Check if parent directory exists
            File parentDirectory = new File(parentDirectoryPath);
            if (!parentDirectory.exists()) {
                return new DirectoryResult(false, null, "Parent directory does not exist: " + parentDirectoryPath);
            }

            if (!parentDirectory.isDirectory()) {
                return new DirectoryResult(false, null, "Parent path is not a directory: " + parentDirectoryPath);
            }

            // Create the new directory path
            String newDirectoryPath = parentDirectoryPath + File.separator + newDirectoryName;
            File newDirectory = new File(newDirectoryPath);

            // Check if directory already exists
            if (newDirectory.exists()) {
                if (newDirectory.isDirectory()) {
                    return new DirectoryResult(true, newDirectoryPath, "Directory already exists: " + newDirectoryPath);
                } else {
                    return new DirectoryResult(false, null, "A file with the same name already exists: " + newDirectoryPath);
                }
            }

            // Create the directory
            boolean success = newDirectory.mkdirs();

            if (success) {
                return new DirectoryResult(true, newDirectoryPath, "Directory created successfully: " + newDirectoryPath);
            } else {
                return new DirectoryResult(false, null, "Failed to create directory: " + newDirectoryPath);
            }

        } catch (SecurityException e) {
            return new DirectoryResult(false, null, "Permission denied: " + e.getMessage());
        } catch (Exception e) {
            return new DirectoryResult(false, null, "Error creating directory: " + e.getMessage());
        }
    }

    /**
     * Creates multiple nested directories
     * @param parentDirectoryPath The parent directory path
     * @param directoryNames Array of directory names to create (nested)
     * @return DirectoryResult object containing success status and final directory path
     */
    public DirectoryResult createNestedDirectories(String parentDirectoryPath, String[] directoryNames) {
        if (directoryNames == null || directoryNames.length == 0) {
            return new DirectoryResult(false, null, "Directory names array cannot be empty");
        }

        String currentPath = parentDirectoryPath;

        for (String dirName : directoryNames) {
            DirectoryResult result = createDirectory(currentPath, dirName);
            if (!result.isSuccess()) {
                return result;
            }
            currentPath = result.getDirectoryPath();
        }

        return new DirectoryResult(true, currentPath, "Nested directories created successfully");
    }

    /**
     * Checks if a directory exists
     * @param directoryPath The directory path to check
     * @return true if directory exists, false otherwise
     */
    public boolean directoryExists(String directoryPath) {
        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            return false;
        }

        File directory = new File(directoryPath.trim());
        return directory.exists() && directory.isDirectory();
    }

    /**
     * Lists all subdirectories in a given directory
     * @param directoryPath The directory path to list
     * @return Array of subdirectory names, or null if error
     */
    public String[] listSubdirectories(String directoryPath) {
        try {
            File directory = new File(directoryPath);
            if (!directory.exists() || !directory.isDirectory()) {
                return null;
            }

            File[] files = directory.listFiles();
            if (files == null) {
                return new String[0];
            }

            java.util.List<String> subdirs = new java.util.ArrayList<>();
            for (File file : files) {
                if (file.isDirectory()) {
                    subdirs.add(file.getName());
                }
            }

            return subdirs.toArray(new String[0]);

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Deletes a directory and all its contents
     * @param directoryPath The directory path to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteDirectory(String directoryPath) {
        try {
            File directory = new File(directoryPath);
            return deleteDirectoryRecursive(directory);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean deleteDirectoryRecursive(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!deleteDirectoryRecursive(file)) {
                        return false;
                    }
                }
            }
        }
        return directory.delete();
    }
}

// DirectoryResult.java
class DirectoryResult {
    private boolean success;
    private String directoryPath;
    private String message;

    public DirectoryResult(boolean success, String directoryPath, String message) {
        this.success = success;
        this.directoryPath = directoryPath;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "DirectoryResult{" +
                "success=" + success +
                ", directoryPath='" + directoryPath + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

// MainActivity.java - Example usage
//package com.hasanjaved.reportmate;
//
//        import android.Manifest;
//        import android.content.pm.PackageManager;
//        import android.os.Bundle;
//        import android.widget.Button;
//        import android.widget.EditText;
//        import android.widget.TextView;
//        import android.widget.Toast;
//        import androidx.appcompat.app.AppCompatActivity;
//        import androidx.core.app.ActivityCompat;
//        import androidx.core.content.ContextCompat;

//public class MainActivity extends AppCompatActivity {
//
//    private static final int PERMISSION_REQUEST_CODE = 1;
//    private static final String[] REQUIRED_PERMISSIONS = {
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE
//    };
//
//    private EditText etParentPath;
//    private EditText etNewDirName;
//    private Button btnCreateDirectory;
//    private TextView tvResult;
//    private DirectoryManager directoryManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        initializeViews();
//        directoryManager = new DirectoryManager(this);
//
//        // Set default parent path
//        etParentPath.setText("/storage/emulated/0/Android/data/com.hasanjaved.reportmate/files/Documents/NEL-SWGRTS1");
//
//        btnCreateDirectory.setOnClickListener(v -> {
//            if (checkPermissions()) {
//                createNewDirectory();
//            } else {
//                requestPermissions();
//            }
//        });
//    }
//
//    private void initializeViews() {
//        etParentPath = findViewById(R.id.etParentPath);
//        etNewDirName = findViewById(R.id.etNewDirName);
//        btnCreateDirectory = findViewById(R.id.btnCreateDirectory);
//        tvResult = findViewById(R.id.tvResult);
//    }
//
//    private void createNewDirectory() {
//        String parentPath = etParentPath.getText().toString().trim();
//        String newDirName = etNewDirName.getText().toString().trim();
//
//        if (parentPath.isEmpty()) {
//            showToast("Please enter parent directory path");
//            return;
//        }
//
//        if (newDirName.isEmpty()) {
//            showToast("Please enter new directory name");
//            return;
//        }
//
//        // Create directory
//        DirectoryResult result = directoryManager.createDirectory(parentPath, newDirName);
//
//        // Display result
//        tvResult.setText(result.getMessage());
//
//        if (result.isSuccess()) {
//            showToast("Directory created successfully!");
//
//            // Clear the new directory name field for next use
//            etNewDirName.setText("");
//
//            // Optionally list subdirectories
//            listSubdirectories(parentPath);
//        } else {
//            showToast("Failed to create directory: " + result.getMessage());
//        }
//    }
//
//    private void listSubdirectories(String parentPath) {
//        String[] subdirs = directoryManager.listSubdirectories(parentPath);
//        if (subdirs != null && subdirs.length > 0) {
//            StringBuilder sb = new StringBuilder();
//            sb.append("\nSubdirectories in parent folder:\n");
//            for (String subdir : subdirs) {
//                sb.append("• ").append(subdir).append("\n");
//            }
//            tvResult.append(sb.toString());
//        }
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
//                createNewDirectory();
//            } else {
//                showToast("Storage permissions are required to create directories");
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
            android:text="Directory Creator"
            android:textSize="20sp"
            android:textStyle="bold"
            android:layout_marginBottom="24dp"
            android:layout_gravity="center_horizontal"/>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Parent Directory Path:"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <EditText
            android:id="@+id/etParentPath"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="e.g., /storage/emulated/0/Android/data/com.hasanjaved.reportmate/files/Documents/NEL-SWGRTS1"
            android:inputType="text"
            android:maxLines="3"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="New Directory Name:"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <EditText
            android:id="@+id/etNewDirName"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Enter new directory name"
            android:inputType="text"
            android:layout_marginBottom="24dp"/>

        <Button
            android:id="@+id/btnCreateDirectory"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Create Directory"
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

// AndroidManifest.xml (Add these permissions)
/*
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
*/

// Example usage in any Activity or Service:
/*
DirectoryManager dirManager = new DirectoryManager(this);

// Create single directory
String parentPath = "/storage/emulated/0/Android/data/com.hasanjaved.reportmate/files/Documents/NEL-SWGRTS1";
String newDirName = "Test_Report_2024";
DirectoryResult result = dirManager.createDirectory(parentPath, newDirName);

if (result.isSuccess()) {
    Log.d("Directory", "Created: " + result.getDirectoryPath());
} else {
    Log.e("Directory", "Error: " + result.getMessage());
}

// Create nested directories
String[] nestedDirs = {"Reports", "2024", "March"};
DirectoryResult nestedResult = dirManager.createNestedDirectories(parentPath, nestedDirs);
*/