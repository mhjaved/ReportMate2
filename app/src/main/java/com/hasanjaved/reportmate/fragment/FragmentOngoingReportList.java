package com.hasanjaved.reportmate.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
import com.hasanjaved.reportmate.databinding.FragmentOngoingReportsBinding;
import com.hasanjaved.reportmate.listeners.HistoryFragmentClickListener;
import com.hasanjaved.reportmate.listeners.HomeFragmentClickListener;
import com.hasanjaved.reportmate.listeners.RecyclerViewClickListener;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.model.ReportHistory;
import com.hasanjaved.reportmate.model.ReportOngoing;
import com.hasanjaved.reportmate.utility.PopupManager;
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
    final boolean[] isCrossVisible = {false};

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


        binding.ivSearch.setOnClickListener(view -> {

            if (isCrossVisible[0]) {
                // Hide password
//                binding.etSearch.setInputType(null);
                binding.ivSearch.setImageResource(R.drawable.ic_search);
                setReportHistory();
            } else {
                // Show password
//                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                if (getReportList() != null) {
                    setSearchResult(Utility.searchReports(getReportList(), binding.etSearch.getText().toString()));
//                    Utility.showLog(Utility.);

                }
                binding.ivSearch.setImageResource(R.drawable.ic_close);
            }
            // Move cursor to the end
//            etPassword.setSelection(etPassword.length());
            isCrossVisible[0] = !isCrossVisible[0];

        });
    }

    private void setReportHistory() {


        ongoingReportList = new ArrayList<>();

        if (getReportList() != null) {
            ongoingReportList.clear();
            ongoingReportList.addAll(getReportList());
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        binding.rvOngoing.setLayoutManager(linearLayoutManager);
        historyRecyclerAdapter = new HistoryRecyclerAdapter(activity, ongoingReportList, -1, this);
        binding.rvOngoing.setAdapter(historyRecyclerAdapter);

    }

    private void setSearchResult(List<Report> list) {


        if (list != null) {
//                    ongoingReportList= new ArrayList<>();
            ongoingReportList.clear();
            ongoingReportList.addAll(list);

            historyRecyclerAdapter.notifyDataSetChanged();
//                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
//                    binding.rvOngoing.setLayoutManager(linearLayoutManager);
//                    historyRecyclerAdapter = new HistoryRecyclerAdapter(activity,ongoingReportList , -1, this);
//                    binding.rvOngoing.setAdapter(historyRecyclerAdapter);
        } else Utility.showToast(activity, "No report found");

    }

    private List<Report> getReportList() {

        ReportOngoing reportOngoing = Utility.getOngoingReportList(activity);
        if (reportOngoing != null)
            if (reportOngoing.getOngoingReportList() != null) {
                return reportOngoing.getOngoingReportList();
            }
        return null;
    }

    private void closeFragment() {
        try {
            activity.finish();
        } catch (Exception e) {
            Utility.showLog(e.toString());
        }

    }

    public void setFragmentClickListener(HistoryFragmentClickListener fragmentClickListener) {
        this.fragmentClickListener = fragmentClickListener;
    }

    @Override
    public void onItemClicked(int index) {

        if (ongoingReportList.get(index) != null)
            fragmentClickListener.addOngoingReportDetails(index);

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
            Utility.deleteOngoingReport(activity, index);
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