package com.hasanjaved.reportmate.utility.not;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DocumentGenerator {

    private static final String TAG = "DocumentGenerator";

    /**
     * Generate a Word document with text and images
     * @param context Application context
     * @param fileName Output file name
     * @param textContent Text to add to document
     * @param imagePaths Array of local image file paths
     * @return File path of generated document, or null if failed
     */
    public static String generateDocument(Context context, String fileName,
                                          String textContent, String[] imagePaths) {
        try {
            // Create a new document
            XWPFDocument document = new XWPFDocument();

            // Add title
            XWPFParagraph titleParagraph = document.createParagraph();
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("j Generated Document");
            titleRun.setBold(true);
            titleRun.setFontSize(18);
            titleRun.addBreak();

            // Add text content
            XWPFParagraph textParagraph = document.createParagraph();
            XWPFRun textRun = textParagraph.createRun();
            textRun.setText(textContent);
            textRun.setFontSize(12);
            textRun.addBreak();

            // Add images if provided
            if (imagePaths != null && imagePaths.length > 0) {
                for (String imagePath : imagePaths) {
                    addImageToDocument(document, imagePath);
                }
            }

            // Save document to external storage
            File documentsDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), "GeneratedDocs");

            if (!documentsDir.exists()) {
                documentsDir.mkdirs();
            }

            File outputFile = new File(documentsDir, fileName + ".docx");
            FileOutputStream out = new FileOutputStream(outputFile);
            document.write(out);
            out.close();
            document.close();

            Log.d(TAG, "Document generated successfully: " + outputFile.getAbsolutePath());
            return outputFile.getAbsolutePath();

        } catch (Exception e) {
            Log.e(TAG, "Error generating document", e);
            return null;
        }
    }

    /**
     * Add an image to the document
     * @param document XWPFDocument instance
     * @param imagePath Local path to image file
     */
    private static void addImageToDocument(XWPFDocument document, String imagePath) {
        try {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                Log.w(TAG, "Image file not found: " + imagePath);
                return;
            }

            // Create paragraph for image
            XWPFParagraph imageParagraph = document.createParagraph();
            XWPFRun imageRun = imageParagraph.createRun();

            // Get image dimensions for proper scaling
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);

            int originalWidth = options.outWidth;
            int originalHeight = options.outHeight;

            // Calculate scaled dimensions (max width: 400px)
            int maxWidth = 400;
            int scaledWidth = Math.min(originalWidth, maxWidth);
            int scaledHeight = (originalHeight * scaledWidth) / originalWidth;

            // Add image to document
            InputStream imageStream = new FileInputStream(imageFile);

            // Determine image format
            int format = getImageFormat(imagePath);

            imageRun.addPicture(imageStream, format, imageFile.getName(),
                    Units.toEMU(scaledWidth), Units.toEMU(scaledHeight));

            imageStream.close();

            // Add line break after image
            imageRun.addBreak();

        } catch (Exception e) {
            Log.e(TAG, "Error adding image: " + imagePath, e);
        }
    }

    /**
     * Determine image format based on file extension
     * @param imagePath Path to image file
     * @return POI image format constant
     */
    private static int getImageFormat(String imagePath) {
        String extension = imagePath.toLowerCase();
        if (extension.endsWith(".png")) {
            return XWPFDocument.PICTURE_TYPE_PNG;
        } else if (extension.endsWith(".jpg") || extension.endsWith(".jpeg")) {
            return XWPFDocument.PICTURE_TYPE_JPEG;
        } else if (extension.endsWith(".gif")) {
            return XWPFDocument.PICTURE_TYPE_GIF;
        } else if (extension.endsWith(".bmp")) {
            return XWPFDocument.PICTURE_TYPE_BMP;
        } else {
            return XWPFDocument.PICTURE_TYPE_JPEG; // Default to JPEG
        }
    }

    /**
     * Generate document from assets folder images
     * @param context Application context
     * @param fileName Output file name
     * @param textContent Text content
     * @param assetImageNames Array of image names in assets folder
     * @return File path of generated document
     */
    public static String generateDocumentFromAssets(Context context, String fileName,
                                                    String textContent, String[] assetImageNames) {
        try {
            XWPFDocument document = new XWPFDocument();

            // Add title
            XWPFParagraph titleParagraph = document.createParagraph();
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("Document with Assets Images");
            titleRun.setBold(true);
            titleRun.setFontSize(18);
            titleRun.addBreak();

            // Add text content
            XWPFParagraph textParagraph = document.createParagraph();
            XWPFRun textRun = textParagraph.createRun();
            textRun.setText(textContent);
            textRun.setFontSize(12);
            textRun.addBreak();

            // Add images from assets
            if (assetImageNames != null && assetImageNames.length > 0) {
                for (String imageName : assetImageNames) {
                    addImageFromAssets(context, document, imageName);
                }
            }

            // Save document
            File documentsDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), "GeneratedDocs");

            if (!documentsDir.exists()) {
                documentsDir.mkdirs();
            }

            File outputFile = new File(documentsDir, fileName + ".docx");
            FileOutputStream out = new FileOutputStream(outputFile);
            document.write(out);
            out.close();
            document.close();

            return outputFile.getAbsolutePath();

        } catch (Exception e) {
            Log.e(TAG, "Error generating document from assets", e);
            return null;
        }
    }

    /**
     * Add image from assets folder
     * @param context Application context
     * @param document XWPFDocument instance
     * @param imageName Name of image in assets folder
     */
    private static void addImageFromAssets(Context context, XWPFDocument document, String imageName) {
        try {
            InputStream imageStream = context.getAssets().open(imageName);

            XWPFParagraph imageParagraph = document.createParagraph();
            XWPFRun imageRun = imageParagraph.createRun();

            // Get image format
            int format = getImageFormat(imageName);

            // Add image with fixed dimensions (adjust as needed)
            imageRun.addPicture(imageStream, format, imageName,
                    Units.toEMU(300), Units.toEMU(200));

            imageRun.addBreak();
            imageStream.close();

        } catch (IOException | InvalidFormatException e) {
            Log.e(TAG, "Error adding image from assets: " + imageName, e);
        }
    }
}