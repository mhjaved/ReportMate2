package com.hasanjaved.reportmate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.listeners.RecyclerViewClickListener;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.model.Report;
import com.hasanjaved.reportmate.utility.Utility;

import java.util.List;

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.MyViewHolder> {


    private List<Report> list;
    private Context context;
    private RecyclerViewClickListener recyclerViewClickListener;
    
    private int selectedItem;


    private static int lastClickedPosition = -1;

    public HistoryRecyclerAdapter(Context context, List<Report> list, int selectedItem, RecyclerViewClickListener recyclerViewClickListener) {
        this.context = context;
        this.list = list;
        this.selectedItem = selectedItem;
        this.recyclerViewClickListener = recyclerViewClickListener;
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public Report getSelectedCircuit() {
        if (getSelectedItem() >= 0)
            return list.get(getSelectedItem());
        return null;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == selectedItem)
            return 1;
        else return 2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

//        if (viewType == 1)
//            itemView = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.item_circuit_list_selected2, parent, false);
//        else
            itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        int currentPosition = position;

//
        holder.tvClientName.setText(list.get(currentPosition).getCustomerName());
        holder.tvProjectCode.setText(list.get(currentPosition).getEquipment().getEquipmentName());
//
//        if (Utility.checkCrmForCircuit(context,list.get(currentPosition)))
//            holder.tvCrm.setVisibility(View.VISIBLE);
//        else holder.tvCrm.setVisibility(View.GONE);
//
//        if (Utility.checkTripForCircuit(context,list.get(currentPosition)))
//            holder.tvTrip.setVisibility(View.VISIBLE);
//        else holder.tvTrip.setVisibility(View.GONE);
//
//
        holder.itemView.setOnClickListener(view -> recyclerViewClickListener.onItemClicked(currentPosition));

    }


    @Override
    public int getItemCount() {
        return list.size();
//    return 5;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvClientName, tvProjectCode,tvCrm,tvTrip;

        public MyViewHolder(View view) {
            super(view);

            tvClientName = view.findViewById(R.id.tvClientName);
            tvProjectCode = view.findViewById(R.id.tvProjectCode);
//            tvCrm = view.findViewById(R.id.tvCrm);
//            tvTrip = view.findViewById(R.id.tvTrip);

        }
    }

}