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

public class HomesListFragment extends ListFragment implements DialogInterface.OnCancelListener, OnChildClickListener {
//	private ProgressDialog mProgressDialog = null;
	private ListHomeAdapter mHomeAdapter;
	private List<Home> mHomes = new ArrayList<Home>();
	private HomeListTask getHome;
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
//		setListShownNoAnimation(true);
		setHasOptionsMenu(true);
		fetchData();
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.home_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}		
	
//	@Override
//	public void onDestroy() {
//		mProgressDialog.dismiss();
//		super.onDestroy();
//	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	private void fetchData() {
//		if ((mProgressDialog == null) || (!mProgressDialog.isShowing())) {
//			mProgressDialog = ProgressDialog.show(getActivity(), "", "", true, true);
//			mProgressDialog.setContentView(R.layout.progress_layout);
//			mProgressDialog.setOnCancelListener(this);
//		}
		
		getHome = new HomeListTask();
		getHome.addHomeListListener(new HomeListListener() {
			public void success(List<Home> Homes) {
                if (getActivity() != null) {
                    mHomes = Homes;
                    fillList();
//                    mProgressDialog.dismiss();
                }
			}

			public void fail() {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Inga objekt hittades", Toast.LENGTH_LONG).show();

//                    mProgressDialog.dismiss();
//				showDialog(DIALOG_FETCH_KOS_ERROR);
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