package com.hasanjaved.reportmate.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.hasanjaved.reportmate.adapter.CircuitListRecyclerAdapter;
import com.hasanjaved.reportmate.data_manager.ReportGeneralData;
import com.hasanjaved.reportmate.listeners.FragmentClickListener;
import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.databinding.FragmentNewReportPhaseTwoBinding;
import com.hasanjaved.reportmate.listeners.RecyclerViewClickListener;
import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.utility.DirectoryManager;
import com.hasanjaved.reportmate.utility.ImageLoader;
import com.hasanjaved.reportmate.utility.PopupManager;
import com.hasanjaved.reportmate.utility.Utility;
import net.cachapa.expandablelayout.ExpandableLayout;
import java.util.ArrayList;
import java.util.List;

public class NewReportFragmentPhaseTwo extends Fragment implements RecyclerViewClickListener, PopupManager.ConfirmCircuitList {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Activity activity;
    private List<CircuitBreaker> circuitBreakerList = new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;

    private View rootView, viewOne, viewTwo, viewThree, viewFour, viewEditCircuitName, viewAddCircuitList, viewDeleteCircuit;
    private FragmentNewReportPhaseTwoBinding binding;
    private String mParam1;
    private String mParam2;

    private RecyclerView rvList;
    private CircuitListRecyclerAdapter circuitListRecyclerAdapter;

    public NewReportFragmentPhaseTwo() {
    }

    private FragmentClickListener fragmentClickListener;

    public void setFragmentClickListener(FragmentClickListener fragmentClickListener) {
        this.fragmentClickListener = fragmentClickListener;
    }

    public static NewReportFragmentPhaseTwo newInstance(String param1, String param2) {
        NewReportFragmentPhaseTwo fragment = new NewReportFragmentPhaseTwo();
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

        binding = FragmentNewReportPhaseTwoBinding.inflate(inflater, container, false);

        rootView = binding.getRoot();

        viewOne = rootView.findViewById(R.id.viewOne);
        viewTwo = rootView.findViewById(R.id.viewTwo);
        viewThree = rootView.findViewById(R.id.viewThree);
        viewFour = rootView.findViewById(R.id.viewFour);
        viewEditCircuitName = rootView.findViewById(R.id.viewEditCircuitName);
        viewAddCircuitList = rootView.findViewById(R.id.viewAddCircuitList);
        viewDeleteCircuit = rootView.findViewById(R.id.viewDeleteCircuit);

        setPageData();

        setViewListeners();
        setCircuitListRv();
        return binding.getRoot();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void setPageData() {
        Report report = Utility.getReport(activity);
        if (report != null) {
            try {

                String equipmentName = Utility.getReport(activity).getEquipment().getEquipmentName();


                ImageLoader.showImageFromStorage(activity, binding.viewOne.sectionSiteDetails.ivSiteDetails, DirectoryManager.getTemperatureImage(equipmentName));
                ImageLoader.showImageFromStorage(activity, binding.viewOne.sectionEquipmentDetails.ivPanelImage, DirectoryManager.getPanelImage(equipmentName));
                ImageLoader.showImageFromStorage(activity, binding.viewOne.sectionEquipmentDetails.ivDbBoxCircuitImage, DirectoryManager.getDbBoxCircuitImage(equipmentName));


                binding.viewOne.etEquipmentName.setText(report.getEquipment().getEquipmentName());
                binding.viewFour.etEquipmentName.setText(report.getEquipment().getEquipmentName());

                binding.viewOne.viewCustomerDetails.tvCustomerName.setText(report.getCustomerName());
                binding.viewOne.viewCustomerDetails.tvCustomerAddress.setText(report.getCustomerAddress());
                binding.viewOne.viewCustomerDetails.tvUser.setText(report.getUserName());
                binding.viewOne.viewCustomerDetails.tvUserAddress.setText(report.getUserAddress());

                binding.viewOne.sectionSiteDetails.tvOwner.setText(report.getOwnerIdentification());
                binding.viewOne.sectionSiteDetails.tvDateOfLastInspection.setText(report.getDateOfLastInspection());
                binding.viewOne.sectionSiteDetails.tvLastInspectionNo.setText(report.getLastInspectionReportNo());
                binding.viewOne.sectionSiteDetails.tvAirTemperature.setText(report.getEquipment().getAirTemperature());
                binding.viewOne.sectionSiteDetails.tvRelativeHumidity.setText(report.getEquipment().getAirHumidity());

                binding.viewOne.sectionEquipmentDetails.tvEquipmentLocation.setText(report.getEquipment().getEquipmentLocation());
                binding.viewOne.sectionEquipmentDetails.tvEquipmentName.setText(report.getEquipment().getEquipmentName());

            } catch (Exception e) {
                Utility.showLog("Exception e  " + e);
            }

        }

    }

    private void setCircuitListRv() {
        linearLayoutManager = new LinearLayoutManager(activity);
        binding.viewOne.rvCircuit.setLayoutManager(linearLayoutManager);
        circuitListRecyclerAdapter = new CircuitListRecyclerAdapter(activity, circuitBreakerList, 0, this);
        binding.viewOne.rvCircuit.setAdapter(circuitListRecyclerAdapter);
    }

    private void setViewListeners() {

        binding.viewOne.btnNext.setOnClickListener(view -> {
                    if (circuitBreakerList.isEmpty()) {
                        Utility.showToast(activity, "Add Circuit");
                    } else {
                     PopupManager.showConfirmCircuitPopup(activity,this);
                    }
                }
        );

        binding.viewTwo.btnNext.setOnClickListener(view -> {
                    savePageTwoPanelBoardRating();
                    showPage(viewThree, viewOne,
                            viewTwo, viewFour);
                }
        );

        binding.viewThree.btnNext.setOnClickListener(view -> {
                    savePageThreeManufacturerCurveDetailsData();
                    showPage(viewFour, viewOne,
                            viewTwo, viewThree);
                }
        );


        binding.viewFour.btnNext.setOnClickListener(view ->
        {
            Utility.showLog(" binding.viewFour.btnNext.setOnClickListener");
            if (fragmentClickListener != null) {
                if (binding.viewFour.tvIr.getContentDescription().equals(getString(R.string.selected)))
                    fragmentClickListener.addNewReportFragmentPhaseThreeIR();
                else fragmentClickListener.addNewReportFragmentPhaseThreeCrmTrip();
            }

        });


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

        binding.viewFour.tvCrmTrip.setOnClickListener(view -> {

                    binding.viewFour.tvCrmTrip.setContentDescription(getString(R.string.selected));
                    binding.viewFour.tvIr.setContentDescription(getString(R.string.not_selected));

                    binding.viewFour.tvCrmTrip.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_item_circuit_list_selected));
                    binding.viewFour.tvIr.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_edit_text));

