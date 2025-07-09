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

            //-----------------------------------------------------------------
            Utility.saveReportOngoing(context, Utility.getReport(context));
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

            //-----------------------------------------------------------------
            Utility.saveReportOngoing(context, Utility.getReport(context));
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

                            //-----------------------------------------------------------------
                            Utility.saveReportOngoing(context, Utility.getReport(context));
                        }


                    }

        }


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

                            //-----------------------------------------------------------------
                            Utility.saveReportOngoing(context, Utility.getReport(context));
                        }


                    }

        }

    }




}
