package com.hasanjaved.reportmate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.listeners.RecyclerViewClickListener;
import com.hasanjaved.reportmate.model.CircuitBreaker;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

public class CrmTestRecyclerAdapter extends RecyclerView.Adapter<CrmTestRecyclerAdapter.MyViewHolder> {

    private List<CircuitBreaker> list;
    private Context context;
    private RecyclerViewClickListener recyclerViewClickListener;

    private int selectedItem;

    private static int lastClickedPosition = -1;

    public CrmTestRecyclerAdapter(Context context, List<CircuitBreaker> list, int selectedItem, RecyclerViewClickListener recyclerViewClickListener) {
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

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crm, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        int currentPosition = position;


//        holder.tvName.setText(list.get(currentPosition).getName());
//        holder.tvBreakerSize.setText(list.get(currentPosition).getSize());

        holder.rl.setOnClickListener(view -> {

            if (holder.expand.isExpanded()) {
                holder.ivArrow.animate().rotation(180).start();
                holder.expand.collapse();
            } else {
                holder.ivArrow.animate().rotation(0).start();
                holder.expand.expand();
            }

        });

    }


    @Override
    public int getItemCount() {
//        return list.size();
    return 3;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rl;
        public ExpandableLayout expand;
        public ImageView ivArrow;

        public MyViewHolder(View view) {
            super(view);

            rl = view.findViewById(R.id.rl);
            expand = view.findViewById(R.id.expand);
            ivArrow = view.findViewById(R.id.ivArrow);

        }
    }

}