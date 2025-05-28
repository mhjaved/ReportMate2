package com.hasanjaved.reportmate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.cachapa.expandablelayout.ExpandableLayout;


public class DesignFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DesignFragment() {
    }


    public static DesignFragment newInstance(String param1, String param2) {
        DesignFragment fragment = new DesignFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.test_layout, container, false);
//        ProgressBar progressBar = rootView.findViewById(R.id.progressBarThree);
//        progressBar.setProgress(20);
        ExpandableLayout expandableLayout = rootView.findViewById(R.id.expandAG);
        ImageView iv = rootView.findViewById(R.id.ivAG);

        rootView.findViewById(R.id.agLabel).setOnClickListener(view -> {
            if (expandableLayout.isExpanded()) {
                iv.animate().rotation(180).start();
                expandableLayout.collapse();
            } else {
                expandableLayout.expand();
                iv.animate().rotation(0).start();
            }
        });




        return rootView;
    }
}