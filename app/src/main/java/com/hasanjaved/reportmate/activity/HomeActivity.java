package com.hasanjaved.reportmate.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hasanjaved.reportmate.DesignFragment;
import com.hasanjaved.reportmate.databinding.ActivityHomeBinding;
import com.hasanjaved.reportmate.utility.DirectoryManager;
import com.hasanjaved.reportmate.utility.ReportGenerator;
import com.hasanjaved.reportmate.fragment.HomeFragment;
import com.hasanjaved.reportmate.OcrTestFragment;
import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.fragment.SettingsFragment;
import com.hasanjaved.reportmate.listeners.HomeFragmentClickListener;
import com.hasanjaved.reportmate.listeners.OnSettingsItemClickedListener;
import com.hasanjaved.reportmate.model.Employee;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.utility.Utility;

public class HomeActivity extends AppCompatActivity implements OnSettingsItemClickedListener, HomeFragmentClickListener {

    private ActivityHomeBinding binding;

    private static final String TAG = Utility.TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        if (!FolderManager.isBaseFolderAvailable(this)){
//            Utility.showLog(" base folder link "+ FolderManager.createBaseFolder(this));
//        }else {
//            Utility.showLog(" base folder available "+ FolderManager.getFolderPathIfExists(this,Utility.BASE_FOLDER_NAME));
//        }

//        String link = FolderManager.createFolder(this,Utility.BASE_FOLDER_NAME);
//        String link2 = FolderManager.createFolderInDirectory(this,link,"projectOne");
//        Utility.showLog("link2 "+link2);
//        String projectPath = FolderManager2.createProjectFolder(this,
//                "/storage/emulated/0/Android/data/com.hasanjaved.reportmate/files/Documents/ReportMate",
//                "projectOne");

//        DirectoryManager.createIrFolder(this,"j test");

        DirectoryManager.createBaseFolder(this,Utility.BASE_FOLDER_NAME);
//        MediaStoreUtils.createFolderInDocuments(this,Utility.BASE_FOLDER_NAME);

//        MediaStoreUtils.createSubFolderInDocuments(this,"ReportMate",Utility.getReport(this).getProjectNo());
        addHomeFragment();

//        generateReportFile(Utility.getReport(this));

//        ReportGenerator2.generateElectricalInspectionReport(this, Utility.getReportDirectory(this),"J report",Utility.IMAGE_SAMPLE_DIRECTORY2);
//        ReportGenerator3.generateElectricalInspectionReportToPublicDocuments(this,"jreport",Utility.IMAGE_SAMPLE_DIRECTORY2);
//                generateElectricalInspectionReport(this, Utility.getReportDirectory(this),"J report",Utility.IMAGE_SAMPLE_DIRECTORY2);

//        FileMover2.moveImageToDocumentsSubfolder(
//                this,
//                Utility.IMAGE_SAMPLE_DIRECTORY,
//                "ir_test.jpg",
//                "ReportMate"
//        );

        Employee employee = Utility.getEmployee(this);
        assert employee != null;
        Utility.showLog(employee.toString());

//        intent.putExtra("show_id", String.valueOf(content.getShowId()));


    }

    @Override
    protected void onResume() {
        super.onResume();

        SettingsFragment settingsFragment = SettingsFragment.newInstance();
        settingsFragment.setCallback(HomeActivity.this);

        if (!isFinishing()) {
            try {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.navigationFragmentHolder, settingsFragment,
                                "NavigationHomeFragment")
                        .commit();
            } catch (IllegalStateException e) {
                Log.e(TAG, e.toString());
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }


    private void addHomeFragment() {

        HomeFragment homeFragment = HomeFragment.newInstance("", "");
        homeFragment.setFragmentClickListener( this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentHolder, homeFragment,
                        "HomeFragment")
                .commit();

    }

    private void addTestFragment() {

        OcrTestFragment ocrTestFragment = OcrTestFragment.newInstance("", "");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentHolder, ocrTestFragment,
                        "ocrTestFragment")
                .commit();

    }

    private void addDesignFragment() {

        DesignFragment fragment = DesignFragment.newInstance("", "");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentHolder, fragment,
                        "DesignFragment")
                .commit();

    }
    private void gotoNewReport() {
        startActivity(new Intent(HomeActivity.this, NewReportActivity.class));
    }

    @Override
    public void onMenuButtonClicked() {
        binding.drawerLayout.openDrawer(GravityCompat.END);
    }

    @Override
    public void onGenerateNewReportClicked() {
        gotoNewReport();
    }


//    public void generateReportFile(Report report) {
//        String reportName = report.getProjectNo();
//
//        boolean success = ReportGenerator2.generateReport(this, reportName,report);
//
//        if (success) {
//            Utility.showLog( " report generated successfully");
//            // File saved to: /storage/emulated/0/Documents/ReportMateReports/
//        } else {
//            Utility.showLog(  "Failed to generate  report");
//        }
//    }

    private void gotoHistoryActivity(String Token) {
        Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
        intent.putExtra(Utility.HISTORY_FRAGMENT_TOKEN, Token);
        startActivity(intent);
    }

//    public void generateReportFile(Report report) {
//        String reportName = report.getProjectNo();
//
//        // Show loading dialog
//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Generating Report");
//        progressDialog.setMessage("Initializing...");
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setMax(100);
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        // Generate report asynchronously
//        ReportGenerator.generateReport(this, reportName, report,
//                new ReportGenerator.ReportGenerationCallback() {
//                    @Override
//                    public void onStarted() {
//                        Utility.showLog("Report generation started");
//                        progressDialog.setMessage("Starting report generation...");
//                    }
//
//                    @Override
//                    public void onProgress(int progress, String message) {
//                        progressDialog.setProgress(progress);
//                        progressDialog.setMessage(message);
//                        Utility.showLog("Progress: " + progress + "% - " + message);
//                    }
//
//                    @Override
//                    public void onSuccess(String filePath) {
//                        progressDialog.dismiss();
//                        Utility.showLog("Report generated successfully: " + filePath);
//
//                        // Show success message
//                        Toast.makeText(HomeActivity.this,
//                                "Report saved successfully!",
//                                Toast.LENGTH_LONG).show();
//
//                        // Optional: Open the file or show notification
//                        // showReportCompletedNotification(filePath);
//                    }
//
//                    @Override
//                    public void onError(String errorMessage) {
//                        progressDialog.dismiss();
//                        Utility.showLog("Failed to generate report: " + errorMessage);
//
//                        // Show error message
//                        Toast.makeText(HomeActivity.this,
//                                "Failed to generate report: " + errorMessage,
//                                Toast.LENGTH_LONG).show();
//                    }
//                }
//        );
//    }

    @Override
    public void onPreviousReportHistoryClicked() {
        gotoHistoryActivity(Utility.HISTORY_FRAGMENT_HISTORY);
    }

    @Override
    public void onOngoingReportClicked() {
        gotoHistoryActivity(Utility.HISTORY_FRAGMENT_ONGOING);
    }


    @Override
    public void onSettingsItemClicked(String eventName) {
        Toast.makeText(this,"clicked "+eventName, Toast.LENGTH_SHORT).show();
        binding.drawerLayout.closeDrawer(GravityCompat.END);
    }

    @Override
    public void logOut(String eventName) {
        Utility.saveEmployee(this,null);
        Intent intent = new Intent(HomeActivity.this, SplashAndLoginActivity.class);
        startActivity(intent);
        finish();
    }
}