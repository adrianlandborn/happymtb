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
import org.happymtb.unofficial.task.VideoImageDownloadTask;
import org.happymtb.unofficial.task.VideoListTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
	private Boolean mPictureList;
	private SharedPreferences preferences;
    private MainActivity mActivity;
	
	/** Called when the activity is first created. */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
//        setListShownNoAnimation(false);
		setHasOptionsMenu(true);		
		fetchData();
		
		preferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mPictureList = preferences.getBoolean("videopicturelist", true);
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.video_menu, menu);
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

			mAlertDialog.setTitle("Gå till sidan...");
			mAlertDialog.setMessage("Skriv in sidnummer som du vill hoppa till (1 - " + mVideoData.getMaxPages() + ")");

			// Set an EditText view to get user input 
			final EditText input = new EditText(mActivity);
			mAlertDialog.setView(input);

			mAlertDialog.setPositiveButton("Hoppa", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Do something with value!
					if (HappyUtils.isInteger(input.getText().toString())) {
						mVideoData.setCurrentPage(Integer.parseInt(input.getText().toString()));
						fetchData();
					}
				}
			});

			mAlertDialog.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			final AlertDialog dialog = mAlertDialog.create();

			input.addTextChangedListener(new PageTextWatcher(dialog, mVideoData.getMaxPages()));

			dialog.show();
			return true;	
		case R.id.video_new_item:
			String url = "http://happymtb.org/video/upload.php";
			Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
			startActivity(browserIntent);							
			return true;				
		}				
		return super.onOptionsItemSelected(item);
	}	
	
	@Override
	public void onDestroy() {
//        if (progDialog != null) {
//            progDialog.dismiss();
//        }
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

	private void fetchData() {
        if (mActivity == null) {
            return;
        }
        showProgress(true);

		getVideo = new VideoListTask();
		getVideo.addVideoListListener(new VideoListListener() {
			public void success(List<VideoItem> VideoItems) {
                if (getActivity() != null) {
                    mVideoData.setVideoItems(VideoItems);
                    fillList();
                    if (mPictureList) {
                        VideoImageDownloadTask getVideoImages = new VideoImageDownloadTask();
                        getVideoImages.execute(mVideoData.getVideoItems(), mVideoAdapter);
                    }

                    showProgress(false);
                }
			}

			public void fail() {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Inga objekt hittades", Toast.LENGTH_LONG).show();
                    mVideoData = new VideoData(1, 1, "", 0, null, 0);
                    showProgress(false);
                }
			}			
		});
		getVideo.execute(mVideoData.getCurrentPage(), mVideoData.getCategory(), mVideoData.getSearch());	
	}

	private void fillList() {
		mVideoAdapter = new ListVideoAdapter(mActivity, mVideoData.getVideoItems());
		setListAdapter(mVideoAdapter);

		getListView().setSelection(mVideoData.getListPosition());
		
		TextView CurrentPage = (TextView) mActivity.findViewById(R.id.video_current_page);
		CurrentPage.setText(Integer.toString(mVideoData.getCurrentPage()));		
		
		TextView MaxPages = (TextView) mActivity.findViewById(R.id.video_no_of_pages);
		MaxPages.setText(Integer.toString(mVideoData.getVideoItems().get(0).getNumberOfVideoPages()));
		mVideoData.setMaxPages(mVideoData.getVideoItems().get(0).getNumberOfVideoPages());			
		
		TextView Category = (TextView) mActivity.findViewById(R.id.video_category);
		Category.setText("Kategori: " + mVideoData.getVideoItems().get(0).getSelectedCategory());

//		TextView searchView = (TextView) mActivity.findViewById(R.id.video_search);
//
//		String mSearch = mVideoData.getSearch();
//
//		if (mSearch.length() > 0) {
//			searchView.setText(" (Sökord: " + mSearch + ")");
//		} else {
//			searchView.setText("");
//		}
	}

	public void refreshList() {
		mVideoData.setListPosition(0);
		mVideoData.setCurrentPage(0);
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
			builder.setTitle("Felmeddelande");
			builder.setMessage(
					"Det blev något fel vid hämtning av köp och sälj")
					.setPositiveButton("Ok",
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
//	        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
//	        	public void onClick(DialogInterface dialog, int id) {
//	    		   VideoSearchDialogFragment.this.getDialog().cancel();
//	        	}
//	        });
//	        Dialog dialog = builder.create();
//	        return dialog;
//	    }
//	}
}