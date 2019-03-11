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

import com.ar.dev.grocerystoreadmin.Adapters.UserAdapter;
import com.ar.dev.grocerystoreadmin.Models.UserModel;
import com.ar.dev.grocerystoreadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragementUser extends Fragment {

    private RecyclerView recyclerViewUser;
    private List<UserModel> userModelList;
    private UserAdapter userAdapter;

    private DatabaseReference databaseUserRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(" Users");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerViewUser=view.findViewById(R.id.recyclerViewUser);
        databaseUserRef=FirebaseDatabase.getInstance().getReference().child("Users");
        databaseUserRef.keepSynced(true);

        userModelList=new ArrayList<>();

        recyclerViewUser.setHasFixedSize(true);
        recyclerViewUser.setLayoutManager(new LinearLayoutManager(view.getContext()));

        databaseUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userModelList.clear();
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    UserModel userModel = userSnapshot.getValue(UserModel.class);
                    userModelList.add(userModel);
                }
                userAdapter=new UserAdapter(view.getContext(),userModelList);
                recyclerViewUser.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }


}
