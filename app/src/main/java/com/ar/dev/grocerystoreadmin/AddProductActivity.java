package com.ar.dev.grocerystoreadmin;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.ar.dev.grocerystoreadmin.Models.ProductModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private EditText etProductName, etProductPrice, etProductDesc;
    private Spinner spinnerCategory;
    private ImageView img1, img2, img3, img4;
    private Button btnAddProduct;

    private int imageClickCheck;
    private String id, name, price, category, desc;
    private String downloadURL1, downloadURL2, downloadURL3, downloadURL4;
    private Uri imageUri, resultUri1, resultUri2, resultUri3, resultUri4;
    private static final int PICK_IMAGE_REQUEST = 1;

    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    public StorageReference fileReference1, fileReference2, fileReference3, fileReference4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("New Product");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        progressBar = findViewById(R.id.pbAddProduct);
        etProductName = findViewById(R.id.etAddProductName);
        etProductPrice = findViewById(R.id.etAddProductPrice);
        etProductDesc = findViewById(R.id.etAddProductDesc);
        spinnerCategory = findViewById(R.id.spinnerAddProduct);
        img1 = findViewById(R.id.ivAddProduct1);
        img2 = findViewById(R.id.ivAddProduct2);
        img3 = findViewById(R.id.ivAddProduct3);
        img4 = findViewById(R.id.ivAddProduct4);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        progressBar.setVisibility(View.GONE);
        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img4.setOnClickListener(this);
        btnAddProduct.setOnClickListener(this);

        databaseRef = FirebaseDatabase.getInstance().getReference("Products");
        storageRef = FirebaseStorage.getInstance().getReference("Product Images");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == btnAddProduct) {
            AddProductToDatabase();
        }
        if (v == img1) {
            imageClickCheck = 1;
            pickImage();
        }
        if (v == img2) {
            imageClickCheck = 2;
            pickImage();
        }
        if (v == img3) {
            imageClickCheck = 3;
            pickImage();
        }
        if (v == img4) {
            imageClickCheck = 4;
            pickImage();
        }
    }


    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(2, 2)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                if (imageClickCheck == 1) {
                    resultUri1 = result.getUri();
                    Picasso.get().load(resultUri1).into(img1);
                } else if (imageClickCheck == 2) {
                    resultUri2 = result.getUri();
                    Picasso.get().load(resultUri2).into(img2);
                } else if (imageClickCheck == 3) {
                    resultUri3 = result.getUri();
                    Picasso.get().load(resultUri3).into(img3);
                } else if (imageClickCheck == 4) {
                    resultUri4 = result.getUri();
                    Picasso.get().load(resultUri4).into(img4);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void AddProductToDatabase() {

        name = etProductName.getText().toString();
        price = etProductPrice.getText().toString();
        desc = etProductDesc.getText().toString();
        category = spinnerCategory.getSelectedItem().toString();

        if (TextUtils.isEmpty(name)) {
            etProductName.setError("Name is required");
            return;
        }
        if (TextUtils.isEmpty(price)) {
            etProductPrice.setError("Price is required");
            return;
        }
        if (TextUtils.isEmpty(desc)) {
            etProductDesc.setError("Description is required");
            return;
        }
        if (category.equals("Select any category")) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }
        if (resultUri1 != null && resultUri2 != null && resultUri3 != null && resultUri4 != null) {

            progressBar.setVisibility(View.VISIBLE);

            final String imgID1 = databaseRef.push().getKey();
            final String imgID2 = databaseRef.push().getKey();
            final String imgID3 = databaseRef.push().getKey();
            final String imgID4 = databaseRef.push().getKey();

            fileReference1 = storageRef.child(imgID1);
            fileReference2 = storageRef.child(imgID2);
            fileReference3 = storageRef.child(imgID3);
            fileReference4 = storageRef.child(imgID4);

            fileReference1.putFile(resultUri1)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return fileReference1.getDownloadUrl();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadURL1 = uri.toString();
                            fileReference2.putFile(resultUri2)
                                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                        @Override
                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                            if (!task.isSuccessful()) {
                                                throw task.getException();
                                            }
                                            return fileReference2.getDownloadUrl();
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            downloadURL2 = uri.toString();
                                            fileReference3.putFile(resultUri3)
                                                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                        @Override
                                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                            if (!task.isSuccessful()) {
                                                                throw task.getException();
                                                            }
                                                            return fileReference3.getDownloadUrl();
                                                        }
                                                    })
                                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            downloadURL3 = uri.toString();
                                                            fileReference4.putFile(resultUri4)
                                                                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                                        @Override
                                                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                                            if (!task.isSuccessful()) {
                                                                                throw task.getException();
                                                                            }
                                                                            return fileReference4.getDownloadUrl();
                                                                        }
                                                                    })
                                                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                        @Override
                                                                        public void onSuccess(Uri uri) {
                                                                            downloadURL4 = uri.toString();
                                                                            id = databaseRef.child(category).push().getKey();

                                                                            ProductModel productModel = new ProductModel(id, name, price, desc, category,
                                                                                    downloadURL1, downloadURL2, downloadURL3, downloadURL4,
                                                                                    imgID1,imgID2,imgID3,imgID4);

                                                                            databaseRef.child(category).child(id).setValue(productModel);

                                                                            progressBar.setVisibility(View.INVISIBLE);

                                                                            Toast.makeText(getApplicationContext(), "Product Inserted", Toast.LENGTH_SHORT).show();
//                                                                            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                                                                                    .setAction("Action", null).show();
                                                                            emptyAll();
                                                                        }
                                                                    });
                                                        }
                                                    });

                                        }
                                    });

                        }
                    });

        } else {
            Toast.makeText(this, "Please inserted all images", Toast.LENGTH_SHORT).show();
        }
    }


    private void emptyAll() {
        etProductName.setText("");
        etProductPrice.setText("");
        etProductDesc.setText("");

        name = price = category = desc = "";
        resultUri1 = resultUri2 = resultUri3 = resultUri4 = null;
        img1.setImageResource(R.drawable.icon_add_image);
        img2.setImageResource(R.drawable.icon_add_image);
        img3.setImageResource(R.drawable.icon_add_image);
        img4.setImageResource(R.drawable.icon_add_image);
    }
}
