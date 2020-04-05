package org.happymtb.unofficial.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.viewpagerindicator.CirclePageIndicator;

import org.happymtb.unofficial.KoSObjectActivity;
import org.happymtb.unofficial.R;
import org.happymtb.unofficial.WebViewActivity;
import org.happymtb.unofficial.adapter.ViewPagerAdapter;
import org.happymtb.unofficial.database.KoSItemDataSource;
import org.happymtb.unofficial.item.KoSObjectItem;
import org.happymtb.unofficial.item.Person;
import org.happymtb.unofficial.volley.KoSObjectRequest;
import org.happymtb.unofficial.volley.MyRequestQueue;

public class KoSObjectFragment extends Fragment implements View.OnClickListener {
	private final static String DATA = "data";

    ImageView mTransitionImageView;

	private KoSObjectRequest mRequest;
	KoSObjectItem mKoSObjectItem;
	View mScrollView;
	View mProgressView;
	View mNoNetworkView;
    private TextView mTitle;
    private Button mPerson;
    private Button mAllAds;
    private TextView mPhone;
    private TextView mCategory;
    private TextView mDate;
    private TextView mText;
    private TextView mYear;
    private TextView mPrice;

    boolean mIsSaved = false;
    boolean mIsSold = false;

    KoSObjectActivity mActivity;
    private String mUrl;
	KoSItemDataSource datasource;

    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mActivity = (KoSObjectActivity) getActivity();

		mUrl = mActivity.getObjectLink();
		if (mUrl.contains("&pm")) {
			openInBrowser(mUrl, true);
		}
		setHasOptionsMenu(true);

        mTransitionImageView = mActivity.findViewById(R.id.image_transition);

		mTitle = mActivity.findViewById(R.id.kos_object_title);
		mPerson = mActivity.findViewById(R.id.kos_object_person);
		mAllAds = mActivity.findViewById(R.id.kos_all_ads);
		mPhone = mActivity.findViewById(R.id.kos_object_phone);
		mCategory = mActivity.findViewById(R.id.kos_object_category);
		mDate = mActivity.findViewById(R.id.kos_object_date);
		mText = mActivity.findViewById(R.id.kos_object_text);
		mPrice = mActivity.findViewById(R.id.kos_object_price);
		mYear = mActivity.findViewById(R.id.kos_object_year_model);
		mScrollView = mActivity.findViewById(R.id.kos_object_scroll);
		mProgressView = mActivity.findViewById(R.id.progress_container_id);
		mNoNetworkView = mActivity.findViewById(R.id.no_network_layout);

        ImageButton actionPhone = mActivity.findViewById(R.id.kos_action_phone);
        ImageButton actionSms = mActivity.findViewById(R.id.kos_action_sms);
        ImageButton actionEmail = mActivity.findViewById(R.id.kos_action_email);
        ImageButton actionPM = mActivity.findViewById(R.id.kos_action_pm);
        Button reloadButton = mActivity.findViewById(R.id.reload_button);

        mPerson.setOnClickListener(this);
        mAllAds.setOnClickListener(this);
        actionPhone.setOnClickListener(this);
        actionSms.setOnClickListener(this);
        actionEmail.setOnClickListener(this);
        actionPM.setOnClickListener(this);

        reloadButton.setOnClickListener(this);

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

        // TODO Replace this with proper solution
//        Intent browserIntent = new Intent(getActivity(), KoSObjectWebActivity.class);
//        browserIntent.putExtra("url", objectLink);
//        Log.d("happyride", objectLink);
//        startActivity(browserIntent);
//        mActivity.finish();


//        getActivity().finish();

		mProgressView.setVisibility(View.VISIBLE);
        mRequest = new KoSObjectRequest(mActivity.getObjectId(), objectLink, new Response.Listener<KoSObjectItem>() {
            @Override
            public void onResponse(KoSObjectItem item) {
                mKoSObjectItem = item;
                if (mKoSObjectItem != null) {
                    fillList();
                    if (mIsSaved) {
                        updateInDatabase();
                    }
                } else {
                    // No data traffic etc.
                    mNoNetworkView.setVisibility(View.VISIBLE);
                }
                mProgressView.setVisibility(View.INVISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                mScrollView.setVisibility(View.INVISIBLE);
                mProgressView.setVisibility(View.INVISIBLE);

                if (error != null && error.networkResponse != null && error.networkResponse.statusCode == MyRequestQueue.SC_NOT_FOUND) {
                    // Page not found == item is sold
                    mActivity.findViewById(R.id.no_content).setVisibility(View.VISIBLE);

                    mIsSold = true;
                    if (mIsSaved) {
                        datasource.setItemSold(mActivity.getObjectId(), true);
                        mActivity.setResult(SavedListFragment.RESULT_MODIFIED, null);
                    }
                    mActivity.invalidateOptionsMenu();
                } else {
                    mNoNetworkView.setVisibility(View.VISIBLE);
                }
            }
        });

        mRequest.setTag(KoSObjectActivity.TAG);
        MyRequestQueue.getInstance(getContext()).addRequest(mRequest);
	}

