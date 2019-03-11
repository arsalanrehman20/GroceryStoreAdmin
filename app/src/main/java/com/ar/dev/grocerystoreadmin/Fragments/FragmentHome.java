package com.ar.dev.grocerystoreadmin.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.ar.dev.grocerystoreadmin.Adapters.ProductAdapter;
import com.ar.dev.grocerystoreadmin.Models.ProductModel;
import com.ar.dev.grocerystoreadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentHome extends Fragment {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private Spinner spinner;

    private ProductAdapter productAdapter;
    private List<ProductModel> productModelList;
    String[] productCategory= {"Electronics","Fruits","Vegetables","Grocery",
    "Personal Care","Clothes & Shoes","Jewellery","Cosmetics"};
    private DatabaseReference databaseRef;

    private SharedPreferences sharedPreferences;

    private View view;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("Grocery Store");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);



        progressBar=view.findViewById(R.id.pbHome);
        spinner = view.findViewById(R.id.spinnerHome);
        recyclerView = view.findViewById(R.id.recyclerViewHome);

        databaseRef=FirebaseDatabase.getInstance().getReference("Products");

        productModelList=new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(),2));

        viewProducts(view);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                viewProducts(view);
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        return view;
    }

    public void viewProducts(final View view){

        if(spinner.getSelectedItem().toString().equals("All")){

            final List<ProductModel> productModelListAll = new ArrayList<>();

            for(int i=0;i<productCategory.length;i++) {
                databaseRef.child(productCategory[i]).
                        addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        productModelList.clear();

                        for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                            ProductModel productModel = productSnapshot.getValue(ProductModel.class);
                            productModelList.add(productModel);
                        }

                        Collections.sort(productModelListAll,ProductModel.BY_ASCENDING);

                        productModelListAll.addAll(productModelList);
                        productAdapter = new ProductAdapter(view.getContext(), productModelListAll);
                        recyclerView.setAdapter(productAdapter);
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(view.getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        else {
            databaseRef.child(spinner.getSelectedItem().toString())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            productModelList.clear();
                            for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                                ProductModel productModel = productSnapshot.getValue(ProductModel.class);
                                productModelList.add(productModel);
                            }
                            productAdapter = new ProductAdapter(view.getContext(), productModelList);
                            recyclerView.setAdapter(productAdapter);
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(view.getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewProducts(view);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.home,menu);

        MenuItem searchItem = menu.findItem(R.id.search_action);
        SearchView searchView = (SearchView)searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

}
