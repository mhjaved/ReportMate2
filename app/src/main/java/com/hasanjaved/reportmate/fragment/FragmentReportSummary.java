package com.hasanjaved.reportmate.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.adapter.CircuitListRecyclerAdapter;
import com.hasanjaved.reportmate.adapter.CrmTestRecyclerAdapter;
import com.hasanjaved.reportmate.adapter.TripTestRecyclerAdapter;
import com.hasanjaved.reportmate.data_manager.ReportGeneralData;
import com.hasanjaved.reportmate.databinding.FragmentReportSummaryBinding;
import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.utility.Utility;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;


public class FragmentReportSummary extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FragmentReportSummaryBinding binding;
    List<CircuitBreaker> circuitBreakerList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView rvCrmList;
    private CrmTestRecyclerAdapter crmTestRecyclerAdapter;
    private TripTestRecyclerAdapter tripTestRecyclerAdapter;
    private Activity activity;

    private View rootView;
    private String mParam1;
    private String mParam2;

    public FragmentReportSummary() {
        // Required empty public constructor
    }

    public static FragmentReportSummary newInstance(String param1, String param2) {
        FragmentReportSummary fragment = new FragmentReportSummary();
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
        // Inflate the layout for this fragment
        binding = FragmentReportSummaryBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();

        setViewListeners();
        setViewListenersIr();
        setCrmListRv();
        setTripListRv();

        return rootView;
    }

    private void setViewListeners() {

        binding.viewSummary.rlCustomerDetails.setOnClickListener(view ->
                setExpandView(binding.viewSummary.expandCustomer, binding.viewSummary.ivArrowCustomer)
        );


        binding.viewSummary.rlSideDetails.setOnClickListener(view ->
                setExpandView(binding.viewSummary.expandSite, binding.viewSummary.ivArrowSite)
        );


        binding.viewSummary.rlEquipment.setOnClickListener(view ->
                setExpandView(binding.viewSummary.expandEquipment, binding.viewSummary.ivArrowEquipment)
        );


    }

    private void setViewListenersIr() {

        //lint to line and line to ground
        binding.viewIrSummary.rlLineToGround.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.expandLineToGround, binding.viewIrSummary.ivArrowLineToGround)
        );

        binding.viewIrSummary.rlLineToLine.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.expandLineToLine, binding.viewIrSummary.ivArrowLineToLine)
        );

        //=================== line to ground inside
        binding.viewIrSummary.groundConnection.connectionOne.rlAConnection.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.groundConnection.connectionOne.expandA, binding.viewIrSummary.groundConnection.connectionOne.ivArrow)
        );
        binding.viewIrSummary.groundConnection.connectionTwo.rlAConnection.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.groundConnection.connectionTwo.expandA, binding.viewIrSummary.groundConnection.connectionTwo.ivArrow)
        );
        binding.viewIrSummary.groundConnection.connectionThree.rlAConnection.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.groundConnection.connectionThree.expandA, binding.viewIrSummary.groundConnection.connectionThree.ivArrow)
        );

        //=================== line to line inside
        binding.viewIrSummary.lineConnection.connectionOne.rlAConnection.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.lineConnection.connectionOne.expandA, binding.viewIrSummary.groundConnection.connectionOne.ivArrow)
        );
        binding.viewIrSummary.lineConnection.connectionTwo.rlAConnection.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.lineConnection.connectionTwo.expandA, binding.viewIrSummary.groundConnection.connectionTwo.ivArrow)
        );
        binding.viewIrSummary.lineConnection.connectionThree.rlAConnection.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.lineConnection.connectionThree.expandA, binding.viewIrSummary.groundConnection.connectionThree.ivArrow)
        );

    }


    private void setCrmListRv() {

        linearLayoutManager = new LinearLayoutManager(activity);
        binding.viewCrmSummary.rvCrm.setLayoutManager(linearLayoutManager);
        crmTestRecyclerAdapter = new CrmTestRecyclerAdapter(activity, circuitBreakerList, 0, null);
        binding.viewCrmSummary.rvCrm.setAdapter(crmTestRecyclerAdapter);

    }

    private void setTripListRv() {

        linearLayoutManager = new LinearLayoutManager(activity);
        binding.viewTripSummary.rvCrm.setLayoutManager(linearLayoutManager);
        binding.viewTripSummary.rvCrm.setNestedScrollingEnabled(true);
        tripTestRecyclerAdapter = new TripTestRecyclerAdapter(activity, circuitBreakerList, 0, null);
        binding.viewTripSummary.rvCrm.setAdapter(tripTestRecyclerAdapter);


    }
    private void setExpandView(ExpandableLayout expand, ImageView arrow) {

        Utility.showLog("000000000");
        if (expand.isExpanded()) {
            arrow.animate().rotation(180).start();
            expand.collapse();
        } else {
            arrow.animate().rotation(0).start();
            expand.expand();
        }

    }

}