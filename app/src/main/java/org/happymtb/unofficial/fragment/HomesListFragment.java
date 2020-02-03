package org.happymtb.unofficial.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.happymtb.unofficial.R;
import org.happymtb.unofficial.adapter.HomeAdapter;
import org.happymtb.unofficial.item.HomeItem;
import org.happymtb.unofficial.volley.HomeListRequest;
import org.happymtb.unofficial.volley.MyRequestQueue;

import java.util.ArrayList;
import java.util.List;

public class HomesListFragment extends RefreshListfragment implements OnChildClickListener {
	public static String TAG = "home_frag";
	private HomeAdapter mHomeAdapter;
    private HomeListRequest mRequest;
    private ArrayList<HomeItem> mHomeItems = new ArrayList<>();

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.main_home));
		if (savedInstanceState != null) {
			mHomeItems = (ArrayList<HomeItem>)savedInstanceState.getSerializable(DATA);

			if (mHomeItems != null && !mHomeItems.isEmpty()) {
				fillList();
				showProgress(false);
			} else {
                fetchData();
			}
        } else {
            fetchData();
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(DATA, mHomeItems);
    }

    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.home_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}		
	
	public void reloadCleanList() {
		mHomeAdapter = null;
		fetchData();
	}

	@Override
	protected void fetchData() {
        if (hasNetworkConnection(true)) {
            mRequest = new HomeListRequest(new Response.Listener<List<HomeItem>>() {

                @Override
                public void onResponse(List<HomeItem> homeItems) {
                    mHomeItems = (ArrayList<HomeItem>) homeItems;

                    fillList();
                    showProgress(false);
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
					if (error != null) {
						Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
					}
                    mHomeItems = new ArrayList<>();
                }
            });

            MyRequestQueue.getInstance(getContext()).addRequest(mRequest);
        }
    }

	protected void fillList() {
		if (mHomeAdapter == null) {
			mHomeAdapter = new HomeAdapter(getActivity(), mHomeItems);
			setListAdapter(mHomeAdapter);
		} else {
            mHomeAdapter.setItems(mHomeItems);
			mHomeAdapter.notifyDataSetChanged();
		}
	}
			
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mHomeItems.get(position).getLink()));
		startActivity(intent);
	}	
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		return false;
	}

    @Override
    public void onDestroy() {
        if (mRequest != null) {
            mRequest.removeListener();
            mRequest.cancel();
        }
        super.onDestroy();
    }
}