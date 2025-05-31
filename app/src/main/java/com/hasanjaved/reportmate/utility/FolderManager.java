package com.hasanjaved.reportmate.utility;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

public class FolderManager {

    private static final String TAG = Utility.TAG;

    public static boolean isBaseFolderAvailable(Context context){
       return doesFolderExist(context,Utility.BASE_FOLDER_NAME);
    }


    public static String createBaseFolder(Context context){
       return createFolder(context, Utility.BASE_FOLDER_NAME);
    }

    /**
     * Create ReportMate folder in Documents directory
     * @param context Application context
     * @return Folder path or null if failed
     */
    public static String createFolder(Context context, String folderName) {
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
    public static boolean doesFolderExist(Context context, String folderName) {
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

    public static String getLinkIfFolderExist(Context context, String folderName) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Check app-specific directory
                File appDocsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                File reportMateDir = new File(appDocsDir, folderName);
                if (reportMateDir.exists() && reportMateDir.isDirectory()) {
                    return reportMateDir.getAbsolutePath();
                }
            } else {
                // Check public Documents directory
                File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                File reportMateDir = new File(documentsDir, folderName);
                if (reportMateDir.exists() && reportMateDir.isDirectory()) {
                    return reportMateDir.getAbsolutePath();
                }
            }
            return null;
        } catch (Exception e) {
            return null;
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

    public static String getFolderPathIfExists(Context context, String folderName) {
        try {
            File documentsDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), folderName);

            if (documentsDir.exists() && documentsDir.isDirectory()) {
                return documentsDir.getAbsolutePath();
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }
    public static String getFolderPathIfExists(String folderName) {
        try {
            File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File targetFolder = new File(documentsDir, folderName);

            if (targetFolder.exists() && targetFolder.isDirectory()) {
                return targetFolder.getAbsolutePath();
            }

            return null;
        } catch (Exception e) {
            return null;
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

    public static String checkFolderInDirectory(Context context, String directoryPath, String folderName) {
        try {
            File parentDirectory = new File(directoryPath);

            // Check if parent directory exists
            if (!parentDirectory.exists() || !parentDirectory.isDirectory()) {
                return null;
            }

            File targetFolder = new File(parentDirectory, folderName);

            // Check if folder exists and is a directory
            if (targetFolder.exists() && targetFolder.isDirectory()) {
                return targetFolder.getAbsolutePath();
            }

            return null;

        } catch (Exception e) {
            return null;
        }
    }


    private static String createFolderInPublicDocs(Context context, String directoryPath, String folderName) {
        try {
            // For public documents, try using the app-specific approach instead
            File appDocsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (appDocsDir != null) {
                File newFolder = new File(appDocsDir, folderName);
                if (!newFolder.exists()) {
                    boolean created = newFolder.mkdirs();
                    if (created) {
                        Utility.showLog("Created folder in app-specific docs: " + newFolder.getAbsolutePath());
                        return newFolder.getAbsolutePath();
                    }
                } else if (newFolder.isDirectory()) {
                    return newFolder.getAbsolutePath();
                }
            }
            return null;
        } catch (Exception e) {
            Utility.showLog("Exception creating folder in public docs: " + e.getMessage());
            return null;
        }
    }


    public static String createFolderInDirectory(Context context, String directoryPath, String folderName) {
        try {
            File parentDirectory = new File(directoryPath);

            // Check if parent directory exists
            if (!parentDirectory.exists() || !parentDirectory.isDirectory()) {
                return null;
            }

            File newFolder = new File(parentDirectory, folderName);

            // Check if folder already exists
            if (newFolder.exists()) {
                return newFolder.isDirectory() ? newFolder.getAbsolutePath() : null;
            }

            // Create the new folder
            if (newFolder.mkdirs()) {
                // Make folder visible by creating a .nomedia file or refreshing media scanner
                makeFolderVisible(context, newFolder);
                return newFolder.getAbsolutePath();
            }

            return null;

        } catch (Exception e) {
            Utility.showLog("Exception: " + e);
            return null;
        }
    }

    // Method to make folder visible in file managers
    private static void makeFolderVisible(Context context, File folder) {
        try {
            // Method 1: Trigger media scanner for the specific folder
            refreshMediaScanner(context, folder.getAbsolutePath());

            // Method 2: Create a placeholder file to make folder visible
            createPlaceholderFile(folder);

            // Method 3: Notify file system about the new folder
            notifyFileSystem(context, folder);

        } catch (Exception e) {
            Utility.showLog("Error making folder visible: " + e.getMessage());
        }
    }

    // Refresh media scanner for the folder
    private static void refreshMediaScanner(Context context, String folderPath) {
        try {
            // Method 1: MediaScannerConnection
            MediaScannerConnection.scanFile(context, new String[]{folderPath}, null, null);

            // Method 2: Broadcast intent
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(new File(folderPath)));
            context.sendBroadcast(mediaScanIntent);

        } catch (Exception e) {
            Utility.showLog("Error refreshing media scanner: " + e.getMessage());
        }
    }

    // Create a placeholder file to make folder visible
    private static void createPlaceholderFile(File folder) {
        try {
            // Option 1: Create a .placeholder file
            File placeholder = new File(folder, ".placeholder");
            if (!placeholder.exists()) {
                placeholder.createNewFile();
            }

            // Option 2: Create a README.txt file
            File readme = new File(folder, "README.txt");
            if (!readme.exists()) {
                FileWriter writer = new FileWriter(readme);
                writer.write("This folder was created by your app.");
                writer.close();
            }

        } catch (Exception e) {
            Utility.showLog("Error creating placeholder file: " + e.getMessage());
        }
    }

    // Notify file system about new folder
    private static void notifyFileSystem(Context context, File folder) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // Use DocumentFile for better compatibility
                DocumentFile.fromFile(folder);
            }

            // Force refresh parent directory
            File parentDir = folder.getParentFile();
            if (parentDir != null) {
                MediaScannerConnection.scanFile(context,
                        new String[]{parentDir.getAbsolutePath()}, null, null);
            }

        } catch (Exception e) {
            Utility.showLog("Error notifying file system: " + e.getMessage());
        }
    }

    // Alternative method using MediaStore for Android 10+
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static void createFolderUsingMediaStore(Context context, String folderName) {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, folderName);
            values.put(MediaStore.Files.FileColumns.MIME_TYPE, DocumentsContract.Document.MIME_TYPE_DIR);
            values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

            Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
            if (uri != null) {
                Utility.showLog("Folder created using MediaStore: " + uri.toString());
            }

        } catch (Exception e) {
            Utility.showLog("Error creating folder using MediaStore: " + e.getMessage());
        }
    }

    // Comprehensive folder creation with visibility fix
    public static String createVisibleFolder(Context context, String directoryPath, String folderName) {
        try {
            // First try normal folder creation
            String folderPath = createFolderInDirectory(context, directoryPath, folderName);

            if (folderPath != null) {
                File folder = new File(folderPath);

                // Ensure folder is visible
                makeFolderVisible(context, folder);

                // Additional steps for better visibility
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // For Android 10+, also try MediaStore approach
                    createFolderUsingMediaStore(context, folderName);
                }

                // Force refresh after a short delay
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    refreshMediaScanner(context, folderPath);
                }, 1000);

                return folderPath;
            }

            return null;

        } catch (Exception e) {
            Utility.showLog("Error in createVisibleFolder: " + e.getMessage());
            return null;
        }
    }
