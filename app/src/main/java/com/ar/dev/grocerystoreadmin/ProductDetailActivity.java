package com.ar.dev.grocerystoreadmin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ar.dev.grocerystoreadmin.Adapters.ProductAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProductDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String PRODUCT_ID = "productid";
    public static final String PRODUCT_NAME = "productname";
    public static final String PRODUCT_PRICE = "productprice";
    public static final String PRODUCT_CATEGORY = "productcategory";
    public static final String PRODUCT_DESCRIPTION = "productdescription";

    public static final String IMAGE_ID1 = "img1";
    public static final String IMAGE_ID2 = "img2";
    public static final String IMAGE_ID3 = "img3";
    public static final String IMAGE_ID4 = "img4";

    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private ImageView bigImg, img1, img2, img3, img4;
    private TextView tvName, tvDesc, tvPrice, tvCategory;

    private String productID, productCategory;
    private String deleleImgID1, deleleImgID2, deleleImgID3, deleleImgID4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Product Details");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        productID = getIntent().getStringExtra(ProductAdapter.PRODUCT_ID);
        productCategory = getIntent().getStringExtra(ProductAdapter.PRODUCT_CATEGORY);

        storageRef = FirebaseStorage.getInstance().getReference("Product Images");
        databaseRef = FirebaseDatabase.getInstance().getReference("Products");

        bigImg = findViewById(R.id.imageViewBigShowProduct);
        img1 = findViewById(R.id.ivShowProduct1);
        img2 = findViewById(R.id.ivShowProduct2);
        img3 = findViewById(R.id.ivShowProduct3);
        img4 = findViewById(R.id.ivShowProduct4);
        tvName = findViewById(R.id.tvNameShowProduct);
        tvPrice = findViewById(R.id.tvPriceShowProduct);
        tvCategory = findViewById(R.id.tvCategoryShowProduct);
        tvDesc = findViewById(R.id.tvDescShowProduct);

        tvName.setText(getIntent().getStringExtra(ProductAdapter.PRODUCT_NAME));
        tvPrice.setText(getIntent().getStringExtra(ProductAdapter.PRODUCT_PRICE));
        tvCategory.setText(getIntent().getStringExtra(ProductAdapter.PRODUCT_CATEGORY));
        tvDesc.setText(getIntent().getStringExtra(ProductAdapter.PRODUCT_DESCRIPTION));

        showImagesFromDatabase(img1, "imgUrl1");
        showImagesFromDatabase(img2, "imgUrl2");
        showImagesFromDatabase(img3, "imgUrl3");
        showImagesFromDatabase(img4, "imgUrl4");

        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img4.setOnClickListener(this);

        showImagesFromDatabase(bigImg, "imgUrl1");

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_update_product) {
            callUpdateActivity();
            return true;
        }
        if (id == R.id.menu_delete_product) {
            // showDeleteDialogue();
            showDeleteDialogue();
        }
        return super.onOptionsItemSelected(item);
    }

    private void callUpdateActivity() {
        Intent intent = new Intent(this, UpdateActivity.class);
        intent.putExtra(PRODUCT_ID,getIntent().getStringExtra(ProductAdapter.PRODUCT_ID));
        intent.putExtra(PRODUCT_NAME,getIntent().getStringExtra(ProductAdapter.PRODUCT_NAME));
        intent.putExtra(PRODUCT_PRICE,getIntent().getStringExtra(ProductAdapter.PRODUCT_PRICE));
        intent.putExtra(PRODUCT_CATEGORY,getIntent().getStringExtra(ProductAdapter.PRODUCT_CATEGORY));
        intent.putExtra(PRODUCT_DESCRIPTION,getIntent().getStringExtra(ProductAdapter.PRODUCT_DESCRIPTION));
        intent.putExtra(IMAGE_ID1,getIntent().getStringExtra(ProductAdapter.IMAGE_ID1));
        intent.putExtra(IMAGE_ID2,getIntent().getStringExtra(ProductAdapter.IMAGE_ID2));
        intent.putExtra(IMAGE_ID3,getIntent().getStringExtra(ProductAdapter.IMAGE_ID3));
        intent.putExtra(IMAGE_ID4,getIntent().getStringExtra(ProductAdapter.IMAGE_ID4));
        finish();
        startActivity(intent);
    }

    private void deleteProduct() {
        deleleImgID1 = getIntent().getStringExtra(ProductAdapter.IMAGE_ID1);
        deleleImgID2 = getIntent().getStringExtra(ProductAdapter.IMAGE_ID2);
        deleleImgID3 = getIntent().getStringExtra(ProductAdapter.IMAGE_ID3);
        deleleImgID4 = getIntent().getStringExtra(ProductAdapter.IMAGE_ID4);

        deleteImageFromDatabase(deleleImgID1);
        deleteImageFromDatabase(deleleImgID2);
        deleteImageFromDatabase(deleleImgID3);
        deleteImageFromDatabase(deleleImgID4);

        finish();

        databaseRef.child(productCategory).child(productID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ProductDetailActivity.this, "Product Deleted", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void showDeleteDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure to delete this product?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        positiveButton.setTextColor(Color.parseColor("#22334d"));
        negativeButton.setTextColor(Color.parseColor("#22334d"));


    }

    private void deleteImageFromDatabase(String ImgID) {
        storageRef.child(ImgID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText(ProductDetailActivity.this, "Image Deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == img1) {
            showImagesFromDatabase(bigImg, "imgUrl1");
        } else if (v == img2) {
            showImagesFromDatabase(bigImg, "imgUrl2");
        } else if (v == img3) {
            showImagesFromDatabase(bigImg, "imgUrl3");
        } else if (v == img4) {
            showImagesFromDatabase(bigImg, "imgUrl4");
        }
    }
}
