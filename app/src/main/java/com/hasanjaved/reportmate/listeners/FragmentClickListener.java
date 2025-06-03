package com.hasanjaved.reportmate.listeners;

import android.widget.ImageView;

import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.model.Report;

public interface FragmentClickListener {

//    void addFragment();
    void addNewReportPhaseTwoFragment();

    void addNewReportFragmentPhaseThreeIR();

    void addNewReportFragmentPhaseThreeCrmTrip();
    void generateReport(Report report);

    void addFragmentCrmTest(CircuitBreaker circuitBreaker);

    void addFragmentTripTest(CircuitBreaker circuitBreaker);
    void openCamera(CameraFragmentClickListener cameraFragmentClickListener, ImageView imageView,String imageName,String subFolder);

    void onFragmentButtonClicked(int buttonNumber, int id, String name);
//     void onScreenTouched();
//     void onOutsideTouched();
//     void updateColorList(List<Integer> colorList );

}
