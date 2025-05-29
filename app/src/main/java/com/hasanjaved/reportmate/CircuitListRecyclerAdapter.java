package com.hasanjaved.reportmate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

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


//        if (position == selectedItem) {
//
//            holder.ivPencil.setVisibility(View.VISIBLE);
//            Utility.shakeSelectedCanvasPencil(holder.ivPencil);
//
//        } else
//            holder.ivPencil.setVisibility(View.GONE);
//
//
//        if (position == selectedItem) {
//            holder.cl.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_season_tab_selected)
//            );
//        } else
//            holder.cl.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_season_not_selected));
//
//
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewClickListener.onItemClicked(currentPosition);
//                canvasRvClickListener.onCategoryClicked(currentPosition);
            }
        });

    }


    @Override
    public int getItemCount() {
//        return list.size();
    return 5;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv, ivPencil;
        public ConstraintLayout cl;

        public MyViewHolder(View view) {
            super(view);
//            iv = view.findViewById(R.id.iv);
//            ivPencil = view.findViewById(R.id.ivPencil);
//            cl = view.findViewById(R.id.cl);

        }
    }

}