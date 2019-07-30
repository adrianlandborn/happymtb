package org.happymtb.unofficial.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.happymtb.unofficial.R;
import org.happymtb.unofficial.item.Item;
import org.happymtb.unofficial.volley.MyRequestQueue;
import org.happymtb.unofficial.volley.ShopsListRequest;

import java.util.ArrayList;
import java.util.List;

public class ShopsListFragment extends ItemsListFragment implements DialogInterface.OnCancelListener, OnChildClickListener {
	public static String TAG = "shops_frag";

	private ShopsListRequest mRequest;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.main_shops));
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void fetchItems() {
		mRequest = new ShopsListRequest(new Response.Listener<List<Item>>() {
            @Override
            public void onResponse(List<Item> items) {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    mAllItems = (ArrayList<Item>) items;
                    fillList();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    Toast.makeText(getActivity(), R.string.shops_no_items_found, Toast.LENGTH_SHORT).show();
                }

            }
        });

        MyRequestQueue.getInstance(getContext()).addRequest(mRequest);
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.shops_submenu:
				return true;
			case R.id.shops_expand_all:
				expandAll();
				return true;	
			case R.id.shops_hide_all:
				collapseAll();
				return true;					
//			case R.id.shops_add:
//				String url = "https://happyride.se/forum/butiker/add.php?cat=4";
//				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//				startActivity(browserIntent);
//				return true;
		}
		return super.onOptionsItemSelected(item);
	}
		
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.shops_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
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