package com.hasanjaved.reportmate.data_manager;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.model.CrmTest;
import com.hasanjaved.reportmate.model.Employee;
import com.hasanjaved.reportmate.model.IrTest;
import com.hasanjaved.reportmate.model.ManufacturerCurveDetails;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.model.TripTest;
import com.hasanjaved.reportmate.utility.Utility;

import java.util.List;

public class TestData {

    public static final String SHARED_PREFERENCE_USER = "SHARED_PREFERENCE_USER";
    public static final String CURRENT_REPORT = "CURRENT_REPORT";
    public static final String EMPLOYEE = "EMPLOYEE";

    public static final String REPORT_MATE_DIRECTORY = "/storage/emulated/0/Android/data/com.hasanjaved.reportmate/files/Documents/ReportMate";
    public static final String TAG = "ReportMate";
    public static final String ImageToken = "ImageToken";

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
                    (TestData.SHARED_PREFERENCE_USER, MODE_PRIVATE).getString(TestData.EMPLOYEE, null);
            Gson gson = new Gson();
            return gson.fromJson(connectionsJSONString, Employee.class);
        } catch (NullPointerException e) {
            Log.d(TestData.TAG, "\n NullPointerException in getEmployee");
            return null;
        }
    }

    public static void saveEmployee(Context context, Employee employee) {

        SharedPreferences mPrefs = context.getSharedPreferences(TestData.SHARED_PREFERENCE_USER, MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String string = gson.toJson(employee);
        prefsEditor.putString(TestData.EMPLOYEE, string);

        prefsEditor.apply();

    }


    public static Report getReport(Context context) {
        try {
            String connectionsJSONString = context.getSharedPreferences
                    (TestData.SHARED_PREFERENCE_USER, MODE_PRIVATE).getString(TestData.CURRENT_REPORT, null);
            Gson gson = new Gson();
            return gson.fromJson(connectionsJSONString, Report.class);
        } catch (NullPointerException e) {
            Log.d(TestData.TAG, "\n NullPointerException in getReport");
            return null;
        }
    }

//    public static void createNewReport(Context context,String reportName, String reportDate, String employeeId){
//        FolderManager.createReportMateFolder(context)
//    }

    public static void saveReport(Context context, Report report) {

        SharedPreferences mPrefs = context.getSharedPreferences(TestData.SHARED_PREFERENCE_USER, MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String string = gson.toJson(report);
        prefsEditor.putString(TestData.CURRENT_REPORT, string);

        prefsEditor.apply();

    }

    public static void saveIrTestGroundConnectionData(Context context,
                                                      String agValue, String bgValue, String cgValue

    ) {

        Report report = Utility.getReport(context);

        if (report != null) {
            IrTest irTest = report.getIrTest();

            if (irTest == null)
                irTest = new IrTest();

            irTest.setAgValue(agValue);
            irTest.setBgValue(bgValue);
            irTest.setCgValue(cgValue);


            report.setIrTest(irTest);

            Utility.saveReport(context, report);
            Utility.showLog(report.toString());
        }

    }

    public static void saveCrmTestData(Context context,
                                       CircuitBreaker circuitBreaker,
                                       String rResValue, String rResUnit,
                                       String yResValue, String yResUnit,
                                       String bResValue, String bResUnit

    ) {


        Report report = Utility.getReport(context);

        if (report != null) {
            if (report.getEquipment() != null)
                if (report.getEquipment().getCircuitBreakerList() != null)
                    if (!report.getEquipment().getCircuitBreakerList().isEmpty()) {

                        int index = Integer.parseInt(circuitBreaker.getCircuitId());

                        if (index < report.getEquipment().getCircuitBreakerList().size()) {
                            List<CircuitBreaker> list= report.getEquipment().getCircuitBreakerList();
                            CircuitBreaker currentBreaker = list.get(index);
                            CrmTest crmTest  = currentBreaker.getCrmTest();

                            if (crmTest == null)
                                crmTest = new CrmTest();

                            crmTest.setrResValue(rResValue);
                            crmTest.setrResUnit(rResUnit);

                            crmTest.setyResValue(yResValue);
                            crmTest.setyResUnit(yResUnit);

                            crmTest.setbResValue(bResValue);
                            crmTest.setbResUnit(bResUnit);

                            currentBreaker.setCrmTest(crmTest);
                            list.set(index,currentBreaker);

                            report.getEquipment().setCircuitBreakerList(list);
                            Utility.saveReport(context, report);
                            Utility.showLog(report.toString());
                        }


                    }

        }

//        Report report = Utility.getReport(context);

//        if (report != null) {
//            CrmTest crmTest = report.getCrmTest();
//
//            if (crmTest == null)
//                crmTest = new CrmTest();
//
//            crmTest.setrResValue(rResValue);
//            crmTest.setrResUnit(rResUnit);
//
//            crmTest.setyResValue(yResValue);
//            crmTest.setyResUnit(yResUnit);
//
//            crmTest.setbResValue(bResValue);
//            crmTest.setbResUnit(bResUnit);
//
//
//            report.setCrmTest(crmTest);
//
//            Utility.saveReport(context, report);
//            Utility.showLog(report.toString());
//        }

    }

    public static void saveTripTestData(Context context,
                                        CircuitBreaker circuitBreaker,
                                        String testAmplitude,
                                        String tripTime,
                                        String instantTrip

    ) {

        Report report = Utility.getReport(context);

        if (report != null) {
            if (report.getEquipment() != null)
                if (report.getEquipment().getCircuitBreakerList() != null)
                    if (!report.getEquipment().getCircuitBreakerList().isEmpty()) {

                        int index = Integer.parseInt(circuitBreaker.getCircuitId());

                        if (index < report.getEquipment().getCircuitBreakerList().size()) {
                            List<CircuitBreaker> list= report.getEquipment().getCircuitBreakerList();
                            CircuitBreaker currentBreaker = list.get(index);
                            TripTest tripTest = currentBreaker.getTripTest();

                            if (tripTest == null)
                                tripTest = new TripTest();

                            tripTest.setTestAmplitude(testAmplitude);
                            tripTest.setTripTime(tripTime);
                            tripTest.setInstantTrip(instantTrip);

                            currentBreaker.setTripTest(tripTest);
                            list.set(index,currentBreaker);

                            report.getEquipment().setCircuitBreakerList(list);
                            Utility.saveReport(context, report);
                            Utility.showLog(report.toString());
                        }


                    }

        }

    }

    public static void saveIrTestLineConnectionData(Context context,
                                                    String abValue, String bcValue, String caValue

    ) {

        Report report = Utility.getReport(context);

        if (report != null) {
            IrTest irTest = report.getIrTest();

            if (irTest == null)
                irTest = new IrTest();

            irTest.setAbValue(abValue);
            irTest.setBcValue(bcValue);
            irTest.setCaValue(caValue);


            report.setIrTest(irTest);

            Utility.saveReport(context, report);
            Utility.showLog(report.toString());
        }

    }


}
