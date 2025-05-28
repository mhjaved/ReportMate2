package com.hasanjaved.reportmate.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.fragment.FragmentCamera;
import com.hasanjaved.reportmate.fragment.NewReportFragmentPhaseOne;
import com.hasanjaved.reportmate.fragment.NewReportFragmentPhaseThreeCrmTrip;
import com.hasanjaved.reportmate.fragment.NewReportFragmentPhaseThreeIR;
import com.hasanjaved.reportmate.fragment.NewReportFragmentPhaseTwo;
import com.hasanjaved.reportmate.listeners.FragmentClickListener;

public class NewReportActivity extends AppCompatActivity implements FragmentClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        addNewReportFragment();
//        addNewReportPhaseTwoFragment();
//        addNewReportPhaseThreeFragment();
//        addNewReportFragmentPhaseThreeCrmTrip();
//        addFragmentCamera();
//        createFile();

//        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
//        String fileLocation = "file:"+preferences.getString(Utility.ImageToken,"hasan");
//

    }

    private void addNewReportFragment(){

        NewReportFragmentPhaseOne fragment = NewReportFragmentPhaseOne.newInstance("","");
        fragment.setFragmentClickListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentHolder,fragment,"")
                .commit();

    }

    private void addNewReportPhaseTwoFragment(){

        NewReportFragmentPhaseTwo fragment = NewReportFragmentPhaseTwo.newInstance("","");
        fragment.setFragmentClickListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder,fragment,"")
                .commit();

    }


    private void addFragmentCamera(){

        FragmentCamera fragment = FragmentCamera.newInstance("","");
//        fragment.setFragmentClickListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder,fragment,"")
                .addToBackStack("")
                .commit();

    }

    private void addNewReportPhaseThreeFragment(){

        NewReportFragmentPhaseThreeIR fragment = NewReportFragmentPhaseThreeIR.newInstance("","");
        fragment.setFragmentClickListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder,fragment,"")
                .commit();

    }

    private void addNewReportFragmentPhaseThreeCrmTrip(){

        NewReportFragmentPhaseThreeCrmTrip fragment = NewReportFragmentPhaseThreeCrmTrip.newInstance("","");
        fragment.setFragmentClickListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentHolder,fragment,"")
                .addToBackStack("")
                .commit();

    }

    @Override
    public void addFragment() {
        addNewReportPhaseTwoFragment();
    }

    @Override
    public void openCamera() {
        addFragmentCamera();
    }

    @Override
    public void onFragmentButtonClicked(int buttonNumber, int id, String name) {

    }
}