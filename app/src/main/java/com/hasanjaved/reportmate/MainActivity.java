package com.hasanjaved.reportmate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.hasanjaved.reportmate.doc_generator.PanelOnlyGenerator;
import com.hasanjaved.reportmate.doc_generator.PanelPicturesGenerator;
import com.hasanjaved.reportmate.utility.Utility;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
//    private static final String[] REQUIRED_PERMISSIONS = {
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        PanelPicturesGenerator.generatePanelGeneralPicturesReport(this,Utility.IMAGE_SAMPLE_DIRECTORY);

        Button generateButton = findViewById(R.id.btnGenerate);
        generateButton.setOnClickListener(v -> {
            if (checkPermissions()) {
//                generatePanelPicturesOnly();
                generateDocxReport();
            } else {
                requestPermissions();
            }
        });
    }

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

    private boolean checkPermissions() {
//        for (String permission : REQUIRED_PERMISSIONS) {
//            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//                return false;
//            }
//        }
        return true;
    }

    private void requestPermissions() {
//        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
    }

    private void generateDocxReport() {
        try {
            XWPFDocument document = new XWPFDocument();

            // Create title
            createTitle(document);

            // Create header information section
            createHeaderSection(document);

            // Create insulation resistance table
            createInsulationResistanceTable(document);

            // Create circuit breaker test table
            createCircuitBreakerTestTable(document);

            // Create remarks section
            createRemarksSection(document);

            // Create images section
            createImagesSection(document);

            // Save document
            saveDocument(document);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating document: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void createTitle(XWPFDocument document) {
        XWPFParagraph titlePara = document.createParagraph();
        titlePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText(Utility.getReport(this).getEquipment().getEquipmentName()+" MCCB & MCB: Test and Inspection Report");
        titleRun.setBold(true);
        titleRun.setFontSize(12);
        titleRun.setColor("008000"); // Green color
    }

    private void createHeaderSection(XWPFDocument document) {
        // Main header table
        XWPFTable headerTable = document.createTable(4, 4);
        setTableBorders(headerTable);

        // Set column widths
        setTableWidth(headerTable, 100);

        // Row 1: Panel Board Circuit Breaker Test Report
        XWPFTableRow row1 = headerTable.getRow(0);
        mergeHorizontally(headerTable, 0, 0, 3);
        setCellText(row1.getCell(0), "PpppANEL BOARD CIRCUIT BREAKER TEST REPORT", true, true);

        // Row 2: Sheet info and page info
        XWPFTableRow row2 = headerTable.getRow(1);
        setCellText(row2.getCell(0), "SHEET NO:", true, false);
        setCellText(row2.getCell(1), "", false, false);
        setCellText(row2.getCell(2), "of", false, true);
        setCellText(row2.getCell(3), "", false, false);

        // Row 3: Customer and Date/Project info
        XWPFTableRow row3 = headerTable.getRow(2);
        setCellText(row3.getCell(0), "CUSTOMER: New Era Fashions Mfrs.(BD)Ltd", false, false);
        setCellText(row3.getCell(1), "DATE: 9.02.2024", false, false);
        setCellText(row3.getCell(2), "PROJECT NO: NEL-SWGRTS-1712220048-1", false, false);
        setCellText(row3.getCell(3), "", false, false);

        // Row 4: Address and Air temp/Rel humidity
        XWPFTableRow row4 = headerTable.getRow(3);
        setCellText(row4.getCell(0), "ADDRESS: Plot No 10 – 12(Part), Sector -8, CEPZ, Chattogram.", false, false);
        setCellText(row4.getCell(1), "AIR TEMP: 27 °C", false, false);
        setCellText(row4.getCell(2), "REL HUMIDITY: 49%", false, false);
        setCellText(row4.getCell(3), "", false, false);

        document.createParagraph(); // Add space

        // Additional info table
        XWPFTable infoTable = document.createTable(3, 2);
        setTableBorders(infoTable);
        setTableWidth(infoTable, 100);

        XWPFTableRow infoRow1 = infoTable.getRow(0);
        setCellText(infoRow1.getCell(0), "OWNER/ USER: New Era Fashions Mfrs.(BD)Ltd", false, false);
        setCellText(infoRow1.getCell(1), "DATE LAST INSPECTION: 17-2-2025", false, false);

        XWPFTableRow infoRow2 = infoTable.getRow(1);
        setCellText(infoRow2.getCell(0), "ADDRESS: Plot No 10 – 12(Part), Sector -8, CEPZ, Chattogram.", false, false);
        setCellText(infoRow2.getCell(1), "LAST INSPECTION REPORT NO: NEL-SWGR280275", false, false);

        XWPFTableRow infoRow3 = infoTable.getRow(2);
        setCellText(infoRow3.getCell(0), "EQUIPMENT LOCATION: 2nd Floor", false, false);
        setCellText(infoRow3.getCell(1), "", false, false);

        document.createParagraph(); // Add space
    }

    private void createInsulationResistanceTable(XWPFDocument document) {
        // Insulation Resistance Header
        XWPFParagraph insulationHeader = document.createParagraph();
        insulationHeader.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun insulationRun = insulationHeader.createRun();
        insulationRun.setText("PANEL BUS INSULATION RESISTANCE IN MEGAOHMS");
        insulationRun.setBold(true);

        XWPFTable insulationTable = document.createTable(4, 8);
        setTableBorders(insulationTable);
        setTableWidth(insulationTable, 100);

        // Header row
        XWPFTableRow headerRow = insulationTable.getRow(0);
        setCellText(headerRow.getCell(0), "A-G: >4000", false, true);
        setCellText(headerRow.getCell(1), "B-G: >4000", false, true);
        setCellText(headerRow.getCell(2), "C-G: >4000", false, true);
        setCellText(headerRow.getCell(3), "A-B: >4000", false, true);
        setCellText(headerRow.getCell(4), "B-C: >4000", false, true);
        setCellText(headerRow.getCell(5), "C-A: >4000", false, true);
        setCellText(headerRow.getCell(6), "", false, true);
        setCellText(headerRow.getCell(7), "", false, true);

        // Panelboard Rating row
        XWPFTableRow ratingRow = insulationTable.getRow(1);
        setCellText(ratingRow.getCell(0), "PANELBOARD Rating", false, false);
        setCellText(ratingRow.getCell(1), "AMPS: 310 A", false, false);
        setCellText(ratingRow.getCell(2), "VOLTAGE: 415 V", false, false);
        setCellText(ratingRow.getCell(3), "", false, false);
        setCellText(ratingRow.getCell(4), "", false, false);
        setCellText(ratingRow.getCell(5), "", false, false);
        setCellText(ratingRow.getCell(6), "", false, false);
        setCellText(ratingRow.getCell(7), "", false, false);

        // Test voltage and model info
        XWPFTableRow testRow = insulationTable.getRow(2);
        setCellText(testRow.getCell(0), "TEST VOLTAGE: 1KV", false, false);
        setCellText(testRow.getCell(1), "MODEL NO.: NOT AVAILABLE", false, false);
        setCellText(testRow.getCell(2), "CATALOG: NOT AVAILABLE", false, false);
        setCellText(testRow.getCell(3), "", false, false);
        setCellText(testRow.getCell(4), "", false, false);
        setCellText(testRow.getCell(5), "", false, false);
        setCellText(testRow.getCell(6), "", false, false);
        setCellText(testRow.getCell(7), "", false, false);

        // MFG info
        XWPFTableRow mfgRow = insulationTable.getRow(3);
        setCellText(mfgRow.getCell(0), "MFG.:", false, false);
        setCellText(mfgRow.getCell(1), "CURVE NO.: Type -C", false, false);
        setCellText(mfgRow.getCell(2), "CURVE RANGE: 1.5 to 10 Times of Full Load Current", false, false);
        setCellText(mfgRow.getCell(3), "MFG.:", false, false);
        setCellText(mfgRow.getCell(4), "CURVE NO.: Type -B", false, false);
        setCellText(mfgRow.getCell(5), "CURVE RANGE: 2 to 16 Times of Full Load Current", false, false);
        setCellText(mfgRow.getCell(6), "MFG.:", false, false);
        setCellText(mfgRow.getCell(7), "CURVE NO.", false, false);

        document.createParagraph(); // Add space
    }

    private void createCircuitBreakerTestTable(XWPFDocument document) {
        XWPFTable circuitTable = document.createTable(8, 11);
        setTableBorders(circuitTable);
        setTableWidth(circuitTable, 100);

        // Header row
        XWPFTableRow headerRow = circuitTable.getRow(0);
        setCellText(headerRow.getCell(0), "CIRCUIT #", false, true);
        setCellText(headerRow.getCell(1), "CKT. BKR. SIZE", false, true);
        setCellText(headerRow.getCell(2), "TEST AMPS", false, true);
        setCellText(headerRow.getCell(3), "TRIP TIME", false, true);
        setCellText(headerRow.getCell(4), "INST TRIP", false, true);
        setCellText(headerRow.getCell(5), "CONTACT RESIS", false, true);
        setCellText(headerRow.getCell(6), "CIRCUIT #", false, true);
        setCellText(headerRow.getCell(7), "CKT. BKR. SIZE", false, true);
        setCellText(headerRow.getCell(8), "TEST AMPS", false, true);
        setCellText(headerRow.getCell(9), "TRIP TIME", false, true);
        setCellText(headerRow.getCell(10), "INST TRIP", false, true);

        // CKT-0 row
        XWPFTableRow ckt0Row = circuitTable.getRow(1);
        setCellText(ckt0Row.getCell(0), "CKT-0", false, false);
        setCellText(ckt0Row.getCell(1), "200 A", false, false);
        setCellText(ckt0Row.getCell(2), "600 A", false, false);
        setCellText(ckt0Row.getCell(3), "22s", false, false);
        setCellText(ckt0Row.getCell(4), "OK", false, false);
        setCellText(ckt0Row.getCell(5), "3.40 mΩ\n2.95 mΩ\n4.15 mΩ", false, false);
        setCellText(ckt0Row.getCell(6), "", false, false);
        setCellText(ckt0Row.getCell(7), "", false, false);
        setCellText(ckt0Row.getCell(8), "", false, false);
        setCellText(ckt0Row.getCell(9), "", false, false);
        setCellText(ckt0Row.getCell(10), "", false, false);

        // Continue with other circuits...
        String[][] circuitData = {
                {"CKT-1", "80 A", "243 A", "48s", "OK", "3.86 mΩ\n3.95 mΩ\n3.79 mΩ"},
                {"CKT-2", "80 A", "242 A", "54 s", "OK", "3.53 mΩ\n4.29 mΩ\n3.23 mΩ"},
                {"CKT-3", "80 A", "244 A", "33s", "OK", "3.23 mΩ\n2.85 mΩ\n2.29 mΩ"},
                {"CKT-4", "63 A", "189s", "28 s", "OK", "3.43 mΩ\n3.90 mΩ\n2.89 mΩ"},
                {"CKT-5", "80 A", "237 s", "51 s", "OK", "3.83 mΩ\n3.04 mΩ\n3.17 mΩ"},
                {"CKT-6", "80 A", "243 s", "46 s", "OK", "3.32 mΩ\n2.65 mΩ"}
        };

        for (int i = 0; i < circuitData.length; i++) {
            XWPFTableRow row = circuitTable.getRow(i + 2);
            for (int j = 0; j < 6; j++) {
                setCellText(row.getCell(j), circuitData[i][j], false, false);
            }
            // Empty cells for right side
            for (int j = 6; j < 11; j++) {
                setCellText(row.getCell(j), "", false, false);
            }
        }

        document.createParagraph(); // Add space
    }

    private void createRemarksSection(XWPFDocument document) {
        XWPFParagraph remarksHeader = document.createParagraph();
        XWPFRun remarksRun = remarksHeader.createRun();
        remarksRun.setText("Remarks:");
        remarksRun.setBold(true);

        String[] remarks = {
                "1. IR test values are Found satisfactory According to ANSI/NETA (7.6.1.1.3.2.2)",
                "2. CBM test values are Found satisfactory According to ANSI/NETA( 7.6.1.1.3.2.3)",
                "3. Trip test values are Found satisfactory According to NFPA 70B Table (11.10.5.2.5)"
        };

        for (String remark : remarks) {
            XWPFParagraph remarkPara = document.createParagraph();
            XWPFRun remarkRun = remarkPara.createRun();
            remarkRun.setText(remark);
        }

        // Equipment used section
        XWPFParagraph equipmentPara = document.createParagraph();
        XWPFRun equipmentRun = equipmentPara.createRun();
        equipmentRun.setText("Equipment Used: IR Tester (HIOKI IR 4056 ), HISAC Swift, Current Injector.");

        // Submitted by section
        XWPFParagraph submittedPara = document.createParagraph();
        XWPFRun submittedRun = submittedPara.createRun();
        submittedRun.setText("Submitted By: Novelty Energy Limited");

        // NFPA code
        XWPFParagraph nfpaPara = document.createParagraph();
        nfpaPara.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun nfpaRun = nfpaPara.createRun();
        nfpaRun.setText("NFPA 70B");

        document.createParagraph(); // Add space
    }

    private void createImagesSection(XWPFDocument document) {
        XWPFParagraph imagesHeader = document.createParagraph();
        XWPFRun imagesRun = imagesHeader.createRun();
        imagesRun.setText("Test Images and Documentation");
        imagesRun.setBold(true);
        imagesRun.setFontSize(14);

        document.createParagraph(); // Add space

        // Create placeholders for 15 images
        for (int i = 1; i <= 15; i++) {
            XWPFParagraph imagePara = document.createParagraph();
            XWPFRun imageRun = imagePara.createRun();
            imageRun.setText("Image " + i + ": [Test Photo/Documentation " + i + "]");
            imageRun.setBold(true);

            // Add space for actual image
            XWPFParagraph spacePara = document.createParagraph();
            XWPFRun spaceRun = spacePara.createRun();
            spaceRun.setText("[IMAGE PLACEHOLDER - Size: 400x300px]");
            spaceRun.setColor("808080");

            // Add description line
            XWPFParagraph descPara = document.createParagraph();
            XWPFRun descRun = descPara.createRun();
            descRun.setText("Description: Test procedure step " + i);

            document.createParagraph(); // Add space between images
        }
    }

    // Helper methods
    private void setCellText(XWPFTableCell cell, String text, boolean bold, boolean center) {
        XWPFParagraph para = cell.getParagraphs().get(0);
        if (center) {
            para.setAlignment(ParagraphAlignment.CENTER);
        }
        XWPFRun run = para.createRun();
        run.setText(text);
        run.setBold(bold);
        run.setFontSize(9);
    }

    private void setTableBorders(XWPFTable table) {
        table.getCTTbl().getTblPr().addNewTblBorders();
        CTTblBorders borders = table.getCTTbl().getTblPr().getTblBorders();
        borders.addNewTop().setVal(STBorder.SINGLE);
        borders.addNewLeft().setVal(STBorder.SINGLE);
        borders.addNewRight().setVal(STBorder.SINGLE);
        borders.addNewBottom().setVal(STBorder.SINGLE);
        borders.addNewInsideH().setVal(STBorder.SINGLE);
        borders.addNewInsideV().setVal(STBorder.SINGLE);
    }

    private void setTableWidth(XWPFTable table, int widthPercent) {
        CTTblWidth tblWidth = table.getCTTbl().getTblPr().addNewTblW();
        tblWidth.setType(STTblWidth.PCT);
        tblWidth.setW(BigInteger.valueOf(widthPercent * 50));
    }

    private void mergeHorizontally(XWPFTable table, int row, int fromCol, int toCol) {
        XWPFTableRow tableRow = table.getRow(row);
        for (int i = fromCol; i <= toCol; i++) {
            XWPFTableCell cell = tableRow.getCell(i);
            if (i == fromCol) {
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
            } else {
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    private void saveDocument(XWPFDocument document) {
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File docFile = new File(downloadsDir, "jPanel_Board_Test_Report.docx");

            FileOutputStream out = new FileOutputStream(docFile);
            document.write(out);
            out.close();
            document.close();

            Toast.makeText(this, "Document saved to: " + docFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Utility.showLog(e.getMessage());
            Toast.makeText(this, "Error saving document: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                generateDocxReport();
            } else {
                Toast.makeText(this, "Permissions required to generate document", Toast.LENGTH_SHORT).show();
            }
        }
    }


}