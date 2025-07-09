package com.hasanjaved.reportmate.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSpinner;

import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.model.CrmTest;
import com.hasanjaved.reportmate.model.Equipment;
import com.hasanjaved.reportmate.model.IrTest;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.model.TripTest;

import java.util.List;

public class PopupManager {

    public static void showConfirmCircuitPopup(Context context, ConfirmCircuitList confirmCircuitList) {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.popup_layout, null);

        // Get references to views
        TextView titleText = customView.findViewById(R.id.popup_title);
        TextView messageText = customView.findViewById(R.id.popup_message);
        Button yesButton = customView.findViewById(R.id.btn_yes);
        Button noButton = customView.findViewById(R.id.btn_no);

        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(customView)
                .setCancelable(true)
                .create();

        // Set content
        titleText.setText(R.string.confirm_action);
        messageText.setText(R.string.are_you_sure_you_want_to_save_circuits);

        // Set button actions
        yesButton.setOnClickListener(v -> {
            // YES action
            Toast.makeText(context, R.string.report_saved_to_ongoing_list, Toast.LENGTH_SHORT).show();
            confirmCircuitList.confirmed();
            dialog.dismiss();
        });

        noButton.setOnClickListener(v -> {
            // NO action
//            Toast.makeText(context, "No clicked - Action cancelled", Toast.LENGTH_SHORT).show();
            confirmCircuitList.cancelled();
            dialog.dismiss();
        });

