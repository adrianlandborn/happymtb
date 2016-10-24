package org.happymtb.unofficial.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import org.happymtb.unofficial.R;
import org.happymtb.unofficial.SimpleImageActivity;
import org.happymtb.unofficial.helpers.HappyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian on 10/3/2016.
 */

public class ViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private String mTitle;
    private List<String> mUrlList = new ArrayList<>();


    public ViewPagerAdapter(Context context, String title, List<String> urlList) {
        mContext = context;
        mTitle = title;
        mUrlList = urlList;
    }

    @Override
    public int getCount() {
        return mUrlList.size();
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public Object instantiateItem(View collection, final int pos) {
        LayoutInflater inflater = (LayoutInflater) collection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.kos_image_pager_layout,null);
        ((ViewPager) collection).addView(view);

        final ImageView imageView = (ImageView) view.findViewById(R.id.pager_image_view);
        Picasso.with(mContext)
                .load(mUrlList.get(pos))
                .placeholder(R.drawable.no_photo)
                .error(R.drawable.no_photo)
                .resize(HappyUtils.getScreenWidth(mContext), HappyUtils.dpToPixel(250f))
                .centerCrop()
                .into(imageView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String largeImageUrl = mUrlList.get(pos).replace("normal", "large");
                Intent zoomImageIntent = new Intent(mContext, SimpleImageActivity.class);
                zoomImageIntent.putExtra("title", mTitle);
                zoomImageIntent.putExtra("url", largeImageUrl);
                mContext.startActivity(zoomImageIntent);
            }
        });
        return view;
    }
}
