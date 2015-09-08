package org.happymtb.unofficial.fragment;

import java.io.IOException;

import org.happymtb.unofficial.WebViewActivity;
import org.happymtb.unofficial.ZoomImageActivity;
import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.KoSObjectItem;
import org.happymtb.unofficial.listener.KoSObjectListener;
import org.happymtb.unofficial.task.KoSObjectImageTask;
import org.happymtb.unofficial.task.KoSObjectTask;
import org.happymtb.unofficial.KoSObjectActivity;
import org.happymtb.unofficial.R;
import org.happymtb.unofficial.ui.ScaleImageView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class KoSObjectFragment extends Fragment implements DialogInterface.OnCancelListener {
	private final static int DIALOG_FETCH_KOS_ERROR = 0;
//	private ProgressDialog mProgressDialog = null;
	private KoSObjectTask mKoSObjectTask;
	private KoSObjectItem mKoSObjectItem;
	private View mProgressView;
//    View mObjectView;
	TextView mTitle;
	TextView mPerson;		
//	TextView mCategory;
	TextView mDate;
	TextView mText;
	TextView mPrice;
	LinearLayout mBackgroundColor;
	ScaleImageView mObjectImageView;
	KoSObjectActivity mKoSObjectActivity;	
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mKoSObjectActivity = (KoSObjectActivity) getActivity();
		
		setHasOptionsMenu(true);

//        mObjectView = mKoSObjectActivity.findViewById(R.id.kos_object);
		mTitle = (TextView) mKoSObjectActivity.findViewById(R.id.kos_object_title);
		mPerson = (TextView) mKoSObjectActivity.findViewById(R.id.kos_object_person);
//		mCategory = (TextView) mKoSObjectActivity.findViewById(R.id.kos_object_category);
		mDate = (TextView) mKoSObjectActivity.findViewById(R.id.kos_object_date);
		mText = (TextView) mKoSObjectActivity.findViewById(R.id.kos_object_text);
		mPrice = (TextView) mKoSObjectActivity.findViewById(R.id.kos_object_price);
		mBackgroundColor = (LinearLayout) mKoSObjectActivity.findViewById(R.id.kos_object_color);
		mObjectImageView = (ScaleImageView) mKoSObjectActivity.findViewById(R.id.kos_object_image);
		mProgressView = mKoSObjectActivity.findViewById(R.id.progress_container_id);

		FetchKoSObject(mKoSObjectActivity.GetObjectLink());
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.kos_object_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}		
	
	private void FetchKoSObject(String objectLink) {
		mProgressView.setVisibility(View.VISIBLE);
//		if ((mProgressDialog == null) || (!mProgressDialog.isShowing())) {
//			mProgressDialog = ProgressDialog.show(mKoSObjectActivity, "", "", true, true);
//			mProgressDialog.setContentView(R.layout.progress_layout);
//			mProgressDialog.setOnCancelListener(this);
//		}
		
		mKoSObjectTask = new KoSObjectTask();
		mKoSObjectTask.addKoSObjectListener(new KoSObjectListener() {
			public void success(KoSObjectItem koSObjectItem) {
				mKoSObjectItem = koSObjectItem;
				if (getActivity() != null) {
					if (mKoSObjectItem != null) {
						fillList();
					}
					mProgressView.setVisibility(View.INVISIBLE);
//					mProgressDialog.dismiss();
				}
			}

			public void fail() {
				if (getActivity() != null) {
					mProgressView.setVisibility(View.INVISIBLE);
//					mProgressDialog.dismiss();
				}
			}
		});

		mKoSObjectTask.execute(objectLink);

	}	
	
	private void fillList() {
		mTitle.setText(mKoSObjectItem.getArea() + " - " + mKoSObjectItem.getType() + " - " + mKoSObjectItem.getTitle());		
		mPerson.setText("Annonsör: " + mKoSObjectItem.getPerson() + "(Telefon: " + mKoSObjectItem.getPhone() + ")");
		mDate.setText("Datum: " + mKoSObjectItem.getDate());

		System.out.println("scaleimage: " + mKoSObjectItem.getImgLink());
		if (!TextUtils.isEmpty(mKoSObjectItem.getImgLink()))
		{
			mObjectImageView.setImageResource(R.drawable.no_photo);
			final String url = mKoSObjectItem.getImgLink();

			mObjectImageView.setTag(url);
			new KoSObjectImageTask().execute(mObjectImageView);
			mObjectImageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent zoomImageIntent = new Intent(getActivity(), ZoomImageActivity.class);
					zoomImageIntent.putExtra("title", mKoSObjectItem.getTitle());
					zoomImageIntent.putExtra("url", url);
					startActivity(zoomImageIntent);
				}
			});
		}
		
		mText.setText(Html.fromHtml(mKoSObjectItem.getText()));		
		mPrice.setText("Pris: " + mKoSObjectItem.getPrice());
		
		if (mKoSObjectItem.getType().contains("Säljes")){
			mBackgroundColor.setBackgroundResource(R.drawable.rowshape_green2);
		} else {
			mBackgroundColor.setBackgroundResource(R.drawable.rowshape_red2);
		}

//        mObjectView.setVisibility(View.VISIBLE);
	}	
	
	protected Dialog onCreateDialog(int id) {		
		Dialog dialog = null;
		AlertDialog.Builder builder;
		switch (id) {
		case DIALOG_FETCH_KOS_ERROR:
			builder = new AlertDialog.Builder(mKoSObjectActivity);
			builder.setTitle("Felmeddelande");
			builder.setMessage(
					"Det blev något fel vid hämtning av köp och sälj")
					.setPositiveButton(mKoSObjectActivity.getString(R.string.OK),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									mKoSObjectActivity.finish();
								}
							});
			dialog = builder.create();
			break;
		}
		return dialog;
	}	
	
	@Override
	public void onCancel(DialogInterface dialog) {
		if (mKoSObjectTask != null) {
			mKoSObjectTask.cancel(true);
		}		
	}

    @Override
    public void onDestroy() {
		super.onDestroy();
//        mProgressDialog.dismiss();
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
			case R.id.kos_object_mail:
				openInBrowser(true);
				return true;
            case R.id.kos_object_share:
                shareObject();
                return true;
            case R.id.kos_object_browser:
                openInBrowser(false);
                return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

    private void shareObject() {
        String message = "Hej! Jag vill tipsa om en annons: " + mKoSObjectActivity.GetObjectLink();
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Dela annons..."));
    }

    private void openInBrowser(boolean isMessage) {

        Intent browserIntent = new Intent(getActivity(), WebViewActivity.class);
        if (isMessage) {
            browserIntent.putExtra("url", mKoSObjectActivity.GetObjectLink() + "&pm");
        } else {
            browserIntent.putExtra("url", mKoSObjectActivity.GetObjectLink());
        }
        startActivity(browserIntent);

    }
}