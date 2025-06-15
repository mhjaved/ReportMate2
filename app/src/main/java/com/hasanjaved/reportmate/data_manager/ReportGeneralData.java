package com.hasanjaved.reportmate.data_manager;

import android.content.Context;

import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.model.Equipment;
import com.hasanjaved.reportmate.model.ManufacturerCurveDetails;
import com.hasanjaved.reportmate.model.PanelBoard;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.utility.MediaStoreUtils;
import com.hasanjaved.reportmate.utility.Utility;

import java.util.List;

public class ReportGeneralData {

    public static void savePageOneData(Context context,String employeeId, String testDate, String projectNumber) {

        Utility.createProjectFolders(context,projectNumber);
//        if ()
//        String folderLocation = FolderManager.createFolder(context,projectNumber);

//        String baseFolderLink = FolderManager.getLinkIfFolderExist(context,Utility.BASE_FOLDER_NAME);
//        String projectLink = null;
//
//        if (baseFolderLink==null)
//            baseFolderLink = FolderManager.createBaseFolder(context);
//        if (baseFolderLink!=null){
//            projectLink = FolderManager.checkFolderInDirectory(context,baseFolderLink,projectNumber);
//            if (projectLink!=null){
//                Utility.showLog(projectLink);
//            }else {
//                projectLink =  FolderManager.createFolderInDirectory(context,baseFolderLink,projectNumber);
//            }
//        }

        Report report =  Utility.getReport(context);

        if (report==null)
            report = new Report();

        report.setEmployeeId(employeeId);
        report.setTestDate(testDate);
        report.setProjectNo(projectNumber);
        report.setFolderLocation("");

        Utility.saveReport(context,report);

    }

    public static void savePageTwoData(Context context,String customerName, String customerAddress, String userName,String userAddress) {
        Report report = Utility.getReport(context);

        if (report!=null){
            report.setCustomerName(customerName);
            report.setCustomerAddress(customerAddress);
            report.setUserName(userName);
            report.setUserAddress(userAddress);

            Utility.saveReport(context,report);
        }

    }


    public static void savePageThreeData(Context context,String equipmentName, String equipmentLocation,
                                         String ownerIdentification, String dateOfLastInspection, String lastReportNumber) {

        Report report = Utility.getReport(context);

        if (report!=null){
            Equipment equipment = report.getEquipment();

            if (equipment==null)
                equipment = new Equipment();
            equipment.setEquipmentName(equipmentName);
            equipment.setEquipmentLocation(equipmentLocation);
            report.setEquipment(equipment);


            report.setDateOfLastInspection(dateOfLastInspection);
            report.setLastInspectionReportNo(lastReportNumber);

            createEquipmentFolder(context,report.getProjectNo(),equipmentName);

            report.setOwnerIdentification(ownerIdentification);


            Utility.saveReport(context,report);
            Utility.showLog(report.toString());
        }

    }
    public static void savePageFourData(Context context,String airTemperature, String humidity) {

        Report report = Utility.getReport(context);

        if (report!=null){
            Equipment equipment = report.getEquipment();

            if (equipment!=null){
                equipment.setAirTemperature(airTemperature);
                equipment.setAirHumidity(humidity);

                report.setEquipment(equipment);

                Utility.saveReport(context,report);

                Utility.showLog(report.toString());
            }

        }

    }
    public static void savePanelBoardData(Context context,String testVoltage,String modelNumber,
                                          String catalog,String amps, String voltage) {

        Report report = Utility.getReport(context);

        if (report!=null){
            PanelBoard panelBoard = report.getPanelBoard();

            if (panelBoard==null)
                panelBoard = new PanelBoard();

            panelBoard.setTestVoltage(testVoltage);
            panelBoard.setModelNo(modelNumber);
            panelBoard.setCatalog(catalog);
            panelBoard.setAmps(amps);
            panelBoard.setVoltage(voltage);

            report.setPanelBoard(panelBoard);

            Utility.saveReport(context,report);
            Utility.showLog(report.toString());
        }

    }
    public static void saveManufacturerCurveDetailsData(Context context,
                                                        String mfgOne,String curveOne,String curveRangeOne,
                                                        String mfgTwo,String curveTwo,String curveRangeTwo,
                                                        String mfgThree,String curveThree,String curveRangeThree
                                          ) {

        Report report = Utility.getReport(context);

        if (report!=null){
            ManufacturerCurveDetails manufacturerCurveDetails = report.getManufacturerCurveDetails();

            if (manufacturerCurveDetails==null)
                manufacturerCurveDetails = new ManufacturerCurveDetails();

            manufacturerCurveDetails.setMfgOne(mfgOne);
            manufacturerCurveDetails.setCurveNumberOne(curveOne);
            manufacturerCurveDetails.setCurveRangeOne(curveRangeOne);

            manufacturerCurveDetails.setMfgTwo(mfgTwo);
            manufacturerCurveDetails.setCurveNumberTwo(curveTwo);
            manufacturerCurveDetails.setCurveRangeTwo(curveRangeTwo);

            manufacturerCurveDetails.setMfgThree(mfgThree);
            manufacturerCurveDetails.setCurveNumberThree(curveThree);
            manufacturerCurveDetails.setCurveRangeThree(curveRangeThree);


            report.setManufacturerCurveDetails(manufacturerCurveDetails);

            Utility.saveReport(context,report);
            Utility.showLog(report.toString());
        }

    }

