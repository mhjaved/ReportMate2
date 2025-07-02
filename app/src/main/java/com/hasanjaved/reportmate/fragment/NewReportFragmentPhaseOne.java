package com.hasanjaved.reportmate.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hasanjaved.reportmate.data_manager.ReportGeneralData;
import com.hasanjaved.reportmate.databinding.FragmentNewReportPhaseOneBinding;
import com.hasanjaved.reportmate.listeners.CameraFragmentClickListener;
import com.hasanjaved.reportmate.listeners.FragmentClickListener;
import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.model.Employee;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.utility.FileMover;
import com.hasanjaved.reportmate.utility.Utility;

import java.util.Calendar;

public class NewReportFragmentPhaseOne extends Fragment implements CameraFragmentClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static EditText etDayOne, etMonthOne, etYearOne, etDayThree, etMonthThree, etYearThree;
    private View rootView, viewGenerateNewReport, viewCustomerDetails, viewSiteDetails, viewGeneralImage, viewGeneralImage2;
    private FragmentNewReportPhaseOneBinding binding;
    private String mParam1;
    private String mParam2;
    private Activity activity;

    public NewReportFragmentPhaseOne() {
    }

    private FragmentClickListener fragmentClickListener;

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
                if (result) {
//                    startCamera(cameraFacing);
                } else {
                    Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    public void setFragmentClickListener(FragmentClickListener fragmentClickListener) {
        this.fragmentClickListener = fragmentClickListener;
    }

    public static NewReportFragmentPhaseOne newInstance(String param1, String param2) {
        NewReportFragmentPhaseOne fragment = new NewReportFragmentPhaseOne();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        activity = getActivity();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewReportPhaseOneBinding.inflate(inflater, container, false);

        rootView = binding.getRoot();

        checkAndRequestReadMediaImagesPermission();

        viewGenerateNewReport = rootView.findViewById(R.id.viewGenerateNewReport);
        viewCustomerDetails = rootView.findViewById(R.id.viewCustomerDetails);
        viewSiteDetails = rootView.findViewById(R.id.viewSiteDetails);
        viewGeneralImage = rootView.findViewById(R.id.viewGeneralImage);
        viewGeneralImage2 = rootView.findViewById(R.id.viewGeneralImage2);

        etDayOne = rootView.findViewById(R.id.etDay);
        etMonthOne = rootView.findViewById(R.id.etMonth);
        etYearOne = rootView.findViewById(R.id.etYear);

        etDayThree = rootView.findViewById(R.id.etDayThree);
        etMonthThree = rootView.findViewById(R.id.etMonthThree);
        etYearThree = rootView.findViewById(R.id.etYearThree);

        // Basic usage - load img.jpg from Documents/ReportMate/
//        ImageLoader.loadImageFromReportMate(activity, binding.viewGeneralImage.imgCamera, "img.jpg");


        setData();

//        FileMover fileMover = new FileMover(activity);
//         fileMover.moveImageFile(Utility.IMAGE_SAMPLE_DIRECTORY, Utility.IMAGE_SAMPLE_DIRECTORY, "temperatureImage", true);

        binding.viewGenerateNewReport.ivCalendar.setOnClickListener(view -> {
            DatePickerFragment1 newFragment = new DatePickerFragment1();
            newFragment.show(getChildFragmentManager(), "datePicker");
        });

        binding.viewSiteDetails.ivCalendar.setOnClickListener(view -> {
            DatePickerFragment2 newFragment = new DatePickerFragment2();
            newFragment.show(getChildFragmentManager(), "datePicker");
        });

        binding.viewGenerateNewReport.btnNext.setOnClickListener(view -> {
                    savePageOneData();
                    showPage(viewCustomerDetails, viewGenerateNewReport, viewSiteDetails, viewGeneralImage, viewGeneralImage2);
                }
        );

        binding.viewCustomerDetails.btnNext.setOnClickListener(view -> {

            if(checkCustomerDetailsData()){
                savePageTwoData();
                showPage(viewSiteDetails, viewGenerateNewReport, viewCustomerDetails, viewGeneralImage, viewGeneralImage2);
            }
                }
        );



        binding.viewSiteDetails.btnNext.setOnClickListener(view -> {
            if (binding.viewSiteDetails.etEquipmentName.getText().toString().isEmpty()) {
                Utility.showToast(activity, "Provide Equipment Name");
            }else if (binding.viewSiteDetails.etEquipmentLocation.getText().toString().isEmpty()){
                Utility.showToast(activity, "Provide Equipment Location");
            }else {
                savePageThreeData();
                showPage(viewGeneralImage, viewGenerateNewReport, viewCustomerDetails, viewSiteDetails, viewGeneralImage2);
            }


                }
        );

        binding.viewGeneralImage.btnNext.setOnClickListener(view -> {
                    savePageFourData();
                    showPageFiveDate();
                    showPage(viewGeneralImage2, viewGenerateNewReport, viewCustomerDetails, viewSiteDetails, viewGeneralImage);
                }
        );


        binding.viewGeneralImage2.btnNext.setOnClickListener(view ->
                {
                    if (fragmentClickListener != null) {
                        fragmentClickListener.addNewReportPhaseTwoFragment();
                    }
                }
        );

        binding.viewGeneralImage.imgCamera.setOnClickListener(view ->{

//            getImagePermission();
                    fragmentClickListener.openCamera(this, binding.viewGeneralImage.ivShowImage,
                            Utility.generalImageTemperature, Utility.getReportDirectory(activity));
                }

        );

        binding.viewGeneralImage2.imgCamera1.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewGeneralImage2.ivShowImage1,
                        Utility.dbBoxPanelFront, Utility.getReportDirectory(activity))
        );
        binding.viewGeneralImage2.imgCamera2.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewGeneralImage2.ivShowImage2,
                        Utility.dbBoxPanelInside, Utility.getReportDirectory(activity))
        );
        binding.viewGeneralImage2.imgCamera3.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewGeneralImage2.ivShowImage3,
                        Utility.dbBoxPanelNameplate, Utility.getReportDirectory(activity))
        );
        binding.viewGeneralImage2.imgCamera4.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewGeneralImage2.ivShowImage4,
                        Utility.dbBoxPanelGrounging, Utility.getReportDirectory(activity))
        );


        binding.viewGenerateNewReport.ivBack.setOnClickListener(view -> {
                    try {
                        activity.finish();
                    } catch (Exception e) {
                        Utility.showLog(e.toString());
                    }
                }
        );

        binding.viewCustomerDetails.ivBack.setOnClickListener(view ->
                showPage(viewGenerateNewReport, viewCustomerDetails,
                        viewSiteDetails, viewGeneralImage, viewGeneralImage2));

        binding.viewSiteDetails.ivBack.setOnClickListener(view ->
                showPage(viewCustomerDetails, viewGenerateNewReport,
                        viewSiteDetails, viewGeneralImage, viewGeneralImage2));

        binding.viewGeneralImage.ivBack.setOnClickListener(view ->
                showPage(viewSiteDetails, viewCustomerDetails, viewGenerateNewReport,
                        viewGeneralImage, viewGeneralImage2));

        binding.viewGeneralImage2.ivBack.setOnClickListener(view ->
                showPage(viewGeneralImage, viewCustomerDetails, viewGenerateNewReport,
                        viewSiteDetails, viewGeneralImage2));


        return binding.getRoot();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public boolean validateEditText(EditText editText) {
        boolean allFilled = true;

//        for (EditText editText : editTexts) {
            String text = editText.getText().toString().trim();
            if (text.isEmpty()) {
                editText.setError("This field cannot be empty");
                allFilled = false;
            } else {
                editText.setError(null); // Clear previous error
            }
//        }

        return allFilled;
    }


    private boolean checkCustomerDetailsData() {
        boolean allFilled = true;

        if (!validateEditText(binding.viewCustomerDetails.etCustomerName)) {
            return false;
//            Utility.showToast(activity, "Provide Customer Name");
        }else if (!validateEditText(binding.viewCustomerDetails.etCustomerAddress)){
           return false;
//            Utility.showToast(activity, "Provide Customer Address");
        }else if (!validateEditText(binding.viewCustomerDetails.etUserName)){
            return false;
//            Utility.showToast(activity, "Provide User Name");
        }else if (!validateEditText(binding.viewCustomerDetails.etUserAddress)){
            return false;
//            Utility.showToast(activity, "Provide User Address");
        }

        return allFilled;
    }

    private boolean checkEditTextData(EditText et,String toastMessage){
        if (et.getText().toString().isEmpty()) {
            et.setError("This field can not be blank");
//            Utility.showToast(activity, "Provide Customer Name");
        }

        return false;
    }

