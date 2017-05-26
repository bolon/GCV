package com.rere.fish.gcv.result.product;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Android dev on 5/26/17.
 */

public abstract class InfiniteScrollListener extends RecyclerView.OnScrollListener {
    private final int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int currentPage = 1;
    private LinearLayoutManager layoutManager;
    private Runnable loadMore = () -> onLoadMore(currentPage);

    public InfiniteScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = layoutManager.getItemCount();
        firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal || totalItemCount == 0) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }

        // End has been reached
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            currentPage++;
            recyclerView.post(loadMore);
            loading = true;
        }

    }

    public abstract void onLoadMore(int current_page);
}