                    setViewFourNextButtonStatus();
                }
        );

        binding.viewFour.tvIr.setOnClickListener(view -> {

                    binding.viewFour.tvIr.setContentDescription(getString(R.string.selected));
                    binding.viewFour.tvCrmTrip.setContentDescription(getString(R.string.not_selected));

                    binding.viewFour.tvIr.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_item_circuit_list_selected));
                    binding.viewFour.tvCrmTrip.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_edit_text));

                    setViewFourNextButtonStatus();

                }
        );

        binding.viewOne.viewAddCircuitList.ivClose.setOnClickListener(view -> viewAddCircuitList.setVisibility(View.GONE));

        binding.viewOne.viewDeleteCircuit.ivClose.setOnClickListener(view -> viewDeleteCircuit.setVisibility(View.GONE));

        binding.viewOne.viewEditCircuitName.ivClose.setOnClickListener(view -> viewEditCircuitName.setVisibility(View.GONE));

        binding.viewOne.rlSummary.setOnClickListener(view ->
                setExpandView(binding.viewOne.expandSummary, binding.viewOne.ivArrowSummary)
        );


        binding.viewOne.rlCustomerDetails.setOnClickListener(view ->
                setExpandView(binding.viewOne.expandCustomer, binding.viewOne.ivArrowCustomer)
        );


        binding.viewOne.rlSideDetails.setOnClickListener(view ->
                setExpandView(binding.viewOne.expandSite, binding.viewOne.ivArrowSite)
        );

        binding.viewOne.rlEquipment.setOnClickListener(view ->
                setExpandView(binding.viewOne.expandEquipment, binding.viewOne.ivArrowEquipment)
        );

        binding.viewOne.rlCircuit.setOnClickListener(view ->
                setExpandView(binding.viewOne.expandCircuitList, binding.viewOne.ivArrowCircuit)
        );

        binding.viewOne.viewEditCircuitName.ivClose.setOnClickListener(view ->
                viewEditCircuitName.setVisibility(View.GONE)
        );

        binding.viewOne.etNumberOfCircuit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    Utility.showLog(" onTextChanged " + s.toString());
                    setCircuitList(s.toString());
