package com.ar.dev.grocerystoreadmin.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ar.dev.grocerystoreadmin.Models.UserModel;
import com.ar.dev.grocerystoreadmin.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<UserModel> userModelList;

    public UserAdapter(Context context, List<UserModel> userModelList) {
        this.context = context;
        this.userModelList = userModelList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_list_layout, parent, false);

        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        UserModel currentUserModel = userModelList.get(position);
        holder.tvName.setText(currentUserModel.getName());
        holder.tvEmail.setText(currentUserModel.getEmail());
        holder.tvContact.setText(currentUserModel.getContact());
        holder.tvAddress.setText(currentUserModel.getAddress());
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvEmail, tvContact, tvAddress;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvEmail = itemView.findViewById(R.id.tvUserEmail);
            tvContact = itemView.findViewById(R.id.tvUserContact);
            tvAddress = itemView.findViewById(R.id.tvUserAddress);
        }
    }
}
