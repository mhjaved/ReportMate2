package com.hasanjaved.reportmate.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hasanjaved.reportmate.databinding.FragmentNewReportPhaseOneBinding;
import com.hasanjaved.reportmate.listeners.FragmentClickListener;
import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.utility.Utility;


public class NewReportFragmentPhaseOne extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View rootView, viewOne, viewTwo, viewThree, viewFour, viewFive;
    private FragmentNewReportPhaseOneBinding binding;
    private String mParam1;
    private String mParam2;

    private Activity activity;

    public NewReportFragmentPhaseOne() {
    }
    private FragmentClickListener fragmentClickListener;

    public void setFragmentClickListener(FragmentClickListener fragmentClickListener) {
        this.fragmentClickListener = fragmentClickListener;
    }

    public static NewReportFragmentPhaseOne newInstance(String param1, String param2) {
        NewReportFragmentPhaseOne fragment = new NewReportFragmentPhaseOne();
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
        binding = FragmentNewReportPhaseOneBinding.inflate(inflater, container, false);

        rootView = binding.getRoot();

        viewOne = rootView.findViewById(R.id.viewOne);
        viewTwo = rootView.findViewById(R.id.viewTwo);
        viewThree = rootView.findViewById(R.id.viewThree);
        viewFour = rootView.findViewById(R.id.viewFour);
        viewFive = rootView.findViewById(R.id.viewFive);

        binding.viewOne.btnNext.setOnClickListener(view ->
                showPage(viewTwo, viewOne,
                        viewThree, viewFour, viewFive));

        binding.viewTwo.btnNext.setOnClickListener(view ->
                showPage(viewThree, viewOne,
                        viewTwo, viewFour, viewFive));

        binding.viewThree.btnNext.setOnClickListener(view ->
                showPage(viewFour, viewOne,
                        viewTwo, viewThree, viewFive));

        binding.viewFour.btnNext.setOnClickListener(view ->
                showPage(viewFive, viewOne,
                        viewTwo, viewThree, viewFour));


        binding.viewFive.btnNext.setOnClickListener(view ->
                {
                    if (fragmentClickListener != null) {
                        fragmentClickListener.addFragment();
                    }
                }
                );

        binding.viewFour.imgCamera.setOnClickListener(view ->
                fragmentClickListener.openCamera()
                );

        binding.viewOne.ivBack.setOnClickListener(view -> {
                    try {
                       activity.finish();
                    } catch (Exception e) {
                        Utility.showLog(e.toString());
                    }
                }
        );

        binding.viewTwo.ivBack.setOnClickListener(view ->
                showPage(viewOne,viewTwo,
                        viewThree, viewFour, viewFive));

        binding.viewThree.ivBack.setOnClickListener(view ->
                showPage(viewTwo, viewOne,
                        viewThree, viewFour, viewFive));

        binding.viewFour.ivBack.setOnClickListener(view ->
                showPage( viewThree,viewTwo, viewOne,
                        viewFour, viewFive));

        binding.viewFive.ivBack.setOnClickListener(view ->
                showPage( viewFour,viewTwo, viewOne,
                        viewThree, viewFive));

        return binding.getRoot();
    }

    private void showPage(View visible, View hide1, View hide2, View hide3, View hide4) {
        visible.setVisibility(View.VISIBLE);
        hide1.setVisibility(View.GONE);
        hide2.setVisibility(View.GONE);
        hide3.setVisibility(View.GONE);
        hide4.setVisibility(View.GONE);
    }

}