package com.hasanjaved.reportmate.utility;

import android.content.Context;
import android.os.Environment;

import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.model.Report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryManager {
    public static final String BASE_FOLDER_NAME = "ReportMate";
    private static final String REPORTMATE_DIRECTORY = "ReportMate";

    public static final String Pictures = "Pictures";

    public static final String GeneralPicture = "General Picture";

    //------------------------ DB BOX IMAGES NAME
    public static final String generalImageTemperature = "Temperature & Humidity";
    public static final String dbBoxPanelFront = "Panel Front Side";
    public static final String dbBoxPanelInside = "Panel Inside";
    public static final String dbBoxPanelNameplate = "Panel Nameplate";
    public static final String dbBoxPanelGrounding = "Panel Grounding";


    //------------------- ALL TEST NAME
    public static final String IrTest = "IR Test";
    public static final String TRIP_TEST = "Trip Test";
    public static final String CRM_TEST = "CRM Test";

    //--------------------------------------------------------
    public static final String IrTest1_AG = "1. A to G";
    public static final String IrTest2_BG = "2. B to G";
    public static final String IrTest3_CG = "3. C to G";
    public static final String IrTest4_AB = "4. A to B";
    public static final String IrTest5_BC = "5. B to C";
    public static final String IrTest6_CA = "6. C to A";


    // ------------------------------------------IR IMAGE NAME
    // ------------------------------------------AG
    public static final String imgAgConnection = "IR Test Connection (A-G)";
    public static final String imgAgResult = "IR Test Result (A-G)";

    //------------------------------------------BG
    public static final String imgBgConnection = "IR Test Connection (B-G)";
    public static final String imgBgResult = "IR Test Result (B-G)";

    //------------------------------------------CG
    public static final String imgCgConnection = "IR Test Connection (C-G)";
    public static final String imgCgResult = "IR Test Result (C-G)";


    // ------------------------------------------AB
    public static final String imgAbConnection = "IR Test Connection (A-B)";
    public static final String imgAbResult = "IR Test Result (A-B)";

    // ------------------------------------------BC
    public static final String imgBcConnection = "IR Test Connection (B-C)";
    public static final String imgBcResult = "IR Test Result (B-C)";
    // ------------------------------------------CA
    public static final String imgCaConnection = "IR Test Connection (C-A)";
    public static final String imgCaResult = "IR Test Result (C-A)";


    //-------------------------
    public static final String imgInjectorCurrent = "imgInjectorCurrent";
    public static final String imgInjectedCurrent = "imgInjectedCurrent";

    //----------------------------
    public static final String imgTripTime = "imgTripTime";
    public static final String imgAfterTripTime = "imgAfterTripTime";

    //-----------------------------------------------
    public static final String imgCrmConnection = "imgCrmConnection";
    public static final String imgCrmResult = "imgCrmResult";


    public static void createBaseFolder(Context context, String baseFolderName) {
        DirectoryCreator.createFolderInDocuments(context, BASE_FOLDER_NAME);
    }

    public static void createProjectEquipmentFolders(Context context, String equipmentName) {
        DirectoryCreator.createSubFolderInDocuments(context, BASE_FOLDER_NAME, equipmentName);
        createIrFolder(context, equipmentName);// ir folder inside
        createGeneralImageFolder(context, equipmentName);// general pic folder inside
    }

    public static void createGeneralImageFolder(Context context, String equipmentName) {
        DirectoryCreator.createFolderInDocuments(context, BASE_FOLDER_NAME + "/" + equipmentName + "/" + GeneralPicture);
    }

    public static void createIrFolder(Context context, String equipmentName) {
        // ir subfolders
        DirectoryCreator.createFolderInDocuments(context, BASE_FOLDER_NAME + "/" + equipmentName + "/" + IrTest + "/" + IrTest1_AG + "/" + Pictures);
        DirectoryCreator.createFolderInDocuments(context, BASE_FOLDER_NAME + "/" + equipmentName + "/" + IrTest + "/" + IrTest2_BG + "/" + Pictures);
        DirectoryCreator.createFolderInDocuments(context, BASE_FOLDER_NAME + "/" + equipmentName + "/" + IrTest + "/" + IrTest3_CG + "/" + Pictures);
        DirectoryCreator.createFolderInDocuments(context, BASE_FOLDER_NAME + "/" + equipmentName + "/" + IrTest + "/" + IrTest4_AB + "/" + Pictures);
        DirectoryCreator.createFolderInDocuments(context, BASE_FOLDER_NAME + "/" + equipmentName + "/" + IrTest + "/" + IrTest5_BC + "/" + Pictures);
        DirectoryCreator.createFolderInDocuments(context, BASE_FOLDER_NAME + "/" + equipmentName + "/" + IrTest + "/" + IrTest6_CA + "/" + Pictures);

    }

    public static void createCircuitFolders(Context context, List<CircuitBreaker> circuitBreakerList) {
        Report report = Utility.getReport(context);
        if (report != null)
            if (report.getProjectNo() != null)
                if (report.getEquipment().getEquipmentName() != null) {
                    for (CircuitBreaker circuitBreaker : circuitBreakerList) {
//                        MediaStoreUtils.createSubFolderInDocuments(context,
//                                Utility.BASE_FOLDER_NAME+"/"+report.getEquipment().getEquipmentName(),
//                                circuitBreaker.getName());
                        createTripAndCrmFolder(context,
                                Utility.BASE_FOLDER_NAME + "/" + report.getEquipment().getEquipmentName() + "/" + circuitBreaker.getName(),
                                circuitBreaker.getName());
                    }
                }

    }

    private static void createTripAndCrmFolder(Context context, String subDirectory, String circuitName) {
        DirectoryCreator.createSubFolderInDocuments(context, subDirectory, circuitName + " " + TRIP_TEST + "/" + Pictures);
        DirectoryCreator.createSubFolderInDocuments(context, subDirectory, circuitName + " " + CRM_TEST + "/" + Pictures);
    }


//    public static String getReportDirectory(Context context) {
//        return Utility.BASE_FOLDER_NAME + "/" + Objects.requireNonNull(Utility.getReport(context)).getProjectNo();
//    }

    public static String getGeneralImageDirectory(String equipmentName) {
//        return Utility.BASE_FOLDER_NAME + "/" + Objects.requireNonNull(Utility.getReport(context)).getProjectNo();
        return BASE_FOLDER_NAME + "/" + equipmentName + "/" + GeneralPicture;
    }

    public static String getTemperatureImage(String equipmentName) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + equipmentName + "/" + GeneralPicture);
        File imageFile = new File(reportMateDir, generalImageTemperature + ".jpg");
        return imageFile.getAbsolutePath();
    }

    public static String getPanelImage(String equipmentName) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + equipmentName + "/" + GeneralPicture);
        File imageFile = new File(reportMateDir, dbBoxPanelFront + ".jpg");
        return imageFile.getAbsolutePath();
    }

    public static String getDbBoxCircuitImage(String equipmentName) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + equipmentName + "/" + GeneralPicture);
        File imageFile = new File(reportMateDir, dbBoxPanelInside + ".jpg");
        return imageFile.getAbsolutePath();
    }


    public static String getIrFolderLinkAG(Report report) {
        return BASE_FOLDER_NAME + "/" + report.getEquipment().getEquipmentName() + "/" + IrTest + "/" + IrTest1_AG + "/" + "Pictures";
    }

    public static String getIrFolderLinkBG(Report report) {
        return BASE_FOLDER_NAME + "/" + report.getEquipment().getEquipmentName() + "/" + IrTest + "/" + IrTest2_BG + "/" + "Pictures";

    }

    public static String getIrFolderLinkCG(Report report) {
        return BASE_FOLDER_NAME + "/" + report.getEquipment().getEquipmentName() + "/" + IrTest + "/" + IrTest3_CG + "/" + "Pictures";
    }

    public static String getIrFolderLinkAB(Report report) {
        return BASE_FOLDER_NAME + "/" + report.getEquipment().getEquipmentName() + "/" + IrTest + "/" + IrTest3_CG + "/" + "Pictures";
    }

    public static String getIrFolderLinkBC(Report report) {
        return BASE_FOLDER_NAME + "/" + report.getEquipment().getEquipmentName() + "/" + IrTest + "/" + IrTest3_CG + "/" + "Pictures";
    }

    public static String getIrFolderLinkCA(Report report) {
        return BASE_FOLDER_NAME + "/" + report.getEquipment().getEquipmentName() + "/" + IrTest + "/" + IrTest3_CG + "/" + "Pictures";
    }


    public static String getCrmFolderLink(Report report, CircuitBreaker circuitBreaker) {
        return BASE_FOLDER_NAME + "/" + report.getEquipment().getEquipmentName() + "/" + circuitBreaker.getName() + "/" + CRM_TEST;
    }


    public static String getTripFolderLink(Report report, CircuitBreaker circuitBreaker) {
        return BASE_FOLDER_NAME + "/" + report.getProjectNo() + "/" + report.getEquipment().getEquipmentName() + "/" + circuitBreaker.getName() + "/" + TRIP_TEST;
    }