	void fillList() {
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
            mPhone.setVisibility(View.VISIBLE);
			mActivity.findViewById(R.id.kos_action_phone_layout).setVisibility(View.VISIBLE);
			mActivity.findViewById(R.id.kos_action_sms_layout).setVisibility(View.VISIBLE);
		}

		if (!TextUtils.isEmpty(person.getEmailLink())) {
			mActivity.findViewById(R.id.kos_action_email_layout).setVisibility(View.VISIBLE);
		}

		if (!TextUtils.isEmpty(mKoSObjectItem.getImgLink())) {
            ViewPager viewPager = mActivity.findViewById(R.id.view_pager);
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(mActivity, mKoSObjectItem.getTitle(), mKoSObjectItem.getImgLinkList());
            viewPager.setAdapter(viewPagerAdapter);

            if (mKoSObjectItem.getImgLinkList().size() > 1) {
                CirclePageIndicator pageIndicator = mActivity.findViewById(R.id.view_pager_indicator);
                pageIndicator.setViewPager(viewPager);
            }
		}

		if (mKoSObjectItem.getYearModel() > 0) {
			mYear.setText("Årsmodell: " + mKoSObjectItem.getYearModel());
			mActivity.findViewById(R.id.kos_object_year_model).setVisibility(View.VISIBLE);
		}

		mText.setText(Html.fromHtml(mKoSObjectItem.getText()));
        String price = mKoSObjectItem.getPrice();
        if (TextUtils.isEmpty(price) || price.equals("0 Kr")) {
            mPrice.setText(KoSListFragment.NO_PRICE);
        } else {
            mPrice.setText("Pris: " + price);
        }
        mScrollView.setVisibility(View.VISIBLE);
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
//                mActivity.invalidateOptionsMenu();
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
        if (datasource.insertKosItem(mKoSObjectItem) > 0) {
            mIsSaved = true;
        } else {
            Toast.makeText(mActivity, R.string.unable_to_save, Toast.LENGTH_SHORT).show();
        }
    }

    void updateInDatabase() {
        if (datasource.updateKosItem(mKoSObjectItem) > 0) {
            mActivity.setResult(SavedListFragment.RESULT_MODIFIED, null);
        } else {
            Toast.makeText(mActivity, R.string.unable_to_update, Toast.LENGTH_SHORT).show();
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

    private void shareObject() {
        if (mKoSObjectItem != null) {
            String message = "Hej! Jag vill tipsa om en annons: " + mKoSObjectItem.getTitle() + " " + mActivity.getObjectLink();
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, getString(R.string.share)));
        }
    }

    private void  openInBrowser(String url, boolean popBackStack) {
        Intent browserIntent = new Intent(getActivity(), WebViewActivity.class);
        browserIntent.putExtra("url", url);
        Log.d("happyride", url);
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
            } else if (v.getId() == R.id.kos_all_ads) {
                openInBrowser("https://happyride.se/" + mKoSObjectItem.getPerson().getId() + "/#admarket", false);
            } else if (v.getId() == R.id.kos_action_phone) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("tel", mKoSObjectItem.getPerson().getPhone(), null));
                startActivity(intent);
            } else if (v.getId() == R.id.kos_action_sms) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", mKoSObjectItem.getPerson().getPhone(), null));
                startActivity(intent);
            } else if (v.getId() == R.id.kos_action_email) {
                openInBrowser(mKoSObjectItem.getPerson().getEmailLink(), false);
            } else if (v.getId() == R.id.kos_action_pm) {
                openInBrowser("https://happyride.se/" + mKoSObjectItem.getPerson().getId() + "/#about", false);
            }
        }
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