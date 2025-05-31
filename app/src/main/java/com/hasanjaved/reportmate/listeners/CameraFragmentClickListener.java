package com.hasanjaved.reportmate.listeners;

import android.widget.ImageView;

public interface CameraFragmentClickListener {

//    void onBackPressed();
    void onCancelPressed();
    void onSaveButtonPressed(ImageView imageView,String imageLocation,String imageName,String subFolder);

}
