package com.hasanjaved.reportmate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.listeners.RecyclerViewClickListener;
import com.hasanjaved.reportmate.model.CircuitBreaker;

import java.util.List;

public class CircuitListRecyclerAdapter extends RecyclerView.Adapter<CircuitListRecyclerAdapter.MyViewHolder> {

    private List<CircuitBreaker> list;
    private Context context;
    private RecyclerViewClickListener recyclerViewClickListener;

    private int selectedItem;


    private static int lastClickedPosition = -1;

    public CircuitListRecyclerAdapter(Context context, List<CircuitBreaker>  list, int selectedItem, RecyclerViewClickListener recyclerViewClickListener) {
        this.context = context;
        this.list = list;
        this.selectedItem = selectedItem;
        this.recyclerViewClickListener = recyclerViewClickListener;
    }

    public int getSelectedItem() {
        return selectedItem;
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

        if (viewType== 1)
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_circuit_list_selected, parent, false);
        else itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_circuit_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        int currentPosition = position;

//        Glide.with(context)
//                .load(canvasCategoryModelList.get(position).getCategoryDrawable())
//                .placeholder(R.drawable.ic_loading_image_place)
//                .into( holder.iv);


            holder.tvName.setText(list.get(currentPosition).getName());
            holder.tvBreakerSize.setText(list.get(currentPosition).getSize());
            holder.btnEdit.setOnClickListener(view -> recyclerViewClickListener.onEditClicked(currentPosition));
            holder.btnDelete.setOnClickListener(view -> recyclerViewClickListener.onDeleteClicked(currentPosition));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewClickListener.onItemClicked(currentPosition);
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
//    return 5;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvBreakerSize;
        public ImageView btnDelete,btnEdit;

        public MyViewHolder(View view) {
            super(view);

            tvName = view.findViewById(R.id.tvName);
            tvBreakerSize = view.findViewById(R.id.tvBreakerSize);
            btnDelete = view.findViewById(R.id.btnDelete);
            btnEdit = view.findViewById(R.id.btnEdit);

        }
    }

}