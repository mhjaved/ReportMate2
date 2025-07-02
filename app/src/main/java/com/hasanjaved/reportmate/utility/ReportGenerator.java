package com.hasanjaved.reportmate.utility;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.model.IrTest;
import com.hasanjaved.reportmate.model.ManufacturerCurveDetails;
import com.hasanjaved.reportmate.model.PanelBoard;
import com.hasanjaved.reportmate.model.Report;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ReportGenerator {

    public interface ReportGenerationCallback {
        void onStarted();

        void onProgress(int progress, String message);

        void onSuccess(String filePath);

        void onError(String errorMessage);
    }

    private static final String TAG = "ReportGenerator";
    private static final String REPORTS_DIRECTORY = "ReportMateReports";

    /**
     * Generate MCCB Report
     *
     * @param context    Application context
     * @param reportName Name of the report file (without extension)
     * @return Success status
     */
    public static void generateReport(Context context, String reportName, Report report, ReportGenerationCallback callback) {
        try {
            XWPFDocument document = new XWPFDocument();

            // Create document titles
            createDocumentTitles(document, report);

            // Create the main table with exact structure
            createExactReportTable(document, report);

            String[] dbBoxTitles = {
                    "Temperature & Humidity",
                    "Panel",
                    "Panel inside",
                    "Additional View"
            };

            // Add images section
            String[] dbBoxImages = {
                    "ReportMate/" + reportName + "/" + Utility.generalImageTemperature + ".jpg",
                    "ReportMate/" + reportName + "/" + Utility.dbBoxPanelFront + ".jpg",
                    "ReportMate/" + reportName + "/" + Utility.dbBoxPanelInside + ".jpg",
                    "ReportMate/" + reportName + "/" + Utility.dbBoxPanelNameplate + ".jpg"
            };

//                                        Utility.showLog(dbBoxImages[0]);


            String[] irTestLabels = {
                    "IR Test Connection (A-G)", "IR Test Result (A-G)",
                    "IR Test Connection (B-G)", "IR Test Result (B-G)",
                    "IR Test Connection (C-G)", "IR Test Result (C-G)",

                    "IR Test Connection (A-B)", "IR Test Result (A-B)",
                    "IR Test Connection (B-C)", "IR Test Result (B-C)",
                    "IR Test Connection (C-A)", "IR Test Result (C-A)"
            };



            String irLinkBase = "ReportMate/" + reportName + "/" + Utility.IrTest + "/";
            String[] irTestImages = {
                    irLinkBase + Utility.imgAgConnection + ".jpg", irLinkBase + Utility.imgAgResult + ".jpg",
                    irLinkBase + Utility.imgBgConnection + ".jpg", irLinkBase + Utility.imgBgResult + ".jpg",
                    irLinkBase + Utility.imgCgConnection + ".jpg", irLinkBase + Utility.imgCgResult + ".jpg",

                    irLinkBase + Utility.imgAbConnection + ".jpg", irLinkBase + Utility.imgAbResult + ".jpg",
                    irLinkBase + Utility.imgBcConnection + ".jpg", irLinkBase + Utility.imgBcResult + ".jpg",
                    irLinkBase + Utility.imgCaConnection + ".jpg", irLinkBase + Utility.imgCaResult + ".jpg"
            };

            addImageSection(document, "Panel general images", report, dbBoxImages, dbBoxTitles);
            addImageSection(document, "MCCB & MCB: IR Test and Results", report, irTestImages, irTestLabels);

            String[] crmTestLabels = {
                    "CRM Test Connection",
                    "CRM Test Result"
            };

            String[] tripTestLabels = {
                    "Current Injector Connection",
                    "Injected Current",
                    "Trip Time",
                    "Trip"
            };
            if (report.getEquipment() != null)
                if (report.getEquipment().getCircuitBreakerList() != null)
                    if (!report.getEquipment().getCircuitBreakerList().isEmpty()){
                        List<CircuitBreaker> circuitBreakerList = report.getEquipment().getCircuitBreakerList();

                        for (CircuitBreaker circuitBreaker: circuitBreakerList){

                            String[] crmImages = new String[2];
                            List<String> imagesCrm = Utility.getCrmImageForReport(context,circuitBreaker.getName());
                            crmImages[0] = imagesCrm.get(0);
                            crmImages[1] = imagesCrm.get(1);

                            addImageSection(document, circuitBreaker.getName()+", CRM Test Connection and Result Pictures", report, crmImages, crmTestLabels);

                            String[] tripImages = new String[4];
                            List<String> imagesTrip = Utility.getTripImageForReport(context,circuitBreaker.getName());
                            tripImages[0] = imagesTrip.get(0);
                            tripImages[1] = imagesTrip.get(1);
                            tripImages[2] = imagesTrip.get(2);
                            tripImages[3] = imagesTrip.get(3);

                            addImageSection(document, circuitBreaker.getName()+", Tripping Test Connection and Result Pictures", report, tripImages, tripTestLabels);

                        }
                    }


            boolean success = saveDocument(context, document, reportName);

            if (success) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (callback != null) {
                        callback.onSuccess("savedPath");
                    }
                });

            }

            // Save document
