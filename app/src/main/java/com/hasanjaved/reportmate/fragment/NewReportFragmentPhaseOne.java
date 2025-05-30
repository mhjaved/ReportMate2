package com.hasanjaved.reportmate.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.hasanjaved.reportmate.data_manager.ReportGeneralData;
import com.hasanjaved.reportmate.databinding.FragmentNewReportPhaseOneBinding;
import com.hasanjaved.reportmate.listeners.FragmentClickListener;
import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.model.Employee;
import com.hasanjaved.reportmate.utility.FolderManager;
import com.hasanjaved.reportmate.utility.Utility;

import java.util.Calendar;


public class NewReportFragmentPhaseOne extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static EditText etDay, etMonth, etYear;
    private View rootView, viewOne, viewTwo, viewThree, viewFour, viewFive;
    private FragmentNewReportPhaseOneBinding binding;
    private String mParam1;
    private String mParam2;
    private Activity activity;

    public NewReportFragmentPhaseOne() {
    }

    private FragmentClickListener fragmentClickListener;

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


        viewOne = rootView.findViewById(R.id.viewOne);
        viewTwo = rootView.findViewById(R.id.viewTwo);
        viewThree = rootView.findViewById(R.id.viewThree);
        viewFour = rootView.findViewById(R.id.viewFour);
        viewFive = rootView.findViewById(R.id.viewFive);

        etDay = rootView.findViewById(R.id.etDay);
        etMonth = rootView.findViewById(R.id.etMonth);
        etYear = rootView.findViewById(R.id.etYear);

        setData();

        binding.viewOne.ivCalendar.setOnClickListener(view -> {
            DatePickerFragment4 newFragment = new DatePickerFragment4();
            newFragment.show(getChildFragmentManager(), "datePicker");
        });

        binding.viewOne.btnNext.setOnClickListener(view -> {

                    saveData();
                    showPage(viewTwo, viewOne,
                            viewThree, viewFour, viewFive);
                }
        );

        binding.viewTwo.btnNext.setOnClickListener(view ->
                showPage(viewThree, viewOne,
                        viewTwo, viewFour, viewFive));

        binding.viewThree.btnNext.setOnClickListener(view ->
                showPage(viewFour, viewOne,
                        viewTwo, viewThree, viewFive));

        binding.viewFour.btnNext.setOnClickListener(view ->
                showPage(viewFive, viewOne,
                        viewTwo, viewThree, viewFour));


        binding.viewFive.btnNext.setOnClickListener(view ->
                {
                    if (fragmentClickListener != null) {
                        fragmentClickListener.addNewReportPhaseTwoFragment();
                    }
                }
        );

        binding.viewFour.imgCamera.setOnClickListener(view ->
                fragmentClickListener.openCamera()
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

        Utility.showLog("doesReportMateFolderExist " + FolderManager.doesReportMateFolderExist(activity, "ReportMate"));

        return binding.getRoot();
    }

    private void saveData() {
        ReportGeneralData.savePageOneData(activity,
                binding.viewOne.etEmployeeId.getText().toString().trim(),
                etDay.getText() + "." + etMonth.getText() + "." + etYear.getText(),
                binding.viewOne.etProjectName.getText().toString()
        );
//        String test = FolderManager.createReportMateFolder(activity,binding.viewOne.etProjectName.getText().toString());
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


    public static class DatePickerFragment4 extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        //        R.style.DatePickerDialogStyle,
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

        //calendar.get(Calendar.YEAR)
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Utility.showLog("year " + year + " month " + month + " day " + day);
            etDay.setText(String.valueOf(day));
            etMonth.setText(String.valueOf(month + 1));
            etYear.setText(String.valueOf(year));
        }
    }

}