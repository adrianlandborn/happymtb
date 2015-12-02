package org.happymtb.unofficial.fragment;

import org.happymtb.unofficial.WebViewActivity;
import org.happymtb.unofficial.ZoomImageActivity;
import org.happymtb.unofficial.item.KoSObjectItem;
import org.happymtb.unofficial.listener.KoSObjectListener;
import org.happymtb.unofficial.task.KoSObjectImageTask;
import org.happymtb.unofficial.task.KoSObjectTask;
import org.happymtb.unofficial.KoSObjectActivity;
import org.happymtb.unofficial.R;
import org.happymtb.unofficial.ui.ScaleImageView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class KoSObjectFragment extends Fragment implements DialogInterface.OnCancelListener, View.OnClickListener {
	private final static int DIALOG_FETCH_KOS_ERROR = 0;
	private final static String DATA = "data";

	private KoSObjectTask mKoSObjectTask;
	private KoSObjectItem mKoSObjectItem;
	private View mScrollView;
	private View mProgressView;
	TextView mTitle;
	TextView mPerson;		
	TextView mPhone;
	TextView mCategory;
	TextView mDate;
	TextView mText;
	TextView mPrice;

	ImageButton mActionPhone;
	ImageButton mActionSms;
	ImageButton mActionEmail;
	ScaleImageView mObjectImageView;
	KoSObjectActivity mActivity;
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mActivity = (KoSObjectActivity) getActivity();

		String url = mActivity.getObjectLink();

		if (url.contains("&pm")) {
			openInBrowser(false, true);
		}
		setHasOptionsMenu(true);

		mTitle = (TextView) mActivity.findViewById(R.id.kos_object_title);
		mPerson = (TextView) mActivity.findViewById(R.id.kos_object_person);
		mPhone = (TextView) mActivity.findViewById(R.id.kos_object_phone);
		mCategory = (TextView) mActivity.findViewById(R.id.kos_object_category);
		mDate = (TextView) mActivity.findViewById(R.id.kos_object_date);
		mText = (TextView) mActivity.findViewById(R.id.kos_object_text);
		mPrice = (TextView) mActivity.findViewById(R.id.kos_object_price);
		mObjectImageView = (ScaleImageView) mActivity.findViewById(R.id.kos_object_image);
		mScrollView = mActivity.findViewById(R.id.kos_object_scroll);
		mProgressView = mActivity.findViewById(R.id.progress_container_id);

		mActionPhone = (ImageButton) mActivity.findViewById(R.id.kos_action_phone);
		mActionSms = (ImageButton) mActivity.findViewById(R.id.kos_action_sms);
		mActionEmail = (ImageButton) mActivity.findViewById(R.id.kos_action_email);

        mActionPhone.setOnClickListener(this);
        mActionSms.setOnClickListener(this);
        mActionEmail.setOnClickListener(this);

		if (savedInstanceState != null) {
			mKoSObjectItem = (KoSObjectItem) savedInstanceState.getSerializable(DATA);

            fillList();
		} else {
			fetchKoSObject(url);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mKoSObjectItem != null) {
			outState.putSerializable(DATA, mKoSObjectItem);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.kos_object_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}		
	
	private void fetchKoSObject(String objectLink) {
		mProgressView.setVisibility(View.VISIBLE);

		mKoSObjectTask = new KoSObjectTask();
		mKoSObjectTask.addKoSObjectListener(new KoSObjectListener() {
			public void success(KoSObjectItem koSObjectItem) {
				mKoSObjectItem = koSObjectItem;
				if (getActivity() != null && !getActivity().isFinishing()) {
					if (mKoSObjectItem != null) {
						fillList();
					}
					mProgressView.setVisibility(View.INVISIBLE);
				}
			}

			public void fail() {
				if (getActivity() != null && !getActivity().isFinishing()) {
					Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
					mProgressView.setVisibility(View.INVISIBLE);
				}
			}
		});

		mKoSObjectTask.execute(objectLink);
	}
	
	private void fillList() {
		mActivity.getSupportActionBar().setTitle(mKoSObjectItem.getType());
		mActivity.getSupportActionBar().setDisplayShowTitleEnabled(true);
		mTitle.setText(mKoSObjectItem.getTitle());
		mCategory.setText( mActivity.getCategory() + ", " + mKoSObjectItem.getArea());
		mPerson.setText(mKoSObjectItem.getPerson());
		mPhone.setText("Telefon: " + mKoSObjectItem.getPhone());
		mDate.setText("Datum: " + mKoSObjectItem.getDate());

		if (mKoSObjectItem.getPhone().startsWith("+") || mKoSObjectItem.getPhone().startsWith("0")) {
			mActivity.findViewById(R.id.kos_action_phone_layout).setVisibility(View.VISIBLE);
			mActivity.findViewById(R.id.kos_action_sms_layout).setVisibility(View.VISIBLE);
		}

		if (!TextUtils.isEmpty(mKoSObjectItem.getImgLink()))
		{
			mObjectImageView.setImageResource(R.drawable.no_photo);
			final String url = mKoSObjectItem.getImgLink();

			mObjectImageView.setTag(url);

            //TODO Use Picasso to download/cache
//            Picasso.with(mActivity).load(url).into(mObjectImageView);
			new KoSObjectImageTask(mObjectImageView).execute();
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
        mScrollView.setVisibility(View.VISIBLE);
	}	
	
	@Override
	public void onCancel(DialogInterface dialog) {
		if (mKoSObjectTask != null) {
			mKoSObjectTask.cancel(true);
		}		
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
            case R.id.kos_object_share:
                shareObject();
                return true;
            case R.id.kos_object_browser:
                openInBrowser(false, false);
                return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

    private void shareObject() {
        String message = "Hej! Jag vill tipsa om en annons: " + mActivity.getObjectLink();
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Dela annons..."));
    }

    private void openInBrowser(boolean isMessage, boolean popBackStack) {
        String url = mActivity.getObjectLink();
        Intent browserIntent = new Intent(getActivity(), WebViewActivity.class);
        if (isMessage) {
            browserIntent.putExtra("url", url + "&pm");
        } else {
            browserIntent.putExtra("url", url);
        }
        startActivity(browserIntent);
        if (popBackStack) {
            mActivity.finish();
        }
    }

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.kos_action_phone) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("tel", mKoSObjectItem.getPhone(), null));
            startActivity(intent);
		} else if (v.getId() == R.id.kos_action_sms) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", mKoSObjectItem.getPhone(), null));
            startActivity(intent);
        } else if (v.getId() == R.id.kos_action_email) {
            openInBrowser(true, false);
		}
	}
}