//                    showCircuitNumberConfirmation(s.toString());
                }
            }
        });


    }

    private void savePageTwoPanelBoardRating() {
        ReportGeneralData.savePanelBoardData(activity,
                binding.viewTwo.etTestVoltage.getText().toString().trim(),
                binding.viewTwo.etModelNumber.getText().toString().trim(),
                binding.viewTwo.etCatalog.getText().toString().trim(),
                binding.viewTwo.etAmps.getText().toString().trim(),
                binding.viewTwo.etVoltage.getText().toString().trim()
        );
    }

    private void savePageThreeManufacturerCurveDetailsData() {

        ReportGeneralData.saveManufacturerCurveDetailsData(activity,
                binding.viewThree.etMfgOne.getText().toString().trim(),
                binding.viewThree.etCurveNumberOne.getText().toString().trim(),
                binding.viewThree.etCurveRangeOne.getText().toString().trim(),

                binding.viewThree.etMfgTwo.getText().toString().trim(),
                binding.viewThree.etCurveNumberTwo.getText().toString().trim(),
                binding.viewThree.etCurveRangeTwo.getText().toString().trim(),

                binding.viewThree.etMfgThree.getText().toString().trim(),
                binding.viewThree.etCurveNumberThree.getText().toString().trim(),
                binding.viewThree.etCurveRangeThree.getText().toString().trim(),

                binding.viewThree.etMfgFour.getText().toString().trim(),
                binding.viewThree.etCurveNumberFour.getText().toString().trim(),
                binding.viewThree.etCurveRangeFour.getText().toString().trim()

        );
    }

//    private void showCircuitNumberConfirmation(String numberOfCircuitString) {
//        viewAddCircuitList.setVisibility(View.VISIBLE);
//        binding.viewOne.viewAddCircuitList.tvCircuitBreakerSize.setText(numberOfCircuitString);
//        binding.viewOne.viewAddCircuitList.btnAddCircuitList.setOnClickListener(view -> {
//            viewAddCircuitList.setVisibility(View.GONE);
//            setCircuitList(numberOfCircuitString);
//        });
//    }

    private void setCircuitList(String numberOfCircuitString) {
        Integer numberOfCircuit = Integer.parseInt(numberOfCircuitString);

        String equipmentName = Utility.getReport(activity).getEquipment().getEquipmentName();

        circuitBreakerList.clear();
//        List<CircuitBreaker> list = new ArrayList<>();
        for (int i = 0; i < numberOfCircuit; i++) {
            CircuitBreaker circuitBreaker = new CircuitBreaker();
            circuitBreaker.setCircuitId(String.valueOf(i));
            circuitBreaker.setName("0" + i);
            circuitBreaker.setSize("0");
            circuitBreaker.setEquipmentName(equipmentName);
            circuitBreakerList.add(circuitBreaker);
        }

        circuitListRecyclerAdapter.notifyDataSetChanged();
    }


    private void setViewFourNextButtonStatus() {

        if (binding.viewFour.tvIr.getContentDescription().equals(getString(R.string.selected)) ||
                binding.viewFour.tvCrmTrip.getContentDescription().equals(getString(R.string.selected)))
            binding.viewFour.btnNext.setEnabled(true);
        else
            binding.viewFour.btnNext.setEnabled(false);

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

    private void saveCircuitListAndShowPageTwo() {
        ReportGeneralData.saveCircuitList(activity, circuitBreakerList);
        showPage(viewTwo, viewOne,
                viewThree, viewFour);
    }

    @Override
    public void onItemClicked(int index) {
        circuitListRecyclerAdapter.setSelectedItem(index);
        circuitListRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEditClicked(int index) {
        Utility.showLog("onEditClicked " + index);
        showEditView(index);
    }

    private void showEditView(int index) {
        viewEditCircuitName.setVisibility(View.VISIBLE);
        binding.viewOne.viewEditCircuitName.tvPreviousCircuitName.setText(circuitBreakerList.get(index).getName());
        binding.viewOne.viewEditCircuitName.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CircuitBreaker circuitBreaker = circuitBreakerList.get(index);
                circuitBreaker.setName(binding.viewOne.viewEditCircuitName.etNewCircuitName.getText().toString().trim());
                circuitBreaker.setSize(binding.viewOne.viewEditCircuitName.etBreaker.getText().toString().trim());
                circuitBreakerList.set(index, circuitBreaker);

                viewEditCircuitName.setVisibility(View.GONE);
                circuitListRecyclerAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onDeleteClicked(int index) {
        Utility.showLog("onDeleteClicked " + index);
        viewDeleteCircuit.setVisibility(View.VISIBLE);

        showDeleteView(index);

    }

    private void showDeleteView(int index) {

        binding.viewOne.viewDeleteCircuit.btnDelete.setOnClickListener(view -> {

            if (index >= 0 && index < circuitBreakerList.size()) {
                circuitBreakerList.remove(index);
                circuitListRecyclerAdapter.notifyDataSetChanged();
                viewDeleteCircuit.setVisibility(View.GONE);

                if (!circuitBreakerList.isEmpty())
                    binding.viewOne.etNumberOfCircuit.setText(String.valueOf(circuitBreakerList.size()));
                else binding.viewOne.etNumberOfCircuit.setText("");

            } else {
                Utility.showLog("Delete Index out of bounds: " + index);
            }
        });
    }

    @Override
    public void confirmed() {
        saveCircuitListAndShowPageTwo();
    }

    @Override
    public void cancelled() {

    }

    @Override
    public void saved() {

    }
}