//    public static List<String> getIrGroundConnectionImages(Context context) {
//        List<String> imageList = new ArrayList<>();
//        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
//        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + Utility.getReport(context).getProjectNo() + "/" + IrTest);
//
//        imageList.add(new File(reportMateDir, imgAgConnection + ".jpg").getAbsolutePath());
//        imageList.add(new File(reportMateDir, imgBgConnection + ".jpg").getAbsolutePath());
//        imageList.add(new File(reportMateDir, imgCgConnection + ".jpg").getAbsolutePath());
//
//        return imageList;
//    }



    //================================================================== GET IR IMAGES

    //----------------------------------------------------------------------------------LINE TO  GROUND
    public static String getIrAbConnectionImages(String equipmentName) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + equipmentName + "/" + IrTest + "/" + IrTest4_AB + "/" + Pictures);
        return new File(reportMateDir, imgAbConnection + ".jpg").getAbsolutePath();
    }
    public static String getIrAbResultImages(String equipmentName) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + equipmentName + "/" + IrTest + "/" + IrTest4_AB + "/" + Pictures);
        return new File(reportMateDir, imgAbResult + ".jpg").getAbsolutePath();
    }

    public static String getIrBcConnectionImages(String equipmentName) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + equipmentName + "/" + IrTest + "/" + IrTest5_BC + "/" + Pictures);
        return new File(reportMateDir, imgBcConnection + ".jpg").getAbsolutePath();
    }

    public static String getIrBcResultImages(String equipmentName) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + equipmentName + "/" + IrTest + "/" + IrTest5_BC + "/" + Pictures);
        return new File(reportMateDir, imgBcResult + ".jpg").getAbsolutePath();
    }

    public static String getIrCaConnectionImages(String equipmentName) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + equipmentName + "/" + IrTest + "/" + IrTest6_CA + "/" + Pictures);
        return new File(reportMateDir, imgCaConnection + ".jpg").getAbsolutePath();
    }

    public static String getIrCaResultImages(String equipmentName) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + equipmentName + "/" + IrTest + "/" + IrTest6_CA + "/" + Pictures);
        return new File(reportMateDir, imgCaResult + ".jpg").getAbsolutePath();
    }
    //-------------------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------- LINE TO LINE
    public static String getIrAgConnectionImages(String equipmentName) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + equipmentName + "/" + IrTest + "/" + IrTest1_AG + "/" + Pictures);
        Utility.showLog( "getIrAgConnectionImages"+new File(reportMateDir, imgAgConnection + ".jpg").getAbsolutePath());
        return new File(reportMateDir, imgAgConnection + ".jpg").getAbsolutePath();
    }
    public static String getIrAgResultImages(String equipmentName) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + equipmentName + "/" + IrTest + "/" + IrTest1_AG + "/" + Pictures);
        return new File(reportMateDir, imgAgResult + ".jpg").getAbsolutePath();
    }

    public static String getIrBgConnectionImages(String equipmentName) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + equipmentName + "/" + IrTest + "/" + IrTest2_BG + "/" + Pictures);
        return new File(reportMateDir, imgBgConnection + ".jpg").getAbsolutePath();
    }

    public static String getIrBgResultImages(String equipmentName) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + equipmentName + "/" + IrTest + "/" + IrTest2_BG + "/" + Pictures);
        return new File(reportMateDir, imgBgResult + ".jpg").getAbsolutePath();
    }

    public static String getIrCgConnectionImages(String equipmentName) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + equipmentName + "/" + IrTest + "/" + IrTest3_CG + "/" + Pictures);
        return new File(reportMateDir, imgCgConnection + ".jpg").getAbsolutePath();
    }

    public static String getIrCgResultImages(String equipmentName) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + equipmentName + "/" + IrTest + "/" + IrTest3_CG + "/" + Pictures);
        return new File(reportMateDir, imgCgResult + ".jpg").getAbsolutePath();
    }
    //-------------------------------------------------------------------------------------------------


