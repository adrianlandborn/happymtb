package org.happymtb.unofficial.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.droidparts.widget.ClearableEditText;
import org.happymtb.unofficial.MainActivity;
import org.happymtb.unofficial.R;
import org.happymtb.unofficial.analytics.GaConstants;
import org.happymtb.unofficial.analytics.HappyApplication;

/**
 * Created by Adrian on 01/07/2015.
 */
public class KoSSearchDialogFragment extends DialogFragment {

	private Button clearAllButton;

	public KoSSearchDialogFragment() {
    }

	public interface SearchDialogDataListener {
		void onSearchData(String text, int category, int region, int type);
	}

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		final MainActivity activity = (MainActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.kos_search, null);
        builder.setView(view);

		// Obtain the shared Tracker instance.
		HappyApplication application = (HappyApplication) getActivity().getApplication();
		Tracker mTracker = application.getDefaultTracker();

		// [START Google analytics screen]
		mTracker.setScreenName(GaConstants.Screens.KOS_SEARCH_DIALOG);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
		// [END Google analytics screen]

		SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

		final ClearableEditText searchString = (ClearableEditText) view.findViewById(R.id.kos_dialog_search_text);
		final Spinner searchCategory = (Spinner) view.findViewById(R.id.kos_dialog_search_category);
		final Spinner searchRegion = (Spinner) view.findViewById(R.id.kos_dialog_search_region);
		final Spinner searchType = (Spinner) view.findViewById(R.id.kos_dialog_search_type);

		clearAllButton = (Button) view.findViewById(R.id.kos_dialog_search_clear_all);

		searchString.setText(mPreferences.getString(KoSListFragment.SEARCH_TEXT, ""));
		searchCategory.setSelection(mPreferences.getInt(KoSListFragment.SEARCH_CATEGORY_SPINNER, 0));
		searchRegion.setSelection(mPreferences.getInt(KoSListFragment.SEARCH_REGION_SPINNER, 0));
		searchType.setSelection(mPreferences.getInt(KoSListFragment.SEARCH_TYPE_SPINNER, 0));

				builder.setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						activity.onSearchData(
								searchString.getText().toString().trim(),
								searchCategory.getSelectedItemPosition(),
								searchRegion.getSelectedItemPosition(),
								searchType.getSelectedItemPosition());
					}
				});
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                KoSSearchDialogFragment.this.getDialog().cancel();
            }
        });

		searchString.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void afterTextChanged(Editable s) {
				updateClearAllButton(searchString.getText().toString().trim(),
						searchCategory.getSelectedItemPosition(),
						searchRegion.getSelectedItemPosition(),
						searchType.getSelectedItemPosition());
			}
		});

		AdapterView.OnItemSelectedListener selectedListener = new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				updateClearAllButton(searchString.getText().toString().trim(),
						searchCategory.getSelectedItemPosition(),
						searchRegion.getSelectedItemPosition(),
						searchType.getSelectedItemPosition());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		};

		searchCategory.setOnItemSelectedListener(selectedListener);
		searchRegion.setOnItemSelectedListener(selectedListener);
		searchType.setOnItemSelectedListener(selectedListener);

		clearAllButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchString.setText("");
				searchCategory.setSelection(0);
				searchRegion.setSelection(0);
				searchType.setSelection(0);

				updateClearAllButton("", 0, 0, 0);
			}
		});
		return builder.create();
	}

    private void updateClearAllButton(String text, int cat, int region, int type) {
		if (TextUtils.isEmpty(text) && cat == 0 && region == 0 && type == 0) {
			clearAllButton.setEnabled(false);
		} else {
			clearAllButton.setEnabled(true);
		}
	}
}
