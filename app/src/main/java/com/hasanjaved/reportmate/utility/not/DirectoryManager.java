package com.hasanjaved.reportmate.utility.not;

import android.content.Context;

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