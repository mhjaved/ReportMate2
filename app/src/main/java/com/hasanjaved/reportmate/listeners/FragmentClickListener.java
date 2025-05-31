package com.hasanjaved.reportmate.listeners;

import android.widget.ImageView;

public interface FragmentClickListener {

//    void addFragment();
    void addNewReportPhaseTwoFragment();

    void addNewReportFragmentPhaseThreeIR();

    void addNewReportFragmentPhaseThreeCrmTrip();
    void openCamera(CameraFragmentClickListener cameraFragmentClickListener, ImageView imageView,String imageName,String subFolder);

    void onFragmentButtonClicked(int buttonNumber, int id, String name);
//     void onScreenTouched();
//     void onOutsideTouched();
//     void updateColorList(List<Integer> colorList );

}
