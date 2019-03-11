package com.ar.dev.grocerystoreadmin;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference databaseRef;

    private Button btnUpdateProduct;
    private EditText etName, etPrice, etDesc;
    private ImageView img1, img2, img3, img4;
    private TextView tvCategory;

    private String newName, newPrice, newDesc;
    private String productCategory, productID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Update Product");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        databaseRef = FirebaseDatabase.getInstance().getReference("Products");

        etName = findViewById(R.id.etUpdateName);
        etPrice = findViewById(R.id.etUpdatePrice);
        etDesc = findViewById(R.id.etUpdateDesc);
        tvCategory = findViewById(R.id.tvCategoryUpdate);
        img1 = findViewById(R.id.ivUpdate1);
        img2 = findViewById(R.id.ivUpdate2);
        img3 = findViewById(R.id.ivUpdate3);
        img4 = findViewById(R.id.ivUpdate4);

        productID=getIntent().getStringExtra(ProductDetailActivity.PRODUCT_ID);
        productCategory=getIntent().getStringExtra(ProductDetailActivity.PRODUCT_CATEGORY);

        btnUpdateProduct=findViewById(R.id.btnUpdateProduct);
        etName.setText(getIntent().getStringExtra(ProductDetailActivity.PRODUCT_NAME));
        etPrice.setText(getIntent().getStringExtra(ProductDetailActivity.PRODUCT_PRICE));
        tvCategory.setText(getIntent().getStringExtra(ProductDetailActivity.PRODUCT_CATEGORY));
        etDesc.setText(getIntent().getStringExtra(ProductDetailActivity.PRODUCT_DESCRIPTION));

        showImagesFromDatabase(img1, "imgUrl1");
        showImagesFromDatabase(img2, "imgUrl2");
        showImagesFromDatabase(img3, "imgUrl3");
        showImagesFromDatabase(img4, "imgUrl4");

        btnUpdateProduct.setOnClickListener(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showImagesFromDatabase(final ImageView imageView, String key) {
        databaseRef.child(productCategory).child(productID).child(key)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null)
                            Picasso.get().load(dataSnapshot.getValue().toString()).into(imageView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void updateProduct(){
        newName=etName.getText().toString();
        newPrice=etPrice.getText().toString();
        newDesc=etDesc.getText().toString();

        if(TextUtils.isEmpty(newName)){
            etName.setError("Name is required");
        }
        if (TextUtils.isEmpty(newPrice)) {
            etPrice.setError("Price is required");
            return;
        }
        if (TextUtils.isEmpty(newDesc)) {
            etDesc.setError("Description is required");
            return;
        }

        databaseRef.child(productCategory).child(productID).child("name").setValue(newName);
        databaseRef.child(productCategory).child(productID).child("price").setValue(newPrice);
        databaseRef.child(productCategory).child(productID).child("desc").setValue(newDesc);
        Toast.makeText(this, "Product Updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if(v==btnUpdateProduct){
            updateProduct();
        }
    }
}
