package com.hasanjaved.reportmate.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.adapter.HistoryRecyclerAdapter;
import com.hasanjaved.reportmate.databinding.FragmentHistoryBinding;
import com.hasanjaved.reportmate.listeners.HistoryFragmentClickListener;
import com.hasanjaved.reportmate.listeners.HomeFragmentClickListener;
import com.hasanjaved.reportmate.listeners.RecyclerViewClickListener;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.model.ReportHistory;
import com.hasanjaved.reportmate.model.ReportOngoing;
import com.hasanjaved.reportmate.utility.Utility;

import java.util.ArrayList;
import java.util.List;

public class FragmentReportHistoryList extends Fragment implements RecyclerViewClickListener {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private HistoryFragmentClickListener fragmentClickListener;
    private HistoryRecyclerAdapter historyRecyclerAdapter;

    private FragmentHistoryBinding binding;
    private Activity activity;
    private List<Report> reporHistoryList;


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

    public void setFragmentClickListener(HistoryFragmentClickListener fragmentClickListener) {
        this.fragmentClickListener = fragmentClickListener;
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

        reporHistoryList = new ArrayList<>();

        ReportHistory reportHistory = Utility.getHistoryReportList(activity);
        if (reportHistory != null)
            if (reportHistory.getReportHistoryList() != null)
                if (!reportHistory.getReportHistoryList().isEmpty()){
                    reporHistoryList.clear();
                    reporHistoryList.addAll(reportHistory.getReportHistoryList());
                }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        binding.rvHistory.setLayoutManager(linearLayoutManager);
        historyRecyclerAdapter = new HistoryRecyclerAdapter(activity, reporHistoryList, -1, this);
        binding.rvHistory.setAdapter(historyRecyclerAdapter);


    }

    private void closeFragment() {
        try {
            activity.finish();
        } catch (Exception e) {
            Utility.showLog(e.toString());
        }

    }


    @Override
    public void onItemClicked(int index) {

        if (reporHistoryList.get(index) != null)
            fragmentClickListener.addHistoryReportDetails(index);

    }

    @Override
    public void onEditClicked(int index) {

    }

    private void showConfirmDeletePopup(int index) {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(activity);
        View customView = inflater.inflate(R.layout.popup_layout, null);

        // Get references to views
        TextView titleText = customView.findViewById(R.id.popup_title);
        TextView messageText = customView.findViewById(R.id.popup_message);
        Button yesButton = customView.findViewById(R.id.btn_yes);
        Button noButton = customView.findViewById(R.id.btn_no);

        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(customView)
                .setCancelable(true)
                .create();

        // Set content
        titleText.setText(R.string.confirm_action);
        messageText.setText(R.string.are_you_sure_you_want_to_save_circuits);

        // Set button actions
        yesButton.setOnClickListener(v -> {
            // YES action
//            Toast.makeText(activity, R.string.report_saved_to_ongoing_list, Toast.LENGTH_SHORT).show();
            Utility.deleteHistoryReport(activity, index);
            setReportHistory();
//            historyRecyclerAdapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        noButton.setOnClickListener(v -> {
            // NO action
//            Toast.makeText(context, "No clicked - Action cancelled", Toast.LENGTH_SHORT).show();

            dialog.dismiss();
        });

        // Show dialog
        dialog.show();
    }

    @Override
    public void onDeleteClicked(int index) {
        showConfirmDeletePopup(index);
    }
}