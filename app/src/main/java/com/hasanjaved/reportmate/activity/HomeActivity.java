package com.hasanjaved.reportmate.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hasanjaved.reportmate.DesignFragment;
import com.hasanjaved.reportmate.databinding.ActivityHomeBinding;
import com.hasanjaved.reportmate.fragment.HomeFragment;
import com.hasanjaved.reportmate.OcrTestFragment;
import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.fragment.SettingsFragment;
import com.hasanjaved.reportmate.listeners.HomeFragmentClickListener;
import com.hasanjaved.reportmate.listeners.OnSettingsItemClickedListener;
import com.hasanjaved.reportmate.model.Employee;
import com.hasanjaved.reportmate.utility.FileMover;
import com.hasanjaved.reportmate.utility.FileMover2;
import com.hasanjaved.reportmate.utility.FolderManager;
import com.hasanjaved.reportmate.utility.FolderManager2;
import com.hasanjaved.reportmate.utility.MediaStoreUtils;
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

//        MediaStoreUtils.createFolderInDocuments(this,"MediaStoreUtils");
        MediaStoreUtils.createSubFolderInDocuments(this,"ReportMate",Utility.getReport(this).getProjectNo());
        addHomeFragment();

//        FileMover2.moveImageToDocumentsSubfolder(
//                this,
//                Utility.IMAGE_SAMPLE_DIRECTORY,
//                "ir_test.jpg",
//                "ReportMate"
//        );

        Employee employee = Utility.getEmployee(this);
        assert employee != null;
        Utility.showLog(employee.toString());

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



    @Override
    public void onPreviousReportHistoryClicked() {

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