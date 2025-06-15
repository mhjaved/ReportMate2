package com.hasanjaved.reportmate.utility;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hasanjaved.reportmate.activity.HomeActivity;
import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.model.Employee;
import com.hasanjaved.reportmate.model.Report;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class Utility {

    public static final String SHARED_PREFERENCE_USER = "SHARED_PREFERENCE_USER";
    public static final String CURRENT_REPORT = "CURRENT_REPORT";
    public static final String PAGE_ONE = "PAGE_ONE";
    public static final String PAGE_THREE = "PAGE_ONE";
    public static final String EMPLOYEE = "EMPLOYEE";


    public static final String REPORT_MATE_DIRECTORY = "/storage/emulated/0/Android/data/com.hasanjaved.reportmate/files/Documents/NEL-SWGRTS1";
    public static final String IMAGE_SAMPLE_DIRECTORY = "/storage/emulated/0/Pictures/Capture.jpg";
    public static final String IMAGE_SAMPLE_DIRECTORY2 = "/storage/emulated/0/Documents/ReportMate/NEL-SWGRTS/dbBoxPanelFront.jpg";
    public static final String IMAGE_SAMPLE_DIRECTORY3 = "/storage/emulated/0/Documents/ReportMate/img.jpg";
    public static final String TAG = "ReportMate";
    public static final String BASE_FOLDER_NAME = "ReportMate";
    private static final String REPORTMATE_DIRECTORY = "ReportMate";

    public static final String TRIP_TEST = "TripTest";
    public static final String CRM_TEST = "CRM_Test";
    public static final String IrTest = "IrTest";
    public static final String generalImageTemperature = "generalImageTemperature";
    public static final String imgAgConnection = "imgAgConnection";
    public static final String imgBgConnection = "imgBgConnection";
    public static final String imgCgConnection = "imgCgConnection";

    public static final String imgAgResult = "imgAgResult";
    public static final String imgBgResult = "imgBgResult";
    public static final String imgCgResult = "imgCgResult";


    public static final String imgAbConnection = "imgAbConnection";
    public static final String imgBcConnection = "imgBcConnection";
    public static final String imgCaConnection = "imgCaConnection";
    public static final String imgCrmConnection = "imgCrmConnection";
    public static final String imgInjectorCurrent = "imgInjectorCurrent";
    public static final String imgInjectedCurrent = "imgInjectedCurrent";
    public static final String imgCrmResult = "imgCrmResult";

    public static final String imgAbResult = "imgAbResult";
    public static final String imgBcResult = "imgBcResult";
    public static final String imgCaResult = "imgCaResult";
//    public static final String imgCrmConnection = "imgCrmConnection";
//    public static final String imgCrmResult = "imgCrmResult";




    public static final String dbBoxPanelFront = "dbBoxPanelFront";
    public static final String dbBoxPanelInside = "dbBoxPanelInside";
    public static final String dbBoxPanelNameplate = "dbBoxPanelNameplate";
    public static final String dbBoxPanelGrounging = "dbBoxPanelGrounging";
    public static final String ImageToken = "ImageToken";
    public static  final String  imgTripTimeConnection ="imgTripTimeConnection";
    public static  final String  imgTripTime="imgTripTime";
    public static  final String  imgAfterTripTime="imgAfterTripTime";

    public static void showLog(String text){
        Log.d(TAG,text);
    }

    public static void showToast(Context context,String text){
        Toast.makeText(context,text, Toast.LENGTH_SHORT).show();
    }

    public static String getReportDirectory(Context context){
        return Utility.BASE_FOLDER_NAME+"/"+ Objects.requireNonNull(Utility.getReport(context)).getProjectNo();
    }

    public static void getCompletedReportList(){

    }

    public static void getRunningReportList(){
    }

    public static Employee getEmployee(Context context) {
        try {
            String connectionsJSONString = context.getSharedPreferences
                    (Utility.SHARED_PREFERENCE_USER, MODE_PRIVATE).getString(Utility.EMPLOYEE, null);
            Gson gson = new Gson();
            return gson.fromJson(connectionsJSONString, Employee.class);
        } catch (NullPointerException e) {
            Log.d(Utility.TAG, "\n NullPointerException in getEmployee");
            return null;
        }
    }

    public static void saveEmployee(Context context, Employee employee) {

        SharedPreferences mPrefs = context.getSharedPreferences(Utility.SHARED_PREFERENCE_USER, MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String string = gson.toJson(employee);
        prefsEditor.putString(Utility.EMPLOYEE, string);

        prefsEditor.apply();

    }


    public static Report getReport(Context context) {
        try {
            String connectionsJSONString = context.getSharedPreferences
                    (Utility.SHARED_PREFERENCE_USER, MODE_PRIVATE).getString(Utility.CURRENT_REPORT, null);
            Gson gson = new Gson();
            return gson.fromJson(connectionsJSONString, Report.class);
        } catch (NullPointerException e) {
            Log.d(Utility.TAG, "\n NullPointerException in getReport");
            return null;
        }
    }

//    public static void createNewReport(Context context,String reportName, String reportDate, String employeeId){
//        FolderManager.createReportMateFolder(context)
//    }

    public static void saveReport(Context context, Report report) {

        SharedPreferences mPrefs = context.getSharedPreferences(Utility.SHARED_PREFERENCE_USER, MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String string = gson.toJson(report);
        prefsEditor.putString(Utility.CURRENT_REPORT, string);

        prefsEditor.apply();

    }

    public static void createBaseFolder(Context context, String baseFolderName) {
        MediaStoreUtils.createFolderInDocuments(context,BASE_FOLDER_NAME);
    }

    public static void createProjectFolders(Context context, String projectNumber) {
        MediaStoreUtils.createSubFolderInDocuments(context,BASE_FOLDER_NAME,projectNumber);
        createIrFolder(context,projectNumber);
    }

    public static void createIrFolder(Context context, String projectNumber){
        MediaStoreUtils.createSubFolderInDocuments(context,BASE_FOLDER_NAME+"/"+projectNumber,Utility.IrTest);
    }

    public static boolean isEmpty(EditText et) {
        if (et.getText().toString().isEmpty())
            return true;
        else return false;
    }

    public static String getIrFolderLink(Activity activity, Report report) {
        return BASE_FOLDER_NAME+"/"+report.getProjectNo()+"/"+Utility.IrTest;
    }

    public static String getCrmFolderLink( Report report, CircuitBreaker circuitBreaker) {
        return BASE_FOLDER_NAME+"/"+report.getProjectNo()+"/"+report.getEquipment().getEquipmentName()+"/"+circuitBreaker.getName()+"/"+Utility.CRM_TEST;
    }


    public static String getTripFolderLink( Report report, CircuitBreaker circuitBreaker) {
        return BASE_FOLDER_NAME+"/"+report.getProjectNo()+"/"+report.getEquipment().getEquipmentName()+"/"+circuitBreaker.getName()+"/"+Utility.TRIP_TEST;
    }

    public static String getTemperatureImage(Context context) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY+"/"+Utility.getReport(context).getProjectNo());
        File imageFile = new File(reportMateDir, generalImageTemperature+".jpg");
        return imageFile.getAbsolutePath();
    }

    public static String getPanelImage(Context context) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY+"/"+Utility.getReport(context).getProjectNo());
        File imageFile = new File(reportMateDir, dbBoxPanelFront+".jpg");
        return imageFile.getAbsolutePath();
    }

    public static String getDbBoxCircuitImage(Context context) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY+"/"+Utility.getReport(context).getProjectNo());
        File imageFile = new File(reportMateDir, dbBoxPanelInside+".jpg");
        return imageFile.getAbsolutePath();
    }
}