    private static void createEquipmentFolder(Context context, String projectNo, String equipmentName) {
        MediaStoreUtils.createSubFolderInDocuments(context,Utility.BASE_FOLDER_NAME+"/"+projectNo,equipmentName);
    }

    public static void saveCircuitList(Context context,List<CircuitBreaker> circuitBreakerList){
        try {

            Report report = Utility.getReport(context);
            Equipment equipment = report.getEquipment();
            equipment.setCircuitBreakerList(circuitBreakerList);
            report.setEquipment(equipment);
            Utility.saveReport(context,report);

            createCircuitFolders(context,circuitBreakerList);

        }catch (Exception e){
            Utility.showLog("Exception "+e);
        }

    }

    private static void createCircuitFolders(Context context, List<CircuitBreaker> circuitBreakerList) {
        Report report = Utility.getReport(context);
        if (report!=null)
            if (report.getProjectNo()!=null)
                if (report.getEquipment().getEquipmentName()!=null){
                    for (CircuitBreaker circuitBreaker: circuitBreakerList){
                        MediaStoreUtils.createSubFolderInDocuments(context,
                                Utility.BASE_FOLDER_NAME+"/"+report.getProjectNo()+"/"+report.getEquipment().getEquipmentName(),
                                circuitBreaker.getName());
                        createTripAndCrmFolder(context,
                                Utility.BASE_FOLDER_NAME+"/"+report.getProjectNo()+"/"+report.getEquipment().getEquipmentName()+"/"+ circuitBreaker.getName());
                    }
                }

    }

    private static void createTripAndCrmFolder(Context context, String s) {
        MediaStoreUtils.createSubFolderInDocuments(context,s,Utility.TRIP_TEST);
        MediaStoreUtils.createSubFolderInDocuments(context,s,Utility.CRM_TEST);
    }

//    public static void savePageFourData(String, String, String, String, String, String, String, String, String, String) {
//
//    }
//
//    public static void savePageFiveData(String, String, String, String, String, String, String, String, String, String) {
//
//    }



//    public static void savePageData(String, String, String, String, String, String, String, String, String, String) {
//
//    }
//
//    public static void savePageData(String, String, String, String, String, String, String, String, String, String) {
//
//    }
//
//    public static void savePageData(String, String, String, String, String, String, String, String, String, String) {
//
//    }
//
//    public static void savePageData(String, String, String, String, String, String, String, String, String, String) {
//
//    }
}
