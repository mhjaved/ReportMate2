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

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.adapter.CircuitListRecyclerAdapter2;
import com.hasanjaved.reportmate.data_manager.TestData;
import com.hasanjaved.reportmate.databinding.FragmentNewReportPhaseThreeCrmTripBinding;
import com.hasanjaved.reportmate.databinding.FragmentTripTestBinding;
import com.hasanjaved.reportmate.listeners.CameraFragmentClickListener;
import com.hasanjaved.reportmate.listeners.FragmentClickListener;
import com.hasanjaved.reportmate.listeners.RecyclerViewClickListener;
import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.utility.FileMover2;
import com.hasanjaved.reportmate.utility.Utility;

import net.cachapa.expandablelayout.ExpandableLayout;

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

    private void setPageOneData() {
        binding.etCircuitBreakerName.setText(circuitBreaker.getName());
        binding.etCircuitBreakerSize.setText(circuitBreaker.getSize());
    }


    private void closeFragment(){
        getParentFragmentManager().popBackStack();
    }
//
//    private void setPageOneData() {
//        Report report = Utility.getReport(activity);
//        if (report != null) {
//            if (report.getEquipment() != null) {
//                if (report.getEquipment().getEquipmentName() != null)
//                    binding.viewOne.etEquipmentName.setText(report.getEquipment().getEquipmentName());
//
//                if (report.getEquipment().getCircuitBreakerList() != null)
//                    if (!report.getEquipment().getCircuitBreakerList().isEmpty()) {
//                        list.addAll(report.getEquipment().getCircuitBreakerList());
//                        circuitListRecyclerAdapter.notifyDataSetChanged();
//                    }
//
//
//            }
//
//        }
//
//    }


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
//
//    private void setCircuitList() {
//
//        linearLayoutManager = new LinearLayoutManager(activity);
//        binding.viewOne.rvCircuit.setLayoutManager(linearLayoutManager);
//        circuitListRecyclerAdapter = new CircuitListRecyclerAdapter2(activity, list, 0, this);
//        binding.viewOne.rvCircuit.setAdapter(circuitListRecyclerAdapter);
//
//    }
//
    private void setViewListeners() {
        binding.ivBack.setOnClickListener(view -> closeFragment());
        binding.btnNext.setOnClickListener(view -> {
            saveTripData();
            closeFragment();
        });

    }

    private void saveTripData() {
        TestData.saveTripTestData(activity,
                binding.etTestAmplitude.getText().toString().trim(),
                binding.etTripTime.getText().toString().trim(),
                binding.etInstantTrip.getText().toString().trim()
                );

    }
//        binding.viewOne.btnNext.setOnClickListener(view -> {
//
//                    if (binding.viewOne.tvTrip.getContentDescription().equals(getString(R.string.selected))) {
//                        showPage(viewTrip, viewCrm, viewOne,
//                                viewFour);
//                    } else if (binding.viewOne.tvCrm.getContentDescription().equals(getString(R.string.selected))) {
//                        showPage(viewCrm, viewOne,
//                                viewTrip, viewFour);
//                    }
//                }
//        );
//
//        binding.viewCrmOne.btnNext.setOnClickListener(view ->
//                showPage(viewTrip, viewOne,
//                        viewCrm, viewFour));
//
//        binding.viewTripOne.btnNext.setOnClickListener(view ->
//                showPage(viewFour, viewOne,
//                        viewCrm, viewTrip));
//
//        binding.viewOne.ivBack.setOnClickListener(view -> {
//                    try {
//                        activity.onBackPressed();
//                    } catch (Exception e) {
//                        Utility.showLog(e.toString());
//                    }
//                }
//        );
//
//        binding.viewCrmOne.ivBack.setOnClickListener(view ->
//                showPage(viewOne, viewCrm,
//                        viewTrip, viewFour));
//
//        binding.viewTripOne.ivBack.setOnClickListener(view ->
//                showPage(viewOne,viewCrm,
//                        viewTrip, viewFour));
//
//        binding.viewFour.ivBack.setOnClickListener(view ->
//                showPage(viewTrip, viewCrm, viewOne,
//                        viewFour));
//
//        binding.viewOne.tvCrm.setOnClickListener(view -> {
//
//                    binding.viewOne.tvCrm.setContentDescription(getString(R.string.selected));
//                    binding.viewOne.tvTrip.setContentDescription(getString(R.string.not_selected));
//
//                    binding.viewOne.tvCrm.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_item_circuit_list_selected));
//                    binding.viewOne.tvTrip.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_edit_text));
//                    setViewFourNextButtonStatus();
//                }
//        );
//
//        binding.viewOne.tvTrip.setOnClickListener(view -> {
//
//                    binding.viewOne.tvTrip.setContentDescription(getString(R.string.selected));
//                    binding.viewOne.tvCrm.setContentDescription(getString(R.string.not_selected));
//
//                    binding.viewOne.tvTrip.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_item_circuit_list_selected));
//                    binding.viewOne.tvCrm.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_edit_text));
//
//                    setViewFourNextButtonStatus();
//
//                }
//        );
//
//
//        binding.viewOne.rlCircuit.setOnClickListener(view ->
//                setExpandView(binding.viewOne.expandCircuitList, binding.viewOne.ivArrowCircuit)
//        );
//
//        binding.viewCrmOne.sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//                Utility.showLog(unitList[i]);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//
//        binding.viewCrmOne.sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//                Utility.showLog(unitList[i]);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//        arrayAdapter = new ArrayAdapter<>(activity, R.layout.item_spinner, unitList);
//        arrayAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
//        binding.viewCrmOne.sp1.setAdapter(arrayAdapter);
//        binding.viewCrmOne.sp2.setAdapter(arrayAdapter);
//
//    }

    ArrayAdapter<String> arrayAdapter;
    String[] unitList = {"Ω", "mΩ", "µΩ"};

//
//    private void setViewFourNextButtonStatus() {
//
////        binding.viewOne.tvTrip.setContentDescription(getString(R.string.selected));
////        binding.viewOne.tvCrm.setContentDescription(getString(R.string.not_selected));
//
//        binding.viewOne.btnNext.setEnabled(binding.viewOne.tvTrip.getContentDescription().equals(getString(R.string.selected)) ||
//                binding.viewOne.tvCrm.getContentDescription().equals(getString(R.string.selected)));
//
//    }
//


//    private void showPage(View visible, View hide1, View hide2, View hide3) {
//        visible.setVisibility(View.VISIBLE);
//        hide1.setVisibility(View.GONE);
//        hide2.setVisibility(View.GONE);
//        hide3.setVisibility(View.GONE);
//    }


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