package org.happymtb.unofficial.fragment;

import org.happymtb.unofficial.WebViewActivity;
import org.happymtb.unofficial.ZoomImageActivity;
import org.happymtb.unofficial.database.KoSItemDataSource;
import org.happymtb.unofficial.item.KoSListItem;
import org.happymtb.unofficial.item.KoSObjectItem;
import org.happymtb.unofficial.item.Person;
import org.happymtb.unofficial.listener.KoSObjectListener;
import org.happymtb.unofficial.task.KoSObjectImageTask;
import org.happymtb.unofficial.task.KoSObjectTask;
import org.happymtb.unofficial.KoSObjectActivity;
import org.happymtb.unofficial.R;
import org.happymtb.unofficial.ui.ScaleImageView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

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
    private TextView mYear;
    private TextView mPrice;

    private boolean mIsSaved = false;
    private boolean mIsSold = false;

    private int mCurrentImagePos = 0;

	ImageButton mPrevButton;
	ImageButton mNextButton;

	ImageButton mActionPhone;
	ImageButton mActionSms;
	ImageButton mActionEmail;
	ImageButton mActionPM;
	FrameLayout mObjectImageFrameLayout;
	ScaleImageView mObjectImageView;
	ScaleImageView mObjectImageView2;
    ScaleImageView mPrimaryImageView;
    ScaleImageView mSecondaryImageView;
	KoSObjectActivity mActivity;

	private KoSItemDataSource datasource;

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mActivity = (KoSObjectActivity) getActivity();

		String url = mActivity.getObjectLink();

		if (url.contains("&pm")) {
			openInBrowser(url, true);
		}
		setHasOptionsMenu(true);

		mTitle = (TextView) mActivity.findViewById(R.id.kos_object_title);
		mPerson = (TextView) mActivity.findViewById(R.id.kos_object_person);
		mPhone = (TextView) mActivity.findViewById(R.id.kos_object_phone);
		mCategory = (TextView) mActivity.findViewById(R.id.kos_object_category);
		mDate = (TextView) mActivity.findViewById(R.id.kos_object_date);
		mText = (TextView) mActivity.findViewById(R.id.kos_object_text);
		mPrice = (TextView) mActivity.findViewById(R.id.kos_object_price);
		mYear = (TextView) mActivity.findViewById(R.id.kos_object_year_model);
		mObjectImageFrameLayout = (FrameLayout) mActivity.findViewById(R.id.kos_object_image_frame);
		mObjectImageView = (ScaleImageView) mActivity.findViewById(R.id.kos_object_image);
		mObjectImageView2 = (ScaleImageView) mActivity.findViewById(R.id.kos_object_image2);
		mScrollView = mActivity.findViewById(R.id.kos_object_scroll);
		mProgressView = mActivity.findViewById(R.id.progress_container_id);

        mPrimaryImageView = mObjectImageView;
        mSecondaryImageView = mObjectImageView2;

		mPrevButton = (ImageButton) mActivity.findViewById(R.id.kos_object_prev_image);
		mNextButton = (ImageButton) mActivity.findViewById(R.id.kos_object_next_image);

		mActionPhone = (ImageButton) mActivity.findViewById(R.id.kos_action_phone);
		mActionSms = (ImageButton) mActivity.findViewById(R.id.kos_action_sms);
		mActionEmail = (ImageButton) mActivity.findViewById(R.id.kos_action_email);
		mActionPM = (ImageButton) mActivity.findViewById(R.id.kos_action_pm);

        mActionPhone.setOnClickListener(this);
        mActionSms.setOnClickListener(this);
        mActionEmail.setOnClickListener(this);
        mActionPM.setOnClickListener(this);

        mPrevButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);

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
                        if (!mKoSObjectItem.getTitle().equals(KoSObjectTask.ITEM_REMOVED)) {
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

		String categoryString;
		if (!TextUtils.isEmpty(mActivity.getObjectCategory())) {
			categoryString = mActivity.getObjectCategory() + ", " + mKoSObjectItem.getArea();
		} else {
			categoryString = mKoSObjectItem.getArea();
		}

		if (!TextUtils.isEmpty(mKoSObjectItem.getTown())) {
			categoryString =  categoryString + ", " + mKoSObjectItem.getTown();
		}

		mCategory.setText(categoryString);
		Person person = mKoSObjectItem.getPerson();
		mPerson.setText(person.getName());
		mDate.setText("Datum: " + mKoSObjectItem.getDate());

		if (person.getPhone().startsWith("+") || person.getPhone().startsWith("0")) {
			mPhone.setText("Telefon: " + person.getPhone());
			mActivity.findViewById(R.id.kos_action_phone_layout).setVisibility(View.VISIBLE);
			mActivity.findViewById(R.id.kos_action_sms_layout).setVisibility(View.VISIBLE);
		}

		if (!TextUtils.isEmpty(person.getEmailLink())) {
			mActivity.findViewById(R.id.kos_action_email_layout).setVisibility(View.VISIBLE);
		}

		if (!TextUtils.isEmpty(person.getPmLink())) {
			mActivity.findViewById(R.id.kos_action_pm_layout).setVisibility(View.VISIBLE);
		}

		if (!TextUtils.isEmpty(mKoSObjectItem.getImgLink())) {
			mObjectImageView.setImageResource(R.drawable.no_photo);

            //TODO Try out the translation/animation
//			mObjectImageView2.setImageResource(R.drawable.no_photo);
//			mObjectImageView2.setVisibility(View.VISIBLE);
			final String url = mKoSObjectItem.getImgLink();


            //TODO Use Picasso to download/cache
//            Picasso.with(mActivity).load(url).into(mObjectImageView);
            loadImage(mObjectImageView, url);

            if (mKoSObjectItem.getImgLinkList().size() > 1) {
                mPrevButton.setVisibility(View.VISIBLE);
                mNextButton.setVisibility(View.VISIBLE);
            }
		}

		if (mKoSObjectItem.getYearModel() > 0) {
			mYear.setText("Årsmodell: " + mKoSObjectItem.getYearModel());
			mActivity.findViewById(R.id.kos_object_year_model).setVisibility(View.VISIBLE);
		}

		mText.setText(Html.fromHtml(mKoSObjectItem.getText()));
        String price = mKoSObjectItem.getPrice();
        if (price.trim().equals("0 Kr")) {
            mPrice.setText("Prisuppgift saknas");
        } else {
            mPrice.setText("Pris: " + price);
        }
        mScrollView.setVisibility(View.VISIBLE);
	}

    private void loadImage(ImageView view, final String url) {
        view.setTag(url);
        new KoSObjectImageTask(view).execute();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String largeImageUrl = url.replace("normal", "large");
                Intent zoomImageIntent = new Intent(getActivity(), ZoomImageActivity.class);
                zoomImageIntent.putExtra("title", mKoSObjectItem.getTitle());
                zoomImageIntent.putExtra("url", largeImageUrl);
                startActivity(zoomImageIntent);
            }
        });
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
                openInBrowser(mActivity.getObjectLink(), false);
                return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

    private void addToDatabase() {
        long row = datasource.insertKosItem(getKoSListItem());

        if (row > 0) {
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
        String message = "Hej! Jag vill tipsa om en annons: " + mKoSObjectItem.getTitle() + " " + mActivity.getObjectLink();
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Dela annons..."));
    }

    private void openInBrowser(String url, boolean popBackStack) {
        Intent browserIntent = new Intent(getActivity(), WebViewActivity.class);
        browserIntent.putExtra("url", url);
        startActivity(browserIntent);
        if (popBackStack) {
            mActivity.finish();
        }
    }

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.kos_action_phone) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("tel", mKoSObjectItem.getPerson().getPhone(), null));
            startActivity(intent);
		} else if (v.getId() == R.id.kos_action_sms) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", mKoSObjectItem.getPerson().getPhone(), null));
            startActivity(intent);
        } else if (v.getId() == R.id.kos_action_email) {
            openInBrowser(mKoSObjectItem.getPerson().getEmailLink(), false);
		} else if (v.getId() == R.id.kos_action_pm) {
            openInBrowser(mKoSObjectItem.getPerson().getPmLink(), false);
		} else if (v.getId() == R.id.kos_object_prev_image) {
            int pos = getPrevImagePos();
            if (pos >= 0) {
                String url = mKoSObjectItem.getImgLinkList().get(pos);
                loadImage(mSecondaryImageView, url);
                mObjectImageView.setVisibility(View.VISIBLE);
                mObjectImageView2.setVisibility(View.VISIBLE);

                int width = getScreenWidth();
                mPrimaryImageView.animate().translationX(0).setDuration(0).start();
                mPrimaryImageView.animate().translationXBy(width).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();

                mSecondaryImageView.animate().translationX(-width).setDuration(0).start();
                mSecondaryImageView.animate().translationXBy(width).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();

                ScaleImageView temp = mPrimaryImageView;
                mPrimaryImageView = mSecondaryImageView;
                mSecondaryImageView = temp;

                mCurrentImagePos = pos;
            }
		} else if (v.getId() == R.id.kos_object_next_image) {
            int pos = getNextImagePos();
            if (pos >= 0) {
                String url = mKoSObjectItem.getImgLinkList().get(pos);
                loadImage(mSecondaryImageView, url);
                mObjectImageView.setVisibility(View.VISIBLE);
                mObjectImageView2.setVisibility(View.VISIBLE);

                int width = getScreenWidth();
                mPrimaryImageView.animate().translationX(0).setDuration(0).start();
                mPrimaryImageView.animate().translationXBy(-width).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();

                mSecondaryImageView.animate().translationX(width).setDuration(0).start();
                mSecondaryImageView.animate().translationXBy(-width).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();

                ScaleImageView temp = mPrimaryImageView;
                mPrimaryImageView = mSecondaryImageView;
                mSecondaryImageView = temp;

                mCurrentImagePos = pos;
            }
		}
	}

    private int getScreenWidth() {
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }

    private int getPrevImagePos() {
        if (mKoSObjectItem == null || mKoSObjectItem.getImgLinkList() == null
                || mKoSObjectItem.getImgLinkList().size() <= 1) {
            return -1;
        }
        List list = mKoSObjectItem.getImgLinkList();
        return (mCurrentImagePos - 1 + list.size()) % list.size();
    }

    private int getNextImagePos() {
        if (mKoSObjectItem == null || mKoSObjectItem.getImgLinkList() == null
                || mKoSObjectItem.getImgLinkList().size() <= 1) {
            return -1;
        }
        List list = mKoSObjectItem.getImgLinkList();

        return (mCurrentImagePos + 1) % list.size();
    }
}