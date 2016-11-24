/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.happymtb.unofficial;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.happymtb.unofficial.analytics.GaConstants;
import org.happymtb.unofficial.analytics.HappyApplication;

import uk.co.senab.photoview.PhotoViewAttacher;

public class SimpleImageActivity extends AppCompatActivity {
    private PhotoViewAttacher mAttacher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_image);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getExtras().getString("title"));

        // Obtain the shared Tracker instance.
        HappyApplication application = (HappyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();

        // [START Google analytics screen]
        mTracker.setScreenName(GaConstants.Categories.KOS_ZOOM_IMAGE);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END sGoogle analytics screen]

        findViewById(R.id.progress_container_id).setVisibility(View.VISIBLE);
        final ImageView mImageView = (ImageView) findViewById(R.id.imageView);

        Callback imageLoadedCallback = new Callback() {

            @Override
            public void onSuccess() {
                findViewById(R.id.progress_container_id).setVisibility(View.GONE);
                if(mAttacher != null) {
                    mAttacher.update();
                } else {
                    mAttacher = new PhotoViewAttacher(mImageView);
                }
            }

            @Override
            public void onError() {
                findViewById(R.id.progress_container_id).setVisibility(View.GONE);
                Toast.makeText(SimpleImageActivity.this, R.string.error_loading_image, Toast.LENGTH_SHORT).show();
            }
        };

        Picasso.with(this).load(getIntent().getExtras().getString("url")).into(mImageView, imageLoadedCallback);

        // The MAGIC happens here!
        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setMediumScale(2.5F);
        mAttacher.setMaximumScale(5.0F);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Need to call clean-up
        mAttacher.cleanup();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}