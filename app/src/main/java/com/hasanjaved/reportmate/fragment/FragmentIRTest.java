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
import com.hasanjaved.reportmate.adapter.CircuitListRecyclerAdapter;
import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.data_manager.TestData;
import com.hasanjaved.reportmate.databinding.FragmentIrTestBinding;
import com.hasanjaved.reportmate.listeners.CameraFragmentClickListener;
import com.hasanjaved.reportmate.listeners.FragmentClickListener;
import com.hasanjaved.reportmate.listeners.RecyclerViewClickListener;
import com.hasanjaved.reportmate.utility.DirectoryManager;
import com.hasanjaved.reportmate.utility.FileMover;
import com.hasanjaved.reportmate.utility.Utility;
import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Objects;


public class FragmentIRTest extends Fragment implements RecyclerViewClickListener , CameraFragmentClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Activity activity;

    private LinearLayoutManager linearLayoutManager;

    private View rootView, viewOne, viewTwo;
//    , viewThree, viewFour;
    private FragmentIrTestBinding binding;
    private String mParam1;
    private String mParam2;

    private RecyclerView rvList;
    private CircuitListRecyclerAdapter circuitListRecyclerAdapter;

    public FragmentIRTest() {
    }

    private FragmentClickListener fragmentClickListener;

    public void setFragmentClickListener(FragmentClickListener fragmentClickListener) {
        this.fragmentClickListener = fragmentClickListener;
    }

    public static FragmentIRTest newInstance(String param1, String param2) {
        FragmentIRTest fragment = new FragmentIRTest();
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

        binding = FragmentIrTestBinding.inflate(inflater, container, false);

        rootView = binding.getRoot();

        viewOne = rootView.findViewById(R.id.viewOne);
        viewTwo = rootView.findViewById(R.id.viewTwo);
//        viewThree = rootView.findViewById(R.id.viewThree);
//        viewFour = rootView.findViewById(R.id.viewFour);


        setViewListeners();


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void setViewListeners() {

        binding.viewOne.btnNext.setOnClickListener(view ->{
            saveIrGroundConnectionData();
                    showPage(viewTwo, viewOne);
                }
               );

        binding.viewTwo.btnNext.setOnClickListener(view ->{
                    saveIrLineConnectionData();
                    getParentFragmentManager().popBackStack();
                    fragmentClickListener.addSummaryReportFragment();
                }
          );


        binding.viewOne.ivBack.setOnClickListener(view -> {
                    try {
                        activity.onBackPressed();
                    } catch (Exception e) {
                        Utility.showLog(e.toString());
                    }
                }
        );

        binding.viewTwo.ivBack.setOnClickListener(view ->
                showPage(viewOne, viewTwo));


        binding.viewOne.rlAg.setOnClickListener(view ->
                setExpandView(binding.viewOne.expandAg, binding.viewOne.ivArrowAg)
        );


        binding.viewOne.rlBg.setOnClickListener(view ->
                setExpandView(binding.viewOne.expandBg, binding.viewOne.ivArrowBg)
        );


        binding.viewOne.rlCg.setOnClickListener(view ->
                setExpandView(binding.viewOne.expandCg, binding.viewOne.ivArrowCg)
        );



        //----------------------------step two-------------------------------------

        binding.viewTwo.rlAb.setOnClickListener(view ->
                setExpandView(binding.viewTwo.expandAb, binding.viewTwo.ivArrowAb)
        );

        binding.viewTwo.rlBc.setOnClickListener(view ->
                setExpandView(binding.viewTwo.expandBc, binding.viewTwo.ivArrowBc)
        );

        binding.viewTwo.rlCa.setOnClickListener(view ->
                setExpandView(binding.viewTwo.expandCa, binding.viewTwo.ivArrowCa)
        );

        setPageOneCamera();
        setPageTwoCamera();

    }

    private void saveIrGroundConnectionData() {
        TestData.saveIrTestGroundConnectionData(activity,
                binding.viewOne.etAG.getText().toString().trim(),
                binding.viewOne.etBG.getText().toString().trim(),
                binding.viewOne.etCG.getText().toString().trim());
    }

    private void saveIrLineConnectionData() {
        TestData.saveIrTestLineConnectionData(activity,
                binding.viewTwo.etAB.getText().toString().trim(),
                binding.viewTwo.etBC.getText().toString().trim(),
                binding.viewTwo.etCA.getText().toString().trim());
    }

    private void setPageOneCamera(){
        //------------------------ set camera

        // -------------------------------------  AG
        binding.viewOne.imgCameraAgConnection.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewOne.ivShowImageAgConnection,
                        DirectoryManager.imgAgConnection, DirectoryManager.getIrFolderLinkAG( Objects.requireNonNull(Utility.getReport(activity))))
        );

        binding.viewOne.imgCameraAgResult.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewOne.ivShowImageAgResult,
                        DirectoryManager.imgAgResult, DirectoryManager.getIrFolderLinkAG(Objects.requireNonNull(Utility.getReport(activity))))
        );


        // -------------------------------------  BG
        binding.viewOne.imgCameraBgConnection.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewOne.ivShowImageBgConnection,
                        DirectoryManager.imgBgConnection, DirectoryManager.getIrFolderLinkBG( Objects.requireNonNull(Utility.getReport(activity))))
        );

        binding.viewOne.imgCameraBgResult.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewOne.ivShowImageBgResult,
                        DirectoryManager.imgBgResult, DirectoryManager.getIrFolderLinkBG( Objects.requireNonNull(Utility.getReport(activity))))
        );


        // -------------------------------------  CG

        binding.viewOne.imgCameraCgConnection.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewOne.ivShowImageCgConnection,
                        DirectoryManager.imgCgConnection, DirectoryManager.getIrFolderLinkCG( Objects.requireNonNull(Utility.getReport(activity))))
        );

        binding.viewOne.imgCameraCgResult.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewOne.ivShowImageCgResult,
                        DirectoryManager.imgCgResult, DirectoryManager.getIrFolderLinkCG( Objects.requireNonNull(Utility.getReport(activity))))
        );


    }

    private void setPageTwoCamera(){
        //------------------------ set camera
        // -------------------------------------  AB

        binding.viewTwo.imgCameraAbConnection.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewTwo.ivShowImageAbConnection,
                        DirectoryManager.imgAbConnection, DirectoryManager.getIrFolderLinkAB( Objects.requireNonNull(Utility.getReport(activity))))
        );

        binding.viewTwo.imgCameraAbResult.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewTwo.ivShowImageAbResult,
                        DirectoryManager.imgAbResult, DirectoryManager.getIrFolderLinkAB( Objects.requireNonNull(Utility.getReport(activity))))
        );



        // -------------------------------------  BC

        binding.viewTwo.imgCameraBcConnection.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewTwo.ivShowImageBcConnection,
                        DirectoryManager.imgBcConnection, DirectoryManager.getIrFolderLinkBC( Objects.requireNonNull(Utility.getReport(activity))))
        );

        binding.viewTwo.imgCameraBcResult.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewTwo.ivShowImageBcResult,
                        DirectoryManager.imgBcResult, DirectoryManager.getIrFolderLinkBC( Objects.requireNonNull(Utility.getReport(activity))))
        );



        // -------------------------------------  CA
        binding.viewTwo.imgCameraCaConnection.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewTwo.ivShowImageCaConnection,
                        DirectoryManager.imgCaConnection, DirectoryManager.getIrFolderLinkCA( Objects.requireNonNull(Utility.getReport(activity))))
        );

        binding.viewTwo.imgCameraCaResult.setOnClickListener(view ->
                fragmentClickListener.openCamera(this, binding.viewTwo.ivShowImageCaResult,
                        DirectoryManager.imgCaResult, DirectoryManager.getIrFolderLinkCA( Objects.requireNonNull(Utility.getReport(activity))))
        );

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

    private void showPage(View visible, View hide1) {
        visible.setVisibility(View.VISIBLE);
        hide1.setVisibility(View.GONE);

    }

    @Override
    public void onItemClicked(int index) {
        circuitListRecyclerAdapter.setSelectedItem(index);
        circuitListRecyclerAdapter.notifyDataSetChanged();
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