package org.happymtb.unofficial.ui;

import android.content.Context;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import org.happymtb.unofficial.R;
import org.happymtb.unofficial.helpers.HappyUtils;

public class BottomBar extends LinearLayout {


    private boolean mVisible = true;

    public BottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.bottombar, this);

        ViewCompat.setElevation(this, HappyUtils.dpToPixel(4f));
    }

    /**
     * Slide up the view
     */
    public void slideUp() {
        if (!mVisible) {
            Animation slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
            startAnimation(slideUp);
            setVisibility(VISIBLE);

            mVisible = true;
        }
    }

    /**
     * Slide down the view
     */
    public void slideDown() {
        if (mVisible) {
            Animation slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
            startAnimation(slideDown);
            setVisibility(GONE);

            mVisible = false;
        }
    }
}
