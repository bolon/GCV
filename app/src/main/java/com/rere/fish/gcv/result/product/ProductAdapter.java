package com.rere.fish.gcv.result.product;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rere.fish.gcv.R;
import com.rere.fish.gcv.result.product.ProductFragment.OnProductsFragmentInteractionListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * And dev
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<ResponseBL.Product> products;
    private final OnProductsFragmentInteractionListener mListener;

    public ProductAdapter(List<ResponseBL.Product> items, OnProductsFragmentInteractionListener listener) {
        products = items;
        mListener = listener;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_product,
                parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, int position) {
        holder.product = products.get(position);

        Glide.with(holder.cardView).load(holder.product.images.get(0)).apply(
                new RequestOptions().centerCrop()).into(holder.thumbProduct);

        holder.idProduct.setText(products.get(position).id);
        holder.contentProduct.setText(products.get(position).name);

        holder.cardView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onProductsInteraction(holder.product.url);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ResponseBL.Product product;
        @BindView(R.id.text_content_product) TextView contentProduct;
        @BindView(R.id.containerViewProduct) CardView cardView;
        @BindView(R.id.text_id_product) TextView idProduct;
        @BindView(R.id.thumb_product) ImageView thumbProduct;

        public ProductViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + contentProduct.getText() + "'";
        }
    }
}
