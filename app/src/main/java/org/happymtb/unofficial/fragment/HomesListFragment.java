package org.happymtb.unofficial.fragment;

import java.util.ArrayList;

import org.happymtb.unofficial.R;
import org.happymtb.unofficial.adapter.ListHomeAdapter;
import org.happymtb.unofficial.item.Home;
import org.happymtb.unofficial.listener.HomeListListener;
import org.happymtb.unofficial.task.HomeListTask;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;

public class HomesListFragment extends RefreshListfragment implements DialogInterface.OnCancelListener, OnChildClickListener {
	public static String TAG = "home_frag";
	private ListHomeAdapter mHomeAdapter;
	private ArrayList<Home> mHomes = new ArrayList<Home>();

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.main_home));
		if (savedInstanceState != null) {
			mHomes = (ArrayList<Home>)savedInstanceState.getSerializable(DATA);

			if (mHomes != null && !mHomes.isEmpty()) {
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(DATA, mHomes);
    }

    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.home_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}		
	
	public void refreshList() {
		mHomeAdapter = null;
		fetchData();
	}
	private void fetchData() {
        showProgress(true);

		HomeListTask homeListTask = new HomeListTask();
		homeListTask.addHomeListListener(new HomeListListener() {
			public void success(ArrayList<Home> Homes) {
				if (getActivity() != null && !getActivity().isFinishing()) {
					mHomes = Homes;
					fillList();

					showProgress(false);
				}
			}

			public void fail() {
				if (getActivity() != null && !getActivity().isFinishing()) {
					Toast.makeText(getActivity(), R.string.no_items_found, Toast.LENGTH_LONG).show();

					showProgress(false);
				}
			}
		});
		homeListTask.execute();
	}
	
	protected void fillList() {
		if (mHomeAdapter == null) {
			mHomeAdapter = new ListHomeAdapter(getActivity(), mHomes);
			setListAdapter(mHomeAdapter);
		} else {
            mHomeAdapter.setItems(mHomes);
			mHomeAdapter.notifyDataSetChanged();
		}
	}
			
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mHomes.get(position).getLink()));
		startActivity(intent);
	}	
	
	@Override
	public void onCancel(DialogInterface dialog) {}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		return false;
	}
}