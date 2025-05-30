package com.hasanjaved.reportmate.utility;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class FolderManager {

    private static final String TAG = Utility.TAG;

    /**
     * Create ReportMate folder in Documents directory
     * @param context Application context
     * @return Folder path or null if failed
     */
    public static String createReportMateFolder(Context context, String folderName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ - Use scoped storage approach
            return createFolderScopedStorage(context,folderName);
        }
        else {
            // Android 9 and below - Direct folder creation
            return createFolderLegacy(folderName);
        }
    }

    /**
     * Create folder using legacy method (Android 9 and below)
     * @return Folder path or null if failed
     */
    private static String createFolderLegacy(String folderName) {
        try {
            File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File reportMateDir = new File(documentsDir, folderName);

            if (!reportMateDir.exists()) {
                boolean created = reportMateDir.mkdirs();
                if (created) {
                    Log.d(TAG, "ReportMate folder created successfully: " + reportMateDir.getAbsolutePath());
                    return reportMateDir.getAbsolutePath();
                } else {
                    Log.e(TAG, "Failed to create ReportMate folder");
                    return null;
                }
            } else {
                Log.d(TAG, "ReportMate folder already exists: " + reportMateDir.getAbsolutePath());
                return reportMateDir.getAbsolutePath();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error creating ReportMate folder (legacy)", e);
            return null;
        }
    }

    /**
     * Create folder using scoped storage (Android 10+)
     * @param context Application context
     * @return Folder path or null if failed
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static String createFolderScopedStorage(Context context, String folderName) {
        try {
            // Method 1: Create in app-specific Documents directory (always works)
            File appDocsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File reportMateDir = new File(appDocsDir, folderName);

            if (!reportMateDir.exists()) {
                boolean created = reportMateDir.mkdirs();
                if (created) {
                    Log.d(TAG, "ReportMate folder created in app directory: " + reportMateDir.getAbsolutePath());
                } else {
                    Log.e(TAG, "Failed to create ReportMate folder in app directory");
                }
            } else {
                Log.d(TAG, "ReportMate folder already exists in app directory: " + reportMateDir.getAbsolutePath());
            }

            // Method 2: Also create a placeholder in public Documents via MediaStore
            createPublicFolderPlaceholder(context,folderName);

            return reportMateDir.getAbsolutePath();

        } catch (Exception e) {
            Log.e(TAG, "Error creating ReportMate folder (scoped storage)", e);
            return null;
        }
    }

    /**
     * Create a placeholder file in public Documents to establish folder presence
     * @param context Application context
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static void createPublicFolderPlaceholder(Context context, String folderName) {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, ".reportmate_folder");
            values.put(MediaStore.Files.FileColumns.MIME_TYPE, "text/plain");
            values.put(MediaStore.Files.FileColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOCUMENTS + "/" + folderName);

            ContentResolver resolver = context.getContentResolver();
            Uri uri = resolver.insert(MediaStore.Files.getContentUri("external"), values);

            if (uri != null) {
                // Write a small placeholder content
                OutputStream outputStream = resolver.openOutputStream(uri);
                if (outputStream != null) {
                    outputStream.write("ReportMate folder created".getBytes());
                    outputStream.close();
                    Log.d(TAG, "Public Documents placeholder created for ReportMate folder");
                }
            }

        } catch (Exception e) {
            Log.w(TAG, "Could not create public folder placeholder", e);
            // This is not critical, so we just log a warning
        }
    }

    /**
     * Check if ReportMate folder exists
     * @param context Application context
     * @return true if folder exists, false otherwise
     */
    public static boolean doesReportMateFolderExist(Context context, String folderName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Check app-specific directory
            File appDocsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File reportMateDir = new File(appDocsDir, folderName);
            return reportMateDir.exists();
        } else {
            // Check public Documents directory
            File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File reportMateDir = new File(documentsDir, folderName);
            return reportMateDir.exists();
        }
    }

    /**
     * Get the ReportMate folder path
     * @param context Application context
     * @return Folder path or null if doesn't exist
     */
    public static String getReportMateFolderPath(Context context, String folderName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File appDocsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File reportMateDir = new File(appDocsDir, folderName);
            return reportMateDir.exists() ? reportMateDir.getAbsolutePath() : null;
        } else {
            File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File reportMateDir = new File(documentsDir, folderName);
            return reportMateDir.exists() ? reportMateDir.getAbsolutePath() : null;
        }
    }

    /**
     * Get ReportMate folder File object
     * @param context Application context
     * @return File object or null if doesn't exist
     */
    public static File getReportMateFolderFile(Context context, String folderName) {
        String folderPath = getReportMateFolderPath(context,folderName);
        return folderPath != null ? new File(folderPath) : null;
    }

    /**
     * List all files in ReportMate folder
     * @param context Application context
     * @return Array of file names, or empty array if folder doesn't exist
     */
    public static String[] listReportMateFiles(Context context, String folderName) {
        File reportMateDir = getReportMateFolderFile(context,folderName);
        if (reportMateDir != null && reportMateDir.exists() && reportMateDir.isDirectory()) {
            String[] files = reportMateDir.list();
            return files != null ? files : new String[0];
        }
        return new String[0];
    }

    /**
     * Delete ReportMate folder and all its contents
     * @param context Application context
     * @return true if deleted successfully, false otherwise
     */
    public static boolean deleteReportMateFolder(Context context, String folderName) {
        try {
            File reportMateDir = getReportMateFolderFile(context,folderName);
            if (reportMateDir != null && reportMateDir.exists()) {
                return deleteRecursively(reportMateDir);
            }
            return true; // Already doesn't exist
        } catch (Exception e) {
            Log.e(TAG, "Error deleting ReportMate folder", e);
            return false;
        }
    }

    /**
     * Recursively delete a directory and all its contents
     * @param file File or directory to delete
     * @return true if deleted successfully
     */
    private static boolean deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (!deleteRecursively(child)) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }
}