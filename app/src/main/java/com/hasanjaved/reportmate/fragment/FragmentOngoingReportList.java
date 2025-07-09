package com.hasanjaved.reportmate.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hasanjaved.reportmate.adapter.HistoryRecyclerAdapter;
import com.hasanjaved.reportmate.databinding.FragmentOngoingReportsBinding;
import com.hasanjaved.reportmate.listeners.HistoryFragmentClickListener;
import com.hasanjaved.reportmate.listeners.HomeFragmentClickListener;
import com.hasanjaved.reportmate.listeners.RecyclerViewClickListener;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.model.ReportOngoing;
import com.hasanjaved.reportmate.utility.Utility;

import java.util.ArrayList;
import java.util.List;

public class FragmentOngoingReportList extends Fragment implements RecyclerViewClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private HistoryFragmentClickListener fragmentClickListener;
    private FragmentOngoingReportsBinding binding;
    private Activity activity;
    private HistoryRecyclerAdapter historyRecyclerAdapter;
    private List<Report> ongoingReportList;

    private String mParam1;
    private String mParam2;

    public FragmentOngoingReportList() {

    }


    public static FragmentOngoingReportList newInstance(String param1, String param2) {
        FragmentOngoingReportList fragment = new FragmentOngoingReportList();
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
        binding = FragmentOngoingReportsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setViewListeners();
        setReportHistory();

        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setViewListeners() {
        binding.ivBack.setOnClickListener(view -> {
            Utility.showLog("back");
            closeFragment();
        });
    }

    private void setReportHistory() {

        ReportOngoing reportOngoing = Utility.getOngoingReportList(activity);
        if (reportOngoing!=null)
            if (reportOngoing.getOngoingReportList()!=null)
                if (!reportOngoing.getOngoingReportList().isEmpty()){
                    ongoingReportList= new ArrayList<>();
                    ongoingReportList.addAll(reportOngoing.getOngoingReportList());

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
                    binding.rvOngoing.setLayoutManager(linearLayoutManager);
                    historyRecyclerAdapter = new HistoryRecyclerAdapter(activity,ongoingReportList , -1, this);
                    binding.rvOngoing.setAdapter(historyRecyclerAdapter);
                }

    }

    private void closeFragment(){
        try {
            activity.finish();
        }catch (Exception e){
            Utility.showLog(e.toString());
        }

    }

    public void setFragmentClickListener(HistoryFragmentClickListener fragmentClickListener) {
        this.fragmentClickListener = fragmentClickListener;
    }

    @Override
    public void onItemClicked(int index) {

        if (ongoingReportList.get(index)!=null)
            fragmentClickListener.addOngoingReportDetails(index);

    }

    @Override
    public void onEditClicked(int index) {

    }

    @Override
    public void onDeleteClicked(int index) {

    }
}