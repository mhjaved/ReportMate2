package com.hasanjaved.reportmate.utility;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

public class FileMover2 {
    public static void moveImageToDocumentsSubfolder(Context context, String sourcePath, String newFileName, String targetSubfolder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                File sourceFile = new File(sourcePath);
                if (!sourceFile.exists()) {
                    Utility.showLog("Source file does not exist: " + sourcePath);
                    return;
                }

                String relativePath = Environment.DIRECTORY_DOCUMENTS + "/" + targetSubfolder;
                Uri collection = MediaStore.Files.getContentUri("external");

                // Delete existing file with same name and path if it exists
                String selection = MediaStore.MediaColumns.RELATIVE_PATH + "=? AND " +
                        MediaStore.MediaColumns.DISPLAY_NAME + "=?";
                String[] selectionArgs = new String[]{relativePath + "/", newFileName};

                ContentResolver resolver = context.getContentResolver();
                int deletedCount = resolver.delete(collection, selection, selectionArgs);
                if (deletedCount > 0) {
                    Utility.showLog("Existing file deleted before replacement: " + newFileName);
                }

                // Set up metadata for the new file
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, newFileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath);

                Uri newFileUri = resolver.insert(collection, values);

                if (newFileUri != null) {
                    try (FileInputStream in = new FileInputStream(sourceFile);
                         OutputStream out = resolver.openOutputStream(newFileUri)) {

                        byte[] buffer = new byte[4096];
                        int length;
                        while ((length = in.read(buffer)) > 0) {
                            out.write(buffer, 0, length);
                        }

                        // Delete original file after copy
                        boolean deleted = sourceFile.delete();
                        Utility.showLog("Moved and replaced: " + relativePath + "/" + newFileName +
                                ". Original deleted: " + deleted);
                    }
                } else {
                    Utility.showLog("Failed to create new file in MediaStore.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                Utility.showLog("Error: " + e.getMessage());
            }
        } else {
            Utility.showLog("This operation requires Android 10+ (API 29+).");
        }
    }

}
