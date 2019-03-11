package com.ar.dev.grocerystoreadmin.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.ar.dev.grocerystoreadmin.Models.OrderItemsModel;
import com.ar.dev.grocerystoreadmin.Models.OrderModel;
import com.ar.dev.grocerystoreadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private String currentUserId;

    private DatabaseReference databaseOrderProductRef;
    private List<OrderItemsModel> orderItemsModelList;
    private RecyclerView recyclerViewShowOrderProduct;
    private OrderProductsAdapter orderProductsAdapter;

    private DatabaseReference databaseOrderUserRef;

    private Context context;
    private List<OrderModel> orderModelList;

    public OrderAdapter(Context context, List<OrderModel> orderModelList) {
        this.context = context;
        this.orderModelList = orderModelList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.order_item_layout, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, int position) {
        final OrderModel currentOrderModel = orderModelList.get(position);
        holder.tvOrderId.setText(currentOrderModel.getOrderID());
        holder.tvOrderDate.setText(currentOrderModel.getOrderDate());
        holder.tvOrderAmount.setText(currentOrderModel.getOrderAmount());
        holder.tvOrderStatus.setText(currentOrderModel.getOrderStatus());

        databaseOrderUserRef = FirebaseDatabase.getInstance().getReference("Order User Details");
        databaseOrderUserRef.child(currentOrderModel.getOrderID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.tvOrderUsername.setText(dataSnapshot.child("Username").getValue().toString());
                        holder.tvOrderContact.setText(dataSnapshot.child("Contact").getValue().toString());
                        holder.tvOrderAddress.setText(dataSnapshot.child("Address").getValue().toString());
                        holder.tvOrderUserId.setText(dataSnapshot.child("Id").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        holder.tvShowOrderProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.show_order_product_dialog);


                databaseOrderProductRef = FirebaseDatabase.getInstance().getReference("Orders Items");

                orderItemsModelList = new ArrayList<>();

                recyclerViewShowOrderProduct = dialog.findViewById(R.id.recyclerViewOrderProducts);
                recyclerViewShowOrderProduct.setHasFixedSize(true);
                recyclerViewShowOrderProduct.setLayoutManager(new LinearLayoutManager(context));

                databaseOrderProductRef.child(currentOrderModel.getOrderID())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                orderItemsModelList.clear();
                                for (DataSnapshot orderItemModelSnapshot : dataSnapshot.getChildren()) {
                                    OrderItemsModel orderItemsModel = orderItemModelSnapshot.getValue(OrderItemsModel.class);
                                    orderItemsModelList.add(orderItemsModel);
                                }
                                orderProductsAdapter = new OrderProductsAdapter(context, orderItemsModelList);
                                recyclerViewShowOrderProduct.setAdapter(orderProductsAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                dialog.show();
            }
        });

        holder.tvOrderStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.change_order_status_diaglog);
                dialog.show();
                final Spinner spinner = dialog.findViewById(R.id.spinnerStatus);
                Button btnUpdateStatus = dialog.findViewById(R.id.btnOrderStatusChange);

                btnUpdateStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Orders");
                        dr.child(holder.tvOrderUserId.getText().toString()).child(currentOrderModel.getOrderID()).child("orderStatus").setValue(spinner.getSelectedItem().toString());
                        holder.tvOrderStatus.setText(spinner.getSelectedItem().toString());
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderModelList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        public TextView tvShowOrderProducts;
        public TextView tvOrderUserId,tvOrderUsername, tvOrderAddress, tvOrderContact;
        public TextView tvOrderId, tvOrderDate, tvOrderAmount, tvOrderStatus;

        public OrderViewHolder(View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderID);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderAmount = itemView.findViewById(R.id.tvOrderPrice);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvShowOrderProducts = itemView.findViewById(R.id.tvShowOrderProducts);
            tvOrderUserId = itemView.findViewById(R.id.tvOrderUserId);
            tvOrderUsername = itemView.findViewById(R.id.tvOrderUserName);
            tvOrderContact = itemView.findViewById(R.id.tvOrderUserContact);
            tvOrderAddress = itemView.findViewById(R.id.tvOrderUserAddress);
        }
    }
}
