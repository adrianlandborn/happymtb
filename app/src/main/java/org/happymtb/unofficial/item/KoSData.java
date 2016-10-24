package org.happymtb.unofficial.item;

import java.io.Serializable;
import java.util.List;

public class KoSData implements Serializable {
	private static final long serialVersionUID = 201110070001L; 

	public static final int ALLA = 0;
	public static final int SALJES = 1;
	public static final int KOPES = 2;

	private int mCurrentPage;
	private int mMaxPages;
	private int mType;
	private int mRegion;
	private String mRegionStr;
	private int mCategory;
	private String mCategoryStr;
	private int mPrice;
	private String mPriceStr;
	private int mYear;
	private String mYearStr;
	private String mSearch;	
	private List<KoSListItem> mKoSListItems;
	private int mListPosition;
	private String mSortAttribute;
	private int mSortAttributePosition;
	private String mSortOrder;
	private int mSortOrderPosition;
	
	
	public KoSData(String sortAttribute, int sortAttributePosition, String sortOrder, int sortOrderPosition,
				   int searchType,
				   int searchRegion, String searchRegionString,
				   int searchCategory, String searchCategoryString,
				   int searchPrice, String searchPriceString,
				   int searchYear, String searchYearString,
				   String searchText) {
		mCurrentPage = 1;
		mMaxPages = 1;
		mType = searchType; // 0;
		mRegion = searchRegion; //0;
		mRegionStr = searchRegionString; //"Hela Sverige";
		mCategory = searchCategory; //0;
		mCategoryStr = searchCategoryString; //"Alla Kategorier";
        mPrice = searchPrice; //0;
		mPriceStr = searchPriceString; //"Alla Prise";
        mYear = searchYear; //0;
		mYearStr = searchYearString; //"Alla Årsmodeller";
		mSearch = searchText;
		mKoSListItems = null;
        mListPosition = 0;
        mSortAttribute = sortAttribute;
        mSortAttributePosition = sortAttributePosition;
        mSortOrder = sortOrder;
		mSortOrderPosition = sortOrderPosition;
	}

	public int getCurrentPage() {
		return mCurrentPage;
	}

	public void setCurrentPage(int mCurrentPage) {
		this.mCurrentPage = mCurrentPage;
	}

	public int getMaxPages() {
		return mMaxPages;
	}

	public void setMaxPages(int mMaxPages) {
		this.mMaxPages = mMaxPages;
	}

	public int getType() {
		return mType;
	}

	public void setTypePosServer(int mType) {
		this.mType = mType;
	}

	public int getRegion() {
		return mRegion;
	}

	public void setRegionPos(int mRegion) {
		this.mRegion = mRegion;
	}

	public String getRegionStr() {
		return mRegionStr;
	}

	public void setRegionName(String mRegionStr) {
		this.mRegionStr = mRegionStr;
	}

    // Category
	public int getCategory() {
		return mCategory;
	}

	public void setCategoryPos(int mCategory) {
		this.mCategory = mCategory;
	}

	public String getCategoryStr() {
		return mCategoryStr;
	}

	public void setCategoryName(String mCategoryStr) {
		this.mCategoryStr = mCategoryStr;
	}

    // Price
    public int getPrice() {
        return mPrice;
    }

    public void setPricePos(int mPrice) {
        this.mPrice = mPrice;
    }

    public String getPriceStr() {
        return mPriceStr;
    }

    public void setPriceName(String mPriceStr) {
        this.mPriceStr = mPriceStr;
    }

    // Year
    public int getYear() {
        return mYear;
    }

    public void setYearPos(int mYear) {
        this.mYear = mYear;
    }

    public String getYearStr() {
        return mYearStr;
    }

    public void setYearName(String mYearStr) {
        this.mYearStr = mYearStr;
    }

	public String getSearchString() {
		return mSearch;
	}

	public void setSearch(String mSearch) {
		this.mSearch = mSearch;
	}

	public List<KoSListItem> getKoSItems() {
		return mKoSListItems;
	}

	public void setKoSItems(List<KoSListItem> mKoSListItems) {
		this.mKoSListItems = mKoSListItems;
	}

	public int getListPosition() {
		return mListPosition;
	}

	public void setListPosition(int mListPosition) {
		this.mListPosition = mListPosition;
	}

	public String getSortAttributeServer() {
		return mSortAttribute;
	}

	public void setSortAttributeServer(String mSortAttribute) {
		this.mSortAttribute = mSortAttribute;
	}

//	public String getSortAttributeStr() {
//		return HappyUtils.getSortAttrNameLocal(getActivity(), sortAttributePos)SortAttributeStr;
//	}

//	public void setSortAttributeStr(String mSortAttributeStr) {
//		this.mSortAttributeStr = mSortAttributeStr;
//	}

	public String getSortOrderServer() {
		return mSortOrder;
	}

	public void setSortOrderServer(String mSortOrder) {
		this.mSortOrder = mSortOrder;
	}

//	public String getSortOrderStr() {
//		return mSortOrderStr;
//	}
//
//	public void setSortOrderStr(String mSortOrderStr) {
//		this.mSortOrderStr = mSortOrderStr;
//	}

	public int getSortOrderPosition() {
		return mSortOrderPosition;
	}

	public void setSortOrderPosition(int mSortOrderPosition) {
		this.mSortOrderPosition = mSortOrderPosition;
	}

	public int getSortAttributePosition() {
		return mSortAttributePosition;
	}

	public void setSortAttributePosition(int mSortAttributePosition) {
		this.mSortAttributePosition = mSortAttributePosition;
	}

}
