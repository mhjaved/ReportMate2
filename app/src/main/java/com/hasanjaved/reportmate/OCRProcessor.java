package com.hasanjaved.reportmate;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

//import com.googlecode.tesseract.android.TessBaseAPI;
import com.hasanjaved.reportmate.utility.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OCRProcessor {

//    private TessBaseAPI tessBaseAPI;
//
//    public OCRProcessor(Context context) {
//        Log.d(Utility.TAG, "Context context OCRProcessor ");
//        tessBaseAPI = new TessBaseAPI();
//        String dataPath = context.getFilesDir() + "/";
//        checkAndCopyTessData(context, dataPath);
//        tessBaseAPI.init(dataPath, "eng"); // Use 'eng' for English
//        tessBaseAPI.setVariable("tessedit_char_whitelist", "0123456789:.°C");
////        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK);
//
//    }
//
//    public String extractText(Bitmap bitmap) {
//        tessBaseAPI.setImage(bitmap);
//        return tessBaseAPI.getUTF8Text();
//    }
//
//    public void release() {
//        if (tessBaseAPI != null) {
////            tessBaseAPI.end();
//            tessBaseAPI.recycle();
//            tessBaseAPI = null;
//        }
//    }

    private void checkAndCopyTessData(Context context, String dataPath) {
        File dir = new File(dataPath );
        if (!dir.exists()) {
            dir.mkdirs();
        }

//        File file = new File(dir, "tessdata/eng.traineddata");
        File file = new File(dir, "tessdata/lets.traineddata");
        if (!file.exists()) {
            try {
//                InputStream in = context.getAssets().open("tessdata/eng.traineddata");
                InputStream in = context.getAssets().open("tessdata/lets.traineddata");
                OutputStream out = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int read;

                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }

                in.close();
                out.flush();
                out.close();
            } catch (IOException e) {
                Log.d(Utility.TAG,"j "+ e);
                e.printStackTrace();
            }
        }
    }
}
