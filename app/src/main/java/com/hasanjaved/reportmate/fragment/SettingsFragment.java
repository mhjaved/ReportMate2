package com.hasanjaved.reportmate.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.listeners.OnSettingsItemClickedListener;


public class SettingsFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private OnSettingsItemClickedListener mCallback;

    private String mParam1;
    private String mParam2;
    private View rootView;

    public SettingsFragment() {
        // Required empty public constructor
    }


    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        rootView.findViewById(R.id.llHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCallback!=null)
                    mCallback.onSettingsItemClicked("home button");
            }
        });

        rootView.findViewById(R.id.btnLogout).setOnClickListener(view -> {
            if(mCallback!=null)
                mCallback.logOut("logOut");
        });

        return rootView;

    }

    public void setCallback(OnSettingsItemClickedListener callback) {
        mCallback = callback;
    }



}