        // Show dialog
        dialog.show();
    }
    public static void showConfirmGenerateReport(Context context, ConfirmGenerateReport  confirmGenerateReport,Report report) {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.popup_layout, null);

        // Get references to views
        TextView titleText = customView.findViewById(R.id.popup_title);
        TextView messageText = customView.findViewById(R.id.popup_message);
        Button yesButton = customView.findViewById(R.id.btn_yes);
        Button noButton = customView.findViewById(R.id.btn_no);

        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(customView)
                .setCancelable(true)
                .create();

        // Set content
        titleText.setText(R.string.confirm_action);
        messageText.setText(R.string.are_you_sure_you_want_to_generate_report);

        // Set button actions
        yesButton.setOnClickListener(v -> {
            // YES action
            Toast.makeText(context, R.string.report_is_generating, Toast.LENGTH_SHORT).show();
            confirmGenerateReport.confirmed(report);
            dialog.dismiss();
        });

        noButton.setOnClickListener(v -> {
            // NO action
//            Toast.makeText(context, "No clicked - Action cancelled", Toast.LENGTH_SHORT).show();
            confirmGenerateReport.cancelled();
            dialog.dismiss();
        });

        // Show dialog
        dialog.show();
    }

    public static void showEditCustomerPopup(Context context, Report report, EditPopupListener editPopupListener) {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.layout_edit_customer_details, null);

        // Get references to views
        EditText etCustomerName = customView.findViewById(R.id.etCustomerName);
        EditText etCustomerAddress = customView.findViewById(R.id.etCustomerAddress);
        EditText etOwner = customView.findViewById(R.id.etOwner);
        EditText etOwnerAddress = customView.findViewById(R.id.etOwnerAddress);

        ImageView ivCancel = customView.findViewById(R.id.ivCancel);
        Button btnSave = customView.findViewById(R.id.btnSave);

        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(customView)
                .setCancelable(true)
                .create();

        // Set content
        etCustomerName.setText(report.getCustomerName());
        etCustomerAddress.setText(report.getCustomerAddress());
        etOwner.setText(report.getOwnerIdentification());
        etOwnerAddress.setText(report.getUserAddress());

        // Set button actions
        btnSave.setOnClickListener(v -> {

            if (Utility.validateEditText(etCustomerName) &&
                    Utility.validateEditText(etCustomerAddress) &&
                    Utility.validateEditText(etOwner) &&
                    Utility.validateEditText(etOwnerAddress)) {

                report.setCustomerName(etCustomerName.getText().toString().trim());
                report.setCustomerAddress(etCustomerAddress.getText().toString().trim());
                report.setOwnerIdentification(etOwner.getText().toString().trim());
                report.setOwnerAddress(etOwnerAddress.getText().toString().trim());

                Utility.saveReportOngoing(context, report);
                editPopupListener.savedCustomerDetails();
                dialog.dismiss();

            }


        });

        ivCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // Show dialog
        dialog.show();
    }

    public static void showEditSitePopup(Context context, Report report, EditPopupListener editPopupListener) {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.layout_edit_site_details, null);

        // Get references to views
        EditText etOwner = customView.findViewById(R.id.etOwner);
        EditText etLastDate = customView.findViewById(R.id.etLastDate);
        EditText etLastReport = customView.findViewById(R.id.etLastReport);
        EditText etAirTemp = customView.findViewById(R.id.etAirTemp);
        EditText etRelativeHumidity = customView.findViewById(R.id.etRelativeHumidity);

        ImageView ivCancel = customView.findViewById(R.id.ivCancel);
        Button btnSave = customView.findViewById(R.id.btnSave);

        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(customView)
                .setCancelable(true)
                .create();

        // Set content
        etOwner.setText(report.getOwnerIdentification());
        etLastDate.setText(report.getDateOfLastInspection());
        etLastReport.setText(report.getLastInspectionReportNo());

        Equipment equipment = report.getEquipment();//

        etAirTemp.setText(equipment.getAirTemperature());
        etRelativeHumidity.setText(equipment.getAirHumidity());


        // Set button actions
        btnSave.setOnClickListener(v -> {

            if (Utility.validateEditText(etOwner) &&
                    Utility.validateEditText(etLastDate) &&
                    Utility.validateEditText(etLastReport) &&
                    Utility.validateEditText(etAirTemp) &&
                    Utility.validateEditText(etRelativeHumidity)) {

                report.setOwnerIdentification(etOwner.getText().toString().trim());
                report.setDateOfLastInspection(etLastDate.getText().toString().trim());
                report.setLastInspectionReportNo(etLastReport.getText().toString().trim());

                equipment.setAirTemperature(etAirTemp.getText().toString().trim());
                equipment.setAirHumidity(etRelativeHumidity.getText().toString().trim());
                report.setEquipment(equipment);


                Utility.saveReportOngoing(context, report);
                editPopupListener.savedSiteDetails();
                dialog.dismiss();

            }


        });

        ivCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // Show dialog
        dialog.show();
    }

    public static void showEditIrPopupGround(Context context, Report report, EditPopupListener editPopupListener) {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.layout_edit_ir_test_ground, null);

        // Get references to views
        EditText etAg = customView.findViewById(R.id.etAg);
        EditText etBg = customView.findViewById(R.id.etBg);
        EditText etCg = customView.findViewById(R.id.etCg);

        ImageView ivCancel = customView.findViewById(R.id.ivCancel);
        Button btnSave = customView.findViewById(R.id.btnSave);

        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(customView)
                .setCancelable(true)
                .create();

        // Set content
        IrTest irTest = report.getIrTest();
        if (irTest == null)
            irTest = new IrTest();
        else {

            if (irTest.getAgValue() != null)
                etAg.setText(irTest.getAgValue());

            if (irTest.getBgValue() != null)
                etBg.setText(irTest.getBgValue());

            if (irTest.getCgValue() != null)
                etCg.setText(irTest.getCgValue());

        }


        // Set button actions
        IrTest finalIrTest = irTest;
        btnSave.setOnClickListener(v -> {

            if (Utility.validateEditText(etAg) &&
                    Utility.validateEditText(etBg) &&
                    Utility.validateEditText(etCg)) {

                finalIrTest.setAgValue(etAg.getText().toString().trim());
                finalIrTest.setBgValue(etBg.getText().toString().trim());
                finalIrTest.setCgValue(etCg.getText().toString().trim());

                report.setIrTest(finalIrTest);
                Utility.saveReportOngoing(context, report);
                editPopupListener.savedIrTestData();
                dialog.dismiss();

            }


        });

        ivCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // Show dialog
        dialog.show();
    }
   public static void showEditIrPopupLine(Context context, Report report, EditPopupListener editPopupListener) {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.layout_edit_ir_test_line, null);

        // Get references to views

        EditText etAb = customView.findViewById(R.id.etAb);
        EditText etBc = customView.findViewById(R.id.etBc);
        EditText etCa = customView.findViewById(R.id.etCa);

        ImageView ivCancel = customView.findViewById(R.id.ivCancel);
        Button btnSave = customView.findViewById(R.id.btnSave);

        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(customView)
                .setCancelable(true)
                .create();

        // Set content
        IrTest irTest = report.getIrTest();
        if (irTest == null)
            irTest = new IrTest();
        else {

            if (irTest.getAbValue() != null)
                etAb.setText(irTest.getAbValue());

            if (irTest.getBcValue() != null)
                etBc.setText(irTest.getBcValue());

            if (irTest.getCaValue() != null)
                etCa.setText(irTest.getCaValue());

        }


        // Set button actions
        IrTest finalIrTest = irTest;
        btnSave.setOnClickListener(v -> {

            if (Utility.validateEditText(etAb) &&
                    Utility.validateEditText(etBc) &&
                    Utility.validateEditText(etCa)) {

                finalIrTest.setAbValue(etAb.getText().toString().trim());
                finalIrTest.setBcValue(etBc.getText().toString().trim());
                finalIrTest.setCaValue(etCa.getText().toString().trim());

                report.setIrTest(finalIrTest);
                Utility.saveReportOngoing(context, report);
                editPopupListener.savedIrTestData();
                dialog.dismiss();

            }


        });

        ivCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // Show dialog
        dialog.show();
    }

    public static void showEditTripPopupLine(Context context, Report report, List<CircuitBreaker> circuitBreakerList,
                                             int index,EditPopupListener editPopupListener) {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.layout_edit_trip_test, null);

        // Get references to views

        EditText etTestAmplitude = customView.findViewById(R.id.etTestAmplitude);
        EditText etTripTime = customView.findViewById(R.id.etTripTime);
        EditText etInstantTrip = customView.findViewById(R.id.etInstantTrip);

        ImageView ivCancel = customView.findViewById(R.id.ivCancel);
        Button btnSave = customView.findViewById(R.id.btnSave);

        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(customView)
                .setCancelable(true)
                .create();

        // Set content
        CircuitBreaker circuitBreaker = circuitBreakerList.get(index);
        TripTest tripTest = circuitBreaker.getTripTest();
        if (tripTest == null)
            tripTest = new TripTest();
        else {

            if (tripTest.getTestAmplitude() != null)
                etTestAmplitude.setText(tripTest.getTestAmplitude());

            if (tripTest.getTripTime() != null)
                etTripTime.setText(tripTest.getTripTime() );

            if (tripTest.getInstantTrip() != null)
                etInstantTrip.setText(tripTest.getInstantTrip() );

        }


        // Set button actions
        TripTest finalTripTest = tripTest;
        btnSave.setOnClickListener(v -> {

            if (Utility.validateEditText(etTestAmplitude) &&
                    Utility.validateEditText(etInstantTrip) &&
                    Utility.validateEditText(etTripTime)) {

                finalTripTest.setTestAmplitude(etTestAmplitude.getText().toString().trim());
                finalTripTest.setTripTime(etTripTime.getText().toString().trim());
                finalTripTest.setInstantTrip(etInstantTrip.getText().toString().trim());

                circuitBreaker.setTripTest(finalTripTest);
                circuitBreakerList.set(index, circuitBreaker);
                Equipment equipment = report.getEquipment();
                equipment.setCircuitBreakerList(circuitBreakerList);
                report.setEquipment(equipment);

                Utility.saveReportOngoing(context, report);
                editPopupListener.savedTripTestData();
                dialog.dismiss();

            }


        });

        ivCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // Show dialog
        dialog.show();
    }
   public static void showEditCrmPopupLine(Context context, Report report, List<CircuitBreaker> circuitBreakerList,
                                           int index,EditPopupListener editPopupListener) {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.layout_edit_crm_test, null);

        // Get references to views

        EditText etRRes = customView.findViewById(R.id.etRRes);
        EditText etYRes = customView.findViewById(R.id.etYRes);
        EditText etBRes = customView.findViewById(R.id.etBRes);

       AppCompatSpinner sp1 = customView.findViewById(R.id.sp1);
       AppCompatSpinner sp2 = customView.findViewById(R.id.sp2);
       AppCompatSpinner sp3 = customView.findViewById(R.id.sp3);

        ImageView ivCancel = customView.findViewById(R.id.ivCancel);
        Button btnSave = customView.findViewById(R.id.btnSave);

        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(customView)
                .setCancelable(true)
                .create();

        // Set content
       CircuitBreaker circuitBreaker = circuitBreakerList.get(index);
       CrmTest crmTest = circuitBreaker.getCrmTest();
        if (crmTest == null)
            crmTest = new CrmTest();
        else {
            Utility.showLog(crmTest.toString());

            if (crmTest.getrResValue() != null)
                etRRes.setText(crmTest.getrResValue());

            if (crmTest.getyResValue() != null)
                etYRes.setText(crmTest.getyResValue());

            if (crmTest.getbResValue() != null)
                etBRes.setText(crmTest.getbResValue());

        }

       ArrayAdapter<String> arrayAdapter;
       String[] unitList = {"mΩ","Ω","µΩ"};

       final String[] rResUnite = {"mΩ"};
       final String[] yResUnite = {"mΩ"};
       final String[] bResUnite = {"mΩ"};


       sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               rResUnite[0] = unitList[i];
               Utility.showLog("rResUnite "+ rResUnite[0]);
           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {
           }
       });



       sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

               yResUnite[0] = unitList[i];
               Utility.showLog("yResUnite "+ yResUnite[0]);
           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {
           }
       });

       sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               bResUnite[0] = unitList[i];
               Utility.showLog("bResUnite "+ bResUnite[0]);
           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {
           }
       });

       arrayAdapter = new ArrayAdapter<>(context, R.layout.item_spinner, unitList);
       arrayAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
       sp1.setAdapter(arrayAdapter);
       sp2.setAdapter(arrayAdapter);
       sp3.setAdapter(arrayAdapter);

       // Set button actions
        CrmTest finalCrmTest = crmTest;
        btnSave.setOnClickListener(v -> {

            if (Utility.validateEditText(etRRes) &&
                    Utility.validateEditText(etYRes) &&
                    Utility.validateEditText(etBRes)) {

                finalCrmTest.setrResValue(etRRes.getText().toString().trim());
                finalCrmTest.setrResUnit( rResUnite[0]);


                finalCrmTest.setyResValue(etYRes.getText().toString().trim());
                finalCrmTest.setyResUnit( yResUnite[0]);


                finalCrmTest.setbResValue(etBRes.getText().toString().trim());
                finalCrmTest.setbResUnit( bResUnite[0]);


                // sava data
                circuitBreaker.setCrmTest(finalCrmTest);
                circuitBreakerList.set(index, circuitBreaker);
                Equipment equipment = report.getEquipment();
                equipment.setCircuitBreakerList(circuitBreakerList);
                report.setEquipment(equipment);

                Utility.saveReportOngoing(context, report);
                editPopupListener.savedCrmTestData();
                dialog.dismiss();
            }


        });

        ivCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // Show dialog
        dialog.show();
    }

    public static void showEditEquipmentPopup(Context context) {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.layout_edit_equipment_details, null);

        // Get references to views
