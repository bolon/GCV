package com.rere.fish.gcv.result.product;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rere.fish.gcv.R;
import com.rere.fish.gcv.result.product.ProductFragment.OnProductsFragmentInteractionListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * And dev
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final List<ResponseBL.Product> products;
    private final List<ResponseBL.Product> filteredProducts = new ArrayList<>();
    private final OnProductsFragmentInteractionListener mListener;
    private Context context;

    public ProductAdapter(Context context, List<ResponseBL.Product> items, OnProductsFragmentInteractionListener listener) {
        products = items;
        mListener = listener;
        filteredProducts.addAll(products);
        this.context = context;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_product,
                parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, int position) {
        holder.product = filteredProducts.get(position);

        try {
            Glide.with(holder.cardView.getContext()).load(holder.product.images.get(0)).placeholder(
                    R.mipmap.ic_placeholder).centerCrop().error(R.mipmap.ic_placeholder).into(
                    holder.thumbProduct);
        } catch (IndexOutOfBoundsException e) {
            Glide.with(holder.cardView.getContext()).load(R.mipmap.ic_placeholder).into(
                    holder.thumbProduct);
        }

        holder.contentProduct.setText(holder.product.name);

        String formattedPrice = (NumberFormat.getCurrencyInstance(new Locale("id", "ID")).format(
                holder.product.price));
        holder.priceProduct.setText(formattedPrice);
        holder.cardView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onProductsInteraction(holder.product.url);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return filteredProducts.size();
    }

    public void addProducts(List<ResponseBL.Product> newProducts) {
        int oldLastPos = this.products.size();
        this.products.addAll(newProducts);
        this.filteredProducts.addAll(newProducts);
        notifyItemRangeInserted(oldLastPos, newProducts.size());
    }

    public void reDisplayProducts(String category, boolean isAddition) {
        if (isAddition) {
            new Thread(() -> {
                filteredProducts.addAll(0, getProductWithCategory(category));
                ((Activity) context).runOnUiThread(this::notifyDataSetChanged);
            }).start();
        } else {
            new Thread(() -> {
                filteredProducts.removeAll(getProductWithCategory(category));
                ((Activity) context).runOnUiThread(this::notifyDataSetChanged);
            }).start();

        }
        Timber.i("current_product_size " + filteredProducts.size());
        this.notifyDataSetChanged();
    }

    private List<ResponseBL.Product> getProductWithCategory(String category) {
        List<ResponseBL.Product> tempProducts = new ArrayList<>();

        for (ResponseBL.Product p : products) {
            if (p.category.equals(category)) tempProducts.add(p);
        }

        return tempProducts;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ResponseBL.Product product;
        @BindView(R.id.text_content_product) TextView contentProduct;
        @BindView(R.id.containerViewProduct) CardView cardView;
        @BindView(R.id.thumb_product) ImageView thumbProduct;
        @BindView(R.id.text_price_product) TextView priceProduct;

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
