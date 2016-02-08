package org.happymtb.unofficial.fragment;

import java.util.List;

import org.happymtb.unofficial.MainActivity;
import org.happymtb.unofficial.R;
import org.happymtb.unofficial.adapter.ListVideoAdapter;
import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.VideoData;
import org.happymtb.unofficial.item.VideoItem;
import org.happymtb.unofficial.listener.PageTextWatcher;
import org.happymtb.unofficial.listener.VideoListListener;
import org.happymtb.unofficial.task.VideoListTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VideoListFragment extends RefreshListfragment implements DialogInterface.OnCancelListener {
	public static String TAG = "video_frag";

	private final static int DIALOG_FETCH_KOS_ERROR = 0;
	private VideoListTask getVideo;
	private ListVideoAdapter mVideoAdapter;
	private VideoData mVideoData = new VideoData(1, 1, "", 0, null, 0);
	private AlertDialog.Builder mAlertDialog;
    private MainActivity mActivity;
	private ListView mListView;

	/** Called when the activity is first created. */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
		mActivity.getSupportActionBar().setTitle(R.string.main_video);


		if (savedInstanceState != null) {
			mVideoData = (VideoData) savedInstanceState.getSerializable(DATA);

			fillList();
			showProgress(false);
		} else {
			fetchData();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mVideoData != null) {
			if (mListView != null) {
				mVideoData.setListPosition(mListView.getFirstVisiblePosition());
			} else {
				mVideoData.setListPosition(0);
			}
				outState.putSerializable(DATA, mVideoData);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.video_frame, container, false);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.video_menu, menu);

		menu.findItem(R.id.video_left).setVisible(mVideoData.getCurrentPage() > 1);
		menu.findItem(R.id.video_right).setVisible(mVideoData.getCurrentPage() < mVideoData.getMaxPages());
		super.onCreateOptionsMenu(menu, inflater);
	}			

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.video_submenu:
			return true;	
		case R.id.video_left:
			previousPage();
			return true;
		case R.id.video_right:
			nextPage();
			return true;
