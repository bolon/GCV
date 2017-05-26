package com.rere.fish.gcv.result.product;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.rere.fish.gcv.R;
import com.rere.fish.gcv.result.ResultActivity;

import org.parceler.Parcels;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;

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
    @BindView(R.id.chipContainer) FlexboxLayout chipContainer;
    @BindView(R.id.text_product_status) TextView textViewProductStatus;
    private int mColumnCount = 2;
    private ResponseBL responseBL;
    private OnProductsFragmentInteractionListener productEventListener;
    private ProductAdapter productAdapter;

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
            textViewProductStatus.setVisibility(View.GONE);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mColumnCount));
            }
            productAdapter = new ProductAdapter(responseBL.getProducts(), productEventListener);
            recyclerView.setAdapter(productAdapter);
        } else {
            textViewProductStatus.setVisibility(View.VISIBLE);
            chipContainer.setVisibility(View.GONE);
        }

        recyclerView.addOnScrollListener(
                new InfiniteScrollListener((LinearLayoutManager) recyclerView.getLayoutManager()) {
                    @Override
                    public void onLoadMore(int current_page) {
                        productEventListener.onRequestedMoreProducts(current_page,
                                ResultActivity.NUMBER_PER_FETCH, Collections.EMPTY_LIST);
                    }
                });

        ChipCloudConfig config = new ChipCloudConfig().selectMode(
                ChipCloud.SelectMode.multi).checkedChipColor(
                getContext().getResources().getColor(R.color.colorPrimaryLight)).checkedTextColor(
                getContext().getResources().getColor(R.color.colorTextPrimary)).uncheckedChipColor(
                Color.parseColor("#efefef")).uncheckedTextColor(
                Color.parseColor("#666666")).useInsetPadding(true);

        ChipCloud chipCloud = new ChipCloud(getActivity(), chipContainer, config);
        chipCloud.addChip("test1");
        chipCloud.addChip("test2");

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnProductsFragmentInteractionListener) {
            productEventListener = (OnProductsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(
                    context.toString() + " must implement OnProductsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        productEventListener = null;
    }

    public void doProductAddition(List<ResponseBL.Product> productList) {
        productAdapter.addProducts(productList);
    }


    public interface OnProductsFragmentInteractionListener {
        void onProductsInteraction(String itemId);

        void onRequestedMoreProducts(int page, int number, List<String> categories);
    }
}
