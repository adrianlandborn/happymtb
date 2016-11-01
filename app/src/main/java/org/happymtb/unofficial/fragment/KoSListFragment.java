package org.happymtb.unofficial.fragment;

import java.util.List;

import org.droidparts.widget.ClearableEditText;
import org.happymtb.unofficial.KoSObjectActivity;
import org.happymtb.unofficial.MainActivity;
import org.happymtb.unofficial.R;
import org.happymtb.unofficial.adapter.KosListdapter;
import org.happymtb.unofficial.analytics.GaConstants;
import org.happymtb.unofficial.analytics.HappyApplication;
import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.KoSData;
import org.happymtb.unofficial.item.KoSListItem;
import org.happymtb.unofficial.listener.KoSListListener;
import org.happymtb.unofficial.listener.PageTextWatcher;
import org.happymtb.unofficial.task.KoSListTask;
import org.happymtb.unofficial.ui.BottomBar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class KoSListFragment extends RefreshListfragment implements DialogInterface.OnCancelListener,
		 View.OnClickListener, AbsListView.OnScrollListener {
    public static String TAG = "kos_frag";

	public final static String LAST_UPDATE = "last_update";
	public final static long ONE_HOUR = 1000 * 60 * 60;
//	public final static long ONE_HOUR = 10 * 1000; //(10 sec)

	public final static String SORT_ORDER_POS = "sort_order_pos";
	public final static String SORT_ORDER_SERVER = "sort_order";
	public final static String SORT_ATTRIBUTE_POS = "sort_attribute_pos";
	public final static String SORT_ATTRIBUTE_SERVER = "sort_attribute";

    public final static String SEARCH_TEXT = "search_text";

    public final static String SEARCH_CATEGORY = "search_category";
    public final static String SEARCH_CATEGORY_POS = "search_category_pos";

    public final static String SEARCH_PRICE = "search_price";
    public final static String SEARCH_PRICE_POS = "search_price_pos";

    public final static String SEARCH_YEAR = "search_year";
    public final static String SEARCH_YEAR_POS= "search_year_pos";

    public final static String SEARCH_REGION = "search_region";
    public final static String SEARCH_REGION_POS = "search_region_pos";

    public final static String SEARCH_TYPE_POS = "search_type";

    public final static String SEARCH_TYPE_SPINNER = "search_type_spinner";
    public final static String SEARCH_REGION_SPINNER = "search_region_spinner";
    public final static String SEARCH_CATEGORY_SPINNER = "search_category_spinner";
    public final static String SEARCH_PRICE_SPINNER = "search_price_spinner";
    public final static String SEARCH_YEAR_SPINNER = "search_year_spinner";

	private Tracker mTracker;

	private KoSListTask mKoSTask;
	private KosListdapter mKoSAdapter;
	private KoSData mKoSData;
	private SharedPreferences mPreferences;
	private MainActivity mActivity;
	private FragmentManager fragmentManager;

	private ImageButton prevPageButton;
	private ImageButton nextPageImageButton;

    private ClearableEditText searchEditText;
    private Spinner searchCategory;
    private Spinner searchRegion;
    private Spinner searchType;
    private Spinner searchPrice;
    private Spinner searchYear;

    private int mHeaderCount;

    private Button mClearSearchButton;

    private SlidingMenu mSlidingMenu;

    private boolean mIsScrollingUp;
    private int mLastFirstVisibleItem;

    /** Called when the activity is first created. */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        mActivity = (MainActivity) getActivity();
		mActivity.getSupportActionBar().setTitle(R.string.main_kos);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);

        mActivity.findViewById(R.id.kos_bottombar).setOnClickListener(this);

        getListView().setOnScrollListener(this);

        prevPageButton = (ImageButton) mActivity.findViewById(R.id.prev);
        prevPageButton.setOnClickListener(this);
        nextPageImageButton = (ImageButton) mActivity.findViewById(R.id.next);
        nextPageImageButton.setOnClickListener(this);

        if (savedInstanceState != null) {
			// Restore Current page.
            mKoSData = (KoSData) savedInstanceState.getSerializable(DATA);

            fillList();
            showProgress(false);
		} else {
            mKoSData = new KoSData(
                    mPreferences.getString(SORT_ATTRIBUTE_SERVER, "creationdate"), mPreferences.getInt(SORT_ATTRIBUTE_POS, 0),
                    mPreferences.getString(SORT_ORDER_SERVER, "DESC"), mPreferences.getInt(SORT_ORDER_POS, 0),
                    mPreferences.getInt(SEARCH_TYPE_POS, KoSData.ALLA),
                    mPreferences.getInt(SEARCH_REGION_POS, 0), mPreferences.getString(SEARCH_REGION, getString(R.string.all_regions)),
                    mPreferences.getInt(SEARCH_CATEGORY_POS, 0), mPreferences.getString(SEARCH_CATEGORY, getString(R.string.all_categories)),
                    mPreferences.getInt(SEARCH_PRICE_POS, 0), mPreferences.getString(SEARCH_PRICE, getString(R.string.all_prices)),
                    mPreferences.getInt(SEARCH_YEAR_POS, 0), mPreferences.getString(SEARCH_YEAR, getString(R.string.all_years)),
                    mPreferences.getString(SEARCH_TEXT, ""));
            fetchData();
        }

        setupSearchSlidingMenu();

	}

    private void setupSearchSlidingMenu() {
        mSlidingMenu = mActivity.getKosSlidingMenu();

        if (mSlidingMenu == null) {
            mSlidingMenu = new SlidingMenu(mActivity);
            mSlidingMenu.setMode(SlidingMenu.RIGHT);
            mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
            mSlidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
            mSlidingMenu.setShadowDrawable(R.drawable.slidemenu_shadow);
//          mSlidingMenu.setBehindScrollScale(R.dimen.slidingmenu_behind_scroll_scale);
            mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
            mSlidingMenu.setFadeDegree(0.85f);
            mSlidingMenu.setBehindWidthRes(R.dimen.slidingmenu_width);
            mSlidingMenu.setMenu(R.layout.kos_search_slide);mSlidingMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
                @Override
                public void onOpened() {
                    // [START Google analytics screen]
                    mTracker.setScreenName(GaConstants.Categories.KOS_SEARCH_MENU);
                    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                    // [END Google analytics screen]
                }
            });

            mSlidingMenu.attachToActivity(mActivity, SlidingMenu.SLIDING_CONTENT);

            mActivity.setKosSlidingMenu(mSlidingMenu);
        } else {
            // TODO remove old listeners before adding new ones?
        }

        searchEditText = (ClearableEditText) mActivity.findViewById(R.id.kos_search_dialog_search_text);
        searchCategory = (Spinner) mActivity.findViewById(R.id.kos_search_dialog_category);
        searchRegion = (Spinner) mActivity.findViewById(R.id.kos_search_dialog_region);
        searchType = (Spinner) mActivity.findViewById(R.id.kos_search_dialog_type);
        searchPrice = (Spinner) mActivity.findViewById(R.id.kos_search_dialog_price);
        searchYear = (Spinner) mActivity.findViewById(R.id.kos_search_dialog_year);

        mClearSearchButton = (Button) mActivity.findViewById(R.id.kos_dialog_search_clear_all);

        searchEditText.setText(mPreferences.getString(KoSListFragment.SEARCH_TEXT, ""));
        searchCategory.setSelection(mPreferences.getInt(KoSListFragment.SEARCH_CATEGORY_SPINNER, 0), false);
        searchRegion.setSelection(mPreferences.getInt(KoSListFragment.SEARCH_REGION_SPINNER, 0), false);
        searchType.setSelection(mPreferences.getInt(KoSListFragment.SEARCH_TYPE_SPINNER, 0), false);
        searchPrice.setSelection(mPreferences.getInt(KoSListFragment.SEARCH_PRICE_SPINNER, 0), false);
        searchYear.setSelection(mPreferences.getInt(KoSListFragment.SEARCH_YEAR_SPINNER, 0), false);

        updateClearAllButton();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateClearAllButton();
                updateSearchParams();
            }
        });

        AdapterView.OnItemSelectedListener selectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateClearAllButton();
                updateSearchParams();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        searchCategory.setOnItemSelectedListener(selectedListener);
        searchRegion.setOnItemSelectedListener(selectedListener);
        searchType.setOnItemSelectedListener(selectedListener);
        searchPrice.setOnItemSelectedListener(selectedListener);
        searchYear.setOnItemSelectedListener(selectedListener);

        mClearSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
                searchCategory.setSelection(0, false);
                searchRegion.setSelection(0, false);
                searchType.setSelection(0, false);
                searchPrice.setSelection(0, false);
                searchYear.setSelection(0, false);

                updateClearAllButton();
            }
        });
    }

    private void updateClearAllButton() {
        if (TextUtils.isEmpty(searchEditText.getText().toString().trim())
                && searchCategory.getSelectedItemPosition() == 0
                && searchRegion.getSelectedItemPosition() == 0
                && searchType.getSelectedItemPosition() == 0
                && searchPrice.getSelectedItemPosition() == 0
                && searchYear.getSelectedItemPosition() == 0) {
            mClearSearchButton.setEnabled(false);
            mClearSearchButton.setTextColor(getResources().getColor(R.color.grey_light));
        } else {
            mClearSearchButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            mClearSearchButton.setEnabled(true);
        }
    }

	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Obtain the shared Tracker instance.
		HappyApplication application = (HappyApplication) getActivity().getApplication();
		mTracker = application.getDefaultTracker();

		// [START Google analytics screen]
		mTracker.setScreenName(GaConstants.Categories.KOS_LIST);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
		// [END Google analytics screen]
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.kos_frame, container, false);
    }

	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mKoSData != null) {
            outState.putSerializable(DATA, mKoSData);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mKoSTask == null || (mKoSTask != null && !mKoSTask.getStatus().equals(AsyncTask.Status.RUNNING))) {
            if (System.currentTimeMillis() > (mPreferences.getLong(LAST_UPDATE, 0) + ONE_HOUR)){
                if (mKoSData.getCurrentPage() == 1) {
                    Toast.makeText(mActivity, "Uppdaterar sökning...", Toast.LENGTH_LONG).show();
                    fetchData();
                }
            }
        }
    }

    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.kos_menu, menu);

		super.onCreateOptionsMenu(menu, inflater);
	}		

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.kos_submenu:
			return true;		
		case R.id.kos_left:
			previousPage();
            sendGaEvent(GaConstants.Actions.PREV_PAGE, GaConstants.Labels.EMPTY);
			return true;
		case R.id.kos_right:
			nextPage();
            sendGaEvent(GaConstants.Actions.NEXT_PAGE, GaConstants.Labels.EMPTY);
			return true;
		case R.id.kos_search_option:
            mSlidingMenu.toggle();
			return true;
		case R.id.kos_go_to_page:
            openGoToPage();
			return true;	
		case R.id.kos_new_item:
			String url = "http://happyride.se/annonser/add.php";
            sendGaEvent(GaConstants.Actions.NEW_ADD, GaConstants.Labels.EMPTY);
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(browserIntent);							
			return true;			
		}
		return super.onOptionsItemSelected(item);
	}

    private void openGoToPage() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
        alert.setTitle(R.string.goto_page);
        alert.setMessage(getString(R.string.enter_page_number, mKoSData.getMaxPages()));

        // Set an EditText view to get user input
        final EditText input = new EditText(mActivity);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton(R.string.open, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do something with value!
                if (HappyUtils.isInteger(input.getText().toString())) {
                    // [START Google analytics screen]
                    mTracker.setScreenName(GaConstants.Categories.KOS_GOTO_DIALOG);
                    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                    // [END Google analytics screen]
                    mKoSData.setCurrentPage(Integer.parseInt(input.getText().toString()));
                    fetchData();
                }
            }
        });

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        final AlertDialog dialog = alert.create();

        input.addTextChangedListener(new PageTextWatcher(dialog, mKoSData.getMaxPages()));

        dialog.show();
    }

    public void reloadCleanList() {
        mKoSData.setCurrentPage(1);
        mKoSAdapter = null;
		fetchData();
	}

    @Override
    protected void fetchData() {
		if (hasNetworkConnection()) {
            mKoSTask = new KoSListTask();
            mKoSTask.addKoSListListener(new KoSListListener() {
                public void success(List<KoSListItem> koSListItems) {
                    Activity activity = getActivity();
                    if (activity != null && !activity.isFinishing()) {
                        mKoSData.setKoSItems(koSListItems);
                        fillList();

                        showList(true);
                        showProgress(false);

                        mPreferences.edit().putLong(LAST_UPDATE, System.currentTimeMillis()).apply();
						if (getView() != null && getListView() != null) {
                            getListView().setSelection(0);
                        }
                    }
                }

                public void fail() {
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        mKoSData.setKoSItems(null);
                        updateBottomBar();
                        updateHeader();

                        // TODO Clear list

                        Toast.makeText(mActivity, mActivity.getString(R.string.kos_no_items_found), Toast.LENGTH_LONG).show();

                        showProgress(false);
                    }
                }
            });
            String year =  mKoSData.getYear() == 0? "0" :  mKoSData.getYearStr();
            mKoSTask.execute(mKoSData.getSearchString(), mKoSData.getCategory(), mKoSData.getRegion(), mKoSData.getType(),
                    "" /*category2*/, ""/*county2*/, ""/*type2*/, mKoSData.getPrice(), year, mKoSData.getCurrentPage(),
                    mKoSData.getSortAttributeServer(), mKoSData.getSortOrderServer());

            //?search=&category=1&county=&type=1&category2=&county2=&type2=&price=3&year=2013&p=1&sortattribute=creationdate&sortorder=DESC
        }
	}

	private void fillList() {
        if (mKoSData.getKoSItems() == null || mKoSData.getKoSItems().isEmpty()) {
            // Workaround for orientation changes before finish loading
            showProgress(true);
            fetchData();

            return;
        } else if (mKoSAdapter == null) {
            mKoSAdapter = new KosListdapter(mActivity, mKoSData.getKoSItems());
			setListAdapter(mKoSAdapter);
		} else {
            mKoSAdapter.setItems(mKoSData.getKoSItems());
			mKoSAdapter.notifyDataSetChanged();
		}

        updateBottomBar();
        updateHeader();
	}

    private void updateBottomBar() {
        // Bottombar
        ViewGroup bottombar = (ViewGroup) mActivity.findViewById(R.id.kos_bottombar);
        if (bottombar != null) {
            if (mKoSData.getKoSItems() == null || mKoSData.getKoSItems().size() == 0) {
                bottombar.setVisibility(View.GONE);
            } else {
                TextView currentPage = (TextView) mActivity.findViewById(R.id.current_page);
                currentPage.setText(Integer.toString(mKoSData.getCurrentPage()));

                int pages = mKoSData.getKoSItems().get(0).getNumberOfKoSPages();
                TextView maxPages = (TextView) mActivity.findViewById(R.id.no_of_pages);
                maxPages.setText(Integer.toString(pages));
                mKoSData.setMaxPages(pages);

                prevPageButton.setVisibility(mKoSData.getCurrentPage() > 1 ? View.VISIBLE : View.INVISIBLE);
                nextPageImageButton.setVisibility(mKoSData.getCurrentPage() < mKoSData.getMaxPages() ? View.VISIBLE : View.INVISIBLE);

                bottombar.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateHeader() {
        // Header
        View header = mActivity.findViewById(R.id.kos_list_header);
        if (mKoSData.getSearchString().length() == 0 && mKoSData.getCategory() == 0 && mKoSData.getRegion() == 0&& mKoSData.getType() == 0 && mKoSData.getPrice() == 0 && mKoSData.getYear() == 0) {
            // Remove header
            if (header != null) {
                getListView().removeHeaderView(header);
                mHeaderCount = 0;
            }

        } else {
            // Add or Set header
            if (header == null) {
                header = (ViewGroup)getActivity().getLayoutInflater().inflate(R.layout.kos_list_header, getListView(), false);
                header.setOnClickListener(this);
                ViewCompat.setElevation(header, HappyUtils.dpToPixel(4f));
                getListView().addHeaderView(header, null, false);

                mHeaderCount = 1;
            }

            String searchString = mKoSData.getSearchString();

            // Search text
            TextView search = (TextView) mActivity.findViewById(R.id.kos_header_search_text);
            if (searchString.length() > 0) {
                search.setVisibility(View.VISIBLE);
                search.setText("Sökord: " + searchString);
            } else {
                search.setVisibility(View.GONE);
                search.setText("");
            }

            // Category
            TextView category = (TextView) mActivity.findViewById(R.id.kos_header_category);
            if (mKoSData.getCategory() != 0) {
                category.setText("Kategori: " + mKoSData.getCategoryStr());
                category.setVisibility(View.VISIBLE);
            } else {
                category.setVisibility(View.GONE);
            }

            // Region
            TextView region = (TextView) mActivity.findViewById(R.id.kos_header_region);
            if (mKoSData.getRegion() != 0) {
                region.setText("Region: " + mKoSData.getRegionStr());
                region.setVisibility(View.VISIBLE);
            } else {
                region.setVisibility(View.GONE);
            }

            // Type
            TextView type = (TextView) mActivity.findViewById(R.id.kos_header_type);
            if (mKoSData.getType() != 0) {
                type.setText("Annonstyp: " +(mKoSData.getType() == KoSData.SALJES ? "Säljes" : "Köpes"));
                type.setVisibility(View.VISIBLE);
            } else {
                type.setVisibility(View.GONE);
            }

            // Price
            TextView price = (TextView) mActivity.findViewById(R.id.kos_header_price);
            if (mKoSData.getPrice() != 0) {
                price.setText("Pris: " + mKoSData.getPriceStr());
                price.setVisibility(View.VISIBLE);
            } else {
                price.setVisibility(View.GONE);
            }

            // Year
            TextView year = (TextView) mActivity.findViewById(R.id.kos_header_year);
            if (mKoSData.getYear() != 0) {
                year.setText("Årsmodell: " + mKoSData.getYearStr());
                year.setVisibility(View.VISIBLE);
            } else {
                year.setVisibility(View.GONE);
            }
        }
    }

	public void nextPage() {
		if (mKoSData.getCurrentPage() < mKoSData.getMaxPages())
		{			
			mKoSAdapter = null;
			mKoSData.setCurrentPage(mKoSData.getCurrentPage() + 1);
			fetchData();
		}
	}

	public void previousPage() {
    	if (mKoSData.getCurrentPage() > 1)
    	{
    		mKoSAdapter = null;
			mKoSData.setCurrentPage(mKoSData.getCurrentPage() - 1);
    		fetchData();
    	}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent koSObject = new Intent(getActivity(), KoSObjectActivity.class);
        if (mKoSData.getKoSItems() != null) {
            KoSListItem item = mKoSData.getKoSItems().get(position - mHeaderCount);
            koSObject.putExtra(KoSObjectActivity.URL, item.getLink());
            koSObject.putExtra(KoSObjectActivity.AREA, item.getArea());
            koSObject.putExtra(KoSObjectActivity.TYPE, item.getType());
            koSObject.putExtra(KoSObjectActivity.TITLE, item.getTitle());
            koSObject.putExtra(KoSObjectActivity.DATE, item.getTime());
            koSObject.putExtra(KoSObjectActivity.PRICE, item.getPrice());
            koSObject.putExtra(KoSObjectActivity.CATEGORY, item.getCategory());
            startActivity(koSObject);
        }
	}



	@Override
	public void onCancel(DialogInterface dialog) {
		if (mKoSTask != null) {
			mKoSTask.cancel(true);
        }
    }

    public void updateSearchParams() {
        String text = searchEditText.getText().toString().trim();
        int categoryPos = searchCategory.getSelectedItemPosition();
        int regionPos = searchRegion.getSelectedItemPosition();
        int typePos = searchType.getSelectedItemPosition();
        int pricePos = searchPrice.getSelectedItemPosition();
        int yearPos = searchYear.getSelectedItemPosition();

        // Sökord
         mKoSData.setSearch(text);

        // Cyklar, Ramar, Komponenter...
        String categoryArrayPosition[] = getResources().getStringArray(R.array.kos_dialog_search_category_position);
        String categoryArrayName[] = getResources().getStringArray(R.array.kos_dialog_search_category_name);
        mKoSData.setCategoryPos(Integer.parseInt(categoryArrayPosition[categoryPos]));
        mKoSData.setCategoryName(categoryArrayName[categoryPos]);

        // Skåne, Blekinge, Halland...
        String regionArrayPosition[] = getResources().getStringArray(R.array.dialog_search_region_position);
        String regionArrayName[] = getResources().getStringArray(R.array.dialog_search_region_name);
        mKoSData.setRegionPos(Integer.parseInt(regionArrayPosition[regionPos]));
        mKoSData.setRegionName(regionArrayName[regionPos]);

        // Alla, Säljes, Köpes
        String typeArrayPosition[] = getResources().getStringArray(R.array.kos_dialog_search_type_position);
        mKoSData.setTypePosServer(Integer.parseInt(typeArrayPosition[typePos]));

        // Priser...
        String priceArrayPosition[] = getResources().getStringArray(R.array.kos_dialog_search_price_position);
        String priceArrayName[] = getResources().getStringArray(R.array.kos_dialog_search_price);
        mKoSData.setPricePos(Integer.parseInt(priceArrayPosition[pricePos]));
        mKoSData.setPriceName(priceArrayName[pricePos]);

        // Årsmodell
        String yearArrayPosition[] = getResources().getStringArray(R.array.kos_dialog_search_year_position);
        String yearArrayName[] = getResources().getStringArray(R.array.kos_dialog_search_year);
        mKoSData.setYearPos(Integer.parseInt(yearArrayPosition[yearPos]));
        mKoSData.setYearName(yearArrayName[yearPos]);

        Editor edit = mPreferences.edit();
        edit.putString(SEARCH_TEXT, text);

        edit.putInt(SEARCH_CATEGORY_POS, Integer.parseInt(categoryArrayPosition[categoryPos]));
        edit.putString(SEARCH_CATEGORY, categoryArrayName[categoryPos]);

        edit.putInt(SEARCH_REGION_POS, Integer.parseInt(regionArrayPosition[regionPos]));
        edit.putString(SEARCH_REGION, regionArrayName[regionPos]);

        edit.putInt(SEARCH_TYPE_POS, Integer.parseInt(typeArrayPosition[typePos]));

        edit.putInt(SEARCH_PRICE_POS, Integer.parseInt(priceArrayPosition[pricePos]));
        edit.putString(SEARCH_PRICE, priceArrayName[pricePos]);

        edit.putInt(SEARCH_YEAR_POS, Integer.parseInt(yearArrayPosition[yearPos]));
        edit.putString(SEARCH_YEAR, yearArrayName[yearPos]);

        // Dialog Spinner selection
        edit.putInt(SEARCH_TYPE_SPINNER, typePos);
        edit.putInt(SEARCH_REGION_SPINNER, regionPos);
        edit.putInt(SEARCH_CATEGORY_SPINNER, categoryPos);
        edit.putInt(SEARCH_PRICE_SPINNER, pricePos);
        edit.putInt(SEARCH_YEAR_SPINNER, yearPos);

        edit.apply();

        mKoSData.setCurrentPage(1);
        mKoSAdapter = null;
        fetchData();
    }

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.prev) {
			previousPage();
			sendGaEvent(GaConstants.Actions.PREV_PAGE, GaConstants.Labels.EMPTY);
		} else if (v.getId() == R.id.next) {
			nextPage();
			sendGaEvent(GaConstants.Actions.NEXT_PAGE, GaConstants.Labels.EMPTY);
        } else if (v.getId() == R.id.kos_list_header) {
            mSlidingMenu.toggle();
        } else if (v.getId() == R.id.kos_bottombar) {
            openGoToPage();
		}
	}

	private void sendGaEvent(String action, String label) {
		mActivity.getTracker().send(new HitBuilders.EventBuilder()
				.setCategory(GaConstants.Categories.KOS_LIST)
				.setAction(action)
				.setLabel(label)
				.build());
	}

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        final ListView listView = getListView();

        if (view.getId() == listView.getId()) {
            final int currentFirstVisibleItem = listView.getFirstVisiblePosition();
            BottomBar bottomBar = (BottomBar) mActivity.findViewById(R.id.kos_bottombar);
            if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                mIsScrollingUp = false;
                bottomBar.slideDown();
            } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                mIsScrollingUp = true;
                bottomBar.slideUp();
            }
            mLastFirstVisibleItem = currentFirstVisibleItem;

            final int lastItem = firstVisibleItem + visibleItemCount;
            if(lastItem == totalItemCount) {
                mIsScrollingUp = true;
                bottomBar.slideUp();
            }
        }
    }
}