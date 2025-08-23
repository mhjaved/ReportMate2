package com.hasanjaved.reportmate.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.fragment.FragmentCamera2;
import com.hasanjaved.reportmate.utility.ReportGenerator;
import com.hasanjaved.reportmate.fragment.FragmentCamera;
import com.hasanjaved.reportmate.fragment.FragmentCrmTest;
import com.hasanjaved.reportmate.fragment.FragmentTripTest;
import com.hasanjaved.reportmate.fragment.NewReportFragmentPhaseOne;
import com.hasanjaved.reportmate.fragment.FragmentCrmTripTest;
import com.hasanjaved.reportmate.fragment.FragmentIRTest;
import com.hasanjaved.reportmate.fragment.NewReportFragmentPhaseTwo;
import com.hasanjaved.reportmate.fragment.FragmentReportSummary;
import com.hasanjaved.reportmate.listeners.CameraFragmentClickListener;
import com.hasanjaved.reportmate.listeners.FragmentClickListener;
import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.utility.Utility;

import java.util.concurrent.CompletableFuture;

public class NewReportActivity extends AppCompatActivity implements FragmentClickListener {

    private ProgressDialog progressDialog;
    private Handler mainHandler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        Utility.saveReport(this,null);
        addNewReportFragment();

        mainHandler = new Handler(Looper.getMainLooper());
        
//        generateSampleDocument();
//        addNewReportFragmentPhaseThreeIR();
//        addNewReportFragmentPhaseTwo();
//        addNewReportPhaseTwoFragment();
//        addNewReportPhaseThreeFragment();
//        addNewReportFragmentPhaseThreeCrmTrip();
//        addFragmentCamera();
//        createFile();
//        addNewReportFragmentPhaseThreeCrmTrip();

//        generateReport(Utility.getReport(this));
//        addFragmentReportSummary();

//        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
//        String fileLocation = "file:"+preferences.getString(Utility.ImageToken,"hasan");

//        // Check permissions first
//        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//            }, 1);
//        } else {
//            generateSampleDocument();
//        }


    }


    private void addNewReportFragment(){

        NewReportFragmentPhaseOne fragment = NewReportFragmentPhaseOne.newInstance("","");
        fragment.setFragmentClickListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentHolder,fragment,"")
                .commit();

    }

