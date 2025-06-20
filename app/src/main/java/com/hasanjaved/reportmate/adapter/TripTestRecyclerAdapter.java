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
import com.hasanjaved.reportmate.model.TripTest;
import com.hasanjaved.reportmate.utility.ImageLoader;
import com.hasanjaved.reportmate.utility.Utility;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

public class TripTestRecyclerAdapter extends RecyclerView.Adapter<TripTestRecyclerAdapter.MyViewHolder> {


    private List<CircuitBreaker> list;
    private Context context;
    private RecyclerViewClickListener recyclerViewClickListener;


    private int selectedItem;


    private static int lastClickedPosition = -1;

    public TripTestRecyclerAdapter(Context context, List<CircuitBreaker> list, int selectedItem, RecyclerViewClickListener recyclerViewClickListener) {
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
                .inflate(R.layout.item_trip, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        int currentPosition = position;


        holder.tvCircuitName.setText(list.get(currentPosition).getName());

        TripTest test = list.get(currentPosition).getTripTest();
        holder.tvTestAmplitude.setText(test.getTestAmplitude()+"mΩ");
        holder.tvTripTime.setText(test.getTripTime());
        holder.tvInstantTrip.setText(test.getInstantTrip());

        List<String> images = Utility.getTripImage(context,list.get(position).getName());
        ImageLoader.showImageFromStorage(context, holder.ivCurrentConnection, images.get(0));
        ImageLoader.showImageFromStorage(context, holder.ivInjectedCurrent, images.get(1));
        ImageLoader.showImageFromStorage(context, holder.ivTripTimeConnection, images.get(2));
        ImageLoader.showImageFromStorage(context, holder.ivTripTime, images.get(3));
        ImageLoader.showImageFromStorage(context, holder.ivAfterTripTime, images.get(4));


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
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rl;
        public ExpandableLayout expand;
        public ImageView ivArrow, ivCurrentConnection, ivInjectedCurrent,
                ivTripTimeConnection, ivTripTime, ivAfterTripTime;
        public TextView tvCircuitName, tvTestAmplitude, tvTripTime, tvInstantTrip;

        public MyViewHolder(View view) {
            super(view);

            rl = view.findViewById(R.id.rl);
            expand = view.findViewById(R.id.expand);
            ivArrow = view.findViewById(R.id.ivArrow);
            tvCircuitName = view.findViewById(R.id.tvCircuitName);
            tvTestAmplitude = view.findViewById(R.id.tvTestAmplitude);
            tvTripTime = view.findViewById(R.id.tvTripTime);
            tvInstantTrip = view.findViewById(R.id.tvInstantTrip);

            ivCurrentConnection = view.findViewById(R.id.ivCurrentConnection);
            ivInjectedCurrent = view.findViewById(R.id.ivInjectedCurrent);
            ivTripTimeConnection = view.findViewById(R.id.ivTripTimeConnection);
            ivTripTime = view.findViewById(R.id.ivTripTime);
            ivAfterTripTime = view.findViewById(R.id.ivAfterTripTime);

        }
    }

}