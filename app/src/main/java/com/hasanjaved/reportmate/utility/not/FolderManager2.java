package com.hasanjaved.reportmate.utility.not;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import com.hasanjaved.reportmate.utility.Utility;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.Date;

public class FolderManager2 {

    private static final String TAG = Utility.TAG;

    public static String createFolderInAppStorage(Context context, String folderName) {
        try {
            // Get the app-specific Documents directory
            File appDocsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (appDocsDir == null) {
                Utility.showLog("External storage not available");
                return null;
            }

            // Create ReportMate folder first if it doesn't exist
            File reportMateDir = new File(appDocsDir, "ReportMate");
            if (!reportMateDir.exists()) {
                boolean reportMateCreated = reportMateDir.mkdirs();
                Utility.showLog("ReportMate folder created: " + reportMateCreated);
            }

            // Create the project folder inside ReportMate
            File projectFolder = new File(reportMateDir, folderName);

            if (projectFolder.exists()) {
                if (projectFolder.isDirectory()) {
                    Utility.showLog("Folder already exists: " + projectFolder.getAbsolutePath());
                    return projectFolder.getAbsolutePath();
                } else {
                    Utility.showLog("File with same name exists, deleting...");
                    projectFolder.delete();
                }
            }

            // Create the folder
            boolean created = projectFolder.mkdirs();
            if (created || projectFolder.exists()) {
                Utility.showLog("Project folder created: " + projectFolder.getAbsolutePath());

                // Create a dummy file to ensure folder is visible and not empty
                createDummyFile(projectFolder);

                return projectFolder.getAbsolutePath();
            } else {
                Utility.showLog("Failed to create project folder");
                return null;
            }

        } catch (Exception e) {
            Utility.showLog("Exception creating folder: " + e.getMessage());
            return null;
        }
    }

    // Specific method for your exact use case
    public static String createProjectFolder(Context context, String directoryPath, String projectName) {
        try {
            Utility.showLog("Creating folder: " + projectName + " in: " + directoryPath);

            // Verify the parent directory exists
            File parentDir = new File(directoryPath);
            if (!parentDir.exists()) {
                Utility.showLog("Parent directory doesn't exist: " + directoryPath);
                return null;
            }

            if (!parentDir.isDirectory()) {
                Utility.showLog("Parent path is not a directory: " + directoryPath);
                return null;
            }

            if (!parentDir.canWrite()) {
                Utility.showLog("No write permission for: " + directoryPath);
                return null;
            }

            // Create the project folder
            File projectFolder = new File(parentDir, projectName);

            Utility.showLog("Attempting to create: " + projectFolder.getAbsolutePath());

            if (projectFolder.exists()) {
                if (projectFolder.isDirectory()) {
                    Utility.showLog("Project folder already exists");
                    return projectFolder.getAbsolutePath();
                } else {
                    Utility.showLog("File exists with same name, deleting...");
                    boolean deleted = projectFolder.delete();
                    Utility.showLog("File deleted: " + deleted);
                }
            }

            // Create directory
            boolean created = projectFolder.mkdir(); // Use mkdir() instead of mkdirs()
            Utility.showLog("mkdir() result: " + created);

            // Double check if folder was created
            if (projectFolder.exists() && projectFolder.isDirectory()) {
                Utility.showLog("Folder verified to exist: " + projectFolder.getAbsolutePath());

                // Create a marker file to ensure visibility
                createMarkerFile(projectFolder);

                // List contents of parent directory to verify
                listDirectoryContents(parentDir);

                return projectFolder.getAbsolutePath();
            } else {
                Utility.showLog("Folder creation failed - folder does not exist after creation attempt");
                return null;
            }

        } catch (SecurityException e) {
            Utility.showLog("Security exception: " + e.getMessage());
            return null;
        } catch (Exception e) {
            Utility.showLog("Exception: " + e.getMessage());
            return null;
        }
    }

    // Create a marker file to make folder visible
    private static void createMarkerFile(File folder) {
        try {
            File markerFile = new File(folder, ".folder_created");
            if (!markerFile.exists()) {
                boolean created = markerFile.createNewFile();
                Utility.showLog("Marker file created: " + created);
            }
        } catch (Exception e) {
            Utility.showLog("Error creating marker file: " + e.getMessage());
        }
    }

    // Create a dummy file to ensure folder is not empty
    private static void createDummyFile(File folder) {
        try {
            File dummyFile = new File(folder, "info.txt");
            if (!dummyFile.exists()) {
                FileWriter writer = new FileWriter(dummyFile);
                writer.write("Project folder created on: " + new Date().toString());
                writer.close();
                Utility.showLog("Info file created in folder");
            }
        } catch (Exception e) {
            Utility.showLog("Error creating info file: " + e.getMessage());
        }
    }

    // Debug method to list directory contents
    private static void listDirectoryContents(File directory) {
        try {
            File[] files = directory.listFiles();
            if (files != null) {
                Utility.showLog("Directory contents (" + files.length + " items):");
                for (File file : files) {
                    Utility.showLog("- " + file.getName() + " (isDir: " + file.isDirectory() + ")");
                }
            } else {
                Utility.showLog("Unable to list directory contents or directory is empty");
            }
        } catch (Exception e) {
            Utility.showLog("Error listing directory: " + e.getMessage());
        }
    }

}