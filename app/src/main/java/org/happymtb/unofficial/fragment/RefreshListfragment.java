package org.happymtb.unofficial.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.happymtb.unofficial.R;

/**
 * Created by Adrian on 07/09/2015.
 */
public abstract class RefreshListfragment extends ListFragment {

    public final static String CURRENT_PAGE = "current_page";
    public final static String CURRENT_POSITION = "current_position";
    public final static String DATA = "data";
    public final static String SLIDE_MENU_ID = "slide_menu_id";
    public final static String LOGGED_IN = "logged_in";

    protected static final int SWIPE_MIN_DISTANCE = 130;
    protected static final int SWIPE_THRESHOLD_VELOCITY = 80;

    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected View mProgressView;

    protected View mNoNetworkView;
    protected Button mReloadButton;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity() ;
        mSwipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mProgressView.setVisibility(View.INVISIBLE);
                reloadCleanList();
            }
        });

        mProgressView = activity.findViewById(R.id.progress_container_id);
        setHasOptionsMenu(true);
        showProgress(true);

        mNoNetworkView = activity.findViewById(R.id.no_network_layout);
        mReloadButton = (Button)activity.findViewById(R.id.reload_button);

        if (mReloadButton != null) {
            mReloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fetchData();
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.refresh_list_loader, container, false);
    }

    protected void showProgress(boolean visible) {
        if (getActivity() != null && !getActivity().isFinishing()) {
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

    protected void showNoNetworkView(boolean visible) {
        if (getActivity() != null && !getActivity().isFinishing() && mNoNetworkView != null) {
            if (visible) {
                mNoNetworkView.setVisibility(View.VISIBLE);
            } else {
                mNoNetworkView.setVisibility(View.GONE);
            }
        }
    }

    protected void showList(boolean visible) {
        if (getActivity() != null && !getActivity().isFinishing() && mSwipeRefreshLayout != null) {
            if (visible) {
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            } else {
                mSwipeRefreshLayout.setVisibility(View.GONE);
            }
        }
    }

    protected boolean hasNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean hasConnection = cm.getActiveNetworkInfo() != null;
        if (hasConnection) {
            mNoNetworkView.setVisibility(View.GONE);
            showProgress(true);
        } else {
            showProgress(false);
            mSwipeRefreshLayout.setVisibility(View.GONE);
            mNoNetworkView.setVisibility(View.VISIBLE);
        }

        return hasConnection;
    }

    protected abstract void fetchData();

    protected abstract void reloadCleanList();
}
