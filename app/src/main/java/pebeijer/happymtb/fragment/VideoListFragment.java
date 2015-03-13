package pebeijer.happymtb.fragment;

import java.util.List;

import pebeijer.happymtb.R;
import pebeijer.happymtb.adapter.ListVideoAdapter;
import pebeijer.happymtb.helpers.Utilities;
import pebeijer.happymtb.item.VideoData;
import pebeijer.happymtb.item.VideoItem;
import pebeijer.happymtb.listener.VideoListListener;
import pebeijer.happymtb.task.VideoImageDownloadTask;
import pebeijer.happymtb.task.VideoListTask;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class VideoListFragment extends ListFragment implements DialogInterface.OnCancelListener {
	private final static int DIALOG_FETCH_KOS_ERROR = 0;
	private ProgressDialog progDialog = null;
	private VideoListTask getVideo;
	private ListVideoAdapter mVideoAdapter;
	private VideoData mVideoData = new VideoData(1, 1, "", 0, null, 0);
	private AlertDialog.Builder mAlertDialog;
	private Boolean mPictureList;
	private int mTextSize;
	private SharedPreferences preferences;
	
	/** Called when the activity is first created. */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListShownNoAnimation(true);	
		setHasOptionsMenu(true);		
		FetchData();
		
		getListView().setDivider(null);
		getListView().setDividerHeight(0);	
		
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String TextSizeArray [] =  getResources().getStringArray(R.array.settings_textsize);
		mTextSize = Integer.parseInt(TextSizeArray[preferences.getInt("textsize", 0)]);
		mPictureList = preferences.getBoolean("videopicturelist", true);
		
		TextView Category = (TextView) getActivity().findViewById(R.id.video_category);
		TextView Search = (TextView) getActivity().findViewById(R.id.video_search);		
		TextView PageText = (TextView) getActivity().findViewById(R.id.video_page_text);
		TextView CurrentPage = (TextView) getActivity().findViewById(R.id.video_current_page);
		TextView ByText = (TextView) getActivity().findViewById(R.id.video_by_text);
		TextView MaxPages = (TextView) getActivity().findViewById(R.id.video_no_of_pages);
		
		Category.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		Search.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		PageText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		CurrentPage.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);		
		ByText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		MaxPages.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.videomenu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}			

	public void setPictureList(Boolean Value) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
	    editor.putBoolean("videopicturelist", Value);              
	    editor.commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.video_submenu:
			return true;	
		case R.id.video_left:
			PreviousPage();
			return true;
		case R.id.video_refresh:
			RefreshPage();
			return true;
		case R.id.video_right:
			NextPage();
			return true;
		case R.id.video_picture_row:		    		
			setPictureList(true);              
			mPictureList = true;
			RefreshPage();
			return true;
		case R.id.video_text_row:
			setPictureList(false);
			mPictureList = false;
			RefreshPage();
			return true;				
		case R.id.video_search:
	        FragmentManager fm = getActivity().getSupportFragmentManager();
	        VideoSearchDialogFragment videoSearchDialog = new VideoSearchDialogFragment();
	        videoSearchDialog.show(fm, "fragment_edit_name");
			return true;			
		case R.id.video_go_to_page:			
			mAlertDialog = new AlertDialog.Builder(getActivity());

			mAlertDialog.setTitle("Gå till sidan...");
			mAlertDialog.setMessage("Skriv in sidnummer som du vill hoppa till (1 - " + mVideoData.getMaxPages() + ")");

			// Set an EditText view to get user input 
			final EditText PageNumber = new EditText(getActivity());
			mAlertDialog.setView(PageNumber);

			mAlertDialog.setPositiveButton("Hoppa", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Do something with value!
					if (Utilities.isInteger(PageNumber.getText().toString())) {
						mVideoData.setCurrentPage(Integer.parseInt(PageNumber.getText().toString()));
						FetchData();
					}					
				}
			});

			mAlertDialog.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
			  }
			});

			mAlertDialog.show();	
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

	private void FetchData() {
		if ((progDialog == null) || (!progDialog.isShowing())) {
			progDialog = ProgressDialog.show(getActivity(), "", "", true, true);
			progDialog.setContentView(R.layout.progresslayout);
			progDialog.setOnCancelListener(this);		
		}
		
		getVideo = new VideoListTask();
		getVideo.addVideoListListener(new VideoListListener() {
			public void Success(List<VideoItem> VideoItems) {
				mVideoData.setVideoItems(VideoItems);
				FillList();	
				if (mPictureList) {
					VideoImageDownloadTask getVideoImages = new VideoImageDownloadTask();
					getVideoImages.execute(mVideoData.getVideoItems(), mVideoAdapter);
				}
				progDialog.dismiss();
			}

			public void Fail() {				
				Toast mToast;
				mToast = Toast.makeText( getActivity()  , "" , Toast.LENGTH_LONG );
				mToast.setText("Inga objekt hittades");
				mToast.show();				
				
				mVideoData = new VideoData(1, 1, "", 0, null, 0);
				
				progDialog.dismiss();
			}			
		});
		getVideo.execute(mVideoData.getCurrentPage(), mVideoData.getCategory(), mVideoData.getSearch());	
	}

	private void FillList() {
		mVideoAdapter = new ListVideoAdapter(getActivity(), mVideoData.getVideoItems());
		setListAdapter(mVideoAdapter);

		getListView().setSelection(mVideoData.getListPosition());
		
		TextView CurrentPage = (TextView) getActivity().findViewById(R.id.video_current_page);
		CurrentPage.setText(Integer.toString(mVideoData.getCurrentPage()));		
		
		TextView MaxPages = (TextView) getActivity().findViewById(R.id.video_no_of_pages);
		MaxPages.setText(Integer.toString(mVideoData.getVideoItems().get(0).getNumberOfVideoPages()));
		mVideoData.setMaxPages(mVideoData.getVideoItems().get(0).getNumberOfVideoPages());			
		
		TextView Category = (TextView) getActivity().findViewById(R.id.video_category);
		Category.setText("Kategori: " + mVideoData.getVideoItems().get(0).getSelectedCategory());

		TextView Search = (TextView) getActivity().findViewById(R.id.video_search);
		
		String mSearch = mVideoData.getSearch();
		
		if (mSearch.length() > 0) {
			Search.setText(" (Sökord: " + mSearch + ")");			
		} else {
			Search.setText("");
		}
	}

	public void RefreshPage() {
		mVideoData.setListPosition(0);
		FetchData();
	}

	public void NextPage() {
		if (mVideoData.getCurrentPage() < mVideoData.getMaxPages()) {
			mVideoData.setListPosition(0);
			mVideoData.setCurrentPage(mVideoData.getCurrentPage() + 1);		
			FetchData();
		}
	}

	public void PreviousPage() {
    	if (mVideoData.getCurrentPage() > 1) {
    		mVideoData.setListPosition(0);
    		mVideoData.setCurrentPage(mVideoData.getCurrentPage() - 1);	
			FetchData();
    	}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String url = mVideoData.getVideoItems().get(position).getLink();		
		url = url.replaceAll("video", "video/i");	
		Uri uri = Uri.parse(url);
		Intent i = new Intent("android.intent.action.VIEW", uri);
		startActivity(i);				
	}

	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder;
		switch (id) {
		case DIALOG_FETCH_KOS_ERROR:
			builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Felmeddelande");
			builder.setMessage(
					"Det blev något fel vid hämtning av köp och sälj")
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									getActivity().finish();
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
	
	public class VideoSearchDialogFragment extends DialogFragment {	
		 public DialogFragment newInstace() {
			 DialogFragment dialogFragment = new VideoSearchDialogFragment();
			 return dialogFragment;
		 }		
		
		@Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        LayoutInflater inflater = getActivity().getLayoutInflater();
	        final View view = inflater.inflate(R.layout.videosearch, null);
	        builder.setView(view);
	        builder.setPositiveButton("Sök", new DialogInterface.OnClickListener() {	            	   
               @Override
               public void onClick(DialogInterface dialog, int id) {
            	   EditText mSearchString = (EditText) view.findViewById(R.id.video_dialog_search_text);
            	   Spinner mSearchCategory = (Spinner) view.findViewById(R.id.video_dialog_search_category);
            	   
            	   mVideoData.setSearch(mSearchString.getText().toString());
               	   int position = mSearchCategory.getSelectedItemPosition();
               	   String CategoryArrayPosition [] =  getResources().getStringArray(R.array.video_dialog_search_category_position);
               	   mVideoData.setCategory(Integer.parseInt(CategoryArrayPosition[position]));
               	   
               	   mVideoData.setCurrentPage(1);
               	   FetchData();	                	   
               }
	        });
	        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int id) {
	    		   VideoSearchDialogFragment.this.getDialog().cancel();
	        	}
	        });
	        Dialog dialog = builder.create();	        
	        return dialog;
	    }
	}	
}