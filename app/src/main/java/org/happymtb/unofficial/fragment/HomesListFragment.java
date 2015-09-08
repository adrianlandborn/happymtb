package org.happymtb.unofficial.fragment;

import java.util.ArrayList;
import java.util.List;

import org.happymtb.unofficial.R;
import org.happymtb.unofficial.adapter.ListHomeAdapter;
import org.happymtb.unofficial.item.Home;
import org.happymtb.unofficial.listener.HomeListListener;
import org.happymtb.unofficial.task.HomeListTask;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;

public class HomesListFragment extends RefreshListfragment implements DialogInterface.OnCancelListener, OnChildClickListener {
	private ListHomeAdapter mHomeAdapter;
	private List<Home> mHomes = new ArrayList<Home>();
	private HomeListTask getHome;
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setHasOptionsMenu(true);
		fetchData();
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.home_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}		
	
	public void refreshPage() {
		fetchData();
	}
	private void fetchData() {
        showProgress(true);

		getHome = new HomeListTask();
		getHome.addHomeListListener(new HomeListListener() {
			public void success(List<Home> Homes) {
                if (getActivity() != null) {
                    mHomes = Homes;
                    fillList();

                    showProgress(false);
                }
			}

			public void fail() {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Inga objekt hittades", Toast.LENGTH_LONG).show();

                    showProgress(false);
                }
			}
		});
		getHome.execute();
	}
	
	protected void fillList() {
        if (getActivity() != null) {
            if (mHomeAdapter == null) {
                mHomeAdapter = new ListHomeAdapter(getActivity(), mHomes);
                setListAdapter(mHomeAdapter);
            } else {
                mHomeAdapter.notifyDataSetChanged();
            }
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