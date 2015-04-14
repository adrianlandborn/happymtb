package org.happymtb.unofficial.fragment;

import java.io.IOException;

import org.happymtb.unofficial.item.KoSObjectItem;
import org.happymtb.unofficial.listener.KoSObjectListener;
import org.happymtb.unofficial.task.KoSObjectImageTask;
import org.happymtb.unofficial.task.KoSObjectTask;
import org.happymtb.unofficial.KoSObjectActivity;
import org.happymtb.unofficial.R;
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
	private ProgressDialog mProgressDialog = null;
	private KoSObjectTask mKoSObjectTask;
	private KoSObjectItem mKoSObjectItem;
	private SharedPreferences mPreferences;
    View mObjectView;
	TextView mTitle;
	TextView mPerson;		
	TextView mDate;
	TextView mText;
	TextView mPrice;
	LinearLayout mBackgroundColor;
	ImageView mObjectImageView;
	KoSObjectActivity mKoSObjectActivity;	
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mKoSObjectActivity = (KoSObjectActivity) getActivity();
		
		setHasOptionsMenu(true);
		
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mKoSObjectActivity);
		String TextSizeArray [] =  getResources().getStringArray(R.array.settings_textsize);
		int mTextSize = Integer.parseInt(TextSizeArray[mPreferences.getInt("textsize", 0)]);

        mObjectView = mKoSObjectActivity.findViewById(R.id.kos_object);
		mTitle = (TextView) mKoSObjectActivity.findViewById(R.id.kos_object_title);
		mPerson = (TextView) mKoSObjectActivity.findViewById(R.id.kos_object_person);		
		mDate = (TextView) mKoSObjectActivity.findViewById(R.id.kos_object_date);
		mText = (TextView) mKoSObjectActivity.findViewById(R.id.kos_object_text);
		mPrice = (TextView) mKoSObjectActivity.findViewById(R.id.kos_object_price);
		mBackgroundColor = (LinearLayout) mKoSObjectActivity.findViewById(R.id.kos_object_color);
		mObjectImageView = (ImageView) mKoSObjectActivity.findViewById(R.id.kos_object_image);

		mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize + 2);
		mPerson.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
		mDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);		
		mText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
		mPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);		
		
		FetchKoSObject(mKoSObjectActivity.GetObjectLink());
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.kos_object_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}		
	
	private void FetchKoSObject(String objectLink) {
		if ((mProgressDialog == null) || (!mProgressDialog.isShowing())) {
			mProgressDialog = ProgressDialog.show(mKoSObjectActivity, "", "", true, true);
			mProgressDialog.setContentView(R.layout.progress_layout);
			mProgressDialog.setOnCancelListener(this);
		}
		
		mKoSObjectTask = new KoSObjectTask();
		mKoSObjectTask.addKoSObjectListener(new KoSObjectListener() {
            public void success(KoSObjectItem koSObjectItem) {
                if (getActivity() != null && mKoSObjectItem != null) {
                    fillList();
                    mProgressDialog.dismiss();
                }
            }

            public void fail() {
                if (getActivity() != null) {
                    mProgressDialog.dismiss();
                }
            }
        });

		mKoSObjectTask.execute(objectLink);

	}	
	
	private void fillList() {
		mTitle.setText(mKoSObjectItem.getArea() + " - " + mKoSObjectItem.getType() + " - " + mKoSObjectItem.getTitle());		
		mPerson.setText("Annonsör: " + mKoSObjectItem.getPerson() + "(Telefon: " + mKoSObjectItem.getPhone() + ")");
		mDate.setText("Datum: " + mKoSObjectItem.getDate());		
	
		if (mKoSObjectItem.getImgLink() != "")
		{				
			String URL = mKoSObjectItem.getImgLink();

			mObjectImageView.setTag(URL);
			new KoSObjectImageTask().execute(mObjectImageView);
		}
		
		mText.setText(Html.fromHtml(mKoSObjectItem.getText()));		
		mPrice.setText("Pris: " + mKoSObjectItem.getPrice());
		
		if (mKoSObjectItem.getType().contains("Säljes")){
			int identifier = getResources().getIdentifier("rowshape_green", "drawable","org.pebeijer.happymtb");
			mBackgroundColor.setBackgroundResource(identifier);
		} else {
			int identifier = getResources().getIdentifier("rowshape_red", "drawable","org.pebeijer.happymtb");
			mBackgroundColor.setBackgroundResource(identifier);
		}

        mObjectView.setVisibility(View.VISIBLE);
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
					.setPositiveButton("Ok",
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
		// TODO Auto-generated method stub
		if (mKoSObjectTask != null) {
			mKoSObjectTask.cancel(true);
		}		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
			case R.id.kos_object_mail:
				Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(mKoSObjectActivity.GetObjectLink() + "&pm"));
				startActivity(browserIntent);
				return true;
            case R.id.kos_object_share:
                shareObject();
                return true;
            case R.id.kos_object_browser:
                openInBrowser();
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

    private void openInBrowser() {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mKoSObjectActivity.GetObjectLink()));
        startActivity(browserIntent);

    }
}