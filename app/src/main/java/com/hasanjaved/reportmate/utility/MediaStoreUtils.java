package com.hasanjaved.reportmate.utility;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class MediaStoreUtils {

    public static void createSubFolderInDocuments(Context context, String parentFolder, String subFolder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Example: Documents/MyFolder/SubFolder
            String relativePath = Environment.DIRECTORY_DOCUMENTS + "/" + parentFolder + "/" + subFolder;

            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, ".placeholder"); // dummy file to trigger folder creation
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath);

            try {
                Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                if (uri != null) {
                    Log.d("MediaStoreUtils", "Subfolder created: " + relativePath);
                } else {
                    Log.e("MediaStoreUtils", "Failed to create subfolder.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("MediaStoreUtils", "Error: " + e.getMessage());
            }
        } else {
            Log.e("MediaStoreUtils", "This requires Android 10 (API 29) or higher.");
        }
    }
    public static void createFolderInDocuments(Context context, String folderName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Define the relative path within the Documents directory
            String relativePath = Environment.DIRECTORY_DOCUMENTS + "/" + folderName;

            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, ".placeholder"); // dummy file
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath);

            try {
                Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                if (uri != null) {
                    Log.d("MediaStoreUtils", "Folder created via MediaStore: " + relativePath);
                } else {
                    Log.e("MediaStoreUtils", "Failed to create folder.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("MediaStoreUtils", "Error: " + e.getMessage());
            }
        } else {
            Log.e("MediaStoreUtils", "MediaStore scoped storage is only available on Android 10+");
        }
    }
}
