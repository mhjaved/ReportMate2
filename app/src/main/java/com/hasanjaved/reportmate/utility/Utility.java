package com.hasanjaved.reportmate.utility;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.model.Employee;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.model.ReportHistory;
import com.hasanjaved.reportmate.model.ReportOngoing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utility {

    public static final String SHARED_PREFERENCE_USER = "SHARED_PREFERENCE_USER";
    public static final String CURRENT_REPORT = "CURRENT_REPORT";
    public static final String ONGOING_REPORT_LIST = "ONGOING_REPORT_LIST";
    public static final String REPORT_HISTORY_LIST = "REPORT_HISTORY_LIST";
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




//    public static final String imgAgConnection = "imgAgConnection";
//    public static final String imgBgConnection = "imgBgConnection";
//    public static final String imgCgConnection = "imgCgConnection";

//    public static final String imgAgResult = "imgAgResult";
//    public static final String imgBgResult = "imgBgResult";
//    public static final String imgCgResult = "imgCgResult";


//    public static final String imgAbConnection = "imgAbConnection";
//    public static final String imgBcConnection = "imgBcConnection";
//    public static final String imgCaConnection = "imgCaConnection";
//    public static final String imgCrmConnection = "imgCrmConnection";
//    public static final String imgCrmResult = "imgCrmResult";

//    public static final String imgAbResult = "imgAbResult";
//    public static final String imgBcResult = "imgBcResult";
//    public static final String imgCaResult = "imgCaResult";

//    public static final String imgCrmConnection = "imgCrmConnection";
//    public static final String imgCrmResult = "imgCrmResult";



    public static final String ImageToken = "ImageToken";

    // trip images



    public static void showLog(String text) {
        Log.d(TAG, text);
    }

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
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


    public static void saveReport(Context context, Report report) {

        SharedPreferences mPrefs = context.getSharedPreferences(Utility.SHARED_PREFERENCE_USER, MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String string = gson.toJson(report);
        prefsEditor.putString(Utility.CURRENT_REPORT, string);

        prefsEditor.apply();
    }

    public static ReportOngoing getOngoingReportList(Context context) {
        try {
            String connectionsJSONString = context.getSharedPreferences
                    (Utility.SHARED_PREFERENCE_USER, MODE_PRIVATE).getString(Utility.ONGOING_REPORT_LIST, null);
            Gson gson = new Gson();
             return gson.fromJson(connectionsJSONString, ReportOngoing.class);

        } catch (NullPointerException e) {
            Log.d(Utility.TAG, "\n NullPointerException in getOngoingReportList");
            return null;
        }
    }

    public static ReportHistory getHistoryReportList(Context context) {
        try {
            String connectionsJSONString = context.getSharedPreferences
                    (Utility.SHARED_PREFERENCE_USER, MODE_PRIVATE).getString(Utility.REPORT_HISTORY_LIST, null);
            Gson gson = new Gson();
             return gson.fromJson(connectionsJSONString, ReportHistory.class);

        } catch (NullPointerException e) {
            Log.d(Utility.TAG, "\n NullPointerException in getHistoryReportList");
            return null;
        }
    }

    public static void saveReportOngoing(Context context, Report report) {

        SharedPreferences mPrefs = context.getSharedPreferences(Utility.SHARED_PREFERENCE_USER, MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        ReportOngoing ongoing = getOngoingReportList(context);
        if (ongoing==null){
           ongoing = new ReportOngoing();
           List<Report> list = new ArrayList<>();
           list.add(report);
           ongoing.setOngoingReportList(list);
        }else {
            List<Report> list = ongoing.getOngoingReportList();
            if (list==null){
                list = new ArrayList<>();
            }
                    boolean updated = false;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getEquipment().getEquipmentName().equals(report.getEquipment().getEquipmentName())) {
                            list.set(i, report);
                            updated = true;
                            break;
                        }
                    }
                    if (!updated) {
                        list.add(report); // Add new if not found
                    }


            ongoing.setOngoingReportList(list);
        }


        Gson gson = new Gson();
        String string = gson.toJson(ongoing);
        prefsEditor.putString(Utility.ONGOING_REPORT_LIST, string);

        prefsEditor.apply();

    }
    public static void saveReportHistory(Context context, Report report) {

        SharedPreferences mPrefs = context.getSharedPreferences(Utility.SHARED_PREFERENCE_USER, MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        ReportHistory reportHistory = getHistoryReportList(context);
        if (reportHistory==null){
            reportHistory = new ReportHistory();
           List<Report> list = new ArrayList<>();
           list.add(report);
            reportHistory.setReportHistoryList(list);
        }else {
            List<Report> list = reportHistory.getReportHistoryList();
            if (list==null){
                list = new ArrayList<>();
            }
                    boolean updated = false;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getEquipment().getEquipmentName().equals(report.getEquipment().getEquipmentName())) {
                            list.set(i, report);
                            updated = true;
                            break;
                        }
                    }
                    if (!updated) {
                        list.add(report); // Add new if not found
                    }


            reportHistory.setReportHistoryList(list);
        }


        Gson gson = new Gson();
        String string = gson.toJson(reportHistory);
        prefsEditor.putString(Utility.REPORT_HISTORY_LIST, string);

        prefsEditor.apply();

    }



    public static boolean isEmpty(EditText et) {
        if (et.getText().toString().isEmpty())
            return true;
        else return false;
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

                isAvailable = isFileAvailable(DirectoryManager.getCrmImage(getReport(context).getEquipment().getEquipmentName(),circuitBreaker.getName()).get(0));
                if (!isAvailable) return false;
                isAvailable = isFileAvailable(DirectoryManager.getCrmImage(getReport(context).getEquipment().getEquipmentName(), circuitBreaker.getName()).get(1));
                if (!isAvailable) return false;

            }
        }


        return true;

    }

    public static Boolean checkCrmForCircuit(Context context,CircuitBreaker circuitBreaker){
        Boolean isAvailable = true;

        isAvailable = isFileAvailable(DirectoryManager.getCrmImage(Utility.getReport(context).getEquipment().getEquipmentName() ,circuitBreaker.getName()).get(0));
        if (!isAvailable) return false;
        isAvailable = isFileAvailable(DirectoryManager.getCrmImage(Utility.getReport(context).getEquipment().getEquipmentName(), circuitBreaker.getName()).get(1));
        if (!isAvailable) return false;

        return true;

    }

    public static Boolean checkTripForCircuit(String equipmentName,CircuitBreaker circuitBreaker){
        Boolean isAvailable = true;

        List<String> images = DirectoryManager.getTripImage(equipmentName, circuitBreaker.getName());

        isAvailable = isFileAvailable(images.get(0));
        if (!isAvailable) return false;
        isAvailable = isFileAvailable(images.get(1));
        if (!isAvailable) return false;
        isAvailable = isFileAvailable(images.get(2));
        if (!isAvailable) return false;
        isAvailable = isFileAvailable(images.get(3));
        if (!isAvailable) return false;
//        isAvailable = isFileAvailable(images.get(4));
//        if (!isAvailable) return false;

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

                List<String> images = DirectoryManager.getTripImage(getReport(context).getEquipment().getEquipmentName(), circuitBreaker.getName());

                isAvailable = isFileAvailable(images.get(0));
                if (!isAvailable) return false;
                isAvailable = isFileAvailable(images.get(1));
                if (!isAvailable) return false;
                isAvailable = isFileAvailable(images.get(2));
                if (!isAvailable) return false;
                isAvailable = isFileAvailable(images.get(3));
                if (!isAvailable) return false;
//                isAvailable = isFileAvailable(images.get(4));
//                if (!isAvailable) return false;

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

    public static boolean validateEditText(EditText editText) {
        boolean allFilled = true;

        String text = editText.getText().toString().trim();
        if (text.isEmpty()) {
            editText.setError("This field cannot be empty");
            allFilled = false;
        } else {
            editText.setError(null); // Clear previous error
        }

        return allFilled;
    }

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getFormatTime(String timeValue) {
        if (timeValue == null || timeValue.trim().isEmpty()) {
            return "0sec";
        }

        try {
            String cleanInput = timeValue.trim();

            // Handle different formats like "2,4" (comma as decimal separator)
            cleanInput = cleanInput.replace(",", ".");

            double time = Double.parseDouble(cleanInput);

            if (time < 0) {
                return "0sec";
            }

            // Get the integer part (minutes)
            int minutes = (int) time;

            // Get the decimal part and convert to seconds
            double decimalPart = time - minutes;
            int seconds = (int) Math.round(decimalPart * 100);

            // Handle overflow
            if (seconds >= 60) {
                minutes += seconds / 60;
                seconds = seconds % 60;
            }

            // Format output
            StringBuilder result = new StringBuilder();

            if (minutes > 0) {
                result.append(minutes).append("min");
                if (seconds > 0) {
                    result.append("\n").append(seconds).append("sec");
                }
            } else {
                result.append(seconds).append("sec");
            }

            return result.toString();

        } catch (NumberFormatException e) {
            return "0sec";
        }
    }
}
