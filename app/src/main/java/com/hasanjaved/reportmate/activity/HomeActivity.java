package com.hasanjaved.reportmate.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hasanjaved.reportmate.DesignFragment;
import com.hasanjaved.reportmate.fragment.HomeFragment;
import com.hasanjaved.reportmate.OcrTestFragment;
import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.SettingsFragment;
import com.hasanjaved.reportmate.databinding.ActivityHomeBinding;
import com.hasanjaved.reportmate.listeners.HomeFragmentClickListener;
import com.hasanjaved.reportmate.listeners.OnSettingsItemClickedListener;
import com.hasanjaved.reportmate.utility.Utility;

public class HomeActivity extends AppCompatActivity implements OnSettingsItemClickedListener, HomeFragmentClickListener {

    private ActivityHomeBinding binding;

    private static final String TAG = Utility.TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        binding.buttonOpen.setOnClickListener(view -> );

        addHomeFragment();

//        addTestFragment();

//        addDesignFragment();
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
    public void onSettingsItemClicked(String eventName) {
        Toast.makeText(this,"clicked "+eventName, Toast.LENGTH_SHORT).show();

        binding.drawerLayout.closeDrawer(GravityCompat.END);

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
}