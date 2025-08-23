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
import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.adapter.OngoingReportCrmTestRecyclerAdapter;
import com.hasanjaved.reportmate.adapter.OngoingReportTripTestRecyclerAdapter;
import com.hasanjaved.reportmate.databinding.FragmentHistoryReportDetailsBinding;
import com.hasanjaved.reportmate.databinding.FragmentOngoingReportDetailsBinding;
import com.hasanjaved.reportmate.listeners.CameraFragmentClickListener;
import com.hasanjaved.reportmate.listeners.EditRecyclerViewClickListener;
import com.hasanjaved.reportmate.listeners.HistoryFragmentClickListener;
import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.model.IrTest;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.utility.DirectoryManager;
import com.hasanjaved.reportmate.utility.FileMover;
import com.hasanjaved.reportmate.utility.ImageLoader;
import com.hasanjaved.reportmate.utility.PopupManager;
import com.hasanjaved.reportmate.utility.Utility;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;


public class FragmentHistoryReportDetails extends Fragment implements PopupManager.EditPopupListener, EditRecyclerViewClickListener, CameraFragmentClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentHistoryReportDetailsBinding binding;
    private List<CircuitBreaker> circuitBreakerList = new ArrayList<>();
    private String equipmentName = "";
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView rvCrmList;
    private OngoingReportCrmTestRecyclerAdapter ongoingReportCrmTestRecyclerAdapter;
    private OngoingReportTripTestRecyclerAdapter ongoingReportTripTestRecyclerAdapter;
    private Activity activity;
    private View rootView;
    private String mParam1;
    private String mParam2;
    private HistoryFragmentClickListener fragmentClickListener;
    private int reportIndex;

    public void setFragmentClickListener(HistoryFragmentClickListener fragmentClickListener, int reportIndex) {
        this.fragmentClickListener = fragmentClickListener;
        this.reportIndex = reportIndex;
        Utility.showLog("setFragmentClickListener " + reportIndex);
    }

    public FragmentHistoryReportDetails() {
        // Required empty public constructor
    }

    public static FragmentHistoryReportDetails newInstance(String param1, String param2) {
        FragmentHistoryReportDetails fragment = new FragmentHistoryReportDetails();
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
        // Inflate the layout for this fragment
        binding = FragmentHistoryReportDetailsBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();

        setViewListeners();
        setViewListenersIr();
        setCrmListRv();
        setTripListRv();

        setPageData();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        fragmentClickListener = null;
//        reportIndex = null;
    }

    private void setViewListeners() {

        binding.btnAddToHistory.setOnClickListener(view -> {
            Utility.showToast(activity,"Report added to history");
            Utility.saveReportHistory(activity,getReport(reportIndex));
                }
        );

        binding.btnGenerateReport.setOnClickListener(view -> {
                    fragmentClickListener.generateReport(getReport(reportIndex));
                }
        );

        binding.ivBack.setOnClickListener(view -> {
                    getParentFragmentManager().popBackStack();
                }
        );

        binding.viewSummary.rlCustomerDetails.setOnClickListener(view ->
                setExpandView(binding.viewSummary.expandCustomer, binding.viewSummary.ivArrowCustomer)
        );

        binding.viewSummary.rlSideDetails.setOnClickListener(view ->
                setExpandView(binding.viewSummary.expandSite, binding.viewSummary.ivArrowSite)
        );

        binding.viewSummary.rlEquipment.setOnClickListener(view ->
                setExpandView(binding.viewSummary.expandEquipment, binding.viewSummary.ivArrowEquipment)
        );


        //--------------------------------------- db box image actions
        binding.viewSummary.sectionSiteDetails.ivSiteDetails.setOnClickListener(view -> {
                    fragmentClickListener.openCamera(this, binding.viewSummary.sectionSiteDetails.ivSiteDetails,
                            DirectoryManager.generalImageTemperature, getGeneralImageDirectory());
                }
        );
        binding.viewSummary.sectionEquipmentDetails.ivPanelImage.setOnClickListener(view -> {
                    fragmentClickListener.openCamera(this, binding.viewSummary.sectionEquipmentDetails.ivPanelImage,
                            DirectoryManager.dbBoxPanelFront, getGeneralImageDirectory());
                }
        );

        binding.viewSummary.sectionEquipmentDetails.ivDbBoxCircuitImage.setOnClickListener(view -> {
                    fragmentClickListener.openCamera(this, binding.viewSummary.sectionEquipmentDetails.ivDbBoxCircuitImage,
                            DirectoryManager.dbBoxPanelInside, getGeneralImageDirectory());
                }
        );

        //--------------------------------------------------- set IR image actions
//        String equipmentName = report.getEquipment().getEquipmentName();
        // ground connection images


        // ------------------------------------- AG BG CG CONNECTION
        binding.viewIrSummary.groundConnection.connectionOne.ivOne.setOnClickListener(view -> {
                    fragmentClickListener.openCamera(this, binding.viewIrSummary.groundConnection.connectionOne.ivOne,
                            DirectoryManager.imgAgConnection, DirectoryManager.getIrFolderLinkAG(getReport(reportIndex)));
                }
        );
        binding.viewIrSummary.groundConnection.connectionTwo.ivOne.setOnClickListener(view -> {
                    fragmentClickListener.openCamera(this, binding.viewIrSummary.groundConnection.connectionTwo.ivOne,
                            DirectoryManager.imgBgConnection, DirectoryManager.getIrFolderLinkBG(getReport(reportIndex)));
                }
        );

        binding.viewIrSummary.groundConnection.connectionThree.ivOne.setOnClickListener(view -> {
                    fragmentClickListener.openCamera(this, binding.viewIrSummary.groundConnection.connectionThree.ivOne,
                            DirectoryManager.imgCgConnection, DirectoryManager.getIrFolderLinkCG(getReport(reportIndex)));
                }
        );


        // ------------------------------------- AG BG CG RESULT

        binding.viewIrSummary.groundConnection.connectionOne.ivTwo.setOnClickListener(view -> {
                    fragmentClickListener.openCamera(this, binding.viewIrSummary.groundConnection.connectionOne.ivTwo,
                            DirectoryManager.imgAgResult, DirectoryManager.getIrFolderLinkAG(getReport(reportIndex)));
                }
        );
        binding.viewIrSummary.groundConnection.connectionTwo.ivTwo.setOnClickListener(view -> {
                    fragmentClickListener.openCamera(this, binding.viewIrSummary.groundConnection.connectionTwo.ivTwo,
                            DirectoryManager.imgBgResult, DirectoryManager.getIrFolderLinkBG(getReport(reportIndex)));
                }
        );
        binding.viewIrSummary.groundConnection.connectionThree.ivTwo.setOnClickListener(view -> {
                    fragmentClickListener.openCamera(this, binding.viewIrSummary.groundConnection.connectionThree.ivTwo,
                            DirectoryManager.imgCgResult, DirectoryManager.getIrFolderLinkCG(getReport(reportIndex)));
                }
        );


        // ------------------------------------- AB BC CA CONNECTION

        binding.viewIrSummary.lineConnection.connectionOne.ivOne.setOnClickListener(view -> {
                    fragmentClickListener.openCamera(this, binding.viewIrSummary.lineConnection.connectionOne.ivOne,
                            DirectoryManager.imgAbConnection, DirectoryManager.getIrFolderLinkAB(getReport(reportIndex)));
                }
        );
        binding.viewIrSummary.lineConnection.connectionTwo.ivOne.setOnClickListener(view -> {
                    fragmentClickListener.openCamera(this, binding.viewIrSummary.lineConnection.connectionTwo.ivOne,
                            DirectoryManager.imgBcConnection, DirectoryManager.getIrFolderLinkBC(getReport(reportIndex)));
                }
        );
        binding.viewIrSummary.lineConnection.connectionThree.ivOne.setOnClickListener(view -> {
                    fragmentClickListener.openCamera(this, binding.viewIrSummary.lineConnection.connectionThree.ivOne,
                            DirectoryManager.imgCaConnection, DirectoryManager.getIrFolderLinkCA(getReport(reportIndex)));
                }
        );

        // ------------------------------------- AB BC CA RESULT

        binding.viewIrSummary.lineConnection.connectionOne.ivTwo.setOnClickListener(view -> {
                    fragmentClickListener.openCamera(this, binding.viewIrSummary.lineConnection.connectionOne.ivTwo,
                            DirectoryManager.imgAbResult, DirectoryManager.getIrFolderLinkAB(getReport(reportIndex)));
                }
        );
        binding.viewIrSummary.lineConnection.connectionTwo.ivTwo.setOnClickListener(view -> {
                    fragmentClickListener.openCamera(this, binding.viewIrSummary.lineConnection.connectionTwo.ivTwo,
                            DirectoryManager.imgBcResult, DirectoryManager.getIrFolderLinkBC(getReport(reportIndex)));
                }
        );
        binding.viewIrSummary.lineConnection.connectionThree.ivTwo.setOnClickListener(view -> {
                    fragmentClickListener.openCamera(this, binding.viewIrSummary.lineConnection.connectionThree.ivTwo,
                            DirectoryManager.imgCaResult, DirectoryManager.getIrFolderLinkCA(getReport(reportIndex)));
                }
        );


    }

    private String getGeneralImageDirectory() {
        return DirectoryManager.getGeneralImageDirectory(getReport(reportIndex).getEquipment().getEquipmentName());
    }

    private void setViewListenersIr() {

        //lint to line and line to ground
        binding.viewIrSummary.rlLineToGround.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.expandLineToGround, binding.viewIrSummary.ivArrowLineToGround)
        );

        binding.viewIrSummary.rlLineToLine.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.expandLineToLine, binding.viewIrSummary.ivArrowLineToLine)
        );

        //--------------------------------- line to ground inside
        binding.viewIrSummary.groundConnection.connectionOne.rlAConnection.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.groundConnection.connectionOne.expandA, binding.viewIrSummary.groundConnection.connectionOne.ivArrow)
        );
        binding.viewIrSummary.groundConnection.connectionTwo.rlAConnection.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.groundConnection.connectionTwo.expandA, binding.viewIrSummary.groundConnection.connectionTwo.ivArrow)
        );
        binding.viewIrSummary.groundConnection.connectionThree.rlAConnection.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.groundConnection.connectionThree.expandA, binding.viewIrSummary.groundConnection.connectionThree.ivArrow)
        );

        //--------------------------------- line to line inside -----------------------------------
        binding.viewIrSummary.lineConnection.connectionOne.rlAConnection.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.lineConnection.connectionOne.expandA, binding.viewIrSummary.lineConnection.connectionOne.ivArrow)
        );
        binding.viewIrSummary.lineConnection.connectionTwo.rlAConnection.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.lineConnection.connectionTwo.expandA, binding.viewIrSummary.lineConnection.connectionTwo.ivArrow)
        );
        binding.viewIrSummary.lineConnection.connectionThree.rlAConnection.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.lineConnection.connectionThree.expandA, binding.viewIrSummary.lineConnection.connectionThree.ivArrow)
        );

        //---------------------------------show pop ups ---------------------------------------------
        binding.viewSummary.ivEditCustomer.setOnClickListener(view ->
                PopupManager.showEditCustomerPopupHistory(activity, getReport(reportIndex), this)
        );
        binding.viewSummary.ivEditSite.setOnClickListener(view ->
                PopupManager.showEditSitePopupHistory(activity, getReport(reportIndex), this)
        );

        binding.viewIrSummary.ivEditLineToGround.setOnClickListener(view ->
                PopupManager.showEditIrPopupGroundHistory(activity, getReport(reportIndex), this)
        );
        binding.viewIrSummary.ivEditLineToLine.setOnClickListener(view ->
                PopupManager.showEditIrPopupLineHistory(activity, getReport(reportIndex), this)
        );

    }

    private Report getReport(int reportIndex) {
        return Utility.getHistoryReportList(activity).getReportHistoryList().get(reportIndex);
    }

    private void setCrmListRv() {
        linearLayoutManager = new LinearLayoutManager(activity);
        binding.viewCrmSummary.rvCrm.setLayoutManager(linearLayoutManager);
        ongoingReportCrmTestRecyclerAdapter = new OngoingReportCrmTestRecyclerAdapter(activity, circuitBreakerList, -1, this);
        binding.viewCrmSummary.rvCrm.setAdapter(ongoingReportCrmTestRecyclerAdapter);
    }

    private void setTripListRv() {
        linearLayoutManager = new LinearLayoutManager(activity);
        binding.viewTripSummary.rvCrm.setLayoutManager(linearLayoutManager);
        binding.viewTripSummary.rvCrm.setNestedScrollingEnabled(true);
        ongoingReportTripTestRecyclerAdapter = new OngoingReportTripTestRecyclerAdapter(activity, circuitBreakerList, -1, this);
        binding.viewTripSummary.rvCrm.setAdapter(ongoingReportTripTestRecyclerAdapter);

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

    private void setPageData() {
//        Report report = Utility.getReport(activity);
        Report report = getReport(reportIndex);
        Utility.showLog("setPageData " + report);

        if (report != null) {
            setEquipmentSummary(report);
            setIrData(report);
            setCrmData(report);
            setTripData(report);
        }

    }


    private void setEquipmentSummary(Report report) {
        try {

            String equipmentName = report.getEquipment().getEquipmentName();
            ImageLoader.showImageFromStorage(activity, binding.viewSummary.sectionSiteDetails.ivSiteDetails, DirectoryManager.getTemperatureImage(equipmentName));
            ImageLoader.showImageFromStorage(activity, binding.viewSummary.sectionEquipmentDetails.ivPanelImage, DirectoryManager.getPanelImage(equipmentName));
            ImageLoader.showImageFromStorage(activity, binding.viewSummary.sectionEquipmentDetails.ivDbBoxCircuitImage, DirectoryManager.getDbBoxCircuitImage(equipmentName));

            binding.viewSummary.viewCustomerDetails.tvCustomerName.setText(report.getCustomerName());
            binding.viewSummary.viewCustomerDetails.tvCustomerAddress.setText(report.getCustomerAddress());
            binding.viewSummary.viewCustomerDetails.tvUser.setText(report.getUserName());
            binding.viewSummary.viewCustomerDetails.tvUserAddress.setText(report.getUserAddress());

            binding.viewSummary.sectionSiteDetails.tvOwner.setText(report.getOwnerIdentification());
            binding.viewSummary.sectionSiteDetails.tvDateOfLastInspection.setText(report.getDateOfLastInspection());
            binding.viewSummary.sectionSiteDetails.tvLastInspectionNo.setText(report.getLastInspectionReportNo());
//            binding.viewSummary.sectionSiteDetails.tvAirTemperature.setText(report.getEquipment().getAirTemperature());
//            binding.viewSummary.sectionSiteDetails.tvRelativeHumidity.setText(report.getEquipment().getAirHumidity());
            binding.viewSummary.sectionSiteDetails.tvAirTemperature.setText(report.getEquipment().getAirTemperature()+"°C");
            binding.viewSummary.sectionSiteDetails.tvRelativeHumidity.setText(report.getEquipment().getAirHumidity()+"%");

            binding.viewSummary.sectionEquipmentDetails.tvEquipmentLocation.setText(report.getEquipment().getEquipmentLocation());
            binding.viewSummary.sectionEquipmentDetails.tvEquipmentName.setText(report.getEquipment().getEquipmentName());

        } catch (Exception e) {
            Utility.showLog("Exception e  " + e);
        }
    }

    private void setIrData(Report report) {
        try {
            //image label of lint ot ground
            binding.viewIrSummary.groundConnection.connectionOne.tvImageOneTitle.setText(getString(R.string.ir_test_connection_img_a_g));
            binding.viewIrSummary.groundConnection.connectionOne.tvImageTwoTitle.setText(getString(R.string.ir_test_result_img_a_g));

            binding.viewIrSummary.groundConnection.connectionTwo.tvImageOneTitle.setText(getString(R.string.ir_test_connection_img_b_g));
            binding.viewIrSummary.groundConnection.connectionTwo.tvImageTwoTitle.setText(getString(R.string.ir_test_result_img_b_g));

            binding.viewIrSummary.groundConnection.connectionThree.tvImageOneTitle.setText(getString(R.string.ir_test_connection_img_c_g));
            binding.viewIrSummary.groundConnection.connectionThree.tvImageTwoTitle.setText(getString(R.string.ir_test_result_img_c_g));

            //image label of lint ot lint
            binding.viewIrSummary.lineConnection.connectionOne.tvImageOneTitle.setText(getString(R.string.ir_test_connection_img_a_b));
            binding.viewIrSummary.lineConnection.connectionOne.tvImageTwoTitle.setText(getString(R.string.ir_test_result_img_a_b));

            binding.viewIrSummary.lineConnection.connectionTwo.tvImageOneTitle.setText(getString(R.string.ir_test_connection_img_b_c));
            binding.viewIrSummary.lineConnection.connectionTwo.tvImageTwoTitle.setText(getString(R.string.ir_test_result_img_b_c));

            binding.viewIrSummary.lineConnection.connectionThree.tvImageOneTitle.setText(getString(R.string.ir_test_connection_img_c_a));
            binding.viewIrSummary.lineConnection.connectionThree.tvImageTwoTitle.setText(getString(R.string.ir_test_result_img_c_a));


            //
            binding.viewIrSummary.groundConnection.connectionOne.tvConnectionName.setText(getString(R.string.a_g_connection));
            binding.viewIrSummary.groundConnection.connectionTwo.tvConnectionName.setText(getString(R.string.b_g_connection));
            binding.viewIrSummary.groundConnection.connectionThree.tvConnectionName.setText(getString(R.string.c_g_connection));

            //
            binding.viewIrSummary.lineConnection.connectionOne.tvConnectionName.setText(getString(R.string.a_b_connection));
            binding.viewIrSummary.lineConnection.connectionTwo.tvConnectionName.setText(getString(R.string.b_c_connection));
            binding.viewIrSummary.lineConnection.connectionThree.tvConnectionName.setText(getString(R.string.c_a_connection));

            //
            binding.viewIrSummary.groundConnection.connectionOne.tvLabel.setText(getString(R.string.a_g_));
            binding.viewIrSummary.groundConnection.connectionTwo.tvLabel.setText(getString(R.string.b_g_));
            binding.viewIrSummary.groundConnection.connectionThree.tvLabel.setText(getString(R.string.c_g_));

            binding.viewIrSummary.lineConnection.connectionOne.tvLabel.setText(getString(R.string.a_b_));
            binding.viewIrSummary.lineConnection.connectionTwo.tvLabel.setText(getString(R.string.b_c_));
            binding.viewIrSummary.lineConnection.connectionThree.tvLabel.setText(getString(R.string.c_a_));


            IrTest irTest = report.getIrTest();
            if (irTest != null) {
                binding.viewIrSummary.groundConnection.connectionOne.tvValue.setText(irTest.getAgValue());
                binding.viewIrSummary.groundConnection.connectionTwo.tvValue.setText(irTest.getBgValue());
                binding.viewIrSummary.groundConnection.connectionThree.tvValue.setText(irTest.getCgValue());

                binding.viewIrSummary.lineConnection.connectionOne.tvValue.setText(irTest.getAbValue());
                binding.viewIrSummary.lineConnection.connectionTwo.tvValue.setText(irTest.getBcValue());
                binding.viewIrSummary.lineConnection.connectionThree.tvValue.setText(irTest.getCaValue());
            }

            String equipmentName = report.getEquipment().getEquipmentName();
            // ground connection images
            ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.groundConnection.connectionOne.ivOne, DirectoryManager.getIrAgConnectionImages(equipmentName));
            ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.groundConnection.connectionTwo.ivOne, DirectoryManager.getIrBgConnectionImages(equipmentName));
            ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.groundConnection.connectionThree.ivOne, DirectoryManager.getIrCgConnectionImages(equipmentName));

            ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.groundConnection.connectionOne.ivTwo, DirectoryManager.getIrAgResultImages(equipmentName));
            ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.groundConnection.connectionTwo.ivTwo, DirectoryManager.getIrBgResultImages(equipmentName));
            ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.groundConnection.connectionThree.ivTwo, DirectoryManager.getIrCgResultImages(equipmentName));


            // line connection images
            ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.lineConnection.connectionOne.ivOne, DirectoryManager.getIrAbConnectionImages(equipmentName));
            ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.lineConnection.connectionTwo.ivOne, DirectoryManager.getIrBcConnectionImages(equipmentName));
            ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.lineConnection.connectionThree.ivOne, DirectoryManager.getIrCaConnectionImages(equipmentName));


            ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.lineConnection.connectionOne.ivTwo, DirectoryManager.getIrAbResultImages(equipmentName));
            ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.lineConnection.connectionTwo.ivTwo, DirectoryManager.getIrBcResultImages(equipmentName));
            ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.lineConnection.connectionThree.ivTwo, DirectoryManager.getIrCaResultImages(equipmentName));

        } catch (Exception e) {
            Utility.showLog("setIrData " + e.toString());
        }
    }

    private void setCrmData(Report report) {
        try {
            circuitBreakerList.clear();
            equipmentName = report.getEquipment().getEquipmentName();
            circuitBreakerList.addAll(report.getEquipment().getCircuitBreakerList());
            ongoingReportCrmTestRecyclerAdapter.notifyDataSetChanged();

            Utility.showLog("setCrmData " + report.getEquipment().getCircuitBreakerList());

        } catch (Exception e) {
            Utility.showLog("setCrmData " + e.toString());
        }
    }

    private void setTripData(Report report) {
        try {
            circuitBreakerList.clear();
            equipmentName = report.getEquipment().getEquipmentName();
            circuitBreakerList.addAll(report.getEquipment().getCircuitBreakerList());
            ongoingReportTripTestRecyclerAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Utility.showLog(e.toString());
        }
    }


    @Override
    public void onItemClicked(int index) {

    }

    @Override
    public void onTripEditClicked(List<CircuitBreaker> list, int index) {
        PopupManager.showEditTripPopupLineHistory(activity, getReport(reportIndex), list, index, this);
    }

    @Override
    public void onCrmEditClicked(List<CircuitBreaker> list, int index) {
        PopupManager.showEditCrmPopupLineHistory(activity, getReport(reportIndex), list, index, this);
    }


    @Override
    public void onDeleteClicked(int index) {

    }

    @Override
    public void onImageClicked(ImageView imageView, String imageName, String imageLocation) {
        fragmentClickListener.openCamera(this, imageView,
                imageName, imageLocation);
    }


    @Override
    public void savedCustomerDetails() {
        setEquipmentSummary(getReport(reportIndex));
    }

    @Override
    public void savedSiteDetails() {
        setEquipmentSummary(getReport(reportIndex));

    }

    @Override
    public void savedIrTestData() {

        setIrData(getReport(reportIndex));
    }

    @Override
    public void savedCrmTestData() {

        setCrmData(getReport(reportIndex));

    }

    @Override
    public void savedTripTestData() {
        setTripData(getReport(reportIndex));
    }

    @Override
    public void onCancelPressed() {

    }

    @Override
    public void onSaveButtonPressed(ImageView imageView, String imageLocation, String imageName, String subFolder) {
        ImageLoader.showImageFromCamera(activity,imageView,imageLocation);
    }
}