package com.hasanjaved.reportmate.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.databinding.ActivityHistoryBinding;
import com.hasanjaved.reportmate.fragment.FragmentOngoingReportDetails;
import com.hasanjaved.reportmate.fragment.FragmentReportHistoryList;
import com.hasanjaved.reportmate.fragment.FragmentOngoingReportList;
import com.hasanjaved.reportmate.fragment.FragmentTripTest;
import com.hasanjaved.reportmate.listeners.HistoryFragmentClickListener;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.utility.Utility;

public class HistoryActivity extends AppCompatActivity implements HistoryFragmentClickListener {

    private ActivityHistoryBinding binding;

    private String fragmentName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragmentHolder), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        if (getIntent().hasExtra(Utility.HISTORY_FRAGMENT_TOKEN)) {
            fragmentName = getIntent().getStringExtra(Utility.HISTORY_FRAGMENT_TOKEN);
            Utility.showLog( "\n fragmentName :" + fragmentName);
            if (fragmentName.equals(Utility.HISTORY_FRAGMENT_HISTORY))
                addHistoryFragment();
            else if (fragmentName.equals(Utility.HISTORY_FRAGMENT_ONGOING))
                addOngoingFragment();

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void addHistoryFragment() {

        FragmentReportHistoryList fragment = FragmentReportHistoryList.newInstance("", "");
        fragment.setFragmentClickListener( this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentHolder, fragment,
                        "HistoryFragment")
                .commit();

    }

    private void addOngoingFragment() {

         FragmentOngoingReportList fragment = FragmentOngoingReportList.newInstance("", "");
        fragment.setFragmentClickListener( this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentHolder, fragment,
                        "OngoingFragment")
                .commit();

    }

    @Override
    public void addOngoingReportDetails(Report report) {
        FragmentOngoingReportDetails fragment = FragmentOngoingReportDetails.newInstance("","");
        fragment.setFragmentClickListener(this,report);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder,fragment,"")
                .addToBackStack("")
                .commit();
    }

    @Override
    public void addHistoryReportDetails(Report report) {

    }
}