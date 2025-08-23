package com.hasanjaved.reportmate.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;

// BEST PRACTICE 1: Use Fragment Factory for Better Memory Management
public class CameraFragmentFactory extends FragmentFactory {

    @NonNull
    @Override
    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
//        if (className.equals(FragmentCamera3.class.getName())) {
//            return new FragmentCamera3();
//        }
        return super.instantiate(classLoader, className);
    }
}
