package com.hasanjaved.reportmate.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.data_manager.TestData;
import com.hasanjaved.reportmate.databinding.FragmentCrmTestBinding;
import com.hasanjaved.reportmate.listeners.CameraFragmentClickListener;
import com.hasanjaved.reportmate.listeners.FragmentClickListener;
import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.utility.FileMover2;
import com.hasanjaved.reportmate.utility.Utility;

import java.util.Objects;


public class FragmentCrmTest extends Fragment implements CameraFragmentClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Activity activity;


    private String rResUnite ="mΩ",yResUnite ="mΩ",bResUnite ="mΩ";
    private View rootView;
    //    , viewOne, viewCrm, viewTrip, viewFour;
    private FragmentCrmTestBinding binding;
    private String mParam1;
    private String mParam2;


    public FragmentCrmTest() {
    }

    private FragmentClickListener fragmentClickListener;

    private CircuitBreaker circuitBreaker;

    public void setFragmentClickListener(FragmentClickListener fragmentClickListener, CircuitBreaker circuitBreaker) {
        this.fragmentClickListener = fragmentClickListener;
        this.circuitBreaker = circuitBreaker;
    }

    public static FragmentCrmTest newInstance(String param1, String param2) {
        FragmentCrmTest fragment = new FragmentCrmTest();
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

        binding = FragmentCrmTestBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();

        setViewListeners();
        setPageOneData();
        setCrmCamera();

        Utility.showLog(circuitBreaker.toString());

        return binding.getRoot();
    }

    private void closeFragment() {
        getParentFragmentManager().popBackStack();
    }

    private void setPageOneData() {
        binding.etCircuitBreakerName.setText(circuitBreaker.getName());
        binding.etCircuitBreakerSize.setText(circuitBreaker.getSize());
    }

    private void setViewListeners() {

        binding.ivBack.setOnClickListener(view ->{
            closeFragment();
            fragmentClickListener.checkTestStatus();
        } );
        binding.btnNext.setOnClickListener(view -> {
            saveCrmData();
            closeFragment();
            fragmentClickListener.checkTestStatus();
        });
        binding.sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Utility.showLog(unitList[i]);
                rResUnite = unitList[i];
                Utility.showLog("rResUnite "+rResUnite);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        binding.sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                yResUnite = unitList[i];
                Utility.showLog("yResUnite "+yResUnite);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bResUnite = unitList[i];
                Utility.showLog("bResUnite "+bResUnite);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        arrayAdapter = new ArrayAdapter<>(activity, R.layout.item_spinner, unitList);
        arrayAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        binding.sp1.setAdapter(arrayAdapter);
        binding.sp2.setAdapter(arrayAdapter);
        binding.sp3.setAdapter(arrayAdapter);

    }

    private void saveCrmData() {
        TestData.saveCrmTestData(activity,
                circuitBreaker,
                binding.et1.getText().toString().trim(),rResUnite,
                binding.et2.getText().toString().trim(),yResUnite,
                binding.et3.getText().toString().trim(),bResUnite
                );
    }

    ArrayAdapter<String> arrayAdapter;
    String[] unitList = {"mΩ","Ω","µΩ"};

    private void setCrmCamera() {

        binding.imgCameraConnection.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.ivShowImageConnection,
                        Utility.imgCrmConnection, Utility.getCrmFolderLink(Objects.requireNonNull(Utility.getReport(activity)), circuitBreaker))
        );

        binding.imgCameraResult.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.ivShowImageResult,
                        Utility.imgCrmResult, Utility.getCrmFolderLink(Objects.requireNonNull(Utility.getReport(activity)), circuitBreaker))
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