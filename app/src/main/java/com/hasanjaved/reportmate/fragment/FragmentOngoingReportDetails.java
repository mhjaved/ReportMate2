package com.hasanjaved.reportmate.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.adapter.CrmTestRecyclerAdapter;
import com.hasanjaved.reportmate.adapter.TripTestRecyclerAdapter;
import com.hasanjaved.reportmate.databinding.FragmentOngoingReportDetailsBinding;
import com.hasanjaved.reportmate.databinding.FragmentReportSummaryBinding;
import com.hasanjaved.reportmate.listeners.FragmentClickListener;
import com.hasanjaved.reportmate.listeners.HistoryFragmentClickListener;
import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.model.IrTest;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.utility.ImageLoader;
import com.hasanjaved.reportmate.utility.Utility;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;


public class FragmentOngoingReportDetails extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FragmentOngoingReportDetailsBinding binding;
    List<CircuitBreaker> circuitBreakerList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView rvCrmList;
    private CrmTestRecyclerAdapter crmTestRecyclerAdapter;
    private TripTestRecyclerAdapter tripTestRecyclerAdapter;
    private Activity activity;

    private View rootView;
    private String mParam1;
    private String mParam2;
    private HistoryFragmentClickListener fragmentClickListener;
    private Report report;

    public void setFragmentClickListener(HistoryFragmentClickListener fragmentClickListener, Report report) {
        this.fragmentClickListener = fragmentClickListener;
        this.report = report;
    }

    public FragmentOngoingReportDetails() {
        // Required empty public constructor
    }

    public static FragmentOngoingReportDetails newInstance(String param1, String param2) {
        FragmentOngoingReportDetails fragment = new FragmentOngoingReportDetails();
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
        binding = FragmentOngoingReportDetailsBinding.inflate(inflater, container, false);
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
        report = null;
    }

    private void setViewListeners() {

//        binding.btnNext.setOnClickListener(view -> {
////                    getParentFragmentManager().popBackStack();
//                    Toast.makeText(activity, "report generating...", Toast.LENGTH_LONG).show();
////                    fragmentClickListener.generateReport(Utility.getReport(activity));
//                }
//        );

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


    }

    private void setViewListenersIr() {

        //lint to line and line to ground
        binding.viewIrSummary.rlLineToGround.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.expandLineToGround, binding.viewIrSummary.ivArrowLineToGround)
        );

        binding.viewIrSummary.rlLineToLine.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.expandLineToLine, binding.viewIrSummary.ivArrowLineToLine)
        );

        //=================== line to ground inside
        binding.viewIrSummary.groundConnection.connectionOne.rlAConnection.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.groundConnection.connectionOne.expandA, binding.viewIrSummary.groundConnection.connectionOne.ivArrow)
        );
        binding.viewIrSummary.groundConnection.connectionTwo.rlAConnection.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.groundConnection.connectionTwo.expandA, binding.viewIrSummary.groundConnection.connectionTwo.ivArrow)
        );
        binding.viewIrSummary.groundConnection.connectionThree.rlAConnection.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.groundConnection.connectionThree.expandA, binding.viewIrSummary.groundConnection.connectionThree.ivArrow)
        );

        //=================== line to line inside
        binding.viewIrSummary.lineConnection.connectionOne.rlAConnection.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.lineConnection.connectionOne.expandA, binding.viewIrSummary.groundConnection.connectionOne.ivArrow)
        );
        binding.viewIrSummary.lineConnection.connectionTwo.rlAConnection.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.lineConnection.connectionTwo.expandA, binding.viewIrSummary.groundConnection.connectionTwo.ivArrow)
        );
        binding.viewIrSummary.lineConnection.connectionThree.rlAConnection.setOnClickListener(view ->
                setExpandView(binding.viewIrSummary.lineConnection.connectionThree.expandA, binding.viewIrSummary.groundConnection.connectionThree.ivArrow)
        );

    }

    private void setCrmListRv() {

        linearLayoutManager = new LinearLayoutManager(activity);
        binding.viewCrmSummary.rvCrm.setLayoutManager(linearLayoutManager);
        crmTestRecyclerAdapter = new CrmTestRecyclerAdapter(activity, circuitBreakerList, -1, null);
        binding.viewCrmSummary.rvCrm.setAdapter(crmTestRecyclerAdapter);

    }

    private void setTripListRv() {

        linearLayoutManager = new LinearLayoutManager(activity);
        binding.viewTripSummary.rvCrm.setLayoutManager(linearLayoutManager);
        binding.viewTripSummary.rvCrm.setNestedScrollingEnabled(true);
        tripTestRecyclerAdapter = new TripTestRecyclerAdapter(activity, circuitBreakerList, -1, null);
        binding.viewTripSummary.rvCrm.setAdapter(tripTestRecyclerAdapter);

    }

    private void setExpandView(ExpandableLayout expand, ImageView arrow) {

        Utility.showLog("000000000");
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
        if (report != null) {
            setEquipmentSummary(report);
            Utility.showLog(report.toString());
//            setIrData(report);
//            setCrmData(report);
//            setTripData(report);
        }

    }


    private void setEquipmentSummary(Report report) {
        try {

            ImageLoader.showImageFromStorage(activity, binding.viewSummary.sectionSiteDetails.ivSiteDetails, Utility.getTemperatureImage(activity));
            ImageLoader.showImageFromStorage(activity, binding.viewSummary.sectionEquipmentDetails.ivPanelImage, Utility.getPanelImage(activity));
            ImageLoader.showImageFromStorage(activity, binding.viewSummary.sectionEquipmentDetails.ivDbBoxCircuitImage, Utility.getDbBoxCircuitImage(activity));

            binding.viewSummary.viewCustomerDetails.tvCustomerName.setText(report.getCustomerName());
            binding.viewSummary.viewCustomerDetails.tvCustomerAddress.setText(report.getCustomerAddress());
            binding.viewSummary.viewCustomerDetails.tvUser.setText(report.getUserName());
            binding.viewSummary.viewCustomerDetails.tvUserAddress.setText(report.getUserAddress());

            binding.viewSummary.sectionSiteDetails.tvOwner.setText(report.getOwnerIdentification());
            binding.viewSummary.sectionSiteDetails.tvDateOfLastInspection.setText(report.getDateOfLastInspection());
            binding.viewSummary.sectionSiteDetails.tvLastInspectionNo.setText(report.getLastInspectionReportNo());
            binding.viewSummary.sectionSiteDetails.tvAirTemperature.setText(report.getEquipment().getAirTemperature());
            binding.viewSummary.sectionSiteDetails.tvRelativeHumidity.setText(report.getEquipment().getAirHumidity());

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


                List<String> groundConnectionImages = Utility.getIrGroundConnectionImages(activity);
                List<String> groundResultImages = Utility.getIrGroundResultImages(activity);

                List<String> lineConnectionImages = Utility.getIrLineConnectionImages(activity);
                List<String> lineResultImages = Utility.getIrLineResultImages(activity);

                // ground connection images
                ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.groundConnection.connectionOne.ivOne, groundConnectionImages.get(0));
                ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.groundConnection.connectionTwo.ivOne, groundConnectionImages.get(1));
                ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.groundConnection.connectionThree.ivOne, groundConnectionImages.get(2));

                ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.groundConnection.connectionOne.ivTwo, groundResultImages.get(0));
                ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.groundConnection.connectionTwo.ivTwo, groundResultImages.get(1));
                ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.groundConnection.connectionThree.ivTwo, groundResultImages.get(2));


                // line connection images
                ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.lineConnection.connectionOne.ivOne, lineConnectionImages.get(0));
                ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.lineConnection.connectionTwo.ivOne, lineConnectionImages.get(1));
                ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.lineConnection.connectionThree.ivOne, lineConnectionImages.get(2));


                ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.lineConnection.connectionOne.ivTwo, lineResultImages.get(0));
                ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.lineConnection.connectionTwo.ivTwo, lineResultImages.get(1));
                ImageLoader.showImageFromStorage(activity, binding.viewIrSummary.lineConnection.connectionThree.ivTwo, lineResultImages.get(2));

            }
        } catch (Exception e) {
            Utility.showLog(e.toString());
        }
    }

    private void setCrmData(Report report) {
        try {
            circuitBreakerList.clear();
            circuitBreakerList.addAll(report.getEquipment().getCircuitBreakerList());
            crmTestRecyclerAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Utility.showLog(e.toString());
        }
    }

    private void setTripData(Report report) {
        try {
            circuitBreakerList.clear();
            circuitBreakerList.addAll(report.getEquipment().getCircuitBreakerList());
            tripTestRecyclerAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Utility.showLog(e.toString());
        }
    }

}