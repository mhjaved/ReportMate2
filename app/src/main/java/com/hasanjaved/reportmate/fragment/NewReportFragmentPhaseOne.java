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
import androidx.annotation.RequiresApi;
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
import com.hasanjaved.reportmate.utility.FileMover2;
import com.hasanjaved.reportmate.utility.ImageLoader;
import com.hasanjaved.reportmate.utility.not.FolderManager;
import com.hasanjaved.reportmate.utility.Utility;

import java.util.Calendar;


public class NewReportFragmentPhaseOne extends Fragment implements CameraFragmentClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static EditText etDayOne, etMonthOne, etYearOne, etDayThree, etMonthThree, etYearThree;
    private View rootView, viewOne, viewTwo, viewThree, viewFour, viewFive;
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

        viewOne = rootView.findViewById(R.id.viewOne);
        viewTwo = rootView.findViewById(R.id.viewTwo);
        viewThree = rootView.findViewById(R.id.viewThree);
        viewFour = rootView.findViewById(R.id.viewFour);
        viewFive = rootView.findViewById(R.id.viewFive);

        etDayOne = rootView.findViewById(R.id.etDay);
        etMonthOne = rootView.findViewById(R.id.etMonth);
        etYearOne = rootView.findViewById(R.id.etYear);

        etDayThree = rootView.findViewById(R.id.etDayThree);
        etMonthThree = rootView.findViewById(R.id.etMonthThree);
        etYearThree = rootView.findViewById(R.id.etYearThree);

        // Basic usage - load img.jpg from Documents/ReportMate/
//        ImageLoader.loadImageFromReportMate(activity, binding.viewFour.imgCamera, "img.jpg");


        setData();

