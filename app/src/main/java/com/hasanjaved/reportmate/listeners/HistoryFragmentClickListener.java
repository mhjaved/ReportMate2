package com.hasanjaved.reportmate.listeners;

import android.widget.ImageView;

import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.model.Report;

public interface HistoryFragmentClickListener {

    void addOngoingReportDetails(int index);

    void addHistoryReportDetails(int index);

    void generateReport(Report report);


    void openCamera(CameraFragmentClickListener cameraFragmentClickListener, ImageView imageView,String imageName,String subFolder);


}
