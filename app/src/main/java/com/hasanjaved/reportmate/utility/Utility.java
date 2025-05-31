package com.hasanjaved.reportmate.utility;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hasanjaved.reportmate.model.Employee;
import com.hasanjaved.reportmate.model.Report;

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
    public static final String IMAGE_SAMPLE_DIRECTORY = "/storage/emulated/0/Pictures/1748710167004.jpg";
    public static final String TAG = "ReportMate";
    public static final String BASE_FOLDER_NAME = "ReportMate";
    public static final String generalImageTemperature = "generalImageTemperature";
    public static final String ImageToken = "ImageToken";
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

    public static void createProjectFolder(){

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



//    public static void saveChildList(Context context, List<ChildInfo> childInfoList) {
//
//        SharedPreferences mPrefs = context.getSharedPreferences(Utility.SHARED_PREFERENCE_USER, MODE_PRIVATE);
//        SharedPreferences.Editor prefsEditor = mPrefs.edit();
//        String connectionsJSONString = new Gson().toJson(childInfoList);
//        prefsEditor.putString(Utility.CHILD_LIST, connectionsJSONString);
//        prefsEditor.apply();
//
//    }
//
//    public static List<ChildInfo> getChildList(Context context) {
//        String connectionsJSONString = context.getSharedPreferences(Utility.SHARED_PREFERENCE_USER, MODE_PRIVATE).getString(Utility.CHILD_LIST, null);
//        Type type = new TypeToken<List<ChildInfo>>() {
//        }.getType();
//        return new Gson().fromJson(connectionsJSONString, type);
//    }

}