//        TextView titleText = customView.findViewById(R.id.popup_title);
//        TextView messageText = customView.findViewById(R.id.popup_message);
//        Button yesButton = customView.findViewById(R.id.btn_yes);
//        Button noButton = customView.findViewById(R.id.btn_no);

        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(customView)
                .setCancelable(true)
                .create();

        // Set content
//        titleText.setText(R.string.confirm_action);
//        messageText.setText(R.string.are_you_sure_you_want_to_save_circuits);

        // Set button actions
//        yesButton.setOnClickListener(v -> {
//            // YES action
//            Toast.makeText(context, R.string.report_saved_to_ongoing_list, Toast.LENGTH_SHORT).show();
//            confirmCircuitList.confirmed();
//            dialog.dismiss();
//        });

//        noButton.setOnClickListener(v -> {
//            // NO action
////            Toast.makeText(context, "No clicked - Action cancelled", Toast.LENGTH_SHORT).show();
//            confirmCircuitList.cancelled();
//            dialog.dismiss();
//        });

        // Show dialog
        dialog.show();
    }

    private void handleYesAction() {
        // Add your YES action logic here
        // Example: delete item, save data, etc.
    }

    private void handleNoAction() {
        // Add your NO action logic here
        // Example: cancel operation, show different screen, etc.
    }

    public interface ConfirmCircuitList {
        void confirmed();

        void cancelled();

        void saved();

    }

    public interface ConfirmGenerateReport {
        void confirmed(Report report);

        void cancelled();


    }

    public interface EditPopupListener {

        void savedCustomerDetails();
        void savedSiteDetails();
        void savedIrTestData();
        void savedCrmTestData();
        void savedTripTestData();

    }
}
