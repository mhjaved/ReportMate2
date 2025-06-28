package com.hasanjaved.reportmate.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.databinding.ActivityHistoryBinding;
import com.hasanjaved.reportmate.databinding.ActivityHomeBinding;
import com.hasanjaved.reportmate.fragment.HistoryFragment;
import com.hasanjaved.reportmate.fragment.HomeFragment;
import com.hasanjaved.reportmate.fragment.OngoingFragment;
import com.hasanjaved.reportmate.utility.Utility;

public class HistoryActivity extends AppCompatActivity {

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

        HistoryFragment fragment = HistoryFragment.newInstance("", "");
//        homeFragment.setFragmentClickListener( this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentHolder, fragment,
                        "HistoryFragment")
                .commit();

    }

    private void addOngoingFragment() {

         OngoingFragment fragment = OngoingFragment.newInstance("", "");
//        homeFragment.setFragmentClickListener( this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentHolder, fragment,
                        "OngoingFragment")
                .commit();

    }

}