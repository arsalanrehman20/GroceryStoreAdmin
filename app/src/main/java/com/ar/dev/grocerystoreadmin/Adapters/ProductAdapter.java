package com.ar.dev.grocerystoreadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.ar.dev.grocerystoreadmin.Models.ProductModel;
import com.ar.dev.grocerystoreadmin.ProductDetailActivity;
import com.ar.dev.grocerystoreadmin.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Filterable {

    public static final String PRODUCT_ID = "productid";
    public static final String PRODUCT_NAME = "productname";
    public static final String PRODUCT_PRICE = "productprice";
    public static final String PRODUCT_CATEGORY = "productcategory";
    public static final String PRODUCT_DESCRIPTION = "productdescription";

    public static final String IMAGE_ID1 = "img1";
    public static final String IMAGE_ID2 = "img2";
    public static final String IMAGE_ID3 = "img3";
    public static final String IMAGE_ID4 = "img4";

    private Context context;
    private List<ProductModel> productModelList;
    private List<ProductModel> productModelListFull;

    public ProductAdapter(Context context, List<ProductModel> productModelList) {
        this.context = context;
        this.productModelList = productModelList;

        productModelListFull=new ArrayList<>(productModelList);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.product_list_layout, parent, false);

        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, int position) {

        final ProductModel currentProductModel = productModelList.get(position);
        holder.tvNameRV.setText(currentProductModel.getName());
        Picasso.get()
                .load(currentProductModel.getImgUrl1())
                .into(holder.imgViewRV);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra(PRODUCT_ID, currentProductModel.getProductID());
                intent.putExtra(PRODUCT_NAME, currentProductModel.getName());
                intent.putExtra(PRODUCT_CATEGORY, currentProductModel.getCategory());
                intent.putExtra(PRODUCT_PRICE, currentProductModel.getPrice());
                intent.putExtra(PRODUCT_DESCRIPTION, currentProductModel.getDesc());
                intent.putExtra(IMAGE_ID1,currentProductModel.getImgID1());
                intent.putExtra(IMAGE_ID2,currentProductModel.getImgID2());
                intent.putExtra(IMAGE_ID3,currentProductModel.getImgID3());
                intent.putExtra(IMAGE_ID4,currentProductModel.getImgID4());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }


    public class ProductViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView imgViewRV;
        public TextView tvNameRV;

        public ProductViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardview_id);
            imgViewRV = itemView.findViewById(R.id.imgProductListLayout);
            tvNameRV = itemView.findViewById(R.id.tvNameProductListLayout);
        }
    }

    @Override
    public Filter getFilter() {
        return productFilter;
    }
    private Filter productFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ProductModel> filteredList = new ArrayList<>();

            if(constraint==null || constraint.length()==0){
                filteredList.addAll(productModelListFull);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(ProductModel productItem : productModelListFull){
                    if(productItem.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(productItem);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values=filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            productModelList.clear();
            productModelList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}
