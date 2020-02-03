package org.happymtb.unofficial.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.happymtb.unofficial.KoSObjectActivity;
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
    public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
        arg0.removeView((View) arg2);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, final int pos) {
        LayoutInflater inflater = (LayoutInflater) collection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.kos_image_pager_layout,null);
        collection.addView(view);

        final ImageView imageView = view.findViewById(R.id.pager_image_view);
        Picasso.with(mContext)
                .load(mUrlList.get(pos))
                .placeholder(R.drawable.no_photo)
                .error(R.drawable.no_photo)
                .resize(HappyUtils.getScreenWidth(mContext), HappyUtils.dpToPixel(250f))
                .centerCrop()
                .noFade()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        ImageView transitionImageView = ((KoSObjectActivity)mContext).findViewById(R.id.image_transition);
                        // TODO Fade out animation
//                        AlphaAnimation anim = new AlphaAnimation(1.0f, 0f);
//                        anim.setDuration(200);
//                        anim.setFillAfter(true);
//                        transitionImageView.startAnimation(anim);
                        transitionImageView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });

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
