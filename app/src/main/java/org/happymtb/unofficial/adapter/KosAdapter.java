package org.happymtb.unofficial.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.happymtb.unofficial.R;
import org.happymtb.unofficial.fragment.KoSListFragment;
import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.KoSListItem;
import org.happymtb.unofficial.view.KoSRowView;

import java.util.ArrayList;
import java.util.List;

public class KosAdapter extends BaseAdapter {
    private Context mContext;
    private List<KoSListItem> mKoSListItems = new ArrayList<>();

    public KosAdapter(Context context, List<KoSListItem> koSListItems) {
        mContext = context;
        mKoSListItems = koSListItems;
    }

    @Override
    public int getCount() {
        if (mKoSListItems != null) {
            return mKoSListItems.size();
        } else {
            return 0;
        }
    }

    public void setItems(List<KoSListItem> items) {
        mKoSListItems = items;
    }

    @Override
    public KoSListItem getItem(int position) {
        return mKoSListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        KoSRowView kosRowView;

        if (convertView == null) {
            kosRowView = new KoSRowView(mContext);
        } else {
            kosRowView = (KoSRowView) convertView;
        }

        kosRowView.setTitle(mKoSListItems.get(position).getTitle());
        kosRowView.setTime(mKoSListItems.get(position).getTime());
        kosRowView.setArea(mKoSListItems.get(position).getArea());
        kosRowView.setCategory(mKoSListItems.get(position).getCategory());
        kosRowView.setPrice(mKoSListItems.get(position).getPrice());

        String imageUrl = mKoSListItems.get(position).getImgLink();
        if (!TextUtils.isEmpty(imageUrl) && imageUrl.equals(KoSListFragment.NO_IMAGE_URL)) {
            kosRowView.getImageView().setImageResource(R.drawable.no_photo);
        } else if (!TextUtils.isEmpty(imageUrl)) {
            if (HappyUtils.isHighDensity(mContext.getResources())) {
                Picasso.with(mContext).load(imageUrl).into(kosRowView.getImageView());
            } else {
                Picasso.with(mContext).load(imageUrl.replace("normal", "small")).into(kosRowView.getImageView());
            }
        } else {
            kosRowView.setObjectImage(null);
        }

        if (mKoSListItems.get(position).getType().equals(KoSListItem.TYPE_SALJES)) {
            kosRowView.setRowBackgroundColor(R.color.kos_green);
        } else {
            kosRowView.setRowBackgroundColor(R.color.kos_red);
        }

        if (position == (mKoSListItems.size() - 1) && mKoSListItems.size() > 3) {
            kosRowView.setBottomPaddingVisible(true);
        } else {
            kosRowView.setBottomPaddingVisible(false);
        }

        return kosRowView;
    }

    static class ViewHolder {
        TextView title;
        ImageView image;
    }

}