//    public void generateReportFile(Report report) {
//        String reportName = report.getProjectNo();
//
//        boolean success = ReportGenerator2.generateReport(this, reportName,report);
//
//        if (success) {
//          Utility.showLog( " report generated successfully");
//            // File saved to: /storage/emulated/0/Documents/ReportMateReports/
//        } else {
//            Utility.showLog(  "Failed to generate  report");
//        }
//    }

    public void generateReportFile(Report report) {
        String reportName = report.getEquipment().getEquipmentName();

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
                        Toast.makeText(NewReportActivity.this,
                                "Report saved successfully!",
                                Toast.LENGTH_LONG).show();

                        // Optional: Open the file or show notification
                        // showReportCompletedNotification(filePath);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        progressDialog.dismiss();
                        Utility.showLog("Failed to generate report: " + errorMessage);

                        // Show error message
                        Toast.makeText(NewReportActivity.this,
                                "Failed to generate report: " + errorMessage,
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
    private void addNewReportFragmentPhaseTwo(){

        NewReportFragmentPhaseTwo fragment = NewReportFragmentPhaseTwo.newInstance("","");
        fragment.setFragmentClickListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder,fragment,"")
                .addToBackStack("")
                .commit();

    }

  private void addFragmentReportSummary(){

        FragmentReportSummary fragment = FragmentReportSummary.newInstance("","");
//        fragment.setFragmentClickListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder,fragment,"")
                .addToBackStack("")
                .commit();

    }


    private void addFragmentCamera(){



    }

    private void generateReportAsync(String reportName, Report report) {
//        if (isGenerating) {
//            Toast.makeText(this, "Report generation already in progress", Toast.LENGTH_SHORT).show();
//            return;
//        }

//        isGenerating = true;
//        generateButton.setEnabled(false);
        showProgressDialog();

        CompletableFuture.runAsync(() -> {
            try {
                ReportGenerator.generateReport(this, reportName, report,
                        new ReportGenerator.ReportGenerationCallback() {
                            @Override
                            public void onStarted() {
                                mainHandler.post(() -> {
                                    updateProgress(0, "Starting report generation...");
                                });
                            }

                            @Override
                            public void onProgress(int progress, String message) {
                                mainHandler.post(() -> {
                                    updateProgress(progress, message);
                                });
                            }

                            @Override
                            public void onSuccess(String filePath) {
                                mainHandler.post(() -> {
                                    hideProgressDialog();
//                                    generateButton.setEnabled(true);
//                                    isGenerating = false;
                                    Toast.makeText(NewReportActivity.this,
                                            "Report generated successfully!", Toast.LENGTH_LONG).show();
                                });
                            }

                            @Override
                            public void onError(String errorMessage) {
                                mainHandler.post(() -> {
                                    hideProgressDialog();
//                                    generateButton.setEnabled(true);
//                                    isGenerating = false;
                                    Toast.makeText(NewReportActivity.this,
                                            "Error generating report: " + errorMessage, Toast.LENGTH_LONG).show();
                                });
                            }
                        });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    hideProgressDialog();
//                    generateButton.setEnabled(true);
//                    isGenerating = false;
                    Toast.makeText(NewReportActivity.this,
                            "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).exceptionally(throwable -> {
            mainHandler.post(() -> {
                hideProgressDialog();
//                generateButton.setEnabled(true);
//                isGenerating = false;
                Toast.makeText(NewReportActivity.this,
                        "Unexpected error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            });
            return null;
        });
    }
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Generating Report");
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void updateProgress(int progress, String message) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setProgress(progress);
            progressDialog.setMessage(message);
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    @Override
    public void addNewReportPhaseTwoFragment() {

        NewReportFragmentPhaseTwo fragment = NewReportFragmentPhaseTwo.newInstance("","");
        fragment.setFragmentClickListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder,fragment,"")
                .addToBackStack("")
                .commit();

    }

    @Override
    public void addNewReportFragmentPhaseThreeIR() {

        Utility.showLog("addNewReportFragmentPhaseThreeIR");

        FragmentIRTest fragment = FragmentIRTest.newInstance("","");
        fragment.setFragmentClickListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder,fragment,"")
                .addToBackStack("")
                .commit();

    }

    FragmentCrmTripTest fragmentCrmTripTest;
    @Override
    public void addNewReportFragmentPhaseThreeCrmTrip(){

        fragmentCrmTripTest = FragmentCrmTripTest.newInstance("","");
        fragmentCrmTripTest.setFragmentClickListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder,fragmentCrmTripTest,"")
                .addToBackStack("")
                .commit();

    }

    @Override
    public void addSummaryReportFragment() {

        FragmentReportSummary fragment = FragmentReportSummary.newInstance("","");
        fragment.setFragmentClickListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder,fragment,"")
                .addToBackStack("")
                .commit();

    }

    @Override
    public void generateReport(Report report) {
//        generateReportFile(report);
        String reportName = report.getEquipment().getEquipmentName();
        generateReportAsync(reportName,report);

    }

    @Override
    public void addFragmentCrmTest(CircuitBreaker circuitBreaker) {

        FragmentCrmTest fragment = FragmentCrmTest.newInstance("","");
        fragment.setFragmentClickListener(this,circuitBreaker);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder,fragment,"")
                .addToBackStack("")
                .commit();

    }

    @Override
    public void addFragmentTripTest(CircuitBreaker circuitBreaker) {

        FragmentTripTest fragment = FragmentTripTest.newInstance("","");
        fragment.setFragmentClickListener(this,circuitBreaker);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder,fragment,"")
                .addToBackStack("")
                .commit();

    }

    @Override
    public void openCamera(CameraFragmentClickListener cameraFragmentClickListener, ImageView imageView,String imageName,String subFolder) {

        FragmentCamera2 fragment = FragmentCamera2.newInstance("","");
        fragment.setFragmentClickListener(cameraFragmentClickListener,imageView, imageName, subFolder);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder,fragment,"")
                .addToBackStack("")
                .commit();
    }

    @Override
    public void checkTestStatus() {
        if (fragmentCrmTripTest!=null)
            fragmentCrmTripTest.checkStatus();
    }

    @Override
    public void onFragmentButtonClicked(int buttonNumber, int id, String name) {

    }


}