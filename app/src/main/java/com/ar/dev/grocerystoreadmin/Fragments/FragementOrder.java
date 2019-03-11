package com.ar.dev.grocerystoreadmin.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ar.dev.grocerystoreadmin.Adapters.OrderAdapter;
import com.ar.dev.grocerystoreadmin.Models.OrderModel;
import com.ar.dev.grocerystoreadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragementOrder extends Fragment {

    private RecyclerView recyclerViewOrder;
    private List<OrderModel> orderModelList;
    private OrderAdapter orderAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(" Orders");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        orderModelList = new ArrayList<>();
        recyclerViewOrder = view.findViewById(R.id.recyclerViewOrder);
        recyclerViewOrder.setHasFixedSize(true);
        recyclerViewOrder.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference followingRef = rootRef.child("Orders");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot dSnapshot : ds.getChildren()) {
                        OrderModel orderModel = dSnapshot.getValue(OrderModel.class);
                        orderModelList.add(orderModel);
                    }
                    orderAdapter = new OrderAdapter(getContext(), orderModelList);
                    recyclerViewOrder.setAdapter(orderAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        followingRef.addListenerForSingleValueEvent(valueEventListener);

        return view;
    }
}
