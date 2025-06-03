package com.hasanjaved.reportmate.utility;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportGenerator {

    private static final String TAG = "ReportGenerator";

    /**
     * Generate electrical inspection report DOCX file
     * @param context Application context
     * @param outputDirectory Directory where file will be saved
     * @param fileName Output file name (without extension)
     * @param imageUrl Path to the inspection image
     * @return Generated file path or null if failed
     */
    public static String generateElectricalInspectionReport(Context context, String outputDirectory, String fileName, String imageUrl) {

//        Utility.showLog();
        try {
            XWPFDocument document = new XWPFDocument();

            // Add report header
            addReportHeader(document);

            // Add client information
            addClientInformation(document);

            // Add test results table
            addTestResultsTable(document);

            // Add panel general pictures section
            addPanelGeneralPictures(document);

            // Add MCCB & MCB test results
            addMCCBTestResults(document);

            // Add inspection image if provided
            if (imageUrl != null && !imageUrl.isEmpty()) {
                addInspectionImage(document, imageUrl);
            }

            // Add DB-3.2 section
            addDB32Section(document);

            // Add footer with remarks
            addReportFooter(document);

            // Save document to provided directory
            String filePath = saveDocumentToDirectory(context, document, outputDirectory, fileName);

            return filePath;

        } catch (Exception e) {
            Log.e(TAG, "Error generating electrical inspection report", e);
            return null;
        }
    }

    private static void addReportHeader(XWPFDocument document) {
        // Title
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText("ELECTRICAL INSPECTION REPORT");
        titleRun.setBold(true);
        titleRun.setFontSize(18);
        titleRun.setFontFamily("Arial");
        titleRun.addBreak();

        // Report details
        XWPFParagraph headerParagraph = document.createParagraph();
        XWPFRun headerRun = headerParagraph.createRun();
        headerRun.setText("Client: New Era Fashions Mfrs.(BD)Ltd.");
        headerRun.addBreak();
        headerRun.setText("Date: " + new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date()));
        headerRun.addBreak();
        headerRun.setText("Report No.: NEL-SWGRTS-11122024-B-1");
        headerRun.addBreak();
        headerRun.setFontSize(11);
        headerRun.setFontFamily("Arial");
    }

    private static void addClientInformation(XWPFDocument document) {
        XWPFParagraph clientParagraph = document.createParagraph();
        XWPFRun clientRun = clientParagraph.createRun();
        clientRun.setText("6    DB-3.1");
        clientRun.setBold(true);
        clientRun.setColor("00FF00"); // Green color
        clientRun.addBreak();

        clientRun.setText("6.1    DB-3.1 MCCB & MCB: Test and Inspection Report");
        clientRun.setBold(false);
        clientRun.setColor("808080"); // Gray color
        clientRun.addBreak();
        clientRun.addBreak();
    }

    private static void addTestResultsTable(XWPFDocument document) {
        // Create table for panel board circuit breaker test report
        XWPFTable table = document.createTable();
        table.setWidth("100%");

        // Header row
        XWPFTableRow headerRow = table.getRow(0);
        headerRow.getCell(0).setText("PANEL BOARD CIRCUIT BREAKER TEST REPORT");
        headerRow.getCell(0).setColor("E6E6FA");

        // Customer information
        XWPFTableRow customerRow = table.createRow();
        customerRow.getCell(0).setText("CUSTOMER: New Era Fashions Mfrs.(BD)Ltd");
        customerRow.addNewTableCell().setText("DATE: 9.02.2024");
        customerRow.addNewTableCell().setText("PROJECT NO: NEL-SWGRTS-11122024-B-1");

        // Address information
//        XWPFTableRow addressRow = table.createRow();
//        addressRow.getCell(0).setText("ADDRESS: Plot No 10 – 12(Part), Sector -A, CEPZ, Chattagram.");
//        addressRow.getCell(1).setText("AIR TEMP: 2C");
//        addressRow.getCell(2).setText("REL HUMIDITY: 49%");

        // Equipment information
//        XWPFTableRow equipmentRow = table.createRow();
//        equipmentRow.getCell(0).setText("EQUIPMENT LOCATION: 2nd Floor");
//        equipmentRow.getCell(1).setText("DATE LAST INSPECTION: 17-2-2025");
//        equipmentRow.getCell(2).setText("LAST INSPECTION REPORT NO: NEL-SWGR280275");

        // Test results header
        addTestResultsData(table);
    }

    private static void addTestResultsData(XWPFTable table) {
        // Circuit data header
        XWPFTableRow circuitHeaderRow = table.createRow();
        String[] headers = {"CIRCUIT", "CKT BRK SIZE", "TEST AMPS", "TIME", "TRIP", "INST TRIP", "CONTACT RESIS"};
        for (int i = 0; i < headers.length; i++) {
            if (i == 0) {
                circuitHeaderRow.getCell(0).setText(headers[i]);
            } else {
                circuitHeaderRow.addNewTableCell().setText(headers[i]);
            }
        }

        // Test data
        String[][] testData = {
                {"CKT-0", "200 A", "600 A", "22s", "OK", "3.08 mΩ", "2.96 mΩ"},
                {"CKT-1", "80 A", "243 A", "48s", "OK", "4.13 mΩ", "3.86 mΩ"},
                {"CKT-2", "80 A", "242 A", "54 s", "OK", "4.78 mΩ", "4.31 mΩ"},
                {"CKT-3", "80 A", "244 A", "35s", "OK", "3.23 mΩ", "2.85 mΩ"},
                {"CKT-4", "63 A", "189s", "24 s", "OK", "4.50 mΩ", "2.98 mΩ"},
                {"CKT-5", "80 A", "237 s", "51 s", "OK", "3.83 mΩ", "3.04 mΩ"},
                {"CKT-6", "80 A", "243 s", "46 s", "OK", "4.32 mΩ", "3.69 mΩ"}
        };

        for (String[] rowData : testData) {
            XWPFTableRow dataRow = table.createRow();
            for (int i = 0; i < rowData.length; i++) {
                if (i == 0) {
                    dataRow.getCell(0).setText(rowData[i]);
                } else {
                    dataRow.addNewTableCell().setText(rowData[i]);
                }
            }
        }
    }

    private static void addPanelGeneralPictures(XWPFDocument document) {
        // Section header
        XWPFParagraph sectionParagraph = document.createParagraph();
        XWPFRun sectionRun = sectionParagraph.createRun();
        sectionRun.setText("6.2    DB-3.1 Panel General Pictures");
        sectionRun.setBold(false);
        sectionRun.setColor("808080"); // Gray color
        sectionRun.addBreak();
        sectionRun.addBreak();

        // Picture placeholders table
        XWPFTable pictureTable = document.createTable(2, 2);
        pictureTable.setWidth("100%");

        // Temperature & Humidity
        pictureTable.getRow(0).getCell(0).setText("Temperature & Humidity");
        pictureTable.getRow(0).getCell(1).setText("Panel");

        // Panel Inside
        pictureTable.getRow(1).getCell(0).setText("Panel Inside");
        pictureTable.getRow(1).getCell(1).setText("[Image placeholder]");
    }

    private static void addMCCBTestResults(XWPFDocument document) {
        // Section header
        XWPFParagraph mcbParagraph = document.createParagraph();
        XWPFRun mcbRun = mcbParagraph.createRun();
        mcbRun.setText("6.3    DB-3.1 MCCB & MCB: IR Test and Results");
        mcbRun.setBold(false);
        mcbRun.setColor("808080"); // Gray color
        mcbRun.addBreak();
        mcbRun.addBreak();

        // IR Test results description
        XWPFParagraph irParagraph = document.createParagraph();
        XWPFRun irRun = irParagraph.createRun();
        irRun.setText("IR Test Connection (A-G), (B-G), (C-G)");
        irRun.addBreak();
        irRun.setText("IR Test Results showing 4000 MΩ for all connections");
        irRun.addBreak();
        irRun.setText("All test results are within acceptable parameters");
        irRun.addBreak();
    }

    private static void addInspectionImage(XWPFDocument document, String imageUrl) {
        try {
            File imageFile = new File(imageUrl);
            if (!imageFile.exists()) {
                Log.w(TAG, "Image file not found: " + imageUrl);
                return;
            }

            // Add image section header
            XWPFParagraph imageParagraph = document.createParagraph();
            XWPFRun imageHeaderRun = imageParagraph.createRun();
            imageHeaderRun.setText("Inspection Images");
            imageHeaderRun.setBold(true);
            imageHeaderRun.setFontSize(14);
            imageHeaderRun.addBreak();

            // Add the image
            XWPFParagraph imageContentParagraph = document.createParagraph();
            imageContentParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun imageRun = imageContentParagraph.createRun();

            // Get image dimensions for scaling
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageUrl, options);

            int originalWidth = options.outWidth;
            int originalHeight = options.outHeight;

            // Calculate scaled dimensions (max width: 500px)
            int maxWidth = 500;
            int scaledWidth = Math.min(originalWidth, maxWidth);
            int scaledHeight = (originalHeight * scaledWidth) / originalWidth;

            FileInputStream imageStream = new FileInputStream(imageFile);
            imageRun.addPicture(imageStream, XWPFDocument.PICTURE_TYPE_JPEG, imageFile.getName(),
                    Units.toEMU(scaledWidth), Units.toEMU(scaledHeight));
            imageStream.close();

            imageRun.addBreak();

        } catch (Exception e) {
            Log.e(TAG, "Error adding inspection image: " + imageUrl, e);
        }
    }

    private static void addDB32Section(XWPFDocument document) {
        // DB-3.2 Section
        XWPFParagraph db32Paragraph = document.createParagraph();
        XWPFRun db32Run = db32Paragraph.createRun();
        db32Run.setText("7    DB-3.2");
        db32Run.setBold(true);
        db32Run.setColor("00FF00"); // Green color
        db32Run.addBreak();

        db32Run.setText("7.1    DB-3.2 MCCB & MCB: Test and Inspection Report");
        db32Run.setBold(false);
        db32Run.setColor("808080"); // Gray color
        db32Run.addBreak();
        db32Run.addBreak();

        // DB-3.2 Test results table
        XWPFTable db32Table = document.createTable();
        db32Table.setWidth("100%");

        // Header
        XWPFTableRow db32HeaderRow = db32Table.getRow(0);
        db32HeaderRow.getCell(0).setText("PANEL BUS INSULATION RESISTANCE IN MEGAOHMS");
        db32HeaderRow.getCell(0).setColor("E6E6FA");

        // Test data for DB-3.2
        XWPFTableRow db32DataRow = db32Table.createRow();
        db32DataRow.getCell(0).setText("CKT-0: 125 A, 375 A, 45s, OK - 6.34 mΩ");
        db32DataRow.addNewTableCell().setText("CKT-18: 10A, 27 A, 10 s, OK - 58.3 mΩ");
    }

    private static void addReportFooter(XWPFDocument document) {
        // Remarks section
        XWPFParagraph remarksParagraph = document.createParagraph();
        XWPFRun remarksRun = remarksParagraph.createRun();
        remarksRun.setText("Remarks:");
        remarksRun.setBold(true);
        remarksRun.addBreak();

        remarksRun.setText("1. IR test values are Found satisfactory According to ANSI/NFTA ( 7.1.1.1.2.3)");
        remarksRun.setBold(false);
        remarksRun.addBreak();

        remarksRun.setText("2. OLM test values are Found satisfactory According to ANSI/NFTA ( 7.1.1.1.2.3)");
        remarksRun.addBreak();

        remarksRun.setText("3. Trip test values are Found satisfactory According to NFPA 70B Table (11.10.5.2.5)");
        remarksRun.addBreak();

        remarksRun.setText("4. Equipment Used: IR Tester (HIOKI IR 4056 ), HISAC Swift, Current Injector.");
        remarksRun.addBreak();

        remarksRun.setText("Submitted By: Novelty Energy Limited");
        remarksRun.addBreak();
        remarksRun.addBreak();

        // NFPA reference
        XWPFParagraph nfpaParagraph = document.createParagraph();
        nfpaParagraph.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun nfpaRun = nfpaParagraph.createRun();
        nfpaRun.setText("NFPA 70B");
        nfpaRun.setFontSize(10);
    }

    private static String saveDocumentToDirectory(Context context, XWPFDocument document, String outputDirectory, String fileName) {
        try {
            // Create the directory if it doesn't exist
            File directory = new File(outputDirectory);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) {
                    Log.e(TAG, "Failed to create directory: " + outputDirectory);
                    return null;
                }
            }

            // Ensure the directory is writable
            if (!directory.canWrite()) {
                Log.e(TAG, "Directory is not writable: " + outputDirectory);
                return null;
            }

            // Create output file
            File outputFile = new File(directory, fileName + ".docx");

            // Write document to file
            FileOutputStream out = new FileOutputStream(outputFile);
            document.write(out);
            out.close();
            document.close();

            Log.d(TAG, "Electrical inspection report generated successfully: " + outputFile.getAbsolutePath());
            Log.d(TAG, "File exists: " + outputFile.exists());
            Log.d(TAG, "File size: " + outputFile.length() + " bytes");

            return outputFile.getAbsolutePath();

        } catch (Exception e) {
            Log.e(TAG, "Error saving document to directory: " + outputDirectory, e);
            return null;
        }
    }

    private static String saveDocument(Context context, XWPFDocument document, String fileName) {
        try {
            // Choose save location based on Android version
            File outputFile;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ - Use app-specific directory
                File reportsDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "ReportMatej");
                if (!reportsDir.exists()) {
                    reportsDir.mkdirs();
                }
                outputFile = new File(reportsDir, fileName + ".docx");
            } else {
                // Older Android versions - Use public Documents directory
                File documentsDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS), "ReportMate");
                if (!documentsDir.exists()) {
                    documentsDir.mkdirs();
                }
                outputFile = new File(documentsDir, fileName + ".docx");
            }

            FileOutputStream out = new FileOutputStream(outputFile);
            document.write(out);
            out.close();
            document.close();

            Log.d(TAG, "Electrical inspection report generated successfully: " + outputFile.getAbsolutePath());
            Log.d(TAG, "File exists: " + outputFile.exists());
            Log.d(TAG, "File size: " + outputFile.length() + " bytes");

            return outputFile.getAbsolutePath();

        } catch (Exception e) {
            Log.e(TAG, "Error saving document", e);
            return null;
        }
    }

    /**
     * Generate report with custom data and directory
     * @param context Application context
     * @param outputDirectory Directory where file will be saved
     * @param fileName Output file name
     * @param clientName Client company name
     * @param reportDate Report date
     * @param reportNumber Report number
     * @param imageUrl Path to inspection image
     * @return Generated file path or null if failed
     */
    public static String generateCustomElectricalReport(Context context, String outputDirectory, String fileName,
                                                        String clientName, String reportDate,
                                                        String reportNumber, String imageUrl) {
        try {
            XWPFDocument document = new XWPFDocument();

            // Custom header with provided data
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("ELECTRICAL INSPECTION REPORT");
            titleRun.setBold(true);
            titleRun.setFontSize(18);
            titleRun.setFontFamily("Arial");
            titleRun.addBreak();

            XWPFParagraph headerParagraph = document.createParagraph();
            XWPFRun headerRun = headerParagraph.createRun();
            headerRun.setText("Client: " + (clientName != null ? clientName : "New Era Fashions Mfrs.(BD)Ltd."));
            headerRun.addBreak();
            headerRun.setText("Date: " + (reportDate != null ? reportDate : new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date())));
            headerRun.addBreak();
            headerRun.setText("Report No.: " + (reportNumber != null ? reportNumber : "NEL-SWGRTS-11122024-B-1"));
            headerRun.addBreak();
            headerRun.setFontSize(11);
            headerRun.setFontFamily("Arial");

            // Add remaining sections
            addClientInformation(document);
            addTestResultsTable(document);
            addPanelGeneralPictures(document);
            addMCCBTestResults(document);

            if (imageUrl != null && !imageUrl.isEmpty()) {
                addInspectionImage(document, imageUrl);
            }

            addDB32Section(document);
            addReportFooter(document);

            return saveDocumentToDirectory(context, document, outputDirectory, fileName);

        } catch (Exception e) {
            Log.e(TAG, "Error generating custom electrical inspection report", e);
            return null;
        }
    }
}

