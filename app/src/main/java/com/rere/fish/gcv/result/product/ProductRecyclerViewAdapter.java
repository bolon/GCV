package com.rere.fish.gcv.result.product;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rere.fish.gcv.R;
import com.rere.fish.gcv.result.product.ProductFragment.OnProductsFragmentInteractionListener;

import java.util.List;

/**
 * And dev
 */
public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolder> {

    private final List<ResponseBL.Product> mValues;
    private final OnProductsFragmentInteractionListener mListener;

    public ProductRecyclerViewAdapter(List<ResponseBL.Product> items, OnProductsFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).name);

        holder.cardView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onProductsInteraction(holder.mItem.url);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final CardView cardView;
        public final TextView mIdView;
        public final TextView mContentView;
        public ResponseBL.Product mItem;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.containerViewProduct);
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
