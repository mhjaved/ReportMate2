package com.hasanjaved.reportmate.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hasanjaved.reportmate.adapter.CircuitListRecyclerAdapter2;
import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.databinding.FragmentNewReportPhaseThreeCrmTripBinding;
import com.hasanjaved.reportmate.listeners.CameraFragmentClickListener;
import com.hasanjaved.reportmate.listeners.FragmentClickListener;
import com.hasanjaved.reportmate.listeners.RecyclerViewClickListener;
import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.utility.FileMover;
import com.hasanjaved.reportmate.utility.Utility;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

public class FragmentCrmTripTest extends Fragment implements RecyclerViewClickListener, CameraFragmentClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Activity activity;

    private LinearLayoutManager linearLayoutManager;

    private List<CircuitBreaker> list = new ArrayList<>();
    private View rootView, viewOne, viewCrm, viewTrip, viewFour;
    private FragmentNewReportPhaseThreeCrmTripBinding binding;
    private String mParam1;
    private String mParam2;

    private RecyclerView rvList;
    private CircuitListRecyclerAdapter2 circuitListRecyclerAdapter;

    public FragmentCrmTripTest() {
    }

    private FragmentClickListener fragmentClickListener;

    public void setFragmentClickListener(FragmentClickListener fragmentClickListener) {
        this.fragmentClickListener = fragmentClickListener;
    }

    public static FragmentCrmTripTest newInstance(String param1, String param2) {
        FragmentCrmTripTest fragment = new FragmentCrmTripTest();
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

        binding = FragmentNewReportPhaseThreeCrmTripBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();

        viewOne = rootView.findViewById(R.id.viewOne);
//        viewCrm = rootView.findViewById(R.id.viewCrmOne);
//        viewTrip = rootView.findViewById(R.id.viewTripOne);
//        viewFour = rootView.findViewById(R.id.viewFour);

        setViewListeners();
        setCircuitList();
        setPageOneData();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkStatusOnResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        Utility.showToast(activity,"onHiddenChanged");
//
//    }

    public void checkStatus(){
        try {


            setCircuitList();

            if (Utility.isCrmTestDone(activity)){
                binding.viewOne.ivCrmChecked.setVisibility(View.VISIBLE);
                Utility.showToast(activity,"Crm Test done for all circuits");
            }else  binding.viewOne.ivCrmChecked.setVisibility(View.GONE);

            if (Utility.isTripTestDone(activity)){
                binding.viewOne.ivTripChecked.setVisibility(View.VISIBLE);
                Utility.showToast(activity,"Trip Test done for all circuits");
            }else  binding.viewOne.ivTripChecked.setVisibility(View.GONE);

            if (Utility.isCrmTestDone(activity)&&Utility.isTripTestDone(activity)){
                Utility.showToast(activity,"Crm and Trip Test done for all circuits");
                closeFragment();
            }

        }catch (Exception e){
            Utility.showLog("jjjjj"+e);
        }
    }

    public void checkStatusOnResume(){
        try {

            if (Utility.isCrmTestDone(activity)){
                binding.viewOne.ivCrmChecked.setVisibility(View.VISIBLE);
                Utility.showToast(activity,"Crm Test done for all circuits");
            }else  binding.viewOne.ivCrmChecked.setVisibility(View.GONE);

            if (Utility.isTripTestDone(activity)){
                binding.viewOne.ivTripChecked.setVisibility(View.VISIBLE);
                Utility.showToast(activity,"Trip Test done for all circuits");
            }else  binding.viewOne.ivTripChecked.setVisibility(View.GONE);

            if (Utility.isCrmTestDone(activity)&&Utility.isTripTestDone(activity)){
                Utility.showToast(activity,"Crm and Trip Test done for all circuits");
//                closeFragment();
            }

        }catch (Exception e){
            Utility.showLog(e.toString());
        }
    }

    private void closeFragment() {
        getParentFragmentManager().popBackStack();
    }
    private void setPageOneData() {
        Report report = Utility.getReport(activity);
        if (report != null) {
            if (report.getEquipment() != null) {
                if (report.getEquipment().getEquipmentName() != null)
                    binding.viewOne.etEquipmentName.setText(report.getEquipment().getEquipmentName());

                if (report.getEquipment().getCircuitBreakerList() != null)
                    if (!report.getEquipment().getCircuitBreakerList().isEmpty()) {
                        list.addAll(report.getEquipment().getCircuitBreakerList());
                        circuitListRecyclerAdapter.notifyDataSetChanged();
                    }


            }

        }

    }

    private void setCircuitList() {

        linearLayoutManager = new LinearLayoutManager(activity);
        binding.viewOne.rvCircuit.setLayoutManager(linearLayoutManager);
        circuitListRecyclerAdapter = new CircuitListRecyclerAdapter2(activity, list, -1, this);
        binding.viewOne.rvCircuit.setAdapter(circuitListRecyclerAdapter);

    }

    private void setViewListeners() {

        binding.viewOne.btnNext.setOnClickListener(view -> {

                    if (binding.viewOne.tvTrip.getContentDescription().equals(getString(R.string.selected))) {
                        fragmentClickListener.addFragmentTripTest(circuitListRecyclerAdapter.getSelectedCircuit());
                    } else if (binding.viewOne.tvCrm.getContentDescription().equals(getString(R.string.selected))) {
                        fragmentClickListener.addFragmentCrmTest(circuitListRecyclerAdapter.getSelectedCircuit());
                    }
                }
        );

//        binding.viewCrmOne.btnNext.setOnClickListener(view ->
//                showPage(viewTrip, viewOne,
//                        viewCrm, viewFour));
//
//        binding.viewTripOne.btnNext.setOnClickListener(view ->
//                showPage(viewFour, viewOne,
//                        viewCrm, viewTrip));

        binding.viewOne.ivBack.setOnClickListener(view -> {
                    try {
                        activity.onBackPressed();
                    } catch (Exception e) {
                        Utility.showLog(e.toString());
                    }
                }
        );

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

        binding.viewOne.tvCrm.setOnClickListener(view -> {

                    binding.viewOne.tvCrm.setContentDescription(getString(R.string.selected));
                    binding.viewOne.tvTrip.setContentDescription(getString(R.string.not_selected));

                    binding.viewOne.tvCrm.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_item_circuit_list_selected));
                    binding.viewOne.tvTrip.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_edit_text));
                    setViewFourNextButtonStatus();
                }
        );

        binding.viewOne.tvTrip.setOnClickListener(view -> {

                    binding.viewOne.tvTrip.setContentDescription(getString(R.string.selected));
                    binding.viewOne.tvCrm.setContentDescription(getString(R.string.not_selected));

                    binding.viewOne.tvTrip.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_item_circuit_list_selected));
                    binding.viewOne.tvCrm.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_edit_text));

                    setViewFourNextButtonStatus();

                }
        );


        binding.viewOne.rlCircuit.setOnClickListener(view ->
                setExpandView(binding.viewOne.expandCircuitList, binding.viewOne.ivArrowCircuit)
        );

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

    }

    ArrayAdapter<String> arrayAdapter;
    String[] unitList = {"Ω", "mΩ", "µΩ"};


    private void setViewFourNextButtonStatus() {

//        Utility.showLog(circuitListRecyclerAdapter.getSelectedCircuit().toString());

        if (circuitListRecyclerAdapter.getSelectedCircuit()!=null){
            binding.viewOne.btnNext.setEnabled(binding.viewOne.tvTrip.getContentDescription().equals(getString(R.string.selected)) ||
                    binding.viewOne.tvCrm.getContentDescription().equals(getString(R.string.selected)));
        }else Utility.showToast(activity,"select circuit");



    }

    private void setExpandView(ExpandableLayout expand, ImageView arrow) {

        if (expand.isExpanded()) {
            arrow.animate().rotation(180).start();
            expand.collapse();
        } else {
            arrow.animate().rotation(0).start();
            expand.expand();
        }

    }

    private void showPage(View visible, View hide1, View hide2, View hide3) {
        visible.setVisibility(View.VISIBLE);
        hide1.setVisibility(View.GONE);
        hide2.setVisibility(View.GONE);
        hide3.setVisibility(View.GONE);
    }


    @Override
    public void onItemClicked(int index) {
        circuitListRecyclerAdapter.setSelectedItem(index);
        circuitListRecyclerAdapter.notifyDataSetChanged();
        setViewFourNextButtonStatus();
    }

    @Override
    public void onEditClicked(int index) {

    }

    @Override
    public void onDeleteClicked(int index) {

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

            FileMover.moveImageToDocumentsSubfolder(
                    activity,
                    imageLocation,
                    imageName,
                    subFolder
            );
        }
    }
}