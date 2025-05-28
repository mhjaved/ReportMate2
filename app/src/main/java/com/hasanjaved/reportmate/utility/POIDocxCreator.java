package com.hasanjaved.reportmate.utility;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import org.apache.poi.util.Units;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

public class POIDocxCreator {

//    public static void createDocWithTextAndImages(Context context, List<String> paragraphs, List<String> imagePaths) {
//        try {
//            // 1. Create the document
//            XWPFDocument document = new XWPFDocument();
//
//            // 2. Add multiple paragraphs
//            for (String text : paragraphs) {
//                XWPFParagraph paragraph = document.createParagraph();
//                XWPFRun run = paragraph.createRun();
//                run.setText(text);
//                run.setFontSize(14);
//                run.setFontFamily("Calibri");
//            }
//
//            // 3. Add multiple images
//            for (String imagePath : imagePaths) {
//                File imageFile = new File(imagePath);
//                if (!imageFile.exists()) continue;
//
//                FileInputStream is = new FileInputStream(imageFile);
//                XWPFParagraph imageParagraph = document.createParagraph();
//                XWPFRun imageRun = imageParagraph.createRun();
//
//                String imgExt = imagePath.substring(imagePath.lastIndexOf(".") + 1).toLowerCase();
//                int format = getImageFormat(imgExt);
//
//                imageRun.addPicture(is, format, imageFile.getName(), Units.toEMU(300), Units.toEMU(200));
//                is.close();
//            }
//
//            // 4. Save the document
//            File outDir = new File(context.getExternalFilesDir(null), "generated_docs");
//            if (!outDir.exists()) outDir.mkdirs();
//
//            File outFile = new File(outDir, "poi_output.docx");
//            FileOutputStream out = new FileOutputStream(outFile);
//            document.write(out);
//            out.close();
//
//            Toast.makeText(context, "DOCX saved at: " + outFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }
//
//    private static int getImageFormat(String ext) {
//        switch (ext) {
//            case "emf": return Document.PICTURE_TYPE_EMF;
//            case "wmf": return Document.PICTURE_TYPE_WMF;
//            case "pict": return Document.PICTURE_TYPE_PICT;
//            case "jpeg":
//            case "jpg": return Document.PICTURE_TYPE_JPEG;
//            case "png": return Document.PICTURE_TYPE_PNG;
//            case "dib": return Document.PICTURE_TYPE_DIB;
//            case "gif": return Document.PICTURE_TYPE_GIF;
//            case "tiff": return Document.PICTURE_TYPE_TIFF;
//            case "eps": return Document.PICTURE_TYPE_EPS;
//            case "bmp": return Document.PICTURE_TYPE_BMP;
//            case "wpg": return Document.PICTURE_TYPE_WPG;
//            default: return Document.PICTURE_TYPE_PNG;
//        }
//    }
//
}
