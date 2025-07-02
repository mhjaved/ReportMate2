package com.hasanjaved.reportmate.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hasanjaved.reportmate.adapter.HistoryRecyclerAdapter;
import com.hasanjaved.reportmate.databinding.FragmentHistoryBinding;
import com.hasanjaved.reportmate.listeners.HistoryFragmentClickListener;
import com.hasanjaved.reportmate.listeners.HomeFragmentClickListener;
import com.hasanjaved.reportmate.listeners.RecyclerViewClickListener;

public class FragmentReportHistoryList extends Fragment implements RecyclerViewClickListener {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private HistoryFragmentClickListener fragmentClickListener;
    private HistoryRecyclerAdapter historyRecyclerAdapter;

    private FragmentHistoryBinding binding;
    private Activity activity;


    private String mParam1;
    private String mParam2;

    public FragmentReportHistoryList() {

    }


    public static FragmentReportHistoryList newInstance(String param1, String param2) {
        FragmentReportHistoryList fragment = new FragmentReportHistoryList();
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
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
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
            closeFragment();
        });
    }

    private void setReportHistory() {
//        if (Re)

        LinearLayoutManager  linearLayoutManager = new LinearLayoutManager(activity);
        binding.rvHistory.setLayoutManager(linearLayoutManager);
        historyRecyclerAdapter = new HistoryRecyclerAdapter(activity, null, -1, this);
        binding.rvHistory.setAdapter(historyRecyclerAdapter);

    }

    private void closeFragment(){
        getParentFragmentManager().popBackStack();
    }

    public void setFragmentClickListener(HistoryFragmentClickListener fragmentClickListener) {
        this.fragmentClickListener = fragmentClickListener;
    }

    @Override
    public void onItemClicked(int index) {
    }

    @Override
    public void onEditClicked(int index) {

    }

    @Override
    public void onDeleteClicked(int index) {

    }
}