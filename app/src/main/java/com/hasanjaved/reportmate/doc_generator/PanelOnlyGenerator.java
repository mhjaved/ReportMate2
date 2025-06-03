package com.hasanjaved.reportmate.doc_generator;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.hasanjaved.reportmate.utility.Utility;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PanelOnlyGenerator {

    private static final String TAG = "PanelOnlyGenerator";
    private static final String REPORTMATE_DIRECTORY = "ReportMate";

    /**
     * Generate Panel General Pictures report only
     * @param context Application context
     * @param imageUrl Path to the capture image ("/storage/emulated/0/Pictures/Capture.jpg")
     * @return Success status
     */
    public static boolean generatePanelGeneralPicturesOnly(Context context, String imageUrl) {
        try {
            XWPFDocument document = new XWPFDocument();

            // Client information header
            createClientHeader(document);

            // Section title
            createSectionTitle(document);

            // Panel pictures table with images
            createPanelPicturesTable(document, imageUrl);

            // Save document to ReportMate folder in public Documents
            return saveDocument(context, document);

        } catch (Exception e) {
            Log.e(TAG, "Error generating panel pictures report", e);
            Toast.makeText(context, "Error generating report: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * Create client information header
     */
    private static void createClientHeader(XWPFDocument document) {
        XWPFParagraph clientPara = document.createParagraph();
        XWPFRun clientRun = clientPara.createRun();

        clientRun.setText("Client: New Era Fashions Mfrs.(BD)Ltd.");
        clientRun.setFontFamily("Arial");
        clientRun.setFontSize(11);
        clientRun.addBreak();

        clientRun.setText("Date: 23.02.2025");
        clientRun.addBreak();

        clientRun.setText("Report No.: NEL-SWGRTS-11122024-B-1");
        clientRun.addBreak();
        clientRun.addBreak();
    }

    /**
     * Create section title
     */
    private static void createSectionTitle(XWPFDocument document) {
        XWPFParagraph titlePara = document.createParagraph();
        XWPFRun titleRun = titlePara.createRun();

        titleRun.setText("6.2    DB-3.1 Panel General Pictures");
        titleRun.setBold(true);
        titleRun.setFontFamily("Arial");
        titleRun.setFontSize(14);
        titleRun.setColor("7CB342"); // Green color matching the image

        titlePara.createRun().addBreak();
        titlePara.createRun().addBreak();
    }

    /**
     * Create 2x2 panel pictures table
     */
    private static void createPanelPicturesTable(XWPFDocument document, String imageUrl) {
        // Create 2x2 table
        XWPFTable table = document.createTable(2, 2);
        setTableBorders(table);
        setTableWidth(table, 100);

        // Set equal column widths (50% each)
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(2500));
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            }
        }

        // Top row
        addImageCell(table.getRow(0).getCell(0), imageUrl, "Temperature & Humidity");
        addImageCell(table.getRow(0).getCell(1), imageUrl, "Panel");

        // Bottom row
        addImageCell(table.getRow(1).getCell(0), imageUrl, "Panel Inside");
        addEmptyCell(table.getRow(1).getCell(1), ""); // Empty cell as shown in image
    }

    /**
     * Add image to table cell with caption
     */
    private static void addImageCell(XWPFTableCell cell, String imageUrl, String caption) {
        try {
            // Clear default paragraph
            cell.removeParagraph(0);

            // Add caption
            XWPFParagraph captionPara = cell.addParagraph();
            captionPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun captionRun = captionPara.createRun();
            captionRun.setText(caption);
            captionRun.setBold(true);
            captionRun.setFontFamily("Arial");
            captionRun.setFontSize(10);

            // Check if image exists
            File imageFile = new File(imageUrl);
            if (imageFile.exists()) {
                // Add image
                XWPFParagraph imagePara = cell.addParagraph();
                imagePara.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun imageRun = imagePara.createRun();

                // Calculate image dimensions
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imageUrl, options);

                int originalWidth = options.outWidth;
                int originalHeight = options.outHeight;

                // Scale to fit cell (approximately 380x280 pixels)
                int maxWidth = 380;
                int maxHeight = 280;
                int scaledWidth = maxWidth;
                int scaledHeight = maxHeight;

                if (originalWidth > 0 && originalHeight > 0) {
                    double aspectRatio = (double) originalWidth / originalHeight;
                    if (aspectRatio > (double) maxWidth / maxHeight) {
                        // Image is wider - fit to width
                        scaledHeight = (int) (maxWidth / aspectRatio);
                    } else {
                        // Image is taller - fit to height
                        scaledWidth = (int) (maxHeight * aspectRatio);
                    }
                }

                // Add the image
                FileInputStream imageStream = new FileInputStream(imageFile);
                imageRun.addPicture(imageStream, XWPFDocument.PICTURE_TYPE_JPEG,
                        imageFile.getName(),
                        Units.toEMU(scaledWidth), Units.toEMU(scaledHeight));
                imageStream.close();

                // Add timestamp (as shown in the original image)
                if (caption.equals("Panel") || caption.equals("Panel Inside")) {
                    XWPFParagraph timestampPara = cell.addParagraph();
                    timestampPara.setAlignment(ParagraphAlignment.RIGHT);
                    XWPFRun timestampRun = timestampPara.createRun();
                    timestampRun.setText("Feb 9, 2025 9:24:31 PM");
                    timestampRun.setFontFamily("Arial");
                    timestampRun.setFontSize(8);
                    timestampRun.setColor("666666");

                    timestampRun.addBreak();
                    timestampRun.setText("Index number: 1928");
                    if (caption.equals("Panel Inside")) {
                        timestampRun.setText("Index number: 1930");
                    }
                }

            } else {
                // Image not found - add placeholder
                XWPFParagraph placeholderPara = cell.addParagraph();
                placeholderPara.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun placeholderRun = placeholderPara.createRun();
                placeholderRun.setText("[Image: " + imageUrl + "]");
                placeholderRun.setColor("888888");
                placeholderRun.setFontFamily("Arial");
                placeholderRun.setFontSize(10);

                placeholderRun.addBreak();
                placeholderRun.setText("Image not found");
                placeholderRun.setColor("FF0000");

                Log.w(TAG, "Image not found: " + imageUrl);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error adding image to cell: " + caption, e);
            addErrorCell(cell, caption, e.getMessage());
        }
    }

    /**
     * Add empty cell (for bottom right)
     */
    private static void addEmptyCell(XWPFTableCell cell, String caption) {
        // Clear default paragraph and leave empty
        cell.removeParagraph(0);
        XWPFParagraph emptyPara = cell.addParagraph();
        emptyPara.createRun().setText(""); // Empty cell
    }

    /**
     * Add error cell when image loading fails
     */
    private static void addErrorCell(XWPFTableCell cell, String caption, String error) {
        cell.removeParagraph(0);

        XWPFParagraph errorPara = cell.addParagraph();
        errorPara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun errorRun = errorPara.createRun();
        errorRun.setText(caption);
        errorRun.setBold(true);
        errorRun.setFontFamily("Arial");
        errorRun.setFontSize(10);
        errorRun.addBreak();
        errorRun.addBreak();

        errorRun.setText("[Error loading image]");
        errorRun.setColor("FF0000");
        errorRun.setBold(false);
        errorRun.setFontSize(9);
    }

    /**
     * Set table borders
     */
    private static void setTableBorders(XWPFTable table) {
        table.getCTTbl().getTblPr().addNewTblBorders();
        CTTblBorders borders = table.getCTTbl().getTblPr().getTblBorders();
        borders.addNewTop().setVal(STBorder.SINGLE);
        borders.addNewLeft().setVal(STBorder.SINGLE);
        borders.addNewRight().setVal(STBorder.SINGLE);
        borders.addNewBottom().setVal(STBorder.SINGLE);
        borders.addNewInsideH().setVal(STBorder.SINGLE);
        borders.addNewInsideV().setVal(STBorder.SINGLE);
    }

    /**
     * Set table width
     */
    private static void setTableWidth(XWPFTable table, int widthPercent) {
        CTTblWidth tblWidth = table.getCTTbl().getTblPr().addNewTblW();
        tblWidth.setType(STTblWidth.PCT);
        tblWidth.setW(BigInteger.valueOf(widthPercent * 50));
    }

    /**
     * Save document to public Documents/ReportMate folder
     */
    private static boolean saveDocument(Context context, XWPFDocument document) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "DB31_Panel_General_Pictures_" + timeStamp + ".docx";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return saveWithMediaStore(context, document, fileName);
            } else {
                return saveLegacy(context, document, fileName);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error saving document", e);
            Toast.makeText(context, "Error saving: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Save using MediaStore (Android 10+) to public Documents/ReportMate
     */
    private static boolean saveWithMediaStore(Context context, XWPFDocument document, String fileName) {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.Files.FileColumns.MIME_TYPE,
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            values.put(MediaStore.Files.FileColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOCUMENTS + "/" + REPORTMATE_DIRECTORY);

            ContentResolver resolver = context.getContentResolver();
            Uri uri = resolver.insert(MediaStore.Files.getContentUri("external"), values);

            if (uri != null) {
                OutputStream outputStream = resolver.openOutputStream(uri);
                document.write(outputStream);
                outputStream.close();
                document.close();

                Toast.makeText(context, "Panel Pictures saved to Documents/ReportMate/" + fileName,
                        Toast.LENGTH_LONG).show();
                Log.d(TAG, "Document saved via MediaStore: " + uri);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "MediaStore save failed", e);
            return saveLegacy(context, document, fileName);
        }
        return false;
    }

    /**
     * Save using legacy method (Android 9 and below) to public Documents/ReportMate
     */
    private static boolean saveLegacy(Context context, XWPFDocument document, String fileName) {
        try {
            File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY);

            if (!reportMateDir.exists()) {
                reportMateDir.mkdirs();
            }

            File docFile = new File(reportMateDir, fileName);
            FileOutputStream out = new FileOutputStream(docFile);
            document.write(out);
            out.close();
            document.close();

            // Make file visible in file managers
            android.media.MediaScannerConnection.scanFile(context,
                    new String[]{docFile.getAbsolutePath()},
                    new String[]{"application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
                    null);

            Toast.makeText(context, "Panel Pictures saved: " + docFile.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "Document saved: " + docFile.getAbsolutePath());
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Legacy save failed", e);
            Toast.makeText(context, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}

// Usage in your Activity:
/*
public class MainActivity extends AppCompatActivity {
    
    private void generatePanelPicturesOnly() {
        String imageUrl = "/storage/emulated/0/Pictures/Capture.jpg";
        
        boolean success = PanelOnlyGenerator.generatePanelGeneralPicturesOnly(this, imageUrl);
        
        if (success) {
            Log.d("MainActivity", "Panel General Pictures report generated successfully");
            // File saved to: /storage/emulated/0/Documents/ReportMate/DB31_Panel_General_Pictures_[timestamp].docx
        } else {
            Log.e("MainActivity", "Failed to generate Panel Pictures report");
        }
    }
    
    // Add button click listener
    Button generatePanelButton = findViewById(R.id.btnGeneratePanel);
    generatePanelButton.setOnClickListener(v -> {
        if (checkPermissions()) {
            generatePanelPicturesOnly();
        } else {
            requestPermissions();
        }
    });
}
*/