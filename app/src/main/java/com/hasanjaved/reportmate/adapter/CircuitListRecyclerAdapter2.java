package com.hasanjaved.reportmate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.listeners.RecyclerViewClickListener;
import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.utility.Utility;

import java.util.List;

public class CircuitListRecyclerAdapter2 extends RecyclerView.Adapter<CircuitListRecyclerAdapter2.MyViewHolder> {


    private List<CircuitBreaker> list;
    private Context context;
    private RecyclerViewClickListener recyclerViewClickListener;


    private int selectedItem;


    private static int lastClickedPosition = -1;

    public CircuitListRecyclerAdapter2(Context context, List<CircuitBreaker> list, int selectedItem, RecyclerViewClickListener recyclerViewClickListener) {
        this.context = context;
        this.list = list;
        this.selectedItem = selectedItem;
        this.recyclerViewClickListener = recyclerViewClickListener;
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public CircuitBreaker getSelectedCircuit() {
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

        if (viewType == 1)
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_circuit_list_selected2, parent, false);
        else itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_circuit_list2, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        int currentPosition = position;


        holder.tvName.setText(list.get(currentPosition).getName());
        holder.tvBreakerSize.setText(list.get(currentPosition).getSize());

        if (Utility.checkCrmForCircuit(context,list.get(currentPosition)))
            holder.tvCrm.setVisibility(View.VISIBLE);
        else holder.tvCrm.setVisibility(View.GONE);

        if (Utility.checkTripForCircuit(list.get(currentPosition).getEquipmentName(),list.get(currentPosition)))
            holder.tvTrip.setVisibility(View.VISIBLE);
        else holder.tvTrip.setVisibility(View.GONE);


        holder.itemView.setOnClickListener(view -> recyclerViewClickListener.onItemClicked(currentPosition));

    }


    @Override
    public int getItemCount() {
        return list.size();
//    return 5;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvBreakerSize,tvCrm,tvTrip;

        public MyViewHolder(View view) {
            super(view);

            tvName = view.findViewById(R.id.tvName);
            tvBreakerSize = view.findViewById(R.id.tvBreakerSize);
            tvCrm = view.findViewById(R.id.tvCrm);
            tvTrip = view.findViewById(R.id.tvTrip);

        }
    }

}