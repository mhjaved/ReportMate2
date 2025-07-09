package com.hasanjaved.reportmate.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.databinding.FragmentHomeBinding;
import com.hasanjaved.reportmate.listeners.HomeFragmentClickListener;
import com.hasanjaved.reportmate.model.Employee;
import com.hasanjaved.reportmate.utility.PopupManager;
import com.hasanjaved.reportmate.utility.Utility;

public class HomeFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private HomeFragmentClickListener homeFragmentClickListener;
    private FragmentHomeBinding binding;
    private Activity activityContext;


    private String mParam1;
    private String mParam2;

    public HomeFragment() {

    }


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        activityContext = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setClickListener();
        setPageData();

        return view;

    }

    private void setClickListener(){
        binding.menuIcon.setOnClickListener(view1 -> {
            if (homeFragmentClickListener != null)
                homeFragmentClickListener.onMenuButtonClicked();
        });

        binding.llGenerateReport.setOnClickListener(view1 -> {
            if (homeFragmentClickListener != null)
                homeFragmentClickListener.onGenerateNewReportClicked();
        });

        binding.llPreviousReport.setOnClickListener(view1 -> {
            if (homeFragmentClickListener != null)
                homeFragmentClickListener.onPreviousReportHistoryClicked();
        });

        binding.llOngoingReport.setOnClickListener(view1 -> {
//            showYesNoPopup();
            if (homeFragmentClickListener != null)
                homeFragmentClickListener.onOngoingReportClicked();
        });

    }

    private void setPageData() {

        Employee employee = Utility.getEmployee(activityContext);
        if (employee != null){
            if (employee.getEmployeeId() != null)
                binding.tvEmployeeId.setText(employee.getEmployeeId());

            if (employee.getEmployeeName() != null)
                binding.tvUserName.setText(employee.getEmployeeName());
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        activityContext=null;
    }

    public void setFragmentClickListener(HomeFragmentClickListener homeFragmentClickListener) {

        this.homeFragmentClickListener = homeFragmentClickListener;
    }

}