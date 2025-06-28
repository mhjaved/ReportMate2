package com.hasanjaved.reportmate.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.doc_generator.ReportGenerator2;
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
import com.hasanjaved.reportmate.utility.ImageLoader;
import com.hasanjaved.reportmate.utility.not.DocumentGenerator;
import com.hasanjaved.reportmate.utility.Utility;

public class NewReportActivity extends AppCompatActivity implements FragmentClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        addNewReportFragment();
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

    private void generateSampleDocument() {
        String textContent = "j This is a sample document generated using Apache POI on Android. " +
                "It contains text content and images from local storage.";
///storage/emulated/0/Pictures/1748521045717.jpg
        // Example with local file paths
        String[] imagePaths = {
                ImageLoader.getImagePath("img.jpg"),
                ImageLoader.getImagePath("img.jpg")
        };

//        String[] imagePaths = {
//                Utility.IMAGE_SAMPLE_DIRECTORY3,
//                Utility.IMAGE_SAMPLE_DIRECTORY3
//        };

        String filePath = DocumentGenerator.generateDocument(
                this, "SampleDocument", textContent, imagePaths);

        if (filePath != null) {
            Utility.showLog(" filePath "+ filePath);
            Toast.makeText(this, "Document created: " + filePath, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Failed to create document", Toast.LENGTH_SHORT).show();
        }

        // Or generate from assets
        String[] assetImages = {"sample_image1.jpg", "sample_image2.png"};
        String assetFilePath = DocumentGenerator.generateDocumentFromAssets(
                this, "AssetDocument", textContent, assetImages);
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
        ReportGenerator2.generateReport(this, reportName, report,
                new ReportGenerator2.ReportGenerationCallback() {
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
        generateReportFile(report);
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
        FragmentCamera fragment = FragmentCamera.newInstance("","");
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