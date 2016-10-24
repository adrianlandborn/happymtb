package org.happymtb.unofficial.fragment;

import org.happymtb.unofficial.WebViewActivity;
import org.happymtb.unofficial.adapter.ViewPagerAdapter;
import org.happymtb.unofficial.analytics.GaConstants;
import org.happymtb.unofficial.database.KoSItemDataSource;
import org.happymtb.unofficial.item.KoSListItem;
import org.happymtb.unofficial.item.KoSObjectItem;
import org.happymtb.unofficial.item.Person;
import org.happymtb.unofficial.listener.KoSObjectListener;
import org.happymtb.unofficial.task.KoSObjectTask;
import org.happymtb.unofficial.KoSObjectActivity;
import org.happymtb.unofficial.R;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.viewpagerindicator.CirclePageIndicator;

public class KoSObjectFragment extends Fragment implements DialogInterface.OnCancelListener, View.OnClickListener {
	private final static int DIALOG_FETCH_KOS_ERROR = 0;
	private final static String DATA = "data";

	private KoSObjectTask mKoSObjectTask;
	private KoSObjectItem mKoSObjectItem;
	private View mScrollView;
	private View mProgressView;
	private View mNoNetworkView;
	private Button mReloadButton;
    private TextView mTitle;
    private Button mPerson;
    private TextView mPhone;
    private TextView mCategory;
    private TextView mDate;
    private TextView mText;
    private TextView mYear;
    private TextView mPrice;

    private boolean mIsSaved = false;
    private boolean mIsSold = false;

    private ImageButton mActionPhone;
    private ImageButton mActionSms;
    private ImageButton mActionEmail;
    private ImageButton mActionPM;

	private KoSObjectActivity mActivity;
    private String mUrl;
	private KoSItemDataSource datasource;

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mActivity = (KoSObjectActivity) getActivity();

		mUrl = mActivity.getObjectLink();
		if (mUrl.contains("&pm")) {
			openInBrowser(mUrl, true);
		}
		setHasOptionsMenu(true);

		mTitle = (TextView) mActivity.findViewById(R.id.kos_object_title);
		mPerson = (Button) mActivity.findViewById(R.id.kos_object_person);
		mPhone = (TextView) mActivity.findViewById(R.id.kos_object_phone);
		mCategory = (TextView) mActivity.findViewById(R.id.kos_object_category);
		mDate = (TextView) mActivity.findViewById(R.id.kos_object_date);
		mText = (TextView) mActivity.findViewById(R.id.kos_object_text);
		mPrice = (TextView) mActivity.findViewById(R.id.kos_object_price);
		mYear = (TextView) mActivity.findViewById(R.id.kos_object_year_model);
		mScrollView = mActivity.findViewById(R.id.kos_object_scroll);
		mProgressView = mActivity.findViewById(R.id.progress_container_id);
		mNoNetworkView = mActivity.findViewById(R.id.no_network_layout);
		mReloadButton = (Button)mActivity.findViewById(R.id.reload_button);

		mActionPhone = (ImageButton) mActivity.findViewById(R.id.kos_action_phone);
		mActionSms = (ImageButton) mActivity.findViewById(R.id.kos_action_sms);
		mActionEmail = (ImageButton) mActivity.findViewById(R.id.kos_action_email);
		mActionPM = (ImageButton) mActivity.findViewById(R.id.kos_action_pm);

        mPerson.setOnClickListener(this);
        mActionPhone.setOnClickListener(this);
        mActionSms.setOnClickListener(this);
        mActionEmail.setOnClickListener(this);
        mActionPM.setOnClickListener(this);

        mReloadButton.setOnClickListener(this);

        if (savedInstanceState != null) {
			mKoSObjectItem = (KoSObjectItem) savedInstanceState.getSerializable(DATA);

            fillList();
		} else {
			fetchKoSObject(mUrl);

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
                    } else {
                        // No data traffic etc.
                        mNoNetworkView.setVisibility(View.VISIBLE);
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
            ViewPager viewPager = (ViewPager)mActivity.findViewById(R.id.view_pager);;
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(mActivity, mKoSObjectItem.getTitle(), mKoSObjectItem.getImgLinkList());
            viewPager.setAdapter(viewPagerAdapter);

            if (mKoSObjectItem.getImgLinkList().size() > 1) {
                CirclePageIndicator pageIndicator = (CirclePageIndicator)mActivity.findViewById(R.id.view_pager_indicator);
                pageIndicator.setViewPager(viewPager);
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
                sendGaEvent(GaConstants.Actions.FAVORITE, GaConstants.Labels.ADD);
                return true;
            case R.id.kos_object_unfavorite:
                removeFromDatabase();
                mActivity.invalidateOptionsMenu();
                sendGaEvent(GaConstants.Actions.FAVORITE, GaConstants.Labels.REMOVE);
                return true;
            case R.id.kos_object_share:
                shareObject();
                sendGaEvent(GaConstants.Actions.SHARE, GaConstants.Labels.EMPTY);
                return true;
            case R.id.kos_object_browser:
                mIsSold = true;
//                mActivity.invalidateOptionsMenu();
                sendGaEvent(GaConstants.Actions.OPEN_IN_BROWSER, GaConstants.Labels.EMPTY);
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

    private void sendGaEvent(String action, String label) {
        mActivity.getTracker().send(new HitBuilders.EventBuilder()
                .setCategory(GaConstants.Categories.KOS_OBJECT)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    private void addToDatabase() {
        if (datasource.insertKosItem(getKoSListItem()) > 0) {
            mIsSaved = true;
        } else {
            Toast.makeText(mActivity, "Annonsen kunde inte sparas", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateInDatabase() {
        if (datasource.updateKosItem(getKoSListItem()) > 0) {
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
                    mKoSObjectItem.getPrice(), 0);
        } else {
            return null;
        }
    }

    private void shareObject() {
        if (mKoSObjectItem != null) {
            String message = "Hej! Jag vill tipsa om en annons: " + mKoSObjectItem.getTitle() + " " + mActivity.getObjectLink();
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, "Dela annons..."));
        }
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
        if (v.getId() == R.id.reload_button) {
            mNoNetworkView.setVisibility(View.INVISIBLE);
            fetchKoSObject(mUrl);
        }

        if (mKoSObjectItem != null) {
            if (v.getId() == R.id.kos_object_person) {
                openInBrowser(mKoSObjectItem.getPerson().getIdLink(), false);
            } else if (v.getId() == R.id.kos_action_phone) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("tel", mKoSObjectItem.getPerson().getPhone(), null));
                startActivity(intent);
            } else if (v.getId() == R.id.kos_action_sms) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", mKoSObjectItem.getPerson().getPhone(), null));
                startActivity(intent);
            } else if (v.getId() == R.id.kos_action_email) {
                openInBrowser(mKoSObjectItem.getPerson().getEmailLink(), false);
            } else if (v.getId() == R.id.kos_action_pm) {
                openInBrowser(mKoSObjectItem.getPerson().getPmLink(), false);
            }
        }
	}
}