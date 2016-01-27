package org.happymtb.unofficial.fragment;

import org.happymtb.unofficial.WebViewActivity;
import org.happymtb.unofficial.ZoomImageActivity;
import org.happymtb.unofficial.database.KoSItemDataSource;
import org.happymtb.unofficial.item.KoSListItem;
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
    private TextView mTitle;
    private TextView mPerson;
    private TextView mPhone;
    private TextView mCategory;
    private TextView mDate;
    private TextView mText;
    private TextView mPrice;

    private boolean mIsSaved = false;
    private boolean mIsSold = false;

	ImageButton mActionPhone;
	ImageButton mActionSms;
	ImageButton mActionEmail;
	ScaleImageView mObjectImageView;
	KoSObjectActivity mActivity;

	private KoSItemDataSource datasource;
	
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

			if (!TextUtils.isEmpty(mActivity.getObjectTitle())) {
				mActivity.getSupportActionBar().setTitle(mActivity.getObjectType());
                mActivity.getSupportActionBar().setDisplayShowTitleEnabled(true);

                //TODO Delete or uncomment?
//				mTitle.setText(mActivity.getObjectTitle());
//				mCategory.setText(mActivity.getObjectCategory() + ", " + mActivity.getObjectArea());
//				mDate.setText("Datum: " + mActivity.getObjectDate());
//                mPrice.setText("Pris: " + mActivity.getObjectPrice());
//                mScrollView.setVisibility(View.VISIBLE);

				//TODO Set old image if already cached
			}
		}

        datasource = new KoSItemDataSource(mActivity);
        datasource.open();

        mIsSaved = datasource.isItemInDatabase(mActivity.getObjectId());
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

        menu.findItem(R.id.kos_object_favorite).setVisible(!mIsSaved && !mIsSold);
        menu.findItem(R.id.kos_object_unfavorite).setVisible(mIsSaved);
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
                        if (!mKoSObjectItem.getDate().equals("1970-01-01 01:00")) {
                            fillList();
                            //TODO update in database
                            if (mIsSaved) {
                                updateInDatabase();
                            }
                        } else {
                            mScrollView.setVisibility(View.INVISIBLE);
                            mActivity.findViewById(R.id.no_content).setVisibility(View.VISIBLE);

                            mIsSold = true;
                            if (mIsSaved) {
                                datasource.setItemSold(mActivity.getObjectId(), true);
                                mActivity.setResult(SavedListFragment.RESULT_MODIFIED, null);
                            }
                            mActivity.invalidateOptionsMenu();
                        }
                    }
				} else {
                    // Something went wrong
                }
                mProgressView.setVisibility(View.INVISIBLE);
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
		if (!TextUtils.isEmpty(mActivity.getObjectCategory())) {
			mCategory.setText(mActivity.getObjectCategory() + ", " + mKoSObjectItem.getArea());
		} else {
			mCategory.setText(mKoSObjectItem.getArea());
		}
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
        String price = mKoSObjectItem.getPrice();
        if (price.trim().equals("Prisuppgift saknas.")) {
            mPrice.setText("Prisuppgift saknas");
        } else {
            mPrice.setText("Pris: " + price);
        }
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
            case R.id.kos_object_favorite:
                addToDatabase();
                mActivity.invalidateOptionsMenu();
                return true;
            case R.id.kos_object_unfavorite:
                removeFromDatabase();
                mActivity.invalidateOptionsMenu();
                return true;
            case R.id.kos_object_share:
                shareObject();
                return true;
            case R.id.kos_object_browser:
                mIsSold = true;
                mActivity.invalidateOptionsMenu();
//                if (mIsSaved) {
//                    if (datasource.setItemSold(mActivity.getObjectId(), true) != -1) {
//                        Toast.makeText(mActivity, "Såld!", Toast.LENGTH_SHORT).show();
//                    }
//                }
                openInBrowser(false, false);
                return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

    private void addToDatabase() {
        long row = datasource.insertKosItem(getKoSListItem());

        if (row > 0) {
//            Toast.makeText(mActivity, "Annonsen sparad", Toast.LENGTH_SHORT).show();
            mIsSaved = true;
        } else {
            Toast.makeText(mActivity, "Annonsen kunde inte sparas", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateInDatabase() {
        long row = datasource.updateKosItem(getKoSListItem());

        if (row > 0) {
            mActivity.setResult(SavedListFragment.RESULT_MODIFIED, null);
        } else {
            Toast.makeText(mActivity, "Annonsen kunde inte uppdateras", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeFromDatabase() {
        mIsSaved = false;
        long id = Long.parseLong(mActivity.getObjectLink().split("id=")[1]);
        boolean success = datasource.deleteItem(id);
		if (success) {
			mActivity.setResult(SavedListFragment.RESULT_MODIFIED, null);
		}
    }

    private KoSListItem getKoSListItem() {
        if (mKoSObjectItem != null) {
            return new KoSListItem(mActivity.getObjectId(), mKoSObjectItem.getDate(), mKoSObjectItem.getType(), mKoSObjectItem.getTitle(),
                    mKoSObjectItem.getArea(), mActivity.getObjectLink(), mKoSObjectItem.getImgLink(), mActivity.getObjectCategory(),
                    mKoSObjectItem.getPrice(), 0, null, null);
        } else {
            return null;
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