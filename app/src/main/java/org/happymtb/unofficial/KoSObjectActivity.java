package org.happymtb.unofficial;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.happymtb.unofficial.fragment.KoSListFragment;
import org.happymtb.unofficial.fragment.KoSObjectFragment;
import org.happymtb.unofficial.volley.MyRequestQueue;

public class KoSObjectActivity extends AppCompatActivity {

	public final static String TAG = "KoSObjectActivity";

	public final static String TRANSITION = "transition";

	public final static String AREA = "area";
	public final static String TYPE = "type";
	public final static String TITLE = "title";
	public final static String DATE = "date";
	public final static String PRICE = "price";
	public final static String CATEGORY = "category";
	public final static String IMAGE_URL = "image_url";
	public final static String URL = "item_url";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		// Set up the action bar to show a dropdown list.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		setContentView(R.layout.kos_object_frame);

		if (getIntent().getBooleanExtra(TRANSITION, false)) {
			findViewById(R.id.kos_object_scroll).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.kos_object_scroll).setVisibility(View.INVISIBLE);
        }

        initFromBundle();

        Fragment fragment;
		if (savedInstanceState == null) {
			fragment = new KoSObjectFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.kosobjectframe, fragment, "kos_object")
					.commit();
		}
	}

    private void initFromBundle() {
        TextView title = (TextView) findViewById(R.id.kos_object_title);
        TextView category = (TextView) findViewById(R.id.kos_object_category);
        TextView date = (TextView) findViewById(R.id.kos_object_date);
        TextView price = (TextView) findViewById(R.id.kos_object_price);
        ImageView transitionImageView = (ImageView) findViewById(R.id.image_transition);
        View viewPagerFrame = findViewById(R.id.kos_object_viewpager_frame);

        title.setText(getObjectTitle());
        category.setText(getObjectCategory() + ", " + getObjectArea());
        date.setText("Datum: " + getObjectDate() + " sedan");

        if (TextUtils.isEmpty(getObjectPrice())) {
            price.setText(KoSListFragment.NO_PRICE);
        } else {
            price.setText("Pris: " + getObjectPrice());
        }

        String imageUrl = getObjectImageLink();
        if (!(KoSListFragment.NO_IMAGE_URL.equals(imageUrl))) {
            Picasso.with(this).load(imageUrl).into(transitionImageView);
        } else {
            transitionImageView.setVisibility(View.GONE);
            viewPagerFrame.setVisibility(View.GONE);
        }

    }

	public long getObjectId() {
		String[] urlSplit = getObjectLink().split("id=");
		if (urlSplit.length == 2) {
			return Long.parseLong(urlSplit[1]);
		} else {
		return -1;
		}
	}
	
	public String getObjectArea() {
        String area = getIntent().getStringExtra(AREA);
        if (TextUtils.isEmpty(area)) {
            area = "";
        }
		return area;
	}

	public String getObjectDate() {
		return getIntent().getStringExtra(DATE);
	}

	public String getObjectType() {
		return getIntent().getStringExtra(TYPE);
	}

	public String getObjectTitle() {
		return getIntent().getStringExtra(TITLE);
	}

	public String getObjectCategory() {
        return getIntent().getStringExtra(CATEGORY);
	}

	public String getObjectPrice() {
		return getIntent().getStringExtra(PRICE);
	}

	public String getObjectImageLink() {
		return getIntent().getStringExtra(IMAGE_URL);
	}

	public String getObjectLink() {
		String objectLink = "";
		Intent intent = getIntent();
		String action = intent.getAction();
		if (action != null) {
			Uri data = intent.getData();
			objectLink = data.toString();

		} else {
			objectLink = intent.getExtras().getString(URL);
		}

		return objectLink;
	}

	@Override
	protected void onStop () {
		super.onStop();
		MyRequestQueue.getInstance(this).getRequestQueue().cancelAll(TAG);
	}
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case android.R.id.home:

				//TODO Use Finish Transitions or not?
// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//				supportFinishAfterTransition();
//			} else {
//	    		this.finish();
//			}
	    		this.finish();

				return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}