//    public static String createFolderInDirectory(Context context, String directoryPath, String folderName) {
//        try {
//            File parentDirectory = new File(directoryPath);
//
//            // Check if parent directory exists
//            if (!parentDirectory.exists() || !parentDirectory.isDirectory()) {
//                return null;
//            }
//
//            File newFolder = new File(parentDirectory, folderName);
//
//            // Check if folder already exists
//            if (newFolder.exists()) {
//                return newFolder.isDirectory() ? newFolder.getAbsolutePath() : null;
//            }
//
//            // Create the new folder
//            if (newFolder.mkdirs()) {
//                return newFolder.getAbsolutePath();
//            }
//
//            return null;
//
//        } catch (Exception e) {
//            Utility.showLog(" e "+e);
//            return null;
//        }
//    }
//    public static boolean createFolderInDirectory(Context context, String directoryPath, String folderName) {
//        try {
//            File parentDirectory = new File(directoryPath);
//
//            // Check if parent directory exists
//            if (!parentDirectory.exists() || !parentDirectory.isDirectory()) {
//                return false;
//            }
//
//            File newFolder = new File(parentDirectory, folderName);
//
//            // Check if folder already exists
//            if (newFolder.exists()) {
//                return newFolder.isDirectory(); // Return true if it's already a directory
//            }
//
//            // Create the new folder
//            return newFolder.mkdirs();
//
//        } catch (Exception e) {
//            return false;
//        }
//    }
}