//		case R.id.video_search:
//	        FragmentManager fm = mActivity.getSupportFragmentManager();
//	        VideoSearchDialogFragment videoSearchDialog = new VideoSearchDialogFragment();
//	        videoSearchDialog.show(fm, "fragment_edit_name");
//			return true;
		case R.id.video_go_to_page:			
			mAlertDialog = new AlertDialog.Builder(mActivity);

			mAlertDialog.setTitle(R.string.goto_page);
			mAlertDialog.setMessage(getString(R.string.enter_page_number, mVideoData.getMaxPages()));

			// Set an EditText view to get user input 
			final EditText input = new EditText(mActivity);
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			mAlertDialog.setView(input);

			mAlertDialog.setPositiveButton(R.string.jump, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Do something with value!
					if (HappyUtils.isInteger(input.getText().toString())) {
						mVideoData.setCurrentPage(Integer.parseInt(input.getText().toString()));
						fetchData();
					}
				}
			});

			mAlertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Cancelled
				}
			});

			final AlertDialog dialog = mAlertDialog.create();

			input.addTextChangedListener(new PageTextWatcher(dialog, mVideoData.getMaxPages()));

			dialog.show();
			return true;	
		case R.id.video_new_item:
			String url = "http://happyride.se/video/upload.php";
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(browserIntent);							
			return true;				
		}				
		return super.onOptionsItemSelected(item);
	}	
	
	private void fetchData() {
        if (mActivity == null) {
            return;
        }
        showProgress(true);

		getVideo = new VideoListTask();
		getVideo.addVideoListListener(new VideoListListener() {
			public void success(List<VideoItem> VideoItems) {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    mVideoData.setVideoItems(VideoItems);
                    fillList();

                    showProgress(false);
                }
			}

			public void fail() {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    Toast.makeText(getActivity(), R.string.no_items_found, Toast.LENGTH_LONG).show();
                    mVideoData = new VideoData(1, 1, "", 0, null, 0);
                    showProgress(false);
                }
			}			
		});
		getVideo.execute(mVideoData.getCurrentPage(), mVideoData.getCategory(), mVideoData.getSearch());	
	}

	private void fillList() {
        if (mVideoData.getVideoItems() == null || mVideoData.getVideoItems().isEmpty()) {
             // Workaround for orientation changes before finish loading
            showProgress(true);
            fetchData();
            return;
        }

		if (mVideoAdapter == null) {
			mVideoAdapter = new ListVideoAdapter(mActivity, mVideoData.getVideoItems());
			setListAdapter(mVideoAdapter);
		} else {
			mVideoAdapter.setItems(mVideoData.getVideoItems());
			mVideoAdapter.notifyDataSetChanged();
		}

		//TODO: View gets null sometimes
		TextView currentPage = (TextView) mActivity.findViewById(R.id.video_current_page);
		currentPage.setText(Integer.toString(mVideoData.getCurrentPage()));
		
		TextView maxPages = (TextView) mActivity.findViewById(R.id.video_no_of_pages);
		maxPages.setText(Integer.toString(mVideoData.getVideoItems().get(0).getNumberOfVideoPages()));
		mVideoData.setMaxPages(mVideoData.getVideoItems().get(0).getNumberOfVideoPages());			
		
		TextView category = (TextView) mActivity.findViewById(R.id.video_category);
		category.setText("Kategori: " + mVideoData.getVideoItems().get(0).getSelectedCategory());

		//TODO Uncomment when fixing Search
//		TextView searchView = (TextView) mActivity.findViewById(R.id.video_search);
//
//		String mSearch = mVideoData.getSearch();
//
//		if (mSearch.length() > 0) {
//			searchView.setText(" (Sökord: " + mSearch + ")");
//		} else {
//			searchView.setText("");
//		}

        getActivity().invalidateOptionsMenu();
	}

	public void refreshList() {
		mVideoData.setListPosition(0);
		mVideoData.setCurrentPage(1);
		fetchData();
	}

	public void nextPage() {
		if (mVideoData.getCurrentPage() < mVideoData.getMaxPages()) {
			mVideoData.setListPosition(0);
			mVideoData.setCurrentPage(mVideoData.getCurrentPage() + 1);		
			fetchData();
		}
	}

	public void previousPage() {
    	if (mVideoData.getCurrentPage() > 1) {
    		mVideoData.setListPosition(0);
    		mVideoData.setCurrentPage(mVideoData.getCurrentPage() - 1);	
			fetchData();
    	}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String url = mVideoData.getVideoItems().get(position).getLink();		
		url = url.replaceAll("video", "video/i");
		Uri uri = Uri.parse(url);
		Intent i = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(i);				
	}

	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder;
		switch (id) {
		case DIALOG_FETCH_KOS_ERROR:
			builder = new AlertDialog.Builder(mActivity);
			builder.setTitle(R.string.error_message);
			builder.setMessage(
					"Det blev något fel vid hämtning av videolistan")
					.setPositiveButton(R.string.OK,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									mActivity.finish();
								}
							});
			dialog = builder.create();
			break;
		}
		return dialog;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		//getActivity().finish();
	}

	// TODO Samma lösning som de andra dialogerna
//	public class VideoSearchDialogFragment extends DialogFragment {
//		 public DialogFragment newInstace() {
//			 DialogFragment dialogFragment = new VideoSearchDialogFragment();
//			 return dialogFragment;
//		 }
//
//		@Override
//	    public Dialog onCreateDialog(Bundle savedInstanceState) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
//	        LayoutInflater inflater = mActivity.getLayoutInflater();
//	        final View view = inflater.inflate(R.layout.video_search, null);
//	        builder.setView(view);
//	        builder.setPositiveButton("Sök", new DialogInterface.OnClickListener() {
//               @Override
//               public void onClick(DialogInterface dialog, int id) {
//            	   EditText mSearchString = (EditText) view.findViewById(R.id.video_dialog_search_text);
//            	   Spinner mSearchCategory = (Spinner) view.findViewById(R.id.video_dialog_search_category);
//
//            	   mVideoData.setSearch(mSearchString.getText().toString());
//               	   int position = mSearchCategory.getSelectedItemPosition();
//               	   String CategoryArrayPosition [] =  getResources().getStringArray(R.array.video_dialog_search_category_position);
//               	   mVideoData.setCategoryPos(Integer.parseInt(CategoryArrayPosition[position]));
//
//               	   mVideoData.setCurrentPage(1);
//               	   fetchData();
//               }
//	        });
//	        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//	        	public void onClick(DialogInterface dialog, int id) {
//	    		   VideoSearchDialogFragment.this.getDialog().cancel();
//	        	}
//	        });
//	        Dialog dialog = builder.create();
//	        return dialog;
//	    }
//	}
}