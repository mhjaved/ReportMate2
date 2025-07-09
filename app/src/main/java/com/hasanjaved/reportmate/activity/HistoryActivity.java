package com.hasanjaved.reportmate.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.databinding.ActivityHistoryBinding;
import com.hasanjaved.reportmate.fragment.FragmentCamera;
import com.hasanjaved.reportmate.fragment.FragmentHistoryReportDetails;
import com.hasanjaved.reportmate.fragment.FragmentOngoingReportDetails;
import com.hasanjaved.reportmate.fragment.FragmentReportHistoryList;
import com.hasanjaved.reportmate.fragment.FragmentOngoingReportList;
import com.hasanjaved.reportmate.listeners.CameraFragmentClickListener;
import com.hasanjaved.reportmate.listeners.HistoryFragmentClickListener;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.utility.PopupManager;
import com.hasanjaved.reportmate.utility.ReportGenerator;
import com.hasanjaved.reportmate.utility.Utility;

public class HistoryActivity extends AppCompatActivity implements HistoryFragmentClickListener , PopupManager.ConfirmGenerateReport {

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

    public void generateReportFile(Report report) {

        String reportName = report.getProjectNo();

        // Show loading dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Generating Report");
        progressDialog.setMessage("Initializing...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Generate report asynchronously
        ReportGenerator.generateReport(this, reportName, report,
                new ReportGenerator.ReportGenerationCallback() {
                    @Override
                    public void onStarted() {
                        Utility.showLog("Report generation started");
                        progressDialog.setMessage("Starting report generation...");
                    }

                    @Override
                    public void onProgress(int progress, String message) {
                        progressDialog.setProgress(progress);
                        progressDialog.setMessage(message);
                        Utility.showLog("Progress: " + progress + "% - " + message);
                    }

                    @Override
                    public void onSuccess(String filePath) {
                        progressDialog.dismiss();
                        Utility.showLog("Report generated successfully: " + filePath);

                        // Show success message
                        Toast.makeText(HistoryActivity.this,
                                "Report saved successfully!",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        progressDialog.dismiss();
                        Utility.showLog("Failed to generate report: " + errorMessage);

                        // Show error message
                        Toast.makeText(HistoryActivity.this,
                                "Failed to generate report: " + errorMessage,
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    @Override
    public void addOngoingReportDetails(int index) {

        FragmentOngoingReportDetails fragment = FragmentOngoingReportDetails.newInstance("","");
        fragment.setFragmentClickListener(this,index);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder,fragment,"")
                .addToBackStack("")
                .commit();
    }

    @Override
    public void addHistoryReportDetails(int index) {

        FragmentHistoryReportDetails fragment = FragmentHistoryReportDetails.newInstance("","");
        fragment.setFragmentClickListener(this,index);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder,fragment,"")
                .addToBackStack("")
                .commit();
    }

    @Override
    public void generateReport(Report report) {
        PopupManager.showConfirmGenerateReport(this,this,report);
    }

    @Override
    public void openCamera(CameraFragmentClickListener cameraFragmentClickListener, ImageView imageView, String imageName, String subFolder) {

        FragmentCamera fragment = FragmentCamera.newInstance("","");
        fragment.setFragmentClickListener(cameraFragmentClickListener,imageView, imageName, subFolder);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder,fragment,"")
                .addToBackStack("")
                .commit();

    }

    private void generateReportWithThread(Report report) {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        Thread thread = new Thread(() -> {
            boolean success = false;
            try {
                generateReportFile(report);
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Post result to main thread
            boolean finalSuccess = success;
            mainHandler.post(() -> {
                if (finalSuccess) {
//                    Toast.makeText(this, "Report generated successfully", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(this, "Failed to generate report", Toast.LENGTH_SHORT).show();
                }
            });
        });

        thread.start();
    }


    @Override
    public void confirmed(Report report) {
        generateReportFile(report);
    }

    @Override
    public void cancelled() {

    }
}