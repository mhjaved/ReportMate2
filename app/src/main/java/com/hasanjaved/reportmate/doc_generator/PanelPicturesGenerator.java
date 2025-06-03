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

public class PanelPicturesGenerator {

    private static final String TAG = "Javed";
    private static final String REPORTMATE_DIRECTORY = "ReportMate";


    public static boolean generatePanelGeneralPicturesReport(Context context, String imageUrl) {
        try {
            XWPFDocument document = new XWPFDocument();

            // Add header information
            createReportHeader(document);

            // Add panel general pictures section
            createPanelGeneralPicturesSection(document, imageUrl);

            // Save document
            return saveDocumentToReportMate(context, document);

        } catch (Exception e) {
            Utility.showLog( "Error generating panel pictures report"+e);
            Toast.makeText(context, "Error generating report: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * Create report header with client information
     */
    private static void createReportHeader(XWPFDocument document) {
        // Client information
        XWPFParagraph clientPara = document.createParagraph();
        XWPFRun clientRun = clientPara.createRun();
        clientRun.setText("Client: New Era Fashions Mfrs.(BD)Ltd.");
        clientRun.setFontSize(11);
        clientRun.addBreak();

        clientRun.setText("Date: 23.02.2025");
        clientRun.addBreak();

        clientRun.setText("Report No.: NEL-SWGRTS-11122024-B-1");
        clientRun.addBreak();
        clientRun.addBreak();

        // Section title
        XWPFParagraph titlePara = document.createParagraph();
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText("6.2    DB-3.1 Panel General Pictures");
        titleRun.setBold(true);
        titleRun.setFontSize(14);
        titleRun.setColor("90EE90"); // Light green color
        titleRun.addBreak();
        titleRun.addBreak();
    }

    /**
     * Create panel general pictures section with 2x2 image layout
     */
    private static void createPanelGeneralPicturesSection(XWPFDocument document, String imageUrl) {
        try {
            // Create a 2x2 table for images
            XWPFTable pictureTable = document.createTable(2, 2);
            setTableBorders(pictureTable);
            setTableWidth(pictureTable, 100);

            // Set equal column widths (50% each)
            for (XWPFTableRow row : pictureTable.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(2500));
                }
            }

            // Row 1, Cell 1: Temperature & Humidity
            XWPFTableCell tempHumidityCell = pictureTable.getRow(0).getCell(0);
            addImageToCell(tempHumidityCell, imageUrl, "Temperature & Humidity", 350, 250);

            // Row 1, Cell 2: Panel
            XWPFTableCell panelCell = pictureTable.getRow(0).getCell(1);
            addImageToCell(panelCell, imageUrl, "Panel", 350, 250);

            // Row 2, Cell 1: Panel Inside
            XWPFTableCell panelInsideCell = pictureTable.getRow(1).getCell(0);
            addImageToCell(panelInsideCell, imageUrl, "Panel Inside", 350, 250);

            // Row 2, Cell 2: Empty or additional image
            XWPFTableCell emptyCell = pictureTable.getRow(1).getCell(1);
            addPlaceholderToCell(emptyCell, "Additional Image", 350, 250);

        } catch (Exception e) {
            Utility.showLog( "Error creating panel pictures section"+e);
        }
    }

    /**
     * Add image to table cell with caption
     */
    private static void addImageToCell(XWPFTableCell cell, String imageUrl, String caption, int width, int height) {
        try {
            // Clear existing paragraphs
            cell.removeParagraph(0);

            // Add caption
            XWPFParagraph captionPara = cell.addParagraph();
            captionPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun captionRun = captionPara.createRun();
            captionRun.setText(caption);
            captionRun.setBold(true);
            captionRun.setFontSize(10);
            captionRun.addBreak();

            // Add image if file exists
            File imageFile = new File(imageUrl);
            if (imageFile.exists()) {
                XWPFParagraph imagePara = cell.addParagraph();
                imagePara.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun imageRun = imagePara.createRun();

                // Get image dimensions for proper scaling
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imageUrl, options);

                int originalWidth = options.outWidth;
                int originalHeight = options.outHeight;

                // Calculate scaled dimensions maintaining aspect ratio
                int scaledWidth = width;
                int scaledHeight = height;
                if (originalWidth > 0 && originalHeight > 0) {
                    double aspectRatio = (double) originalWidth / originalHeight;
                    if (aspectRatio > 1) {
                        // Landscape
                        scaledHeight = (int) (width / aspectRatio);
                    } else {
                        // Portrait
                        scaledWidth = (int) (height * aspectRatio);
                    }
                }

                // Add image
                FileInputStream imageStream = new FileInputStream(imageFile);
                imageRun.addPicture(imageStream, XWPFDocument.PICTURE_TYPE_JPEG, imageFile.getName(),
                        Units.toEMU(scaledWidth), Units.toEMU(scaledHeight));
                imageStream.close();

                // Add timestamp if available
                XWPFParagraph timestampPara = cell.addParagraph();
                timestampPara.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun timestampRun = timestampPara.createRun();
                timestampRun.setText("Feb 9, 2025 9:24:31 PM");
                timestampRun.setFontSize(8);
                timestampRun.setColor("666666");

            } else {
                // Image not found - add placeholder
                XWPFParagraph placeholderPara = cell.addParagraph();
                placeholderPara.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun placeholderRun = placeholderPara.createRun();
                placeholderRun.setText("[Image not found]");
                placeholderRun.setColor("FF0000");
                placeholderRun.setFontSize(12);

                Utility.showLog("Image file not found: " + imageUrl);
            }

        } catch (Exception e) {
            Utility.showLog( "Error adding image to cell: " + caption+ e);
            // Add error placeholder
            addPlaceholderToCell(cell, caption + " [Error loading image]", width, height);
        }
    }

