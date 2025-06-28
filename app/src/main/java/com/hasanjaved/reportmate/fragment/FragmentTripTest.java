package com.hasanjaved.reportmate.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hasanjaved.reportmate.data_manager.TestData;
import com.hasanjaved.reportmate.databinding.FragmentTripTestBinding;
import com.hasanjaved.reportmate.listeners.CameraFragmentClickListener;
import com.hasanjaved.reportmate.listeners.FragmentClickListener;
import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.utility.FileMover2;
import com.hasanjaved.reportmate.utility.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FragmentTripTest extends Fragment implements  CameraFragmentClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Activity activity;

    private LinearLayoutManager linearLayoutManager;

    private List<CircuitBreaker> list = new ArrayList<>();
    private View rootView, viewOne, viewCrm, viewTrip, viewFour;
    private FragmentTripTestBinding binding;
    private String mParam1;
    private String mParam2;

    private RecyclerView rvList;

    public FragmentTripTest() {
    }

    private FragmentClickListener fragmentClickListener;

    private CircuitBreaker circuitBreaker;
    public void setFragmentClickListener(FragmentClickListener fragmentClickListener,CircuitBreaker circuitBreaker) {
        this.fragmentClickListener = fragmentClickListener;
        this.circuitBreaker = circuitBreaker;
    }

    public static FragmentTripTest newInstance(String param1, String param2) {
        FragmentTripTest fragment = new FragmentTripTest();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        activity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTripTestBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();

        Utility.showToast(activity,"trip test");

        setViewListeners();
        setTripCamera();
        setPageOneData();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setPageOneData() {
        binding.etCircuitBreakerName.setText(circuitBreaker.getName());
        binding.etCircuitBreakerSize.setText(circuitBreaker.getSize());
    }


    private void closeFragment(){
        getParentFragmentManager().popBackStack();
    }

    private void setTripCamera() {

        String tripDirectory = Utility.getTripFolderLink(Objects.requireNonNull(Utility.getReport(activity)), circuitBreaker);

        binding.imgInjectorCurrent.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.imgInjectorCurrentShow,
                        Utility.imgInjectorCurrent, tripDirectory));

        binding.imgInjectedCurrent.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.imgInjectedCurrentShow,
                        Utility.imgInjectedCurrent, tripDirectory));

        binding.imgTripTimeConnection.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.imgTripTimeConnectionShow,
                        Utility.imgTripTimeConnection,tripDirectory));

        binding.imgTripTime.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.imgTripTimeShow,
                        Utility.imgTripTime, tripDirectory));

        binding.imgAfterTripTime.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.imgAfterTripTimeShow,
                        Utility.imgAfterTripTime, tripDirectory));

    }

    private void setViewListeners() {
        binding.ivBack.setOnClickListener(view -> {
            fragmentClickListener.checkTestStatus();
            closeFragment();
        });
        binding.btnNext.setOnClickListener(view -> {
            fragmentClickListener.checkTestStatus();
            saveTripData();
            closeFragment();
        });

    }

    private void saveTripData() {
        TestData.saveTripTestData(activity,
                circuitBreaker,
                binding.etTestAmplitude.getText().toString().trim(),
                binding.etTripTime.getText().toString().trim(),
                binding.etInstantTrip.getText().toString().trim()
                );

    }


    @Override
    public void onCancelPressed() {

    }

    @Override
    public void onSaveButtonPressed(ImageView imageView, String imageLocation, String imageName, String subFolder) {
        if (!imageLocation.equals("")) {
            imageView.setVisibility(View.VISIBLE);

            Glide.with(activity)
                    .load(Uri.parse("file:" + imageLocation))
                    .into(imageView);

            FileMover2.moveImageToDocumentsSubfolder(
                    activity,
                    imageLocation,
                    imageName,
                    subFolder
            );
        }
    }
}