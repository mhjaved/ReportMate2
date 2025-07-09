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
import com.hasanjaved.reportmate.model.CrmTest;
import com.hasanjaved.reportmate.utility.DirectoryManager;
import com.hasanjaved.reportmate.utility.ImageLoader;
import com.hasanjaved.reportmate.utility.Utility;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

public class OngoingReportCrmTestRecyclerAdapter extends RecyclerView.Adapter<OngoingReportCrmTestRecyclerAdapter.MyViewHolder> {

    private List<CircuitBreaker> list;
    private Context context;
    private EditRecyclerViewClickListener recyclerViewClickListener;

    private int selectedItem;

    private static int lastClickedPosition = -1;

    public OngoingReportCrmTestRecyclerAdapter(Context context, List<CircuitBreaker> list, int selectedItem, EditRecyclerViewClickListener recyclerViewClickListener) {
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
                .inflate(R.layout.item_crm_edit, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        int currentPosition = position;

        List<String> images = DirectoryManager.getCrmImage(list.get(position).getEquipmentName(),list.get(position).getName());

        holder.tvCircuitName.setText(list.get(currentPosition).getName());

        try {
            CrmTest crmTest = list.get(currentPosition).getCrmTest();

            String text = crmTest.getrResValue()+crmTest.getrResUnit();
            holder.tvRResValue.setText(text);

            text =crmTest.getyResValue()+crmTest.getyResUnit();
            holder.tvYResValue.setText(text);

            text =crmTest.getbResValue()+crmTest.getbResUnit();
            holder.tvBResValue.setText(text);

        }catch (Exception e){
            Utility.showLog(getClass().getSimpleName()+e);
        }

        ImageLoader.showImageFromStorage(context, holder.ivConnection, images.get(0));
        ImageLoader.showImageFromStorage(context, holder.ivResult, images.get(1));


        holder.ivEdit.setOnClickListener(view -> recyclerViewClickListener.onCrmEditClicked(list,currentPosition));

        //----------------------------------------image on click
        holder.ivConnection.setOnClickListener(view ->{

            recyclerViewClickListener.onImageClicked(holder.ivConnection,DirectoryManager.imgCrmConnection,
                    DirectoryManager.getCrmFolderLink( list.get(currentPosition)));
        } );

        holder.ivResult.setOnClickListener(view -> recyclerViewClickListener.onImageClicked(holder.ivResult,DirectoryManager.imgCrmResult,
                DirectoryManager.getCrmFolderLink(list.get(currentPosition))));

        //-----------------------------------------------------------------------


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
//    return 3;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rl;
        public ExpandableLayout expand;
        private TextView tvCircuitName,tvRResValue,tvYResValue,tvBResValue;
        public ImageView ivArrow,ivConnection,ivResult,ivEdit;

        public MyViewHolder(View view) {
            super(view);

            rl = view.findViewById(R.id.rl);
            ivEdit = view.findViewById(R.id.ivEdit);
            expand = view.findViewById(R.id.expand);
            ivArrow = view.findViewById(R.id.ivArrow);
            tvCircuitName = view.findViewById(R.id.tvCircuitName);
            ivResult = view.findViewById(R.id.ivResult);
            ivConnection = view.findViewById(R.id.ivConnection);
            tvRResValue = view.findViewById(R.id.tvRResValue);
            tvYResValue = view.findViewById(R.id.tvYResValue);
            tvBResValue = view.findViewById(R.id.tvBResValue);
        }
    }

}