// Usage Examples:
/*
// 1. Save to custom directory
String customDir = "/storage/emulated/0/Documents/MyReports";
String reportPath = ReportGenerator.generateElectricalInspectionReport(
    this,
    customDir,
    "ElectricalInspection_" + System.currentTimeMillis(),
    "/storage/emulated/0/Pictures/1748710167004.jpg"
);

// 2. Save to ReportMate directory (backward compatibility)
String reportMatePath = ReportGenerator.generateElectricalInspectionReportToReportMate(
    this,
    "ElectricalInspection_2024",
    "/storage/emulated/0/Pictures/1748710167004.jpg"
);

// 3. Custom report with specific directory
String specificDir = "/storage/emulated/0/Download/Reports";
String customReportPath = ReportGenerator.generateCustomElectricalReport(
    this,
    specificDir,
    "CustomReport_2024",
    "New Era Fashions Mfrs.(BD)Ltd.",
    "23.02.2025",
    "NEL-SWGRTS-11122024-B-1",
    "/storage/emulated/0/Pictures/1748710167004.jpg"
);

// 4. Validate directory before generating report
String targetDir = "/storage/emulated/0/Documents/ElectricalReports";
if (ReportGenerator.isDirectoryValid(targetDir)) {
    String reportPath = ReportGenerator.generateElectricalInspectionReport(
        this, targetDir, "Report_" + System.currentTimeMillis(),
        "/storage/emulated/0/Pictures/1748710167004.jpg"
    );

    if (reportPath != null) {
        Toast.makeText(this, "Report generated: " + reportPath, Toast.LENGTH_LONG).show();
    }
} else {
    Toast.makeText(this, "Invalid directory: " + targetDir, Toast.LENGTH_SHORT).show();
}

// 5. Get ReportMate directory path
String reportMateDir = ReportGenerator.getReportMateDirectory(this);
Log.d("ReportGenerator", "ReportMate directory: " + reportMateDir);
*/