//    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
//    private void getImagePermission(){
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
//                != PackageManager.PERMISSION_GRANTED) {
//            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
//        } else {
////            startCamera(cameraFacing);
//        }
//    }


    private static final int REQUEST_READ_MEDIA_IMAGES = 101;

    private void checkAndRequestReadMediaImagesPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)) {
                    // Optional: Show a custom explanation dialog here before requesting
                    requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                            REQUEST_READ_MEDIA_IMAGES);
                } else {
                    // Request permission directly
                    requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                            REQUEST_READ_MEDIA_IMAGES);
                }
            } else {
                // Permission already granted, proceed with your logic
            }
        } else {
            // For older versions, use READ_EXTERNAL_STORAGE if needed
        }
    }

    private void showPageFiveDate(){
        try {
            Report report = Utility.getReport(activity);
            binding.viewGeneralImage2.etEquipmentName.setText(report.getEquipment().getEquipmentName());
            binding.viewGeneralImage2.etEquipmentLocation.setText(report.getEquipment().getEquipmentLocation());
        }catch (Exception e){
            Utility.showLog(e.toString());
        }
    }

    private void savePageOneData() {
        ReportGeneralData.savePageOneData(activity,
                binding.viewGenerateNewReport.etEmployeeId.getText().toString().trim(),
                etDayOne.getText() + "." + etMonthOne.getText() + "." + etYearOne.getText(),
                binding.viewGenerateNewReport.etProjectName.getText().toString()
        );
        Utility.showLog(Utility.getReport(activity).toString());
    }



    private void savePageTwoData() {
        ReportGeneralData.savePageTwoData(activity,
                binding.viewCustomerDetails.etCustomerName.getText().toString().trim(),
                binding.viewCustomerDetails.etCustomerAddress.getText().toString().trim(),
                binding.viewCustomerDetails.etUserName.getText().toString().trim(),
                binding.viewCustomerDetails.etUserAddress.getText().toString().trim()
        );
        Utility.showLog(Utility.getReport(activity).toString());
    }

    private void savePageThreeData() {
        ReportGeneralData.savePageThreeData(activity,
                binding.viewSiteDetails.etEquipmentName.getText().toString().trim(),
                binding.viewSiteDetails.etEquipmentLocation.getText().toString().trim(),
                binding.viewSiteDetails.etOwnerId.getText().toString().trim(),
                etDayThree.getText() + "." + etMonthThree.getText() + "." + etYearThree.getText(),
                binding.viewSiteDetails.etLastInspectionNo.getText().toString().trim()
        );
        Utility.showLog(Utility.getReport(activity).toString());
    }

    private void savePageFourData() {
        ReportGeneralData.savePageFourData(activity,
                binding.viewGeneralImage.airTempInput.getText().toString().trim(),
                binding.viewGeneralImage.relHumidityInput.getText().toString().trim());
        Utility.showLog(Utility.getReport(activity).toString());
    }

    private void setData() {
        Employee employee = Utility.getEmployee(activity);
        if (employee != null) {
            if (employee.getEmployeeId() != null)
                binding.viewGenerateNewReport.etEmployeeId.setText(employee.getEmployeeId());
        }
    }

    private void showPage(View visible, View hide1, View hide2, View hide3, View hide4) {
        visible.setVisibility(View.VISIBLE);
        hide1.setVisibility(View.GONE);
        hide2.setVisibility(View.GONE);
        hide3.setVisibility(View.GONE);
        hide4.setVisibility(View.GONE);
    }

    @Override
    public void onCancelPressed() {

    }

    @Override
    public void onSaveButtonPressed(ImageView imageView, String imageLocation, String imageName, String subFolder) {

        if (!imageLocation.equals("")) {
            imageView.setVisibility(View.VISIBLE);
            Glide.with(activity)
                    .load(Uri.parse("file:" + imageLocation))
                    .into(imageView);

            FileMover.moveImageToDocumentsSubfolder(
                    activity,
                    imageLocation,
                    imageName,
                    subFolder
            );
        }

    }

    public static class DatePickerFragment1 extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(getContext(),

                    this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));

            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

            return datePicker;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Utility.showLog("year " + year + " month " + month + " day " + day);

            etDayOne.setText(String.valueOf(day));
            etMonthOne.setText(String.valueOf(month + 1));
            etYearOne.setText(String.valueOf(year));


        }
    }

    public static class DatePickerFragment2 extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(getContext(),

                    this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));

            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

            return datePicker;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Utility.showLog("year " + year + " month " + month + " day " + day);
            etDayThree.setText(String.valueOf(day));
            etMonthThree.setText(String.valueOf(month + 1));
            etYearThree.setText(String.valueOf(year));
        }
    }

}