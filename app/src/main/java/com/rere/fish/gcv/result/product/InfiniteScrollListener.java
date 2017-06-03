package com.rere.fish.gcv.result.product;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.rere.fish.gcv.result.OnChipContainerInteraction;

/**
 * Created by Android dev on 5/26/17.
 */

public abstract class InfiniteScrollListener extends RecyclerView.OnScrollListener {
    private static final int SCROLL_DOWN_TRESHOLD = 72; //px
    private static final int SCROLL_UP_TRESHOLD = -72;
    private final int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int currentPage = 1;
    private LinearLayoutManager layoutManager;
    private Runnable loadMore = () -> onLoadMore(currentPage);
    private OnChipContainerInteraction chipListener;

    public InfiniteScrollListener(LinearLayoutManager layoutManager, OnChipContainerInteraction chipListener) {
        this.layoutManager = layoutManager;
        this.chipListener = chipListener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = layoutManager.getItemCount();
        firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

        if (dy > SCROLL_DOWN_TRESHOLD) {
            chipListener.onChipViewStateChange(false);
        } else if (dy < SCROLL_UP_TRESHOLD) {
            chipListener.onChipViewStateChange(true);
        } else if (firstVisibleItem == 0) {
            chipListener.onChipViewStateChange(true);
        }

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
