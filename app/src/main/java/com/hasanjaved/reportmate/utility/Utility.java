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
import java.util.ArrayList;
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
    public static final String HISTORY_FRAGMENT_TOKEN = "HISTORY_FRAGMENT_TOKEN";
    public static final String HISTORY_FRAGMENT_HISTORY = "HISTORY_FRAGMENT_HISTORY";
    public static final String HISTORY_FRAGMENT_ONGOING = "HISTORY_FRAGMENT_ONGOING";
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

    // trip images
    public static final String imgInjectorCurrent = "imgInjectorCurrent";
    public static final String imgInjectedCurrent = "imgInjectedCurrent";
    public static final String imgTripTimeConnection = "imgTripTimeConnection";
    public static final String imgTripTime = "imgTripTime";
    public static final String imgAfterTripTime = "imgAfterTripTime";


    public static void showLog(String text) {
        Log.d(TAG, text);
    }

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static String getReportDirectory(Context context) {
        return Utility.BASE_FOLDER_NAME + "/" + Objects.requireNonNull(Utility.getReport(context)).getProjectNo();
    }

    public static void getCompletedReportList() {

    }

    public static void getRunningReportList() {
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
        MediaStoreUtils.createFolderInDocuments(context, BASE_FOLDER_NAME);
    }

    public static void createProjectFolders(Context context, String projectNumber) {
        MediaStoreUtils.createSubFolderInDocuments(context, BASE_FOLDER_NAME, projectNumber);
        createIrFolder(context, projectNumber);
    }

    public static void createIrFolder(Context context, String projectNumber) {
        MediaStoreUtils.createSubFolderInDocuments(context, BASE_FOLDER_NAME + "/" + projectNumber, Utility.IrTest);
    }

    public static boolean isEmpty(EditText et) {
        if (et.getText().toString().isEmpty())
            return true;
        else return false;
    }

    public static String getIrFolderLink(Activity activity, Report report) {
        return BASE_FOLDER_NAME + "/" + report.getProjectNo() + "/" + Utility.IrTest;
    }

    public static String getCrmFolderLink(Report report, CircuitBreaker circuitBreaker) {
        return BASE_FOLDER_NAME + "/" + report.getProjectNo() + "/" + report.getEquipment().getEquipmentName() + "/" + circuitBreaker.getName() + "/" + Utility.CRM_TEST;
    }


    public static String getTripFolderLink(Report report, CircuitBreaker circuitBreaker) {
        return BASE_FOLDER_NAME + "/" + report.getProjectNo() + "/" + report.getEquipment().getEquipmentName() + "/" + circuitBreaker.getName() + "/" + Utility.TRIP_TEST;
    }

    public static String getTemperatureImage(Context context) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + Utility.getReport(context).getProjectNo());
        File imageFile = new File(reportMateDir, generalImageTemperature + ".jpg");
        return imageFile.getAbsolutePath();
    }

    public static String getPanelImage(Context context) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + Utility.getReport(context).getProjectNo());
        File imageFile = new File(reportMateDir, dbBoxPanelFront + ".jpg");
        return imageFile.getAbsolutePath();
    }

    public static String getDbBoxCircuitImage(Context context) {
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + Utility.getReport(context).getProjectNo());
        File imageFile = new File(reportMateDir, dbBoxPanelInside + ".jpg");
        return imageFile.getAbsolutePath();
    }

    public static List<String> getIrGroundConnectionImages(Context context) {
        List<String> imageList = new ArrayList<>();
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + Utility.getReport(context).getProjectNo() + "/" + Utility.IrTest);

        imageList.add(new File(reportMateDir, imgAgConnection + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgBgConnection + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgCgConnection + ".jpg").getAbsolutePath());

        return imageList;
    }

    public static List<String> getIrGroundResultImages(Context context) {
        List<String> imageList = new ArrayList<>();
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + Utility.getReport(context).getProjectNo() + "/" + Utility.IrTest);

        imageList.add(new File(reportMateDir, imgAgResult + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgBgResult + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgCgResult + ".jpg").getAbsolutePath());

        return imageList;
    }


    public static List<String> getIrLineConnectionImages(Context context) {
        List<String> imageList = new ArrayList<>();
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + Utility.getReport(context).getProjectNo() + "/" + Utility.IrTest);

        imageList.add(new File(reportMateDir, imgAbConnection + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgBcConnection + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgCaConnection + ".jpg").getAbsolutePath());

        return imageList;
    }

    public static List<String> getIrLineResultImages(Context context) {
        List<String> imageList = new ArrayList<>();
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + Utility.getReport(context).getProjectNo() + "/" + Utility.IrTest);

        imageList.add(new File(reportMateDir, imgAbResult + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgBcResult + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgCaResult + ".jpg").getAbsolutePath());

        return imageList;
    }

    public static List<String> getCrmImage(Context context, String circuitName) {
        List<String> imageList = new ArrayList<>();

        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + Utility.getReport(context).getProjectNo() + "/"
                + Utility.getReport(context).getEquipment().getEquipmentName() + "/" + circuitName + "/" + Utility.CRM_TEST);

        imageList.add(new File(reportMateDir, imgCrmConnection + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgCrmResult + ".jpg").getAbsolutePath());

        return imageList;
    }
    public static List<String> getCrmImageForReport(Context context, String circuitName) {
        List<String> imageList = new ArrayList<>();

        File reportMateDir = new File(REPORTMATE_DIRECTORY + "/" + Utility.getReport(context).getProjectNo() + "/"
                + Utility.getReport(context).getEquipment().getEquipmentName() + "/" + circuitName + "/" + Utility.CRM_TEST);

        imageList.add(new File(reportMateDir, imgCrmConnection + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgCrmResult + ".jpg").getAbsolutePath());

        return imageList;
    }

    public static List<String> getTripImage(Context context, String circuitName) {
        List<String> imageList = new ArrayList<>();

        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File reportMateDir = new File(documentsDir, REPORTMATE_DIRECTORY + "/" + Utility.getReport(context).getProjectNo() + "/"
                + Utility.getReport(context).getEquipment().getEquipmentName() + "/" + circuitName + "/" + Utility.TRIP_TEST);

        imageList.add(new File(reportMateDir, imgInjectorCurrent + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgInjectedCurrent + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgTripTimeConnection + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgTripTime + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgAfterTripTime + ".jpg").getAbsolutePath());

        return imageList;
    }
  public static List<String> getTripImageForReport(Context context, String circuitName) {
        List<String> imageList = new ArrayList<>();

        File reportMateDir = new File( REPORTMATE_DIRECTORY + "/" + Utility.getReport(context).getProjectNo() + "/"
                + Utility.getReport(context).getEquipment().getEquipmentName() + "/" + circuitName + "/" + Utility.TRIP_TEST);

        imageList.add(new File(reportMateDir, imgInjectorCurrent + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgInjectedCurrent + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgTripTimeConnection + ".jpg").getAbsolutePath());
        imageList.add(new File(reportMateDir, imgTripTime + ".jpg").getAbsolutePath());

        return imageList;
    }

    public static Boolean isCrmTestDone(Context context) {
        Boolean isAvailable = true;

        if (getReport(context) == null)
            return false;

        if (getReport(context).getEquipment() == null)
            return false;

        if (getReport(context).getEquipment().getCircuitBreakerList() == null)
            return false;

        if (getReport(context).getEquipment().getCircuitBreakerList().isEmpty())
            return false;
        else {
            for (CircuitBreaker circuitBreaker : getReport(context).getEquipment().getCircuitBreakerList()) {

                isAvailable = isFileAvailable(getCrmImage(context, circuitBreaker.getName()).get(0));
                if (!isAvailable) return false;
                isAvailable = isFileAvailable(getCrmImage(context, circuitBreaker.getName()).get(1));
                if (!isAvailable) return false;

            }
        }


        return true;

    }

    public static Boolean checkCrmForCircuit(Context context,CircuitBreaker circuitBreaker){
        Boolean isAvailable = true;

        isAvailable = isFileAvailable(getCrmImage(context, circuitBreaker.getName()).get(0));
        if (!isAvailable) return false;
        isAvailable = isFileAvailable(getCrmImage(context, circuitBreaker.getName()).get(1));
        if (!isAvailable) return false;

        return true;

    }

    public static Boolean checkTripForCircuit(Context context,CircuitBreaker circuitBreaker){
        Boolean isAvailable = true;

        List<String> images = getTripImage(context, circuitBreaker.getName());

        isAvailable = isFileAvailable(images.get(0));
        if (!isAvailable) return false;
        isAvailable = isFileAvailable(images.get(1));
        if (!isAvailable) return false;
        isAvailable = isFileAvailable(images.get(2));
        if (!isAvailable) return false;
        isAvailable = isFileAvailable(images.get(3));
        if (!isAvailable) return false;
        isAvailable = isFileAvailable(images.get(4));
        if (!isAvailable) return false;

        return true;

    }
    public static Boolean isTripTestDone(Context context) {
        Boolean isAvailable = true;

        if (getReport(context) == null)
            return false;

        if (getReport(context).getEquipment() == null)
            return false;

        if (getReport(context).getEquipment().getCircuitBreakerList() == null)
            return false;

        if (getReport(context).getEquipment().getCircuitBreakerList().isEmpty())
            return false;
        else {
            for (CircuitBreaker circuitBreaker : getReport(context).getEquipment().getCircuitBreakerList()) {

                List<String> images = getTripImage(context, circuitBreaker.getName());

                isAvailable = isFileAvailable(images.get(0));
                if (!isAvailable) return false;
                isAvailable = isFileAvailable(images.get(1));
                if (!isAvailable) return false;
                isAvailable = isFileAvailable(images.get(2));
                if (!isAvailable) return false;
                isAvailable = isFileAvailable(images.get(3));
                if (!isAvailable) return false;
                isAvailable = isFileAvailable(images.get(4));
                if (!isAvailable) return false;

            }
        }


        return true;
    }

    public static boolean isFileAvailable(String filePath) {
        try {
            if (filePath == null || filePath.trim().isEmpty()) {
                return false;
            }

            File file = new File(filePath);
            return file.exists() && file.isFile() && file.canRead();

        } catch (Exception e) {
            return false;
        }
    }
}
