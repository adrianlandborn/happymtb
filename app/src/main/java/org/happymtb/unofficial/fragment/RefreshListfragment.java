package org.happymtb.unofficial.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.happymtb.unofficial.R;

/**
 * Created by Adrian on 07/09/2015.
 */
public abstract class RefreshListfragment extends ListFragment {

    public final static String CURRENT_PAGE = "current_page";
    public final static String CURRENT_POSITION = "current_position";
    public final static String DATA = "data";
    public final static String LOGGED_IN = "logged_in";

    protected static final int SWIPE_MIN_DISTANCE = 130;
    protected static final int SWIPE_THRESHOLD_VELOCITY = 80;

    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected View mProgressView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mProgressView.setVisibility(View.INVISIBLE);
                refreshList();
            }
        });

        mProgressView = getActivity().findViewById(R.id.progress_container_id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.refresh_list_loader, container, false);
    }

    protected void showProgress(boolean visible) {
        if (getActivity() != null) {
            if (visible) {
                if (!mSwipeRefreshLayout.isRefreshing()) {
                    mProgressView.setVisibility(View.VISIBLE);
                }
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
                mProgressView.setVisibility(View.INVISIBLE);
            }
        }
    }

    protected abstract void refreshList();
}
