package com.hasanjaved.reportmate.listeners;

import com.hasanjaved.reportmate.model.CircuitBreaker;
import com.hasanjaved.reportmate.model.CrmTest;
import com.hasanjaved.reportmate.model.TripTest;

import java.util.List;

public interface EditRecyclerViewClickListener {
    void onItemClicked(int index);
    void onTripEditClicked(List<CircuitBreaker> list, int index );
    void onCrmEditClicked(List<CircuitBreaker> list, int index );
    void onDeleteClicked(int index);

}
