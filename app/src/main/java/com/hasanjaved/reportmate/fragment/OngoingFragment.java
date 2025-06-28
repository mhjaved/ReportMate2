package com.hasanjaved.reportmate.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.hasanjaved.reportmate.databinding.FragmentHomeBinding;
import com.hasanjaved.reportmate.databinding.FragmentOngoingReportsBinding;
import com.hasanjaved.reportmate.listeners.HomeFragmentClickListener;
import com.hasanjaved.reportmate.model.Employee;
import com.hasanjaved.reportmate.utility.Utility;

public class OngoingFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private HomeFragmentClickListener homeFragmentClickListener;
    private FragmentOngoingReportsBinding binding;
    private Activity activity;


    private String mParam1;
    private String mParam2;

    public OngoingFragment() {

    }


    public static OngoingFragment newInstance(String param1, String param2) {
        OngoingFragment fragment = new OngoingFragment();
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
        binding = FragmentOngoingReportsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setViewListeners();
        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setViewListeners() {
        binding.ivBack.setOnClickListener(view -> {
            closeFragment();
        });
    }

    private void closeFragment(){
        getParentFragmentManager().popBackStack();
    }

    public void setFragmentClickListener(HomeFragmentClickListener homeFragmentClickListener) {
        this.homeFragmentClickListener = homeFragmentClickListener;
    }

}