//    public static List<String> getIrGroundConnectionImages(Context context, Report report) {
//        List<String> imageList = new ArrayList<>();
//        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
//        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + report.getProjectNo() + "/" + IrTest);
//
////        if (isFileAvailable(new File(reportMateDir, imgAgConnection + ".jpg").getAbsolutePath()))
//        imageList.add(new File(reportMateDir, imgAgConnection + ".jpg").getAbsolutePath());
//
////        if (isFileAvailable(new File(reportMateDir, imgBgConnection + ".jpg").getAbsolutePath()))
//        imageList.add(new File(reportMateDir, imgBgConnection + ".jpg").getAbsolutePath());
//
////        if (isFileAvailable(new File(reportMateDir, imgCgConnection + ".jpg").getAbsolutePath()))
//        imageList.add(new File(reportMateDir, imgCgConnection + ".jpg").getAbsolutePath());
//
//        return imageList;
//    }

//    public static List<String> getIrGroundResultImages(Context context) {
//        List<String> imageList = new ArrayList<>();
//        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
//        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + Utility.getReport(context).getProjectNo() + "/" + IrTest);
//
//        imageList.add(new File(reportMateDir, imgAgResult + ".jpg").getAbsolutePath());
//        imageList.add(new File(reportMateDir, imgBgResult + ".jpg").getAbsolutePath());
//        imageList.add(new File(reportMateDir, imgCgResult + ".jpg").getAbsolutePath());
//
//        return imageList;
//    }


