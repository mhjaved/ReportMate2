package com.hasanjaved.reportmate.doc_generator;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.hasanjaved.reportmate.model.Report;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// this is being used to generate report ,
// all others are test
public class ReportGenerator {

    private static final String TAG = "ReportGenerator";
    private static final String REPORTS_DIRECTORY = "ReportMateReports";

    /**
     * Generate MCCB  Report
     * @param context Application context
     * @param reportName Name of the report file (without extension)
     * @return Success status
     */
    public static boolean generateReport(Context context, String reportName, Report report) {
        try {
            XWPFDocument document = new XWPFDocument();

            // Create document titles
            createDocumentTitles(document,report);

            // Create the main table with exact structure
            createExactReportTable(document,report);

            // Save document
            return saveDocument(context, document, reportName);

        } catch (Exception e) {
            Log.e(TAG, "Error generating  report", e);
            Toast.makeText(context, "Error generating report: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * Create document titles exactly as in original
     */
    private static void createDocumentTitles(XWPFDocument document, Report report) {
        // Main title
        XWPFParagraph titlePara = document.createParagraph();
        titlePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText(report.getProjectNo());
        titleRun.setBold(true);
        titleRun.setFontFamily("Arial");
        titleRun.setFontSize(16);

        // Subtitle
        XWPFParagraph subtitlePara = document.createParagraph();
        subtitlePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun subtitleRun = subtitlePara.createRun();
        subtitleRun.setText(report.getProjectNo()+": Test and Inspection Report");
        subtitleRun.setBold(true);
        subtitleRun.setFontFamily("Arial");
        subtitleRun.setFontSize(14);

        // Add spacing
        document.createParagraph();
    }

    /**
     * Create the exact table structure matching the original document
     */
    private static void createExactReportTable(XWPFDocument document, Report report) {
        // Create table with 16 columns to match original structure
        XWPFTable table = document.createTable();
        setTableBorders(table);
        setTableWidth(table, 100);

        // Row 1: PANEL BOARD CIRCUIT BREAKER TEST REPORT (colspan=16)
        XWPFTableRow row1 = table.getRow(0);
        expandRowTo16Columns(row1);
        mergeCellsHorizontally(table, 0, 0, 15);
        setCellText(row1.getCell(0), "PANEL BOARD CIRCUIT BREAKER TEST REPORT", true, ParagraphAlignment.CENTER);

        // Row 2: Empty (colspan=16)
        XWPFTableRow row2 = table.createRow();
        expandRowTo16Columns(row2);
        mergeCellsHorizontally(table, 1, 0, 15);
        setCellText(row2.getCell(0), "", false, ParagraphAlignment.CENTER);

        // Row 3: Empty (colspan=16)
        XWPFTableRow row3 = table.createRow();
        expandRowTo16Columns(row3);
        mergeCellsHorizontally(table, 2, 0, 15);
        setCellText(row3.getCell(0), "", false, ParagraphAlignment.CENTER);

        // Row 4: SHEET NO section (colspan=9, 4, 3)
        XWPFTableRow row4 = table.createRow();
        expandRowTo16Columns(row4);
        mergeCellsHorizontally(table, 3, 0, 8);   // colspan=9
        mergeCellsHorizontally(table, 3, 9, 12);  // colspan=4
        mergeCellsHorizontally(table, 3, 13, 15); // colspan=3
        setCellText(row4.getCell(0), "", false, ParagraphAlignment.LEFT);
        setCellText(row4.getCell(9), "SHEET NO:01", false, ParagraphAlignment.LEFT);
        setCellText(row4.getCell(13), "of :07", false, ParagraphAlignment.LEFT);

        // Row 5: CUSTOMER, DATE, PROJECT NO (colspan=9, 4, 3)
        XWPFTableRow row5 = table.createRow();
        expandRowTo16Columns(row5);
        mergeCellsHorizontally(table, 4, 0, 8);   // colspan=9
        mergeCellsHorizontally(table, 4, 9, 12);  // colspan=4
        mergeCellsHorizontally(table, 4, 13, 15); // colspan=3
        setCellText(row5.getCell(0), "CUSTOMER:"+report.getCustomerName(), false, ParagraphAlignment.LEFT);
        setCellText(row5.getCell(9), "DATE: "+report.getTestDate(), false, ParagraphAlignment.LEFT);
        setCellText(row5.getCell(13), "PROJECT NO: "+report.getProjectNo(), false, ParagraphAlignment.LEFT);

        // Row 6: ADDRESS, AIR TEMP, REL HUMIDITY (colspan=9, 4, 3)
        XWPFTableRow row6 = table.createRow();
        expandRowTo16Columns(row6);
        mergeCellsHorizontally(table, 5, 0, 8);   // colspan=9
        mergeCellsHorizontally(table, 5, 9, 12);  // colspan=4
        mergeCellsHorizontally(table, 5, 13, 15); // colspan=3
        setCellText(row6.getCell(0), "ADDRESS: "+report.getCustomerAddress(), false, ParagraphAlignment.LEFT);
        setCellText(row6.getCell(9), "AIR TEMP: "+report.getEquipment().getAirTemperature(), false, ParagraphAlignment.LEFT);
        setCellText(row6.getCell(13), "REL HUMIDITY: "+report.getEquipment().getAirHumidity(), false, ParagraphAlignment.LEFT);

        // Row 7: OWNER/USER, DATE LAST INSPECTION (colspan=9, 7)
        XWPFTableRow row7 = table.createRow();
        expandRowTo16Columns(row7);
        mergeCellsHorizontally(table, 6, 0, 8);   // colspan=9
        mergeCellsHorizontally(table, 6, 9, 15);  // colspan=7
        setCellText(row7.getCell(0), "OWNER/ USER:"+report.getOwnerIdentification(), false, ParagraphAlignment.LEFT);
        setCellText(row7.getCell(9), "DATE LAST INSPECTION: NOT AVAILABLE", false, ParagraphAlignment.LEFT);

        // Row 8: ADDRESS, LAST INSPECTION REPORT NO (colspan=9, 7)
        XWPFTableRow row8 = table.createRow();
        expandRowTo16Columns(row8);
        mergeCellsHorizontally(table, 7, 0, 8);   // colspan=9
        mergeCellsHorizontally(table, 7, 9, 15);  // colspan=7
        setCellText(row8.getCell(0), "ADDRESS:"+report.getCustomerAddress(), false, ParagraphAlignment.LEFT);
        setCellText(row8.getCell(9), "LAST INSPECTION REPORT NO: NOT AVAILABLE", false, ParagraphAlignment.LEFT);

        // Row 9: EQUIPMENT LOCATION (colspan=16)
        XWPFTableRow row9 = table.createRow();
        expandRowTo16Columns(row9);
        mergeCellsHorizontally(table, 8, 0, 15);
        setCellText(row9.getCell(0), "EQUIPMENT LOCATION: "+report.getEquipment().getEquipmentLocation(), false, ParagraphAlignment.LEFT);

        // Row 10: OWNER IDENTIFICATION (colspan=16)
        XWPFTableRow row10 = table.createRow();
        expandRowTo16Columns(row10);
        mergeCellsHorizontally(table, 9, 0, 15);
        setCellText(row10.getCell(0), "OWNER IDENTIFICATION: "+report.getOwnerIdentification(), false, ParagraphAlignment.LEFT);

        // Row 11: Empty (colspan=16)
        XWPFTableRow row11 = table.createRow();
        expandRowTo16Columns(row11);
        mergeCellsHorizontally(table, 10, 0, 15);
        setCellText(row11.getCell(0), "", false, ParagraphAlignment.CENTER);

        // Row 12: PANEL BUS INSULATION RESISTANCE IN MEGAOHMS (colspan=16)
        XWPFTableRow row12 = table.createRow();
        expandRowTo16Columns(row12);
        mergeCellsHorizontally(table, 11, 0, 15);
        setCellText(row12.getCell(0), "PANEL BUS INSULATION RESISTANCE IN MEGAOHMS", true, ParagraphAlignment.CENTER);

        // Row 13: Insulation resistance values (colspan=2,3,4,3,2,2)
        XWPFTableRow row13 = table.createRow();
        expandRowTo16Columns(row13);
        mergeCellsHorizontally(table, 12, 0, 1);   // colspan=2
        mergeCellsHorizontally(table, 12, 2, 4);   // colspan=3
        mergeCellsHorizontally(table, 12, 5, 8);   // colspan=4
        mergeCellsHorizontally(table, 12, 9, 11);  // colspan=3
        mergeCellsHorizontally(table, 12, 12, 13); // colspan=2
        mergeCellsHorizontally(table, 12, 14, 15); // colspan=2
        setCellText(row13.getCell(0), "A-G: >2100", false, ParagraphAlignment.CENTER);
        setCellText(row13.getCell(2), "B-G: >2100", false, ParagraphAlignment.CENTER);
        setCellText(row13.getCell(5), "C-G: >2100", false, ParagraphAlignment.CENTER);
        setCellText(row13.getCell(9), "A-B: >2100", false, ParagraphAlignment.CENTER);
        setCellText(row13.getCell(12), "B-C: >2100", false, ParagraphAlignment.CENTER);
        setCellText(row13.getCell(14), "C-A: >2100", false, ParagraphAlignment.CENTER);

        // Row 14: PANELBOARD Rating, AMPS, VOLTAGE (colspan=4,6,6)
        XWPFTableRow row14 = table.createRow();
        expandRowTo16Columns(row14);
        mergeCellsHorizontally(table, 13, 0, 3);   // colspan=4
        mergeCellsHorizontally(table, 13, 4, 9);   // colspan=6
        mergeCellsHorizontally(table, 13, 10, 15); // colspan=6
        setCellText(row14.getCell(0), "PANELBOARD Rating", false, ParagraphAlignment.LEFT);
        setCellText(row14.getCell(4), "AMPS: ", false, ParagraphAlignment.LEFT);
        setCellText(row14.getCell(10), "VOLTAGE: ", false, ParagraphAlignment.LEFT);

        // Row 15: TEST VOLTAGE, MODEL NO, CATALOG (colspan=4,6,6)
        XWPFTableRow row15 = table.createRow();
        expandRowTo16Columns(row15);
        mergeCellsHorizontally(table, 14, 0, 3);   // colspan=4
        mergeCellsHorizontally(table, 14, 4, 9);   // colspan=6
        mergeCellsHorizontally(table, 14, 10, 15); // colspan=6
        setCellText(row15.getCell(0), "TEST VOLTAGE: ", false, ParagraphAlignment.LEFT);
        setCellText(row15.getCell(4), "MODEL NO.: ", false, ParagraphAlignment.LEFT);
        setCellText(row15.getCell(10), "CATALOG: ", false, ParagraphAlignment.LEFT);

        // Row 16: MFG, CURVE NO Type-C, CURVE RANGE (colspan=4,6,6)
        XWPFTableRow row16 = table.createRow();
        expandRowTo16Columns(row16);
        mergeCellsHorizontally(table, 15, 0, 3);   // colspan=4
        mergeCellsHorizontally(table, 15, 4, 9);   // colspan=6
        mergeCellsHorizontally(table, 15, 10, 15); // colspan=6
        setCellText(row16.getCell(0), "MFG.:", false, ParagraphAlignment.LEFT);
        setCellText(row16.getCell(4), "CURVE NO.: ", false, ParagraphAlignment.LEFT);
        setCellText(row16.getCell(10), "CURVE RANGE: ", false, ParagraphAlignment.LEFT);

        // Row 17: MFG, CURVE NO Type-B, CURVE RANGE (colspan=4,6,6)
        XWPFTableRow row17 = table.createRow();
        expandRowTo16Columns(row17);
        mergeCellsHorizontally(table, 16, 0, 3);   // colspan=4
        mergeCellsHorizontally(table, 16, 4, 9);   // colspan=6
        mergeCellsHorizontally(table, 16, 10, 15); // colspan=6
        setCellText(row17.getCell(0), "MFG.:", false, ParagraphAlignment.LEFT);
        setCellText(row17.getCell(4), "CURVE NO.: ", false, ParagraphAlignment.LEFT);
        setCellText(row17.getCell(10), "CURVE RANGE: 2 to 16 Times of Full Load Current", false, ParagraphAlignment.LEFT);

        // Row 18: Empty MFG rows (colspan=4,6,6)
        XWPFTableRow row18 = table.createRow();
        expandRowTo16Columns(row18);
        mergeCellsHorizontally(table, 17, 0, 3);   // colspan=4
        mergeCellsHorizontally(table, 17, 4, 9);   // colspan=6
        mergeCellsHorizontally(table, 17, 10, 15); // colspan=6
        setCellText(row18.getCell(0), "MFG.", false, ParagraphAlignment.LEFT);
        setCellText(row18.getCell(4), "CURVE NO.", false, ParagraphAlignment.LEFT);
        setCellText(row18.getCell(10), "CURVE RANGE", false, ParagraphAlignment.LEFT);

        // Row 19: Empty MFG rows (colspan=4,6,6)
        XWPFTableRow row19 = table.createRow();
        expandRowTo16Columns(row19);
        mergeCellsHorizontally(table, 18, 0, 3);   // colspan=4
        mergeCellsHorizontally(table, 18, 4, 9);   // colspan=6
        mergeCellsHorizontally(table, 18, 10, 15); // colspan=6
        setCellText(row19.getCell(0), "MFG.", false, ParagraphAlignment.LEFT);
        setCellText(row19.getCell(4), "CURVE NO.", false, ParagraphAlignment.LEFT);
        setCellText(row19.getCell(10), "CURVE RANGE", false, ParagraphAlignment.LEFT);

        // Row 20: Circuit test header
        XWPFTableRow row20 = table.createRow();
        expandRowTo16Columns(row20);
        mergeCellsHorizontally(table, 19, 1, 2);   // CKT BKR SIZE colspan=2
        mergeCellsHorizontally(table, 19, 4, 5);   // TRIP TIME colspan=2
        mergeCellsHorizontally(table, 19, 8, 10);  // CIRCUIT # colspan=3
        setCellText(row20.getCell(0), "CIRCUIT #", false, ParagraphAlignment.CENTER);
        setCellText(row20.getCell(1), "CKT.\nBKR.\nSIZE", false, ParagraphAlignment.CENTER);
        setCellText(row20.getCell(3), "TEST AMPS", false, ParagraphAlignment.CENTER);
        setCellText(row20.getCell(4), "TRIP TIME", false, ParagraphAlignment.CENTER);
        setCellText(row20.getCell(6), "INST TRIP", false, ParagraphAlignment.CENTER);
        setCellText(row20.getCell(7), "CONTACT RESIS", false, ParagraphAlignment.CENTER);
        setCellText(row20.getCell(8), "CIRCUIT #", false, ParagraphAlignment.CENTER);
        setCellText(row20.getCell(11), "CKT.\nBKR.\nSIZE", false, ParagraphAlignment.CENTER);
        setCellText(row20.getCell(12), "TEST AMPS", false, ParagraphAlignment.CENTER);
        setCellText(row20.getCell(13), "TRIP TIME", false, ParagraphAlignment.CENTER);
        setCellText(row20.getCell(14), "INST TRIP", false, ParagraphAlignment.CENTER);
        setCellText(row20.getCell(15), "CONTACT RESIS", false, ParagraphAlignment.CENTER);


        addCircuitBreakerData(table, report);

        // Add circuit breaker data with proper rowspan
//        addCircuitBreakerDataWithRowspan(table, "ckt1", "", "", "", "OK",
//                new String[]{"", "", ""});
//
//        addCircuitBreakerDataWithRowspan(table, "ckt2", "", "", "", "OK",
//                new String[]{"", "", ""});

//        addCircuitBreakerDataWithRowspan(table, "ckt3", "", "", "", "OK",
//                new String[]{"4.78 mΩ", "5.30 mΩ", "4.48 mΩ"});
//
//        addCircuitBreakerDataWithRowspan(table, "", "", "", "", "OK",
//                new String[]{"4.92 mΩ", "5.36 mΩ", "4.48 mΩ"});

        // Add empty circuit rows
//        addEmptyCircuitRowsWithRowspan(table);
//        addEmptyCircuitRowsWithRowspan(table);

        // Add remarks section
        addRemarksToTable(table);
    }

    /**
     * Add circuit breaker data with proper rowspan (3 rows per circuit)
     */
    private static void addCircuitBreakerDataWithRowspan(XWPFTable table, String circuit, String size,
                                                         String testAmps, String tripTime, String instTrip,
                                                         String[] contactRes) {
        int currentRowIndex = table.getNumberOfRows();

        // Row 1 of circuit data
        XWPFTableRow row1 = table.createRow();
        expandRowTo16Columns(row1);
        setCellText(row1.getCell(0), circuit, true, ParagraphAlignment.CENTER);
        mergeCellsHorizontally(table, currentRowIndex, 1, 2);
        setCellText(row1.getCell(1), size, false, ParagraphAlignment.CENTER);
        setCellText(row1.getCell(3), testAmps, false, ParagraphAlignment.CENTER);
        mergeCellsHorizontally(table, currentRowIndex, 4, 5);
        setCellText(row1.getCell(4), tripTime, false, ParagraphAlignment.CENTER);
        setCellText(row1.getCell(6), instTrip, false, ParagraphAlignment.CENTER);
        setCellText(row1.getCell(7), contactRes[0], false, ParagraphAlignment.CENTER);
        // Right side empty
        mergeCellsHorizontally(table, currentRowIndex, 8, 10);
        setCellText(row1.getCell(8), "", false, ParagraphAlignment.CENTER);
        for (int i = 11; i < 16; i++) {
            setCellText(row1.getCell(i), "", false, ParagraphAlignment.CENTER);
        }

        // Row 2 of circuit data
        XWPFTableRow row2 = table.createRow();
        expandRowTo16Columns(row2);
        setCellText(row2.getCell(7), contactRes[1], false, ParagraphAlignment.CENTER);
        setCellText(row2.getCell(15), "", false, ParagraphAlignment.CENTER);

        // Row 3 of circuit data
        XWPFTableRow row3 = table.createRow();
        expandRowTo16Columns(row3);
        setCellText(row3.getCell(7), contactRes[2], false, ParagraphAlignment.CENTER);
        setCellText(row3.getCell(15), "", false, ParagraphAlignment.CENTER);
    }

    /**
     * Add empty circuit rows with proper rowspan
     */
    private static void addEmptyCircuitRowsWithRowspan(XWPFTable table) {
        for (int i = 0; i < 3; i++) {
            XWPFTableRow row = table.createRow();
            expandRowTo16Columns(row);
            for (int j = 0; j < 16; j++) {
                setCellText(row.getCell(j), "", false, ParagraphAlignment.CENTER);
            }
        }
    }

    /**
     * Add remarks section to the table
     */
    private static void addRemarksToTable(XWPFTable table) {
        // Remarks header
        XWPFTableRow remarksHeaderRow = table.createRow();
        expandRowTo16Columns(remarksHeaderRow);
        mergeCellsHorizontally(table, table.getNumberOfRows() - 1, 0, 15);
        setCellText(remarksHeaderRow.getCell(0), "Remarks:", false, ParagraphAlignment.LEFT);

        // Remarks content
        String[] remarks = {
                "1. IR test values are Found satisfactory According to ANSI/NETA ATS (7.6.1.1.3.2.2)",
                "2. CRM test values are Found satisfactory to ANSI/NETA ATS (7.6.1.1.3.2.3)",
                "3. Trip test values are Found satisfactory According to NFPA 70B Table (11.10.5.2.5)",
                "Equipment Used: IR Tester (KYORITSU KEW-3125A), Contact Resistance Measurement (HISAC Swift), Current Injector.",
                "Submitted By: Novelty Energy Limited",
                "NFPA 70B"
        };

        for (String remark : remarks) {
            XWPFTableRow remarkRow = table.createRow();
            expandRowTo16Columns(remarkRow);
            mergeCellsHorizontally(table, table.getNumberOfRows() - 1, 0, 15);
            boolean isBold = remark.contains("Novelty Energy Limited");
            setCellText(remarkRow.getCell(0), remark, isBold, ParagraphAlignment.LEFT);
        }
    }

    /**
     * Expand table row to 16 columns
     */
    private static void expandRowTo16Columns(XWPFTableRow row) {
        while (row.getTableCells().size() < 16) {
            row.addNewTableCell();
        }
    }

    /**
     * Merge cells horizontally
     */
    private static void mergeCellsHorizontally(XWPFTable table, int row, int fromCol, int toCol) {
        XWPFTableRow tableRow = table.getRow(row);
        for (int colIndex = fromCol; colIndex <= toCol; colIndex++) {
            XWPFTableCell cell = tableRow.getCell(colIndex);
            CTTcPr tcPr = cell.getCTTc().addNewTcPr();
            if (colIndex == fromCol) {
                tcPr.addNewHMerge().setVal(STMerge.RESTART);
            } else {
                tcPr.addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    /**
     * Set cell text with formatting
     */
    private static void setCellText(XWPFTableCell cell, String text, boolean bold, ParagraphAlignment alignment) {
        cell.removeParagraph(0);
        XWPFParagraph para = cell.addParagraph();
        para.setAlignment(alignment);

        XWPFRun run = para.createRun();
        run.setText(text);
        run.setBold(bold);
        run.setFontFamily("Arial");
        run.setFontSize(9);

        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
    }

    /**
     * Set table borders
     */
    private static void setTableBorders(XWPFTable table) {
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        if (tblPr == null) {
            tblPr = table.getCTTbl().addNewTblPr();
        }

        CTTblBorders borders = tblPr.addNewTblBorders();
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
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        if (tblPr == null) {
            tblPr = table.getCTTbl().addNewTblPr();
        }

        CTTblWidth tblWidth = tblPr.addNewTblW();
        tblWidth.setType(STTblWidth.PCT);
        tblWidth.setW(BigInteger.valueOf(widthPercent * 50));
    }

    /**
     * Save document to ReportMateReports folder in public Documents
     */
    private static boolean saveDocument(Context context, XWPFDocument document, String reportName) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//            String fileName = reportName + "_" + timeStamp + ".docx";
            String fileName = reportName +".docx";

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
     * Save using MediaStore (Android 10+)
     */
    private static boolean saveWithMediaStore(Context context, XWPFDocument document, String fileName) {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.Files.FileColumns.MIME_TYPE,
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            values.put(MediaStore.Files.FileColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOCUMENTS + "/" + REPORTS_DIRECTORY);

            ContentResolver resolver = context.getContentResolver();
            Uri uri = resolver.insert(MediaStore.Files.getContentUri("external"), values);

            if (uri != null) {
                OutputStream outputStream = resolver.openOutputStream(uri);
                document.write(outputStream);
                outputStream.close();
                document.close();

                Toast.makeText(context, "Report saved to Documents/" + REPORTS_DIRECTORY + "/" + fileName,
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
     * Save using legacy method (Android 9 and below)
     */
    private static boolean saveLegacy(Context context, XWPFDocument document, String fileName) {
        try {
            File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File reportsDir = new File(documentsDir, REPORTS_DIRECTORY);

            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }

            File docFile = new File(reportsDir, fileName);
            FileOutputStream out = new FileOutputStream(docFile);
            document.write(out);
            out.close();
            document.close();

            android.media.MediaScannerConnection.scanFile(context,
                    new String[]{docFile.getAbsolutePath()},
                    new String[]{"application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
                    null);

            Toast.makeText(context, " Report saved: " + docFile.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "Document saved: " + docFile.getAbsolutePath());
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Legacy save failed", e);
            Toast.makeText(context, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //====================================================================
    /**
     * Add circuit breaker data with proper structure matching the image
     */
    private static void addCircuitBreakerData(XWPFTable table, Report report) {
        // Circuit data - each circuit has multiple resistance measurements
        addCircuitRow(table, "CKT-0", "200 A", "600 A", "22s", "OK",
                new String[]{"3.40 mΩ", "3.08 mΩ", "2.95 mΩ"});

        addCircuitRow(table, "CKT-1", "80 A", "243 A", "48s", "OK",
                new String[]{"4.15 mΩ", "3.86 mΩ", "3.95 mΩ", "3.79 mΩ"});

        addCircuitRow(table, "CKT-2", "80 A", "242 A", "54 s", "OK",
                new String[]{"3.53 mΩ", "4.25 mΩ"});

        addCircuitRow(table, "CKT-3", "80 A", "244 A", "33s", "OK",
                new String[]{"3.23 mΩ", "3.23 mΩ", "2.85 mΩ"});

        addCircuitRow(table, "CKT-4", "63 A", "189s", "28 s", "OK",
                new String[]{"2.29 mΩ", "3.43 mΩ", "3.96 mΩ", "2.89 mΩ"});

        addCircuitRow(table, "CKT-5", "80 A", "237 s", "51 s", "OK",
                new String[]{"3.83 mΩ", "3.04 mΩ"});

        addCircuitRow(table, "CKT-6", "80 A", "243 s", "46 s", "OK",
                new String[]{"3.17 mΩ", "3.32 mΩ", "2.65 mΩ"});
    }

    /**
     * Add a single circuit row with multiple resistance measurements
     */
    private static void addCircuitRow(XWPFTable table, String circuit, String size,
                                      String testAmps, String tripTime, String instTrip,
                                      String[] contactResistanceValues) {

        int maxRows = Math.max(contactResistanceValues.length, 1);
        int startRowIndex = table.getNumberOfRows();

        // Create the required number of rows for this circuit
        for (int i = 0; i < maxRows; i++) {
            XWPFTableRow row = table.createRow();
            expandRowTo16Columns(row);

            if (i == 0) {
                // First row contains all the circuit info
                setCellText(row.getCell(0), circuit, false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(1), size, false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(2), testAmps, false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(3), tripTime, false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(4), instTrip, false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(5), contactResistanceValues[i], false, ParagraphAlignment.CENTER);
            } else {
                // Subsequent rows only have resistance values
                setCellText(row.getCell(0), "", false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(1), "", false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(2), "", false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(3), "", false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(4), "", false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(5), contactResistanceValues[i], false, ParagraphAlignment.CENTER);
            }

            // Right side columns (6-15) - empty for now
            for (int j = 6; j < 16; j++) {
                setCellText(row.getCell(j), "", false, ParagraphAlignment.CENTER);
            }
        }

        // Apply vertical merge to the first 5 columns for this circuit
        if (maxRows > 1) {
            mergeVerticalCells(table, startRowIndex, startRowIndex + maxRows - 1, 0); // Circuit #
            mergeVerticalCells(table, startRowIndex, startRowIndex + maxRows - 1, 1); // Size
            mergeVerticalCells(table, startRowIndex, startRowIndex + maxRows - 1, 2); // Test Amps
            mergeVerticalCells(table, startRowIndex, startRowIndex + maxRows - 1, 3); // Trip Time
            mergeVerticalCells(table, startRowIndex, startRowIndex + maxRows - 1, 4); // Inst Trip
        }
    }

    /**
     * Merge cells vertically
     */
    private static void mergeVerticalCells(XWPFTable table, int fromRow, int toRow, int col) {
        for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
            CTTcPr tcPr = cell.getCTTc().getTcPr();
            if (tcPr == null) {
                tcPr = cell.getCTTc().addNewTcPr();
            }

            if (rowIndex == fromRow) {
                tcPr.addNewVMerge().setVal(STMerge.RESTART);
            } else {
                tcPr.addNewVMerge().setVal(STMerge.CONTINUE);
            }
        }
    }
}

// Usage in your Activity:
/*
public class MainActivity extends AppCompatActivity {

    private void generateYKKReport() {
        String reportName = "YKK_MCCB_MSB_DV22_BEPZA_Report";

        boolean success = YKKReportGenerator.generateYKKMCCBReport(this, reportName);

        if (success) {
            Log.d("MainActivity", "YKK MCCB report generated successfully");
            // File saved to: /storage/emulated/0/Documents/ReportMateReports/
        } else {
            Log.e("MainActivity", "Failed to generate YKK MCCB report");
        }
    }

    // Add button click listener
    Button generateYKKButton = findViewById(R.id.btnGenerateYKK);
    generateYKKButton.setOnClickListener(v -> generateYKKReport());
}
*/