package com.jsstech.woocommerceapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jsstech.woocommerceapp.Interface.ItemClickListener;
import com.jsstech.woocommerceapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
   public TextView txtProductName,txtProductDescription,txtProductPrice;
   public ImageView pImageView;
    private ItemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {

        super(itemView);
        pImageView=itemView.findViewById(R.id.mProd_image);
        txtProductName=itemView.findViewById(R.id.mProd_name);
        txtProductDescription=itemView.findViewById(R.id.mProd_Descr);
        txtProductPrice=itemView.findViewById(R.id.mProd_prices);


    }
    public void setItemClickListener(ItemClickListener listener){

        this.listener=listener;
    }

    @Override
    public void onClick(View view) {

        listener.onClick(view,getAdapterPosition(),false);

    }
}
