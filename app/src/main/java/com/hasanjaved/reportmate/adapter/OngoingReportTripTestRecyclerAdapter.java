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
import com.hasanjaved.reportmate.listeners.EditRecyclerViewClickListener;
import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.model.TripTest;
import com.hasanjaved.reportmate.utility.DirectoryManager;
import com.hasanjaved.reportmate.utility.ImageLoader;
import com.hasanjaved.reportmate.utility.Utility;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

public class OngoingReportTripTestRecyclerAdapter extends RecyclerView.Adapter<OngoingReportTripTestRecyclerAdapter.MyViewHolder> {


    private List<CircuitBreaker> list;
    private Context context;
    private EditRecyclerViewClickListener recyclerViewClickListener;


    private int selectedItem;


    private static int lastClickedPosition = -1;

    public OngoingReportTripTestRecyclerAdapter(Context context, List<CircuitBreaker> list, int selectedItem, EditRecyclerViewClickListener recyclerViewClickListener) {
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
                .inflate(R.layout.item_trip_edit, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        int currentPosition = position;
        holder.tvCircuitName.setText(list.get(currentPosition).getName());
        try {
            TripTest test = list.get(currentPosition).getTripTest();
            String text = test.getTestAmplitude()+"mΩ";
            holder.tvTestAmplitude.setText(text);
            holder.tvTripTime.setText(test.getTripTime());
            holder.tvInstantTrip.setText(test.getInstantTrip());

        }catch (Exception e){
            Utility.showLog(getClass().getSimpleName()+e);
        }

        try {
            List<String> images = DirectoryManager.getTripImage(list.get(position).getEquipmentName(),list.get(position).getName());

            Utility.showLog(images.toString());

            ImageLoader.showImageFromStorage(context, holder.ivCurrentConnection, images.get(0));
            ImageLoader.showImageFromStorage(context, holder.ivInjectedCurrent, images.get(1));
//            ImageLoader.showImageFromStorage(context, holder.ivTripTimeConnection, images.get(2));
            ImageLoader.showImageFromStorage(context, holder.ivTripTime, images.get(2));
            ImageLoader.showImageFromStorage(context, holder.ivAfterTripTime, images.get(3));
        }catch (Exception e){
            Utility.showLog(getClass().getSimpleName()+e);
        }


        holder.ivEdit.setOnClickListener(view -> recyclerViewClickListener.onTripEditClicked(list,currentPosition));

        //---------------------------------------- image actions
        holder.ivCurrentConnection.setOnClickListener(view ->{

            recyclerViewClickListener.onImageClicked(holder.ivCurrentConnection,
                                                    DirectoryManager.imgInjectorCurrent,
                                                    DirectoryManager.getTripFolderLink( list.get(currentPosition)));
        } );

        holder.ivInjectedCurrent.setOnClickListener(view ->{

            recyclerViewClickListener.onImageClicked(holder.ivInjectedCurrent,
                    DirectoryManager.imgInjectedCurrent,
                    DirectoryManager.getTripFolderLink( list.get(currentPosition)));
        } );

        holder.ivTripTime.setOnClickListener(view ->{

            recyclerViewClickListener.onImageClicked(holder.ivTripTime,
                    DirectoryManager.imgTripTime,
                    DirectoryManager.getTripFolderLink( list.get(currentPosition)));
        } );

        holder.ivAfterTripTime.setOnClickListener(view ->{

            recyclerViewClickListener.onImageClicked(holder.ivAfterTripTime,
                    DirectoryManager.imgAfterTripTime,
                    DirectoryManager.getTripFolderLink( list.get(currentPosition)));
        } );
        //-----------------------------------------------------------------------------------------------

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
                ivTripTimeConnection, ivTripTime, ivAfterTripTime,
                ivEdit;
        public TextView tvCircuitName, tvTestAmplitude, tvTripTime, tvInstantTrip;

        public MyViewHolder(View view) {
            super(view);

            rl = view.findViewById(R.id.rl);
            ivEdit = view.findViewById(R.id.ivEdit);
            expand = view.findViewById(R.id.expand);
            ivArrow = view.findViewById(R.id.ivArrow);
            tvCircuitName = view.findViewById(R.id.tvCircuitName);
            tvTestAmplitude = view.findViewById(R.id.tvTestAmplitude);
            tvTripTime = view.findViewById(R.id.tvTripTime);
            tvInstantTrip = view.findViewById(R.id.tvInstantTrip);

            ivCurrentConnection = view.findViewById(R.id.ivCurrentConnection);
            ivInjectedCurrent = view.findViewById(R.id.ivInjectedCurrent);
//            ivTripTimeConnection = view.findViewById(R.id.ivTripTimeConnection);
            ivTripTime = view.findViewById(R.id.ivTripTime);
            ivAfterTripTime = view.findViewById(R.id.ivAfterTripTime);

        }
    }

}