//        FileMover fileMover = new FileMover(activity);
//         fileMover.moveImageFile(Utility.IMAGE_SAMPLE_DIRECTORY, Utility.IMAGE_SAMPLE_DIRECTORY, "temperatureImage", true);

        binding.viewOne.ivCalendar.setOnClickListener(view -> {
            DatePickerFragment1 newFragment = new DatePickerFragment1();
            newFragment.show(getChildFragmentManager(), "datePicker");
        });

        binding.viewThree.ivCalendar.setOnClickListener(view -> {
            DatePickerFragment2 newFragment = new DatePickerFragment2();
            newFragment.show(getChildFragmentManager(), "datePicker");
        });

        binding.viewOne.btnNext.setOnClickListener(view -> {
                    savePageOneData();
                    showPage(viewTwo, viewOne, viewThree, viewFour, viewFive);
                }
        );

        binding.viewTwo.btnNext.setOnClickListener(view -> {

            if (binding.viewTwo.etCustomerName.getText().toString().isEmpty()) {
                Utility.showToast(activity, "Provide Customer Name");
            }else if (binding.viewTwo.etCustomerAddress.getText().toString().isEmpty()){
                Utility.showToast(activity, "Provide Customer Address");
            }else if (binding.viewTwo.etUserName.getText().toString().isEmpty()){
                Utility.showToast(activity, "Provide User Name");
            }else if (binding.viewTwo.etUserAddress.getText().toString().isEmpty()){
                Utility.showToast(activity, "Provide User Address");
            }else {
                savePageTwoData();
                showPage(viewThree, viewOne, viewTwo, viewFour, viewFive);
            }
                }
        );

        binding.viewThree.btnNext.setOnClickListener(view -> {
            if (binding.viewThree.etEquipmentName.getText().toString().isEmpty()) {
                Utility.showToast(activity, "Provide Equipment Name");
            }else if (binding.viewThree.etEquipmentLocation.getText().toString().isEmpty()){
                Utility.showToast(activity, "Provide Equipment Location");
            }else {
                savePageThreeData();
                showPage(viewFour, viewOne, viewTwo, viewThree, viewFive);
            }


                }
        );

        binding.viewFour.btnNext.setOnClickListener(view -> {
                    savePageFourData();
                    showPageFiveDate();
                    showPage(viewFive, viewOne, viewTwo, viewThree, viewFour);
                }
        );


        binding.viewFive.btnNext.setOnClickListener(view ->
                {
                    if (fragmentClickListener != null) {
                        fragmentClickListener.addNewReportPhaseTwoFragment();
                    }
                }
        );

        binding.viewFour.imgCamera.setOnClickListener(view ->{

//            getImagePermission();
                    fragmentClickListener.openCamera(this, binding.viewFour.ivShowImage,
                            Utility.generalImageTemperature, Utility.getReportDirectory(activity));
                }

        );

        binding.viewFive.imgCamera1.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewFive.ivShowImage1,
                        Utility.dbBoxPanelFront, Utility.getReportDirectory(activity))
        );
        binding.viewFive.imgCamera2.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewFive.ivShowImage2,
                        Utility.dbBoxPanelInside, Utility.getReportDirectory(activity))
        );
        binding.viewFive.imgCamera3.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewFive.ivShowImage3,
                        Utility.dbBoxPanelNameplate, Utility.getReportDirectory(activity))
        );
        binding.viewFive.imgCamera4.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewFive.ivShowImage4,
                        Utility.dbBoxPanelGrounging, Utility.getReportDirectory(activity))
        );


        binding.viewOne.ivBack.setOnClickListener(view -> {
                    try {
                        activity.finish();
                    } catch (Exception e) {
                        Utility.showLog(e.toString());
                    }
                }
        );

        binding.viewTwo.ivBack.setOnClickListener(view ->
                showPage(viewOne, viewTwo,
                        viewThree, viewFour, viewFive));

        binding.viewThree.ivBack.setOnClickListener(view ->
                showPage(viewTwo, viewOne,
                        viewThree, viewFour, viewFive));

        binding.viewFour.ivBack.setOnClickListener(view ->
                showPage(viewThree, viewTwo, viewOne,
                        viewFour, viewFive));

        binding.viewFive.ivBack.setOnClickListener(view ->
                showPage(viewFour, viewTwo, viewOne,
                        viewThree, viewFive));

        Utility.showLog("doesReportMateFolderExist " + FolderManager.doesFolderExist(activity, "ReportMate"));

        return binding.getRoot();

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
            binding.viewFive.etEquipmentName.setText(report.getEquipment().getEquipmentName());
            binding.viewFive.etEquipmentLocation.setText(report.getEquipment().getEquipmentLocation());
        }catch (Exception e){
            Utility.showLog(e.toString());
        }
    }

    private void savePageOneData() {
        ReportGeneralData.savePageOneData(activity,
                binding.viewOne.etEmployeeId.getText().toString().trim(),
                etDayOne.getText() + "." + etMonthOne.getText() + "." + etYearOne.getText(),
                binding.viewOne.etProjectName.getText().toString()
        );
        Utility.showLog(Utility.getReport(activity).toString());
    }



    private void savePageTwoData() {
        ReportGeneralData.savePageTwoData(activity,
                binding.viewTwo.etCustomerName.getText().toString().trim(),
                binding.viewTwo.etCustomerAddress.getText().toString().trim(),
                binding.viewTwo.etUserName.getText().toString().trim(),
                binding.viewTwo.etUserAddress.getText().toString().trim()
        );
        Utility.showLog(Utility.getReport(activity).toString());
    }

    private void savePageThreeData() {
        ReportGeneralData.savePageThreeData(activity,
                binding.viewThree.etEquipmentName.getText().toString().trim(),
                binding.viewThree.etEquipmentLocation.getText().toString().trim(),
                binding.viewThree.etOwnerId.getText().toString().trim(),
                etDayThree.getText() + "." + etMonthThree.getText() + "." + etYearThree.getText(),
                binding.viewThree.etLastInspectionNo.getText().toString().trim()
        );
        Utility.showLog(Utility.getReport(activity).toString());
    }

    private void savePageFourData() {
        ReportGeneralData.savePageFourData(activity,
                binding.viewFour.airTempInput.getText().toString().trim(),
                binding.viewFour.relHumidityInput.getText().toString().trim());
        Utility.showLog(Utility.getReport(activity).toString());
    }

    private void setData() {
        Employee employee = Utility.getEmployee(activity);
        if (employee != null) {
            if (employee.getEmployeeId() != null)
                binding.viewOne.etEmployeeId.setText(employee.getEmployeeId());
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

            FileMover2.moveImageToDocumentsSubfolder(
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