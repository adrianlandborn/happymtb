package pebeijer.happymtb.item;

import java.io.Serializable;
import java.util.List;

public class KoSData implements Serializable {
	private static final long serialVersionUID = 201110070001L; 
	
	private int mCurrentPage;
	private int mMaxPages;
	private int mType;
	private int mRegion;
	private String mRegionStr;
	private int mCategory;
	private String mCategoryStr;
	private String mSearch;	
	private List<KoSItem> mKoSItems;
	private int mListPosition;
	private String mSortAttribute;
	private String mSortAttributeStr;
	private int mSortAttributePosition;
	private String mSortOrder;
	private String mSortOrderStr;
	private int mSortOrderPosition;
	
	
	public KoSData(int CurrentPage,	int MaxPages, int Type, int Region, String RegionStr, int Category,
		String CategoryStr, String Search, List<KoSItem> KoSItems, int ListPosition, String SortAttribute, String SortAttributeStr,
		int SortAttributePosition, String SortOrder, String SortOrderStr, int SortOrderPosition) {
		setCurrentPage(CurrentPage);
		setMaxPages(MaxPages);
		setType(Type);
		setRegion(Region);
		setRegionStr(RegionStr);
		setCategory(Category);
		setCategoryStr(CategoryStr);
		setSearch(Search);
		setKoSItems(KoSItems);
		setListPosition(ListPosition);			
		setSortAttribute(SortAttribute);
		setSortAttributeStr(SortAttributeStr);
		setSortAttributePosition(SortAttributePosition);
		setSortOrder(SortOrder);
		setSortOrderStr(SortOrderStr);
		setSortOrderPosition(SortOrderPosition);
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

	public void setType(int mType) {
		this.mType = mType;
	}

	public int getRegion() {
		return mRegion;
	}

	public void setRegion(int mRegion) {
		this.mRegion = mRegion;
	}

	public String getRegionStr() {
		return mRegionStr;
	}

	public void setRegionStr(String mRegionStr) {
		this.mRegionStr = mRegionStr;
	}

	public int getCategory() {
		return mCategory;
	}

	public void setCategory(int mCategory) {
		this.mCategory = mCategory;
	}

	public String getCategoryStr() {
		return mCategoryStr;
	}

	public void setCategoryStr(String mCategoryStr) {
		this.mCategoryStr = mCategoryStr;
	}

	public String getSearch() {
		return mSearch;
	}

	public void setSearch(String mSearch) {
		this.mSearch = mSearch;
	}

	public List<KoSItem> getKoSItems() {
		return mKoSItems;
	}

	public void setKoSItems(List<KoSItem> mKoSItems) {
		this.mKoSItems = mKoSItems;
	}

	public int getListPosition() {
		return mListPosition;
	}

	public void setListPosition(int mListPosition) {
		this.mListPosition = mListPosition;
	}

	public String getSortAttribute() {
		return mSortAttribute;
	}

	public void setSortAttribute(String mSortAttribute) {
		this.mSortAttribute = mSortAttribute;
	}

	public String getSortAttributeStr() {
		return mSortAttributeStr;
	}

	public void setSortAttributeStr(String mSortAttributeStr) {
		this.mSortAttributeStr = mSortAttributeStr;
	}

	public String getSortOrder() {
		return mSortOrder;
	}

	public void setSortOrder(String mSortOrder) {
		this.mSortOrder = mSortOrder;
	}

	public String getSortOrderStr() {
		return mSortOrderStr;
	}

	public void setSortOrderStr(String mSortOrderStr) {
		this.mSortOrderStr = mSortOrderStr;
	}

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
