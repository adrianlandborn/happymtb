package pebeijer.happymtb.fragment;

import java.util.ArrayList;
import java.util.List;

import pebeijer.happymtb.R;
import pebeijer.happymtb.adapter.ListHomeAdapter;
import pebeijer.happymtb.item.Home;
import pebeijer.happymtb.listener.HomeListListener;
import pebeijer.happymtb.task.HomeListTask;
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
	protected ProgressDialog progDialog = null;
	protected ListHomeAdapter HomeAdapter;
	protected List<Home> mAllHomes = new ArrayList<Home>();
	protected List<Home> mHomes = new ArrayList<Home>();
	private HomeListTask getHome;
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setListShownNoAnimation(true);
		setHasOptionsMenu(true);
		FetchData();
		
		getListView().setDivider(null);
		getListView().setDividerHeight(0);		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.homemenu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}		
	
	@Override
	public void onDestroy() {
		progDialog.dismiss();
		super.onDestroy();
	}

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
	
	private void FetchData() {
		if ((progDialog == null) || (!progDialog.isShowing())) {
			progDialog = ProgressDialog.show(getActivity(), "", "", true, true);
			progDialog.setContentView(R.layout.progresslayout);
			progDialog.setOnCancelListener(this);
		}
		
		getHome = new HomeListTask();
		getHome.addHomeListListener(new HomeListListener() {
			public void Success(List<Home> Homes) {
				mHomes = Homes;										
				FillList();			
				progDialog.dismiss();
			}

			public void Fail() {
				Toast mToast;
				mToast = Toast.makeText( getActivity()  , "" , Toast.LENGTH_LONG );
				mToast.setText("Inga objekt hittades");
				mToast.show();
							
				progDialog.dismiss();
//				showDialog(DIALOG_FETCH_KOS_ERROR);
			}
		});
		getHome.execute();
	}
	
	protected void FillList() {	
		if (HomeAdapter == null) {
			HomeAdapter = new ListHomeAdapter(getActivity(), mHomes);
			setListAdapter(HomeAdapter);
		} else {
			HomeAdapter.notifyDataSetChanged();
		}		
	}	
			
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mHomes.get(position).getLink()));
		startActivity(intent);
	}	
	
	@Override
	public void onCancel(DialogInterface dialog) {
		//getActivity().finish();
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		// TODO Auto-generated method stub
		return false;
	}
}