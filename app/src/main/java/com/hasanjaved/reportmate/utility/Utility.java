package com.hasanjaved.reportmate.utility;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
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


    public static final String ImageToken = "ImageToken";
    public static int ImageWidth = 380;
    public static int ImageHeight = 225;

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
            Utility.showLog( "\n NullPointerException in getEmployee");
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
            Utility.showLog( "\n NullPointerException in getReport");
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

    public static ReportHistory getHistoryReportList(Context context) {
        try {
            String connectionsJSONString = context.getSharedPreferences
                    (Utility.SHARED_PREFERENCE_USER, MODE_PRIVATE).getString(Utility.REPORT_HISTORY_LIST, null);
            Gson gson = new Gson();
             return gson.fromJson(connectionsJSONString, ReportHistory.class);

        } catch (NullPointerException e) {
            Utility.showLog( "\n NullPointerException in getHistoryReportList");
            return null;
        }
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
    public static void deleteHistoryReport(Context context, int index) {
        try {
            // Get current history report list
            ReportHistory reportHistory = getHistoryReportList(context);

            // Check if history reports exist
            if (reportHistory == null || reportHistory.getReportHistoryList() == null) {
                Utility.showLog("No history reports found to delete");
                return;
            }

            List<Report> list = reportHistory.getReportHistoryList();

            // Validate index
            if (index < 0 || index >= list.size()) {
                Utility.showLog("Invalid index: " + index + ". List size: " + list.size());
                return;
            }

            // Remove the report at the specified index
            Report removedReport = list.remove(index);
            Utility.showLog("Deleted history report: " +
                    (removedReport.getEquipment() != null ? removedReport.getEquipment().getEquipmentName() : "Unknown"));

            // Update the history report list
            reportHistory.setReportHistoryList(list);

            // Save updated list to SharedPreferences
            SharedPreferences mPrefs = context.getSharedPreferences(Utility.SHARED_PREFERENCE_USER, MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();

            if (list.isEmpty()) {
                // If list is empty, remove the entire key
                prefsEditor.remove(Utility.REPORT_HISTORY_LIST);
                Utility.showLog("All history reports deleted. Cleared from SharedPreferences.");
            } else {
                // Save updated list
                Gson gson = new Gson();
                String string = gson.toJson(reportHistory);
                prefsEditor.putString(Utility.REPORT_HISTORY_LIST, string);
                Utility.showLog("Updated history reports list. Remaining count: " + list.size());
            }

            prefsEditor.apply();

        } catch (Exception e) {
            Utility.showLog("Error deleting history report at index " + index + ": " + e.getMessage());
        }
    }
    public static ReportOngoing getOngoingReportList(Context context) {
        try {
            String connectionsJSONString = context.getSharedPreferences
                    (Utility.SHARED_PREFERENCE_USER, MODE_PRIVATE).getString(Utility.ONGOING_REPORT_LIST, null);
            Gson gson = new Gson();
            return gson.fromJson(connectionsJSONString, ReportOngoing.class);

        } catch (NullPointerException e) {
            Utility.showLog( "\n NullPointerException in getOngoingReportList");
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



    public static void deleteOngoingReport(Context context, int index) {
        try {
            // Get current ongoing report list
            ReportOngoing ongoing = getOngoingReportList(context);

            // Check if ongoing reports exist
            if (ongoing == null || ongoing.getOngoingReportList() == null) {
                Utility.showLog( "No ongoing reports found to delete");
                return;
            }

            List<Report> list = ongoing.getOngoingReportList();

            // Validate index
            if (index < 0 || index >= list.size()) {
                Utility.showLog( "Invalid index: " + index + ". List size: " + list.size());
                return;
            }

            // Remove the report at the specified index
            Report removedReport = list.remove(index);
            Utility.showLog( "Deleted ongoing report: " +
                    (removedReport.getEquipment() != null ? removedReport.getEquipment().getEquipmentName() : "Unknown"));

            // Update the ongoing report list
            ongoing.setOngoingReportList(list);

            // Save updated list to SharedPreferences
            SharedPreferences mPrefs = context.getSharedPreferences(Utility.SHARED_PREFERENCE_USER, MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();

            if (list.isEmpty()) {
                // If list is empty, remove the entire key or save empty list
                prefsEditor.remove(Utility.ONGOING_REPORT_LIST);
                Utility.showLog( "All ongoing reports deleted. Cleared from SharedPreferences.");
            } else {
                // Save updated list
                Gson gson = new Gson();
                String string = gson.toJson(ongoing);
                prefsEditor.putString(Utility.ONGOING_REPORT_LIST, string);
                Utility.showLog( "Updated ongoing reports list. Remaining count: " + list.size());
            }

            prefsEditor.apply();

        } catch (Exception e) {
            Log.e(Utility.TAG, "Error deleting ongoing report at index " + index, e);
        }
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

    public static boolean match(String text, String equipmentName) {
        // Handle null or empty inputs
        if (text == null || equipmentName == null) {
            return false;
        }

        // Trim whitespace and convert to lowercase for case-insensitive comparison
        String searchText = text.trim().toLowerCase();
        String equipment = equipmentName.trim().toLowerCase();

        // Check if search text is empty
        if (searchText.isEmpty()) {
            return false;
        }

        // Strategy 1: Check if text matches the full equipment name
        if (equipment.equals(searchText)) {
            return true;
        }

            // If equipment name has less than 5 characters, match against the entire name
            return equipment.equals(searchText);

    }

    /**
     * Search and filter reports based on text matching various Report fields
     *
     * @param reports List of reports to search through
     * @param searchText Text to search for in report fields
     * @return List of reports that match the search criteria
     */
    public static List<Report> searchReports(List<Report> reports, String searchText) {

        Utility.showLog(searchText+"    \n"+reports.toString());
        try {
            List<Report> filteredReports = new ArrayList<>();

            // Handle null or empty inputs
            if (reports == null || searchText == null) {
                return filteredReports; // Return empty list
            }

            // Trim and convert search text to lowercase for case-insensitive comparison
            String text = searchText.trim().toLowerCase();

            // If search text is empty, return empty list
            if (text.isEmpty()) {
                return filteredReports;
            }

            // Iterate through all reports
            for (Report report : reports) {
                if (report != null && matchesReportFields(report, text)) {
                    filteredReports.add(report);
                }
            }

            return filteredReports;
        }catch (Exception e){
            return null;
        }

    }

    private static boolean matchesReportFields(Report report, String searchText) {

        // Check user name (owner identification)
        if (containsIgnoreCase(report.getOwnerIdentification(), searchText)) {
            return true;
        }

        // Check customer address
        if (containsIgnoreCase(report.getCustomerAddress(), searchText)) {
            return true;
        }

        // Check date fields
        if (containsIgnoreCase(report.getTestDate(), searchText)) {
            return true;
        }


        // Check EquipmentName
        if (report.getEquipment() != null) {
            return containsIgnoreCase(report.getEquipment().getEquipmentName(), searchText);
        }

        return false;
    }

    private static boolean containsIgnoreCase(String source, String searchText) {
        if (source == null || searchText == null) {
            return false;
        }
        return source.toLowerCase().contains(searchText);
    }

}
