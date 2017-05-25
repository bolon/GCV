package com.rere.fish.gcv.result.product;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rere.fish.gcv.R;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnProductsFragmentInteractionListener}
 * interface.
 */
public class ProductFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_PRODUCTS = "products";

    @BindView(R.id.list) RecyclerView recyclerView;
    @BindView(R.id.toolbarListProduct) Toolbar toolbar;
    private int mColumnCount = 2;
    private ResponseBL responseBL;
    private OnProductsFragmentInteractionListener mListener;

    public ProductFragment() {
    }

    @SuppressWarnings("unused")
    public static ProductFragment newInstance(int columnCount, ResponseBL responseBL) {
        ProductFragment fragment = new ProductFragment();
        Parcelable responseWrapped = Parcels.wrap(responseBL);

        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putParcelable(ARG_PRODUCTS, responseWrapped);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            responseBL = Parcels.unwrap(getArguments().getParcelable(ARG_PRODUCTS));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        ButterKnife.bind(this, view);

        // Set the adapter
        if (!responseBL.getProducts().isEmpty()) {
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mColumnCount));
            }
            recyclerView.setAdapter(new ProductAdapter(responseBL.getProducts(), mListener));
        } else {
            //todo : if response empty show smthing (maybe lottie animation)
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnProductsFragmentInteractionListener) {
            mListener = (OnProductsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnProductsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnProductsFragmentInteractionListener {
        void onProductsInteraction(String itemId);
    }
}
