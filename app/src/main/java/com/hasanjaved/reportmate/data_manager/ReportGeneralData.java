package com.hasanjaved.reportmate.data_manager;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hasanjaved.reportmate.model.Employee;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.utility.FolderManager;
import com.hasanjaved.reportmate.utility.Utility;

public class ReportGeneralData {

    public static void savePageOneData(Context context,String employeeId, String testDate, String projectNumber) {

//        if ()
//        String folderLocation = FolderManager.createFolder(context,projectNumber);

        String baseFolderLink = FolderManager.getLinkIfFolderExist(context,Utility.BASE_FOLDER_NAME);
        String projectLink = null;

        if (baseFolderLink==null)
            baseFolderLink = FolderManager.createBaseFolder(context);

        if (baseFolderLink!=null){
            projectLink = FolderManager.checkFolderInDirectory(context,baseFolderLink,projectNumber);

            if (projectLink!=null){
                Utility.showLog(projectLink);
            }else {
                projectLink =  FolderManager.createFolderInDirectory(context,baseFolderLink,projectNumber);
            }
        }

        Report report =  Utility.getReport(context);

        if (report==null)
            report = new Report();

        report.setEmployeeId(employeeId);
        report.setTestDate(testDate);
        report.setProjectNo(projectNumber);
        report.setFolderLocation(projectLink);

        Utility.saveReport(context,report);

    }

    public static void savePageTwoData(Context context,String customerName, String customerAddress, String userName,String userAddress) {

    }


    public static void savePageThreeData(Context context,String equipmentName, String equipmentLocation, String ownerIdentification, String dateOfLastInspection, String lastReportNumber) {

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