//            return success;

        } catch (Exception e) {
            Log.e(TAG, "Error generating report", e);
//            Toast.makeText(context, "Error generating report: " + e.getMessage(), Toast.LENGTH_LONG).show();
//            return false;

            new Handler(Looper.getMainLooper()).post(() -> {
                if (callback != null) {
                    callback.onError("Error generating report: " + e.getMessage());
                }
            });
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
        subtitleRun.setText(report.getProjectNo() + ": Test and Inspection Report");
        subtitleRun.setBold(true);
        subtitleRun.setFontFamily("Arial");
        subtitleRun.setFontSize(14);

        // Add spacing
        document.createParagraph();
    }

    static ManufacturerCurveDetails manufacturerCurveDetails = null;
    static PanelBoard panelBoard = null;
    static IrTest irTest = null;

    private static String checkNull(String val) {
        if (val == null) return "";
        else return val;
    }

    /**
     * Create the exact table structure matching the original document
     */
    private static void createExactReportTable(XWPFDocument document, Report report) {

        // Create table with 12 columns to match original structure
        XWPFTable table = document.createTable();
        setTableBorders(table);
        setTableWidth(table, 100);

        // Row 1: PANEL BOARD CIRCUIT BREAKER TEST REPORT (colspan=12)
        XWPFTableRow row1 = table.getRow(0);
        expandRowTo12Columns(row1);
        mergeCellsHorizontally(table, 0, 0, 11);
        setCellText(row1.getCell(0), "PANEL BOARD CIRCUIT BREAKER TEST REPORT", true, ParagraphAlignment.CENTER);

        // Row 2: SHEET NO section (colspan=7, 3, 2)
        XWPFTableRow row2 = table.createRow();
        expandRowTo12Columns(row2);
        mergeCellsHorizontally(table, 1, 0, 6);   // colspan=7
        mergeCellsHorizontally(table, 1, 7, 9);   // colspan=3
        mergeCellsHorizontally(table, 1, 10, 11); // colspan=2
        setCellText(row2.getCell(0), "", false, ParagraphAlignment.LEFT);
        setCellText(row2.getCell(7), "SHEET NO:", false, ParagraphAlignment.LEFT);
        setCellText(row2.getCell(10), "of :", false, ParagraphAlignment.LEFT);


        // Row 3: CUSTOMER, DATE, PROJECT NO (colspan=7, 3, 2)
        XWPFTableRow row3 = table.createRow();
        expandRowTo12Columns(row3);
        mergeCellsHorizontally(table, 2, 0, 6);   // colspan=7
        mergeCellsHorizontally(table, 2, 7, 9);   // colspan=3
        mergeCellsHorizontally(table, 2, 10, 11); // colspan=2

        setCellText(row3.getCell(0), "CUSTOMER: " + report.getCustomerName(), false, ParagraphAlignment.LEFT);
        setCellText(row3.getCell(7), "DATE: " + report.getTestDate(), false, ParagraphAlignment.LEFT);
        setCellText(row3.getCell(10), "PROJECT NO: " + report.getProjectNo(), false, ParagraphAlignment.LEFT);

        // Row 4: ADDRESS, AIR TEMP, REL HUMIDITY (colspan=7, 3, 2)
        XWPFTableRow row4 = table.createRow();
        expandRowTo12Columns(row4);
        mergeCellsHorizontally(table, 3, 0, 6);   // colspan=7
        mergeCellsHorizontally(table, 3, 7, 9);   // colspan=3
        mergeCellsHorizontally(table, 3, 10, 11); // colspan=2
        setCellText(row4.getCell(0), "ADDRESS: " + report.getCustomerAddress(), false, ParagraphAlignment.LEFT);
        setCellText(row4.getCell(7), "AIR TEMP: " + report.getEquipment().getAirTemperature(), false, ParagraphAlignment.LEFT);
        setCellText(row4.getCell(10), "REL HUMIDITY: " + report.getEquipment().getAirHumidity(), false, ParagraphAlignment.LEFT);

        // Row 5: OWNER/USER, DATE LAST INSPECTION (colspan=7, 5)
        XWPFTableRow row5 = table.createRow();
        expandRowTo12Columns(row5);
        mergeCellsHorizontally(table, 4, 0, 6);   // colspan=7
        mergeCellsHorizontally(table, 4, 7, 11);  // colspan=5
        setCellText(row5.getCell(0), "OWNER/ USER: " + report.getOwnerIdentification(), false, ParagraphAlignment.LEFT);
        setCellText(row5.getCell(7), "DATE LAST INSPECTION: NOT AVAILABLE", false, ParagraphAlignment.LEFT);

        // Row 6: ADDRESS, LAST INSPECTION REPORT NO (colspan=7, 5)
        XWPFTableRow row6 = table.createRow();
        expandRowTo12Columns(row6);
        mergeCellsHorizontally(table, 5, 0, 6);   // colspan=7
        mergeCellsHorizontally(table, 5, 7, 11);  // colspan=5
        setCellText(row6.getCell(0), "ADDRESS: " + report.getCustomerAddress(), false, ParagraphAlignment.LEFT);
        setCellText(row6.getCell(7), "LAST INSPECTION REPORT NO: NOT AVAILABLE", false, ParagraphAlignment.LEFT);

        // Row 7: EQUIPMENT LOCATION (colspan=12)
        XWPFTableRow row7 = table.createRow();
        expandRowTo12Columns(row7);
        mergeCellsHorizontally(table, 6, 0, 11);
        setCellText(row7.getCell(0), "EQUIPMENT LOCATION: " + report.getEquipment().getEquipmentLocation(), false, ParagraphAlignment.LEFT);

        // Row 8: OWNER IDENTIFICATION (colspan=12)
        XWPFTableRow row8 = table.createRow();
        expandRowTo12Columns(row8);
        mergeCellsHorizontally(table, 7, 0, 11);
        setCellText(row8.getCell(0), "OWNER IDENTIFICATION: " + report.getOwnerIdentification(), false, ParagraphAlignment.LEFT);

        // Row 9: Empty (colspan=12)
        XWPFTableRow row9 = table.createRow();
        expandRowTo12Columns(row9);
        mergeCellsHorizontally(table, 8, 0, 11);
        setCellText(row9.getCell(0), "", false, ParagraphAlignment.CENTER);

        // Row 10: PANEL BUS INSULATION RESISTANCE IN MEGAOHMS (colspan=12)
        XWPFTableRow row10 = table.createRow();
        expandRowTo12Columns(row10);
        mergeCellsHorizontally(table, 9, 0, 11);
        setCellText(row10.getCell(0), "PANEL BUS INSULATION RESISTANCE IN MEGAOHMS", true, ParagraphAlignment.CENTER);

        // Row 11: Insulation resistance values (colspan=2,2,2,2,2,2)
        XWPFTableRow row11 = table.createRow();
        expandRowTo12Columns(row11);
        mergeCellsHorizontally(table, 10, 0, 1);   // A-G
        mergeCellsHorizontally(table, 10, 2, 3);   // B-G
        mergeCellsHorizontally(table, 10, 4, 5);   // C-G
        mergeCellsHorizontally(table, 10, 6, 7);   // A-B
        mergeCellsHorizontally(table, 10, 8, 9);   // B-C
        mergeCellsHorizontally(table, 10, 10, 11); // C-A

        irTest = report.getIrTest();
        setCellText(row11.getCell(0), "A-G: " + checkNull(irTest.getAgValue()), false, ParagraphAlignment.CENTER);
        setCellText(row11.getCell(2), "B-G: " + checkNull(irTest.getBgValue()), false, ParagraphAlignment.CENTER);
        setCellText(row11.getCell(4), "C-G: " + checkNull(irTest.getCgValue()), false, ParagraphAlignment.CENTER);
        setCellText(row11.getCell(6), "A-B: " + checkNull(irTest.getAbValue()), false, ParagraphAlignment.CENTER);
        setCellText(row11.getCell(8), "B-C: " + checkNull(irTest.getBcValue()), false, ParagraphAlignment.CENTER);
        setCellText(row11.getCell(10), "C-A: " + checkNull(irTest.getCaValue()), false, ParagraphAlignment.CENTER);


        panelBoard = report.getPanelBoard();
        // Row 12: PANELBOARD Rating, AMPS, VOLTAGE (colspan=4,4,4)
        XWPFTableRow row12 = table.createRow();
        expandRowTo12Columns(row12);
        mergeCellsHorizontally(table, 11, 0, 3);   // colspan=4
        mergeCellsHorizontally(table, 11, 4, 7);   // colspan=4
        mergeCellsHorizontally(table, 11, 8, 11);  // colspan=4
        setCellText(row12.getCell(0), "PANELBOARD Rating", false, ParagraphAlignment.LEFT);
        setCellText(row12.getCell(4), "AMPS:" + checkNull(panelBoard.getAmps()) + "A", false, ParagraphAlignment.LEFT);
        setCellText(row12.getCell(8), "VOLTAGE: " + checkNull(panelBoard.getVoltage()) + "V", false, ParagraphAlignment.LEFT);

        // Row 13: TEST VOLTAGE, MODEL NO, CATALOG (colspan=4,4,4)
        XWPFTableRow row13 = table.createRow();
        expandRowTo12Columns(row13);
        mergeCellsHorizontally(table, 12, 0, 3);   // colspan=4
        mergeCellsHorizontally(table, 12, 4, 7);   // colspan=4
        mergeCellsHorizontally(table, 12, 8, 11);  // colspan=4
        setCellText(row13.getCell(0), "TEST VOLTAGE:" + checkNull(panelBoard.getTestVoltage()) + "V", false, ParagraphAlignment.LEFT);
        setCellText(row13.getCell(4), "MODEL NO.: " + checkNull(panelBoard.getModelNo()), false, ParagraphAlignment.LEFT);
        setCellText(row13.getCell(8), "CATALOG:" + checkNull(panelBoard.getCatalog()), false, ParagraphAlignment.LEFT);


        manufacturerCurveDetails = report.getManufacturerCurveDetails();

        // Row 14: MFG, CURVE NO Type-C, CURVE RANGE (colspan=4,4,4)
        XWPFTableRow row14 = table.createRow();
        expandRowTo12Columns(row14);
        mergeCellsHorizontally(table, 13, 0, 3);   // colspan=4
        mergeCellsHorizontally(table, 13, 4, 7);   // colspan=4
        mergeCellsHorizontally(table, 13, 8, 11);  // colspan=4
        setCellText(row14.getCell(0), "MFG.:" + checkNull(manufacturerCurveDetails.getMfgOne()), false, ParagraphAlignment.LEFT);
        setCellText(row14.getCell(4), "CURVE NO.: " + checkNull(manufacturerCurveDetails.getCurveNumberOne()), false, ParagraphAlignment.LEFT);
        setCellText(row14.getCell(8), "CURVE RANGE:" + checkNull(manufacturerCurveDetails.getCurveRangeOne()), false, ParagraphAlignment.LEFT);

        // Row 15: MFG, CURVE NO Type-B, CURVE RANGE (colspan=4,4,4)
        XWPFTableRow row15 = table.createRow();
        expandRowTo12Columns(row15);
        mergeCellsHorizontally(table, 14, 0, 3);   // colspan=4
        mergeCellsHorizontally(table, 14, 4, 7);   // colspan=4
        mergeCellsHorizontally(table, 14, 8, 11);  // colspan=4

        setCellText(row15.getCell(0), "MFG.:" + checkNull(manufacturerCurveDetails.getMfgTwo()), false, ParagraphAlignment.LEFT);
        setCellText(row15.getCell(4), "CURVE NO.:" + checkNull(manufacturerCurveDetails.getCurveNumberTwo()), false, ParagraphAlignment.LEFT);
        setCellText(row15.getCell(8), "CURVE RANGE:" + checkNull(manufacturerCurveDetails.getCurveRangeTwo()), false, ParagraphAlignment.LEFT);

        // Row 16: Empty MFG rows (colspan=4,4,4)
        XWPFTableRow row16 = table.createRow();
        expandRowTo12Columns(row16);
        mergeCellsHorizontally(table, 15, 0, 3);   // colspan=4
        mergeCellsHorizontally(table, 15, 4, 7);   // colspan=4
        mergeCellsHorizontally(table, 15, 8, 11);  // colspan=4
        setCellText(row16.getCell(0), "MFG." + checkNull(manufacturerCurveDetails.getMfgThree()), false, ParagraphAlignment.LEFT);
        setCellText(row16.getCell(4), "CURVE NO." + checkNull(manufacturerCurveDetails.getCurveNumberThree()), false, ParagraphAlignment.LEFT);
        setCellText(row16.getCell(8), "CURVE RANGE" + checkNull(manufacturerCurveDetails.getCurveRangeThree()), false, ParagraphAlignment.LEFT);

        // Row 17: Empty MFG rows (colspan=4,4,4)
        XWPFTableRow row17 = table.createRow();
        expandRowTo12Columns(row17);
        mergeCellsHorizontally(table, 16, 0, 3);   // colspan=4
        mergeCellsHorizontally(table, 16, 4, 7);   // colspan=4
        mergeCellsHorizontally(table, 16, 8, 11);  // colspan=4
        setCellText(row17.getCell(0), "MFG.", false, ParagraphAlignment.LEFT);
        setCellText(row17.getCell(4), "CURVE NO.", false, ParagraphAlignment.LEFT);
        setCellText(row17.getCell(8), "CURVE RANGE", false, ParagraphAlignment.LEFT);

        // Row 18: Circuit test header (all individual columns)
        XWPFTableRow row18 = table.createRow();
        expandRowTo12Columns(row18);
        setCellText(row18.getCell(0), "CIRCUIT\n#", false, ParagraphAlignment.CENTER);
        setCellText(row18.getCell(1), "CKT.\nBKR.\nSIZE", false, ParagraphAlignment.CENTER);
        setCellText(row18.getCell(2), "TEST\nAMPS", false, ParagraphAlignment.CENTER);
        setCellText(row18.getCell(3), "TRIP\nTIME", false, ParagraphAlignment.CENTER);
        setCellText(row18.getCell(4), "INST\nTRIP", false, ParagraphAlignment.CENTER);
        setCellText(row18.getCell(5), "CONTACT\nRESIS", false, ParagraphAlignment.CENTER);
        setCellText(row18.getCell(6), "CIRCUIT\n#", false, ParagraphAlignment.CENTER);
        setCellText(row18.getCell(7), "CKT.\nBKR.\nSIZE", false, ParagraphAlignment.CENTER);
        setCellText(row18.getCell(8), "TEST\nAMPS", false, ParagraphAlignment.CENTER);
        setCellText(row18.getCell(9), "TRIP\nTIME", false, ParagraphAlignment.CENTER);
        setCellText(row18.getCell(10), "INST\nTRIP", false, ParagraphAlignment.CENTER);
        setCellText(row18.getCell(11), "CONTACT\nRESIS", false, ParagraphAlignment.CENTER);


        List<CircuitBreaker> circuitBreakerList;
        if (report.getEquipment() != null) {
            if (report.getEquipment().getCircuitBreakerList() != null)
                if (!report.getEquipment().getCircuitBreakerList().isEmpty()) {
                    circuitBreakerList = report.getEquipment().getCircuitBreakerList();

                    Utility.showLog(circuitBreakerList.toString());

                    for (int i = 0; i < circuitBreakerList.size(); i += 2) {

                        String leftCircuit = "", leftSize = "", leftTestAmps = "", leftTripTime = "", leftInstTrip = "";
                        String[] leftResistanceValues = new String[3];

                        String rightCircuit = "", rightSize = "", rightTestAmps = "", rightTripTime = "", rightInstTrip = "";
                        String[] rightResistanceValues = new String[3];

                        try {
                            CircuitBreaker circuitBreakerLeft = null;
                            if (circuitBreakerList.get(i) != null) {

                                circuitBreakerLeft = circuitBreakerList.get(i);
                                leftCircuit = circuitBreakerLeft.getName();
                                leftSize = circuitBreakerLeft.getSize() + "A";
                                leftTestAmps = circuitBreakerLeft.getTripTest().getTestAmplitude() + "A";
                                leftTripTime = circuitBreakerLeft.getTripTest().getTripTime();
                                leftInstTrip = circuitBreakerLeft.getTripTest().getInstantTrip();

                                leftResistanceValues[0] = circuitBreakerLeft.getCrmTest().getrResValue() + circuitBreakerLeft.getCrmTest().getrResUnit();
                                leftResistanceValues[1] = circuitBreakerLeft.getCrmTest().getyResValue() + circuitBreakerLeft.getCrmTest().getyResUnit();
                                leftResistanceValues[2] = circuitBreakerLeft.getCrmTest().getbResValue() + circuitBreakerLeft.getCrmTest().getbResUnit();

                            }


                            CircuitBreaker circuitBreakerRight = null;

                            if (i + 1 < circuitBreakerList.size()) {
                                circuitBreakerRight = circuitBreakerList.get(i + 1);

                                rightCircuit = circuitBreakerRight.getName();
                                rightSize = circuitBreakerRight.getSize() + "A";
                                rightTestAmps = circuitBreakerRight.getTripTest().getTestAmplitude() + "A";
                                rightTripTime = circuitBreakerRight.getTripTest().getTripTime() + "sec";
                                rightInstTrip = circuitBreakerRight.getTripTest().getInstantTrip();

                                rightResistanceValues[0] = circuitBreakerRight.getCrmTest().getrResValue() + circuitBreakerRight.getCrmTest().getrResUnit();
                                rightResistanceValues[1] = circuitBreakerRight.getCrmTest().getyResValue() + circuitBreakerRight.getCrmTest().getyResUnit();
                                rightResistanceValues[2] = circuitBreakerRight.getCrmTest().getbResValue() + circuitBreakerRight.getCrmTest().getbResUnit();
                            }
                        } catch (Exception e) {
                            Utility.showLog(e.toString());
                        }


                        addCircuitWithResistanceRowsBothSides(table,
                                // Left side data
                                leftCircuit, leftSize, leftTestAmps, leftTripTime, leftInstTrip, leftResistanceValues,
                                // Right side data
                                rightCircuit, rightSize, rightTestAmps, rightTripTime, rightInstTrip, rightResistanceValues);

                    }

                }


        }




        // Add remarks section
        addRemarksToTable(table);
    }

    /**
     * Add circuit data to the right side of an existing row
     */
    private static void addCircuitToRightSide(XWPFTable table, int leftSideIndex,
                                              String circuitName, String size, String testAmps,
                                              String tripTime, String instTrip, String[] resistanceValues) {

        // Find the starting row for the left side circuit
        int circuitStartRow = findCircuitStartRow(table, leftSideIndex);

        if (circuitStartRow == -1) {
            // Left side circuit not found, add as new pair
            addCircuitWithResistanceRowsBothSides(table,
                    "", "", "", "", "", new String[]{""},
                    circuitName, size, testAmps, tripTime, instTrip, resistanceValues);
            return;
        }

        // Get the number of rows the left circuit uses
        int leftCircuitRows = getCircuitRowCount(table, circuitStartRow);
        int rightCircuitRows = resistanceValues.length;

        // Determine how many rows we need total
        int totalRows = Math.max(leftCircuitRows, rightCircuitRows);

        // Add additional rows if needed
        while (circuitStartRow + totalRows > table.getNumberOfRows()) {
            XWPFTableRow newRow = table.createRow();
            expandRowTo12Columns(newRow);
            // Fill left side cells with empty content
            for (int col = 0; col < 6; col++) {
                setCellText(newRow.getCell(col), "", false, ParagraphAlignment.CENTER);
            }
        }

        // Fill the right side data
        for (int i = 0; i < totalRows; i++) {
            XWPFTableRow row = table.getRow(circuitStartRow + i);

            if (i == 0) {
                // First row gets the circuit data
                setCellText(row.getCell(6), circuitName, false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(7), size, false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(8), testAmps, false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(9), tripTime, false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(10), instTrip, false, ParagraphAlignment.CENTER);
            } else {
                // Subsequent rows are empty for merged cells
                setCellText(row.getCell(6), "", false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(7), "", false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(8), "", false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(9), "", false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(10), "", false, ParagraphAlignment.CENTER);
            }

            // Right side contact resistance value for this row
            if (i < rightCircuitRows) {
                setCellText(row.getCell(11), resistanceValues[i], false, ParagraphAlignment.CENTER);
            } else {
                setCellText(row.getCell(11), "", false, ParagraphAlignment.CENTER);
            }
        }

        // Apply vertical merging to the right side columns (6-10)
        if (rightCircuitRows > 1) {
            for (int col = 6; col < 11; col++) {
                mergeVerticalCells(table, circuitStartRow, circuitStartRow + rightCircuitRows - 1, col);
            }
        }
    }

    /**
     * Find the starting row of a circuit based on its index in the left side
     */
    private static int findCircuitStartRow(XWPFTable table, int circuitIndex) {
        int currentCircuit = 0;
        int headerRows = 18; // Number of rows before circuit data starts

        for (int i = headerRows; i < table.getNumberOfRows(); i++) {
            XWPFTableRow row = table.getRow(i);
            if (row.getTableCells().size() > 0) {
                String cellText = row.getCell(0).getText().trim();
                if (cellText.startsWith("CKT-")) {
                    if (currentCircuit == circuitIndex) {
                        return i;
                    }
                    currentCircuit++;
                }
            }
        }
        return -1; // Not found
    }

    /**
     * Get the number of rows a circuit uses (based on resistance values)
     */
    private static int getCircuitRowCount(XWPFTable table, int startRow) {
        int count = 1;
        for (int i = startRow + 1; i < table.getNumberOfRows(); i++) {
            XWPFTableRow row = table.getRow(i);
            if (row.getTableCells().size() > 0) {
                String cellText = row.getCell(0).getText().trim();
                if (cellText.startsWith("CKT-") || cellText.equals("Remarks:")) {
                    break; // Found next circuit or end of circuits
                }
                count++;
            }
        }
        return count;
    }

    /**
     * Add circuit with multiple resistance measurement rows for both left and right sides
     */
    private static void addCircuitWithResistanceRowsBothSides(XWPFTable table,
                                                              // Left side parameters
                                                              String leftCircuitName, String leftSize, String leftTestAmps,
                                                              String leftTripTime, String leftInstTrip, String[] leftResistanceValues,
                                                              // Right side parameters
                                                              String rightCircuitName, String rightSize, String rightTestAmps,
                                                              String rightTripTime, String rightInstTrip, String[] rightResistanceValues) {

        // Determine the maximum number of rows needed (based on the side with more resistance values)
        int numRows = Math.max(leftResistanceValues.length, rightResistanceValues.length);
        int startRowIndex = table.getNumberOfRows();

        // Create all rows for this circuit
        for (int i = 0; i < numRows; i++) {
            XWPFTableRow row = table.createRow();
            expandRowTo12Columns(row);

            // Left side (columns 0-5)
            if (i == 0) {
                // First row gets the circuit data
                setCellText(row.getCell(0), leftCircuitName, false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(1), leftSize, false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(2), leftTestAmps, false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(3), leftTripTime, false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(4), leftInstTrip, false, ParagraphAlignment.CENTER);
            } else {
                // Subsequent rows are empty for merged cells
                setCellText(row.getCell(0), "", false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(1), "", false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(2), "", false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(3), "", false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(4), "", false, ParagraphAlignment.CENTER);
            }

            // Left side contact resistance value for this row
            if (i < leftResistanceValues.length) {
                setCellText(row.getCell(5), leftResistanceValues[i], false, ParagraphAlignment.CENTER);
            } else {
                setCellText(row.getCell(5), "", false, ParagraphAlignment.CENTER);
            }

            // Right side (columns 6-11)
            if (i == 0) {
                // First row gets the circuit data
                setCellText(row.getCell(6), rightCircuitName, false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(7), rightSize, false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(8), rightTestAmps, false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(9), rightTripTime, false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(10), rightInstTrip, false, ParagraphAlignment.CENTER);
            } else {
                // Subsequent rows are empty for merged cells
                setCellText(row.getCell(6), "", false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(7), "", false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(8), "", false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(9), "", false, ParagraphAlignment.CENTER);
                setCellText(row.getCell(10), "", false, ParagraphAlignment.CENTER);
            }

            // Right side contact resistance value for this row
            if (i < rightResistanceValues.length) {
                setCellText(row.getCell(11), rightResistanceValues[i], false, ParagraphAlignment.CENTER);
            } else {
                setCellText(row.getCell(11), "", false, ParagraphAlignment.CENTER);
            }
        }

        // Apply vertical merging to the first 5 columns (left side circuit info)
        if (numRows > 1) {
            for (int col = 0; col < 5; col++) {
                mergeVerticalCells(table, startRowIndex, startRowIndex + numRows - 1, col);
            }
            // Apply vertical merging to columns 6-10 (right side circuit info)
            for (int col = 6; col < 11; col++) {
                mergeVerticalCells(table, startRowIndex, startRowIndex + numRows - 1, col);
            }
        }
    }

    /**
     * Add remarks section to the table
     */
    private static void addRemarksToTable(XWPFTable table) {
        // Remarks header
        XWPFTableRow remarksHeaderRow = table.createRow();
        expandRowTo12Columns(remarksHeaderRow);
        mergeCellsHorizontally(table, table.getNumberOfRows() - 1, 0, 11);
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
            expandRowTo12Columns(remarkRow);
            mergeCellsHorizontally(table, table.getNumberOfRows() - 1, 0, 11);
            boolean isBold = remark.contains("Novelty Energy Limited");
            setCellText(remarkRow.getCell(0), remark, isBold, ParagraphAlignment.LEFT);
        }
    }

    /**
     * Expand table row to 12 columns
     */
    private static void expandRowTo12Columns(XWPFTableRow row) {
        while (row.getTableCells().size() < 12) {
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
            CTTcPr tcPr = cell.getCTTc().getTcPr();
            if (tcPr == null) {
                tcPr = cell.getCTTc().addNewTcPr();
            }
            if (colIndex == fromCol) {
                tcPr.addNewHMerge().setVal(STMerge.RESTART);
            } else {
                tcPr.addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    /**
     * Merge cells vertically using POI
     */
    private static void mergeVerticalCells(XWPFTable table, int fromRow, int toRow, int col) {
        for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
            XWPFTableRow row = table.getRow(rowIndex);
            XWPFTableCell cell = row.getCell(col);

            CTTcPr tcPr = cell.getCTTc().getTcPr();
            if (tcPr == null) {
                tcPr = cell.getCTTc().addNewTcPr();
            }

            CTVMerge vMerge = tcPr.getVMerge();
            if (vMerge == null) {
                vMerge = tcPr.addNewVMerge();
            }

            if (rowIndex == fromRow) {
                vMerge.setVal(STMerge.RESTART);
            } else {
                vMerge.setVal(STMerge.CONTINUE);
            }
        }
    }

    /**
     * Set cell text with formatting
     */
    private static void setCellText(XWPFTableCell cell, String text, boolean bold, ParagraphAlignment alignment) {
        // Clear existing content
        cell.removeParagraph(0);

        XWPFParagraph para = cell.addParagraph();
        para.setAlignment(alignment);

        XWPFRun run = para.createRun();
        run.setText(text);
        run.setBold(bold);
        run.setFontFamily("Arial");
        run.setFontSize(9);

        // Set cell vertical alignment
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

        // Set cell margins for better spacing
        CTTcPr tcPr = cell.getCTTc().getTcPr();
        if (tcPr == null) {
            tcPr = cell.getCTTc().addNewTcPr();
        }

        CTTcMar tcMar = tcPr.getTcMar();
        if (tcMar == null) {
            tcMar = tcPr.addNewTcMar();
        }

        // Set small margins
        if (tcMar.getTop() == null) tcMar.addNewTop().setW(BigInteger.valueOf(50));
        if (tcMar.getBottom() == null) tcMar.addNewBottom().setW(BigInteger.valueOf(50));
        if (tcMar.getLeft() == null) tcMar.addNewLeft().setW(BigInteger.valueOf(50));
        if (tcMar.getRight() == null) tcMar.addNewRight().setW(BigInteger.valueOf(50));
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

    /**
     * Save document to ReportMateReports folder in public Documents
     */
    private static boolean saveDocument(Context context, XWPFDocument document, String reportName) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//            String fileName = reportName + "_" + timeStamp + ".docx";
            String fileName = reportName + ".docx";

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

    /**
     * Add images section to the document with proper table structure
     */
    private static void addImageSection(XWPFDocument document, String title, Report report, String[] imagePaths, String[] imageNames) {
        try {
            // Add section heading
            XWPFParagraph imageSectionPara = document.createParagraph();
            imageSectionPara.setAlignment(ParagraphAlignment.LEFT);
            imageSectionPara.setSpacingAfter(200);
            imageSectionPara.setSpacingBefore(200);
            XWPFRun imageSectionRun = imageSectionPara.createRun();
            imageSectionRun.setText( title);
            imageSectionRun.setBold(true);
            imageSectionRun.setFontFamily("Arial");
            imageSectionRun.setFontSize(12);
            imageSectionRun.setColor("00B050"); // Green color

            // Create table for images
            createImageTable(document, imagePaths, imageNames);

        } catch (Exception e) {
            Log.e(TAG, "Error adding image section", e);
        }
    }

    /**
     * Create table with images and captions
     */
    private static void createImageTable(XWPFDocument document, String[] imagePaths, String[] imageNames) {
        try {
            XWPFTable imageTable = document.createTable();
            setTableBorders(imageTable);
            setTableWidth(imageTable, 100);

            // Process images in pairs (2 images per row)
            for (int i = 0; i < imagePaths.length; i += 2) {
                // Create image row
                XWPFTableRow imageRow;
                if (i == 0) {
                    imageRow = imageTable.getRow(0); // Use existing first row
                } else {
                    imageRow = imageTable.createRow();
                }

                // Ensure we have 2 columns
                while (imageRow.getTableCells().size() < 2) {
                    imageRow.addNewTableCell();
                }

                // Add first image with smaller size
                addImageToCell2(imageRow.getCell(0), imagePaths[i], 250, 180);

                // Add second image (if exists) with smaller size
                if (i + 1 < imagePaths.length) {
                    addImageToCell2(imageRow.getCell(1), imagePaths[i + 1], 250, 180);
                } else {
                    // Empty cell if odd number of images
                    setCellText(imageRow.getCell(1), "", false, ParagraphAlignment.CENTER);
                }

                // Create caption row
                XWPFTableRow captionRow = imageTable.createRow();
                while (captionRow.getTableCells().size() < 2) {
                    captionRow.addNewTableCell();
                }

                // Add first caption
                setCellText(captionRow.getCell(0), imageNames[i], true, ParagraphAlignment.CENTER);

                // Add second caption (if exists)
                if (i + 1 < imageNames.length) {
                    setCellText(captionRow.getCell(1), imageNames[i + 1], true, ParagraphAlignment.CENTER);
                } else {
                    setCellText(captionRow.getCell(1), "", false, ParagraphAlignment.CENTER);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error creating image table", e);
        }
    }

    /**
     * Add image to a table cell
     */
    private static void addImageToCell(XWPFTableCell cell, String imagePath, int width, int height) {
        try {
            // Clear existing content
            cell.removeParagraph(0);

            XWPFParagraph para = cell.addParagraph();
            para.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = para.createRun();

            // Read image from public documents
            File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), imagePath);

            if (imageFile.exists()) {
                FileInputStream imageStream = new FileInputStream(imageFile);

                // Determine image format
                String fileName = imageFile.getName().toLowerCase();
                int pictureType;
                if (fileName.endsWith(".png")) {
                    pictureType = XWPFDocument.PICTURE_TYPE_PNG;
                } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    pictureType = XWPFDocument.PICTURE_TYPE_JPEG;
                } else {
                    pictureType = XWPFDocument.PICTURE_TYPE_JPEG; // Default
                }

                // Add picture to the run
                run.addPicture(imageStream, pictureType, imageFile.getName(),
                        Units.toEMU(width), Units.toEMU(height));

                imageStream.close();
            } else {
                // If image doesn't exist, add placeholder text
                run.setText("Image not found: " + imagePath);
                run.setFontFamily("Arial");
                run.setFontSize(10);
            }

            // Set cell properties
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            // Set cell properties for better image display
            CTTcPr tcPr = cell.getCTTc().getTcPr();
            if (tcPr == null) {
                tcPr = cell.getCTTc().addNewTcPr();
            }

            // Set cell margins for better spacing
            CTTcMar tcMar = tcPr.getTcMar();
            if (tcMar == null) {
                tcMar = tcPr.addNewTcMar();
            }

            // Set margins around the image
            if (tcMar.getTop() == null) tcMar.addNewTop().setW(BigInteger.valueOf(100));
            if (tcMar.getBottom() == null) tcMar.addNewBottom().setW(BigInteger.valueOf(100));
            if (tcMar.getLeft() == null) tcMar.addNewLeft().setW(BigInteger.valueOf(100));
            if (tcMar.getRight() == null) tcMar.addNewRight().setW(BigInteger.valueOf(100));

        } catch (Exception e) {
            Log.e(TAG, "Error adding image to cell: " + imagePath, e);
            // Add error text instead
            try {
                cell.removeParagraph(0);
                XWPFParagraph para = cell.addParagraph();
                para.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun run = para.createRun();
                run.setText("Error loading image: " + imagePath);
                run.setFontFamily("Arial");
                run.setFontSize(10);
            } catch (Exception ex) {
                Log.e(TAG, "Error adding error text", ex);
            }
        }
    }

    /**
     * Alternative method using MediaStore for Android 10+
     */
    private static void addImageToCellFromMediaStore(Context context, XWPFTableCell cell, String imageName, int width, int height) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use MediaStore to find the image
                ContentResolver resolver = context.getContentResolver();
                String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME};
                String selection = MediaStore.Images.Media.DISPLAY_NAME + "=?";
                String[] selectionArgs = {imageName};

                Cursor cursor = resolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null
                );

                if (cursor != null && cursor.moveToFirst()) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                    InputStream imageStream = resolver.openInputStream(imageUri);

                    if (imageStream != null) {
                        cell.removeParagraph(0);
                        XWPFParagraph para = cell.addParagraph();
                        para.setAlignment(ParagraphAlignment.CENTER);
                        XWPFRun run = para.createRun();

                        int pictureType = imageName.toLowerCase().endsWith(".png") ?
                                XWPFDocument.PICTURE_TYPE_PNG : XWPFDocument.PICTURE_TYPE_JPEG;

                        run.addPicture(imageStream, pictureType, imageName,
                                Units.toEMU(width), Units.toEMU(height));

                        imageStream.close();
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    }
                }

                if (cursor != null) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding image from MediaStore: " + imageName, e);
        }
    }


    private static void addImageToCell2(XWPFTableCell cell, String imagePath, int maxWidth, int maxHeight) {

        Utility.showLog("imagePath "+imagePath);
        try {
            // Clear existing content
            if (cell.getParagraphs().size() > 0) {
                cell.removeParagraph(0);
            }

            XWPFParagraph para = cell.addParagraph();
            para.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = para.createRun();

            // Read image from public documents
            File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), imagePath);

            if (imageFile.exists()) {
                // Process the image to fix orientation and resize
                byte[] processedImageBytes = processImage(imageFile, maxWidth, maxHeight);

                if (processedImageBytes != null) {
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(processedImageBytes);

                    // Determine image format
                    String fileName = imageFile.getName().toLowerCase();
                    int pictureType;
                    if (fileName.endsWith(".png")) {
                        pictureType = XWPFDocument.PICTURE_TYPE_PNG;
                    } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                        pictureType = XWPFDocument.PICTURE_TYPE_JPEG;
                    } else {
                        pictureType = XWPFDocument.PICTURE_TYPE_JPEG; // Default
                    }

                    // Get the actual dimensions after processing
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeByteArray(processedImageBytes, 0, processedImageBytes.length, options);

                    int finalWidth = options.outWidth;
                    int finalHeight = options.outHeight;

                    // Use the processed image dimensions for Word document
                    // Scale for display in document (keep higher resolution)
                    if (finalWidth > maxWidth || finalHeight > maxHeight) {
                        float scale = Math.min((float) maxWidth / finalWidth, (float) maxHeight / finalHeight);
                        finalWidth = Math.round(finalWidth * scale);
                        finalHeight = Math.round(finalHeight * scale);
                    }

                    // Add picture to the run with correct dimensions
                    run.addPicture(imageStream, pictureType, imageFile.getName(),
                            Units.toEMU(finalWidth), Units.toEMU(finalHeight));

                    imageStream.close();
                } else {
                    addErrorText(run, "Failed to process image: " + imagePath);
                }
            } else {
                addErrorText(run, "Image not found: " + imagePath);
            }

            // Set cell properties for better image display
            setCellProperties(cell);

        } catch (Exception e) {
            Log.e(TAG, "Error adding image to cell: " + imagePath, e);
            addErrorTextToCell(cell, "Error loading image: " + imagePath);
        }
    }

    /**
     * Process image to fix rotation and resize appropriately while maintaining high quality
     */
    private static byte[] processImage(File imageFile, int maxWidth, int maxHeight) {
        try {
            // Read EXIF data to get orientation
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            // Decode image with high quality settings
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

            // Use higher resolution target for better quality
            int targetWidth = maxWidth * 3; // 3x the display size for higher quality
            int targetHeight = maxHeight * 3;

            // Calculate sample size more conservatively for higher quality
            int sampleSize = calculateSampleSizeHighQuality(options.outWidth, options.outHeight, targetWidth, targetHeight);

            options.inSampleSize = sampleSize;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888; // Highest quality color format
            options.inDither = false;
            options.inScaled = false;

            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
            if (bitmap == null) {
                return null;
            }

            // Apply rotation based on EXIF orientation using high quality filtering
            bitmap = rotateImageIfNeeded(bitmap, orientation);

            // Resize if necessary while maintaining aspect ratio and high quality
            bitmap = resizeImageHighQuality(bitmap, targetWidth, targetHeight);

            // Convert to byte array with maximum quality
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Always use PNG for maximum quality in documents
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            bitmap.recycle();

            return outputStream.toByteArray();

        } catch (IOException e) {
            Log.e(TAG, "Error processing image: " + imageFile.getAbsolutePath(), e);
            return null;
        }
    }

    /**
     * Rotate image based on EXIF orientation
     */
    private static Bitmap rotateImageIfNeeded(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.preScale(-1.0f, 1.0f);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.preScale(1.0f, -1.0f);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.postRotate(90);
                matrix.preScale(-1.0f, 1.0f);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.postRotate(-90);
                matrix.preScale(-1.0f, 1.0f);
                break;
            default:
                return bitmap; // No rotation needed
        }

        try {
            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (rotated != bitmap) {
                bitmap.recycle();
            }
            return rotated;
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "Out of memory rotating image", e);
            return bitmap;
        }
    }

    /**
     * Resize image while maintaining aspect ratio with high quality filtering
     */
    private static Bitmap resizeImageHighQuality(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Calculate scale factor
        float scale = Math.min((float) maxWidth / width, (float) maxHeight / height);

        // Only scale down if the image is significantly larger
        if (scale >= 0.8f) { // Keep original size if only minor scaling needed
            return bitmap;
        }

        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);

        try {
            // Use createScaledBitmap with filter=true for high quality scaling
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
            if (resized != bitmap) {
                bitmap.recycle();
            }
            return resized;
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "Out of memory resizing image", e);
            return bitmap;
        }
    }

    /**
     * Calculate sample size for high quality loading (more conservative)
     */
    private static int calculateSampleSizeHighQuality(int width, int height, int reqWidth, int reqHeight) {
        int sampleSize = 1;

        // Only use sample size if image is much larger than required
        if (height > reqHeight * 2 || width > reqWidth * 2) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // More conservative sampling to maintain quality
            while ((halfHeight / sampleSize) >= reqHeight * 1.5 && (halfWidth / sampleSize) >= reqWidth * 1.5) {
                sampleSize *= 2;
            }
        }

        return sampleSize;
    }

    /**
     * Set cell properties for better image display
     */
    private static void setCellProperties(XWPFTableCell cell) {
        try {
            // Set cell vertical alignment
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            // Set cell properties for better image display
            CTTcPr tcPr = cell.getCTTc().getTcPr();
            if (tcPr == null) {
                tcPr = cell.getCTTc().addNewTcPr();
            }

            // Set cell margins for better spacing
            CTTcMar tcMar = tcPr.getTcMar();
            if (tcMar == null) {
                tcMar = tcPr.addNewTcMar();
            }

            // Set smaller margins for better image fit
            if (tcMar.getTop() == null) tcMar.addNewTop().setW(BigInteger.valueOf(50));
            if (tcMar.getBottom() == null) tcMar.addNewBottom().setW(BigInteger.valueOf(50));
            if (tcMar.getLeft() == null) tcMar.addNewLeft().setW(BigInteger.valueOf(50));
            if (tcMar.getRight() == null) tcMar.addNewRight().setW(BigInteger.valueOf(50));

        } catch (Exception e) {
            Log.e(TAG, "Error setting cell properties", e);
        }
    }

    /**
     * Add error text to run
     */
    private static void addErrorText(XWPFRun run, String message) {
        run.setText(message);
        run.setFontFamily("Arial");
        run.setFontSize(10);
        run.setColor("FF0000"); // Red color for errors
    }

    /**
     * Add error text to cell
     */
    private static void addErrorTextToCell(XWPFTableCell cell, String message) {
        try {
            if (cell.getParagraphs().size() > 0) {
                cell.removeParagraph(0);
            }
            XWPFParagraph para = cell.addParagraph();
            para.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = para.createRun();
            addErrorText(run, message);
        } catch (Exception ex) {
            Log.e(TAG, "Error adding error text to cell", ex);
        }
    }

// Usage example in your main report generation method:
/*
public static boolean generateReportWithImages(Context context, String reportName, Report report) {
    try {
        XWPFDocument document = new XWPFDocument();

        // Create document titles
        createDocumentTitles(document, report);

        // Create the main table
        createExactReportTable(document, report);

        // Add images section
        String[] imagePaths = {
            "ReportMateReports/temperature_humidity.jpg",
            "ReportMateReports/panel_exterior.jpg",
            "ReportMateReports/panel_interior.jpg",
            "ReportMateReports/additional_view.jpg"
        };

        String[] imageNames = {
            "Temperature & Humidity",
            "Panel",
            "Panel inside",
            "Additional View"
        };

        addImageSection(document, report, imagePaths, imageNames);

        // Save document
        return saveDocument(context, document, reportName);

    } catch (Exception e) {
        Log.e(TAG, "Error generating report with images", e);
        return false;
    }
}
*/

}