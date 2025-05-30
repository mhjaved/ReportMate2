package com.hasanjaved.reportmate.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hasanjaved.reportmate.CircuitListRecyclerAdapter;
import com.hasanjaved.reportmate.CircuitListRecyclerAdapter2;
import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.databinding.FragmentNewReportPhaseOneBinding;
import com.hasanjaved.reportmate.databinding.FragmentNewReportPhaseThreeCrmTripBinding;
import com.hasanjaved.reportmate.databinding.FragmentNewReportPhaseTwoBinding;
import com.hasanjaved.reportmate.listeners.FragmentClickListener;
import com.hasanjaved.reportmate.listeners.RecyclerViewClickListener;
import com.hasanjaved.reportmate.utility.Utility;

import net.cachapa.expandablelayout.ExpandableLayout;


public class NewReportFragmentPhaseThreeCrmTrip  extends Fragment implements RecyclerViewClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Activity activity;

    private LinearLayoutManager linearLayoutManager;

    private View rootView, viewOne, viewTwo, viewThree, viewFour;
    private FragmentNewReportPhaseThreeCrmTripBinding binding;
    private String mParam1;
    private String mParam2;

    private RecyclerView rvList;
    private CircuitListRecyclerAdapter2 circuitListRecyclerAdapter;

    public NewReportFragmentPhaseThreeCrmTrip() {
    }

    private FragmentClickListener fragmentClickListener;

    public void setFragmentClickListener(FragmentClickListener fragmentClickListener) {
        this.fragmentClickListener = fragmentClickListener;
    }

    public static NewReportFragmentPhaseThreeCrmTrip newInstance(String param1, String param2) {
        NewReportFragmentPhaseThreeCrmTrip fragment = new NewReportFragmentPhaseThreeCrmTrip();
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
        viewTwo = rootView.findViewById(R.id.viewTwo);
        viewThree = rootView.findViewById(R.id.viewThree);
        viewFour = rootView.findViewById(R.id.viewFour);


        setViewListeners();
        setCircuitList();


        return binding.getRoot();
    }

    private void setCircuitList() {

        linearLayoutManager = new LinearLayoutManager(activity);
        binding.viewOne.rvCircuit.setLayoutManager(linearLayoutManager);
        circuitListRecyclerAdapter = new CircuitListRecyclerAdapter2(activity, null, 0, this);
        binding.viewOne.rvCircuit.setAdapter(circuitListRecyclerAdapter);
    }

    private void setViewListeners() {

        binding.viewOne.btnNext.setOnClickListener(view ->
                showPage(viewTwo, viewOne,
                        viewThree, viewFour));

        binding.viewTwo.btnNext.setOnClickListener(view ->
                showPage(viewThree, viewOne,
                        viewTwo, viewFour));

        binding.viewThree.btnNext.setOnClickListener(view ->
                showPage(viewFour, viewOne,
                        viewTwo, viewThree));


        binding.viewOne.ivBack.setOnClickListener(view -> {
                    try {
                        activity.onBackPressed();
                    } catch (Exception e) {
                        Utility.showLog(e.toString());
                    }
                }
        );

        binding.viewTwo.ivBack.setOnClickListener(view ->
                showPage(viewOne, viewTwo,
                        viewThree, viewFour));

        binding.viewThree.ivBack.setOnClickListener(view ->
                showPage(viewTwo, viewOne,
                        viewThree, viewFour));

        binding.viewFour.ivBack.setOnClickListener(view ->
                showPage(viewThree, viewTwo, viewOne,
                        viewFour));

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

        binding.viewTwo.sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Utility.showLog(unitList[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        binding.viewTwo.sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Utility.showLog(unitList[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        arrayAdapter = new ArrayAdapter<>(activity,R.layout.item_spinner,unitList);
        arrayAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        binding.viewTwo.sp1.setAdapter(arrayAdapter);
        binding.viewTwo.sp2.setAdapter(arrayAdapter);

    }
    ArrayAdapter<String> arrayAdapter;
    String[] unitList={"Ω","mΩ","µΩ"};


    private void setViewFourNextButtonStatus(){

//        binding.viewOne.tvTrip.setContentDescription(getString(R.string.selected));
//        binding.viewOne.tvCrm.setContentDescription(getString(R.string.not_selected));

        binding.viewOne.btnNext.setEnabled(binding.viewOne.tvTrip.getContentDescription().equals(getString(R.string.selected)) ||
                binding.viewOne.tvCrm.getContentDescription().equals(getString(R.string.selected)));

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
    }
}