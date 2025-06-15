//package com.hasanjaved.reportmate.doc_generator;
//
//import android.content.ContentResolver;
//import android.content.ContentUris;
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Looper;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.hasanjaved.reportmate.model.Report;
//import com.hasanjaved.reportmate.utility.Utility;
//
//import org.apache.poi.util.Units;
//import org.apache.poi.xwpf.usermodel.*;
//import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.math.BigInteger;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class AsyncReportGenerator {
//
//    private static final String TAG = "AsyncReportGenerator";
//    private static final String REPORTS_DIRECTORY = "ReportMateReports";
//    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
//
//    /**
//     * Callback interface for async operations
//     */
////    public interface ReportGenerationCallback {
////        void onStarted();
////        void onProgress(int progress, String message);
////        void onSuccess(String filePath);
////        void onError(String errorMessage);
////    }
//
//    /**
//     * Generate MCCB Report asynchronously
//     */
//    public static void generateReportAsync(Context context, String reportName, Report report,
//                                           ReportGenerationCallback callback) {
//
//        // Notify start on main thread
//        new Handler(Looper.getMainLooper()).post(() -> {
//            if (callback != null) {
//                callback.onStarted();
//            }
//        });
//
//        // Execute on background thread
//        executorService.execute(() -> {
//            try {
//                updateProgress(callback, 5, "Initializing document...");
//
//                XWPFDocument document = new XWPFDocument();
//
//                // Create document titles
//                updateProgress(callback, 10, "Creating document titles...");
//                createDocumentTitles(document, report);
//
//                // Create the main table with exact structure
//                updateProgress(callback, 20, "Creating main table...");
//                createExactReportTable(document, report, callback);
//
//                // Prepare image data
//                updateProgress(callback, 60, "Preparing images...");
//                String[] dbBoxTitles = {
//                        "Temperature & Humidity",
//                        "Panel",
//                        "Panel inside",
//                        "Additional View"
//                };
//
//                String[] dbBoxImages = {
//                        "ReportMate/" + reportName + "/" + Utility.generalImageTemperature + ".jpg",
//                        "ReportMate/" + reportName + "/" + Utility.dbBoxPanelFront + ".jpg",
//                        "ReportMate/" + reportName + "/" + Utility.dbBoxPanelInside + ".jpg",
//                        "ReportMate/" + reportName + "/" + Utility.dbBoxPanelNameplate + ".jpg"
//                };
//
//                String[] irTestLabels = {
//                        "IR Test Connection (A-G)", "IR Test Result (A-G)",
//                        "IR Test Connection (B-G)", "IR Test Result (B-G)",
//                        "IR Test Connection (C-G)", "IR Test Result (C-G)",
//                        "IR Test Connection (A-B)", "IR Test Result (A-B)",
//                        "IR Test Connection (B-C)", "IR Test Result (B-C)",
//                        "IR Test Connection (C-A)", "IR Test Result (C-A)"
//                };
//
//                String irLinkBase = "ReportMate/" + reportName + "/" + Utility.IrTest + "/";
//                String[] irTestImages = {
//                        irLinkBase + Utility.imgAgConnection + ".jpg", irLinkBase + Utility.imgAgResult + ".jpg",
//                        irLinkBase + Utility.imgBgConnection + ".jpg", irLinkBase + Utility.imgBgResult + ".jpg",
//                        irLinkBase + Utility.imgCgConnection + ".jpg", irLinkBase + Utility.imgCgResult + ".jpg",
//                        irLinkBase + Utility.imgAbConnection + ".jpg", irLinkBase + Utility.imgAbResult + ".jpg",
//                        irLinkBase + Utility.imgBcConnection + ".jpg", irLinkBase + Utility.imgBcResult + ".jpg",
//                        irLinkBase + Utility.imgCaConnection + ".jpg", irLinkBase + Utility.imgCaResult + ".jpg"
//                };
//
//                // Add images sections
//                updateProgress(callback, 70, "Adding panel images...");
//                addImageSectionAsync(document, "Panel general images", report, dbBoxImages, dbBoxTitles, callback);
//
//                updateProgress(callback, 80, "Adding IR test images...");
//                addImageSectionAsync(document, "MCCB & MCB: IR Test and Results", report, irTestImages, irTestLabels, callback);
//
//                // Save document
//                updateProgress(callback, 90, "Saving document...");
//                String savedPath = saveDocumentAsync(context, document, reportName);
//
//                updateProgress(callback, 100, "Report generated successfully!");
//
//                // Success callback
//                new Handler(Looper.getMainLooper()).post(() -> {
//                    if (callback != null) {
//                        callback.onSuccess(savedPath);
//                    }
//                });
//
//            } catch (Exception e) {
//                Log.e(TAG, "Error generating report", e);
//
//                // Error callback
//                new Handler(Looper.getMainLooper()).post(() -> {
//                    if (callback != null) {
//                        callback.onError("Error generating report: " + e.getMessage());
//                    }
//                });
//            }
//        });
//    }
//
//    /**
//     * Synchronous version for backward compatibility
//     */
//    public static boolean generateReport(Context context, String reportName, Report report) {
//        try {
//            XWPFDocument document = new XWPFDocument();
//
//            // Create document titles
//            createDocumentTitles(document, report);
//
//            // Create the main table with exact structure
//            createExactReportTable(document, report, null);
//
//            String[] dbBoxTitles = {
//                    "Temperature & Humidity",
//                    "Panel",
//                    "Panel inside",
//                    "Additional View"
//            };
//
//            String[] dbBoxImages = {
//                    "ReportMate/" + reportName + "/" + Utility.generalImageTemperature + ".jpg",
//                    "ReportMate/" + reportName + "/" + Utility.dbBoxPanelFront + ".jpg",
//                    "ReportMate/" + reportName + "/" + Utility.dbBoxPanelInside + ".jpg",
//                    "ReportMate/" + reportName + "/" + Utility.dbBoxPanelNameplate + ".jpg"
//            };
//
//            String[] irTestLabels = {
//                    "IR Test Connection (A-G)", "IR Test Result (A-G)",
//                    "IR Test Connection (B-G)", "IR Test Result (B-G)",
//                    "IR Test Connection (C-G)", "IR Test Result (C-G)",
//                    "IR Test Connection (A-B)", "IR Test Result (A-B)",
//                    "IR Test Connection (B-C)", "IR Test Result (B-C)",
//                    "IR Test Connection (C-A)", "IR Test Result (C-A)"
//            };
//
//            String irLinkBase = "ReportMate/" + reportName + "/" + Utility.IrTest + "/";
//            String[] irTestImages = {
//                    irLinkBase + Utility.imgAgConnection + ".jpg", irLinkBase + Utility.imgAgResult + ".jpg",
//                    irLinkBase + Utility.imgBgConnection + ".jpg", irLinkBase + Utility.imgBgResult + ".jpg",
//                    irLinkBase + Utility.imgCgConnection + ".jpg", irLinkBase + Utility.imgCgResult + ".jpg",
//                    irLinkBase + Utility.imgAbConnection + ".jpg", irLinkBase + Utility.imgAbResult + ".jpg",
//                    irLinkBase + Utility.imgBcConnection + ".jpg", irLinkBase + Utility.imgBcResult + ".jpg",
//                    irLinkBase + Utility.imgCaConnection + ".jpg", irLinkBase + Utility.imgCaResult + ".jpg"
//            };
//
//            addImageSection(document, "Panel general images", report, dbBoxImages, dbBoxTitles);
//            addImageSection(document, "MCCB & MCB: IR Test and Results", report, irTestImages, irTestLabels);
//
//            // Save document
//            return saveDocument(context, document, reportName);
//
//        } catch (Exception e) {
//            Log.e(TAG, "Error generating report", e);
//            Toast.makeText(context, "Error generating report: " + e.getMessage(), Toast.LENGTH_LONG).show();
//            return false;
//        }
//    }
//
//    /**
//     * Update progress on main thread
//     */
//    private static void updateProgress(ReportGenerationCallback callback, int progress, String message) {
//        new Handler(Looper.getMainLooper()).post(() -> {
//            if (callback != null) {
//                callback.onProgress(progress, message);
//            }
//        });
//    }
//
//    /**
//     * Create document titles exactly as in original
//     */
//    private static void createDocumentTitles(XWPFDocument document, Report report) {
//        // Main title
//        XWPFParagraph titlePara = document.createParagraph();
//        titlePara.setAlignment(ParagraphAlignment.CENTER);
//        XWPFRun titleRun = titlePara.createRun();
//        titleRun.setText(report.getProjectNo());
//        titleRun.setBold(true);
//        titleRun.setFontFamily("Arial");
//        titleRun.setFontSize(16);
//
//        // Subtitle
//        XWPFParagraph subtitlePara = document.createParagraph();
//        subtitlePara.setAlignment(ParagraphAlignment.CENTER);
//        XWPFRun subtitleRun = subtitlePara.createRun();
//        subtitleRun.setText(report.getProjectNo() + ": Test and Inspection Report");
//        subtitleRun.setBold(true);
//        subtitleRun.setFontFamily("Arial");
//        subtitleRun.setFontSize(14);
//
//        // Add spacing
//        document.createParagraph();
//    }
//
//    /**
//     * Create the exact table structure matching the original document
//     */
//    private static void createExactReportTable(XWPFDocument document, Report report, ReportGenerationCallback callback) {
//        // Create table with 12 columns to match original structure
//        XWPFTable table = document.createTable();
//        setTableBorders(table);
//        setTableWidth(table, 100);
//
//        if (callback != null) {
//            updateProgress(callback, 25, "Creating table headers...");
//        }
//
//        // Row 1: PANEL BOARD CIRCUIT BREAKER TEST REPORT (colspan=12)
//        XWPFTableRow row1 = table.getRow(0);
//        expandRowTo12Columns(row1);
//        mergeCellsHorizontally(table, 0, 0, 11);
//        setCellText(row1.getCell(0), "PANEL BOARD CIRCUIT BREAKER TEST REPORT", true, ParagraphAlignment.CENTER);
//
//        // Row 2: SHEET NO section (colspan=7, 3, 2)
//        XWPFTableRow row2 = table.createRow();
//        expandRowTo12Columns(row2);
//        mergeCellsHorizontally(table, 1, 0, 6);
//        mergeCellsHorizontally(table, 1, 7, 9);
//        mergeCellsHorizontally(table, 1, 10, 11);
//        setCellText(row2.getCell(0), "", false, ParagraphAlignment.LEFT);
//        setCellText(row2.getCell(7), "SHEET NO:01", false, ParagraphAlignment.LEFT);
//        setCellText(row2.getCell(10), "of :07", false, ParagraphAlignment.LEFT);
//
//        // Continue with remaining table creation...
//        createTableRows(table, report, callback);
//
//        if (callback != null) {
//            updateProgress(callback, 50, "Adding circuit data...");
//        }
//
//        // Add circuit data using loop
//        addCircuitDataWithLoop(table, callback);
//
//        if (callback != null) {
//            updateProgress(callback, 55, "Adding remarks...");
//        }
//
//        // Add remarks section
//        addRemarksToTable(table);
//    }
//
//    /**
//     * Create remaining table rows
//     */
//    private static void createTableRows(XWPFTable table, Report report, ReportGenerationCallback callback) {
//        if (callback != null) {
//            updateProgress(callback, 30, "Adding customer information...");
//        }
//
//        // Row 3: CUSTOMER, DATE, PROJECT NO (colspan=7, 3, 2)
//        XWPFTableRow row3 = table.createRow();
//        expandRowTo12Columns(row3);
//        mergeCellsHorizontally(table, 2, 0, 6);
//        mergeCellsHorizontally(table, 2, 7, 9);
//        mergeCellsHorizontally(table, 2, 10, 11);
//        setCellText(row3.getCell(0), "CUSTOMER: " + report.getCustomerName(), false, ParagraphAlignment.LEFT);
//        setCellText(row3.getCell(7), "DATE: " + report.getTestDate(), false, ParagraphAlignment.LEFT);
//        setCellText(row3.getCell(10), "PROJECT NO: " + report.getProjectNo(), false, ParagraphAlignment.LEFT);
//
//        // Row 4: ADDRESS, AIR TEMP, REL HUMIDITY (colspan=7, 3, 2)
//        XWPFTableRow row4 = table.createRow();
//        expandRowTo12Columns(row4);
//        mergeCellsHorizontally(table, 3, 0, 6);
//        mergeCellsHorizontally(table, 3, 7, 9);
//        mergeCellsHorizontally(table, 3, 10, 11);
//        setCellText(row4.getCell(0), "ADDRESS: " + report.getCustomerAddress(), false, ParagraphAlignment.LEFT);
//        setCellText(row4.getCell(7), "AIR TEMP: " + report.getEquipment().getAirTemperature(), false, ParagraphAlignment.LEFT);
//        setCellText(row4.getCell(10), "REL HUMIDITY: " + report.getEquipment().getAirHumidity(), false, ParagraphAlignment.LEFT);
//
//        if (callback != null) {
//            updateProgress(callback, 35, "Adding equipment details...");
//        }
//
//        // Continue with remaining rows...
//        addEquipmentAndInsulationRows(table, report);
//
//        if (callback != null) {
//            updateProgress(callback, 45, "Adding circuit headers...");
//        }
//
//        // Add circuit headers
//        addCircuitHeaders(table);
//    }
//
//    /**
//     * Add equipment and insulation resistance rows
//     */
//    private static void addEquipmentAndInsulationRows(XWPFTable table, Report report) {
//        // Row 5: OWNER/USER, DATE LAST INSPECTION (colspan=7, 5)
//        XWPFTableRow row5 = table.createRow();
//        expandRowTo12Columns(row5);
//        mergeCellsHorizontally(table, 4, 0, 6);
//        mergeCellsHorizontally(table, 4, 7, 11);
//        setCellText(row5.getCell(0), "OWNER/ USER: " + report.getOwnerIdentification(), false, ParagraphAlignment.LEFT);
//        setCellText(row5.getCell(7), "DATE LAST INSPECTION: NOT AVAILABLE", false, ParagraphAlignment.LEFT);
//
//        // Row 6: ADDRESS, LAST INSPECTION REPORT NO (colspan=7, 5)
//        XWPFTableRow row6 = table.createRow();
//        expandRowTo12Columns(row6);
//        mergeCellsHorizontally(table, 5, 0, 6);
//        mergeCellsHorizontally(table, 5, 7, 11);
//        setCellText(row6.getCell(0), "ADDRESS: " + report.getCustomerAddress(), false, ParagraphAlignment.LEFT);
//        setCellText(row6.getCell(7), "LAST INSPECTION REPORT NO: NOT AVAILABLE", false, ParagraphAlignment.LEFT);
//
//        // Row 7: EQUIPMENT LOCATION (colspan=12)
//        XWPFTableRow row7 = table.createRow();
//        expandRowTo12Columns(row7);
//        mergeCellsHorizontally(table, 6, 0, 11);
//        setCellText(row7.getCell(0), "EQUIPMENT LOCATION: " + report.getEquipment().getEquipmentLocation(), false, ParagraphAlignment.LEFT);
//
//        // Row 8: OWNER IDENTIFICATION (colspan=12)
//        XWPFTableRow row8 = table.createRow();
//        expandRowTo12Columns(row8);
//        mergeCellsHorizontally(table, 7, 0, 11);
//        setCellText(row8.getCell(0), "OWNER IDENTIFICATION: " + report.getOwnerIdentification(), false, ParagraphAlignment.LEFT);
//
//        // Row 9: Empty (colspan=12)
//        XWPFTableRow row9 = table.createRow();
//        expandRowTo12Columns(row9);
//        mergeCellsHorizontally(table, 8, 0, 11);
//        setCellText(row9.getCell(0), "", false, ParagraphAlignment.CENTER);
//
//        // Row 10: PANEL BUS INSULATION RESISTANCE IN MEGAOHMS (colspan=12)
//        XWPFTableRow row10 = table.createRow();
//        expandRowTo12Columns(row10);
//        mergeCellsHorizontally(table, 9, 0, 11);
//        setCellText(row10.getCell(0), "PANEL BUS INSULATION RESISTANCE IN MEGAOHMS", true, ParagraphAlignment.CENTER);
//
//        // Row 11: Insulation resistance values (colspan=2,2,2,2,2,2)
//        XWPFTableRow row11 = table.createRow();
//        expandRowTo12Columns(row11);
//        mergeCellsHorizontally(table, 10, 0, 1);
//        mergeCellsHorizontally(table, 10, 2, 3);
//        mergeCellsHorizontally(table, 10, 4, 5);
//        mergeCellsHorizontally(table, 10, 6, 7);
//        mergeCellsHorizontally(table, 10, 8, 9);
//        mergeCellsHorizontally(table, 10, 10, 11);
//        setCellText(row11.getCell(0), "A-G: >2100", false, ParagraphAlignment.CENTER);
//        setCellText(row11.getCell(2), "B-G: >2100", false, ParagraphAlignment.CENTER);
//        setCellText(row11.getCell(4), "C-G: >2100", false, ParagraphAlignment.CENTER);
//        setCellText(row11.getCell(6), "A-B: >2100", false, ParagraphAlignment.CENTER);
//        setCellText(row11.getCell(8), "B-C: >2100", false, ParagraphAlignment.CENTER);
//        setCellText(row11.getCell(10), "C-A: >2100", false, ParagraphAlignment.CENTER);
//
//        // Add panel board rating rows
//        addPanelBoardRatingRows(table);
//    }
//
//    /**
//     * Add panel board rating rows
//     */
//    private static void addPanelBoardRatingRows(XWPFTable table) {
//        // Row 12: PANELBOARD Rating, AMPS, VOLTAGE (colspan=4,4,4)
//        XWPFTableRow row12 = table.createRow();
//        expandRowTo12Columns(row12);
//        mergeCellsHorizontally(table, 11, 0, 3);
//        mergeCellsHorizontally(table, 11, 4, 7);
//        mergeCellsHorizontally(table, 11, 8, 11);
//        setCellText(row12.getCell(0), "PANELBOARD Rating", false, ParagraphAlignment.LEFT);
//        setCellText(row12.getCell(4), "AMPS: 310 A", false, ParagraphAlignment.LEFT);
//        setCellText(row12.getCell(8), "VOLTAGE: 415 V", false, ParagraphAlignment.LEFT);
//
//        // Row 13: TEST VOLTAGE, MODEL NO, CATALOG (colspan=4,4,4)
//        XWPFTableRow row13 = table.createRow();
//        expandRowTo12Columns(row13);
//        mergeCellsHorizontally(table, 12, 0, 3);
//        mergeCellsHorizontally(table, 12, 4, 7);
//        mergeCellsHorizontally(table, 12, 8, 11);
//        setCellText(row13.getCell(0), "TEST VOLTAGE: 1KV", false, ParagraphAlignment.LEFT);
//        setCellText(row13.getCell(4), "MODEL NO.: NOT AVAILABLE", false, ParagraphAlignment.LEFT);
//        setCellText(row13.getCell(8), "CATALOG: NOT AVAILABLE", false, ParagraphAlignment.LEFT);
//
//        // Add MFG rows
//        for (int i = 0; i < 4; i++) {
//            XWPFTableRow mfgRow = table.createRow();
//            expandRowTo12Columns(mfgRow);
//            mergeCellsHorizontally(table, 13 + i, 0, 3);
//            mergeCellsHorizontally(table, 13 + i, 4, 7);
//            mergeCellsHorizontally(table, 13 + i, 8, 11);
//
//            if (i == 0) {
//                setCellText(mfgRow.getCell(0), "MFG.:", false, ParagraphAlignment.LEFT);
//                setCellText(mfgRow.getCell(4), "CURVE NO.: 1.5 to 10 Times of Full Load Current", false, ParagraphAlignment.LEFT);
//                setCellText(mfgRow.getCell(8), "CURVE RANGE:", false, ParagraphAlignment.LEFT);
//            } else if (i == 1) {
//                setCellText(mfgRow.getCell(0), "MFG.:", false, ParagraphAlignment.LEFT);
//                setCellText(mfgRow.getCell(4), "CURVE NO.: Type -B", false, ParagraphAlignment.LEFT);
//                setCellText(mfgRow.getCell(8), "CURVE RANGE: 2 to 16 Times of Full Load Current", false, ParagraphAlignment.LEFT);
//            } else {
//                setCellText(mfgRow.getCell(0), "MFG.", false, ParagraphAlignment.LEFT);
//                setCellText(mfgRow.getCell(4), "CURVE NO.", false, ParagraphAlignment.LEFT);
//                setCellText(mfgRow.getCell(8), "CURVE RANGE", false, ParagraphAlignment.LEFT);
//            }
//        }
//    }
//
//    /**
//     * Add circuit headers
//     */
//    private static void addCircuitHeaders(XWPFTable table) {
//        XWPFTableRow row18 = table.createRow();
//        expandRowTo12Columns(row18);
//        setCellText(row18.getCell(0), "CIRCUIT\n#", false, ParagraphAlignment.CENTER);
//        setCellText(row18.getCell(1), "CKT.\nBKR.\nSIZE", false, ParagraphAlignment.CENTER);
//        setCellText(row18.getCell(2), "TEST\nAMPS", false, ParagraphAlignment.CENTER);
//        setCellText(row18.getCell(3), "TRIP\nTIME", false, ParagraphAlignment.CENTER);
//        setCellText(row18.getCell(4), "INST\nTRIP", false, ParagraphAlignment.CENTER);
//        setCellText(row18.getCell(5), "CONTACT\nRESIS", false, ParagraphAlignment.CENTER);
//        setCellText(row18.getCell(6), "CIRCUIT\n#", false, ParagraphAlignment.CENTER);
//        setCellText(row18.getCell(7), "CKT.\nBKR.\nSIZE", false, ParagraphAlignment.CENTER);
//        setCellText(row18.getCell(8), "TEST\nAMPS", false, ParagraphAlignment.CENTER);
//        setCellText(row18.getCell(9), "TRIP\nTIME", false, ParagraphAlignment.CENTER);
//        setCellText(row18.getCell(10), "INST\nTRIP", false, ParagraphAlignment.CENTER);
//        setCellText(row18.getCell(11), "CONTACT\nRESIS", false, ParagraphAlignment.CENTER);
//    }
//
//    /**
//     * Add circuit data using loop with progress updates
//     */
//    private static void addCircuitDataWithLoop(XWPFTable table, ReportGenerationCallback callback) {
//        String[][] circuitData = {
//                {"CKT-0", "200\nA", "600 A", "22s", "OK", "3.40\nmΩ,3.08\nmΩ,2.95\nmΩ"},
//                {"CKT-1", "80 A", "243 A", "48s", "OK", "4.15\nmΩ,3.86\nmΩ,3.95\nmΩ,3.79\nmΩ"},
//                {"CKT-2", "80 A", "242 A", "54 s", "OK", "3.53\nmΩ,4.25\nmΩ"},
//                {"CKT-3", "80 A", "244 A", "33s", "OK", "3.23\nmΩ,3.23\nmΩ,2.85\nmΩ"},
//                {"CKT-4", "63 A", "189s", "28 s", "OK", "2.29\nmΩ,3.43\nmΩ,3.96\nmΩ,2.89\nmΩ"},
//                {"CKT-5", "80 A", "237 s", "51 s", "OK", "3.83\nmΩ,3.04\nmΩ"},
//                {"CKT-6", "80 A", "243 s", "46 s", "OK", "3.17\nmΩ,3.32\nmΩ,2.65\nmΩ"},
//                {"CKT-7", "100 A", "300 A", "35s", "OK", "2.50\nmΩ,2.75\nmΩ"},
//                {"CKT-8", "125 A", "375 A", "40s", "OK", "2.10\nmΩ,2.35\nmΩ,2.60\nmΩ"},
//                {"CKT-9", "150 A", "450 A", "30s", "OK", "1.95\nmΩ,2.20\nmΩ"},
//                {"CKT-10", "200 A", "600 A", "25s", "OK", "1.80\nmΩ,2.05\nmΩ,2.30\nmΩ"},
//                {"CKT-11", "250 A", "750 A", "20s", "OK", "1.65\nmΩ,1.90\nmΩ"},
//                {"CKT-12", "300 A", "900 A", "18s", "OK", "1.50\nmΩ,1.75\nmΩ,2.00\nmΩ"}
//        };
//
//        // Add circuits using a loop - process pairs (left and right sides)
//        for (int i = 0; i < circuitData.length; i += 2) {
//            // Progress update for each circuit pair
//            if (callback != null) {
//                int circuitProgress = 50 + (int)((double)(i + 2) / circuitData.length * 5);
//                updateProgress(callback, circuitProgress, "Processing circuit " + (i/2 + 1) + "...");
//            }
//
//            // Left side data
//            String leftCircuit = circuitData[i][0];
//            String leftSize = circuitData[i][1];
//            String leftTestAmps = circuitData[i][2];
//            String leftTripTime = circuitData[i][3];
//            String leftInstTrip = circuitData[i][4];
//            String[] leftResistanceValues = circuitData[i][5].split(",");
//
//            // Right side data (next circuit or empty if odd number)
//            String rightCircuit, rightSize, rightTestAmps, rightTripTime, rightInstTrip;
//            String[] rightResistanceValues;
//
//            if (i + 1 < circuitData.length) {
//                // Use next circuit for right side
//                rightCircuit = circuitData[i + 1][0];
//                rightSize = circuitData[i + 1][1];
//                rightTestAmps = circuitData[i + 1][2];
//                rightTripTime = circuitData[i + 1][3];
//                rightInstTrip = circuitData[i + 1][4];
//                rightResistanceValues = circuitData[i + 1][5].split(",");
//            } else {
//                // Use empty data for right side if odd number of circuits
//                rightCircuit = "";
//                rightSize = "";
//                rightTestAmps = "";
//                rightTripTime = "";
//                rightInstTrip = "";
//                rightResistanceValues = new String[]{""};
//            }
//
//            addCircuitWithResistanceRowsBothSides(table,
//                    // Left side data
//                    leftCircuit, leftSize, leftTestAmps, leftTripTime, leftInstTrip, leftResistanceValues,
//                    // Right side data
//                    rightCircuit, rightSize, rightTestAmps, rightTripTime, rightInstTrip, rightResistanceValues);
//        }
//    }
//
//    /**
//     * Add images section asynchronously
//     */
//    private static void addImageSectionAsync(XWPFDocument document, String title, Report report,
//                                             String[] imagePaths, String[] imageNames,
//                                             ReportGenerationCallback callback) {
//        try {
//            // Add section heading
//            XWPFParagraph imageSectionPara = document.createParagraph();
//            imageSectionPara.setAlignment(ParagraphAlignment.LEFT);
//            imageSectionPara.setSpacingAfter(200);
//            imageSectionPara.setSpacingBefore(200);
//            XWPFRun imageSectionRun = imageSectionPara.createRun();
//            imageSectionRun.setText("6.2    DB-3.1 " + title);
//            imageSectionRun.setBold(