    /**
     * Add placeholder to table cell
     */
    private static void addPlaceholderToCell(XWPFTableCell cell, String caption, int width, int height) {
        try {
            // Clear existing paragraphs
            if (!cell.getParagraphs().isEmpty()) {
                cell.removeParagraph(0);
            }

            // Add caption
            XWPFParagraph captionPara = cell.addParagraph();
            captionPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun captionRun = captionPara.createRun();
            captionRun.setText(caption);
            captionRun.setBold(true);
            captionRun.setFontSize(10);
            captionRun.addBreak();

            // Add placeholder box
            XWPFParagraph placeholderPara = cell.addParagraph();
            placeholderPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun placeholderRun = placeholderPara.createRun();
            placeholderRun.setText("[Image Placeholder]");
            placeholderRun.setColor("808080");
            placeholderRun.setFontSize(10);
            placeholderRun.addBreak();

            // Add dimensions info
            XWPFRun dimensionsRun = placeholderPara.createRun();
            dimensionsRun.setText("Size: " + width + "x" + height + "px");
            dimensionsRun.setColor("CCCCCC");
            dimensionsRun.setFontSize(8);

        } catch (Exception e) {
            Utility.showLog("Error adding placeholder to cell"+ e);
        }
    }

    /**
     * Set table borders
     */
    private static void setTableBorders(XWPFTable table) {
        table.getCTTbl().getTblPr().addNewTblBorders();
        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders borders =
                table.getCTTbl().getTblPr().getTblBorders();
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
     * Save document to ReportMate directory
     */
    private static boolean saveDocumentToReportMate(Context context, XWPFDocument document) {
        try {
            // Generate filename with timestamp
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "Panel_General_Pictures_" + timeStamp + ".docx";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ - Use MediaStore
                return saveWithMediaStore(context, document, fileName);
            } else {
                // Legacy Android - Direct file access
                return saveLegacy(context, document, fileName);
            }

        } catch (Exception e) {
            Utility.showLog( "Error saving document"+ e);
            Toast.makeText(context, "Error saving document: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * Save using MediaStore (Android 10+)
     */
    private static boolean saveWithMediaStore(Context context, XWPFDocument document, String fileName) {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/" + REPORTMATE_DIRECTORY);

            ContentResolver resolver = context.getContentResolver();
            Uri collection = MediaStore.Files.getContentUri("external");
            Uri uri = resolver.insert(collection, values);

            if (uri != null) {
                OutputStream outputStream = resolver.openOutputStream(uri);
                if (outputStream != null) {
                    document.write(outputStream);
                    outputStream.close();
                    document.close();

                    Toast.makeText(context, "Panel Pictures Report saved to Documents/ReportMate/" + fileName, Toast.LENGTH_LONG).show();
                    Utility.showLog("Document saved via MediaStore: " + uri.toString());
                    return true;
                } else {
                    throw new RuntimeException("Failed to open output stream");
                }
            } else {
                throw new RuntimeException("Failed to create MediaStore entry");
            }

        } catch (Exception e) {
            Utility.showLog("MediaStore save failed, trying legacy method"+ e);
            // Fallback to legacy method
            return saveLegacy(context, document, fileName);
        }
    }

    /**
     * Save using legacy method (Android 9 and below)
     */
    private static boolean saveLegacy(Context context, XWPFDocument document, String fileName) {
        try {
            // Create ReportMate directory in public Documents
            File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY);

            // Create directory if it doesn't exist
            if (!reportMateDir.exists()) {
                boolean created = reportMateDir.mkdirs();
                if (!created) {
                    throw new RuntimeException("Failed to create ReportMate directory");
                }
            }

            // Create file
            File docFile = new File(reportMateDir, fileName);

            // Write document
            FileOutputStream out = new FileOutputStream(docFile);
            document.write(out);
            out.close();
            document.close();

            // Trigger media scan to make file visible
            android.media.MediaScannerConnection.scanFile(context,
                    new String[]{docFile.getAbsolutePath()},
                    new String[]{"application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
                    null);

            Toast.makeText(context, "Panel Pictures Report saved to: " + docFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            Utility.showLog("Document saved (legacy): " + docFile.getAbsolutePath());
            return true;

        } catch (Exception e) {

            Utility.showLog( "Legacy save failed"+ e);

            // Final fallback - save to Downloads
            try {
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File reportMateDir = new File(downloadsDir, REPORTMATE_DIRECTORY);

                if (!reportMateDir.exists()) {
                    reportMateDir.mkdirs();
                }

                File docFile = new File(reportMateDir, fileName);
                FileOutputStream out = new FileOutputStream(docFile);
                document.write(out);
                out.close();
                document.close();

                Toast.makeText(context, "Panel Pictures Report saved to Downloads/ReportMate/" + fileName, Toast.LENGTH_LONG).show();
                Utility.showLog("Document saved to Downloads fallback: " + docFile.getAbsolutePath());
                return true;

            } catch (Exception fallbackException) {
                Utility.showLog( "All save methods failed"+ fallbackException);
                Toast.makeText(context, "Failed to save document: " + fallbackException.getMessage(), Toast.LENGTH_LONG).show();
                Utility.showLog("All save methods failed: " + fallbackException.getMessage());
                return false;
            }
        }
    }
}

// Usage in your Activity:
/*
public class MainActivity extends AppCompatActivity {

    private void generatePanelPicturesReport() {
        String imageUrl = "/storage/emulated/0/Pictures/Capture.jpg";

        boolean success = PanelPicturesGenerator.generatePanelGeneralPicturesReport(this, imageUrl);

        if (success) {
            Log.d("MainActivity", "Panel Pictures Report generated successfully");
        } else {
            Log.e("MainActivity", "Failed to generate Panel Pictures Report");
        }
    }

    // Add this to your existing button click listener or create a new button
    Button panelPicturesButton = findViewById(R.id.btnGeneratePanelPictures);
    panelPicturesButton.setOnClickListener(v -> {
        if (checkPermissions()) {
            generatePanelPicturesReport();
        } else {
            requestPermissions();
        }
    });
}
*/