//    public static List<String> getIrLineConnectionImages(Context context) {
//        List<String> imageList = new ArrayList<>();
//        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
//        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + Utility.getReport(context).getProjectNo() + "/" + IrTest);
//
//        imageList.add(new File(reportMateDir, imgAbConnection + ".jpg").getAbsolutePath());
//        imageList.add(new File(reportMateDir, imgBcConnection + ".jpg").getAbsolutePath());
//        imageList.add(new File(reportMateDir, imgCaConnection + ".jpg").getAbsolutePath());
//
//        return imageList;
//    }

//    public static List<String> getIrLineResultImages(Context context) {
//        List<String> imageList = new ArrayList<>();
//        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
//        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + Utility.getReport(context).getProjectNo() + "/" + IrTest);
//
//        imageList.add(new File(reportMateDir, imgAbResult + ".jpg").getAbsolutePath());
//        imageList.add(new File(reportMateDir, imgBcResult + ".jpg").getAbsolutePath());
//        imageList.add(new File(reportMateDir, imgCaResult + ".jpg").getAbsolutePath());
//
//        return imageList;
//    }

    public static List<String> getCrmImage(Context context, String circuitName) {
        List<String> imageList = new ArrayList<>();

        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + Utility.getReport(context).getProjectNo() + "/"
                + Utility.getReport(context).getEquipment().getEquipmentName() + "/" + circuitName + "/" + CRM_TEST);

        imageList.add(new File(reportMateDir, imgCrmConnection + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgCrmResult + ".jpg").getAbsolutePath());

        return imageList;
    }

    public static List<String> getCrmImageForReport(Context context, String circuitName) {
        List<String> imageList = new ArrayList<>();

        File reportMateDir = new File(REPORTMATE_DIRECTORY + "/" + Utility.getReport(context).getProjectNo() + "/"
                + Utility.getReport(context).getEquipment().getEquipmentName() + "/" + circuitName + "/" + CRM_TEST);

        imageList.add(new File(reportMateDir, imgCrmConnection + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgCrmResult + ".jpg").getAbsolutePath());

        return imageList;
    }

    public static List<String> getTripImage(Context context, String circuitName) {
        List<String> imageList = new ArrayList<>();

        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + Utility.getReport(context).getProjectNo() + "/"
                + Utility.getReport(context).getEquipment().getEquipmentName() + "/" + circuitName + "/" + TRIP_TEST);

        imageList.add(new File(reportMateDir, imgInjectorCurrent + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgInjectedCurrent + ".jpg").getAbsolutePath());
//        imageList.add(new File(reportMateDir, imgTripTimeConnection + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgTripTime + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgAfterTripTime + ".jpg").getAbsolutePath());

        return imageList;
    }

    public static List<String> getTripImageForReport(Context context, String circuitName) {
        List<String> imageList = new ArrayList<>();

        File reportMateDir = new File(REPORTMATE_DIRECTORY + "/" + Utility.getReport(context).getProjectNo() + "/"
                + Utility.getReport(context).getEquipment().getEquipmentName() + "/" + circuitName + "/" + TRIP_TEST);

        imageList.add(new File(reportMateDir, imgInjectorCurrent + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgInjectedCurrent + ".jpg").getAbsolutePath());
//        imageList.add(new File(reportMateDir, imgTripTimeConnection + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgTripTime + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgAfterTripTime + ".jpg").getAbsolutePath());


        return imageList;
    }

}
